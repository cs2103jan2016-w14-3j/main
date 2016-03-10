package main.java.storage;

import java.util.ArrayList;
import java.util.Stack;

import main.java.data.Task;

public class UndoStorage {

	Stack< ArrayList<Task> > undoStack; 
	
	public UndoStorage() {
		undoStack =  new Stack< ArrayList<Task> >(); 
	}
	
}
