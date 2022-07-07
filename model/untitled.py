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

    def sendMessage(self, body):
        data_message = {
            "body" : body
        }
        result = self.push_service.notify_topic_subscribers(topic_name="falldown7", data_message=data_message)

        print(result)
        
    def falldown(self):
        ref = db.reference('fall_down/test')
        ref.update({'name': '홍이삭', 'fall_down': True, 'girlfriend':False,'date':str(datetime.datetime.now())})
        self.sendMessage('[속보] 홍이삭 여친 생김')