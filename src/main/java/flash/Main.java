/* @@author A0124078H */
package main.java.flash;

import java.io.File;

import java.io.IOException;
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
import main.java.data.TASK_STATUS;
import main.java.data.Task;
import main.java.gui.CommandBarController;
import main.java.gui.SideBarController;
import main.java.gui.TabsController;
import main.java.gui.TasksItemController;
import main.java.gui.TasksTableController;

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

	private ArrayList<String> historyLog;
	private ArrayList<Task> result;
	private ArrayList<Task> searchResult = new ArrayList<Task>();
	private ArrayList<Task> allResult = new ArrayList<Task>();
	private ArrayList<Task> pendingResult = new ArrayList<Task>();
	private ArrayList<Task> floatingResult = new ArrayList<Task>();
	private ArrayList<Task> overdueResult = new ArrayList<Task>();
	private ArrayList<Task> completeResult = new ArrayList<Task>();
	private ListView<TasksItemController> pendingDisplay;
	private ListView<TasksItemController> completeDisplay;
	private ListView<TasksItemController> allDisplay;
	private ListView<TasksItemController> floatingDisplay;
	private ListView<TasksItemController> overdueDisplay;

	private static final int EXPANDED_WIDTH = 84;
	private static final int COMMAND_INDEX = 0;
	private static final String EMPTY_STRING = "";
	private static final String SPLIT = "\\s+";
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
	private static final String CLEARUPCOMING_COMMAND = "clearUpcoming";
	private static final String CLEARCOMPLETE_COMMAND = "clearComplete";
	private static final String CLEAROVERDUE_COMMAND = "clearOverdue";
	private static final String CLEARFLOATING_COMMAND = "clearFloating";
	private static final String CLEARALL_COMMAND = "clearAll";
	private static final String DELETECOMPLETE_COMMAND = "deleteComplete";
	private static final String SHOW_COMMAND = "show";
	private static final String SHOWCOMPLETE_COMMAND = "showComplete";

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
	private Label lblPending = new Label();
	private Label lblCompleted = new Label();
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
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.initStyle(StageStyle.TRANSPARENT);
		this.primaryStage.getIcons().add(new Image("/main/resources/images/cache.png"));
		this.primaryStage.setHeight(670);
		this.primaryStage.setWidth(570);
		initControllers(this);
		initLogic();
		initRootLayout();
		changeRedTheme();
		checkIsTasksEmpty();
		overdueTimer();
	}

	/********************************** Initialisation ***********************************************/
	/***********************************************************************************************/

	private void initControllers(Main main) {
		allTableControl = new TasksTableController();
		floatingTableControl = new TasksTableController();
		overdueTableControl = new TasksTableController();
		pendingTableControl = new TasksTableController();
		completeTableControl = new TasksTableController();

		barControl = new CommandBarController(this);
		tabControl = new TabsController();

		allDisplay = allTableControl.getListView();
		floatingDisplay = floatingTableControl.getListView();
		overdueDisplay = overdueTableControl.getListView();
		pendingDisplay = pendingTableControl.getListView();
		completeDisplay = completeTableControl.getListView();
	}

	private void initLogic() throws Exception {
		logic = new Logic();
	}

	/**
	 * Initialises the RootLayout that will contain all other JavaFX components.
	 */
	private void initRootLayout() {

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
			e.printStackTrace();
		}
	}

	private void checkIsTasksEmpty() throws Exception {
		populateAllPendingTasks();
		populateAllCompleteTasks();
		switchToModifiedTab();
		reinitialiseModifiedBoolean();

	}

	private void checkOverdue() {
		ArrayList<Task> overdueList = logic.checkOverdue();
		String taskName = null;
		if (!overdueList.isEmpty()) {
			try {
				checkIsTasksEmpty();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				taskName = locateOverdueTask(overdueList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (taskName != null) {
				notification(taskName);
			}
		}
	}

	/****** Allows dragging of the windows **********/

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

	private void showSidebar() {

		// create a sidebar with some content in it.
		final Pane sidePane = createSidebarContent();
		sidebar = new SideBarController(EXPANDED_WIDTH, sidePane);
		VBox.setVgrow(sidePane, Priority.ALWAYS);
		rootLayout.setLeft(sidebar);
		sidebar.hideSidebar();
		createTopBar(sidebar);
	}

	private void overdueTimer() {
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(30000), ae -> checkOverdue()));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private String locateOverdueTask(ArrayList<Task> overdueList) throws Exception {
		String taskName = "";
		taskName += overdueList.size();
		return taskName;
	}

	private void reinitialiseModifiedBoolean() {
		isModifiedOverdue = false;
		isModifiedPending = false;
		isModifiedFloating = false;
		isModifiedComplete = false;
	}

	private void populateAllCompleteTasks() throws Exception {
		if (logic.displayComplete().isEmpty()) {
			// if complete is empty
			tabControl.setEmptyCompleteTab();
			tabControl.setCompletedNotification(logic.displayComplete().size());
		} else {
			setupCompleteTable();
		}
	}

	private void populateAllPendingTasks() throws Exception {
		if (logic.displayPending().isEmpty()) {
			// if all pending is empty
			setupNoTasksTabs();

		} else {
			// all pending is not empty
			setupIndividualTabNotification();
			populateAllTable();
		}
	}

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

	private void setupCompleteTable() throws Exception {
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
		// System.out.println("completeresult: " + completeResult.size());
	}

	private void populateAllTable() throws Exception {
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
			if (temp.getStatus() == TASK_STATUS.UPCOMING) {
				pendingTableControl.addTask(temp, ++pendingCount, theme);
				pendingResult.add(temp);
				if (temp.getLastModified()) {
					isModifiedPending = true;
				}
			} else if (temp.getStatus() == TASK_STATUS.FLOATING) {
				floatingTableControl.addTask(temp, ++floatingCount, theme);
				floatingResult.add(temp);
				if (temp.getLastModified()) {
					isModifiedFloating = true;
				}
			} else if (temp.getStatus() == TASK_STATUS.OVERDUE) {
				overdueTableControl.addTask(temp, ++overdueCount, theme);
				overdueResult.add(temp);
				if (temp.getLastModified()) {
					isModifiedOverdue = true;
				}
			}
		}

	}

	private void setupIndividualTabNotification() throws Exception {
		int overdueCount = 0;
		int pendingCount = 0;
		int floatingCount = 0;

		allTableControl.clearTask();
		pendingTableControl.clearTask();
		overdueTableControl.clearTask();
		floatingTableControl.clearTask();

		for (Task temp : logic.displayPending()) {
			if (temp.getStatus() == TASK_STATUS.UPCOMING) {
				++pendingCount;
			} else if (temp.getStatus() == TASK_STATUS.FLOATING) {
				++floatingCount;
			} else if (temp.getStatus() == TASK_STATUS.OVERDUE) {
				++overdueCount;
			}
		}

		tabControl.setAllTab(allTableControl);
		if (pendingCount == 0) {
			tabControl.setPendingTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
		} else {
			tabControl.setPendingTab(pendingTableControl);
		}

		if (floatingCount == 0) {
			tabControl.setFloatingTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
		} else {
			tabControl.setFloatingTab(floatingTableControl);
		}

		if (overdueCount == 0) {
			tabControl.setOverdueTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
		} else {
			tabControl.setOverdueTab(overdueTableControl);
		}

		// notification

		tabControl.setAllNotification(logic.displayPending().size());
		tabControl.setPendingNotification(pendingCount);
		tabControl.setOverdueNotification(overdueCount);
		tabControl.setFloatingNotification(floatingCount);
	}

	private void setupNoTasksTabs() throws Exception {
		tabControl.setPendingTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
		tabControl.setAllTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
		tabControl.setOverdueTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
		tabControl.setFloatingTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));

		tabControl.setAllNotification(logic.displayPending().size());
		tabControl.setPendingNotification(0);
		tabControl.setOverdueNotification(0);
		tabControl.setFloatingNotification(0);
	}

	private void createTopBar(SideBarController sidebar) {
		HBox toolBar = new HBox();
		HBox titleBar = new HBox();
		hBar = new HBox();
		topBar = new HBox();
		HBox leftTopBar = new HBox();

		// title
		Image icon = new Image("/main/resources/images/flashIcon.png");
		ImageView flashView = new ImageView(icon);

		Label empty1 = new Label(" ");

		Image imgTitle = new Image("/main/resources/images/title.png");
		ImageView iconView = new ImageView(imgTitle);

		titleBar.getChildren().addAll(flashView, empty1, iconView);
		titleBar.setAlignment(Pos.CENTER_LEFT);
		titleBar.setPadding(new Insets(0, 0, 0, 5));

		HBox empty2 = new HBox();
		empty2.setPadding(new Insets(0, 0, 0, 455));
		// 2 app control buttons
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

	private VBox createSidebarContent() {// create some content to put in the
											// sidebar.

		final VBox sidePane = new VBox();
		sidePane.getStyleClass().add("sidePane");

		VBox profile = new VBox();
		Image icon = new Image("/main/resources/images/flashpoint.png");
		ImageView iconView = new ImageView(icon);
		iconView.setFitWidth(70);
		iconView.setPreserveRatio(true);

		Label empty = new Label();
		lblPending.getStyleClass().add("lblPendingCompleted");
		lblCompleted.getStyleClass().add("lblPendingCompleted");

		profile.getChildren().addAll(iconView, empty, lblPending, lblCompleted);
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

	private void showHelpPage(Button btnHelp) {
		btnHelp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				popOverForHelp();
				sidebar.hideSidebar();
			}
		});

	}

	private void showTabs() {
		rootLayout.setCenter(tabControl);
	}

	private void showTasks() {
		tabControl.setAllTab(allTableControl);
		tabControl.setPendingTab(pendingTableControl);
		tabControl.setCompleteTab(completeTableControl);
		tabControl.setOverdueTab(overdueTableControl);
		tabControl.setFloatingTab(floatingTableControl);
	}

	private void showCommandBar() {
		rootLayout.setBottom(barControl);
		// barControl.setText("What is your main focus for today?");
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
			if (!text.equalsIgnoreCase("help")) {
				checkIsTasksEmpty();
			}else{
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

	private void handleEnterKey(ListView<TasksItemController> display) {

		TasksItemController chosen = display.getSelectionModel().getSelectedItem();
		barControl.updateUserInput("edit " + chosen.getTaskName() + ", ");
		barControl.getFocus();
	}

	private void handleDeleteKey(ListView<TasksItemController> display) {
		TasksItemController chosen = display.getSelectionModel().getSelectedItem();
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
		if (pointer <= 0) {
			return historyLog.get(pointer);
		}
		pointer--;
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
		commandByNumber = false;
		boolean editByNumber = false;
		pointer = historyLog.size();
		
		if (StringUtils.isBlank(userInput)) {
			setFeedback(commandBarController, "error", userInput);
			return;
		}
		// UI related command
		else if (userInput.equalsIgnoreCase(HELP_COMMAND)) {
			popOverForHelp();
			setFeedback(commandBarController, "valid", userInput);
		}
		if (userInput.equalsIgnoreCase("theme blue")) {
			changeBlueTheme();
			setFeedback(commandBarController, "valid", userInput);
		} else if (userInput.equalsIgnoreCase("theme red")) {
			changeRedTheme();
			setFeedback(commandBarController, "valid", userInput);
		} else if (userInput.equalsIgnoreCase("theme ")) {
			changeTransparentTheme();
			setFeedback(commandBarController, "valid", userInput);
		} else if (userInput.equalsIgnoreCase("theme green")) {
			changeGreenTheme();
			setFeedback(commandBarController, "valid", userInput);
		} else if (userInput.equalsIgnoreCase("switch")) {
			handleSwitchCommand(commandBarController, userInput);
		} else {
			// Non- UI related command
			if (userInput.equalsIgnoreCase("clear")) {
				userInput = handleClearByTab(userInput);
			}
			
			String[] fragments = userInput.split(" ");
			int numberToChange = -1;
			//check if the user enter a number after command key word
			if (fragments.length > 1) {
				
				System.out.println("fragmentss "+ fragments[0]);
				if(fragments[COMMAND_INDEX].equalsIgnoreCase("edit")){
					fragments[1]= fragments[1].substring(0,fragments[1].indexOf(','));		
				}
				System.out.println("fragmentss "+ fragments[1]);
				try {
					numberToChange = Integer.parseInt(fragments[1]);
					commandByNumber = true;
				} catch (NumberFormatException e) {
					commandByNumber = false;
				}
				
				System.out.println("before "+ numberToChange);
				// if the user delete/edit/mark/unmark by number
				if (commandByNumber) {
					System.out.println("after" + numberToChange);
					numberToChange -= 1;
					if(fragments[COMMAND_INDEX].equalsIgnoreCase("delete")||fragments[COMMAND_INDEX].equalsIgnoreCase("edit")
							||fragments[COMMAND_INDEX].equalsIgnoreCase("mark")||fragments[COMMAND_INDEX].equalsIgnoreCase("unmark")){
					       userInput = handleCommandByNumber(userInput,fragments,commandBarController,numberToChange);	
					}
					 //edit still goes through logic.handleusercommand
					if(fragments[COMMAND_INDEX].equalsIgnoreCase("edit")){
						commandByNumber = false;
						editByNumber = true;
					}
				}
			}
            		
			
			// delete from complete tab
			if (fragments[COMMAND_INDEX].equalsIgnoreCase("delete") && tabControl.getCompleteTab().isSelected()) {
				userInput = appendDeleteComplete(fragments);
				// show from complete tab
			} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("show") && tabControl.getCompleteTab().isSelected()) {
				userInput = appendShowComplete(fragments);
				// sort from complete tab
			} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("sort") && tabControl.getCompleteTab().isSelected()) {
				userInput = appendSortComplete(fragments);
				// edit by partial match
			} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("edit")) {
				if(editByNumber==false){
				   userInput = handleEditByPartialMatching(userInput);
				}
			}
			
			// if the user delete/edit/mark/unmark by task matching
			if (fragments[COMMAND_INDEX].equalsIgnoreCase("add")) {
				commandByNumber = false;
			}
			if (!commandByNumber) {
				try {
					result = new ArrayList<Task>(logic.handleUserCommand(userInput, result));
				} catch (Exception e) {
					isError = true;
					setFeedback(commandBarController, "error", e.toString());
				}
				if (fragments[COMMAND_INDEX].equalsIgnoreCase("show")
						|| fragments[COMMAND_INDEX].equalsIgnoreCase("showComplete")) {
					if (result.size() != 0) {
						popOverForShow(commandBarController, fragments[1]);
					}
				}
			}
		}

		if (isError == false) {
			setFeedback(commandBarController, "valid", userInput);
		}

		historyLog.add(userInput);
		pointer++;
		isError = false;
		new CommandBarController();
		commandBarController.clear();

	}

	private String handleCommandByNumber(String userInput,String[] fragments,CommandBarController commandBarController, int numberToChange) {
		
		if (tabControl.getAllTab().isSelected()) {
			if (numberToChange >= allResult.size()) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			}else{			
			    try {
			    	userInput = validHandleCommandByNumberAllTab(userInput, fragments, commandBarController,numberToChange);				  
		     	} catch (Exception e) {
				   e.printStackTrace();
		    	}
			}
		} else if (tabControl.getPendingTab().isSelected()) {
			if (numberToChange >= pendingResult.size()) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			}
			try {
				userInput = validHandleCommandByNumberPendingTab(userInput, fragments, commandBarController, numberToChange);				  
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (tabControl.getFloatingTab().isSelected()) {
			if (numberToChange >= floatingResult.size()) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			}
			try {
				userInput = validHandleCommandByNumberFloatingTab(userInput, fragments, commandBarController,numberToChange);				  
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (tabControl.getOverdueTab().isSelected()) {
			if (numberToChange >= overdueResult.size()) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			}
			try {
				userInput = validHandleCommandByNumberOverdueTab(userInput, fragments, commandBarController,numberToChange);				  
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (tabControl.getCompleteTab().isSelected()) {
			if (numberToChange >= completeResult.size()) {
				invalidHandleCommandByNumber(commandBarController);
				return userInput;
			}
			try {
				userInput = validHandleCommandByNumberCompleteTab(userInput, fragments, commandBarController,numberToChange);				  
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	   
		return userInput;
	}

	private String validHandleCommandByNumberCompleteTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws Exception {
		if (fragments[COMMAND_INDEX].equalsIgnoreCase("delete")) {
			 logic.delete(completeResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("mark")) {
			invalidCommandUnderTab(commandBarController);
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("unmark")) {
			logic.unmark(completeResult.get(numberToChange));
		}else if (fragments[COMMAND_INDEX].equalsIgnoreCase("edit")) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();			
		    userInput = "edit " + completeResult.get(numberToChange).getTask() + ", " + update;
		}
		return userInput;
	}

	private String validHandleCommandByNumberOverdueTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws Exception {
		if (fragments[COMMAND_INDEX].equalsIgnoreCase("delete")) {
			 logic.delete(overdueResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("mark")) {
			logic.mark(overdueResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("unmark")) {
			invalidCommandUnderTab(commandBarController);
		}else if (fragments[COMMAND_INDEX].equalsIgnoreCase("edit")) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();			
		    userInput = "edit " + overdueResult.get(numberToChange).getTask() + ", " + update;
		}
		return userInput;
	}

	private String validHandleCommandByNumberFloatingTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws Exception {
		if (fragments[COMMAND_INDEX].equalsIgnoreCase("delete")) {
			 logic.delete(floatingResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("mark")) {
			logic.mark(floatingResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("unmark")) {
			invalidCommandUnderTab(commandBarController);
		}else if (fragments[COMMAND_INDEX].equalsIgnoreCase("edit")) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();			
		    userInput = "edit " + floatingResult.get(numberToChange).getTask() + ", " + update;
		}
		return userInput;
	}

	private String validHandleCommandByNumberPendingTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws Exception {
		if (fragments[COMMAND_INDEX].equalsIgnoreCase("delete")) {
			 logic.delete(pendingResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("mark")) {
			logic.mark(pendingResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("unmark")) {
			invalidCommandUnderTab(commandBarController);
		}else if (fragments[COMMAND_INDEX].equalsIgnoreCase("edit")) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();			
		    userInput = "edit " + pendingResult.get(numberToChange).getTask() + ", " + update;
		}
		return userInput;
	}

	private String validHandleCommandByNumberAllTab(String userInput, String[] fragments,
			CommandBarController commandBarController, int numberToChange) throws Exception {
		if (fragments[COMMAND_INDEX].equalsIgnoreCase("delete")) {
			 logic.delete(allResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("mark")) {
			logic.mark(allResult.get(numberToChange));
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("unmark")) {
			invalidCommandUnderTab(commandBarController);
		} else if (fragments[COMMAND_INDEX].equalsIgnoreCase("edit")) {
			String update = userInput.substring(userInput.indexOf(',') + 1).trim();		
			System.out.println("hereasadasd "+  numberToChange);
		    userInput = "edit " + allResult.get(numberToChange).getTask() + ", " + update;
		}
		System.out.println("userinput "+  userInput);
		return userInput;
	}

	private void invalidCommandUnderTab(CommandBarController commandBarController) {
		setFeedback(commandBarController, "error", "Command not allowed.");
		isError = true;
	}

	private void invalidHandleCommandByNumber(CommandBarController commandBarController) {
		setFeedback(commandBarController, "error", "Number has exceeded tasks limit.");
		isError = true;
	}

	private String handleEditByPartialMatching(String userInput) {
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

	private String appendSortComplete(String[] fragments) {
		String userInput;
		fragments[COMMAND_INDEX] = "sortComplete";
		String showComplete = "sortComplete ";
		for (int i = 1; i < fragments.length; i++) {
			showComplete += fragments[i];
		}
		userInput = showComplete;
		return userInput;
	}

	private String appendShowComplete(String[] fragments) {
		String userInput;
		fragments[COMMAND_INDEX] = "showComplete";
		String showComplete = "showComplete ";
		for (int i = 1; i < fragments.length; i++) {
			showComplete += fragments[i];
		}
		userInput = showComplete;
		return userInput;
	}

	private String appendDeleteComplete(String[] fragments) {
		String userInput;
		fragments[COMMAND_INDEX] = "deleteComplete";
		String deleteComplete = "deleteComplete ";
		for (int i = 1; i < fragments.length; i++) {
			deleteComplete += fragments[i];
		}
		userInput = deleteComplete;
		return userInput;
	}


	private String handleClearByTab(String userInput) {
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

	private void handleSwitchCommand(CommandBarController commandBarController, String userInput) {
		if (tabControl.getPendingTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getOverdueTab());
			lblTitle.setText("Overdue Tasks");
			setFeedback(commandBarController, "valid", userInput);
		} else if (tabControl.getOverdueTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getCompleteTab());
			lblTitle.setText("Completed Tasks");
			setFeedback(commandBarController, "valid", userInput);
		} else if (tabControl.getCompleteTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getAllTab());
			lblTitle.setText("All Tasks");
			setFeedback(commandBarController, "valid", userInput);
		} else if (tabControl.getAllTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getFloatingTab());
			lblTitle.setText("Floating Tasks");
			setFeedback(commandBarController, "valid", userInput);
		} else if (tabControl.getFloatingTab().isSelected()) {
			tabControl.getTabPane().getSelectionModel().select(tabControl.getPendingTab());
			lblTitle.setText("Pending Tasks");
			setFeedback(commandBarController, "valid", userInput);
		}
	}

	private void popOverForShow(CommandBarController commandBarController, String title) {
		PopOver bgPopOver = new PopOver();
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
		bgPopOver.show(tabControl.getScene().getWindow());
	}

	private void popOverForHelp() {
		PopOver helpPopOver = new PopOver();
		helpPopOver.setDetachable(true);
		helpPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
		helpPopOver.setArrowIndent(5);
		helpPopOver.setContentNode(new ImageView(new Image("/main/resources/images/help.png")));
		helpPopOver.show(tabControl.getScene().getWindow());
	}

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

	private void changeBlueTheme() {
		topBar.getStyleClass().clear();
		topBar.getStyleClass().add("blueTopBar");
		hBar.getStyleClass().clear();
		hBar.getStyleClass().add("blueToolBar");
//		pendingTableControl.getStylesheets().clear();
//		completeTableControl.getStylesheets().clear();
		tabControl.getStylesheets().clear();
//		pendingTableControl.getStylesheets().add("/main/resources/styles/blue.css");
//		completeTableControl.getStylesheets().add("/main/resources/styles/blue.css");
		tabControl.getStylesheets().add("/main/resources/styles/blue.css");
		theme = "blue";
		pendingTableControl.setTheme("blue");
	}

	private void setFeedback(CommandBarController commandBarController, String type, String userInput) {
		assert commandBarController != null;	
		isFeedback = true;
        //if the userInput is not a one word command
		if (userInput.indexOf(' ') != -1) {
			setFeedbackNonOneWordCommand(commandBarController, type, userInput);
		} else if (userInput.indexOf(' ') == -1) {
			setFeedbackOneWordCommand(commandBarController, type, userInput);
		}
	}

	private void setFeedbackOneWordCommand(CommandBarController commandBarController, String type, String userInput) {
		if (type.equals("error")) {
			commandBarController.setFeedback("Invalid Command" + ": ", Color.RED);
		} else if (type.equals("valid")) {
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

	private void setFeedbackNonOneWordCommand(CommandBarController commandBarController, String type,
			String userInput) {
		int i = 1;
		i = userInput.indexOf(' ');
		String firstWord = userInput.substring(0, i);
		String subString = userInput.substring(i + 1);
		if (type.equals("error")) {
			commandBarController.setFeedback("Invalid Command" + ": " + subString, Color.RED);
		} else if (type.equals("valid")) {
			if (isTasksCommand(firstWord)) {
				if (firstWord.equalsIgnoreCase(DELETE_COMMAND) || firstWord.equalsIgnoreCase(DELETECOMPLETE_COMMAND)) {
					firstWord = "delete";
					commandBarController.setFeedback("Task has been successfully " + firstWord + "d", Color.BLACK);
				} else{
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

	private boolean isTasksCommand(String firstWord) {
		if (firstWord.equalsIgnoreCase(MARK_COMMAND) || firstWord.equalsIgnoreCase(UNMARK_COMMAND)
				|| firstWord.equalsIgnoreCase(ADD_COMMAND) || firstWord.equalsIgnoreCase(DELETE_COMMAND)
				|| firstWord.equalsIgnoreCase(DELETECOMPLETE_COMMAND) || firstWord.equalsIgnoreCase(EDIT_COMMAND)
				|| firstWord.equalsIgnoreCase(HELP_COMMAND)) {
			return true;
		}
		return false;

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

	public void trySearch(String oldValue, String newValue) {
		// if user enter space, return and start again
		if (StringUtils.isBlank(newValue)) {
			return;
		}

		String[] fragments = null;
		fragments = newValue.split(SPLIT);
		
		boolean isEdit = fragments[COMMAND_INDEX].equalsIgnoreCase("edit");
		boolean isDelete = fragments[COMMAND_INDEX].equalsIgnoreCase("delete");
		boolean isSearch = fragments[COMMAND_INDEX].equalsIgnoreCase("search");
		boolean isMark = fragments[COMMAND_INDEX].equalsIgnoreCase("mark");
		boolean isUnmark = fragments[COMMAND_INDEX].equalsIgnoreCase("unmark");
        
		//check if it is command by number
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
		// if not command by number, live search, else if it is command by number, stop live search		
		if (!commandByNumbers) {
		  try {
			if ((tabControl.getAllTab().isSelected()) && (isEdit || isDelete || isSearch || isMark || isUnmark)) {
				liveSearchAllTab(oldValue, newValue, isEdit, isDelete, isSearch, isMark);
			} else if ((tabControl.getPendingTab().isSelected()) && (isEdit || isDelete || isSearch || isMark || isUnmark)) {
				liveSearchPendingTab(oldValue, newValue, isEdit, isDelete, isSearch, isMark);
			} else if ((tabControl.getFloatingTab().isSelected()) && (isEdit || isDelete || isSearch || isMark || isUnmark)) {
				liveSearchFloatingTab(oldValue, newValue, isEdit, isDelete, isSearch, isMark);
			} else if ((tabControl.getOverdueTab().isSelected()) && (isEdit || isDelete || isSearch || isMark || isUnmark)) {
				liveSearchOverdueTab(oldValue, newValue, isEdit, isDelete, isSearch, isMark);
			} else if ((tabControl.getCompleteTab().isSelected()) && isDelete || isSearch || isUnmark) {
				liveSearchCompleteTab(oldValue, newValue, isDelete, isSearch, isUnmark);
			}
		   } catch (Exception e) {
			e.printStackTrace();
		 }
		}

	}

	private void liveSearchCompleteTab(String oldValue, String newValue, boolean isDelete, boolean isSearch,
			boolean isUnmark) throws Exception {
		searchResult = logic.handleSearchCompleted(oldValue, newValue);
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

	private void liveSearchOverdueTab(String oldValue, String newValue, boolean isEdit, boolean isDelete,
			boolean isSearch, boolean isMark) throws Exception {
		searchResult = logic.handleSearchPending(oldValue, newValue);
		if (isEdit || isDelete || isSearch) {
			populateOverdueList(searchResult);
		} else if (isMark) {
			populateOverdueList(searchResult);
			populateCompleteList(logic.displayComplete());
		}
	}

	private void liveSearchFloatingTab(String oldValue, String newValue, boolean isEdit, boolean isDelete,
			boolean isSearch, boolean isMark) throws Exception {
		searchResult = logic.handleSearchPending(oldValue, newValue);
		if (isEdit || isDelete || isSearch) {
			populateFloatingList(searchResult);
		} else if (isMark) {
			populateFloatingList(searchResult);
			populateCompleteList(logic.displayComplete());
		}
	}

	private void liveSearchPendingTab(String oldValue, String newValue, boolean isEdit, boolean isDelete,
			boolean isSearch, boolean isMark) throws Exception {
		searchResult = logic.handleSearchPending(oldValue, newValue);
		if (isEdit || isDelete || isSearch) {
			populatePendingList(searchResult);
		} else if (isMark) {
			populatePendingList(searchResult);
			populateCompleteList(logic.displayComplete());
		}
	}

	private void liveSearchAllTab(String oldValue, String newValue, boolean isEdit, boolean isDelete, boolean isSearch,
			boolean isMark) throws Exception {
		searchResult = logic.handleSearchPending(oldValue, newValue);
		if (isEdit || isDelete || isSearch) {
			populateAllList(searchResult);
		} else if (isMark) {
			populateAllList(searchResult);
			populateCompleteList(logic.displayComplete());
		}
	}

	public void showColourCommand(String oldValue, String newValue) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(oldValue)) {
			return;
		}
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

	public void populateAllList(ArrayList<Task> searchResult) {
		allTableControl.clearTask();
		allResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			allTableControl.addTask(temp, ++count, theme);
			allResult.add(temp);
		}

	}

	private void populateOverdueList(ArrayList<Task> searchResult) {
		overdueTableControl.clearTask();
		overdueResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			if (temp.getStatus() == TASK_STATUS.OVERDUE) {
				overdueTableControl.addTask(temp, ++count, theme);
				overdueResult.add(temp);
			}
		}
	}

	private void populateFloatingList(ArrayList<Task> searchResult) {
		floatingTableControl.clearTask();
		floatingResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			if (temp.getStatus() == TASK_STATUS.FLOATING) {
				floatingTableControl.addTask(temp, ++count, theme);
				floatingResult.add(temp);
			}
		}
	}

	private void populateCompleteList(ArrayList<Task> searchResult) {
		completeTableControl.clearTask();
		completeResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			if (temp.getStatus() == TASK_STATUS.COMPLETED) {
				completeTableControl.addTask(temp, ++count, theme);
				completeResult.add(temp);
			}
		}
	}

	private void populatePendingList(ArrayList<Task> searchResult) {
		pendingTableControl.clearTask();
		pendingResult.clear();
		int count = 0;
		for (Task temp : searchResult) {
			if (temp.getStatus() == TASK_STATUS.UPCOMING) {
				pendingTableControl.addTask(temp, ++count, theme);
				pendingResult.add(temp);
			}
		}
	}

	public void saveToLocation(Button btnSave) {
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
						setFeedback(barControl, "error", "file path not recognised");
					}
				}
				sidebar.hideSidebar();
			}
		});

	}

	public void moveToLocation(Button btnMove) {
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
                         setFeedback(barControl, "error", "file path not recognised");
					}
				}
				sidebar.hideSidebar();
			}
		});

	}

	public void loadFilename(Button btnLoad) {

		btnLoad.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File loadFile = fileChooser.showOpenDialog(null);
				if (loadFile != null) {
					logic.loadFilename(loadFile.getAbsolutePath());
					try {
						checkIsTasksEmpty();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				sidebar.hideSidebar();
			}
		});
	}

	/************************************
	 * Various listener for hotkeys
	 **********************************************/

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
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(completeDisplay);
				}
			}

		});
	}

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
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(overdueDisplay);
				}
			}

		});
	}

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
						e1.printStackTrace();
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(floatingDisplay);
				}
			}

		});
	}

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
						e1.printStackTrace();
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(allDisplay);
				}
			}

		});
	}

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
						e1.printStackTrace();
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(pendingDisplay);
				}
			}

		});
	}

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
					try {
						checkIsTasksEmpty();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/************************************
	 * END OF LISTENER
	 **********************************************/

	private void backgroundChooser(Button btnBackground) {

		bgPopOver = new PopOver();
		bgPopOver.setDetachable(false);
		bgPopOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(5));
		gridPane.setHgap(5);
		gridPane.setVgap(5);

		woodBg = new ImageView(new Image("/main/resources/images/wood.jpg"));
		handleWoodBg();
		woodBg.setFitWidth(100);
		woodBg.setPreserveRatio(true);
		gridPane.add(woodBg, 0, 0);

		cropBg = new ImageView(new Image("/main/resources/images/crop.jpg"));
		handleCropBg();
		cropBg.setFitWidth(100);
		cropBg.setPreserveRatio(true);
		gridPane.add(cropBg, 1, 0);

		towerBg = new ImageView(new Image("/main/resources/images/tower.jpg"));
		handleTowerBg();
		towerBg.setFitWidth(100);
		towerBg.setPreserveRatio(true);
		gridPane.add(towerBg, 2, 0);

		parisBg = new ImageView(new Image("/main/resources/images/paris.jpg"));
		handleParisBg();
		parisBg.setFitWidth(100);
		parisBg.setPreserveRatio(true);
		gridPane.add(parisBg, 0, 1);

		balloonBg = new ImageView(new Image("/main/resources/images/balloon.jpg"));
		handleBalloonBg();
		balloonBg.setFitWidth(100);
		balloonBg.setPreserveRatio(true);
		gridPane.add(balloonBg, 1, 1);

		blackBg = new ImageView(new Image("/main/resources/images/grass.jpg"));
		handleBlackbg();
		blackBg.setFitWidth(100);
		blackBg.setPreserveRatio(true);
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

	/* Method for handling image button to change background- crop */
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

	/* Method for handling image button to change background- tower */
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

	/* Method for handling image button to change background- paris */
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

	/* Method for handling image button to change background- balloon */
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

	/* Method for handling image button to change background- black */
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

	/* Method for handling image button to change background- wood */
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

	private Point2D getPopupPosition(Button node) {
		Window window = node.getScene().getWindow();
		Point2D point = node.localToScene(0, 0);
		double x = point.getX() + window.getX() + node.getWidth() + 2;
		double y = point.getY() + window.getY();
		return new Point2D(x, y);
	}

	private Point2D getPopupPosition(Label node) {
		Window window = node.getScene().getWindow();
		Point2D point = node.localToScene(0, 0);
		double x = point.getX() + window.getX() + node.getWidth();
		double y = point.getY() + window.getY();
		return new Point2D(x, y);
	}

	private void notification(String userInput) {
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

	private void minimise(Button minimiseApp) {
		minimiseApp.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
				stage.setIconified(true);
			}
		});
	}

	private void exit(Button btnExit) {
		btnExit.setMnemonicParsing(true);
		btnExit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
	}

}
/* @@author A0124078H */