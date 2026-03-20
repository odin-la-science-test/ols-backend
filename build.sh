#!/bin/bash
set -e

echo "==> Configuring Java environment..."
# Find Java installation
if [ -z "$JAVA_HOME" ]; then
    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    echo "JAVA_HOME set to: $JAVA_HOME"
fi

echo "Java version:"
java -version

echo "==> Installing Maven..."
# Download and install Maven
MAVEN_VERSION=3.9.6
wget -q https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz
export M2_HOME=$PWD/apache-maven-${MAVEN_VERSION}
export PATH=$M2_HOME/bin:$PATH

echo "Maven version:"
mvn -version

echo "==> Building application..."
mvn clean package -DskipTests

echo "==> Build complete!"
ls -lh target/*.jar
