#!/bin/bash
buildDir="build/libs"
pluginsDir=$1
for file in $pluginsDir/Discord-Link*; do
	rm -r $file
done
for file in $buildDir/Discord-Link*; do
	cp $file $pluginsDir
done
