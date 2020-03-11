package com.cs474;

import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;

/*
Usage of the template method in order to create the design patterns
 */
public abstract class DPTemplateMethod {
    protected abstract void readInput() throws IOException, ParseException;
    protected abstract void createFiles() throws IOException;
    protected abstract void cleanUP() throws IOException;
    public final void generatePattern() throws IOException, ParseException {
        cleanUP();
        readInput();
        createFiles();
    }
}
