#!/bin/bash
buildDir="build/libs"
pluginsDir="/D/Thermos-Infinity-1.7.10/plugins"
for file in $pluginsDir/Discord-Link*; do
	rm -r $file
done
for file in $buildDir/Discord-Link*; do
	cp $file $pluginsDir
done
