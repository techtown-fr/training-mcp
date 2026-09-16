#!/bin/sh
# Wrapper so Inspector 2 can spawn the JAR without treating -jar as a CLI flag.
exec java -jar "$(cd "$(dirname "$0")" && pwd)/target/geocalc-mcp-server.jar"
