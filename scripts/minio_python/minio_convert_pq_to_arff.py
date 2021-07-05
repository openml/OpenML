import sys
import os
import pandas as pd
from minio import Minio
from minio.error import S3Error
from dotenv import load_dotenv
import arff
load_dotenv()





def attributes_arff_from_df(df):
    """ Describe attributes of the dataframe according to ARFF specification.
    Parameters
    ----------
    df : DataFrame, shape (n_samples, n_features)
        The dataframe containing the data set.
    Returns
    -------
    attributes_arff : str
        The data set attributes as required by the ARFF format.
    """
    PD_DTYPES_TO_ARFF_DTYPE = {"integer": "INTEGER", "floating": "REAL", "string": "STRING"}
    attributes_arff = []

    if not all([isinstance(column_name, str) for column_name in df.columns]):
        logger.warning("Converting non-str column names to str.")
        df.columns = [str(column_name) for column_name in df.columns]

    for column_name in df:
        # skipna=True does not infer properly the dtype. The NA values are
        # dropped before the inference instead.
        column_dtype = pd.api.types.infer_dtype(df[column_name].dropna(), skipna=False)

        if column_dtype == "categorical":
            # for categorical feature, arff expects a list string. However, a
            # categorical column can contain mixed type and should therefore
            # raise an error asking to convert all entries to string.
            categories = df[column_name].cat.categories
            categories_dtype = pd.api.types.infer_dtype(categories)
            if categories_dtype not in ("string", "unicode"):
                raise ValueError(
                    "The column '{}' of the dataframe is of "
                    "'category' dtype. Therefore, all values in "
                    "this columns should be string. Please "
                    "convert the entries which are not string. "
                    "Got {} dtype in this column.".format(column_name, categories_dtype)
                )
            attributes_arff.append((column_name, categories.tolist()))
        elif column_dtype == "boolean":
            # boolean are encoded as categorical.
            attributes_arff.append((column_name, ["True", "False"]))
        elif column_dtype in PD_DTYPES_TO_ARFF_DTYPE.keys():
            attributes_arff.append((column_name, PD_DTYPES_TO_ARFF_DTYPE[column_dtype]))
        else:
            raise ValueError(
                "The dtype '{}' of the column '{}' is not "
                "currently supported by liac-arff. Supported "
                "dtypes are categorical, string, integer, "
                "floating, and boolean.".format(column_dtype, column_name)
            )
    return attributes_arff

def pq_to_arff(file_path: str):
    df = pd.read_parquet(file_path)
    attributes = attributes_arff_from_df(df)
    arff_dic = {
        'attributes': attributes,
        'data': df.values,
        'relation': 'dataset',
        'description': ''
    }
    with open(f"dataset.arff", "w", encoding="utf8") as f:
        arff.dump(arff_dic, f)
    return f



