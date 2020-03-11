package com.cs474;

import com.squareup.javapoet.*;
import org.json.simple.JSONArray;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FacadeBuilder extends DesignPatternBuilder {
    public void buildPattern() throws IOException {
        CodeGenerator.logger.trace("Successfully created a builder object. Now in buildPattern Facade");
        //JSON input we read in
        String facadeName = (String) nameReplacements.get("facadeName");
        String facadeOperation = (String) nameReplacements.get("facadeOperation");
        JSONArray subsystems = (JSONArray) nameReplacements.get("subsystemClasses");

        //constructing the start function that encapsulates the subsystem method calls in the facade pattern
        MethodSpec start = MethodSpec.methodBuilder(facadeOperation)
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addComment("Call subsystem methods as needed")
                .build();

        //The actual facade which will have fields that each represent each of the subsystem classes in use within the facade method
        TypeSpec.Builder facade = TypeSpec.classBuilder(facadeName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(start);
        for (int i = 0; i < subsystems.size(); i++) {
            facade.addField(FieldSpec.builder(ClassName.bestGuess((String)subsystems.get(i)), (String)((String) subsystems.get(i)).toLowerCase())
                    .addModifiers(Modifier.PRIVATE)
                    .initializer("new $L()", (String)subsystems.get(i))
                    .build());
        }
        facade.build();

        //creates each of the classes that will be used in the facade. Just empty classes are provided
        ArrayList<TypeSpec> subsystemclasses = new ArrayList<TypeSpec>();
        for (int i = 0; i < subsystems.size(); i++) {
            subsystemclasses.add(TypeSpec.classBuilder((String)subsystems.get(i))
                    .addModifiers(Modifier.PUBLIC)
                    .build());
        }

        //creating a new file in src/com/cs474/generatedpatterns
        //this is where the code will be stored

        File file = new File(path);
        ArrayList<TypeSpec> allfiles = new ArrayList<TypeSpec>();
        allfiles.addAll(subsystemclasses);
        allfiles.add(facade.build());
        for (int i = 0; i < allfiles.size(); i++) {
            JavaFile javaFile = JavaFile.builder("com.cs474.generatedpatterns", allfiles.get(i)).build();
            javaFile.writeTo(file);
        }
        System.out.println("Facade builder - file created");

        CodeGenerator.logger.debug("Code should be written to files now from {}", FacadeBuilder.class.getSimpleName());
    }
}
