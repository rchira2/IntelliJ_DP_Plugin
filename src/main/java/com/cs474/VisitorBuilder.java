package com.cs474;

import com.squareup.javapoet.*;
import org.json.simple.JSONArray;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VisitorBuilder extends DesignPatternBuilder {
    @Override
    public void buildPattern() throws IOException {
        CodeGenerator.logger.trace("Successfully created a builder object. Now in buildPattern of Visitor");
        //JSON input
        String abstractElement = (String) nameReplacements.get("abstractElement");
        String abstractVisitor = (String) nameReplacements.get("abstractVisitor");
        String concreteVisitor = (String) nameReplacements.get("concreteVisitor");
        JSONArray elements = (JSONArray) nameReplacements.get("concreteElements");

        //these are creations for the abstract methods visit and accept
        MethodSpec concvisit = MethodSpec.methodBuilder("visit")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess("ConcreteElement"), "e")
                .addAnnotation(Override.class)
                .build();

        MethodSpec absaccept = MethodSpec.methodBuilder("accept")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(ClassName.bestGuess(abstractVisitor), abstractVisitor.toLowerCase())
                .build(); //for loop

        //making the abstract element class with an abstract accept method
        TypeSpec abselement = TypeSpec.classBuilder(abstractElement)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addMethod(absaccept)
                .build();

        //function definiton for accept method
        MethodSpec accept = MethodSpec.methodBuilder("accept")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(abstractVisitor), abstractVisitor.toLowerCase())
                .addAnnotation(Override.class)
                .addStatement("$L.$N(this)", abstractVisitor.toLowerCase(), concvisit)
                .build();

        //creating the abstract visitor. The abstract visitor will have visit functions for each concrete Element
        //specified by the user
        TypeSpec.Builder absvisitor = TypeSpec.classBuilder(abstractVisitor)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        for (int i = 0; i < elements.size() ; i++) {
            absvisitor.addMethod(MethodSpec.methodBuilder("visit")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(ClassName.bestGuess((String)elements.get(i)),((String) elements.get(i)).toLowerCase())
                    .build());
        }
        absvisitor.build();

        //creation of the concrete visitor which will have empty function definitions for each of the visit operations for each Element
        TypeSpec.Builder concVisitor = TypeSpec.classBuilder(concreteVisitor)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ClassName.bestGuess(abstractVisitor));
        for (int i = 0; i < elements.size() ; i++) {
            concVisitor.addMethod(MethodSpec.methodBuilder("visit")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.bestGuess((String)elements.get(i)),((String) elements.get(i)).toLowerCase())
                    .addAnnotation(Override.class)
                    .build());
        }

        concVisitor.build();
        ArrayList<TypeSpec> concreteElements = new ArrayList<TypeSpec>();

        //for loop which will add classes for each of the concrete elements that the user specified
        for (int i = 0; i < elements.size() ; i++) {
            concreteElements.add(TypeSpec.classBuilder((String) elements.get(i)).addModifiers(Modifier.PUBLIC)
                    .superclass(ClassName.bestGuess(abstractElement))
                    .addMethod(accept)
                    .build());
        }

        //creating a new file in src/com/cs474/generatedpatterns
        //this is where the code will be stored
        File file = new File(path);
        ArrayList<TypeSpec> allfiles = new ArrayList<TypeSpec>();
        allfiles.addAll(concreteElements);
        allfiles.add(abselement);
        allfiles.add(absvisitor.build());
        allfiles.add(concVisitor.build());
        for (int i = 0; i < allfiles.size(); i++) {
            JavaFile javaFile = JavaFile.builder("com.cs474.generatedpatterns", allfiles.get(i)).build();
            javaFile.writeTo(file);
        }
        CodeGenerator.logger.debug("Code should be written to files now from {}", VisitorBuilder.class.getSimpleName());
    }
}
