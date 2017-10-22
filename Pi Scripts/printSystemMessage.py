import sys
import textDisp
from time import sleep

if (argv[1] == "dev"):
    displayText("Entering Dev Mode")
    sleep(3)
    clearScreen()
elif (argv[1] == "shut"):
    displayText("Shutting down..")
    sleep(3)
    clearScreen()
    
