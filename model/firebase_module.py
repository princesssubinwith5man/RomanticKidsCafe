import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import time
import datetime
from pyfcm import FCMNotification


class FirebaseMsg:
    def __init__(self):

        self.APIKEY = "AAAAu1lLfgk:APA91bGm97mlyM7ZNInkEDMKmP_qBD5ndwodlwkV_5Lq0-UUQa9LD6iUzBgrKY1IC2jo063Q43WmepjL0Stk3tBipttq4sKza3T64pAH_zLWw291YlvHpzWdlOWJJ1cWvwFkxNlLPnpp"
        self.TOKEN = ""
        self.push_service = FCMNotification(self.APIKEY)

        self.db_url = 'https://princesssuvin6dwarf.firebaseio.com/'

        self.cred = credentials.Certificate("/home/lwjeong/RomanticKidsCafe/princesssuvinand6dwarf-firebase-adminsdk-ef7wr-73a48d4bb7.json")
        firebase_admin.initialize_app(self.cred, {
            #'projectId': 'princesssuvinand6dwarf-default-rtdb',
            'databaseURL' : 'https://princesssuvinand6dwarf-default-rtdb.asia-southeast1.firebasedatabase.app/'
        })
        
    def date_format(self,date):
        year = date.year
        month = date.month
        day = date.day
        hour = date.hour
        minute = date.minute
        second = date.second
    
        if len(str(minute)) == 1:
            minute = '0'+str(minute)
        if len(str(hour)) == 1:
            hour = '0'+str(hour)
        if len(str(second)) == 1:
            second = '0'+str(second)
    
        return f'{year}년 {month}월 {day}일 {hour}:{minute}:{second}'
        
    def sendMessage(self,body,url,topic_name):
        data_message = {
            "url" : url,
            "body" : body
        }
    
        result = self.push_service.notify_topic_subscribers(topic_name=topic_name, data_message=data_message)

        print(result)
        
    def falldown(self,url):
        ref = db.reference('cafe')
        for k in ref.get():
            #print(ref.get()[k]['url'])
            if ref.get()[k]['url'] == url:
                topic_name = ref.get()[k]['topic_name']
                cafe_name = k
                break
        ref1 = db.reference('fall_down/'+cafe_name)
        ref1.update({'name': '홍이삭', 'fall_down': True, 'girlfriend':False,'date':self.date_format(datetime.datetime.now())})
        self.sendMessage('아이가 넘어졌어요!',url,topic_name)