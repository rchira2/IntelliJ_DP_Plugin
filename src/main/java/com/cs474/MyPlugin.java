package com.cs474;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;

public class MyPlugin {
    private JScrollPane rootPane;
    private JButton generateButton;
    private Project project;
    private JPanel rootPanel;
    private JList dpList;
    private JTextArea selectAPatternFromTextArea;
    private CodeGenerator depacog = new CodeGenerator();

    public MyPlugin(ToolWindow toolWindow){
        //event listener for the button that will get the selected list item and set it as the desired pattern, then they will
        //invoke the code generator to generate the pattern
        generateButton.addActionListener(e -> {
            depacog.setProject(project);
            if(dpList.getSelectedValue() != null) {
                CodeGenerator.logger.trace("User selected a pattern");
                depacog.setPatternReq(dpList.getSelectedValue().toString().toLowerCase());
                try {
                    depacog.generatePattern();
                    CodeGenerator.logger.trace("Pattern was successfully created");
                } catch (IOException | ParseException ex) {
                    CodeGenerator.logger.error("Error in pattern creation");
                    ex.printStackTrace();
                }
            }
        });
    }
    public JPanel getContent(){
        return rootPanel;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
