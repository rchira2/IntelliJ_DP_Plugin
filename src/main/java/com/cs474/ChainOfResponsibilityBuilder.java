package com.cs474;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ChainOfResponsibilityBuilder extends DesignPatternBuilder {

    @Override
    public void buildPattern() throws IOException {
        CodeGenerator.logger.trace("Successfully created a builder object. Now in buildPattern COR");
        //All the JSON input we read in from our map object
        String abstractHandler = (String) nameReplacements.get("abstractHandlerName");
        String concreteHandler = (String) nameReplacements.get("concreteHandlerName");
        String nexthandler = (String) nameReplacements.get("nextHandler");
        String canHandleRequest = (String) nameReplacements.get("canHandleRequest");
        String handleRequest = (String) nameReplacements.get("handleRequest");

        //creating the abstract handler class
        TypeSpec abshandler = TypeSpec.classBuilder(abstractHandler).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //name is a variable
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC).build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.bestGuess(abstractHandler), nexthandler)
                        .addStatement("this.$L = $L", nexthandler, nexthandler)
                        .build())
                .addMethod(MethodSpec.methodBuilder(canHandleRequest)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(TypeName.BOOLEAN).build())
                .addField(FieldSpec.builder(ClassName.bestGuess(abstractHandler), nexthandler)
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .addMethod(MethodSpec.methodBuilder(handleRequest)
                        .addModifiers(Modifier.PUBLIC)
                        .beginControlFlow("if($L != null)", nexthandler)
                        .addStatement("$L.$L()", nexthandler, handleRequest)
                        .endControlFlow()
                        .build())
                .build();

        //creating the concrete handler class which will have 2 methods. One to see if it can handle the request and another to
        //actually handle the request
        TypeSpec concHandler = TypeSpec.classBuilder(concreteHandler).superclass(ClassName.bestGuess(abstractHandler))
                .addMethod(MethodSpec.methodBuilder(canHandleRequest)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.BOOLEAN)
                        .addAnnotation(Override.class)
                        .addStatement("return false")
                        .build())
                .addMethod(MethodSpec.methodBuilder(handleRequest)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .beginControlFlow("if($L())", canHandleRequest)
                        .nextControlFlow("else")
                        .addStatement("super.$L()", handleRequest)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.bestGuess(abstractHandler), nexthandler)
                        .addStatement("super($L)", nexthandler)
                        .build())
                .build();


        //creating a new file in src/com/cs474/generatedpatterns
        //this is where the code will be stored
        File file = new File(path);
        ArrayList<TypeSpec> allfiles = new ArrayList<TypeSpec>();
        allfiles.add(abshandler);
        allfiles.add(concHandler);
        for (int i = 0; i < allfiles.size(); i++) {
            JavaFile javaFile = JavaFile.builder("com.cs474.generatedpatterns", allfiles.get(i)).build();
            javaFile.writeTo(file);
        }

        CodeGenerator.logger.debug("Code should be written to files now from {}", ChainOfResponsibilityBuilder.class.getSimpleName());
    }
}
