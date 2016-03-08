package main.java.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import main.java.data.Task;
import main.java.flash.Main;

public class TasksTableController extends BorderPane {

	private Main mainApp;
	private HistoryLogsController historyLogs;

	@FXML
	private ListView<TasksItemController> tasksDisplay;

//	@FXML
//	private Label title;

	private static final String FILE_STATS_FXML = "/main/resources/layouts/TasksTable.fxml";

	private ArrayList<TasksItemController> items;
	
	public TasksTableController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FILE_STATS_FXML));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//title.setText(taskName);
		initialise();
	}
	
	private void initialise() {
		items = new ArrayList<TasksItemController>();
		tasksDisplay.setItems(FXCollections.observableList(items));
	}

	public void addTask(Task task, int count) {	
	
		addFileStatsItem(task,count);
		
		//Collections.sort(items);

		tasksDisplay.setItems(FXCollections.observableList(items));
		
		//showLog();
	}
	
	
	public void clearTask(){
	   items.clear();    	
	}
	
	public ArrayList getTask(){
		return items;    	
	}
	
	public void showLog(){
		historyLogs.showLog();
	}

	/**
	 * Each TaskItems displayed as a row
	 * in this custom view.
	 * @param count 
	 * 
	 * @param currentFile
	 * @param currentNumLines
	 */
	private void addFileStatsItem(Task task, int count) {
		items.add(new TasksItemController(task,count));
	}

}
