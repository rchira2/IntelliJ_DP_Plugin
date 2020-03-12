package com.cs474;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;


public class DesignPatternToolWindow implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //create a plugin, set the project and get the content from the Swing form
        MyPlugin myPlugin = new MyPlugin(toolWindow);
        myPlugin.setProject(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myPlugin.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        CodeGenerator.logger.trace("The tool window was created and Swing form was attached");
    }
}
