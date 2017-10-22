#!/bin/bash

devMode=$(sudo python checkSwitch.py)

if [ "$devMode" == 0 ];
then
	sudo python printSystemMessage.py boot

	sudo python main.py
	
	sudo python printSystemMessage.py shut

	sudo rm *.h264
	
	sudo shutdown -h +0
else
	sudo python printSystemMessage.py dev
fi