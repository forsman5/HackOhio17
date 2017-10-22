import sys
import constants
import textDisp

if (sys.argv[1] == "dev"):
    textDisp.displayText("Entering Dev Mode")
    constants.clearDelay(3)
elif (sys.argv[1] == "shut"):
    textDisp.displayText("Shutting down..")
    constants.clearDelay(3)
else:
    textDisp.displayText("ERROR 1 ENCOUNTERED")
    
