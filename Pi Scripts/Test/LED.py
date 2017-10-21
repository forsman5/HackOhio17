import RPi.GPIO as GPIO

PIN_NUMBER = 24

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(PIN_NUMBER,GPIO.OUT)

sentinel = True

while sentinel:
    userIn = input("Enter On, Off, camera, or stop: ")

    if (userIn == "On" or userIn == "on" ):
        print("LED on")
        GPIO.output(PIN_NUMBER,GPIO.HIGH);
    elif (userIn == "off" or userIn == "Off"):
        print("LED off")
        GPIO.output(PIN_NUMBER,GPIO.LOW)
    else:
        print("interpretted as stop..")
        sentinel = False

        


