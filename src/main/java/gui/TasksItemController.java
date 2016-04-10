/* @@author A0124078H */
package main.java.gui;

import java.io.IOException;

import java.text.SimpleDateFormat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import main.java.data.TaskType;
import main.java.data.TaskStatus;
import main.java.data.Task;
import java.util.Date;
import java.util.List;

public class TasksItemController extends BorderPane {

	@FXML private BorderPane cardLayout;
	@FXML private VBox card;
	@FXML private Text taskname;
	@FXML private Text date;
	@FXML private Label labelDate;
	@FXML private Label lblIndex;
	@FXML private Shape priorityColor;
	@FXML private Shape tagDateColor;
	@FXML private VBox vbox;
	@FXML private ImageView banner;
	@FXML private ImageView imgDate;

	private static final String FILE_STATS_ITEM_FXML = "/main/resources/layouts/TasksItem.fxml";
	private static final String STRING_FILL_STYLE_FORMAT = "-fx-fill: %s";
	private static final String BASE_COLOUR_HIGH = "#EF5350";
	private static final String BASE_COLOUR_DEFAULT = "rgba(0,0,255,0.3)";
	private static final String BASE_COLOUR_MED = "#FFA726";
	private static final String BASE_COLOUR_LOW = "#66BB6A";
	private static final String GREEN_THEME = "rgba(0,255,0,0.3)";
	private static final String TRANSPARENT_THEME = "rgba(192,192,192,0.3)";
	private static final String RED_THEME = "rgba(255,0,0,0.4)";

	private String taskName;
	private String taskPriority;
	private Task task;
	private String taskTime;
	private Boolean isLastModified;
	private TaskType taskType;

	public TasksItemController(Task task, int count, String theme) {
		this.task = task;
		this.taskName = task.getTask();
		this.taskPriority = task.getPriority().getType();
		this.taskTime = showTime(task.getTime());
		this.taskType = task.getType();
		this.isLastModified = task.getLastModified();

		FXMLLoader loader = new FXMLLoader(getClass().getResource(FILE_STATS_ITEM_FXML));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setUpTaskIndex();
		setUpTaskName(task, count);
		setUpTaskTime(task);
		setUpTaskPriorityColour(task, theme);

	}
	
	/** Set up task priority colour for task
	 * @param task
	 * @param theme
	 */
	private void setUpTaskPriorityColour(Task task, String theme) {
		this.priorityColor.setStyle(String.format(STRING_FILL_STYLE_FORMAT, generateColour(task.getPriority().getType(), theme)));
	}
	

	/** Set up task time for task
	 * @param task
	 */
	private void setUpTaskTime(Task task) {
		if (!task.getTime().isEmpty()) {
			this.labelDate.setText(showTime(task.getTime()));
			labelDate.setStyle("-fx-background-color: transparent; -fx-padding: 5px; -fx-font-size:12px;");
			if (isToday(task.getTime())) {
				setUpTodayTask();
			}
			if (task.getStatus() == TaskStatus.OVERDUE) {
				setUpOverdueTask(task);
			}
			if (task.getStatus() == TaskStatus.COMPLETED) {
				setUpCompleteTaskWithTime(task);
			}
		} else {
			if (task.getStatus() == TaskStatus.COMPLETED) {
				setUpCompleteTaskNoTime();
			}
		}
	}
	
	/**
	 * Set up completed task with no time specified
	 */
	private void setUpCompleteTaskNoTime() {
		labelDate.setStyle("-fx-text-fill: green;-fx-background-color: transparent; "
				+ "-fx-padding: 5px; -fx-font-size:12px;");
		this.labelDate.setText("[COMPLETED] ");
	}
	
	/**
	 * Set up completed task with user time specified
	 * @param task
	 */
	private void setUpCompleteTaskWithTime(Task task) {
		labelDate.setStyle("-fx-text-fill: green;-fx-background-color: transparent; "
				+ "-fx-padding: 5px; -fx-font-size:12px;");
		this.labelDate.setText("[COMPLETED] " + showTime(task.getTime()));
	}
	
	
	/** Set up overdue task label for task
	 * @param task
	 */
	private void setUpOverdueTask(Task task) {
		labelDate.setStyle("-fx-text-fill: #F50057;-fx-background-color: transparent; "
				+ "-fx-padding: 5px; -fx-font-size:12px;");
		this.labelDate.setText("[OVERDUE] " + showTime(task.getTime()));
		this.banner.setImage(new Image("/main/resources/images/overdue.png"));
		banner.setFitWidth(70);
		banner.setPreserveRatio(true);
	}
	
