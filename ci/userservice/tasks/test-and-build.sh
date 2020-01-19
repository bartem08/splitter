#!/usr/bin/env bash

set -e -u

#export TERM=${TERM:-dumb}
#version=$(<latest-version/version)-SNAPSHOT

pushd git
   ./gradlew clean UserService:build
popd

cp git/UserService/build/libs/*.jar splitter-jars/