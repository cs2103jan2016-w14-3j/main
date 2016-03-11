package main.java.gui;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import main.java.data.Task;
import main.java.flash.Main;

public class TasksTableController extends BorderPane {

	private Main mainApp;
	private HistoryLogsController historyLogs;
	private CommandBarController commandBar;

	@FXML
	private ListView<TasksItemController> tasksDisplay;

//	@FXML
//	private Label title;

	private static final String FILE_STATS_FXML = "/main/resources/layouts/TasksTable.fxml";

	private ArrayList<TasksItemController> items;
	protected String taskname;
	
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
		this.items = new ArrayList<TasksItemController>();
		this.tasksDisplay.setItems(FXCollections.observableList(items));
		commandBar = new CommandBarController();
	}
	
	public ListView<TasksItemController> getListView(){
		return tasksDisplay;
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
	
	public void setItems(ObservableList<TasksItemController> subentries) {
		// TODO Auto-generated method stub
		tasksDisplay.setItems(subentries);
	}

	public void controlToList() {
		    int count = 0;
			tasksDisplay.requestFocus();
			tasksDisplay.scrollTo(count);
            tasksDisplay.getFocusModel().focus(count);
			tasksDisplay.getSelectionModel().select(count);
		
	}

}
