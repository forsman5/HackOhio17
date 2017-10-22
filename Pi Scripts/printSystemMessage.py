from sys import argv
from constants import *
from textDisp import displayYellow

if (argv[1] == "dev"):
    displayYellow("Entering Dev Mode")
    clearDelay(MESSAGE_DURATION)
elif (argv[1] == "shut"):
    displayYellow("Shutting down..")
    clearDelay(MESSAGE_DURATION)
elif (argv[1] == "boot"):
    displayYellow("System Booted")
    clearDelay(MESSAGE_DURATION / 2)
elif (argv[1] == "bootEarly"):
    displayYellow("Boot Started..")
    clearDelay(MESSAGE_DURATION)
else:
    displayYellow("ERROR 1 ENCOUNTERED")
    clearDelay(MESSAGE_DURATION * 5)
    
