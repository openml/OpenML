import sys
import os
import pandas as pd
from minio import Minio
from minio.error import S3Error
from dotenv import load_dotenv
import arff
import pymysql
import logging
from typing import Optional
import urllib.request
import io # for io.StringIO()
from minio_convert_pq_to_arff import *
from minio_convert_arff_to_pq import *

load_dotenv()

#TODO Change prints to logs. 

def init_minio():
    client = Minio(
    os.getenv('MINIO_URL'),
    access_key=os.getenv('MINIO_ACCESS_KEY'),
    secret_key=os.getenv('MINIO_SECRET_KEY'),
    secure=False)
    return client

def connect_to_db():
    connection = pymysql.connect(host='localhost',
                             user=os.getenv('USERNAME'),
                             password=os.getenv('PASSWORD'),
                             database=os.getenv('DB_URI'),
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)
    return connection



def get_pending_datasets(connection, conversion_type):
    if conversion_type not in ['parquet', 'arff', 'none']:
        raise ValueError(
            "conversion_type should be one of 'parquet', 'arff' or 'none' "
            "Invalid type specified: {}".format(conversion_type)
        )
    with connection.cursor() as cursor:
        # Read a single record
        sql = "SELECT * FROM `data_conversion` WHERE `conversion_type`= " + "'"+conversion_type+"'"
        df = pd.read_sql_query(sql, connection)
        print("Pending datasets to be converted to ", conversion_type)
        print(df.head())
    return df


def update_conversion_table(did, connection):
    sql = "UPDATE data_conversion SET conversion_type = 'none' WHERE did = " + str(did)
    my_cursor = connection.cursor()
    my_cursor.execute(sql)
    connection.commit()

def perform_conversions():
    """
    Get NEWLY uploaded datasets which are to be converted to parquet or ARFF,
    Convert and upload them

    """
    print("Connecting to db")
    connection = connect_to_db()
    print("Connecting to minio")
    minio_client = init_minio()


    print('Getting datasets to be converted to arff')
    df_pq = get_pending_datasets(connection, 'arff')
    for index, row in df_pq.iterrows():
        did = row['did']
        # TODO: Change this hardcoded URL and fetch from dataset table
        parquet_url = f"http://openml1.win.tue.nl/dataset{did}/dataset_{did}.pq"      
        arff_file = pq_to_arff(parquet_url)
        minio_client.fput_object(f"dataset{did}", f"dataset_{did}.arff", arff_file.name)
        update_conversion_table(did, connection)
        print("saved dataset in ARFF format to bucket ", did)




    logging.info('Getting datasets to be converted to pq')
    df_arff = get_pending_datasets(connection, 'parquet')
    for index, row in df_arff.iterrows():
        did = row['did']
        # TODO: Change this hardcoded URL and fetch from dataset table
        arff_url = f"http://openml1.win.tue.nl/dataset{did}/dataset_{did}.arff"    
        df = arff_to_pandas(arff_url)
        df.to_parquet('dataset.pq')

        minio_client.fput_object(f"dataset{did}", f"dataset_{did}.pq", 'dataset.pq')
        print("saved dataset in parquet format to bucket  ", did)
        update_conversion_table(did, connection)

    logging.info('Done')

    if os.path.exists("dataset.pq"):
        os.remove("dataset.pq")
    else:
        print("The file does not exist")   

if __name__ == '__main__':
  perform_conversions()