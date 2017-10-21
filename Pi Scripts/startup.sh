#!/bin/bash

devMode=$(sudo python checkSwitch.py)

if [ "$devMode" == 0 ];
then
	sudo python main.py

	sudo shutdown -h +0
fi