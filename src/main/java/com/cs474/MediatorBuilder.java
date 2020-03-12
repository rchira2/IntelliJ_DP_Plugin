package com.cs474;

import com.squareup.javapoet.*;
import org.json.simple.JSONArray;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MediatorBuilder extends DesignPatternBuilder {
    @Override
    public void buildPattern() throws IOException {
        CodeGenerator.logger.trace("Successfully created a builder object. Now in buildPattern Mediator");
        //JSON input
        String abstractMediator = (String) nameReplacements.get("abstractMediator");
        String concreteMediator = (String) nameReplacements.get("concreteMediator");
        String abstractColleague = (String) nameReplacements.get("abstractColleague");
        String mediateFunction = (String) nameReplacements.get("mediateFunction");
        JSONArray colleagues = (JSONArray) nameReplacements.get("concreteColleagues");

        //construction of abstract mediator which will contain the mediate function
        TypeSpec absMediator = TypeSpec.classBuilder(abstractMediator)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //name is a variable
                .addMethod(MethodSpec.methodBuilder(mediateFunction)
                        .addParameter(ClassName.bestGuess(abstractColleague), abstractColleague.toLowerCase())
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build())
                .build();

        //construction of concrete mediator
        TypeSpec.Builder concMediator = TypeSpec.classBuilder(concreteMediator)
                .addModifiers(Modifier.PUBLIC) //name is a variable
                .addMethod(MethodSpec.methodBuilder(mediateFunction)
                        .addParameter(ClassName.bestGuess(abstractColleague), abstractColleague.toLowerCase())
                        .addModifiers(Modifier.PUBLIC) //for loop needed
                        .addComment("Change functionality based on who sender is")
                        .addAnnotation(Override.class).build())
                .superclass(ClassName.bestGuess(abstractMediator));
        for (int i = 0; i < colleagues.size(); i++) {
            concMediator.addField(FieldSpec.builder(ClassName.bestGuess((String)colleagues.get(i)), (String)((String) colleagues.get(i)).toLowerCase())
                    .addModifiers(Modifier.PRIVATE)
                    .build());
        }

        //abstract class defines what kind of colleagues will be used in the pattern
        TypeSpec absColleague = TypeSpec.classBuilder(abstractColleague)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //name is a variable
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(ClassName.bestGuess(abstractMediator), abstractMediator.toLowerCase())
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this.$L = $L", abstractMediator.toLowerCase(), abstractMediator.toLowerCase())
                        .build())
                .addField(FieldSpec.builder(ClassName.bestGuess(abstractMediator), abstractMediator.toLowerCase())
                        .addModifiers(Modifier.PROTECTED)
                        .build())
                .build();

        ArrayList<TypeSpec> concColleagues = new ArrayList<TypeSpec>();

        //This for loop will construct each of the "colleagues" that will be used in the mediator pattern
        for (int i = 0; i < colleagues.size(); i++){
            concColleagues.add(TypeSpec.classBuilder((String)colleagues.get(i))
                    .addModifiers(Modifier.PUBLIC) //name is a variable
                    .addMethod(MethodSpec.constructorBuilder()
                            .addParameter(ClassName.bestGuess(abstractMediator), abstractMediator.toLowerCase())
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("super($L)", abstractMediator.toLowerCase())
                            .build())
                    .addMethod(MethodSpec.methodBuilder("action")
                            .addComment("Add some functionality that results in the following being called")
                            .addStatement("$L.$L(this)", abstractMediator.toLowerCase(), mediateFunction)
                            .addModifiers(Modifier.PUBLIC).build())
                    .superclass(ClassName.bestGuess(abstractColleague)).build());
        }

        //creating a new file in src/com/cs474/generatedpatterns
        //this is where the code will be stored
        File file = new File(path);
        ArrayList<TypeSpec> allfiles = new ArrayList<TypeSpec>();
        allfiles.addAll(concColleagues);
        allfiles.add(absMediator);
        allfiles.add(concMediator.build());
        allfiles.add(absColleague);
        for (int i = 0; i < allfiles.size(); i++) {
            JavaFile javaFile = JavaFile.builder("com.cs474.generatedpatterns", allfiles.get(i)).build();
            javaFile.writeTo(file);
        }

        CodeGenerator.logger.debug("Code should be written to files now from {}", MediatorBuilder.class.getSimpleName());
    }
}
