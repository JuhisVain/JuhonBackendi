#!/bin/bash

# Checking server's response
function accessServer {
	curl -si http://localhost/weather?q=HELSINKI | head -n5 | tail -n1
}

# Should output "Server: JUBA server\n" 10 times
# Will probably output nothing is server not responding
for i in {1..10}
do
	accessServer
done
