#!/usr/bin/env bash

set -e -u

#export TERM=${TERM:-dumb}
#version=$(<latest-version/version)-SNAPSHOT

pushd git-sources
   ./gradlew clean UserService:build
popd

cp git-sources/UserService/build/libs/*.jar splitter-jars/