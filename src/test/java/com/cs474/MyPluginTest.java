package com.cs474;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/*
Wasnt sure how to do tests for GUI elements, so just wrote the skeleton for those
Tested general depacog functionality for other tests
 */
public class MyPluginTest {
    CodeGenerator depacog;
    @Before
    public void setUp() throws Exception {
        depacog = new CodeGenerator();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void numberOfFilesTest() throws IOException, ParseException {
        //check to see if builder pattern results in 3 files being created
        depacog.setPatternReq("builder");
        depacog.generatePattern();
        File file = new File(System.getProperty("user.dir") + "/src/com/cs474/generatedpatterns");
        int numFiles = file.listFiles().length;
        assertEquals(3, numFiles);

    }

    @Test
    public void correctFileNameTest() {
        //check to see if Mediator.java is successfully created
        depacog.setPatternReq("mediator");
        File file = new File(System.getProperty("user.dir") + "/src/com/cs474/generatedpatterns");
        File[] files = file.listFiles();
        ArrayList<String> filenames = new ArrayList<String>();
        for (File f : files){
            filenames.add(f.getName());
        }
        assertTrue(filenames.contains("Mediator.java"));
    }

    @Test
    public void patternSetProperly() {
        //check to see if the button generate action successfully set the pattern
        //not sure how to add the button click here
        assertEquals(depacog.getPatternReq(), "visitor" );
    }

    @Test
    public void noPatternSelected() {
        //if the generate button was clicked with no pattern selected, then nothing should be made
        //not sure how to incorporate GUI stuff
        File file = new File(System.getProperty("user.dir") + "/src/com/cs474/generatedpatterns");
        int numFiles = file.listFiles().length;
        assertEquals(0, numFiles);
    }

    @Test
    public void multiplePatternsCreated() throws IOException, ParseException {
        //if multiple patterns are created one after another, they should all be in the folder
        depacog.setPatternReq("abstract factory");
        depacog.generatePattern();
        depacog.setPatternReq("template method");
        File file = new File(System.getProperty("user.dir") + "/src/com/cs474/generatedpatterns");
        int numFiles = file.listFiles().length;
        assertEquals(6, numFiles);
    }
}