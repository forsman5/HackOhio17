import RPi.GPIO as GPIO
import picamera
import datetime
import os
import sys

from bluetooth import *
from constants import *
from textDisp import displayText
from time import sleep

#function to change a .h264 to a mp4 and alert the user
#requires the installation of MP4Box!!
def convertVideo(file):
    fileWord = file[:(len(file) - 5)]
    os.system("MP4Box -fps 60 -add " + fileWord + ".h264 " + fileWord + ".mp4")
    displayText("Video@ " + fileWord + ".mp4")
    clearDelay(MESSAGE_DURATION)

def takePicture(cam):
    fileName = datetime.datetime.now().strftime("%Y%m%d%H%M%S") + ".png"
    camera.capture(fileName)
    displayText("Picture@ " + fileName)
    clearDelay(MESSAGE_DURATION)

# establish bluetooth connection here

camera = picamera.PiCamera()
camera.resolution = (XRESOLUTION, YRESOLUTION)

#GPIO setup
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(IN_PIN,GPIO.IN, pull_up_down = GPIO.PUD_UP)
GPIO.setup(VIDEO_PIN,GPIO.IN, pull_up_down = GPIO.PUD_UP)

# currently only searches for GPIO button press

#turn off on hold button press
buttonHeld = False

while (not buttonHeld):
    pictureSentinel = BUTTON_UNPRESSED
    videoSentinel = BUTTON_UNPRESSED
    #oldBlueTooth = initial bluetooth reading
    
    while (pictureSentinel == BUTTON_UNPRESSED and videoSentinel == BUTTON_UNPRESSED):
        #try { sock.getData }
        # if data != null { parseData }
        # if (data == pic) pictureSentinel = BUTTON_PRESSED )
        pictureSentinel = GPIO.input(IN_PIN)
        videoSentinel = GPIO.input(VIDEO_PIN)

        #if picture button stopped loop -- TAKE PICTURE
        if (pictureSentinel == BUTTON_PRESSED):
            #looking for a hold
            loopCount = 1

            while (pictureSentinel == BUTTON_PRESSED and loopCount < 250):
                loopCount = loopCount + 1
                pictureSentinel = GPIO.input(IN_PIN)
                sleep(.01)

            # resetting for waiting for button to be unpressed
            pictureSentinel = BUTTON_PRESSED

            if (loopCount > 200): # 2 seconds
                #pictureSentinel = unpressed by virtue of reaching this step
                buttonHeld = True

            if (not buttonHeld):
                takePicture(camera)

        #if video button stopped loop -- RECORD VIDEO     
        elif (videoSentinel == BUTTON_PRESSED):
            #wait for videoSentinel to be unpressed to start
            while (videoSentinel == BUTTON_PRESSED):
                videoSentinel = GPIO.input(VIDEO_PIN)
                sleep(.01)

            # start recording
            fileNameVideo = datetime.datetime.now().strftime("%Y%m%d%H%M%S") + ".h264"
            camera.start_recording(fileNameVideo)

            #timer to count number of loops elapsed - cap for length of video
            elapsed = 0

            #wait for button press
            while (videoSentinel == BUTTON_UNPRESSED and elapsed < 600): # elapsed = .05 sleep * 600 = 30 seconds max
                videoSentinel = GPIO.input(VIDEO_PIN)
                sleep(.05)
                elapsed = elapsed + 1

            camera.stop_recording()

            #converting the raw video into something useful
            convertVideo(fileNameVideo)
            
        sleep(.01)

camera.close()
