package com.cs474;

import com.squareup.javapoet.*;
import org.json.simple.JSONArray;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TemplateMethodBuilder extends DesignPatternBuilder {
    @Override
    public void buildPattern() throws IOException {
        CodeGenerator.logger.trace("Successfully created a builder object. Now in buildPattern for TM");
        //JSON input
        String templateMethodOp = (String) nameReplacements.get("templateOperation");
        String abstractTMName = (String) nameReplacements.get("abstractTMName");
        String concreteTMName = (String) nameReplacements.get("concreteTMName");
        JSONArray operations = (JSONArray) nameReplacements.get("primitiveOperations");

        //builds the template method which will contain operations specified by the user
        MethodSpec.Builder templateOp = MethodSpec.methodBuilder(templateMethodOp)
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        for (int i = 0; i < operations.size(); i++) {
            templateOp.addStatement("$L()", (String)operations.get(i));
        }
        templateOp.build();

        //abstract template which will have method stubs for each primitive operation and factory method
        TypeSpec.Builder absTemplate = TypeSpec.classBuilder(abstractTMName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        for (int i = 0; i < operations.size(); i++) {
            absTemplate.addMethod(MethodSpec.methodBuilder((String)operations.get(i))
                    .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                    .build());
        }
        absTemplate.addMethod(templateOp.build()).build();

        //concrete template will have methods waiting to be overwritten
        TypeSpec.Builder concTemplate = TypeSpec.classBuilder(concreteTMName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ClassName.bestGuess(abstractTMName));
        for (int i = 0; i < operations.size(); i++) {
            concTemplate.addMethod(MethodSpec.methodBuilder((String)operations.get(i))
                    .addAnnotation(Override.class)
                    .addComment("Implement each method")
                    .addModifiers(Modifier.PROTECTED)
                    .build());
        }
        concTemplate.build();

        //creating a new file in src/com/cs474/generatedpatterns
        //this is where the code will be stored
        File file = new File(path);
        ArrayList<TypeSpec> allfiles = new ArrayList<TypeSpec>();
        allfiles.add(concTemplate.build());
        allfiles.add(absTemplate.build());
        for (int i = 0; i < allfiles.size(); i++) {
            JavaFile javaFile = JavaFile.builder("com.cs474.generatedpatterns", allfiles.get(i)).build();
            javaFile.writeTo(file);
        }

        CodeGenerator.logger.debug("Code should be written to files now from {}", TemplateMethodBuilder.class.getSimpleName());
    }
}
