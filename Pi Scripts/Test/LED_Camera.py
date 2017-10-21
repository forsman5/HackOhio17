import RPi.GPIO as GPIO
import picamera
import time

camera = picamera.PiCamera()

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(18,GPIO.OUT)

sentinel = True
PIN_NUMBER = 18

while sentinel:
    userIn = input("Enter On, Off, camera, or stop: ")

    if (userIn == "On" or userIn == "on" ):
        print("LED on")
        GPIO.output(PIN_NUMBER,GPIO.HIGH);
    elif (userIn == "off" or userIn == "Off"):
        print("LED off")
        GPIO.output(PIN_NUMBER,GPIO.LOW)
    elif (userIn == "Camera" or userIn == "camera")
        fileName = input("Enter the filename, includng extension, to save to: ")
    
        print("Point the camera where you want. A three second preview will start. \n")
        print("Once the preview is closed, an image file will be saved at the specified location.")
        sleep(3)
        camera.start_preview()
        sleep(3)
        camera.capture(fileName)
        camera.stop_preview()
            
    else:
        print("interpretted as stop..")
        sentinel = False


camera.close()
        


