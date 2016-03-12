package main.java.flash;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.logic.Logic;
import main.java.data.Task;
import main.java.gui.CommandBarController;
import main.java.gui.EmptyTableController;
import main.java.gui.RootLayoutController;
import main.java.gui.TasksItemController;
import main.java.gui.TasksTableController;
import main.java.gui.HistoryLogsController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ListView;

public class Main extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private Logic logic = new Logic();
	private Task task;

	private TasksTableController tableControl = new TasksTableController();
	private HistoryLogsController logControl = new HistoryLogsController();
	private CommandBarController barControl = new CommandBarController(this);
	private EmptyTableController emptyTable;

	private Boolean isChange = false;
	private Boolean isDelete = false;

	private ArrayList<String> historyLog;
	private ArrayList<Task> result;
	private ArrayList<Task> finalResult = new ArrayList<Task>();
	private ArrayList<Task> searchResult = new ArrayList<Task>();
	private ListView<TasksItemController> list = new ListView<TasksItemController>();

	private final String EMPTY_STRING = "";
	private static final String SPACE = " ";
	private static final String SPLIT = "\\s+";

	private static final int OFFSET = 1;
	private static final int COMMAND_INDEX = 0;
	private static final int THE_REST_INDEX = 1;
	private int pointer;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		// this.primaryStage.initStyle(StageStyle.TRANSPARENT);;
		this.primaryStage.setTitle("Flashpoint");
		this.primaryStage.getIcons().add(new Image("/main/resources/images/lightning.fw.png"));

		initRootLayout();
		initLog();
		result = initLogic();
		checkIsTasksEmpty();
	}

	private void checkIsTasksEmpty() {
		if (result.isEmpty()) {
			rootLayout.setCenter(new EmptyTableController());
			System.out.println("lalalal");
		} else {
			populateList(tableControl, result);
		}
	}

	private void initLog() {
		// TODO Auto-generated method stub
		historyLog = new ArrayList<String>();
	}

	private ArrayList<Task> initLogic() throws Exception {
		return logic.initLogic();
	}

	/**
	 * Initialises the RootLayout that will contain all other JavaFX components.
	 */
	private void initRootLayout() {

		try {
			// load root layout from fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/layouts/RootLayout.fxml"));
			rootLayout = loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);

			scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
				if (key.getCode() == KeyCode.ESCAPE) {
					primaryStage.hide();
				}
			});

			RootLayoutController rootController = new RootLayoutController(this);

			showCommandBar();
			showTasks(this);
			showLog(this);
			listenerForTaskList();

			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showTasks(Main mianApp) {
		rootLayout.setCenter(tableControl);
	}

	private void showCommandBar() {
		rootLayout.setBottom(barControl);
	}

	private void showLog(Main mainApp) {
		// rootLayout.setCenter(logControl);
	}

	public void handleSearch(String oldValue, String newValue) {

		String[] fragments = newValue.split(SPLIT);
		boolean isEdit = fragments[COMMAND_INDEX].equalsIgnoreCase("edit");
		boolean isDelete = fragments[COMMAND_INDEX].equalsIgnoreCase("delete");
		boolean isSearch = fragments[COMMAND_INDEX].equalsIgnoreCase("search");
        
		if(newValue.contains(SPACE)){
			updateList();
		}
		if ((isEdit || isDelete || isSearch) && fragments.length > 1) {
			newValue = fragments[1];
			String[] parts = newValue.toLowerCase().split(SPACE);
			ObservableList<TasksItemController> temp = FXCollections.observableArrayList();

			int count = 0;
			for (Task task : result) {
				boolean match = true;
				String taskMatch = task.getTask() + task.getPriority() + task.getTime();
				for (String part : parts) {
//					System.out.println(part);
					if (!taskMatch.toLowerCase().contains(part)) {
						match = false;
						break;
					}
				}
				// if match add to temp
				if (match) {
					temp.add(new TasksItemController(task, count++));
				}
			}
			 tableControl.setItems(temp);
		}
		
	}

	public void handleKeyPress(CommandBarController commandBarController, KeyEvent event, String text)
			throws Exception {
		// TODO Auto-generated method stub
		int i;
		String taskname = null;
		if (event.getCode() == KeyCode.ENTER) {
			handleEnterPress(commandBarController, text);
		} else if ((event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) && !historyLog.isEmpty()) {
			event.consume(); // nullifies the default behavior of UP and DOWN on
								// a TextArea
			handleGetPastCommands(event);
		} else if ((event.getCode() == KeyCode.TAB)) {
			// i = text.indexOf(' ');
			// String firstWord = text.substring(0,i);
			tableControl.controlToList();

		}
	}

	private void listenerForTaskList() {
		// TODO Auto-generated method stub
		ListView<TasksItemController> tasksDisplay = tableControl.getListView();

		tasksDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					// System.out.println("enter");
					handleEnterKey();
				} else if (e.getCode() == KeyCode.ESCAPE) {
					// handleEscKey();
					// System.out.println("escape");
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey();
					// System.out.println("delete");
				}
			}

			private void handleEnterKey() {
				// TODO Auto-generated method stub
				TasksItemController chosen = tasksDisplay.getSelectionModel().getSelectedItem();
				// System.out.println(chosen.getTaskName());
				barControl.updateUserInput("edit " + chosen.getTaskName());
				barControl.requestFocus();
			}

			private void handleDeleteKey() {
				// TODO Auto-generated method stub
				TasksItemController chosen = tasksDisplay.getSelectionModel().getSelectedItem();
				// System.out.println(chosen.getTaskName());
				barControl.updateUserInput("delete " + chosen.getTaskName());
				barControl.requestFocus();
			}

		});

	}

	private void handleGetPastCommands(KeyEvent event) {
		String pastCommand = getPastCommandFromHistory(event.getCode());
		barControl.updateUserInput(pastCommand);
	}

	private String getPastCommandFromHistory(KeyCode code) {
		if (code == KeyCode.DOWN) {
			return getNextCommand();
		} else if (code == KeyCode.UP) {
			return getPreviousCommand();
		} else {
			return EMPTY_STRING;
		}
	}

	private String getPreviousCommand() {
		if (pointer > 0) {
			pointer--;
		}
		return historyLog.get(pointer);
	}

	private String getNextCommand() {
		if (pointer < historyLog.size() - 1) {
			pointer++;
		}
		return historyLog.get(pointer);
	}

	private void handleEnterPress(CommandBarController commandBarController, String userInput) throws Exception {
		int number;

		// if user enter number for either delete or edit
		if (userInput.matches("\\d+")) {
			logControl.addLog(userInput);
			historyLog.add(userInput);
			number = Integer.parseInt(userInput);
			// check if is delete or edit
			if (isChange) {
				handleEditWithNumber(number);
			} else {
				handleDeleteWithNumber(number);
			}
			setFeedback(commandBarController, userInput);

			// if no more tasks
			if (isListEmpty()) {
				rootLayout.setCenter(new EmptyTableController());
				// System.out.println("lalalal");
			} else {
				showTasks(this);
			}

			tableControl.clearTask();
			populateList2(tableControl, logic.display());

			commandBarController.clear();

			isChange = false;

			return;

		} else if (userInput.isEmpty()) {

			return;
		} else {
			// normal command
			setFeedback(commandBarController, userInput);
			logControl.addLog(userInput);
			historyLog.add(userInput);
			result = new ArrayList<Task>(logic.handleUserCommand(userInput, result));

			// if no more tasks
			if (result.isEmpty()) {
				rootLayout.setCenter(new EmptyTableController());
			} else {
				showTasks(this);
			}
			tableControl.clearTask();

			if (isListEmpty()) {
				commandBarController.clear();
				return;
			}

			if (logic.isEditCommand(userInput)) {
				handleEditCommand();
			}
			if (logic.isDeleteCommand(userInput)) {
				handleDeleteCommand();
			}

			populateList2(tableControl, result);

		}

		commandBarController.clear();
	}

	private void handleDeleteCommand() {
		for (Task temp : result) {
			temp.setShowToUserDelete(true);
		}
	}

	private void handleEditCommand() {
		handleDeleteCommand();
		finalResult.add(result.remove(result.size() - 1));
		isChange = true;
	}

	private boolean isListEmpty() throws Exception {
		return logic.display().isEmpty();
	}

	private void setFeedback(CommandBarController commandBarController, String userInput) {
		int i = 1;
		if (userInput.indexOf(' ') != -1) {
			i = userInput.indexOf(' ');
			String firstWord = userInput.substring(0, i);
			String subString = userInput.substring(i + 1);
			commandBarController.setFeedback("  Successfully " + firstWord + "ed " + "[" + subString + "]  ");
		} else {
			commandBarController.setFeedback("  Successfully " + userInput + "ed   ");
		}
	}

	private void handleDeleteWithNumber(int number) throws Exception {
		logic.delete(result.get(number - 1));
	}

	private void handleEditWithNumber(int number) throws Exception {
		finalResult.add(result.get(number - 1));
		logic.edit(finalResult);
		finalResult.clear();
	}

	public void populateList(TasksTableController tableControl, ArrayList<Task> result) {
		int count = 1;
		tableControl.clearTask();
		for (Task temp : result) {
			tableControl.addTask(temp, count++);
		}
	}

	private void updateList() {
		int count = 1;
		tableControl.clearTask();
		try {
			for (Task temp : logic.display()) {
				tableControl.addTask(temp, count++);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void populateList2(TasksTableController tableControl, ArrayList<Task> result) {
		int count = 1;

		for (Task temp : result) {
			tableControl.addTask(temp, count++);
		}
	}

}
