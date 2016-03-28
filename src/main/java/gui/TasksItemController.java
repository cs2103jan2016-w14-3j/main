package main.java.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import main.java.data.TASK_NATURE;
import main.java.data.TASK_STATUS;
import main.java.data.Task;
import java.util.Date;
import java.util.List;


public class TasksItemController extends BorderPane {

	@FXML
	private BorderPane cardLayout;

	@FXML
	private VBox card;

	@FXML
	private Text filename;

	@FXML
	private Text date;

	@FXML
	private Label labelDate;

	@FXML
	private Label firstLetter;
	
	@FXML
	private Shape priorityColor;

	@FXML
	private Shape tagDateColor;

	@FXML
	private VBox vbox;
	
	@FXML
	private ImageView banner;

	private static final String FILE_STATS_ITEM_FXML = "/main/resources/layouts/TasksItem.fxml";

	private static final String STRING_FILL_STYLE_FORMAT = "-fx-fill: %s";

	private static final String BASE_COLOUR_HIGH = "#FF80AB";
	private static final String BASE_COLOUR_20 = "#E8803D";
	private static final String BASE_COLOUR_MED = "#F0C419";
	private static final String BASE_COLOUR_60 = "#D7E84A";
	private static final String BASE_COLOUR_LOW = "#00C853";

	private String taskName;

	private String taskPriority;

	private String taskTime;

	private TASK_NATURE taskType;

	public TasksItemController(Task task,int count) {
		this.taskName = task.getTask();
		//System.out.println(task.getPriority());
		this.taskPriority = task.getPriority().getType();
		this.taskTime = showTime(task.getTime());
		this.taskType = task.getType();

		FXMLLoader loader = new FXMLLoader(getClass().getResource(FILE_STATS_ITEM_FXML));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//firstLetter.setText(String.valueOf(task.getTask().charAt(0)).toUpperCase());
		firstLetter.setStyle("-fx-font-size: 40px; -font-text-fill:white;");
        if(count==999){
        	this.filename.setText(" " + task.getTask());
        }else{
		    this.filename.setText(" "+task.getTask());
		    firstLetter.setText(String.valueOf(count));
        }
		if(task.getStatus()== TASK_STATUS.OVERDUE){
			System.out.println(task);
			filename.setStyle("-fx-fill: #F50057;");
		}

		if(!task.getTime().isEmpty()){
			this.labelDate.setText(showTime(task.getTime()));
			labelDate.setStyle("-fx-background-color: transparent; -fx-padding: 5px; -fx-font-size:12px;");
			if(task.getStatus()== TASK_STATUS.OVERDUE){
				labelDate.setStyle("-fx-text-fill: #F50057;");
				this.labelDate.setText(" [OVERDUE] "+showTime(task.getTime()));
				this.banner.setImage(new Image("/main/resources/images/overdue.png"));
				banner.setFitWidth(70);
				banner.setPreserveRatio(true);
			}
			if(task.getStatus()== TASK_STATUS.COMPLETED){
				labelDate.setStyle("-fx-text-fill: #76FF03;");
				this.labelDate.setText(" [COMPLETED] "+showTime(task.getTime()));
			}
		}

		//card.setPadding(Insets.EMPTY);
		this.priorityColor.setStyle(String.format(STRING_FILL_STYLE_FORMAT,
				generateColour(task.getPriority().getType())));
		
		
	}


	private String showTime(List<Date> dates) {
		SimpleDateFormat df = new SimpleDateFormat("EEEE dd MMM hh:mma");
		SimpleDateFormat df1 = new SimpleDateFormat("hh:mma");
		SimpleDateFormat df2 = new SimpleDateFormat("EEEE dd MMM");
		SimpleDateFormat df3 = new SimpleDateFormat("EEEE");
		if (dates.size() == 0) {
			return "No specified time";
		}
		else {
			if (taskType == TASK_NATURE.DEADLINE) {
				return "Due: " + df.format(dates.get(0));
			}
			else if (taskType == TASK_NATURE.DURATION) {
				String time;
				//System.out.println(dates);
				if (dates.get(0).toString().substring(0, 10).equals(dates.get(1).toString().substring(0, 10))) {
					time = df2.format(dates.get(0)) + " from " + df1.format(dates.get(0)) 
					+ " to " + df1.format(dates.get(1));
				}
				else {
					time = "From " + df.format(dates.get(0)) 
					+ " to " + df.format(dates.get(1));
				}
				return time;
			}
			else if (taskType == TASK_NATURE.EVENT){
				String time = "";
				for (int i = 0; i < dates.size(); i++) {
					time += df.format(dates.get(i));
					if (i + 1 < dates.size()) {
						time += ", ";
					}
				}
				return time;
			}
			else if (taskType == TASK_NATURE.RECURRING_EVERY){
				String time = "Every " + df3.format(dates.get(0));
				return time;
			}
			else {
				String time = "Every alternate " + df3.format(dates.get(0));
				return time;
			}
		}
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
			return BASE_COLOUR_LOW;
		} else if (priority.equals("zai")) {
			return BASE_COLOUR_60;
		} else if (priority.equals("medium")) {
			return BASE_COLOUR_MED;
		} else if (priority.equals("high")) {
			return BASE_COLOUR_HIGH;
		} else {
			return BASE_COLOUR_20;
		}
	}

}