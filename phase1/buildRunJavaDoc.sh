#!/bin/bash

# Check if the out/production/phase2 folder exists and create it if it doesn't
if [ ! -d ./out/production/phase2 ]; then
	mkdir ./out/production/phase2/
fi

#Generate the javaDocs for the project
echo "generating javaDoc"
if [ ! -d ./javaDoc ]; then
	mkdir ./javaDoc
fi
javadoc -d ./javaDoc -cp src/ model viewer

# build the entire project and put it in the out/production/phase1 folder
echo "building project"
javac -d ./out/production/phase2 ./src/*/*.java

# Move the needed resources
if [ ! -e ./out/production/phase2/viewer/viewer.fxml]; then
	echo "moving viewer.fxml"
	cp ./src/viewer/viewer.fxml ./out/production/phase2/viewer/viewer.fxml
fi

if [ ! -e ./out/production/phase2/viewer/NoImage.png]; then
	echo "moving NoImage.png"
	cp ./src/viewer/NoImage.png ./out/production/phase2/viewer/NoImage.png
fi

# run the project
echo "starting project"
java -cp ./out/production/phase1 viewer.Viewer
