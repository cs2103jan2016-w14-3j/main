/* @@author A0124078H */
package main.java.gui;

import java.io.IOException;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import main.java.data.Task;

public class TasksTableController extends BorderPane {

	@FXML private ListView<TasksItemController> tasksDisplay;

	private static final String FILE_STATS_FXML = "/main/resources/layouts/TasksTable.fxml";
	private static final String RED_THEME = "red";
	private ArrayList<TasksItemController> items;
	protected String taskname;
	private String theme;

	public TasksTableController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FILE_STATS_FXML));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		initialise();
		setColour(RED_THEME);
	}

	private void initialise() {
		this.items = new ArrayList<TasksItemController>();
		this.tasksDisplay.setItems(FXCollections.observableList(items));
	}

	/**
	 * Get focus to the latest task change done by user. The task will be
	 * highlighted in the display
	 **/

	public void displayModified() {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getLastModified()) {
				tasksDisplay.requestFocus();
				tasksDisplay.scrollTo(i);
				tasksDisplay.getFocusModel().focus(i);
				tasksDisplay.getSelectionModel().select(i);
				items.get(i).setLastModified(false);
			}

		}
	}

	public ListView<TasksItemController> getListView() {
		return tasksDisplay;
	}

	public void addTask(Task task, int count, String theme) {
		setTasksItem(task, count, theme);
		tasksDisplay.setItems(FXCollections.observableList(items));
	}

	/**
	 *Each TaskItems displayed as a row in this custom view. 
	 * @param task
	 * @param count
	 * @param theme
	 */
	private void setTasksItem(Task task, int count, String theme) {
		items.add(new TasksItemController(task, count, theme));
	}

	public void setItems(ObservableList<TasksItemController> subentries) {
		tasksDisplay.setItems(subentries);
	}

	/**
	 * Return the control back to the task display
	 */
	public void controlToList() {
		int count = 0;
		tasksDisplay.requestFocus();
		tasksDisplay.scrollTo(count);
		tasksDisplay.getFocusModel().focus(count);
		tasksDisplay.getSelectionModel().select(count);
	}

	public void clearTask() {
		items.clear();
		tasksDisplay.refresh();
	}

	public int getSize() {
		return items.size();
	}

	public void setTheme(String colour) {
		theme = colour;
	}

	public void setColour(String colour) {
		tasksDisplay.getStylesheets().add("/main/resources/styles/stylesheet.css");
	}

}
/* @@author A0124078H */