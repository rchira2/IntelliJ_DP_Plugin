package com.cs474;

import java.io.IOException;
import java.util.Map;

public class MediatorFactory implements DesignPatternFactory {

    @Override
    public void createPattern(Map nameReplacements, String path) throws IOException {
        director.setBuilder(new MediatorBuilder());
        director.getBuilder().nameReplacements = nameReplacements;
        director.getBuilder().path = path;
        director.constructPattern();
    }
}
