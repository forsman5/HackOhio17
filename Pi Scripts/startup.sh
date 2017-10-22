#!/bin/bash

/usr/bin/sudo /usr/bin/python printSystemMessage.py bootEarly

devMode=$(/usr/bin/sudo /usr/bin/python checkSwitch.py)

if [ "$devMode" == 0 ];
then
	/usr/bin/sudo /usr/bin/python printSystemMessage.py boot

	/usr/bin/sudo /usr/bin/python main.py
	
	/usr/bin/sudo /usr/bin/python printSystemMessage.py shut

	/usr/bin/sudo /usr/bin/rm *.h264 -f
	
	/usr/bin/sudo /usr/bin/shutdown -h +0
else
	/usr/bin/sudo /usr/bin/python printSystemMessage.py dev
fi