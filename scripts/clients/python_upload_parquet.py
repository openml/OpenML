#!/usr/bin/env python3
"""
Small helper: convert a pandas DataFrame to parquet and upload to OpenML API.

Usage:
  - Install requirements: pip install -r scripts/clients/requirements.txt
  - Run:
      python3 scripts/clients/python_upload_parquet.py \
        --api-url https://openml.example.org/data/v1 \
        --api-key YOUR_KEY \
        --name my_dataset \
        --file /path/to/output.parquet

This script demonstrates how a client can produce a parquet file and POST
it to the OpenML `/data` endpoint with an XML description that sets
`format` to `parquet`.
"""
import argparse
import io
import os
import sys
import requests
import pandas as pd

def build_description_xml(name, description, fmt='parquet', visibility='public'):
    xml = f'''<?xml version="1.0" encoding="UTF-8"?>
<oml:data xmlns:oml="http://openml.org/openml">
  <oml:name>{name}</oml:name>
  <oml:description>{description}</oml:description>
  <oml:version>1</oml:version>
  <oml:format>{fmt}</oml:format>
  <oml:visibility>{visibility}</oml:visibility>
</oml:data>'''
    return xml

def upload_parquet(api_url, api_key, parquet_path, name, description):
    desc_xml = build_description_xml(name, description, fmt='parquet')
    files = {
        'description': ('description.xml', desc_xml, 'application/xml'),
        'dataset': (os.path.basename(parquet_path), open(parquet_path, 'rb'), 'application/octet-stream')
    }
    data = {'api_key': api_key}
    resp = requests.post(api_url, data=data, files=files)
    resp.raise_for_status()
    return resp.text

def main():
    p = argparse.ArgumentParser()
    p.add_argument('--api-url', required=True)
    p.add_argument('--api-key', required=True)
    p.add_argument('--file', required=True)
    p.add_argument('--name', required=True)
    p.add_argument('--description', default='Uploaded via python_upload_parquet.py')

    args = p.parse_args()

    # If path points to a CSV, convert to parquet
    if args.file.endswith('.csv'):
        df = pd.read_csv(args.file)
        parquet_path = args.file.rsplit('.', 1)[0] + '.parquet'
        df.to_parquet(parquet_path, index=False)
    else:
        parquet_path = args.file

    print('Uploading', parquet_path, 'to', args.api_url)
    print(upload_parquet(args.api_url, args.api_key, parquet_path, args.name, args.description))

if __name__ == '__main__':
    main()