	/**
	 * Set up today label for task due today
	 */
	private void setUpTodayTask() {
		this.imgDate.setImage(new Image("/main/resources/images/imgToday.png"));
		imgDate.setFitWidth(60);
		imgDate.setPreserveRatio(true);
	}

	/** Set up task name for task
	 * @param task
	 * @param count
	 */
	private void setUpTaskName(Task task, int count) {
		if (count == 999) {
			this.taskname.setText(" " + task.getTask());
			lblIndex.setText("1");
		} else {
			this.taskname.setText(" " + task.getTask());
			lblIndex.setText(String.valueOf(count));
		}
		if (task.getStatus() == TaskStatus.OVERDUE) {
			taskname.setStyle("-fx-fill: #F50057;");
		}
	}

	/**
	 * Set up task index number for task
	 */
	private void setUpTaskIndex() {
		lblIndex.setStyle("-fx-font-size: 38px; -font-text-fill:white;");
	}
	/* @@author A0127481E */
	private boolean isToday(List<Date> dates) {
		int size = dates.size();
		if (size == 0) {
			return false;
		} else {
			Date today = new Date();
			String todayDate = today.toString().substring(0, 9);
			if (size == 1) {
				if (dates.get(0).toString().substring(0, 9).equals(todayDate)) {
					return true;
				}
			} else if (size == 2) {
				if (today.after(dates.get(0)) && today.before(dates.get(1))) {
					return true;
				}
			}
		}
		return false;
	}

	private String showTime(List<Date> dates) {
		SimpleDateFormat df = new SimpleDateFormat("EEEE dd MMM hh:mma");
		SimpleDateFormat df1 = new SimpleDateFormat("hh:mma");
		SimpleDateFormat df2 = new SimpleDateFormat("EEEE dd MMM");
		//SimpleDateFormat df3 = new SimpleDateFormat("EEEE");
		if (dates.size() == 0) {
			return "No specified time";
		} else {
			if (taskType == TaskType.DEADLINE) {
				return "Due: " + df.format(dates.get(0));
			} else if (taskType == TaskType.DURATION) {
				String time;
				// System.out.println(dates);
				if (dates.get(0).toString().substring(0, 10).equals(dates.get(1).toString().substring(0, 10))) {
					time = df2.format(dates.get(0)) + " " + df1.format(dates.get(0)) + " - " + df1.format(dates.get(1));
				} else {
					time = "" + df.format(dates.get(0)) + " to " + df.format(dates.get(1));
				}
				return time;
			} else {
				String time = "";
				for (int i = 0; i < dates.size(); i++) {
					time += df.format(dates.get(i));
					if (i + 1 < dates.size()) {
						time += ", ";
					}
				}
				return time;
			}
		
		}
	}

	/* @@author A0124078H */
	/** Get and return the taskname for task
	 * @return
	 */
	public String getTaskName() {
		return this.taskName;
	}

	/** Get and return the task priority for task
	 * @return
	 */
	public String getTaskPriority() {
		return this.taskPriority;
	}

	/** Get and return the task time for task
	 * @return
	 */
	public String getTaskTime() {
		return this.taskTime;
	}

	public boolean getLastModified() {
		return this.isLastModified;
	}

	public void setLastModified(Boolean value) {
		this.isLastModified = value;
		this.task.setLastModified(false);
	}

	
	/** Set up colour for task item depending on the priority
	 * @param priority
	 * @param theme
	 * @return
	 */
	private String generateColour(String priority, String theme) {
		if (priority.equals("low")) {
			return BASE_COLOUR_LOW;
		} else if (priority.equals("medium")) {
			return BASE_COLOUR_MED;
		} else if (priority.equals("high")) {
			return BASE_COLOUR_HIGH;
		} else {
			if (theme.equals("green")) {
				return GREEN_THEME;
			} else if (theme.equals("blue")) {
				return BASE_COLOUR_DEFAULT;
			} else if (theme.equals("transparent")) {
				return TRANSPARENT_THEME;
			} else if (theme.equals("red")) {
				return RED_THEME;
			}
			return BASE_COLOUR_DEFAULT;
		}
	}

}
/* @@author A0124078H */