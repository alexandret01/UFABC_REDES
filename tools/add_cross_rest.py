#!/usr/bin/env python

# Filename:                     add_cross_rest.py
# Command to run the program:   python3 add_cross_rest.py


import requests
import json

# Suppress HTTPS warnings
from urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(category=InsecureRequestWarning)

# Print a stream of bytes as pretty JSON
def printBytesAsJSON(bytes):
	print(json.dumps(json.loads(bytes), indent=2))

# Retrieve configuration through RESTCONF
oxc1 = requests.post(
	url = 'http://172.17.36.21:8008/api/data/optical-switch:cross-connects',
	auth = ('admin', 'root'),
	headers = {
		'Accept': 'application/yang-data+json',
                'Content-Type': 'application/yang-data+json'
	},

	data = json.dumps({
  "pair": [
    {
      "ingress": 1,
      "egress": 10
    },
    {
      "ingress": 2,
      "egress": 9
    }
  ]
}),
	verify = False)

# Print the HTTP response code
print('Response Code: CrossConnect in ports 1/10 and 2/9 in Polatis OXC 1')
print(str(oxc1.status_code))
print("")
# Retrieve configuration through RESTCONF
oxc2 = requests.post(
	url = 'http://172.17.36.22:8008/api/data/optical-switch:cross-connects',
	auth = ('admin', 'root'),
	headers = {
		'Accept': 'application/yang-data+json',
                'Content-Type': 'application/yang-data+json'
	},

	data = json.dumps({
  "pair": [
    {
      "ingress": 1,
      "egress": 10
    },
    {
      "ingress": 2,
      "egress": 9
    }
  ]
}),
	verify = False)

# Print the HTTP response code
print('Response Code: CrossConnect in ports 1/10 and 2/9 in Polatis OXC 2')
print(str(oxc2.status_code))
