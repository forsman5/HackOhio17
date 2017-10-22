import RPi.GPIO as GPIO
import picamera
import constants
import datetime

# establish bluetooth connection here

camera = picamera.PiCamera()
camera.resolution = (constants.XRESOLUTION, constants.YRESOLUTION)

#GPIO setup
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(constants.IN_PIN,GPIO.IN, pull_up_down = GPIO.PUD_UP)

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

        #if picture button stopped loop
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

        #if video button stopped loop       
        elif (videoSentinel == constants.BUTTON_PRESSED)
            # start recording
            camera.start_recording(datetime.datetime.now().strftime("%Y%m%d%H%M%S") + ".h264"
)

            #timer to count number of loops elapsed - cap for length of video
            elapsed = 0

            #wait for button press
            while (videoSentinel == constants.BUTTON_UNPRESSED and elapsed < 600): # elapsed = .05 sleep * 600 = 30 seconds max
                videoSentinel = GPIO.input(constants.VIDEO_PIN)
                sleep(.05)
                elapsed = elapsed + 1
            
            camera.stop_recording()
            
        sleep(.01)

    if (not buttonHeld and pictureSentinel == constants.BUTTON_PRESSED):
        camera.start_preview()
        sleep(3)
        fileName = datetime.datetime.now().strftime("%Y%m%d%H%M%S") + ".png"
        camera.capture(fileName)
        camera.stop_preview()
        displayText("Picture@ " + fileName)
        clearDelay(3)

camera.close()
