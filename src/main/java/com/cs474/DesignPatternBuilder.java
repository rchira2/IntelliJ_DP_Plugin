package com.cs474;

import java.io.IOException;
import java.util.Map;

public abstract class DesignPatternBuilder {
    String path;
    Map nameReplacements;      //this Map will contain all the information we need for a particular pattern. Given to us by user via JSON
    public abstract void buildPattern() throws IOException;
}
