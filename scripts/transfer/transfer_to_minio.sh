#!/usr/bin/env bash
# Example transfer script used by the API when MINIO_TRANSFER_SCRIPT is configured.
# It receives two arguments: local file path and destination path inside MinIO.
# The operator should customize the method below (mc, aws, rclone, etc.) and
# make sure credentials are configured outside of this script.

set -euo pipefail

LOCAL="$1"
DEST="$2"  # e.g. datasets/0000/0001/dataset_1.pq

### OPTION 1: Using `mc` (MinIO client). Configure an alias 'minio' and bucket 'mybucket'.
# mc cp "$LOCAL" "minio/mybucket/$DEST"

### OPTION 2: Using AWS CLI against S3-compatible MinIO endpoint
# Example: AWS env vars or profile must be configured with credentials.
# aws --endpoint-url https://minio.example.org s3 cp "$LOCAL" "s3://mybucket/$DEST"

### OPTION 3: Using rclone
# rclone copyto "$LOCAL" "minio:mybucket/$DEST"

### Default: if no command is uncommented, print what would be done and exit
echo "Transfer script invoked with: local=$LOCAL dest=$DEST"
echo "Customize this script to actually upload to MinIO (mc/aws/rclone)."
exit 0
