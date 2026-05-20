#!/usr/bin/env python3

# Filename:                     add_cross_rest.py
# Command to run the program:   python3 add_cross_rest.py
#
# Topologia atual do lab:
#   OXC1 (172.17.36.21) — COM DEFEITO, ignorado
#   OXC2 (172.17.36.22) — ativo
#     Cross-connects bidirecionais:
#       porta 1  <-> porta 13  (T100DCT#2 WDM <-> T100DCT#27 WDM, caminho direto)
#       porta 5  <-> porta 9   (T100DCT#27 WDM <-> T100DCT#2 WDM, caminho de retorno)

import requests
import json

# Suppress HTTPS warnings
from urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(category=InsecureRequestWarning)

# ---------------------------------------------------------------
# OXC1 (172.17.36.21) — COM DEFEITO, não configurar
# ---------------------------------------------------------------
print('OXC1 (172.17.36.21) marcado como COM DEFEITO — ignorando.')
print('')

# ---------------------------------------------------------------
# OXC2 (172.17.36.22) — cross-connects bidirecionais
# ---------------------------------------------------------------
oxc2 = requests.post(
    url='http://172.17.36.22:8008/api/data/optical-switch:cross-connects',
    auth=('admin', 'root'),
    headers={
        'Accept': 'application/yang-data+json',
        'Content-Type': 'application/yang-data+json'
    },
    data=json.dumps({
        "pair": [
            {"ingress": 1,  "egress": 13},   # T100DCT#2 TX  -> T100DCT#27 RX
            {"ingress": 13, "egress": 1},    # T100DCT#27 RX -> T100DCT#2 TX (retorno)
            {"ingress": 5,  "egress": 9},    # T100DCT#27 TX -> T100DCT#2 RX
            {"ingress": 9,  "egress": 5}     # T100DCT#2 RX  -> T100DCT#27 TX (retorno)
        ]
    }),
    verify=False
)

print('OXC2 (172.17.36.22) — Cross-connects: 1↔13 e 5↔9')
print('HTTP Status:', oxc2.status_code)
if oxc2.text:
    try:
        print(json.dumps(json.loads(oxc2.text), indent=2))
    except Exception:
        print(oxc2.text)
