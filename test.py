from ipywidgets import Video, Image
from IPython.display import display 
from PIL import Image 
import numpy as np
import cv2
import base64
import ipywidgets as widgets
import copy
import time
import mediapipe as mp
import IPython

mp_drawing = mp.solutions.drawing_utils
mp_drawing_styles = mp.solutions.drawing_stylesa
mp_pose = mp.solutions.pose

cap = cv2.VideoCapture('./before/Image/1.mp4')
wImg = widgets.Image(
    layout = widgets.Layout(border="solid")
)
display(wImg)

if cap.isOpened():
    ret, img = cap.read()
    if ret:
        # 동영상 파일에서 캡쳐된 이미지를 이미지 파일 스트림으로 다시 인코딩을 한다.
        tmpStream = cv2.imencode(".jpeg", img)[1].tostring()
        wImg.value = tmpStream
        # 20 프레임이 되기 위한 딜레이 다만, 실제로 입력한 것보다 조금 더 딜레이가 있다
        ret, img = cap.read()
cap.release()

# 영상 느리게 나오는건 사양때문이 아니라 인터넷 속도 때문입니다
# 1080p 사진을 초당 몇십장씩 보내야 해서 100mbps가 부족하기때문에 느리게 나와여

w = round(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
h = round(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
fps = cap.get(cv2.CAP_PROP_FPS) # 카메라에 따라 값이 정상적, 비정상적

# fourcc 값 받아오기, *는 문자를 풀어쓰는 방식, *'DIVX' == 'D', 'I', 'V', 'X'
fourcc = cv2.VideoWriter_fourcc(*'DIVX')

# 1프레임과 다음 프레임 사이의 간격 설정
delay = round(1000/fps)

# 웹캠으로 찰영한 영상을 저장하기
# cv2.VideoWriter 객체 생성, 기존에 받아온 속성값 입력
out = cv2.VideoWriter('output.avi', fourcc, fps, (w, h))

cap = cv2.VideoCapture('./before/Image/1.mp4')
wImg = widgets.Image(
    layout = widgets.Layout(border="solid")
)
display(wImg)
with mp_pose.Pose(
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5) as pose:
  while cap.isOpened():
    success, image = cap.read()
    if not success:
      print("Ignoring empty camera frame.")
      # If loading a video, use 'break' instead of 'continue'.
      continue

    # To improve performance, optionally mark the image as not writeable to
    # pass by reference.
    image.flags.writeable = False
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    results = pose.process(image)

    # Draw the pose annotation on the image.
    image.flags.writeable = True
    image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
    mp_drawing.draw_landmarks(
        image,
        results.pose_landmarks,
        mp_pose.POSE_CONNECTIONS,
        landmark_drawing_spec=mp_drawing_styles.get_default_pose_landmarks_style())
    # Flip the image horizontally for a selfie-view display.

    #cv2.imshow('MediaPipe Pose', cv2.flip(image, 1))
    out.write(image)

cap.release()
out.release()
