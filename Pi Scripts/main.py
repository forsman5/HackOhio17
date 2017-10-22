import RPi.GPIO as GPIO
import picamera
import constants
import datetime
import textDisp
import os
import sys

from time import sleep

#function to change a .h264 to a mp4 and alert the user
#requires the installation of MP4Box!!
def convertVideo(file):
    fileWord = file[:(len(file) - 5)]
    os.system("MP4Box -fps 60 -add " + fileWord + ".h264 " + fileWord + ".mp4")
    textDisp.displayText("Video@ " + fileWord + ".mp4")
    constants.clearDelay(constants.MESSAGE_DURATION)

def takePicture(cam):
    camera.start_preview()
    sleep(3)
    fileName = datetime.datetime.now().strftime("%Y%m%d%H%M%S") + ".png"
    camera.capture(fileName)
    camera.stop_preview()
    textDisp.displayText("Picture@ " + fileName)
    constants.clearDelay(constants.MESSAGE_DURATION)

# establish bluetooth connection here

camera = picamera.PiCamera()
camera.resolution = (constants.XRESOLUTION, constants.YRESOLUTION)

#GPIO setup
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(constants.IN_PIN,GPIO.IN, pull_up_down = GPIO.PUD_UP)
GPIO.setup(constants.VIDEO_PIN,GPIO.IN, pull_up_down = GPIO.PUD_UP)

# currently only searches for GPIO button press

#turn off on hold button press
buttonHeld = False

while (not buttonHeld):
    pictureSentinel = constants.BUTTON_UNPRESSED
    videoSentinel = constants.BUTTON_UNPRESSED
    #oldBlueTooth = initial bluetooth reading
    
    while (pictureSentinel == constants.BUTTON_UNPRESSED and videoSentinel == constants.BUTTON_UNPRESSED):
        #newBlueTooth = getReading()
        #if (oldBlueTooth != newBluetooth):
        #   pictureSentinel = constants.BUTTON_PRESSED
        #else:
        pictureSentinel = GPIO.input(constants.IN_PIN)
        videoSentinel = GPIO.input(constants.VIDEO_PIN)

        #if picture button stopped loop -- TAKE PICTURE
        if (pictureSentinel == constants.BUTTON_PRESSED):
            #looking for a hold
            loopCount = 1

            while (pictureSentinel == constants.BUTTON_PRESSED and loopCount < 250):
                loopCount = loopCount + 1
                pictureSentinel = GPIO.input(constants.IN_PIN)
                sleep(.01)

            # resetting for waiting for button to be unpressed
            pictureSentinel = constants.BUTTON_PRESSED

            if (loopCount > 200): # 2 seconds
                #pictureSentinel = unpressed by virtue of reaching this step
                buttonHeld = True

            if (not buttonHeld)
                takePicture(camera)

        #if video button stopped loop -- RECORD VIDEO     
        elif (videoSentinel == constants.BUTTON_PRESSED):
            #wait for videoSentinel to be unpressed to start
            while (videoSentinel == constants.BUTTON_PRESSED):
                videoSentinel = GPIO.input(constants.VIDEO_PIN)
                sleep(.01)

            # start recording
            fileNameVideo = datetime.datetime.now().strftime("%Y%m%d%H%M%S") + ".h264"
            camera.start_recording(fileNameVideo)
            camera.start_preview()

            #timer to count number of loops elapsed - cap for length of video
            elapsed = 0

            #wait for button press
            while (videoSentinel == constants.BUTTON_UNPRESSED and elapsed < 600): # elapsed = .05 sleep * 600 = 30 seconds max
                videoSentinel = GPIO.input(constants.VIDEO_PIN)
                sleep(.05)
                elapsed = elapsed + 1

            camera.stop_preview()
            camera.stop_recording()

            #converting the raw video into something useful
            convertVideo(fileNameVideo)
            
        sleep(.01)

camera.close()
