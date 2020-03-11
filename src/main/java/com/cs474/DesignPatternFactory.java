package com.cs474;

import java.io.IOException;
import java.util.Map;

/*
Each of our design pattern factories will do the same thing. The createPattern method will take a map object.
This map object is the JSON input for that particular design pattern. The Factory will create a builder for that pattern
and the builder will utilize the map that we pass as a parameter in order to build the pattern.
 */
interface DesignPatternFactory {
    Director director = new Director();
    public void createPattern(Map nameReplacements, String path) throws IOException;
}
