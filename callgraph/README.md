# Callgraph Analysis

## Building

    $ ant clean compile jar

## Running

    $ java -jar build/jar/callgraph.jar folder_containing_classes

For example, to run the analysis on Guava, first unpack the jar:

    $ jar xf guava-16.0.1.jar
    $ java -jar build/jar/callgraph.jar com
