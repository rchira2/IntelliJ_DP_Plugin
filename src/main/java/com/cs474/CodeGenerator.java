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
            case "visitor":
                return new VisitorFactory();
            case "factory method":
                return new FactoryMethodFactory();
            case "builder":
                return new BuilderFactory();
            case "template method":
                return new TemplateMethodFactory();
            case "chain of responsibility":
                return new ChainOfResponsibilityFactory();
            case "mediator":
                return new MediatorFactory();
            case "abstract factory":
                return new AbstractFactoryPatternFactory();
            default:
                logger.error("Something is wrong with JSON file. Input was: {}", pattern);
                throw new IllegalArgumentException("Not a valid pattern");
        }
    }

    @Override
    protected void readInput() throws IOException, ParseException {
        //creates the JSON object and gets the pattern the user desires
        String filename;
        if(project == null){
            filename = "input.json";
        }
        else {
            filename = project.getBasePath() + "/input.json";
        }
        this.jo = (JSONObject) new JSONParser().parse(new FileReader(filename));
        //this.patternReq = ((String) jo.get("pattern")).toLowerCase();
        System.out.println("Requested pattern: " + patternReq);
        CodeGenerator.logger.trace("This is the pattern they requested {}", patternReq);
    }

    @Override
    protected void createFiles() throws IOException {
        //use the pattern string to get a factory and get the object for that pattern. Transform into map and create the pattern
        this.factory = CodeGenerator.getFactory(this.patternReq);
        Map pattern = (Map)this.jo.get(this.patternReq);
        CodeGenerator.logger.info("Do we have the right JSON map? {}", pattern);
        String path;
        if(project == null){
            path = "/src";
        }
        else{
            path = project.getBasePath() + "/src";
        }
        this.factory.createPattern(pattern, path);
        System.out.println("A pattern was created");
    }

    @Override
    protected void cleanUP() throws IOException {
        //decide to get rid of this function
        //this allows the user to chain build design patterns without fear of losing their work
        //also, the pattern doesn't quite write to the source folder of the user, but when it does,
        //this prevents their work from being deleted.
        //this method is still here to satisfy template method
    }

    public void setProject(Project project) {
        this.project = project;
    }
    public void setPatternReq(String patternReq){
        this.patternReq = patternReq;
    }
    public Project getProject(){
        return project;
    }

    public String getPatternReq(){
        return patternReq;
    }
}
