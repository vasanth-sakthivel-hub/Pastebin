#!/bin/bash
# Build script for deployment platforms

# Install Maven if not present
if ! command -v mvn &> /dev/null; then
    echo "Maven not found, installing..."
    apt-get update && apt-get install -y maven
fi

# Build the application
mvn clean package -DskipTests

echo "Build complete!"
