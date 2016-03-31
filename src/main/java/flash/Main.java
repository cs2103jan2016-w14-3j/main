package main.java.flash;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


public class Main extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private HBox hBar;
	private HBox topBar;
	private Scene scene;
	
	private Logic logic;
	private Task task;

	private TasksTableController allTableControl;
	private TasksTableController floatingTableControl;
	private TasksTableController pendingTableControl;
	private TasksTableController completeTableControl;
	private TasksTableController overdueTableControl;

	private CommandBarController barControl;
	private TabsController tabControl;
	private ArrayList<String> historyLog;
	private ArrayList<Task> result;
	private ArrayList<Task> searchResult = new ArrayList<Task>();
	private ListView<TasksItemController> pendingDisplay;
	private ListView<TasksItemController> completeDisplay;
	private ListView<TasksItemController> allDisplay;
	private ListView<TasksItemController> floatingDisplay;
	private ListView<TasksItemController> overdueDisplay;

	private static final String EMPTY_STRING = "";
	private static final String SPLIT = "\\s+";
	private static final int COMMAND_INDEX = 0;
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
	private static final int EXPANDED_WIDTH = 84;

	private int pointer;
	private boolean isFeedback = false;
	private boolean isError = false;
	private static double xOffset = 0;
	private static double yOffset = 0;
	private Label lblPending = new Label();
	private Label lblCompleted = new Label();
	private Label lblTitle;


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Flashpoint");
		this.primaryStage.initStyle(StageStyle.TRANSPARENT);
		this.primaryStage.getIcons().add(new Image("/main/resources/images/cache.png"));
		this.primaryStage.setHeight(670);
		this.primaryStage.setWidth(570);
		initControllers(this);
		initLogic();
		initRootLayout();
		checkIsTasksEmpty();
		changeBlueTheme();
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

	private void checkIsTasksEmpty() throws Exception {
		if (logic.displayPending().isEmpty()) {
			//if all pending is empty
			tabControl.setPendingTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
			tabControl.setAllTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
			tabControl.setOverdueTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
			tabControl.setFloatingTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
			
			tabControl.setAllNotification(logic.displayPending().size());
			tabControl.setPendingNotification(0);
			tabControl.setOverdueNotification(0);
			tabControl.setFloatingNotification(0);
	
		} else {
			int allCount = 0;
			int overdueCount = 0;
			int pendingCount = 0;
			int floatingCount = 0;		

			allTableControl.clearTask();
			pendingTableControl.clearTask();
			overdueTableControl.clearTask();
			floatingTableControl.clearTask();
			
			for (Task temp : logic.displayPending()) {
				if(temp.getStatus()==TASK_STATUS.UPCOMING){
					++pendingCount;
				}else if(temp.getStatus()==TASK_STATUS.FLOATING){
					++floatingCount;
				}else if(temp.getStatus()==TASK_STATUS.OVERDUE){
					++overdueCount;
				}							
			}
//			System.out.println("number of pending task: " + pendingCount);
//			System.out.println("number of floating task: " + floatingCount);
//			System.out.println("number of overdue task: " + overdueCount);
			
			tabControl.setAllTab(allTableControl);
			if(pendingCount==0){
				tabControl.setPendingTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
			}else{
				tabControl.setPendingTab(pendingTableControl);
			}
			
			if(floatingCount==0){
				tabControl.setFloatingTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
			}else{
				tabControl.setFloatingTab(floatingTableControl);
			}
			
			if(overdueCount==0){
				tabControl.setOverdueTab(new ImageView(new Image("/main/resources/images/intro.fw.png")));
			}else{
				tabControl.setOverdueTab(overdueTableControl);
			}		
			
		//notification
		
			tabControl.setAllNotification(logic.displayPending().size());
			tabControl.setPendingNotification(pendingCount);
			tabControl.setOverdueNotification(overdueCount);
			tabControl.setFloatingNotification(floatingCount);
			
			allCount = 0;
			overdueCount = 0;
			pendingCount = 0;
			floatingCount = 0;		
			
			for (Task temp : logic.displayPending()) {
				allTableControl.addTask(temp,++allCount);
				if(temp.getStatus()==TASK_STATUS.UPCOMING){
					pendingTableControl.addTask(temp,++pendingCount);
				}else if(temp.getStatus()==TASK_STATUS.FLOATING){
					floatingTableControl.addTask(temp,++floatingCount);
				}else if(temp.getStatus()==TASK_STATUS.OVERDUE){
					overdueTableControl.addTask(temp,++overdueCount);
				}							
			}
					
		}		
		if (logic.displayComplete().isEmpty()) {
			//if complete is empty
			tabControl.setEmptyCompleteTab();
			tabControl.setCompletedNotification(logic.displayComplete().size());
		} else {
			
			int completeCount = 0;
			completeTableControl.clearTask();
			tabControl.setCompleteTab(completeTableControl);
			for (Task temp : logic.displayComplete()) {
				completeTableControl.addTask(temp,++completeCount);
			}
			tabControl.setCompletedNotification(completeCount);
		}
		
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

	private void listenForStageInput() {
//		scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
//			if (key.getCode() == KeyCode.ESCAPE) {
//				primaryStage.setIconified(true);
//			}
//		});
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
		SideBarController sidebar = new SideBarController(EXPANDED_WIDTH, sidePane);
		VBox.setVgrow(sidePane, Priority.ALWAYS);	
		rootLayout.setLeft(sidebar);	
		sidebar.hideSidebar();	
		
		createTopBar(sidebar);
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
		
		titleBar.getChildren().addAll(flashView,empty1,iconView);
		titleBar.setAlignment(Pos.CENTER_LEFT);
		titleBar.setPadding(new Insets(0, 0, 0, 5));
       
		HBox empty2 = new HBox();
        empty2.setPadding(new Insets(0, 0, 0, 455));
//		 2 app control buttons
		Button closeApp = new Button();
		closeApp.getStyleClass().add("closeApp");
		exit(closeApp);
    
		Button minimiseApp = new Button();
		minimiseApp.getStyleClass().add("minimiseApp");
		minimiseApp.setPadding(new Insets(4, 0, 0, 0));
		minimise(minimiseApp);
        
		toolBar.getChildren().addAll(empty2, minimiseApp,closeApp);
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

	private void minimise(Button minimiseApp) {
		minimiseApp.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
				// is stage minimizable into task bar. (true | false)
				stage.setIconified(true);
			}
		});
	}

	private VBox createSidebarContent() {// create some content to put in the
											// sidebar.

		final VBox sidePane = new VBox();
		sidePane.getStyleClass().add("sidePane");

		VBox profile = new VBox();
		Image icon = new Image("/main/resources/images/profilePhoto.png");
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

		final Button btnSave = new Button();
		btnSave.getStyleClass().add("saveButton");
		btnSave.setPadding(Insets.EMPTY);
		moveToLocation(btnSave);

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
		sidePane.getChildren().addAll(profile, btnNew, btnLoad, btnSave, btnHelp, btnExit);
		return sidePane;
	}

	private void showHelpPage(Button btnHelp) {
		btnHelp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(tabControl.getAllTab().isSelected()){
				   tabControl.setAllTab(new ImageView(new Image("/main/resources/images/help.png")));
				}else if(tabControl.getPendingTab().isSelected()){
					   tabControl.setPendingTab(new ImageView(new Image("/main/resources/images/help.png")));
				}else if(tabControl.getFloatingTab().isSelected()){
					   tabControl.setFloatingTab(new ImageView(new Image("/main/resources/images/help.png")));
				}else if(tabControl.getOverdueTab().isSelected()){
					   tabControl.setOverdueTab(new ImageView(new Image("/main/resources/images/help.png")));
				}else if(tabControl.getCompleteTab().isSelected()){
					   tabControl.setCompleteTab(new ImageView(new Image("/main/resources/images/help.png")));
				}
					   
					   
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
		//barControl.setText("What is your main focus for today?");
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
			if(!text.equalsIgnoreCase("help")){
			  checkIsTasksEmpty();
			}
		} else if ((event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) && !historyLog.isEmpty()) {
			event.consume(); // nullifies the default behavior of UP and DOWN on
			// a TextArea
			handleGetPastCommands(event);
		} else if ((event.getCode() == KeyCode.TAB)) {
			event.consume();
			if(tabControl.getAllTab().isSelected()){
				allTableControl.controlToList();
			}else if(tabControl.getPendingTab().isSelected()){
				pendingTableControl.controlToList();
			}else if(tabControl.getOverdueTab().isSelected()){
				overdueTableControl.controlToList();
			}else if(tabControl.getFloatingTab().isSelected()){
				floatingTableControl.controlToList();
			}else if(tabControl.getCompleteTab().isSelected()){
				completeTableControl.controlToList();
			}
		
		}else if ((event.getCode()== KeyCode.F5)){
			checkIsTasksEmpty();
		}
	}

	private void listenerForTaskList() {

		pendingDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					handleEnterKey(pendingDisplay);
				} else if (e.getCode() == KeyCode.ESCAPE) {
					try {
						populatePendingList(logic.displayPending());
						tabControl.setPendingTab(pendingTableControl);
						System.out.println("escapppp");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(pendingDisplay);
				}
			}

		});
		
		allDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					System.out.print("enter pressed");
					handleEnterKey(allDisplay);
				} else if (e.getCode() == KeyCode.ESCAPE) {
					try {
						populateAllList(logic.displayPending());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(allDisplay);
				}
			}

		});
		
		floatingDisplay.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ENTER) {
					handleEnterKey(floatingDisplay);
				} else if (e.getCode() == KeyCode.ESCAPE) {
					try {
						populateFloatingList(logic.displayPending());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					barControl.getFocus();
				} else if (e.getCode() == KeyCode.DELETE) {
					handleDeleteKey(floatingDisplay);
				}
			}

		});
		
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
		
		tabControl.getTabPane().setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if(tabControl.getPendingTab().isSelected()){
					lblTitle.setText("Pending Tasks");
				}else if(tabControl.getCompleteTab().isSelected()){
					lblTitle.setText("Completed Tasks");
				}else if(tabControl.getOverdueTab().isSelected()){
					lblTitle.setText("Overdue Tasks");
				}else if(tabControl.getFloatingTab().isSelected()){
					lblTitle.setText("Floating Tasks");
				}else if(tabControl.getAllTab().isSelected()){
					lblTitle.setText("All Tasks");
				}
			}

		});
		
		

	}

	private void handleEnterKey(ListView<TasksItemController> display) {
		
		TasksItemController chosen = display.getSelectionModel().getSelectedItem();
		barControl.updateUserInput("edit " + chosen.getTaskName()+", ");
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

		if (userInput.isEmpty()) {                        
		}else if (userInput.equalsIgnoreCase("help")) {
			if(tabControl.getAllTab().isSelected()){
				   tabControl.setAllTab(new ImageView(new Image("/main/resources/images/help.png")));
				}else if(tabControl.getPendingTab().isSelected()){
					   tabControl.setPendingTab(new ImageView(new Image("/main/resources/images/help.png")));
				}else if(tabControl.getFloatingTab().isSelected()){
					   tabControl.setFloatingTab(new ImageView(new Image("/main/resources/images/help.png")));
				}else if(tabControl.getOverdueTab().isSelected()){
					   tabControl.setOverdueTab(new ImageView(new Image("/main/resources/images/help.png")));
				}else if(tabControl.getCompleteTab().isSelected()){
					   tabControl.setCompleteTab(new ImageView(new Image("/main/resources/images/help.png")));
				}
			//notification(userInput);
			setFeedback(commandBarController, "valid", userInput);
		}else if(userInput.equalsIgnoreCase("theme blue")){
			changeBlueTheme();
			setFeedback(commandBarController, "valid", userInput);
		}else if(userInput.equalsIgnoreCase("theme red")){
			changeRedTheme();
			setFeedback(commandBarController, "valid", userInput);
		}else if(userInput.equalsIgnoreCase("theme orange")){
			changeOrangeTheme();
			setFeedback(commandBarController, "valid", userInput);
		}else if(userInput.equalsIgnoreCase("theme green")){
			changeGreenTheme();
			setFeedback(commandBarController, "valid", userInput);
		}else if(userInput.equalsIgnoreCase("switch")){
			if(tabControl.getPendingTab().isSelected()){
			   tabControl.getTabPane().getSelectionModel().select(tabControl.getOverdueTab());
			   lblTitle.setText("Overdue Tasks");
			   setFeedback(commandBarController, "valid", userInput);
			}else if (tabControl.getOverdueTab().isSelected()){
			   tabControl.getTabPane().getSelectionModel().select(tabControl.getCompleteTab());
			   lblTitle.setText("Completed Tasks");
			   setFeedback(commandBarController, "valid", userInput);
			}else if (tabControl.getCompleteTab().isSelected()){
				   tabControl.getTabPane().getSelectionModel().select(tabControl.getAllTab());
				   lblTitle.setText("All Tasks");
				   setFeedback(commandBarController, "valid", userInput);
			}else if (tabControl.getAllTab().isSelected()){
			        tabControl.getTabPane().getSelectionModel().select(tabControl.getFloatingTab());
					 lblTitle.setText("Floating Tasks");
					  setFeedback(commandBarController, "valid", userInput);
			}else if (tabControl.getFloatingTab().isSelected()){
				   tabControl.getTabPane().getSelectionModel().select(tabControl.getPendingTab());
				   lblTitle.setText("Pending Tasks");
				   setFeedback(commandBarController, "valid", userInput);
			}
		}
		else {
			// normal command
			historyLog.add(userInput);
            
			if (userInput.equalsIgnoreCase("clear")) {
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
			}
			try {
				result = new ArrayList<Task>(logic.handleUserCommand(userInput, result));
			} catch (Exception e) {
				isError = true;
				setFeedback(commandBarController, "error", e.toString());
				System.out.println(e.toString());
			}

			if (isError == false) {
				setFeedback(commandBarController, "valid", userInput);
			}
		}
		historyLog.add(userInput);	
		isError = false;
		new CommandBarController();
		commandBarController.clear();
	}

	private void changeRedTheme() {
		topBar.getStyleClass().clear();
		hBar.getStyleClass().clear();
		topBar.getStyleClass().add("topBar");
		hBar.getStyleClass().add("toolBar");
		pendingTableControl.getStylesheets().clear();
		completeTableControl.getStylesheets().clear();
		tabControl.getStylesheets().clear();
		pendingTableControl.getStylesheets().add("/main/resources/styles/stylesheet.css");
		completeTableControl.getStylesheets().add("/main/resources/styles/stylesheet.css");
		tabControl.getStylesheets().add("/main/resources/styles/stylesheet.css");
	}
	
	private void changeGreenTheme() {
		topBar.getStyleClass().clear();
		hBar.getStyleClass().clear();
		topBar.getStyleClass().add("greenTopBar");
		hBar.getStyleClass().add("greenToolBar");
		pendingTableControl.getStylesheets().clear();
		completeTableControl.getStylesheets().clear();
		tabControl.getStylesheets().clear();
		pendingTableControl.getStylesheets().add("/main/resources/styles/green.css");
		completeTableControl.getStylesheets().add("/main/resources/styles/green.css");
		tabControl.getStylesheets().add("/main/resources/styles/green.css");
	}
	
	private void changeOrangeTheme() {
		topBar.getStyleClass().clear();
		hBar.getStyleClass().clear();
		topBar.getStyleClass().add("orangeTopBar");
		hBar.getStyleClass().add("orangeToolBar");
		pendingTableControl.getStylesheets().clear();
		completeTableControl.getStylesheets().clear();
		tabControl.getStylesheets().clear();
		pendingTableControl.getStylesheets().add("/main/resources/styles/orange.css");
		completeTableControl.getStylesheets().add("/main/resources/styles/orange.css");
		tabControl.getStylesheets().add("/main/resources/styles/orange.css");
	}

	private void changeBlueTheme() {
		topBar.getStyleClass().clear();	    
		topBar.getStyleClass().add("blueTopBar");		
		hBar.getStyleClass().clear();
		hBar.getStyleClass().add("blueToolBar");
		pendingTableControl.getStylesheets().clear();
		completeTableControl.getStylesheets().clear();
		tabControl.getStylesheets().clear();
		pendingTableControl.getStylesheets().add("/main/resources/styles/blue.css");
		completeTableControl.getStylesheets().add("/main/resources/styles/blue.css");
		tabControl.getStylesheets().add("/main/resources/styles/blue.css");
	}

	private void setFeedback(CommandBarController commandBarController, String type, String userInput) {
		assert commandBarController != null;
		int i = 1;
		isFeedback = true;
		
		if (userInput.indexOf(' ') != -1) {
			i = userInput.indexOf(' ');
			String firstWord = userInput.substring(0, i);
			String subString = userInput.substring(i + 1);
			if (type.equals("error")) {
				commandBarController.setFeedback("Invalid Command" + ": " + subString, Color.RED);
				return;
			} else {
				if(isTasksCommand(firstWord)){
					if(firstWord.equalsIgnoreCase(DELETE_COMMAND)){
						commandBarController.setFeedback("Task has been successfully " + firstWord + "d" + ": " + subString,Color.GREEN);
					}
				     commandBarController.setFeedback("Task has been successfully " + firstWord + "ed" + ": " + subString,Color.GREEN);
				}else if(firstWord.equalsIgnoreCase(SORT_COMMAND)){
					commandBarController.setFeedback("Task has been successfully " + firstWord + "ed " + "by " + subString,Color.GREEN);
				}else if(firstWord.equalsIgnoreCase(OPEN_COMMAND)||firstWord.equalsIgnoreCase(SAVE_COMMAND)||firstWord.equalsIgnoreCase(MOVE_COMMAND)){
					commandBarController.setFeedback("File has been successfully " + firstWord + "ed ",Color.GREEN);
				}else if(firstWord.equalsIgnoreCase(THEME_COMMAND)){
					commandBarController.setFeedback(subString +" "+ firstWord + " has been activated",Color.GREEN);
			    }else{
			    	commandBarController.setFeedback("Invalid Command", Color.RED);
			    }
			}
		} else {
			if(userInput.equalsIgnoreCase(UNDO_COMMAND)){
				 commandBarController.setFeedback("Previous command has been undone",Color.GREEN);
			}
			else if(userInput.equalsIgnoreCase(REDO_COMMAND)){
				commandBarController.setFeedback("Previous Change has been restored",Color.GREEN);
			}
			else if(userInput.equalsIgnoreCase(CLEARUPCOMING_COMMAND)||userInput.equalsIgnoreCase(CLEARCOMPLETE_COMMAND)
					||userInput.equalsIgnoreCase(CLEAROVERDUE_COMMAND) ||userInput.equalsIgnoreCase(CLEARFLOATING_COMMAND)
					||userInput.equalsIgnoreCase(CLEARALL_COMMAND)){
				commandBarController.setFeedback("All tasks have been cleared",Color.GREEN);
			}
			else if(userInput.equalsIgnoreCase(SWITCH_COMMAND)){
				if(tabControl.getPendingTab().isSelected()){
					commandBarController.setFeedback("Switched to pending tab",Color.GREEN);
				}else if(tabControl.getCompleteTab().isSelected()){
					commandBarController.setFeedback("Switched to completed tab",Color.GREEN);
		     	}else if(tabControl.getFloatingTab().isSelected()){
					commandBarController.setFeedback("Switched to floating tab",Color.GREEN);
		     	}else if(tabControl.getOverdueTab().isSelected()){
					commandBarController.setFeedback("Switched to overdue tab",Color.GREEN);
		     	}else if(tabControl.getAllTab().isSelected()){
					commandBarController.setFeedback("Switched to all tab",Color.GREEN);
		     	}
			}else{
				commandBarController.setFeedback("Invalid Command", Color.RED);
			}
		}
	}
	
	private boolean isTasksCommand(String firstWord){
		if(firstWord.equalsIgnoreCase(MARK_COMMAND)||firstWord.equalsIgnoreCase(UNMARK_COMMAND)||firstWord.equalsIgnoreCase(ADD_COMMAND)
				||firstWord.equalsIgnoreCase(DELETE_COMMAND)||firstWord.equalsIgnoreCase(EDIT_COMMAND)){
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

		String[] fragments = null;
		fragments = newValue.split(SPLIT);
		boolean isEdit = fragments[COMMAND_INDEX].equalsIgnoreCase("edit");
		boolean isDelete = fragments[COMMAND_INDEX].equalsIgnoreCase("delete");
		boolean isSearch = fragments[COMMAND_INDEX].equalsIgnoreCase("search");
		boolean isMark = fragments[COMMAND_INDEX].equalsIgnoreCase("mark");
		boolean isUnmark = fragments[COMMAND_INDEX].equalsIgnoreCase("unmark");
        boolean quit = fragments[COMMAND_INDEX].equalsIgnoreCase("q");
        
        if(quit){
        	try {
				checkIsTasksEmpty();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		// TODO Auto-generated method stub
		try {
			if ((tabControl.getAllTab().isSelected()) && isEdit || isDelete || isSearch || isMark || isUnmark) {
				searchResult = logic.handleSearchPending(oldValue, newValue);
				if (isEdit || isDelete || isSearch) {
					populateAllList(searchResult);
				}else if(isMark){
					populateAllList(searchResult);
					populateCompleteList(logic.displayComplete());
				}
			}
			if ((tabControl.getPendingTab().isSelected()) && isEdit || isDelete || isSearch || isMark || isUnmark ) {
				searchResult = logic.handleSearchPending(oldValue, newValue);
				if (isEdit || isDelete || isSearch) {
					populatePendingList(searchResult);		
				}else if(isMark){
					populatePendingList(searchResult);
					populateCompleteList(logic.displayComplete());
				}
			}
			 if ((tabControl.getFloatingTab().isSelected()) && isEdit || isDelete || isSearch || isMark || isUnmark) {
				searchResult = logic.handleSearchPending(oldValue, newValue);
				if (isEdit || isDelete || isSearch) {
					populateFloatingList(searchResult);
				}else if(isMark){
					populateFloatingList(searchResult);
					populateCompleteList(logic.displayComplete());
				}
			}
			 if ((tabControl.getOverdueTab().isSelected()) && isEdit || isDelete || isSearch || isMark || isUnmark) {
				searchResult = logic.handleSearchPending(oldValue, newValue);
				if (isEdit || isDelete || isSearch) {
					populateOverdueList(searchResult);
				}else if(isMark){
					populateOverdueList(searchResult);
					populateCompleteList(logic.displayComplete());
				}
			}
			if ((tabControl.getCompleteTab().isSelected()) &&  isDelete || isSearch || isUnmark ) {
				searchResult = logic.handleSearchCompleted(oldValue,newValue);
				if (isDelete || isSearch) {
					populateCompleteList(searchResult);
				}else if(isUnmark){
					populateCompleteList(searchResult);
					populateAllList(logic.displayPending());
					populatePendingList(logic.displayPending());
					populateOverdueList(logic.displayPending());
					populateFloatingList(logic.displayPending());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void populateAllList(ArrayList<Task> searchResult) {	
		allTableControl.clearTask();
		int count=0;
		for (Task temp : searchResult) {
//			if(searchResult.size()==1){
//				count = 999;
//			}
			allTableControl.addTask(temp,++count);
		}
		
	}

	private void populateOverdueList(ArrayList<Task> searchResult) {
		overdueTableControl.clearTask();
		int count=0;
		for (Task temp : searchResult) {
//			if(searchResult.size()==1){
//				count = 999;
//			}
			if(temp.getStatus()==TASK_STATUS.OVERDUE){
			     overdueTableControl.addTask(temp,++count);
			}
		}	
	}

	private void populateFloatingList(ArrayList<Task> searchResult) {
		floatingTableControl.clearTask();
		int count=0;
		for (Task temp : searchResult) {
//			if(searchResult.size()==1){
//				count = 999;
//			}
			if(temp.getStatus()==TASK_STATUS.FLOATING){
			     floatingTableControl.addTask(temp,++count);
			}
		}	
	}

	private void populateCompleteList(ArrayList<Task> searchResult) {
		completeTableControl.clearTask();
		int count=0;
		for (Task temp : searchResult) {
//			if(searchResult.size()==1){
//				count = 999;
//			}
			if(temp.getStatus()==TASK_STATUS.COMPLETED){
		     	completeTableControl.addTask(temp,++count);
			}
		}	
	}

	private void populatePendingList(ArrayList<Task> searchResult) {
		pendingTableControl.clearTask();
		int count=0;
		for (Task temp : searchResult) {
//			if(searchResult.size()==1){
//				count = 999;
//			}
			if(temp.getStatus()==TASK_STATUS.UPCOMING){
			    pendingTableControl.addTask(temp,++count);
			}
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

	public void moveToLocation(Button btnSave) {
		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File saveFile = fileChooser.showSaveDialog(null);
				if (saveFile != null) {
					logic.moveToLocation(saveFile.getAbsolutePath());
				}
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
			}
		});
	}

//	private void notification(String userInput) {
//		String title = "Successfully ";
//		String message = userInput;
//		NotificationType notification = NotificationType.SUCCESS;
//
//		TrayNotification tray = new TrayNotification();
//		tray.setTitle(title);
//		tray.setMessage(message);
//		tray.setAnimationType(AnimationType.POPUP);
//		tray.setNotificationType(notification);
//		tray.showAndDismiss(Duration.seconds(2));
//
//	}

	private void exit(Button btnExit) {
		btnExit.setMnemonicParsing(true);
		// btnExit.setAccelerator(new
		// KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_DOWN));
		btnExit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
	}

}
