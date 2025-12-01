# Large Dataset Upload Fix

## Problem Summary
Users attempting to upload large datasets (2.7GB+) were encountering:
1. **Server-side rejection**: PHP upload limits too restrictive (2MB max)
2. **Client-side OverflowError**: Python SSL limitation when sending >2GB as single buffer

## Server-Side Fix (COMPLETED ✓)

### Changes Made to `/docker/config/php.ini`:

| Setting | Old Value | New Value | Purpose |
|---------|-----------|-----------|---------|
| `upload_max_filesize` | 2M | **5G** | Maximum size per uploaded file |
| `post_max_size` | 8M | **5G** | Maximum total POST request size |
| `max_execution_time` | 30 | **3600** | Maximum script runtime (1 hour) |
| `memory_limit` | 16G | 16G | Already sufficient ✓ |

### Deployment Required
After making these changes, you **must restart** the OpenML Docker container:
```bash
docker-compose down
docker-compose up -d --build
```

Or if using plain Docker:
```bash
docker stop <container_name>
docker start <container_name>
```

---

## Client-Side Issue (Still Needs Addressing)

### The OverflowError Explained
```
OverflowError: string longer than 2147483647 bytes
```

**Root cause**: Python's SSL layer uses a signed 32-bit integer for write buffer length. This limits a single `send()` call to 2,147,483,647 bytes (2^31-1 ≈ 2GB).

**Why it happens**: The `openml-python` client or `requests` library may be:
1. Reading the entire 2.7GB file into memory as one bytes object
2. Building the entire multipart POST body in memory
3. Attempting to send it in one SSL write operation

### Solutions for Client-Side

#### Option 1: Stream the Upload (RECOMMENDED)
Modify how the file is passed to the OpenML client. Instead of:
```python
# BAD - loads entire file into memory
with open('dataset.arff', 'rb') as f:
    data = f.read()  # 2.7GB in RAM!
    openml_dataset.publish()  # triggers OverflowError
```

Use streaming (requires patching openml-python or using direct requests):
```python
# GOOD - streams in chunks
import requests

with open('dataset.arff', 'rb') as f:
    files = {'dataset': ('dataset.arff', f)}  # Pass file handle, not bytes
    response = requests.post(
        'https://openml.org/api/v1/data',
        files=files,
        data={'api_key': 'YOUR_KEY', 'description': xml_description}
    )
```

**Note**: If `openml-python` internally calls `f.read()`, you'll need to patch it or use Option 2/3.

#### Option 2: Compress Before Upload
Reduce file size below 2GB:
```bash
# ARFF supports gzip compression
gzip dataset.arff
# Result: dataset.arff.gz (often 10-50x smaller for sparse data)
```

Then upload the `.arff.gz` file. OpenML should accept compressed ARFF.

#### Option 3: Host Externally and Register by URL
Upload to a service that handles large files:
- **Zenodo**: Free, DOI-based, handles 50GB+
- **AWS S3**: Pay-per-use, unlimited size
- **Institutional repository**: Check your university

Then register the dataset in OpenML by URL:
```python
import openml

dataset = openml.datasets.OpenMLDataset(
    name="My Large Dataset",
    description="...",
    url="https://zenodo.org/record/12345/files/dataset.arff.gz",
    format="arff",
    version_label="1.0"
)
dataset.publish()
```

#### Option 4: Patch openml-python
If you control the client environment, patch the library to use streaming:

**File to patch**: `<python_site_packages>/openml/_api_calls.py`

Find the section that builds `file_elements` and ensure it passes file handles, not bytes:
```python
# In _perform_api_call or _read_url_files
# BEFORE (bad):
file_data = open(filepath, 'rb').read()  # Loads all into memory
file_elements = {'dataset': (filename, file_data)}

# AFTER (good):
file_handle = open(filepath, 'rb')  # Keep handle open
file_elements = {'dataset': (filename, file_handle)}
```

---

## Testing Your Fix

### Server-Side Test
1. Check PHP configuration is loaded:
   ```bash
   docker exec <container_name> php -i | grep -E 'upload_max_filesize|post_max_size|max_execution_time'
   ```
   Should show: `upload_max_filesize => 5G`, `post_max_size => 5G`, `max_execution_time => 3600`

