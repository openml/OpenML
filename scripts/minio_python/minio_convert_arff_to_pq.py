from minio import Minio
from minio.error import S3Error
import sys
import pandas as pd
import arff
import os
from typing import Optional
from dotenv import load_dotenv
import urllib
load_dotenv()


def arff_to_pandas(
    url: str, file_path=None, encoding: Optional[str] = None, **kwargs
) -> pd.DataFrame:
    """ Load data from the ARFF file into a pd.DataFrame.
    Parameters
    ----------
    file_path: url
        url of the minio ARFF file
    encoding: str, optional
        Encoding of the ARFF file.
    **kwargs:
        Any arugments for arff.load.
    Returns
    -------
    pandas.DataFrame
        A dataframe of the data in the ARFF file,
        with categorical columns having category dtype.
    """
   # for row in arff.loads(file_path):
    #    print("first row of ARFF file ", row)
     #   break

    # with open(file_path, "r", encoding=encoding) as arff_file:
    #     arff_dict = arff.load(arff_file, **kwargs)

    if file_path and url:
        raise ValueError(
            "Provide either URL or file path of the ARFF file, not both"
        )
    if file_path:
        with open(file_path, "r", encoding=encoding) as arff_file:
            arff_dict = arff.load(arff_file, **kwargs)
    else:
        ftpstream = urllib.request.urlopen(url)
        arff_dict = arff.load(io.StringIO(ftpstream.read().decode('utf-8')), **kwargs)

    # print(type(arff_dict))
    attribute_names, data_types = zip(*arff_dict["attributes"])
    data = pd.DataFrame(arff_dict["data"], columns=attribute_names)
    for attribute_name, dtype in arff_dict["attributes"]:
        # 'real' and 'numeric' are probably interpreted correctly.
        # Date support needs to be added.
        if isinstance(dtype, list):
            data[attribute_name] = data[attribute_name].astype("category")
    return data