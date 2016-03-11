package main.java.flash;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.data.Task;
import main.java.gui.CommandBarController;
import main.java.gui.EmptyTableController;
import main.java.gui.RootLayoutController;
import main.java.gui.TasksTableController;
import main.java.logic.Logic;
import main.java.gui.HistoryLogsController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyEvent;
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
	
	private static final String EMPTY_STRING = "";
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
			rootLayout.setTop(new EmptyTableController());
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

			showCommandBar(this);
			showTasks(this);
			showLog(this);

			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showTasks(Main mianApp) {
		rootLayout.setTop(tableControl);
	}

	private void showCommandBar(Main mainApp) {
		rootLayout.setBottom(barControl);
	}

	private void showLog(Main mainApp) {
		rootLayout.setCenter(logControl);
	}

	public void handleKeyPress(CommandBarController commandBarController, KeyEvent event, String text) throws Exception {
		// TODO Auto-generated method stub
		if (event.getCode() == KeyCode.ENTER) {
			handleEnterPress(commandBarController, text);
		}else if ((event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) && !historyLog.isEmpty()) {
             event.consume(); // nullifies the default behavior of UP and DOWN on a TextArea
             handleGetPastCommands(event);
		}
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
        }else {
        	pointer = historyLog.size()-1;
        }
        return historyLog.get(pointer);
    }

    private String getNextCommand() {
        if (pointer < historyLog.size() - 1) {
            pointer++;
        }else{
        	pointer = 0;
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
            //check if is delete or edit
			if (isChange) {
				handleEditWithNumber(number);
			} else {
				handleDeleteWithNumber(number);
			}
			setFeedback(commandBarController);

			// if no more tasks
			if (isListEmpty()) {
				rootLayout.setTop(new EmptyTableController());
				// System.out.println("lalalal");
			} else {
				showTasks(this);
			}
			
			tableControl.clearTask();
			populateList(tableControl, logic.display());
            
			commandBarController.clear();

			isChange = false;

			return;

		}else if (userInput.isEmpty()){
			return;
		}else {
            //normal command
			setFeedback(commandBarController);
			logControl.addLog(userInput);
			historyLog.add(userInput);
			result = new ArrayList<Task>(logic.handleUserCommand(userInput, result));

			// if no more tasks
			if (result.isEmpty()) {
				rootLayout.setTop(new EmptyTableController());
				// System.out.println("lalalal");
			} else {
				showTasks(this);
			}
			tableControl.clearTask();
			
			if(isListEmpty()){
				commandBarController.clear();
				return;
			}

			if (logic.isEditCommand(userInput)) {
				handleEditCommand();
			}
			if (logic.isDeleteCommand(userInput)) {
				handleDeleteCommand();
			}

			populateList(tableControl, result);

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

	private void setFeedback(CommandBarController commandBarController) {
		commandBarController.setFeedback("success");
	}

	private void handleDeleteWithNumber(int number) throws Exception {
		logic.delete(result.get(number - 1));
	}

	private void handleEditWithNumber(int number) throws Exception {
		finalResult.add(result.get(number - 1));
		logic.edit(finalResult);
		finalResult.clear();
	}

	private void populateList(TasksTableController tableControl, ArrayList<Task> result) {
		int count = 1;

		for (Task temp : result) {
			tableControl.addTask(temp, count++);
		}
	}

}