2. Try a test upload via curl:
   ```bash
   curl -X POST https://your-openml-server.org/api/v1/data \
     -F "api_key=YOUR_KEY" \
     -F "description=@description.xml" \
     -F "dataset=@test_large_file.arff"
   ```

### Client-Side Test
1. Try uploading a 1GB file first (below the 2GB SSL limit)
2. Monitor memory usage: `htop` or Task Manager
3. If successful, the client is streaming properly
4. For 2.7GB files, use compression or external hosting

---

## Recommended Workflow for 2.7GB Dataset

**Best approach combining all solutions:**

1. **Compress the dataset** (reduces transfer time and bypasses SSL limit):
   ```bash
   gzip -9 dataset.arff  # Maximum compression
   ```

2. **Verify server config** (already fixed in this repo):
   - Restart Docker container to load new php.ini

3. **Upload via direct HTTP streaming** (bypass openml-python client):
   ```python
   import requests
   
   api_key = "YOUR_API_KEY"
   url = "https://openml.org/api/v1/data"
   
   # Prepare XML description
   xml_desc = """<?xml version="1.0" encoding="UTF-8"?>
   <oml:data_set_description xmlns:oml="http://openml.org/openml">
     <oml:name>Dataset Name</oml:name>
     <oml:description>Description here</oml:description>
     <oml:format>arff</oml:format>
   </oml:data_set_description>"""
   
   # Stream upload
   with open('dataset.arff.gz', 'rb') as f:
       response = requests.post(
           url,
           data={'api_key': api_key, 'description': xml_desc},
           files={'dataset': ('dataset.arff.gz', f)},
           timeout=3600  # 1 hour timeout for large uploads
       )
   
   print(response.text)
   ```

4. **Monitor upload progress** (optional):
   ```python
   from tqdm import tqdm
   import requests
   
   # Wrapper for progress bar
   class TqdmUploader:
       def __init__(self, filename):
           self.filename = filename
           self.size = os.path.getsize(filename)
           self.progress = tqdm(total=self.size, unit='B', unit_scale=True)
       
       def __enter__(self):
           self.f = open(self.filename, 'rb')
           return self
       
       def __exit__(self, *args):
           self.f.close()
           self.progress.close()
       
       def read(self, size=-1):
           chunk = self.f.read(size)
           self.progress.update(len(chunk))
           return chunk
   
   with TqdmUploader('dataset.arff.gz') as uploader:
       response = requests.post(url, files={'dataset': uploader}, ...)
   ```

---

## Additional Considerations

### Web Server Configuration
If you're using **nginx** as a reverse proxy (not present in current setup), also add:
```nginx
client_max_body_size 5G;
proxy_read_timeout 3600s;
```

### Network Timeouts
For very large uploads over slow connections:
- **Client timeout**: Set `timeout=7200` in requests (2 hours)
- **Server timeout**: Already set via `max_execution_time = 3600`
- **Load balancer timeout**: Check cloud provider settings (AWS ALB, GCP LB, etc.)

### Storage Space
Uploading 2.7GB datasets requires adequate disk space:
- **Temporary space**: `/tmp` needs ~2.7GB during upload
- **Final storage**: `DATA_PATH` needs ~2.7GB per dataset
- **Recommend**: 50GB+ free space on server

### Alternative: Split Dataset
If all else fails, consider splitting into multiple smaller datasets:
```python
# Split dataset into chunks
import pandas as pd

df = pd.read_csv('dataset.csv')
chunk_size = 1_000_000  # 1M rows per chunk

for i, start in enumerate(range(0, len(df), chunk_size)):
    chunk = df[start:start + chunk_size]
    chunk.to_csv(f'dataset_part{i}.arff', index=False, header=True)
    # Upload each part separately
```

---

## Summary

✅ **Server-side limits fixed** (this repo)  
⚠️ **Client-side requires**:
- File compression (easiest)
- Streaming upload (most robust)
- External hosting (most flexible)

**For your 2.7GB file**: Compress with gzip first, should reduce to <500MB for typical datasets.
