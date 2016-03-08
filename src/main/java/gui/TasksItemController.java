package main.java.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import main.java.data.Task;

public class TasksItemController extends BorderPane implements
Comparable<TasksItemController> {

	@FXML
	private HBox card;

	@FXML
	private Text filename;

	@FXML
	private Text date;

	@FXML
	private Text percentage;
	
	@FXML
	private Label labelDate;


	@FXML
	private Shape circle;
	
	@FXML
	private Shape tagDateColor;

	private static final String FILE_STATS_ITEM_FXML = "/main/resources/layouts/TasksItem.fxml";

	private static final String STRING_CIRCLE_FILL_STYLE_FORMAT = "-fx-fill: %s";

	private static final String BASE_COLOUR_0 = "#FF4D5E";
	private static final String BASE_COLOUR_20 = "#E8803D";
	private static final String BASE_COLOUR_40 = "#FFD251";
	private static final String BASE_COLOUR_60 = "#D7E84A";
	private static final String BASE_COLOUR_80 = "#51FF61";

	
	private double percentageValue;

	public TasksItemController(Task task) {
		double percentage = 0;
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FILE_STATS_ITEM_FXML));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//this.percentageValue = percentage;

		this.filename.setText(task.getTask());
	//	this.date.setText(task.getTime());
		if(!task.getTime().isEmpty()){
		   this.labelDate.setText(task.getTime());
	    	labelDate.setStyle("-fx-background-color: #1160F2; -fx-padding: 5px;");
		}
		this.circle.setStyle(String.format(STRING_CIRCLE_FILL_STYLE_FORMAT,
				generateColour(percentage)));
	}

	private String generateColour(double percentage) {
		if (percentage >= 80) {
			return BASE_COLOUR_80;
		} else if (percentage >= 60) {
			return BASE_COLOUR_60;
		} else if (percentage >= 40) {
			return BASE_COLOUR_40;
		} else if (percentage >= 20) {
			return BASE_COLOUR_20;
		} else {
			return BASE_COLOUR_0;
		}
	}

//	private String generateTruncatedName(String filename) {
//		if (filename.length() >= MAX_FILENAME_LENGTH) {
//			return String.format(STRING_TRUNCATED_FORMAT,
//					filename.substring(filename.length() -
//							MAX_FILENAME_LENGTH));
//		}
//		return filename;
//	}

	public double getPercentageValue() {
		return percentageValue;
	}

 
	@Override
	public int compareTo(TasksItemController otherItem) {
		return (int) Math.round(otherItem.getPercentageValue() -
				percentageValue);
	}
}