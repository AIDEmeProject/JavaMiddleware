# Installation 

Currently, to use the software the following dependancies are needed : 

- nodejs
- java


Installing the frontend dependencies

```bash

cd src/frontend/gui
npm install 

```

Installing webplatform dependancies (django)

cd src/webplatform
pip install -r requirements.txt


# usage for devlopment 

1. launch the java webserver exposing the backend

```bash
mvn exec:java -Dexec.mainClass="application.ApplicationServerMain"
``

2. Launch the webplatform (django)

```bash
cd src/webplatform
python3 manage.py runserver

3. Launch the frontend

```
cd src/frontend/gui
npm start

```
