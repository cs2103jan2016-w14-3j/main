/* @@author A0124078H */
package main.java.gui;

import java.io.File;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import main.java.logic.Logic;

import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import main.java.data.Task;
import main.java.enumeration.TaskStatus;
import main.java.exception.InvalidInputFormatException;
import main.java.exception.NoFileNameException;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.*;

import main.java.Log.EventLog;

/**
 * @author Xunda
 */
public class Main extends Application {

	@FXML
	private BorderPane root;

	private Stage primaryStage;
	private BorderPane rootLayout;
	private HBox hBar;
	private HBox topBar;
	private Scene scene;
	private Logic logic;

	private TasksTableController allTableControl;
	private TasksTableController floatingTableControl;
	private TasksTableController pendingTableControl;
	private TasksTableController completeTableControl;
	private TasksTableController overdueTableControl;
	private SideBarController sidebar;
	private CommandBarController barControl;
	private TabsController tabControl;
	private EventLog appLog;
	private ArrayList<String> historyLog;
	private ArrayList<Task> result;
	private ArrayList<Task> searchResult;
	private ArrayList<Task> allResult;
	private ArrayList<Task> pendingResult;
	private ArrayList<Task> floatingResult;
	private ArrayList<Task> overdueResult;
	private ArrayList<Task> completeResult;
	private ListView<TasksItemController> pendingDisplay;
	private ListView<TasksItemController> completeDisplay;
	private ListView<TasksItemController> allDisplay;
	private ListView<TasksItemController> floatingDisplay;
	private ListView<TasksItemController> overdueDisplay;

	private static final int EXPANDED_WIDTH = 84;
	private static final int COMMAND_INDEX = 0;
	private static final int DEFAULT_HEIGHT = 670;
	private static final int DEFAULT_WIDTH = 570;
	private static final String EMPTY_STRING = "";
	private static final String SPLIT = "\\s+";
	private static final String VALID = "valid";
	private static final String ERROR = "error";
	private static final String ADD_COMMAND = "add";
	private static final String DELETE_COMMAND = "delete";
	private static final String SEARCH_COMMAND = "search";
	private static final String MOVE_COMMAND = "move";
	private static final String SORT_COMMAND = "sort";
	private static final String EDIT_COMMAND = "edit";
	private static final String UNDO_COMMAND = "undo";
	private static final String HELP_COMMAND = "help";
	private static final String MARK_COMMAND = "mark";
	private static final String UNMARK_COMMAND = "unmark";
	private static final String SWITCH_COMMAND = "switch";
	private static final String REDO_COMMAND = "redo";
	private static final String THEME_COMMAND = "theme";
	private static final String OPEN_COMMAND = "open";
	private static final String SAVE_COMMAND = "save";
	private static final String CLEAR_COMMAND = "clear";
	private static final String CLEARUPCOMING_COMMAND = "clearUpcoming";
	private static final String CLEARCOMPLETE_COMMAND = "clearComplete";
	private static final String CLEAROVERDUE_COMMAND = "clearOverdue";
	private static final String CLEARFLOATING_COMMAND = "clearFloating";
	private static final String CLEARALL_COMMAND = "clearAll";
	private static final String DELETECOMPLETE_COMMAND = "deleteComplete";
	private static final String SHOW_COMMAND = "show";
	private static final String SHOWCOMPLETE_COMMAND = "showComplete";
	private static final String FLASH_ICON = "/main/resources/images/cache.png";
	private static final String INTRO_IMAGE = "/main/resources/images/intro.fw.png";
	private static final String FLASH_LOGO = "/main/resources/images/flashIcon.png";
	private static final String FLASH_TITLE = "/main/resources/images/title.png";
	private static final String FLASH_POINT = "/main/resources/images/flashpoint.png";

