package main.java.gui;

import java.io.IOException;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import main.java.data.Task;


public class TasksItemController extends BorderPane {

	@FXML
	private HBox card;

	@FXML
	private Text filename;

	@FXML
	private Text date;
	
	@FXML
	private Label labelDate;

	@FXML
	private Shape priorityColor;
	
	@FXML
	private Shape tagDateColor;

	private static final String FILE_STATS_ITEM_FXML = "/main/resources/layouts/TasksItem.fxml";

	private static final String STRING_FILL_STYLE_FORMAT = "-fx-fill: %s";

	private static final String BASE_COLOUR_0 = "#FF4D5E";
	private static final String BASE_COLOUR_20 = "#E8803D";
	private static final String BASE_COLOUR_40 = "#FFD251";
	private static final String BASE_COLOUR_60 = "#D7E84A";
	private static final String BASE_COLOUR_80 = "#51FF61";

	private String taskName;

	private String taskPriority;

	private String taskTime;

	public TasksItemController(Task task) {
		this.taskName = task.getTask();
		this.taskPriority = task.getPriority();
		this.taskTime = task.getTime();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FILE_STATS_ITEM_FXML));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.filename.setText(task.getTask());
		
		if(!task.getTime().isEmpty()){
		   this.labelDate.setText(task.getTime());
	    	labelDate.setStyle("-fx-background-color: #1160F2; -fx-padding: 5px;");
		}
		this.priorityColor.setStyle(String.format(STRING_FILL_STYLE_FORMAT,
				generateColour(task.getPriority())));
	}
	
	public String getTaskName(){
		return this.taskName;
	}

	public String getTaskPriority(){
		return this.taskPriority;
	}

	public String getTaskTime(){
		return this.taskTime;
	}
	
	public void setBgColour(){
		card.setStyle("-fx-background-color: green");
	}

	private String generateColour(String priority) {
		if (priority.equals("low")) {
			return BASE_COLOUR_80;
		} else if (priority.equals("zai")) {
			return BASE_COLOUR_60;
		} else if (priority.equals("med")) {
			return BASE_COLOUR_40;
		} else if (priority.equals("high")) {
			return BASE_COLOUR_0;
		} else {
			return BASE_COLOUR_20;
		}
	}

}