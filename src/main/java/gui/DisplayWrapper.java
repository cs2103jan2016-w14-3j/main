package main.java.gui;

import java.util.ArrayList;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.java.data.Task;


public class DisplayWrapper {
    private Task taskToDo;
    private StringProperty taskName;
    private StringProperty taskTime;
    private StringProperty taskPriority;


    
    public DisplayWrapper(Task taskToDo) {
        this.taskToDo = taskToDo;
        this.taskName = new SimpleStringProperty(taskToDo.getTask());
        this.taskTime = new SimpleStringProperty(taskToDo.getTime());
        this.taskPriority = new SimpleStringProperty(taskToDo.getPriority());
    }

    public StringProperty taskName() {
        return taskName;
    }

    public StringProperty taskTime() {
        return taskTime;
    }

    public StringProperty taskPriority() {
        return taskPriority;
    }
//
//    public ArrayList<CodeSnippet> getCodeSnippets() {
//        return author.getCodeSnippets();
//    }
    
}
