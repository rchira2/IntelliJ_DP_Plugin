package com.cs474;

import com.squareup.javapoet.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class FactoryMethodBuilder extends DesignPatternBuilder {
    @Override
    public void buildPattern() throws IOException {
        CodeGenerator.logger.trace("Successfully created a builder object. Now in buildPattern FM");
        //JSON input that we read in
        String productInterfaceName = (String) nameReplacements.get("productInterfaceName");
        String abstractFMName = (String) nameReplacements.get("abstractFMName");
        JSONArray ja = (JSONArray) nameReplacements.get("concreteProducts");
        //This JSON array has each of the products and the concrete factory that it pertains to. We will create both

        //product interface
        TypeSpec product = TypeSpec.interfaceBuilder("Product").build();

        //abstract factory (not the patter) will contain the method (factorymethod) to creating a product
        TypeSpec absFactory = TypeSpec.classBuilder(abstractFMName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addMethod(MethodSpec.methodBuilder("factorymethod")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(ClassName.bestGuess("Product"))
                        .build())
                .addMethod(MethodSpec.methodBuilder("operation")
                        .returns(TypeName.VOID).addModifiers(Modifier.PUBLIC)
                        .addStatement("product = factorymethod()")
                        .build())
                .addField(ClassName.bestGuess(productInterfaceName), productInterfaceName.toLowerCase(), Modifier.PRIVATE)
                .build();

        //This for loop will construct each of the concrete products we intend on creating and each of the concrete
        //factories that we will use in order to construct each specific product
        ArrayList<TypeSpec> concProducts = new ArrayList<TypeSpec>();
        ArrayList<TypeSpec> concFactories = new ArrayList<TypeSpec>();
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo = (JSONObject) ja.get(i);
            concProducts.add(TypeSpec.classBuilder((String)jo.get("class"))
                    .addSuperinterface(ClassName.bestGuess(productInterfaceName))
                    .build());
            concFactories.add(TypeSpec.classBuilder((String)jo.get("concFactory"))
                    .superclass(ClassName.bestGuess(abstractFMName))
                    .addModifiers(Modifier.PUBLIC).addMethod(MethodSpec.methodBuilder("factorymethod")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ClassName.bestGuess(productInterfaceName))
                            .addAnnotation(Override.class)
                            .addStatement("return new $L()", (String)jo.get("class"))
                            .build())
                    .build());
        }

        //creating a new file in src/com/cs474/generatedpatterns
        //this is where the code will be stored
        File file = new File(path);
        ArrayList<TypeSpec> allfiles = new ArrayList<TypeSpec>();
        allfiles.addAll(concFactories);
        allfiles.addAll(concProducts);
        allfiles.add(absFactory);
        allfiles.add(product);
        for (int i = 0; i < allfiles.size(); i++) {
            JavaFile javaFile = JavaFile.builder("com.cs474.generatedpatterns", allfiles.get(i)).build();
            javaFile.writeTo(file);
        }

        CodeGenerator.logger.debug("Code should be written to files now from {}", FactoryMethodBuilder.class.getSimpleName());
    }
}
