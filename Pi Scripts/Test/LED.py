import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(18,GPIO.OUT)

sentinel = True
PIN_NUMBER = 18

while sentinel:
    userIn = input("Enter On, Off, or stop: ")

    if (userIn == "On" or userIn == "on" ):
        print("LED on")
        GPIO.output(PIN_NUMBER,GPIO.HIGH);
    elif (userIn == "off" or userIn == "Off"):
        print("LED off")
        GPIO.output(PIN_NUMBER,GPIO.LOW)
    else:
        print("interpretted as stop..")
        sentinel = False
        


