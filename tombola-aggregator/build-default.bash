#!/usr/bin/bash

clear

STAGE="default"

# mvn -P"$STAGE" -B -X -e -Dmaven.test.skip=true -DskipTests clean install -Dstyle.color=always
mvn -P"$STAGE" -B -Dmaven.test.skip=true -DskipTests clean install -Dstyle.color=always
cp -f ../tombola-app/target/tombola.war tombola.war
mvn -B -Dmaven.test.skip=true -DskipTests clean -Dstyle.color=always
