package com.cs474;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;

public class MyPlugin {
    private JScrollPane rootPane;
    private JList list1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JButton generateButton;
    private Project project;

    public MyPlugin(ToolWindow toolWindow){
        generateButton.addActionListener(e -> {
            CodeGenerator d = new CodeGenerator();
            d.setProject(project);
            try {
                d.generatePattern();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });
    }
    public JScrollPane getContent(){
        return rootPane;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
