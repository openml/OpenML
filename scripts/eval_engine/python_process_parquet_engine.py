#!/usr/bin/env python3
"""
Example evaluation engine worker that downloads a parquet dataset from
MinIO (or any URL returned in `parquet_url`), computes a few simple
meta-features, and uploads a `data/features` XML to the OpenML API.

This is an example adapter to help update evaluation engines to support
parquet datasets. In production you would integrate this logic into the
real evaluation engine (e.g., the existing Weka-based engine).
"""
import argparse
import requests
import pandas as pd
import io
import xml.etree.ElementTree as ET

def download_parquet(url):
    r = requests.get(url)
    r.raise_for_status()
    return pd.read_parquet(io.BytesIO(r.content))

def compute_simple_metafeatures(df):
    feats = {}
    feats['NumberOfInstances'] = int(df.shape[0])
    feats['NumberOfFeatures'] = int(df.shape[1])
    # count missing values total
    feats['NumberOfMissingValues'] = int(df.isna().sum().sum())
    return feats

def build_features_xml(did, eval_id, features):
    root = ET.Element('oml:data', xmlns='http://openml.org/openml')
    ET.SubElement(root, 'oml:did').text = str(did)
    ET.SubElement(root, 'oml:evaluation_engine_id').text = str(eval_id)
    for name, val in features.items():
        q = ET.SubElement(root, 'oml:quality')
        ET.SubElement(q, 'oml:name').text = name
        ET.SubElement(q, 'oml:value').text = str(val)
    return ET.tostring(root, encoding='utf-8', xml_declaration=True)

def upload_features(api_url, api_key, xml_bytes):
    files = {'description': ('features.xml', xml_bytes, 'application/xml')}
    data = {'api_key': api_key}
    r = requests.post(api_url, data=data, files=files)
    r.raise_for_status()
    return r.text

def main():
    p = argparse.ArgumentParser()
    p.add_argument('--parquet-url', required=True, help='URL to parquet file (parquet_url)')
    p.add_argument('--api-url', required=True, help='OpenML data features upload endpoint: e.g. https://openml.example.org/data/features')
    p.add_argument('--api-key', required=True)
    p.add_argument('--did', required=True, type=int)
    p.add_argument('--eval-id', default=1, type=int)

    args = p.parse_args()

    print('Downloading parquet from', args.parquet_url)
    df = download_parquet(args.parquet_url)
    print('Computing features...')
    feats = compute_simple_metafeatures(df)
    xml = build_features_xml(args.did, args.eval_id, feats)
    print('Uploading features to API...')
    print(upload_features(args.api_url, args.api_key, xml))

if __name__ == '__main__':
    main()
