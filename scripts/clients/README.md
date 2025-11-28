Python client helper for parquet upload

Files:
- `python_upload_parquet.py`: Convert CSV/DataFrame -> parquet and POST to `/data` API.
- `requirements.txt`: Python dependencies.

Quick start:

1. Create a virtualenv and install dependencies:

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r scripts/clients/requirements.txt
```

2. Convert CSV and upload:

```bash
python3 scripts/clients/python_upload_parquet.py \
  --api-url https://openml.example.org/data/v1 \
  --api-key YOUR_API_KEY \
  --file /path/to/my.csv \
  --name my_dataset
```

The script will convert `my.csv` to `my.parquet` and upload it using the
OpenML upload API with `format=parquet` in the XML description.
