import RPi.GPIO as GPIO
from time import sleep
from constants import *

#length of time to check for button press
WINDOW = 200

#GPIO setup
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(SWITCH_PIN, GPIO.IN, pull_up_down = GPIO.PUD_UP)

# this is done differently than in main - different ways to account for bouncing
# later, we can decide whichever works better and port to the other occurence
timer = 0;
pressedCount = 0;
while (timer < WINDOW):
    returnValue = GPIO.input(SWITCH_PIN)
    if (returnValue == BUTTON_PRESSED):
        pressedCount = pressedCount + 1

    sleep(.01)
    timer = timer + 1

percentage = pressedCount / WINDOW

if (percentage > .95):
    returnValue = 1
else:
    returnValue = 0

#return the value to the shell
print(returnValue)
