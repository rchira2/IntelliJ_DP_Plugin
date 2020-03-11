package com.cs474;

import java.io.IOException;

/*
Director class will be instantiated by the respective design pattern factory
The director will have a builder and will construct the design pattern by calling the respective design patterns builder
 */
public class Director {
    private DesignPatternBuilder builder;
    public Director(){}
    public void setBuilder(DesignPatternBuilder builder){
        this.builder = builder;
    }
    public DesignPatternBuilder getBuilder(){
        return builder;
    }
    public void constructPattern() throws IOException {
        builder.buildPattern();
    }
}
