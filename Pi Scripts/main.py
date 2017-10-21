# establish bluetooth connection here

import RPi.GPIO as GPIO
import picamera
from time import sleep

camera = picamera.PiCamera()

#constants
FILENAME = "RecentFile.png"
IN_PIN = 23

#GPIO setup
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(IN_PIN,GPIO.IN)

# currently only searches for GPIO button press
while (1):
    sentinel = False
    while (!sentinel):
        #reassign sentinel here also using bluetooth
        sentinel = GPIO.input(IN_PIN) # and bluetooth.signal()
        sleep(.5)

    camera.start_preview()
    sleep(3)
    camera.capture(FILENAME)
    camera.stop_preview()

camera.close()
