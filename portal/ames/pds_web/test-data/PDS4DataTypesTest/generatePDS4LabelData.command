#!/bin/bash

#current work directory
CWD="$(cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "Current working directory: $CWD"

while getopts u:d:j:f:w: option 
do case "${option}" in 

u) PDS3LABELURL=${OPTARG}


	if grep -Fxq "$PDS3LABELURL" $CWD/listOfLabels.txt
	then
    	echo "label already exists. Regenerating PDS4 label..."

	else
    	echo "label not found"

		#save to the list of labels
		echo "$PDS3LABELURL" >> $CWD/listOfLabels.txt
		#downoload label
		cd "$CWD/labels/" && { curl -O $PDS3LABELURL ; cd -; }

	fi

	PDS3FILENAME=${PDS3LABELURL##*/}
	echo "Converting PDS3 to PDS4 label..."
	$CWD/transform $CWD/labels/$PDS3FILENAME -f pds4-label -o "$CWD/labels"
	echo "Finish converting PDS3 to PDS4 label"

;; 

d) PDS3DATAURL=${OPTARG}

	#download and save the data file
	cd "$CWD/labels/" && { curl -O $PDS3DATAURL ; cd -; }




;; 






w) BINARYFILENAME=${OPTARG}

	echo "compiling java file..."
	javac $CWD/binaryData/ReadWritePDS4Binary.java
	echo "done compiling java file..."
	echo "running program..."
	java -cp $CWD/binaryData ReadWritePDS4Binary "$CWD/binaryData/data" $BINARYFILENAME
	echo "finish running program..."
	echo

	#copy into PDS4 labels directory
	cp -r $CWD/binaryData/data/* $CWD/PDS4LABELS/
;; 


f) FORMAT=$OPTARG




echo "Converting PDS3 to PDS4 label..."
$_cwd/transform $_cwd/labeltest.lbl -f pds4-label -o "$_cwd/output"
echo "Finish converting PDS3 to PDS4 label"
echo 
echo
#read -p "Press any key to continue..."




;; 
esac done 



#file name
#FILENAME=$1






#primrose