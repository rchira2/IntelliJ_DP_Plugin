Roshan Chirayil - rchira2
#### Running:
gradle build
gradle runIDE

runIDE is used to open up another IDE instance with the plugin running in order to test out the features. Must have an input.json file in the target project.
#### Design:
For my design, I chose to implement my plugin as a dockable Tool Window that is available on the right side of the IDE.

![Imgur](https://i.imgur.com/V85AxbZ.png)

Now, the design isn't the greatest. Since it was my first time using Swing, I had some issues with the alignment of certain components. Because of this, the plugin doesn't have the cleanest design, but that can always be improved upon later.

![Imgur](https://i.imgur.com/oXaJo24.png)

##### Usage:
There is a list of the available design patterns that the user can select from. The user will simply select one of the patterns, and click on the generate button. That's all there is to it.

#### Abstraction:
I wanted to minimize the amount of code rewriting I would have to do in order to make my plugin work with my already existing code generator. This meant that my plugin requires the user to have an input.json file in their project directory. This way, my plugin acts as a simple wrapper to my code generator. The user will select their desired plugin and click on the generate button. Then, my generatePattern() in my Code Generator class is invoke and the design pattern is created.

The usage of the plugin as discussed before is quite simple. Select an item from a list and then click on the generate button. 
```java
public MyPlugin(ToolWindow toolWindow){
    generateButton.addActionListener(e -> {
        depacog.setProject(project);
        if(dpList.getSelectedValue() != null) {
            depacog.setPatternReq(dpList.getSelectedValue().toString().toLowerCase());
            try {
                depacog.generatePattern();
            } catch (IOException | ParseException ex) {
                ex.printStackTrace();
            }
        }
    });
}
```
The above is all the code there is for interacting with the plugin. There is an event listener attached to the generate button. This listener will get the list value, set the pattern in our Code Generator object and finally, generate the pattern. The bit about setting the project is for us to be able to access the directory of the project in which the plugin is invoked. This enables us to read the JSON file and write to a directory within the users project.

As far as creation of the tool window goes, I created a custom class that implements the ToolWindowFactory. The tool window is registered in our plugin.xml file and is created upon the opening of a project. This class has a method called createToolWindowContent().
```java
public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        MyPlugin myPlugin = new MyPlugin(toolWindow);
        myPlugin.setProject(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myPlugin.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
```
In this method, we get the project that was opened and we instantiate our Plugin object. Finally, we set all the content.

For content, I utilized Java Swing. I used the Swing UI in order to create a design for my plugin. Due to my inexperience with Swing, the plugin is not the best looking. My Swing form consists of a root JPanel, and then a Jlist and JButton contained within. The list has the names of the patterns and the button is the generate button. There is also a JTextArea that has some instructions on usage.

#### Changes to the Code Generator
Due to the implementation of my plugin as a wrapper to the code generator that I had created prior, there were very little changes that I needed to make. The main change was to add a path variable to the DesignPatternBuilder class, so that each Builder would know the class to write the files to. Adding this path was done by getting the Project object from my ToolWindow code. I passed this Project to my Code Generator class and from there I was able to get the users project directory in order to read the input.json file and write to the specified path.

As far as reading the JSON input goes, I still read the input file in order to get all the name substitutions. I no longer read the input file in order to get the desired pattern since that is done within the Tool Window and the plugin itself.
```java
filename = project.getBasePath() + "/input.json";
```
```java
path = project.getBasePath() + "/src";
```
```java
director.getBuilder().nameReplacements = nameReplacements;
director.getBuilder().path = path;
```
The prior code generator only had the nameReplacements variable, this one has the path variable as well.

#### Sample JSON Input
```json
{

  "facade": {
    "facadeName":"ConcreteFacade",
    "subsystemClasses": ["Subclass1", "Subsystem2", "Subsystem3"],
    "facadeOperation": "start"
  },

  "factory method": {
    "productInterfaceName": "Product",
    "abstractFMName": "FactoryMethod",
    "concreteProducts": [{"class": "Product1", "concFactory": "Creator1"}, {"class":  "Product2", "concFactory": "Creator2"}]
  },

  "chain of responsibility": {
    "abstractHandlerName": "Handler",
    "concreteHandlerName": "ConcreteHandler",
    "nextHandler": "successor",
    "handleRequest": "handleRequest",
    "canHandleRequest": "canHandleRequest"
  },

  "mediator": {
    "abstractMediator": "Mediator",
    "concreteMediator": "ConcreteMediator",
    "mediateFunction": "mediate",
    "abstractColleague": "Colleague",
    "concreteColleagues": ["Colleague1", "Colleague2", "Colleague3"]
  },

  "template method": {
    "abstractTMName": "TemplateMethod",
    "primitiveOperations": ["action1", "action2", "action3"],
    "concreteTMName": "ConcreteTM",
    "templateOperation": "templateOperation"
  },

  "visitor": {
    "abstractElement": "Element",
    "concreteElements": ["ElementA", "ElementB", "ElementC"],
    "abstractVisitor": "Visitor",
    "concreteVisitor": "ElementVisitor"
  },

  "abstract factory": {
    "abstractFactory": "AbstractFactory",
    "concreteFactory": "ConcreteFactory",
    "productInterfaces": ["ProductA", "ProductB"],
    "concProducts": [{"class": "ProductA1", "superinterface": "ProductA"}, {"class": "ProductB1", "superinterface": "ProductB"}]
  },

  "builder": {
    "abstractBuilder": "Builder",
    "concreteBuilder": "ConcreteBuilder",
    "directorName": "Director",
    "complexObjectName": "ComplexObject",
    "buildSteps": ["buildPartA", "buildPartB", "buildPartC"]
  }
}
```
Go down to the object that has the pattern you want as the key and change the values. For example, if you chose to generate the Builder Pattern, you can specify what you want your abstract builder class to be named. You can specify and number of steps in the buildSteps key. Your complex object will be the name of the class that you want to actually build. This file will be read when you click the generate button within the ToolWindow.

#### Sample Run
Go ahead and enter gradle runIDE into the terminal or run the Plugin run configuration to start up a new IDE instance with the plugin running. Have an input.json file in the project directory and edit the values for whatever design pattern you want. Open up the Design Pattern Generator tool window and select the pattern you would like and click generate. 
Here's a shot of what that would like in default IntelliJ colorscheme with a floating window.

![Imgur](https://i.imgur.com/a0TkhCl.png)

Suppose we wanted the Builder design pattern (lets use the default input.json values). We select Builder and the following .java files will be created.

```java
package com.cs474.generatedpatterns;

public abstract class Builder {
  public abstract ComplexObject getResult();

  public abstract void buildPartA();

  public abstract void buildPartB();

  public abstract void buildPartC();
}

public class ConcreteBuilder extends Builder {
  ComplexObject complexobject = new ComplexObject();

  @Override
  public ComplexObject getResult() {
    return complexobject;
  }

  @Override
  public void buildPartA() {
  }

  @Override
  public void buildPartB() {
  }

  @Override
  public void buildPartC() {
  }
}

public class Director {
  Builder builder;

  Director(Builder builder) {
    this.builder = builder;
  }

  public void construct() {
    builder.buildPartA;
    builder.buildPartB;
    builder.buildPartC;
  }
}
```
#### Limitations
One of the big issues is the way that the plugin looks. One certain IDE color schemes, the plugin will be hard to see and use. It's a very basic design.

The usage of a input.json file might be a turn off to some people. They may want to have all the plugin stuff contained within itself. Errors in the json file could cause potential issues. 

File location: I was unable to figure out how to fully incorporate code into a users working code base. The methods I tried would write the code to some other package, so to avoid confusion I created a generic package where the code will write to and it is the responsibility of the user to copy and paste this code into their source folder. On the bright side, this means that if a user were to potentially name one of their design pattern objects the same name as a source code file, the source code won't get overrriden, so this acts as an extra safety net.

#### Pros to My Implementation
One of the pros to the simple design is that it is pretty simple to use. It'll be pretty hard for a person to break the code since no user input is entered into the plugin. On the other hand, having details in the input.json file might make life hard.

The input.json file, however, offers a speed alternative that manually entering each user specification would not be able to offer. If a user makes a mistake in entering their data, the plugin won't work, but they'll be able to quickly change whatever they need to, whereas a mistype could result in the user having to type everything over again. Also, the JSON input offers quick chaining of patterns. Once the overhead of setting up the JSON file is done, the user can rapidly create design patterns.
