import io
import numpy as np
import PIL
import requests
import openpifpaf
import torch
import cv2
import json
import time
from PIL import Image
from pymongo import MongoClient
from datetime import datetime

#db에 웹캠에 비친 사람의 관절 좌표를 등록
def input_db(db, data, human_index):
    input_data = dict()
    
    input_data['date'] = datetime.now()
    input_data['human_index'] = human_index
    for i in range(10):
        
        for j in range(2):
            index = "v" + str(i)
            if j%2 == 0:
                index += 'x'
            else:
                index += 'y'
            
            input_data[index] = data[i*2 + j]

    db.insert_one(input_data)
    
#xy = 관절 좌표
def calcul_xy(xy):
    result = []
    
    neck = [((xy[5][0]+xy[6][0])/2 + xy[0][0]) / 2, ((xy[5][1]+xy[6][1])/2 + xy[0][1]) / 2]
    hip = [(xy[11][0]+xy[12][0])/2, (xy[11][1]+xy[12][1])/2]
    
    result.append((xy[0][0] - neck[0]).item())
    result.append((xy[0][1] - neck[1]).item())
    
    result.append((neck[0] - xy[7][0]).item())
    result.append((neck[1] - xy[7][1]).item())
    
    result.append((neck[0] - xy[8][0]).item())
    result.append((neck[1] - xy[8][1]).item())
    
    result.append((xy[7][0] - xy[9][0]).item())
    result.append((xy[7][1] - xy[9][1]).item())
    
    result.append((xy[8][0] - xy[10][0]).item())
    result.append((xy[8][1] - xy[10][1]).item())
    
    # v5 neck - hip
    result.append((neck[0] - hip[0]).item())
    result.append((neck[1] - hip[1]).item())
    
    result.append((hip[0] - xy[13][0]).item())
    result.append((hip[1] - xy[13][1]).item())
    
    result.append((hip[0] - xy[14][0]).item())
    result.append((hip[1] - xy[14][1]).item())
    
    result.append((xy[13][0] - xy[15][0]).item())
    result.append((xy[13][1] - xy[15][1]).item())
    
    result.append((xy[14][0] - xy[16][0]).item())
    result.append((xy[14][1] - xy[16][1]).item())
    
    return result

def main():
    predictor = openpifpaf.Predictor(checkpoint='shufflenetv2k16')
    #predictions, gt_anns, image_meta = predictor.pil_image(pil_im)
    url = "http://222.120.21.20:5000/stream?src=0" #웹캠 서버 주소
    cap = cv2.VideoCapture(url)
    prev = 0
    FPS = 10
    client = MongoClient(host='114.70.235.37', port=27017)
    db = client['mydb']
    db = db.xy_wj
    if cap.isOpened(): 
        ret, img = cap.read()
        cur = time.time() - prev
        before_chk = False
    
        chk_xy = [0] * 10
        xy = [0] * 10
        points = [0] * 10
        before_xy = None
        human_index = 0
        cnt = 0
        while ret:
            img = Image.fromarray(img)

            predictions, gt_anns, image_meta = predictor.pil_image(img)

            if len(predictions) < 1: #사람없음
                ret, img = cap.read()
                cur = time.time() - prev
                
                continue
        
            #xy 좌표추가 
            human_cnt = len(predictions) # 현재 사람 명수
            # 값 0인지 체크
            if before_chk:
                for idx in range(human_cnt):
                    for i in range(len(xy[idx])):
                        ## 좌표 중 0으로 출력되는 포인트를 인식하지 못한 부분은 이전 값으로 채우기
                        if xy[idx][i][0] == 0:
                            xy[idx][i][0], xy[idx][i][1] = before_xy[idx][i][0], before_xy[idx][i][1]
                        
            for i in range(human_cnt):
                tmp_data = predictions[i].data
                # 중심점        
                point = int(((tmp_data[5][0]+tmp_data[6][0])/2 + tmp_data[0][0]) / 2) # hip 좌표
    
                if points[i] == 0: # 중심점 없을시
                    points[human_index] = point
                    xy[human_index] = predictions[i].data
                    #db 값넣기
                    result = calcul_xy(xy[human_index])
                    input_db(db, result, human_index)
                    human_index+=1   
                else:
                    #저장된 중심점중 제일 가까운 값과 인덱스
                    min_val = min(points, key=lambda x:abs(x-point))
                    min_index = points.index(min_val)
                    xy[min_index] = predictions[i].data
                    #db 값넣기
                    result = calcul_xy(xy[min_index])
                    input_db(db, result, min_index)                     
              
            before_xy = xy
            before_chk = True
        

            ret, img = cap.read()
            cur = time.time() - prev
            cnt+=1

    else:
        print("url is not opened!!")

if __name__=='__main__':
    main()