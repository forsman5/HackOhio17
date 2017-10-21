#!/bin/bash

turnOff=$(sudo python checkSwitch.py)

sudo python main.py

if [turnOff = "1"]
	sudo shutdown -h
fi