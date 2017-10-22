import time
from time import sleep

import Adafruit_GPIO.SPI as SPI
import Adafruit_SSD1306

from PIL import Image
from PIL import ImageDraw
from PIL import ImageFont


# Raspberry Pi pin configuration:
RST = 21
# Note the following are only used with SPI:
# DC = 23
# SPI_PORT = 0
# SPI_DEVICE = 0

# 128x64 display with hardware I2C: 
disp = Adafruit_SSD1306.SSD1306_128_64(rst=RST)


def clearScreen():

	# Initialize library.
	disp.begin()

	# Clear display.
	disp.clear()
	disp.display()

	# Create blank image for drawing.
	# Make sure to create image with mode '1' for 1-bit color.
	width = disp.width
	height = disp.height
	image = Image.new('1', (width, height))

	# Get drawing object to draw on image.
	draw = ImageDraw.Draw(image)

	# Draw a black filled box to clear the image.
	draw.rectangle((0,0,width,height), outline=0, fill=0)

	# Display image.
	disp.image(image)
	disp.display()

def displayText( str ):

	maxChars = 14
	# Initialize library.
	disp.begin()

	# Clear display.
	disp.clear()
	disp.display()

	# Create blank image for drawing.
	# Make sure to create image with mode '1' for 1-bit color.
	width = disp.width
	height = disp.height
	image = Image.new('1', (width, height))

	# Get drawing object to draw on image.
	draw = ImageDraw.Draw(image)

	# Draw a black filled box to clear the image.
	draw.rectangle((0,0,width,height), outline=0, fill=0)

	# Load default font.
#	font = ImageFont.load_default()
	fontPath = "./Minecraft.ttf"
	minecraft20 = ImageFont.truetype ( fontPath, 20 )
	minecraft16 = ImageFont.truetype ( fontPath, 16 )
	minecraft30 = ImageFont.truetype ( fontPath, 30 )


	# Alternatively load a TTF font.  Make sure the .ttf font file is in the same directory as the python script!
	# Some other nice fonts to try: http://www.dafont.com/bitmap.php
	#font = ImageFont.truetype('Minecraftia.ttf', 8)
		
	x = 0
	top = 2

	if(len(str) > 2 * maxChars):
		firstLine = str[:maxChars]
		secondLine = str[maxChars:2*maxChars]
		thirdLine = str[2*maxChars:]
		# Write two lines of text.
		draw.text((x, top + 14),  firstLine,  font=minecraft16, fill=255)
		draw.text((x, top + 30),  secondLine,  font=minecraft16, fill=255)	
		draw.text((x, top + 46),  thirdLine,  font=minecraft16, fill=255)		
	elif (len(str) > maxChars):
		firstLine = str[:maxChars]
		secondLine = str[maxChars:]
		# Write two lines of text.
		draw.text((x, top + 14),  firstLine,  font=minecraft16, fill=255)
		draw.text((x, top + 30),  secondLine,  font=minecraft16, fill=255)
	else:
		firstLine = str
		# Write two lines of text.
		draw.text((x, top + 14),  firstLine,  font=minecraft16, fill=255)

	# Display image.
	disp.image(image)
	disp.display()


def displayYellow( str ):

	# Initialize library.
	disp.begin()

	# Clear display.
	disp.clear()
	disp.display()

	# Create blank image for drawing.
	# Make sure to create image with mode '1' for 1-bit color.
	width = disp.width
	height = disp.height
	image = Image.new('1', (width, height))

	# Get drawing object to draw on image.
	draw = ImageDraw.Draw(image)

	# Draw a black filled box to clear the image.
	draw.rectangle((0,0,width,height), outline=0, fill=0)

	# Load default font.
#	font = ImageFont.load_default()
	fontPath = "./Minecraft.ttf"
	minecraft20 = ImageFont.truetype ( fontPath, 20 )
	minecraft16 = ImageFont.truetype ( fontPath, 16 )
	minecraft14 = ImageFont.truetype ( fontPath, 14)


	# Alternatively load a TTF font.  Make sure the .ttf font file is in the same directory as the python script!
	# Some other nice fonts to try: http://www.dafont.com/bitmap.php
	#font = ImageFont.truetype('Minecraftia.ttf', 8)
		
	x = 0
	top = 0

		# Write two lines of text.
	draw.text((x, top),  str,  font=minecraft16, fill=255)


	# Display image.
	disp.image(image)
	disp.display()
