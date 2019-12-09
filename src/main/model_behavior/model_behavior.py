import umap
from flask import Flask, jsonify

app = Flask(__name__)



def compute_boundary(file_path):

    import pandas as pd
    import numpy as np
    import os

    if not os.path.isfile(file_path):
        file_path=os.path.join('../../../', file_path)

    print(file_path)
    df = pd.read_csv(file_path, index_col=0)

    print(len(df))
    print('computing projections')
    embedding = compute_umap(df)

    labels = df[df.columns[-1]].astype('int').values
    embedding_with_labels = np.hstack((embedding, labels.reshape(-1, 1)))

    print(embedding.shape)

    os.remove(file_path)
    return embedding_with_labels, df


def compute_umap(data):

    embedding = umap.UMAP().fit_transform(data)
    return embedding


@app.route('/umap')
def umap_route():

    embedding, df = compute_boundary('labeled_points_java.csv')

    print('done')
    return jsonify(embedding=embedding.tolist())


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
