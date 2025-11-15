Client examples for parquet upload

This folder contains small client examples for uploading parquet datasets to
the OpenML API.

- `python_upload_parquet.py`: full helper that converts CSV/dataframe to parquet and uploads.
- `R_upload_parquet.R`: R script showing same behavior using `arrow` and `httr`.
- `Java_upload_parquet_README.md`: Java snippet that uploads an existing parquet file.

Usage notes:
- Ensure the OpenML server is configured with `MINIO_TRANSFER_SCRIPT` above
  if you want automatic transfer to MinIO.
- The API expects the `description` multipart part to contain an XML with
  `<oml:format>parquet</oml:format>`.
