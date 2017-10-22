import sys
import constants
import textDisp

if (sys.argv[1] == "dev"):
    textDisp.displayYellow("Entering Dev Mode")
    constants.clearDelay(constants.MESSAGE_DURATION)
elif (sys.argv[1] == "shut"):
    textDisp.displayYellow("Shutting down..")
    constants.clearDelay(constants.MESSAGE_DURATION)
elif (sys.argv[1] == "boot"):
    textDisp.displayYellow("System Booted")
    constants.clearDelay(constants.MESSAGE_DURATION / 2)
else:
    textDisp.displayYellow("ERROR 1 ENCOUNTERED")
    constants.clearDelay(constants.MESSAGE_DURATION * 5)
    