	private PopOver bgPopOver;
	private int pointer = 0;
	private boolean isFeedback = false;
	private boolean isError = false;
	private boolean commandByNumber = false;
	private boolean isModifiedOverdue = false;
	private boolean isModifiedPending = false;
	private boolean isModifiedFloating = false;
	private boolean isModifiedComplete = false;
	private static double xOffset = 0;
	private static double yOffset = 0;
	private Label lblTitle;
	private String theme = null;
	private String background = "paris";
	private ImageView woodBg;
	private ImageView parisBg;
	private ImageView blackBg;
	private ImageView cropBg;
	private ImageView towerBg;
	private ImageView balloonBg;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		this.primaryStage.initStyle(StageStyle.TRANSPARENT);
		this.primaryStage.getIcons().add(new Image(FLASH_ICON));
		this.primaryStage.setHeight(DEFAULT_HEIGHT);
		this.primaryStage.setWidth(DEFAULT_WIDTH);
		initControllers(this);
		initLogic();
		initRootLayout();
		changeRedTheme();
		checkIsTasksEmpty();
		overdueTimer();
		appLog.getLogger().info("Start logging.....");
	}

	/********************************** Initialisation ***********************************************/

	/***********************************************************************************************/
	/**
	 * Initialising the controllers needed *******
	 * 
	 * @param main
	 */
	private void initControllers(Main main) {
		assert main != null;
		allTableControl = new TasksTableController();
		floatingTableControl = new TasksTableController();
		overdueTableControl = new TasksTableController();
		pendingTableControl = new TasksTableController();
		completeTableControl = new TasksTableController();

		barControl = new CommandBarController(this);
		tabControl = new TabsController();
		appLog = new EventLog();

		allDisplay = allTableControl.getListView();
		floatingDisplay = floatingTableControl.getListView();
		overdueDisplay = overdueTableControl.getListView();
		pendingDisplay = pendingTableControl.getListView();
		completeDisplay = completeTableControl.getListView();

		searchResult = new ArrayList<Task>();
		allResult = new ArrayList<Task>();
		pendingResult = new ArrayList<Task>();
		floatingResult = new ArrayList<Task>();
		overdueResult = new ArrayList<Task>();
		completeResult = new ArrayList<Task>();
	}

	private void initLogic() throws IOException {
		logic = new Logic();
	}

	/**
	 * Initialises the RootLayout that will contain all other JavaFX components.
	 */
	private void initRootLayout() throws IOException {

		try {
			// load root layout from fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/layouts/RootLayout.fxml"));
			rootLayout = loader.load();
			scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			rootLayout.setPadding(new Insets(0, 0, 0, 0));
			rootLayout.getStyleClass().add(0, "root");

			listenForStageInput();
			showSidebar();
			showTabs();
			showCommandBar();
			showTasks();
			initLog();
			listenerForTaskList();
			primaryStage.show();

		} catch (IOException e) {
			appLog.getLogger().warning("Unable to initialise rootLayout: " + e);
		}
	}

	/**
	 * Check to see if there are tasks to be displayed, refresh the list *******
	 */
	private void checkIsTasksEmpty() {
		populateAllPendingTasks();
		populateAllCompleteTasks();
		switchToModifiedTab();
		reinitialiseModifiedBoolean();

	}

	/**
	 * check for overdue task, refresh the list and prompt with notification if
	 * necessary
	 */
	private void checkOverdue() {
		ArrayList<Task> overdueList;
		String taskName = null;
		try {
			overdueList = logic.checkOverdue();
			if (!overdueList.isEmpty()) {
				checkIsTasksEmpty();
				taskName = locateOverdueTask(overdueList);
				if (taskName != null) {
					notification(taskName);
				}
			}
		} catch (IOException e1) {
			appLog.getLogger().warning("Unable to check overdue tasks: " + e1);
		}
	}

	/**
	 * Allows the application window be be dragged
	 */
	private void listenForStageInput() {
		rootLayout.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = primaryStage.getX() - event.getScreenX();
				yOffset = primaryStage.getY() - event.getScreenY();
			}
		});
		rootLayout.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				primaryStage.setX(event.getScreenX() + xOffset);
				primaryStage.setY(event.getScreenY() + yOffset);
			}
		});
	}

	/**
	 * Display side bar and attach it to borderpane rootlayout
	 */
	private void showSidebar() {
		// create a sidebar with some content in it.
		final Pane sidePane = createSidebarContent();
		sidebar = new SideBarController(EXPANDED_WIDTH, sidePane);
		VBox.setVgrow(sidePane, Priority.ALWAYS);
		rootLayout.setLeft(sidebar);
		sidebar.hideSidebar();
		createTopBar(sidebar);
	}

	/**
	 * Display tabs and attach it to borderpane rootlayout
	 */
	private void showTabs() {
		rootLayout.setCenter(tabControl);
	}

	/**
	 * Add TasksTableControl to specific tab
	 */
	private void showTasks() {
		tabControl.setAllTab(allTableControl);
		tabControl.setPendingTab(pendingTableControl);
		tabControl.setCompleteTab(completeTableControl);
		tabControl.setOverdueTab(overdueTableControl);
		tabControl.setFloatingTab(floatingTableControl);
	}

	/**
	 * Display command bar and attach it to borderpane rootlayout
	 */
	private void showCommandBar() {
		rootLayout.setBottom(barControl);
		barControl.getFocus();
		barControl.setBgColour("med");
	}

	/**
	 * Initialises history log for user input
	 */
	private void initLog() {
		historyLog = new ArrayList<String>();
	}

	/**
	 * Listener for various hotkey pressed
	 */
	private void listenerForTaskList() {
		listenerForThemeChange();
		listenerForBackgroundChange();
		listenerForPendingDisplay();
		listenerForAllDisplay();
		listenerForFloatingDisplay();
		listenerForOverdueDisplay();
		listenerForCompleteDisplay();
		listenerForTabControl();
	}

	/***********************
	 * sub-functions of Initialisation
	 ******************************************/
	/***********************************************************************************************/

	/**
	 * Check for overdue task every 30 seconds
	 */
	private void overdueTimer() {
		Timeline timeline;
		timeline = new Timeline(new KeyFrame(Duration.millis(30000), ae -> checkOverdue()));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	/**
	 * Locate the overdue tasks and return the number of tasks that are overdue
	 * 
	 * @param overdueList
	 *            arraylist of tasks which are overdued
	 * @return number of tasks as String
	 */
	private String locateOverdueTask(ArrayList<Task> overdueList) {
		assert overdueList != null;
		String taskName = "";
		taskName += overdueList.size();
		return taskName;
	}

	/**
	 * reinitialises the boolean value for the next usage *******
	 */
	private void reinitialiseModifiedBoolean() {
		isModifiedOverdue = false;
		isModifiedPending = false;
		isModifiedFloating = false;
		isModifiedComplete = false;
	}

	/**
	 * Populates completed tasks under completed tabs *******
	 */
	private void populateAllCompleteTasks() {
		if (logic.displayComplete().isEmpty()) {
			// if complete is empty
			tabControl.setEmptyCompleteTab();
			tabControl.setCompletedNotification(logic.displayComplete().size());
		} else {
			setupCompleteTable();
		}
	}

	/**
	 * Populates all pending tasks under all non-complete tabs *******
	 */
	private void populateAllPendingTasks() {
		if (logic.displayPending().isEmpty()) {
			// if all pending is empty
			setupNoTasksTabs();
		} else {
			// all pending is not empty
			setupIndividualTabNotification();
			populateAllTable();
		}
	}

	/**
	 * Change the focus to the particular tab that the latest task changed is
	 * belonged to*******
	 */
	private void switchToModifiedTab() {
		if (isModifiedFloating) {
			floatingTableControl.displayModified();
			tabControl.getTabPane().getSelectionModel().select(tabControl.getFloatingTab());
		} else if (isModifiedOverdue) {
			overdueTableControl.displayModified();
			tabControl.getTabPane().getSelectionModel().select(tabControl.getOverdueTab());
		}
		if (isModifiedPending) {
			pendingTableControl.displayModified();
			tabControl.getTabPane().getSelectionModel().select(tabControl.getPendingTab());
		}
		if (isModifiedComplete) {
			completeTableControl.displayModified();
			tabControl.getTabPane().getSelectionModel().select(tabControl.getCompleteTab());
		}
		barControl.getFocus();
	}

	/**
	 * Setup the complete table with complete task controller *******
	 */
	private void setupCompleteTable() {
		int completeCount = 0;
		completeTableControl.clearTask();
		completeResult.clear();
		tabControl.setCompleteTab(completeTableControl);
		for (Task temp : logic.displayComplete()) {
			completeTableControl.addTask(temp, ++completeCount, theme);
			completeResult.add(temp);
			if (temp.getLastModified()) {
				isModifiedComplete = true;
			}
		}
		tabControl.setCompletedNotification(completeCount);
	}

	/**
	 * Populates all tasks under all tab *******
	 */
	private void populateAllTable() {
		int allCount = 0;
		int overdueCount = 0;
		int pendingCount = 0;
		int floatingCount = 0;

		allResult.clear();
		pendingResult.clear();
		overdueResult.clear();
		floatingResult.clear();

		for (Task temp : logic.displayPending()) {
			allTableControl.addTask(temp, ++allCount, theme);
			allResult.add(temp);
			if (temp.getStatus() == TaskStatus.UPCOMING) {
				pendingTableControl.addTask(temp, ++pendingCount, theme);
				pendingResult.add(temp);
				if (temp.getLastModified()) {
					isModifiedPending = true;
				}
			} else if (temp.getStatus() == TaskStatus.FLOATING) {
				floatingTableControl.addTask(temp, ++floatingCount, theme);
				floatingResult.add(temp);
				if (temp.getLastModified()) {
					isModifiedFloating = true;
				}
			} else if (temp.getStatus() == TaskStatus.OVERDUE) {
				overdueTableControl.addTask(temp, ++overdueCount, theme);
				overdueResult.add(temp);
				if (temp.getLastModified()) {
					isModifiedOverdue = true;
				}
			}
		}
	}

	/**
	 * Setup notification badge for individual tab, set to invisible if there is
	 * no task under tab *******
	 */
	private void setupIndividualTabNotification() {
		int overdueCount = 0;
		int pendingCount = 0;
		int floatingCount = 0;
		allTableControl.clearTask();
		pendingTableControl.clearTask();
		overdueTableControl.clearTask();
		floatingTableControl.clearTask();

		for (Task temp : logic.displayPending()) {
			if (temp.getStatus() == TaskStatus.UPCOMING) {
				++pendingCount;
			} else if (temp.getStatus() == TaskStatus.FLOATING) {
				++floatingCount;
			} else if (temp.getStatus() == TaskStatus.OVERDUE) {
				++overdueCount;
			}
		}
		tabControl.setAllTab(allTableControl);
		setupUncompletedTabs(overdueCount, pendingCount, floatingCount);
		// notification
		tabControl.setAllNotification(logic.displayPending().size());
		tabControl.setPendingNotification(pendingCount);
		tabControl.setOverdueNotification(overdueCount);
		tabControl.setFloatingNotification(floatingCount);
	}

	/**
	 * setup all uncompleted tabs *******
	 * 
	 * @param overdueCount
	 * @param pendingCount
	 * @param floatingCount
	 */

	private void setupUncompletedTabs(int overdueCount, int pendingCount, int floatingCount) {
		if (pendingCount == 0) {
			tabControl.setPendingTab(new ImageView(new Image(INTRO_IMAGE)));
		} else {
			tabControl.setPendingTab(pendingTableControl);
		}

		if (floatingCount == 0) {
			tabControl.setFloatingTab(new ImageView(new Image(INTRO_IMAGE)));
		} else {
			tabControl.setFloatingTab(floatingTableControl);
		}

		if (overdueCount == 0) {
			tabControl.setOverdueTab(new ImageView(new Image(INTRO_IMAGE)));
		} else {
			tabControl.setOverdueTab(overdueTableControl);
		}
	}

	/**
	 * Setup no task image if there are not tasks available *******
	 */
	private void setupNoTasksTabs() {
		tabControl.setPendingTab(new ImageView(new Image(INTRO_IMAGE)));
		tabControl.setAllTab(new ImageView(new Image(INTRO_IMAGE)));
		tabControl.setOverdueTab(new ImageView(new Image(INTRO_IMAGE)));
		tabControl.setFloatingTab(new ImageView(new Image(INTRO_IMAGE)));

		tabControl.setAllNotification(logic.displayPending().size());
		tabControl.setPendingNotification(0);
		tabControl.setOverdueNotification(0);
		tabControl.setFloatingNotification(0);
	}

	/**
	 * create top bar and attach it to rootlayout *******
	 * 
	 * @param sidebar
	 *            sidebarController
	 */
	private void createTopBar(SideBarController sidebar) {
		assert sidebar != null;
		HBox toolBar = new HBox();
		HBox titleBar = new HBox();
		hBar = new HBox();
		topBar = new HBox();
		HBox leftTopBar = new HBox();

		// create icon and title
		Image icon = new Image(FLASH_LOGO);
		ImageView flashView = new ImageView(icon);
		Label empty1 = new Label(" ");
		Image imgTitle = new Image(FLASH_TITLE);
		ImageView iconView = new ImageView(imgTitle);

		titleBar.getChildren().addAll(flashView, empty1, iconView);
		titleBar.setAlignment(Pos.CENTER_LEFT);
		titleBar.setPadding(new Insets(0, 0, 0, 5));

		// create 2 app control buttons , minimise and close buttons
		HBox empty2 = new HBox();
		empty2.setPadding(new Insets(0, 0, 0, 455));
		Button closeApp = new Button();
		closeApp.getStyleClass().add("closeApp");
		exit(closeApp);

		Button minimiseApp = new Button();
		minimiseApp.getStyleClass().add("minimiseApp");
		minimiseApp.setPadding(new Insets(4, 0, 0, 0));
		minimise(minimiseApp);

		toolBar.getChildren().addAll(empty2, minimiseApp, closeApp);
		toolBar.setAlignment(Pos.TOP_RIGHT);

		hBar.getChildren().addAll(titleBar, toolBar);
		hBar.getStyleClass().add("toolBar");

		// sidebar button
		lblTitle = new Label("Pending Tasks");
		lblTitle.getStyleClass().add("lblTitle");
		lblTitle.setPadding(new Insets(0, 0, 5, 10));
		leftTopBar.getChildren().addAll(sidebar.getControlButton(), lblTitle);
		leftTopBar.setAlignment(Pos.CENTER_LEFT);

		topBar.setPadding(new Insets(5, 0, 5, 0));
		topBar.getStyleClass().add("topBar");
		HBox.setHgrow(leftTopBar, Priority.ALWAYS);
		topBar.getChildren().addAll(leftTopBar);

		VBox vTop = new VBox();
		vTop.getChildren().addAll(hBar, topBar);
		rootLayout.setTop(vTop);
	}

	/**
	 * create side bar, set up content within side bar *******
	 * 
	 * @returns pane Vbox
	 */
	private VBox createSidebarContent() {
		final VBox sidePane = new VBox();
		sidePane.getStyleClass().add("sidePane");

		VBox profile = new VBox();
		Image icon = new Image(FLASH_POINT);
		ImageView iconView = new ImageView(icon);
		iconView.setFitWidth(70);
		iconView.setPreserveRatio(true);

		Label empty = new Label();
		profile.getChildren().addAll(iconView, empty);
		profile.getStyleClass().add("profileBox");
		profile.setAlignment(Pos.CENTER);
		profile.setPadding(new Insets(10, 0, 20, 0));

		final Button btnNew = new Button();
		btnNew.getStyleClass().add("newButton");
		btnNew.setPadding(Insets.EMPTY);
		backgroundChooser(btnNew);

		final Button btnSave = new Button();
		btnSave.getStyleClass().add("saveButton");
		btnSave.setPadding(Insets.EMPTY);
		saveToLocation(btnSave);

		final Button btnMove = new Button();
		btnMove.getStyleClass().add("moveButton");
		btnMove.setPadding(Insets.EMPTY);
		moveToLocation(btnMove);

		final Button btnLoad = new Button();
		btnLoad.getStyleClass().add("loadButton");
		btnLoad.setPadding(Insets.EMPTY);
		loadFilename(btnLoad);

		final Button btnHelp = new Button();
		btnHelp.getStyleClass().add("helpButton");
		btnHelp.setPadding(Insets.EMPTY);
		showHelpPage(btnHelp);

		final Button btnExit = new Button();
		btnExit.getStyleClass().add("exitButton");
		btnExit.setPadding(Insets.EMPTY);
		exit(btnExit);
		sidePane.getChildren().addAll(profile, btnLoad, btnSave, btnMove, btnHelp, btnNew, btnExit);
		return sidePane;
	}

	/**
	 * show help page upon help button pressed *******
	 */
	private void showHelpPage(Button btnHelp) {
		btnHelp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				popOverForHelp();
				sidebar.hideSidebar();
			}
		});

	}

	/**
	 * Handles the hotkey control *******
	 * 
	 * @param commandBarController
	 * @param event
	 * @param text
	 */
	public void handleKeyPress(CommandBarController commandBarController, KeyEvent event, String text)
			throws IOException {
		assert event != null;
		assert text != null;
		assert commandBarController != null;
		if (event.getCode() == KeyCode.ENTER) {
			handleEnterPress(commandBarController, text);
			if (!text.equalsIgnoreCase(HELP_COMMAND)) {
				checkIsTasksEmpty();
			} else {
				commandBarController.setFeedback("Help Shown", Color.BLACK);
			}
		} else if ((event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) && !historyLog.isEmpty()) {
			event.consume();
			handleGetPastCommands(event);
		} else if ((event.getCode() == KeyCode.TAB)) {
			event.consume();
			if (tabControl.getAllTab().isSelected()) {
				allTableControl.controlToList();
			} else if (tabControl.getPendingTab().isSelected()) {
				pendingTableControl.controlToList();
			} else if (tabControl.getOverdueTab().isSelected()) {
				overdueTableControl.controlToList();
			} else if (tabControl.getFloatingTab().isSelected()) {
				floatingTableControl.controlToList();
			} else if (tabControl.getCompleteTab().isSelected()) {
				completeTableControl.controlToList();
			}
		} else if ((event.getCode() == KeyCode.F5)) {
			checkIsTasksEmpty();
		}
	}

	/**
	 * Handle enter key for auto complete edit function
	 * 
	 * @param display
	 */
	private void handleEnterKey(ListView<TasksItemController> display) {
		assert display != null;
		TasksItemController chosen = display.getSelectionModel().getSelectedItem();
		barControl.updateUserInput("edit " + chosen.getTaskName() + ", ");
		barControl.getFocus();
	}

	/**
	 * Handle enter key for auto complete delete function
	 * 
	 * @param display
	 */
	private void handleDeleteKey(ListView<TasksItemController> display) {
		assert display != null;
		TasksItemController chosen = display.getSelectionModel().getSelectedItem();
		barControl.updateUserInput("delete " + chosen.getTaskName());
		barControl.getFocus();
	}

	/**
	 * retrieve past user input history
	 * 
	 * @param event
	 */
	private void handleGetPastCommands(KeyEvent event) {
		assert event != null;
		String pastCommand = getPastCommandFromHistory(event.getCode());
		barControl.updateUserInput(pastCommand);
	}

	/**
	 * Handles history of user input by pressing up and down key
	 * 
	 * @param code
	 * @return
	 */
	private String getPastCommandFromHistory(KeyCode code) {
		assert code != null;
		if (code == KeyCode.DOWN) {
			return getNextCommand();
		} else if (code == KeyCode.UP) {
			return getPreviousCommand();
		} else {
			return EMPTY_STRING;
		}
	}

	/**
	 * Get the previous user input
	 * 
	 * @return
	 */
	private String getPreviousCommand() {
		if (pointer <= 0) {
			return historyLog.get(pointer);
		}
		pointer--;
		return historyLog.get(pointer);
	}

	/**
	 * Get the next user input
	 * 
	 * @return
	 */
	private String getNextCommand() {
		if (pointer < historyLog.size() - 1) {
			pointer++;
		}
		return historyLog.get(pointer);
	}

	/**
	 * Handles the user input each time user press enter *******
	 * 
	 * @param commandBarController
	 * @param event
	 * @param text
	 */
	private void handleEnterPress(CommandBarController commandBarController, String userInput) throws IOException {
		assert commandBarController != null;
		assert userInput != null;

		commandByNumber = false;
		boolean editByNumber = false;
		pointer = historyLog.size();

		if (StringUtils.isBlank(userInput)) {
			setFeedback(commandBarController, ERROR, userInput);
			return;
		}
		String[] fragments = userInput.split(" ");
		// UI related command
		if (userInput.equalsIgnoreCase(HELP_COMMAND) || fragments[COMMAND_INDEX].equalsIgnoreCase(THEME_COMMAND)) {
			handleGuiCommand(commandBarController, userInput);
		} else {
			// Non- UI related command
			int numberToChange = -1;
			// check if the user enter a number after command key word
			// handles command by number
			if (fragments.length > 1) {
				if (fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND) && !fragments[1].contains(",")) {
					setFeedback(commandBarController, ERROR, "Invalid Usage of edit");
					resetCommandBar(commandBarController, userInput);
					return;
				}
				numberToChange = checkIfNumberIsValid(fragments, numberToChange);
				// if the user delete/edit/mark/unmark by number
				if (commandByNumber) {
					numberToChange -= 1;
					if (fragments[COMMAND_INDEX].equalsIgnoreCase(DELETE_COMMAND)
							|| fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND)
							|| fragments[COMMAND_INDEX].equalsIgnoreCase(MARK_COMMAND)
							|| fragments[COMMAND_INDEX].equalsIgnoreCase(UNMARK_COMMAND)) {
						userInput = handleCommandByNumber(userInput, fragments, commandBarController, numberToChange);
					}
					// edit still goes through logic.handleusercommand
					if (fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND)) {
						commandByNumber = false;
						editByNumber = true;
					}
				}
			}
			try {
				userInput = handleCommandByTab(userInput, editByNumber, fragments);
			} catch (IndexOutOfBoundsException e) {
				isError = true;
				setFeedback(commandBarController, ERROR, "There are no available Tasks");
				appLog.getLogger().warning("" + e);
			}

			// if the user delete/edit/mark/unmark by task matching
			if (fragments[COMMAND_INDEX].equalsIgnoreCase(ADD_COMMAND)) {
				commandByNumber = false;
			}
			if (!commandByNumber) {
				handleCommandWithoutNumber(commandBarController, userInput, fragments);
			}
		}
		if (isError == false) {
			setFeedback(commandBarController, VALID, userInput);
		}
		resetCommandBar(commandBarController, userInput);
	}

	/** Handle non numbering command
	 * @param commandBarController
	 * @param userInput
	 * @param fragments
	 * @throws IOException
	 */
	private void handleCommandWithoutNumber(CommandBarController commandBarController, String userInput,
			String[] fragments) throws IOException {
		try {
			result = new ArrayList<Task>(logic.handleUserCommand(userInput, result));
		} catch (InvalidInputFormatException e) {
			isError = true;
			setFeedback(commandBarController, ERROR, e.toString());
			appLog.getLogger().warning("" + e);
		} catch (NoFileNameException e) {
			isError = true;
			setFeedback(commandBarController, ERROR, e.toString());
			appLog.getLogger().warning("" + e);
		}catch (InvalidPathException e) {
			isError = true;
			setFeedback(commandBarController, ERROR, e.toString());
			appLog.getLogger().warning("" + e);
		}
		if (fragments[COMMAND_INDEX].equalsIgnoreCase(SHOW_COMMAND)
				|| fragments[COMMAND_INDEX].equalsIgnoreCase(SHOWCOMPLETE_COMMAND)) {
			try{
			if (result.size() != 0) {
				popOverForShow(commandBarController, fragments[1]);
			}
			}catch(NullPointerException e){
				appLog.getLogger().warning("" + e);
			}
		}
	}

	/** Check if the number for command is valid
	 * @param fragments
	 * @param numberToChange
	 * @return int
	 */
	private int checkIfNumberIsValid(String[] fragments, int numberToChange) {
		if (fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND) && fragments[1].contains(",")) {
			fragments[1] = fragments[1].substring(0, fragments[1].indexOf(','));
		}
		try {
			numberToChange = Integer.parseInt(fragments[1]);
			commandByNumber = true;
		} catch (NumberFormatException e) {
			commandByNumber = false;
		}
		return numberToChange;
	}

	/** Reset the command bar after user press enter
	 * @param commandBarController
	 * @param userInput
	 */
	private void resetCommandBar(CommandBarController commandBarController, String userInput) {
		historyLog.add(userInput);
		pointer++;
		isError = false;
		new CommandBarController();
		commandBarController.clear();
	}

	/** Only handles GUI related command such as help and theme
	 * @param commandBarController
	 * @param userInput
	 */
	private void handleGuiCommand(CommandBarController commandBarController, String userInput) {
		if (userInput.equalsIgnoreCase(HELP_COMMAND)) {
			popOverForHelp();
			setFeedback(commandBarController, VALID, userInput);
		} else if (userInput.equalsIgnoreCase("theme blue")) {
			changeBlueTheme();
			setFeedback(commandBarController, VALID, userInput);
		} else if (userInput.equalsIgnoreCase("theme red")) {
			changeRedTheme();
			setFeedback(commandBarController, VALID, userInput);
		} else if (userInput.equalsIgnoreCase("theme transparent")) {
			changeTransparentTheme();
			setFeedback(commandBarController, VALID, userInput);
		} else if (userInput.equalsIgnoreCase("theme green")) {
			changeGreenTheme();
			setFeedback(commandBarController, VALID, userInput);
		} else if (userInput.equalsIgnoreCase(SWITCH_COMMAND)) {
			handleSwitchCommand(commandBarController, userInput);
		}
	}

	/**
	 * Handles command by the tab user is in, segregate content
	 * 
	 * @param userInput
	 * @param editByNumber
	 * @param fragments
	 * @return
	 */
	private String handleCommandByTab(String userInput, boolean editByNumber, String[] fragments)
			throws IndexOutOfBoundsException {
		assert userInput != null;
		assert fragments != null;

		// delete from complete tab
		if (userInput.equalsIgnoreCase(CLEAR_COMMAND)) {
			userInput = handleClearByTab(userInput);
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(DELETE_COMMAND)
				&& tabControl.getCompleteTab().isSelected()) {
			userInput = appendDeleteComplete(fragments);
			// show from complete tab
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(SHOW_COMMAND)
				&& tabControl.getCompleteTab().isSelected()) {
			userInput = appendShowComplete(fragments);
			// sort from complete tab
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(SORT_COMMAND)
				&& tabControl.getCompleteTab().isSelected()) {
			userInput = appendSortComplete(fragments);
			// edit by partial match
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND)) {
			if (editByNumber == false) {
				userInput = handleEditByPartialMatching(userInput);
			}
		}
		return userInput;
	}

	/**
	 * Handle user command by number instead of task name
	 * 
	 * @param userInput
	 * @param fragments
	 * @param commandBarController
	 * @param numberToChange
	 * @return
	 */
	private String handleCommandByNumber(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) {
		assert commandBarController != null;
		assert fragments != null;
		assert userInput != null;

		if (tabControl.getAllTab().isSelected()) {
			if (numberToChange >= allResult.size() || numberToChange < 0) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			} else {
				try {
					userInput = validHandleCommandByNumberAllTab(userInput, fragments, commandBarController,
							numberToChange);
				} catch (IOException e) {
					appLog.getLogger().warning("Unable to handle command by number: " + e);
				}
			}
		} else if (tabControl.getPendingTab().isSelected()) {
			if (numberToChange >= pendingResult.size() || numberToChange < 0) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			}
			try {
				userInput = validHandleCommandByNumberPendingTab(userInput, fragments, commandBarController,
						numberToChange);
			} catch (IOException e) {
				appLog.getLogger().warning("Unable to handle Command by number in pending tab: " + e);
			}
		} else if (tabControl.getFloatingTab().isSelected()) {
			if (numberToChange >= floatingResult.size() || numberToChange < 0) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			}
			try {
				userInput = validHandleCommandByNumberFloatingTab(userInput, fragments, commandBarController,
						numberToChange);
			} catch (IOException e) {
				appLog.getLogger().warning("Unable to handle Command by number in floating tab: " + e);
			}
		} else if (tabControl.getOverdueTab().isSelected()) {
			if (numberToChange >= overdueResult.size() || numberToChange <= 0) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			}
			try {
				userInput = validHandleCommandByNumberOverdueTab(userInput, fragments, commandBarController,
						numberToChange);
			} catch (IOException e) {
				appLog.getLogger().warning("Unable to handle Command by number in overdue tab: " + e);
			}
		} else if (tabControl.getCompleteTab().isSelected()) {
			if (numberToChange >= completeResult.size() || numberToChange < 0) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			}
			try {
				userInput = validHandleCommandByNumberCompleteTab(userInput, fragments, commandBarController,
						numberToChange);
			} catch (IOException e) {
				appLog.getLogger().warning("Unable to handle Command by number in complete tab: " + e);
			}
		}

		return userInput;
	}

	/**
	 * Handles valid user command by number in the complete tab
	 * 
	 * @param userInput
	 * @param fragments
	 * @param commandBarController
	 * @param numberToChange
	 * @return
	 * @throws IOException
	 */
	private String validHandleCommandByNumberCompleteTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws IOException {
		assert commandBarController != null;
		assert userInput != null;
		assert fragments != null;

		if (fragments[COMMAND_INDEX].equalsIgnoreCase(DELETE_COMMAND)) {
			logic.delete(completeResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(MARK_COMMAND)) {
			invalidCommandUnderTab(commandBarController);
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(UNMARK_COMMAND)) {
			logic.unmark(completeResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND)) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();
			userInput = "edit " + completeResult.get(numberToChange).getTask() + ", " + update;
		}
		return userInput;
	}

	/**
	 * Handles valid user command by number in overdue tab
	 * 
	 * @param userInput
	 * @param fragments
	 * @param commandBarController
	 * @param numberToChange
	 * @return
	 * @throws IOException
	 */
	private String validHandleCommandByNumberOverdueTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws IOException {
		assert commandBarController != null;
		assert userInput != null;
		assert fragments != null;

		if (fragments[COMMAND_INDEX].equalsIgnoreCase(DELETE_COMMAND)) {
			logic.delete(overdueResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(MARK_COMMAND)) {
			logic.mark(overdueResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(UNMARK_COMMAND)) {
			invalidCommandUnderTab(commandBarController);
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND)) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();
			userInput = "edit " + overdueResult.get(numberToChange).getTask() + ", " + update;
		}
		return userInput;
	}

	/**
	 * Handles the valid user command by number in floating tab
	 * 
	 * @param userInput
	 * @param fragments
	 * @param commandBarController
	 * @param numberToChange
	 * @return
	 * @throws IOException
	 */
	private String validHandleCommandByNumberFloatingTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws IOException {
		assert commandBarController != null;
		assert userInput != null;
		assert fragments != null;

		if (fragments[COMMAND_INDEX].equalsIgnoreCase(DELETE_COMMAND)) {
			logic.delete(floatingResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(MARK_COMMAND)) {
			logic.mark(floatingResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(UNMARK_COMMAND)) {
			invalidCommandUnderTab(commandBarController);
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND)) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();
			userInput = "edit " + floatingResult.get(numberToChange).getTask() + ", " + update;
		}
		return userInput;
	}

	/**
	 * Handles the valid user command by number in pending tab
	 * 
	 * @param userInput
	 * @param fragments
	 * @param commandBarController
	 * @param numberToChange
	 * @return
	 * @throws IOException
	 */
	private String validHandleCommandByNumberPendingTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws IOException {
		assert commandBarController != null;
		assert userInput != null;
		assert fragments != null;

		if (fragments[COMMAND_INDEX].equalsIgnoreCase(DELETE_COMMAND)) {
			logic.delete(pendingResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(MARK_COMMAND)) {
			logic.mark(pendingResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(UNMARK_COMMAND)) {
			invalidCommandUnderTab(commandBarController);
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND)) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();
			userInput = "edit " + pendingResult.get(numberToChange).getTask() + ", " + update;
		}
		return userInput;
	}

	/**
	 * Handles valid user command by number in all tab
	 * 
	 * @param userInput
	 * @param fragments
	 * @param commandBarController
	 * @param numberToChange
	 * @return
	 * @throws IOException
	 */
	private String validHandleCommandByNumberAllTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws IOException {
		assert commandBarController != null;
		assert userInput != null;
		assert fragments != null;

		if (fragments[COMMAND_INDEX].equalsIgnoreCase(DELETE_COMMAND)) {
			logic.delete(allResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(MARK_COMMAND)) {
			logic.mark(allResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(UNMARK_COMMAND)) {
			invalidCommandUnderTab(commandBarController);
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND)) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();
			userInput = "edit " + allResult.get(numberToChange).getTask() + ", " + update;
		}
		return userInput;
	}

	/**
	 * Handles invalid command under tab
	 * 
	 * @param commandBarController
	 */
	private void invalidCommandUnderTab(CommandBarController commandBarController) {
		assert commandBarController != null;
		setFeedback(commandBarController, ERROR, "Command not allowed.");
		isError = true;
	}

	/**
	 * Handles invalid command by numbers
	 * 
	 * @param commandBarController
	 */
	private void invalidHandleCommandByNumber(CommandBarController commandBarController) {
		assert commandBarController != null;
		setFeedback(commandBarController, ERROR, "a Invalid Index entered.");
		isError = true;
	}

	/**
	 * Handles edit by partial matching
	 * 
	 * @param userInput
	 * @return
	 */
	private String handleEditByPartialMatching(String userInput) throws IndexOutOfBoundsException {
		assert userInput != null;

		String update = userInput.substring(userInput.indexOf(',') + 1).trim();
		if (tabControl.getAllTab().isSelected()) {
			userInput = "edit " + allResult.get(0).getTask() + ", " + update;
		} else if (tabControl.getPendingTab().isSelected()) {
			userInput = "edit " + pendingResult.get(0).getTask() + ", " + update;
		} else if (tabControl.getFloatingTab().isSelected()) {
			userInput = "edit " + floatingResult.get(0).getTask() + ", " + update;
		} else if (tabControl.getOverdueTab().isSelected()) {
			userInput = "edit " + overdueResult.get(0).getTask() + ", " + update;
		}
		return userInput;
	}

	/**
	 * Append complete keyword to sort when under complete tab
	 * 
	 * @param fragments
	 * @return
	 */
	private String appendSortComplete(String[] fragments) {
		assert fragments != null;

		String userInput;
		fragments[COMMAND_INDEX] = "sortComplete";
		String sortComplete = "sortComplete ";
		for (int i = 1; i < fragments.length; i++) {
			sortComplete += fragments[i];
		}
		userInput = sortComplete;
		return userInput;
	}

	/**
	 * Append complete keyword to show in complete tab
	 * 
	 * @param fragments
	 * @return
	 */
	private String appendShowComplete(String[] fragments) {
		assert fragments != null;

		String userInput;
		fragments[COMMAND_INDEX] = "showComplete";
		String showComplete = "showComplete ";
		for (int i = 1; i < fragments.length; i++) {
			showComplete += fragments[i];
		}
		userInput = showComplete;
		return userInput;
	}

	/**
	 * Append complete keyword to delete in complete tab
	 * 
	 * @param fragments
	 * @return
	 */
	private String appendDeleteComplete(String[] fragments) {
		assert fragments != null;

		String userInput;
		fragments[COMMAND_INDEX] = "deleteComplete";
		String deleteComplete = "deleteComplete ";
		for (int i = 1; i < fragments.length; i++) {
			deleteComplete += fragments[i];
		}
		userInput = deleteComplete;
		return userInput;
	}

	/**
	 * Handles clear command by tab
	 * 
	 * @param userInput
	 * @return
	 */
	private String handleClearByTab(String userInput) {
		assert userInput != null;

		if (tabControl.getPendingTab().isSelected()) {
			userInput = userInput + "Upcoming";
		} else if (tabControl.getCompleteTab().isSelected()) {
			userInput = userInput + "Complete";
		} else if (tabControl.getOverdueTab().isSelected()) {
			userInput = userInput + "Overdue";
		} else if (tabControl.getFloatingTab().isSelected()) {
			userInput = userInput + "Floating";
		} else if (tabControl.getAllTab().isSelected()) {
			userInput = userInput + "All";
		}
		return userInput;
	}

	/**
	 * Handles switch command
	 * 
	 * @param commandBarController
	 * @param userInput
	 */
	private void handleSwitchCommand(CommandBarController commandBarController, String userInput) {
		assert commandBarController != null;
		assert userInput != null;

		if (tabControl.getPendingTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getOverdueTab());
			lblTitle.setText("Overdue Tasks");
			setFeedback(commandBarController, VALID, userInput);
		} else if (tabControl.getOverdueTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getCompleteTab());
			lblTitle.setText("Completed Tasks");
			setFeedback(commandBarController, VALID, userInput);
		} else if (tabControl.getCompleteTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getAllTab());
			lblTitle.setText("All Tasks");
			setFeedback(commandBarController, VALID, userInput);
		} else if (tabControl.getAllTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getFloatingTab());
			lblTitle.setText("Floating Tasks");
			setFeedback(commandBarController, VALID, userInput);
		} else if (tabControl.getFloatingTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getPendingTab());
			lblTitle.setText("Pending Tasks");
			setFeedback(commandBarController, VALID, userInput);
		}
	}

	/**
	 * Display popOver for show command
	 * 
	 * @param commandBarController
	 * @param title
	 */
	private void popOverForShow(CommandBarController commandBarController, String title) {
		assert commandBarController != null;
		assert title != null;

		PopOver bgPopOver = new PopOver();
		bgPopOver.detach();
		bgPopOver.setDetachable(true);
		bgPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
		bgPopOver.setArrowIndent(5);
		int count = 0;
		TasksTableController popOverTableControl = new TasksTableController();
		popOverTableControl.clearTask();
		for (Task temp : result) {
			popOverTableControl.addTask(temp, ++count, theme);
		}
		VBox vbox = new VBox();
		Label lblTitle = new Label(title);
		lblTitle.getStyleClass().add("showTitle");
		vbox.getChildren().addAll(lblTitle, popOverTableControl);
		vbox.setAlignment(Pos.CENTER);
		bgPopOver.setContentNode(vbox);
		bgPopOver.show(tabControl.getScene().getWindow(),getPopupPosition(tabControl).getX(),
				getPopupPosition(tabControl).getY());
	}

	/**
	 * Display popOver for help command
	 */
	private void popOverForHelp() {
		PopOver helpPopOver = new PopOver();
		helpPopOver.setDetachable(true);
		helpPopOver.detach();
		helpPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
		helpPopOver.setArrowIndent(5);
		helpPopOver.setContentNode(new ImageView(new Image("/main/resources/images/help.png")));
		helpPopOver.show(tabControl.getScene().getWindow(),getPopupPosition(tabControl).getX(),
				getPopupPosition(tabControl).getY());
	}

	/**
	 * Change theme to red colour
	 */
	private void changeRedTheme() {
		topBar.getStyleClass().clear();
		hBar.getStyleClass().clear();
		topBar.getStyleClass().add("topBar");
		hBar.getStyleClass().add("toolBar");
		tabControl.getStylesheets().clear();
		tabControl.getStylesheets().add("/main/resources/styles/stylesheet.css");
		theme = "red";
		pendingTableControl.setTheme("red");
	}

	/**
	 * Change theme to green colour
	 */
	private void changeGreenTheme() {
		topBar.getStyleClass().clear();
		hBar.getStyleClass().clear();
		topBar.getStyleClass().add("greenTopBar");
		hBar.getStyleClass().add("greenToolBar");
		tabControl.getStylesheets().clear();
		tabControl.getStylesheets().add("/main/resources/styles/green.css");
		theme = "green";
		pendingTableControl.setTheme("green");
	}

	/**
	 * Change theme to transparent colour
	 */
	private void changeTransparentTheme() {
		topBar.getStyleClass().clear();
		hBar.getStyleClass().clear();
		topBar.getStyleClass().add("orangeTopBar");
		hBar.getStyleClass().add("orangeToolBar");
		tabControl.getStylesheets().clear();
		tabControl.getStylesheets().add("/main/resources/styles/orange.css");
		theme = "transparent";
		pendingTableControl.setTheme("transparent");
	}

	/**
	 * Change theme to blue colour
	 */
	private void changeBlueTheme() {
		topBar.getStyleClass().clear();
		topBar.getStyleClass().add("blueTopBar");
		hBar.getStyleClass().clear();
		hBar.getStyleClass().add("blueToolBar");
		tabControl.getStylesheets().clear();
		tabControl.getStylesheets().add("/main/resources/styles/blue.css");
		theme = "blue";
		pendingTableControl.setTheme("blue");
	}

	/******************************
	 * set user feedback
	 *****************************/
	/*******************************************************************************/

	/***
	 * set user feedback to display to user *******
	 * 
	 * @param commandBarController
	 * @param type
	 * @param userInput
	 */
	private void setFeedback(CommandBarController commandBarController, String type, String userInput) {
		assert commandBarController != null;
		assert type != null;
		assert userInput != null;

		isFeedback = true;
		// if the userInput is not a one word command
		if (userInput.indexOf(' ') != -1) {
			setFeedbackNonOneWordCommand(commandBarController, type, userInput);
		} else if (userInput.indexOf(' ') == -1) {
			setFeedbackOneWordCommand(commandBarController, type, userInput);
		}
	}

	/**
	 * Set feedback for one word command such as "clear"
	 * 
	 * @param commandBarController
	 * @param type
	 * @param userInput
	 */
	private void setFeedbackOneWordCommand(CommandBarController commandBarController, String type, String userInput) {
		assert commandBarController != null;
		assert type != null;
		assert userInput != null;

		if (type.equals(ERROR)) {
			commandBarController.setFeedback("Invalid Command" + ": ", Color.RED);
		} else if (type.equals(VALID)) {
			if (userInput.equalsIgnoreCase(UNDO_COMMAND)) {
				commandBarController.setFeedback("Previous command has been undone", Color.BLACK);
			} else if (userInput.equalsIgnoreCase(REDO_COMMAND)) {
				commandBarController.setFeedback("Previous Change has been restored", Color.BLACK);
			} else if (userInput.equalsIgnoreCase(HELP_COMMAND)) {
				commandBarController.setFeedback("Help shown", Color.BLACK);
			} else if (userInput.equalsIgnoreCase(CLEARUPCOMING_COMMAND)
					|| userInput.equalsIgnoreCase(CLEARCOMPLETE_COMMAND)
					|| userInput.equalsIgnoreCase(CLEAROVERDUE_COMMAND)
					|| userInput.equalsIgnoreCase(CLEARFLOATING_COMMAND)
					|| userInput.equalsIgnoreCase(CLEARALL_COMMAND)) {
				commandBarController.setFeedback("All tasks have been cleared", Color.BLACK);
			} else if (userInput.equalsIgnoreCase(SWITCH_COMMAND)) {
				if (tabControl.getPendingTab().isSelected()) {
					commandBarController.setFeedback("Switched to pending tab", Color.BLACK);
				} else if (tabControl.getCompleteTab().isSelected()) {
					commandBarController.setFeedback("Switched to completed tab", Color.BLACK);
				} else if (tabControl.getFloatingTab().isSelected()) {
					commandBarController.setFeedback("Switched to floating tab", Color.BLACK);
				} else if (tabControl.getOverdueTab().isSelected()) {
					commandBarController.setFeedback("Switched to overdue tab", Color.BLACK);
				} else if (tabControl.getAllTab().isSelected()) {
					commandBarController.setFeedback("Switched to all tab", Color.BLACK);
				}
			}
		}
	}

	/**
	 * Set feedback for non-one word command such as "delete sth"
	 * 
	 * @param commandBarController
	 * @param type
	 * @param userInput
	 */
	private void setFeedbackNonOneWordCommand(CommandBarController commandBarController, String type,
			String userInput) {
		assert commandBarController != null;
		assert type != null;
		assert userInput != null;

		int i = 1;
		i = userInput.indexOf(' ');
		String firstWord = userInput.substring(0, i);
		String subString = userInput.substring(i + 1);
		if (type.equals(ERROR)) {
			commandBarController.setFeedback("Invalid Command" + ": " + subString, Color.RED);
		} else if (type.equals(VALID)) {
			if (isTasksCommand(firstWord)) {
				if (firstWord.equalsIgnoreCase(DELETE_COMMAND) || firstWord.equalsIgnoreCase(DELETECOMPLETE_COMMAND)) {
					firstWord = "delete";
					commandBarController.setFeedback("Task has been successfully " + firstWord + "d", Color.BLACK);
				} else {
					commandBarController.setFeedback("Task has been successfully " + firstWord + "ed", Color.BLACK);
				}
			} else if (firstWord.equalsIgnoreCase(SHOW_COMMAND)) {
				commandBarController.setFeedback("Task has been successfully " + firstWord + "n ", Color.BLACK);
			} else if (firstWord.equalsIgnoreCase(SHOWCOMPLETE_COMMAND)) {
				firstWord = "show";
				commandBarController.setFeedback("Task has been successfully " + firstWord + "n ", Color.BLACK);
			} else if (firstWord.equalsIgnoreCase(SORT_COMMAND)) {
				commandBarController.setFeedback("Task has been successfully " + firstWord + "ed ", Color.BLACK);
			} else if (firstWord.equalsIgnoreCase(OPEN_COMMAND)) {
				commandBarController.setFeedback("File has been successfully " + firstWord + "ed ", Color.BLACK);
			} else if (firstWord.equalsIgnoreCase(SAVE_COMMAND) || firstWord.equalsIgnoreCase(MOVE_COMMAND)) {
				commandBarController.setFeedback("File has been successfully " + firstWord + "d ", Color.BLACK);
			} else if (firstWord.equalsIgnoreCase(THEME_COMMAND)) {
				commandBarController.setFeedback(subString + " " + firstWord + " has been activated", Color.BLACK);
			} else {
				commandBarController.setFeedback("Invalid Command", Color.RED);
			}
		}
	}

	/**
	 * check if it is a valid task command
	 * 
	 * @param firstWord
	 * @return
	 */
	private boolean isTasksCommand(String firstWord) {
		assert firstWord != null;

		if (firstWord.equalsIgnoreCase(MARK_COMMAND) || firstWord.equalsIgnoreCase(UNMARK_COMMAND)
				|| firstWord.equalsIgnoreCase(ADD_COMMAND) || firstWord.equalsIgnoreCase(DELETE_COMMAND)
				|| firstWord.equalsIgnoreCase(DELETECOMPLETE_COMMAND) || firstWord.equalsIgnoreCase(EDIT_COMMAND)
				|| firstWord.equalsIgnoreCase(HELP_COMMAND)) {
			return true;
		}
		return false;

	}

	/**
	 * Check and return the first keyword of user input
	 * 
	 * @param input
	 * @return
	 */
	public static String firstWord(String input) {
		assert input != null;

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

	/**
	 * Remove all the style from commandbar controller
	 * 
	 * @param n
	 */
	private void removeAllStyle(Node n) {
		assert n != null;
		n.getStyleClass().removeAll("bad", "med", "good", "best");
	}

	/******************************
	 * Start of live search
	 *************************/
	/*****************************************************************************/

	/***
	 * Listen for the user Input character by character *******
	 * 
	 * @param newValue
	 */
	public void liveSearch(String newValue) {
		assert newValue != null;
		// if user enter space, return and start again
		if (StringUtils.isBlank(newValue)) {
			return;
		}
		String[] fragments = null;
		fragments = newValue.split(SPLIT);

		boolean isEdit = fragments[COMMAND_INDEX].equalsIgnoreCase(EDIT_COMMAND);
		boolean isDelete = fragments[COMMAND_INDEX].equalsIgnoreCase(DELETE_COMMAND);
		boolean isSearch = fragments[COMMAND_INDEX].equalsIgnoreCase(SEARCH_COMMAND);
		boolean isMark = fragments[COMMAND_INDEX].equalsIgnoreCase(MARK_COMMAND);
		boolean isUnmark = fragments[COMMAND_INDEX].equalsIgnoreCase(UNMARK_COMMAND);

		// check if it is command by number
		int number = -1;
		boolean commandByNumbers = false;
		if (fragments.length > 1) {
			try {
				number = Integer.parseInt(fragments[1].substring(0, 1));
				commandByNumbers = true;
			} catch (NumberFormatException e) {
				commandByNumbers = false;
			}
		}
		// if not command by number, live search, else if it is command by
		// number, stop live search
		if (!commandByNumbers) {
			try {
				if ((tabControl.getAllTab().isSelected()) && (isEdit || isDelete || isSearch || isMark || isUnmark)) {
					liveSearchAllTab(newValue, isEdit, isDelete, isSearch, isMark);
				} else if ((tabControl.getPendingTab().isSelected())
						&& (isEdit || isDelete || isSearch || isMark || isUnmark)) {
					liveSearchPendingTab(newValue, isEdit, isDelete, isSearch, isMark);
				} else if ((tabControl.getFloatingTab().isSelected())
						&& (isEdit || isDelete || isSearch || isMark || isUnmark)) {
					liveSearchFloatingTab(newValue, isEdit, isDelete, isSearch, isMark);
				} else if ((tabControl.getOverdueTab().isSelected())
						&& (isEdit || isDelete || isSearch || isMark || isUnmark)) {
					liveSearchOverdueTab(newValue, isEdit, isDelete, isSearch, isMark);
				} else if ((tabControl.getCompleteTab().isSelected()) && isDelete || isSearch || isUnmark) {
					liveSearchCompleteTab(newValue, isDelete, isSearch, isUnmark);
				}
			} catch (Exception e) {
				appLog.getLogger().warning("" + e);
			}
		}

	}

	/***
	 * Handles command keyword validity, valid keyword will be shown in
	 * green,invalid in red *******
	 * 
	 * @param newValue
	 */
	public void showColourCommand(String newValue) {
		assert newValue != null;
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

	/**************************
	 * Live search sub method
	 *********************************/

	/**
	 * Handles live search in complete tab
	 * 
	 * @param newValue
	 * @param isDelete
	 * @param isSearch
	 * @param isUnmark
	 * @throws Exception
	 */
	private void liveSearchCompleteTab(String newValue, boolean isDelete, boolean isSearch, boolean isUnmark)
			throws Exception {
		searchResult = logic.handleSearchCompleted(newValue);
		if (isDelete || isSearch) {
			populateCompleteList(searchResult);
		} else if (isUnmark) {
			populateCompleteList(searchResult);
			populateAllList(logic.displayPending());
			populatePendingList(logic.displayPending());
			populateOverdueList(logic.displayPending());
			populateFloatingList(logic.displayPending());
		}
	}

	/**
	 * Handles live search in overdue tab
	 * 
	 * @param newValue
	 * @param isEdit
	 * @param isDelete
	 * @param isSearch
	 * @param isMark
	 * @throws Exception
	 */
	private void liveSearchOverdueTab(String newValue, boolean isEdit, boolean isDelete, boolean isSearch,
			boolean isMark) throws Exception {
		searchResult = logic.handleSearchPending(newValue);
		if (isEdit || isDelete || isSearch) {
			populateOverdueList(searchResult);
		} else if (isMark) {
			populateOverdueList(searchResult);
			populateCompleteList(logic.displayComplete());
		}
	}

	/**
	 * Handles live search in floating tab
	 * 
	 * @param newValue
	 * @param isEdit
	 * @param isDelete
	 * @param isSearch
	 * @param isMark
	 * @throws Exception
	 */
	private void liveSearchFloatingTab(String newValue, boolean isEdit, boolean isDelete, boolean isSearch,
			boolean isMark) throws Exception {
		searchResult = logic.handleSearchPending(newValue);
		if (isEdit || isDelete || isSearch) {
			populateFloatingList(searchResult);
		} else if (isMark) {
			populateFloatingList(searchResult);
			populateCompleteList(logic.displayComplete());
		}
	}

	/**
	 * Handles live search in pending tab
	 * 
	 * @param newValue
	 * @param isEdit
	 * @param isDelete
	 * @param isSearch
	 * @param isMark
	 * @throws Exception
	 */
	private void liveSearchPendingTab(String newValue, boolean isEdit, boolean isDelete, boolean isSearch,
			boolean isMark) throws Exception {
		searchResult = logic.handleSearchPending(newValue);
		if (isEdit || isDelete || isSearch) {
			populatePendingList(searchResult);
		} else if (isMark) {
			populatePendingList(searchResult);
			populateCompleteList(logic.displayComplete());
		}
	}

	/**
	 * handles live search in all tab
	 * 
	 * @param newValue
	 * @param isEdit
	 * @param isDelete
	 * @param isSearch
	 * @param isMark
	 * @throws Exception
	 */
	private void liveSearchAllTab(String newValue, boolean isEdit, boolean isDelete, boolean isSearch, boolean isMark)
			throws Exception {
		searchResult = logic.handleSearchPending(newValue);
		if (isEdit || isDelete || isSearch) {
			populateAllList(searchResult);
		} else if (isMark) {
			populateAllList(searchResult);
			populateCompleteList(logic.displayComplete());
		}
	}

	/**
	 * Populate all task list in all tab
	 * 
	 * @param searchResult
	 */
	private void populateAllList(ArrayList<Task> searchResult) {
		assert searchResult != null;
		allTableControl.clearTask();
		allResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			allTableControl.addTask(temp, ++count, theme);
			allResult.add(temp);
		}

	}

	/**
	 * Populates overdue task list in overdue tab
	 * 
	 * @param searchResult
	 */
	private void populateOverdueList(ArrayList<Task> searchResult) {
		assert searchResult != null;
		overdueTableControl.clearTask();
		overdueResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			if (temp.getStatus() == TaskStatus.OVERDUE) {
				overdueTableControl.addTask(temp, ++count, theme);
				overdueResult.add(temp);
			}
		}
	}

	/**
	 * Populates floating task list in floating tab
	 * 
	 * @param searchResult
	 */
	private void populateFloatingList(ArrayList<Task> searchResult) {
		assert searchResult != null;
		floatingTableControl.clearTask();
		floatingResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			if (temp.getStatus() == TaskStatus.FLOATING) {
				floatingTableControl.addTask(temp, ++count, theme);
				floatingResult.add(temp);
			}
		}
	}

	/**
	 * Populates complete task list in complete tab
	 * 
	 * @param searchResult
	 */
	private void populateCompleteList(ArrayList<Task> searchResult) {
		assert searchResult != null;
		completeTableControl.clearTask();
		completeResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			if (temp.getStatus() == TaskStatus.COMPLETED) {
				completeTableControl.addTask(temp, ++count, theme);
				completeResult.add(temp);
			}
		}
	}

	/**
	 * Populates pending task list in pending tab
	 * 
	 * @param searchResult
	 */
	private void populatePendingList(ArrayList<Task> searchResult) {
		assert searchResult != null;
		pendingTableControl.clearTask();
		pendingResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			if (temp.getStatus() == TaskStatus.UPCOMING) {
				pendingTableControl.addTask(temp, ++count, theme);
				pendingResult.add(temp);
			}
		}
	}

	/********************************
	 * End of Live Search
	 ***************************************/

	/**
	 * Handles save function for btnSave
	 * 
	 * @param btnSave
	 */
	private void saveToLocation(Button btnSave) {
		assert btnSave != null;
		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Documents (*.txt)",
						"*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File saveFile = fileChooser.showSaveDialog(null);
				if (saveFile != null) {
					try {
						logic.saveToLocation(saveFile.getAbsolutePath());
					} catch (Exception e) {
						setFeedback(barControl, ERROR, "file path not recognised");
						appLog.getLogger().warning("File path not recognised: " + e);
					}
				}
				sidebar.hideSidebar();
			}
		});

	}

	/**
	 * Handles move function for btnMove
	 * 
	 * @param btnMove
	 */
	private void moveToLocation(Button btnMove) {
		assert btnMove != null;
		btnMove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Documents (*.txt)",
						"*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File saveFile = fileChooser.showSaveDialog(null);
				if (saveFile != null) {
					try {
						logic.moveToLocation(saveFile.getAbsolutePath());
					} catch (Exception e) {
						setFeedback(barControl, ERROR, "file path not recognised");
						appLog.getLogger().warning("File path not recognised: " + e);
					}
				}
				sidebar.hideSidebar();
			}
		});

	}

	/**
	 * Handles load function for btnLoad
	 * 
	 * @param btnLoad
	 */

	private void loadFilename(Button btnLoad) {
		assert btnLoad != null;
		btnLoad.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File loadFile = fileChooser.showOpenDialog(null);
				if (loadFile != null) {
					try {
						logic.loadFilename(loadFile.getAbsolutePath());
					} catch (IOException e1) {
						appLog.getLogger().warning("File path not recognised: " + e1);
					}
					try {
						checkIsTasksEmpty();
					} catch (Exception e) {
						setFeedback(barControl, ERROR, "Unable to refresh task list");
						appLog.getLogger().warning("Unable to refresh task list: " + e);
					}
				}
				sidebar.hideSidebar();
			}
		});
	}

	/************************************
	 * Various listener for hotkeys
	 **********************************************/

	/**
	 * Listens for tab changes and heading display
	 */
	private void listenerForTabControl() {
		tabControl.getTabPane().setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (tabControl.getPendingTab().isSelected()) {
					lblTitle.setText("Pending Tasks");
				} else if (tabControl.getCompleteTab().isSelected()) {
					lblTitle.setText("Completed Tasks");
				} else if (tabControl.getOverdueTab().isSelected()) {
					lblTitle.setText("Overdue Tasks");
				} else if (tabControl.getFloatingTab().isSelected()) {
					lblTitle.setText("Floating Tasks");
				} else if (tabControl.getAllTab().isSelected()) {
					lblTitle.setText("All Tasks");
				}
			}

		});
	}

	/**
	 * Listens for user key press in complete tab
	 */
	private void listenerForCompleteDisplay() {
		completeDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					handleEnterKey(completeDisplay);
				} else if (e.getCode() == KeyCode.ESCAPE) {
					try {
						populateCompleteList(logic.displayComplete());
					} catch (Exception e1) {
						appLog.getLogger().warning("Unable to populate complete list: " + e1);
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(completeDisplay);
				}
			}

		});
	}

	/**
	 * Listens for user key press in overdue tab
	 */
	private void listenerForOverdueDisplay() {
		overdueDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					handleEnterKey(overdueDisplay);
				} else if (e.getCode() == KeyCode.ESCAPE) {
					try {
						populateOverdueList(logic.displayPending());
					} catch (Exception e1) {
						appLog.getLogger().warning("Unable to populate complete list: " + e1);
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(overdueDisplay);
				}
			}

		});
	}

	/**
	 * Listens for user key press in floating tab
	 */
	private void listenerForFloatingDisplay() {
		floatingDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					handleEnterKey(floatingDisplay);
				} else if (e.getCode() == KeyCode.ESCAPE) {
					try {
						populateFloatingList(logic.displayPending());
					} catch (Exception e1) {
						appLog.getLogger().warning("Unable to populate complete list: " + e1);
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(floatingDisplay);
				}
			}

		});
	}

	/**
	 * Listens for user key press in all tab
	 */
	private void listenerForAllDisplay() {
		allDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					handleEnterKey(allDisplay);
				} else if (e.getCode() == KeyCode.ESCAPE) {
					try {
						populateAllList(logic.displayPending());
					} catch (Exception e1) {
						appLog.getLogger().warning("Unable to populate complete list: " + e1);
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(allDisplay);
				}
			}

		});
	}

	/**
	 * Listens for user key press in pending tab
	 */
	private void listenerForPendingDisplay() {
		pendingDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					handleEnterKey(pendingDisplay);
				} else if (e.getCode() == KeyCode.ESCAPE) {
					try {
						populatePendingList(logic.displayPending());
						tabControl.setPendingTab(pendingTableControl);
					} catch (Exception e1) {
						appLog.getLogger().warning("Unable to populate complete list: " + e1);
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(pendingDisplay);
				}
			}

		});
	}

	/**
	 * Listens for user key press for background changes
	 */
	private void listenerForBackgroundChange() {
		final KeyCombination keyComb2 = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);
		scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (keyComb2.match(event)) {
					if (background.equals("paris")) {
						rootLayout.getStyleClass().remove(0);
						rootLayout.getStyleClass().add(0, "rootBlack");
						background = "black";
					} else if (background.equals("black")) {
						rootLayout.getStyleClass().remove(0);
						rootLayout.getStyleClass().add(0, "rootTower");
						background = "tower";
					} else if (background.equals("tower")) {
						rootLayout.getStyleClass().remove(0);
						rootLayout.getStyleClass().add(0, "rootCrop");
						background = "crop";
					} else if (background.equals("crop")) {
						rootLayout.getStyleClass().remove(0);
						rootLayout.getStyleClass().add(0, "rootBalloon");
						background = "balloon";
					} else if (background.equals("balloon")) {
						rootLayout.getStyleClass().remove(0);
						rootLayout.getStyleClass().add(0, "rootWood");
						background = "wood";
					} else if (background.equals("wood")) {
						rootLayout.getStyleClass().remove(0);
						rootLayout.getStyleClass().add(0, "rootParis");
						background = "paris";
					}

				}
			}
		});
	}

	/**
	 * Listens for user key press in theme changes
	 */
	private void listenerForThemeChange() {
		final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
		scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (keyComb1.match(event)) {
					if (theme.equals("green")) {
						changeBlueTheme();
					} else if (theme.equals("blue")) {
						changeTransparentTheme();
					} else if (theme.equals("transparent")) {
						changeRedTheme();
					} else if (theme.equals("red")) {
						changeGreenTheme();
					}
					checkIsTasksEmpty();
				}
			}
		});
	}

	/*************************************
	 * END OF LISTENER
	 ****************************************/

	/**
	 * creating popover and gridpane for btnBackground
	 * 
	 * @param btnBackground
	 */
	private void backgroundChooser(Button btnBackground) {
		bgPopOver = new PopOver();
		bgPopOver.setDetachable(false);
		bgPopOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(5));
		gridPane.setHgap(5);
		gridPane.setVgap(5);

		createImageViewForBackground();
		gridPane.add(woodBg, 0, 0);
		gridPane.add(cropBg, 1, 0);
		gridPane.add(towerBg, 2, 0);
		gridPane.add(parisBg, 0, 1);
		gridPane.add(balloonBg, 1, 1);
		gridPane.add(blackBg, 2, 1);
		bgPopOver.setContentNode(gridPane);

		btnBackground.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (bgPopOver.getScene() != null) {
					bgPopOver.setArrowIndent(5);
					bgPopOver.show(btnBackground.getScene().getWindow(), getPopupPosition(btnBackground).getX(),
							getPopupPosition(btnBackground).getY());
				}
			}
		});
	}

	/**
	 * Create imageView for background chooser popover
	 */
	private void createImageViewForBackground() {
		woodBg = new ImageView(new Image("/main/resources/images/wood.jpg"));
		cropBg = new ImageView(new Image("/main/resources/images/crop.jpg"));
		towerBg = new ImageView(new Image("/main/resources/images/tower.jpg"));
		parisBg = new ImageView(new Image("/main/resources/images/paris.jpg"));
		balloonBg = new ImageView(new Image("/main/resources/images/balloon.jpg"));
		blackBg = new ImageView(new Image("/main/resources/images/grass.jpg"));
		handleWoodBg();
		handleCropBg();
		handleTowerBg();
		handleParisBg();
		handleBalloonBg();
		handleBlackbg();
		setImageProperties(woodBg);
		setImageProperties(cropBg);
		setImageProperties(towerBg);
		setImageProperties(parisBg);
		setImageProperties(balloonBg);
		setImageProperties(blackBg);
	}

	/**
	 * set up image properties for background image
	 * 
	 * @param image
	 */
	private void setImageProperties(ImageView image) {
		image.setFitWidth(100);
		image.setPreserveRatio(true);
	}

	/**
	 * Method for handling image button to change background- crop
	 */
	private void handleCropBg() {
		cropBg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				background = "crop";
				rootLayout.getStyleClass().remove(0);
				rootLayout.getStyleClass().add(0, "rootCrop");
				event.consume();
				sidebar.hideSidebar();
				bgPopOver.hide();
			}
		});
	}

	/**
	 * Method for handling image button to change background- tower
	 */
	private void handleTowerBg() {
		towerBg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				background = "tower";
				rootLayout.getStyleClass().remove(0);
				rootLayout.getStyleClass().add(0, "rootTower");
				event.consume();
				sidebar.hideSidebar();
				bgPopOver.hide();
			}
		});
	}

	/**
	 * Method for handling image button to change background- paris
	 */
	private void handleParisBg() {
		parisBg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				background = "paris";
				rootLayout.getStyleClass().remove(0);
				rootLayout.getStyleClass().add(0, "rootParis");
				event.consume();
				sidebar.hideSidebar();
				bgPopOver.hide();
			}
		});
	}

	/**
	 * Method for handling image button to change background- balloon
	 */
	private void handleBalloonBg() {
		balloonBg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				background = "balloon";
				rootLayout.getStyleClass().remove(0);
				rootLayout.getStyleClass().add(0, "rootBalloon");
				event.consume();
				sidebar.hideSidebar();
				bgPopOver.hide();
			}
		});
	}

	/**
	 * Method for handling image button to change background- black
	 */
	private void handleBlackbg() {
		blackBg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				background = "black";
				rootLayout.getStyleClass().remove(0);
				rootLayout.getStyleClass().add(0, "rootBlack");
				event.consume();
				sidebar.hideSidebar();
				bgPopOver.hide();
			}
		});
	}

	/**
	 * Method for handling image button to change background- wood
	 */
	private void handleWoodBg() {
		woodBg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				background = "wood";
				rootLayout.getStyleClass().remove(0);
				rootLayout.getStyleClass().add(0, "rootWood");
				event.consume();
				sidebar.hideSidebar();
				bgPopOver.hide();
			}
		});
	}

	/**
	 * Handles popover display positions
	 * 
	 * @param node
	 * @return
	 */
	private Point2D getPopupPosition(Button node) {
		assert node != null;
		Window window = node.getScene().getWindow();
		Point2D point = node.localToScene(0, 0);
		double x = point.getX() + window.getX() + node.getWidth() + 2;
		double y = point.getY() + window.getY();
		return new Point2D(x, y);
	}
	
	/**
	 * Handles popover display positions
	 * 
	 * @param node
	 * @return
	 */
	private Point2D getPopupPosition(Node node) {
		assert node != null;
		Window window = node.getScene().getWindow();
		Point2D point = node.localToScene(0, 0);
		double x = point.getX() + window.getX();
		double y = point.getY() + window.getY();
		return new Point2D(x, y);
	}

	/**
	 * Handles notification setup for user overdue task
	 * 
	 * @param userInput
	 */
	private void notification(String userInput) {
		assert userInput != null;
		String title = "You have " + userInput + " new task(s) overdue.";
		NotificationType notification = NotificationType.CUSTOM;
		TrayNotification tray = new TrayNotification();
		tray.setTitle(title);
		tray.setMessage("Switch to overdue tab to learn more.");
		tray.setRectangleFill(Paint.valueOf("#D50000"));
		tray.setImage(new Image("/main/resources/images/overdueNotification.png"));
		tray.setAnimationType(AnimationType.POPUP);
		tray.setNotificationType(notification);
		tray.showAndDismiss(Duration.seconds(2));

	}

	/**
	 * Minimises the application
	 * 
	 * @param minimiseApp
	 */
	private void minimise(Button minimiseApp) {
		assert minimiseApp != null;
		minimiseApp.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
				stage.setIconified(true);
			}
		});
	}

	/**
	 * Exit the application
	 * 
	 * @param btnExit
	 */
	private void exit(Button btnExit) {
		assert btnExit != null;
		btnExit.setMnemonicParsing(true);
		btnExit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
	}

}
/* @@author A0124078H */