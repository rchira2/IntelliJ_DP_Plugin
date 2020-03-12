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
    private CodeGenerator depacog = new CodeGenerator();

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
    public JPanel getContent(){
        return rootPanel;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
