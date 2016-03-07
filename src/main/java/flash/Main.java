package main.java.flash;

import java.io.IOException;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.backend.Logic;
import main.java.data.Task;
import main.java.gui.CommandBarController;
import main.java.gui.RootLayoutController;
import main.java.gui.TasksTableController;
import main.java.gui.HistoryLogsController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	private TasksTableController tableControl = new TasksTableController();
	private HistoryLogsController logControl = new HistoryLogsController();
	private Logic logic = new Logic(); 
	private Task task;
	private Boolean isTray = true;
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		//this.primaryStage.initStyle(StageStyle.TRANSPARENT);;
		this.primaryStage.setTitle("Flashpoint");
		this.primaryStage.getIcons().add(new Image("/main/resources/images/lightning.fw.png"));

		initRootLayout();
		

		initLogic();

		// Add components to RootLayout
		//showCommandBar();
		
	}
	
	private void initLogic() throws Exception{
		logic.initLogic();
	}
	
	 /**
     * Initialises the RootLayout that will contain all other JavaFX components.
     */
	private void initRootLayout(){
		
		try{
			//load root layout from fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource
					("/main/resources/layouts/RootLayout.fxml"));
			rootLayout = loader.load();
			
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			      if(key.getCode()==KeyCode.ESCAPE ) {
			    	  primaryStage.hide();
			      }
			});
			
			RootLayoutController rootController = new RootLayoutController(this);
			
			showCommandBar(this);
			showTasks(this);
			showLog(this);
			
			primaryStage.show();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void showTasks(Main mianApp){
		rootLayout.setTop(tableControl);
	}
	
	private void showCommandBar(Main mainApp){
		rootLayout.setBottom(new CommandBarController(mainApp));
	}
	
	private void showLog(Main mainApp){
		rootLayout.setCenter(logControl);
	}

	public void handleKeyPress(CommandBarController commandBarController, KeyCode code, String text) throws Exception {
		// TODO Auto-generated method stub
		if (code == KeyCode.ENTER) {
            handleEnterPress(commandBarController, text);
        }
    }

    private void handleEnterPress(CommandBarController commandBarController,
                                  String userInput) throws Exception {
    	commandBarController.setFeedback("success");
    	task = logic.handleUserCommand(userInput);
        tableControl.addTask(task);
        logControl.addLog(userInput);
        commandBarController.clear();
    }
	
}
