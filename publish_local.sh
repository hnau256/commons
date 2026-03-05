#!/bin/bash
set -e

./gradlew publishToMavenLocal -PsignAllPublications=true