import umap
from flask import Flask, jsonify

app = Flask(__name__)



def compute_boundary(file_path):

    import pandas as pd
    import numpy as np
    import os

    if not os.path.isfile(file_path):
        file_path=os.path.join('../../../', file_path)
    df = pd.read_csv(file_path, index_col=0)
    print(df.head(5))
    print(len(df))
    print('computing projections')
    embedding = compute_umap(df)
    print('done')
    labels = df[df.columns[-1]].astype('int').values

    return np.hstack((embedding, labels.reshape(-1, 1))), df



def compute_umap(data):

    embedding = umap.UMAP().fit_transform(data)
    return embedding


@app.route('/umap')
def umap_route():

    embedding, df = compute_boundary('labeled_points_java.csv')

    return jsonify(embedding=embedding.tolist())


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
