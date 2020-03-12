package com.cs474;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AbstractFactoryBuilder extends DesignPatternBuilder {
    @Override
    public void buildPattern() throws IOException {
        CodeGenerator.logger.trace("Successfully created a builder object. Now in buildPattern AFB");

        //JSON input
        String abstractFactory = (String) nameReplacements.get("abstractFactory");
        String concreteFactory = (String) nameReplacements.get("concreteFactory");
        JSONArray prodInterfaces = (JSONArray) nameReplacements.get("productInterfaces");
        JSONArray pClasses = (JSONArray) nameReplacements.get("concProducts");
        //the last 2 arrays pertain to the different interfaces for products
        //as well as the different concrete implementations for each object

        //abstract factory will contain createProduct methods for each interface that was inputted
        TypeSpec.Builder absFactory = TypeSpec.classBuilder(abstractFactory)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT); //name is a variable
        for (int i = 0; i < prodInterfaces.size(); i++) {
            absFactory.addMethod(MethodSpec.methodBuilder("create" + (String) prodInterfaces.get(i)) //need for each different abstract product
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(ClassName.bestGuess((String)prodInterfaces.get(i)))
                    .build());
        }

        //concrete factory will have empty function bodies of the above
        TypeSpec.Builder concFactory = TypeSpec.classBuilder(concreteFactory)
                .addModifiers(Modifier.PUBLIC) //name is a variable
                .superclass(ClassName.bestGuess(abstractFactory));

        //create interfaces for each product listed
        ArrayList<TypeSpec> productInterfaces = new ArrayList<TypeSpec>();
        for (int i = 0; i < prodInterfaces.size(); i++) {
            productInterfaces.add(TypeSpec.interfaceBuilder((String)prodInterfaces.get(i)).build());
        }

        //create methods for each of the concrete products that were given
        ArrayList<TypeSpec> prodClasses = new ArrayList<TypeSpec>();
        for (int i = 0; i < pClasses.size(); i++) {
            JSONObject jo = (JSONObject) pClasses.get(i);
            prodClasses.add(TypeSpec.classBuilder((String) jo.get("class"))
                    .addSuperinterface(ClassName.bestGuess((String) jo.get("superinterface")))
                    .addModifiers(Modifier.PUBLIC)
                    .build());
            concFactory.addMethod(MethodSpec.methodBuilder("create" + (String) jo.get("superinterface")) //need for each different abstract product
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.bestGuess((String) jo.get("superinterface")))
                    .addAnnotation(Override.class)
                    .addStatement("return new $L()", (String) jo.get("class")).build());
        }

        //creating a new file in src/com/cs474/generatedpatterns in the users project
        //this is where the code will be stored
        File file = new File(path);
        ArrayList<TypeSpec> allfiles = new ArrayList<TypeSpec>();
        allfiles.addAll(prodClasses);
        allfiles.addAll(productInterfaces);
        allfiles.add(absFactory.build());
        allfiles.add(concFactory.build());
        for (int i = 0; i < allfiles.size(); i++) {
            JavaFile javaFile = JavaFile.builder("com.cs474.generatedpatterns", allfiles.get(i)).build();
            javaFile.writeTo(file);
        }
        CodeGenerator.logger.debug("Code should be written to files now from {}", AbstractFactoryBuilder.class.getSimpleName());
    }
}
