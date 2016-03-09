package main.java.flash;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.backend.Logic;
import main.java.data.Task;
import main.java.gui.CommandBarController;
import main.java.gui.EmptyTableController;
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
	private EmptyTableController emptyTable;
	private Task task;
	private Boolean isTray = true;
	private ArrayList<Task> result;
	
	
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
	
		result = initLogic();
		if(result.isEmpty()){
	    	rootLayout.setTop(new EmptyTableController());
	    	System.out.println("lalalal");
	    }else{
		  populateList(tableControl,result);
	    }
		// Add components to RootLayout
		//showCommandBar();
		
	}
	
	private ArrayList<Task> initLogic() throws Exception{
		return logic.initLogic();
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
    	int number;
    	//number = Integer.parseInt(userInput);
    	
    	if(userInput.matches("\\d+")){
    	 number = Integer.parseInt(userInput);
    	  logic.delete(result.get(number-1));
    	  commandBarController.setFeedback("success"); 	 
    	 
    	  tableControl.clearTask();
    	  
    	  populateList(tableControl, logic.display());
    	  commandBarController.clear();
    	  
    	  return;
    	  
    	}else{
    	 
        commandBarController.setFeedback("success");
        logControl.addLog(userInput);
    	result = new ArrayList<Task> (logic.handleUserCommand(userInput,result));
    	
    	//if no more tasks
	    if(result.isEmpty()){
	    	rootLayout.setTop(new EmptyTableController());
	    	System.out.println("lalalal");
	    }else{
	    	showTasks(this);    	
	    }
    	tableControl.clearTask();
    	
    	populateList(tableControl,result);
    	
        //abc
    	}
    	
        commandBarController.clear();
    }
    
    private void handleDeleteEnterPress(CommandBarController commandBarController,
            String userInput) throws Exception {
    	
    }
    
    private void populateList(TasksTableController tableControl,ArrayList<Task> result){
    	int count = 1;
    	for (Task temp : result) {
    		tableControl.addTask(temp,count++);
		}
    }
	
}
