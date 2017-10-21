import RPi.GPIO as GPIO
import constants

#GPIO setup
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(constants.SWITCH_PIN, GPIO.IN, pull_up_down = GPIO.PUD_UP)

returnValue = GPIO.input(constants.SWITCH_PIN)

if (returnValue == constants.BUTTON_PRESSED)
    returnValue = 1
else
    returnValue = 0

print(returnValue)
