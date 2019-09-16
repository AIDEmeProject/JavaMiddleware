import json
from flask import Flask, request, jsonify


app = Flask(__name__)

import pandas as pd

import pandas_profiling

def generate_pandas_profiling(data_path):

    df = pd.read_csv(data_path)

    profile = pandas_profiling.ProfileReport(df)

    return profile.html

@app.route('/pandas-report', methods=['GET'])
def prediction():

    path = "../java/application/data/example2.csv"
    html = generate_pandas_profiling(path)

    return html


if __name__ == "__main__":

    app.run(debug=True)

