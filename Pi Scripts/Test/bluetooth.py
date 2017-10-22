from bluetooth import *
import sys

uuid = "00002902-0000-1000-8000-00805f9b34fb"
service_matches = bluetooth.find_service( uuid = uuid )

if (len(service_matches) == 0):
    print "couldn't find the FooBar service"
    sys.exit(0)

first_match = service_matches[0]
port = first_match["port"]
name = first_match["name"]
host = first_match["host"]

print "connecting to \"%s\" on %s" % (name, host)

sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
sock.connect((host, port))

lastData = sock.recv(1024)
count = 0
while (count < 99999):
    count = count + 1
    data = sock.recv(1024)
    if (data != lastData):
        print(data)
        lastData=data

sock.close()
