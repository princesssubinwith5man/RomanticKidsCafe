from pymongo import MongoClient
import pymongo
import datetime
import numpy as np
import time
import torch
import torch.nn as nn
import gc
import os
from torch.utils.data import TensorDataset, DataLoader
from tqdm import tqdm
from dotenv import load_dotenv
from firebase_module import FirebaseMsg

def make_input(client, db):
    batch_size = 32
    tmp_data= db.find().sort([("date", pymongo.DESCENDING)]).limit(2)
    data = list(tmp_data)[0]
    start_date = data["date"] - datetime.timedelta(seconds=5) # 최근 5초간 데이터 가져오기

    max_people = db.find({"date": {'$gte' : start_date, '$lt' : data["date"]}}).sort("human_index",pymongo.DESCENDING).limit(2)
    k = list(max_people)[0]
    k = k["human_index"]
    tmp_loader = []
    for i in range(k+1):
        result= db.find({"human_index":i}).sort([("date", pymongo.DESCENDING)]).limit(2)

        data = list(result)[0]
        start_date = data["date"] - datetime.timedelta(seconds=5)
        seq = list(db.find({"date": {'$gte' : start_date, '$lt' : data["date"]}}))

        list_data = list() # 관절 좌표 리스트

        for j in seq:
            # 필요하지 않은 데이터 삭제
            del j['date']
            del j['_id']
            del j['human_index']

            list_data.append(list(j.values()))

        if(len(list_data) < 30):
            return

        index = list(range(5, len(list_data)-1, int(len(list_data)/30)))
        list_data = np.array(list_data, dtype=np.float32)

        y_false = np.array([0])
        list_data = list_data[index]
        list_data = np.array([list_data])

        tmp_data = TensorDataset(torch.from_numpy(list_data), torch.from_numpy(y_false))
        tmp_loader.append(DataLoader(tmp_data, shuffle=False, batch_size=batch_size))

    return tmp_loader

# dataloaders
class SentimentLSTM(nn.Module):

    def __init__(self, seq_size, output_size, hidden_dim, n_layers):

        super(SentimentLSTM, self).__init__()

        self.output_size = output_size
        self.n_layers = n_layers
        self.hidden_dim = hidden_dim

        # embedding and LSTM layers
        self.lstm = nn.LSTM(seq_size, hidden_dim, n_layers, batch_first=True, dropout=0.2)

        # linear and sigmoid layer
        self.fc = nn.Linear(hidden_dim, output_size)
        self.sig = nn.Sigmoid()

    def forward(self, x):
        batch_size = x.size(0)
        lstm_out, hidden = self.lstm(x)
        out = self.fc(lstm_out[:, -1, :])

        # sigmoid function
        sig_out = self.sig(out)

        return sig_out, out

def init_weights(m):
    for name, param in m.named_parameters():
        nn.init.uniform_(param.data, -0.08, 0.08)

def predict(model, test_loader, sequence_length=30):

    model.eval()
    batch_size = 32
    acc_list = list()

    for inputs, labels in test_loader:
        inputs, labels = inputs.cuda(), labels.cuda()

        output, h = model(inputs)

        pred = torch.round(output.squeeze())

        accuracy = (pred==labels).sum().item() / 32
        acc_list.append(accuracy)

def model_run(model, data_loader_list, sequence_length=30):

    model.eval()
    batch_size = 32
    acc_list = list()

    for data_loader in data_loader_list:
        for inputs, labels in data_loader:
            inputs, labels = inputs.cuda(), labels.cuda()
            output, h = model(inputs)
            pred = torch.round(output.squeeze())

            if int(pred) != 0:
                return True
            else:
                return False

def run(db, client, model, fb_msg, url):
    print("detection running")
    while True:        
        data_loader_list = make_input(client, db)
        if data_loader_list is None:
            continue

        if model_run(model, data_loader_list):
            print("넘어짐 발생")
            fb_msg.falldown(url)
            time.sleep(3)

def main():
    # 쿠다 캐시 비우기
    gc.collect()
    torch.cuda.empty_cache()

    # 변수 불러오기
    load_dotenv()
    global url, db_ip, db_port, db_name, cl_name
    url = os.getenv('CamAddr')
    db_ip = os.getenv('dbip')
    db_port = int(os.getenv('dbport'))
    db_name = os.getenv('dbname')
    cl_name = os.getenv('clname')
    
    # 초기화 
    client = MongoClient(host=db_ip, port=db_port) # mongodb
    db = client[db_name][cl_name]
    hidden_dim = 512
    n_layers = 4
    lr = 0.01
    output_size = 1

    model = SentimentLSTM(20, output_size, hidden_dim, n_layers)
    model.to('cuda')
    model.load_state_dict(torch.load("model_weight.pth"))
    
    # 실행
    fb_msg = FirebaseMsg()
    run(db, client, model, fb_msg, url)
    
if __name__=='__main__':
    main()