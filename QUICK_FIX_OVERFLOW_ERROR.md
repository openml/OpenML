# Quick Fix: OverflowError on Large Dataset Upload

## Error You're Seeing
```
OverflowError: string longer than 2147483647 bytes
```

## Immediate Solutions (Pick One)

### Solution 1: Compress Your Dataset (EASIEST) ⭐
```bash
gzip -9 your_dataset.arff
```
This typically reduces file size by 80-95% for sparse datasets. Upload the `.arff.gz` file instead.

### Solution 2: Use Direct HTTP Upload (MOST RELIABLE)
Replace your `publish_dataset.py` with this:

```python
import requests
import os

# Configuration
API_KEY = "your_api_key_here"
DATASET_FILE = "your_dataset.arff"  # or .arff.gz
DATASET_NAME = "Your Dataset Name"
DATASET_DESCRIPTION = "Description of your dataset"

# Create XML description
xml_description = f"""<?xml version="1.0" encoding="UTF-8"?>
<oml:data_set_description xmlns:oml="http://openml.org/openml">
    <oml:name>{DATASET_NAME}</oml:name>
    <oml:description>{DATASET_DESCRIPTION}</oml:description>
    <oml:format>arff</oml:format>
</oml:data_set_description>"""

# Upload with streaming (no memory overflow)
print(f"Uploading {DATASET_FILE} ({os.path.getsize(DATASET_FILE) / 1e9:.2f} GB)...")
with open(DATASET_FILE, 'rb') as f:
    response = requests.post(
        'https://www.openml.org/api/v1/data',
        data={
            'api_key': API_KEY,
            'description': xml_description
        },
        files={'dataset': (os.path.basename(DATASET_FILE), f)},
        timeout=7200  # 2 hour timeout
    )

print(response.status_code)
print(response.text)
```

### Solution 3: Host Externally (BEST FOR VERY LARGE FILES)
1. Upload to Zenodo, Figshare, or S3
2. Get the permanent URL
3. Register in OpenML:

```python
import openml

dataset = openml.datasets.OpenMLDataset(
    name="Your Dataset Name",
    description="Your description",
    url="https://zenodo.org/record/XXXXX/files/dataset.arff.gz",
    format="arff"
)
dataset.publish()
```

## Why This Happens

1. **Python limitation**: SSL write buffer cannot exceed 2GB (signed 32-bit int max)
2. **Client bug**: openml-python loads entire file into memory instead of streaming
3. **Server limits**: Default OpenML server limits were 2MB (now fixed to 5GB)

## Need More Help?

See [LARGE_DATASET_UPLOAD_FIX.md](./LARGE_DATASET_UPLOAD_FIX.md) for complete details.
