package main.java.flash;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.logic.Logic;
import main.java.parser.InvalidInputFormatException;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import main.java.data.Task;
import main.java.gui.CommandBarController;
import main.java.gui.EmptyTableController;
import main.java.gui.HelpDisplayController;
import main.java.gui.TabsController;
import main.java.gui.TasksItemController;
import main.java.gui.TasksTableController;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class Main extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private Logic logic;
	private Task task;

	private TasksTableController pendingTableControl;
	private TasksTableController completeTableControl;
	private CommandBarController barControl;
	private TabsController tabControl;
	private ArrayList<String> historyLog;
	private ArrayList<Task> result;
	private ArrayList<Task> previousResult = new ArrayList<Task>();
	private ArrayList<Task> searchResult = new ArrayList<Task>();
	private ListView<TasksItemController> tasksDisplay;
	private ListView<TasksItemController> completeDisplay;

	private static final String EMPTY_STRING = "";
	private static final String SPACE = " ";
	private static final String SPLIT = "\\s+";
	private static final int COMMAND_INDEX = 0;

	private int pointer;
	private boolean isFeedback = false;
	private int count = 0;
	

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Flashpoint");
		this.primaryStage.getIcons().add(new Image("/main/resources/images/cache.png"));

		initControllers(this);
		initLogic();
		initRootLayout();
		checkIsTasksEmpty();
	}

	/********************************** Initialisation ***********************************************/
	/***********************************************************************************************/
	private void initControllers(Main main) {
		pendingTableControl = new TasksTableController();
		completeTableControl = new TasksTableController();
		barControl = new CommandBarController(this);
		tabControl = new TabsController();
		tasksDisplay = pendingTableControl.getListView();
		completeDisplay = completeTableControl.getListView();
	}

	private void initLogic() throws Exception {
		logic = new Logic();
	}

	private void checkIsTasksEmpty() throws Exception {
		if (logic.displayPending().isEmpty()) {
			tabControl.setUpcomingTab(new EmptyTableController());
			tabControl.setUpcomingLabel("   You have " +logic.displayPending().size()+" upcoming task.");
		} else {
			tabControl.setUpcomingTab(pendingTableControl);
			tabControl.setUpcomingLabel("   You have " +logic.displayPending().size()+" upcoming tasks.");
		}

		if (logic.displayComplete().isEmpty()) {
			tabControl.setEmptyCompleteTab();
			tabControl.setCompleteLabel("   You have " +logic.displayComplete().size()+" completed task.");
		} else {
			tabControl.setCompleteTab(completeTableControl);
			tabControl.setCompleteLabel("   You have " +logic.displayComplete().size()+" completed tasks.");
		}

		updateList();
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

			showTabs();
			showCommandBar();
			showTasks();
			initLog();
			listenerForTaskList();
			saveFilename();
			loadFilename();
			

			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showTabs() {
		rootLayout.setTop(tabControl);
	}

	private void showTasks() {
		tabControl.setUpcomingTab(pendingTableControl);
		tabControl.setCompleteTab(completeTableControl);
	}

	private void showCommandBar() {
		rootLayout.setBottom(barControl);
		barControl.setText("What is your main focus for today?");
		barControl.getFocus();
		barControl.setBgColour("med");
	}

	private void initLog() {
		historyLog = new ArrayList<String>();
	}

	public void handleKeyPress(CommandBarController commandBarController, KeyEvent event, String text)
			throws Exception {
		assert commandBarController != null;
		if (event.getCode() == KeyCode.ENTER) {
			handleEnterPress(commandBarController, text);
			checkIsTasksEmpty();
		} else if ((event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) && !historyLog.isEmpty()) {
			event.consume(); // nullifies the default behavior of UP and DOWN on
								// a TextArea
			handleGetPastCommands(event);
		} else if ((event.getCode() == KeyCode.TAB)) {
			pendingTableControl.controlToList();
		}
	}

	private void listenerForTaskList() {

		tasksDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					handleEnterKey();
				} else if (e.getCode() == KeyCode.ESCAPE) {
					// handleEscKey();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey();
				}
			}

		});

		completeDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					handleEnterKey();
				} else if (e.getCode() == KeyCode.ESCAPE) {
					// handleEscKey();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey();
				}
			}

		});

	}

	private void handleEnterKey() {
		TasksItemController chosen = tasksDisplay.getSelectionModel().getSelectedItem();
		barControl.updateUserInput("edit " + chosen.getTaskName());
		barControl.getFocus();
	}

	private void handleDeleteKey() {
		TasksItemController chosen = tasksDisplay.getSelectionModel().getSelectedItem();
		barControl.updateUserInput("delete " + chosen.getTaskName());
		barControl.getFocus();
	}

	private void handleGetPastCommands(KeyEvent event) {
		assert event != null;
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
		assert commandBarController != null;
		count = 0;
		if (userInput.equalsIgnoreCase("help")) {
			notification(userInput);
			historyLog.add(userInput);
			tabControl.setUpcomingTab(new HelpDisplayController());
			commandBarController.clear();
			return;
		}

		if (userInput.isEmpty()) {
			return;
		} else {
			// normal command
			historyLog.add(userInput);

			if (userInput.equalsIgnoreCase("clear")) {
				if (tabControl.getUpcomingTab().isSelected()) {
					userInput = userInput + "Upcoming";
				} else if (tabControl.getCompleteTab().isSelected()) {
					userInput = userInput + "Complete";
				}
			}
            try{
			 result = new ArrayList<Task>(logic.handleUserCommand(userInput, result));
            }catch(Exception e){
            	setFeedback(commandBarController,"error", e.toString());
            }
            setFeedback(commandBarController,"valid", userInput);

		}
		new CommandBarController();
		commandBarController.clear();
	}

	private void setFeedback(CommandBarController commandBarController, String type ,String userInput) {
		assert commandBarController != null;
		int i = 1;
		isFeedback = true;
		if (userInput.indexOf(' ') != -1) {
			i = userInput.indexOf(' ');
			String firstWord = userInput.substring(0, i);
			String subString = userInput.substring(i + 1);
			if(type.equals("error")){
			     commandBarController.setFeedback("Error" + "' " + subString + " '",Color.RED);
			}else{
				commandBarController.setFeedback("Successfully " +firstWord+"ed "+ "' " + subString + " '",Color.GREEN);
			}
		} else {
			commandBarController.setFeedback("  Successfully " + userInput + "ed   ", Color.GREEN);
		}
	}

	// Method that returns the first word
	public static String firstWord(String input) {
		String result = input; // if no space found later, input is the first
								// word

		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == ' ') {
				result = input.substring(0, i);
				break;
			}
		}
		return result;
	}

	public void removeAllStyle(Node n) {
		n.getStyleClass().removeAll("bad", "med", "good", "best");
	}

	public void populateList(ArrayList<Task> result) {
		pendingTableControl.clearTask();
		for (Task temp : result) {
			pendingTableControl.addTask(temp);
		}
	}

	private void updateList() {
		Platform.runLater(() -> {
		pendingTableControl.clearTask();
		completeTableControl.clearTask();
		try {
			for (Task temp : logic.displayPending()) {
				pendingTableControl.addTask(temp);
			}
			for (Task temp : logic.displayComplete()) {
				completeTableControl.addTask(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		});
	}

	public void trySearch(String oldValue, String newValue) {
		
		String[] fragments = null;
		fragments = newValue.split(SPLIT);
		boolean isEdit = fragments[COMMAND_INDEX].equalsIgnoreCase("edit");
		boolean isDelete = fragments[COMMAND_INDEX].equalsIgnoreCase("delete");
		boolean isSearch = fragments[COMMAND_INDEX].equalsIgnoreCase("search");
		boolean isMark = fragments[COMMAND_INDEX].equalsIgnoreCase("mark");
		
		// TODO Auto-generated method stub
		try {
			if(isEdit || isDelete || isSearch || isMark){
			   searchResult = logic.handleSearch(oldValue, newValue);
			   if (searchResult.size() != 0 && searchResult.size()!= pendingTableControl.getSize()) {
					populateList(searchResult);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	public void showColourCommand(String oldValue, String newValue) {
		// TODO Auto-generated method stub
		String[] fragments = null;
		fragments = newValue.split(SPLIT);

		if (isFeedback || newValue.equals(EMPTY_STRING)) {
			removeAllStyle(barControl.getCommandBar());
			barControl.setBgColour("med");
		}

		if (logic.isCommand(fragments[COMMAND_INDEX])) {
			removeAllStyle(barControl.getCommandBar());
			barControl.setBgColour("best");
		} else if (!logic.isCommand(fragments[COMMAND_INDEX]) && !newValue.equals(EMPTY_STRING)) {
			removeAllStyle(barControl.getCommandBar());
			barControl.setBgColour("bad");
		}
	}

	public void saveFilename() {
		tabControl.getSaveMenu().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File saveFile = fileChooser.showSaveDialog(null);	
				if(saveFile!=null){
				 logic.saveFilename(saveFile.getAbsolutePath());
				}
			}
		});

	}

	public void loadFilename() {

		tabControl.getLoadMenu().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File loadFile = fileChooser.showOpenDialog(null);
				if (loadFile!= null){
					logic.loadFilename(loadFile.getAbsolutePath());
				}
			}
		});

	}
	
	private void notification(String userInput){
		String title = "Successfully ";
        String message = userInput;
        NotificationType notification = NotificationType.SUCCESS;

        TrayNotification tray = new TrayNotification();
        tray.setTitle(title);
        tray.setMessage(message);
        tray.setAnimationType(AnimationType.POPUP);
        tray.setNotificationType(notification);
        tray.showAndDismiss(Duration.seconds(2));
        
	}

}
