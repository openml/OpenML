from minio import Minio
from minio.error import S3Error
import sys
import os
from dotenv import load_dotenv
import json

load_dotenv()

client = Minio(
    os.getenv('MINIO_URL'),
    access_key=os.getenv('MINIO_ACCESS_KEY'),
    secret_key=os.getenv('MINIO_SECRET_KEY'),
    secure=False
)
id = sys.argv[1]
address = sys.argv[2]
file_type = sys.argv[3]

print("Creating bucket", id)
client.make_bucket(f"dataset{id}")
print("Successfully created bucket ")
client.fput_object(f"dataset{id}", f"dataset_{id}.{file_type}",address)

# Set bucket policy to read-only for bucket 'my-bucketname'

policy_read_only = {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": {"AWS": "*"},
            "Action": ["s3:GetBucketLocation", "s3:ListBucket"],
            "Resource": f"arn:aws:s3:::dataset"+str(id),
        },
        {
            "Effect": "Allow",
            "Principal": {"AWS": "*"},
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::dataset"+str(id)+"/*",
        },
    ],
}

client.set_bucket_policy(f'dataset{id}', json.dumps(policy_read_only))
