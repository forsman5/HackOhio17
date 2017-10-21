import RPi.GPIO as GPIO
import picamera
from time import sleep
import datetime

# establish bluetooth connection here

camera = picamera.PiCamera()

#constants
IN_PIN = 23
BUTTON_PRESSED = False
BUTTON_UNPRESSED = True

#GPIO setup
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(IN_PIN,GPIO.IN, pull_up_down = GPIO.PUD_UP)

# currently only searches for GPIO button press

#turn off on hold button press
buttonHeld = False

while (not buttonHeld):
    sentinel = BUTTON_UNPRESSED
    #oldBlueTooth = initial bluetooth reading
    
    while (sentinel == BUTTON_UNPRESSED):
        #newBlueTooth = getReading()
        #if (oldBlueTooth != newBluetooth):
        #   sentinel = false
        #else:
        sentinel = GPIO.input(IN_PIN)
        
        if (sentinel == BUTTON_PRESSED):
            #looking for a hold
            loopCount = 1

            while (sentinel == BUTTON_PRESSED and loopCount < 250):
                loopCount = loopCount + 1
                sentinel = GPIO.input(IN_PIN)
                sleep(.01)

            # resetting for waiting for button to be unpressed
            sentinel = BUTTON_PRESSED

            if (loopCount > 200): # 2 seconds
                #sentinel = unpressed by virtue of reaching this step
                buttonHeld = True
            
        sleep(.01)

    if (not buttonHeld):
        camera.start_preview()
        sleep(3)
        camera.capture(datetime.datetime.now().strftime("%X") + "_" + datetime.datetime.now().strftime("%x") + ".png")
        camera.stop_preview()

camera.close()
