package com.cs474;

import java.io.IOException;
import java.util.Map;

public class BuilderFactory implements DesignPatternFactory {
    @Override
    public void createPattern(Map nameReplacements, String path) throws IOException {
        director.setBuilder(new BuilderPatternBuilder());
        director.getBuilder().nameReplacements = nameReplacements;
        director.getBuilder().path = path;
        CodeGenerator.logger.debug("Check to see if the correct map was passed over {}", nameReplacements);
        director.constructPattern();
    }
}
