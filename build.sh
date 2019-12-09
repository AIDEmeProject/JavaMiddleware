echo "Building java backend..."

echo "building backend"
mvn clean compile assembly:single
targetFile="data_exploration-1.0-SNAPSHOT-jar-with-dependencies.jar"
cp target/$targetFile build

cp -r src/main/model_behavior/ build 

echo "building javascript frontend"
cd src/frontend/gui
yarn build
mkdir ../../../build/static
cp -r build/* ../../../build/static

echo "packaging the application and copying to the web application download folder"
cd ../../../build
zip -r aideplus.zip model_behavior Dockerfile $targetFile frontend launch_all.sh docker_run.sh run.sh static cars_encoded.csv

cp aideplus.zip ../src/webplatform/bin

echo "Done !"

