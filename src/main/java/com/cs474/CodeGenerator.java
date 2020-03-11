package com.cs474;

import ch.qos.logback.core.util.FileUtil;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.squareup.javapoet.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public class CodeGenerator extends DPTemplateMethod {
    static final Logger logger = LoggerFactory.getLogger("CodeGenerator");
    private String patternReq;
    private JSONObject jo;
    private DesignPatternFactory factory;
    private Project project;
    /*
    This function will return a factory based on the JSON input read in that the user can change
     */
    private static DesignPatternFactory getFactory(String pattern){
        switch(pattern) {
            case "facade":
                return new FacadeFactory();
            //case "visitor":
            //    return new VisitorFactory();
            //case "factory method":
            //    return new FactoryMethodFactory();
            //case "builder":
            //    return new BuilderFactory();
            //case "template method":
            //    return new TemplateMethodFactory();
            //case "chain of responsibility":
            //    return new ChainOfResponsibilityFactory();
            //case "mediator":
            //    return new MediatorFactory();
            //case "abstract factory":
            //    return new AbstractFactoryPatternFactory();
            default:
                logger.error("Something is wrong with JSON file. Input was: {}", pattern);
                throw new IllegalArgumentException("Not a valid pattern");
        }
    }
    public static void main(String[] args) throws IOException, ParseException {
        //create a JSON object that will be parsed
        //JSON file has objects and key value pairs for each pattern
        //user needs to specify the pattern at the top of the JSON file
        //then the user finds the specific object for their pattern and modify the parameters to their liking
        //do not change any of the parameters for patterns you are not using
        CodeGenerator DePaCog = new CodeGenerator();
        DePaCog.generatePattern(); //templateMethod example
    }

    @Override
    protected void readInput() throws IOException, ParseException {
        //creates the JSON object and gets the pattern the user desires
        //System.out.println(project.getBasePath() + "/input.json");
        String filename = project.getBasePath() + "/input.json";
        this.jo = (JSONObject) new JSONParser().parse(new FileReader(filename));
        this.patternReq = ((String) jo.get("pattern")).toLowerCase();
        System.out.println("Requested pattern: " + patternReq);
        CodeGenerator.logger.trace("This is the pattern they requested {}", patternReq);
    }

    @Override
    protected void createFiles() throws IOException {
        //use the pattern string to get a factory and get the object for that pattern. Transform into map and create the pattern
        this.factory = CodeGenerator.getFactory(this.patternReq);
        Map pattern = (Map)this.jo.get(this.patternReq);
        CodeGenerator.logger.info("Do we have the right JSON map? {}", pattern);
        String path = project.getBasePath() + "/src";
        this.factory.createPattern(pattern, path);
        System.out.println("A pattern was created");
    }

    @Override
    protected void cleanUP() throws IOException {
        //this function deletes all files that were in the generatedpatterns folder
        //it is up to the user to save the previous files before generating new patterns, otherwise we may create too many
        //files and slow down the system
        File file = new File(System.getProperty("user.dir") + "/src/com/cs474/generatedpatterns");
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    public void setProject(Project project) {
        this.project = project;
    }
    public Project getProject(){
        return project;
    }
}