package com.cs474;

import com.squareup.javapoet.*;
import org.json.simple.JSONArray;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BuilderPatternBuilder extends DesignPatternBuilder {
    @Override
    public void buildPattern() throws IOException {
        CodeGenerator.logger.trace("Successfully created a builder object. Now in buildPattern Builder");
        //The following are input parameters we receive from the JSON object pertaining to Builder pattern
        String abstractBuilder = (String) nameReplacements.get("abstractBuilder");
        String concreteBuilder = (String) nameReplacements.get("concreteBuilder");
        String directorName = (String) nameReplacements.get("directorName");
        String complexObjectName = (String) nameReplacements.get("complexObjectName");
        JSONArray buildSteps = (JSONArray) nameReplacements.get("buildSteps");


        //building the abstract builder class
        TypeSpec.Builder builder = TypeSpec.classBuilder(abstractBuilder)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addMethod(MethodSpec.methodBuilder("getResult")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(ClassName.bestGuess(complexObjectName))
                        .build());
        for (int i = 0; i < buildSteps.size(); i++) {
            builder.addMethod(MethodSpec.methodBuilder((String) buildSteps.get(i))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .build());
        }
        builder.build();

        //building a construct method which will contain the methods that a builder can call, hence the for loop
        MethodSpec.Builder construct = MethodSpec.methodBuilder("construct")
                .addModifiers(Modifier.PUBLIC);
        for (int i = 0; i < buildSteps.size(); i++) {
            construct.addStatement("$L.$L", abstractBuilder.toLowerCase(), ((String) buildSteps.get(i)));
        }
        construct.build();

        //constructing the director class, which has a constructor and the construct method from above
        TypeSpec director = TypeSpec.classBuilder(directorName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(construct.build())
                .addField(ClassName.bestGuess(abstractBuilder), abstractBuilder.toLowerCase())
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(ClassName.bestGuess(abstractBuilder), abstractBuilder.toLowerCase())
                        .addStatement("this.$L = $L", abstractBuilder.toLowerCase(),abstractBuilder.toLowerCase())
                        .build())
                .build();

        //concrete builder, this is the actual implementation and will have function bodies for each of the build steps the user
        //specified in the JSON file
        TypeSpec.Builder concBuilder = TypeSpec.classBuilder(concreteBuilder)
                .superclass(ClassName.bestGuess(abstractBuilder))
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(ClassName.bestGuess(complexObjectName), complexObjectName.toLowerCase())
                        .initializer("new $L()", complexObjectName).build())
                .addMethod(MethodSpec.methodBuilder("getResult")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.bestGuess(complexObjectName))
                        .addStatement("return $L", complexObjectName.toLowerCase())
                        .addAnnotation(Override.class)
                        .build());

        //adding each of the build steps to the concrete builder class
        for (int i = 0; i < buildSteps.size(); i++) {
            concBuilder.addMethod(MethodSpec.methodBuilder((String)buildSteps.get(i))
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .build());
        }

        //creating a new file in src/com/cs474/generatedpatterns
        //this is where the code will be stored
        File file = new File(path);
        ArrayList<TypeSpec> allfiles = new ArrayList<TypeSpec>();
        allfiles.add(director);
        allfiles.add(builder.build());
        allfiles.add(concBuilder.build());
        for (int i = 0; i < allfiles.size(); i++) {
            JavaFile javaFile = JavaFile.builder("com.cs474.generatedpatterns", allfiles.get(i)).build();
            javaFile.writeTo(file);
        }

        CodeGenerator.logger.debug("Code should be written to files now from {}", BuilderPatternBuilder.class.getSimpleName());
    }
}
