echo "Building java backend..."

mvn clean compile assembly:single
targetFile="data_exploration-1.0-SNAPSHOT-jar-with-dependencies.jar"
cp target/$targetFile build

echo "building javascript frontend"
cd src/frontend/gui
yarn build
cp -r build ../../../build/frontend

echo "packaging the application and copying to the web application download folder"
cd ../../../build
zip aideplus.zip Dockerfile $targetFile frontend docker_run.sh run.sh

cp aideplus.zip ../src/webplatform/bin

echo "Done !"

