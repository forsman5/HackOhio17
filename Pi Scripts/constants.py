from time import sleep
import textDisp
#constants

#pin marking where the PICTURE TAKING button is connected to
IN_PIN = 24

#pin marking where the VIDEO TAKING button is connected to
VIDEO_PIN = 20

#pin that controls the checking on startup
    # if this is true, then after finishing exectuion, the py will NOT shut down
    # otherwise it will shut down
SWITCH_PIN = 24

#constants to control actions on button pressing - for in case of inversion
    #also allows code to be easier to understand
BUTTON_PRESSED = False
BUTTON_UNPRESSED = True

#resolutions for the camera
XRESOLUTION = 640
YRESOLUTION = 480

#sleep then clear screen. used for multithreading
def clearDelay(s):
    sleep(s)
    clearScreen()
