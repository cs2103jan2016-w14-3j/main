package main.java.gui;

import java.io.File;
import java.io.IOException;




import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class TabsController extends BorderPane {
	
	@FXML
	private Tab upcomingTab;	
	
	@FXML
	private Tab completeTab;	
	
	@FXML
	private MenuButton setting;
    private MenuItem loadFile;
    private MenuItem exitItem;
    
    @FXML
	private Button btnNew;
    @FXML
	private Button btnLoad;
    @FXML
	private Button btnSave;
    @FXML
	private Button btnHelp;
    @FXML
	private Button btnSetting;
    @FXML
	private Button btnExit;
    
    @FXML
	private Label lblUpcoming;
    @FXML
	private Label lblComplete;
    
    
	protected File file;

	private MenuItem saveFile;

	private MenuItem helpPage;
	
	
	private static final String COMMAND_BAR_LAYOUT_FXML = "/main/resources/layouts/TasksTabs.fxml";

	public TabsController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(COMMAND_BAR_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initSetting();
		initButton();
		exit();
	}
	
	private void initButton() {
		btnNew = new Button();
		btnLoad = new Button();
        btnSave = new Button();
	    btnHelp = new Button();
	    btnSetting = new Button();
	    btnExit = new Button();
	    
	    btnNew.setVisible(false);
	    btnLoad.setVisible(false);
	    btnSave.setVisible(false);
	    btnHelp.setVisible(false);
	    btnExit.setVisible(false);
	    btnSetting.setVisible(false);
	    //Image image = new Image("/main/resources/images/loadFile.png");
	   // btnSetting.setGraphic(new ImageView(image));
		
	}

	private void initSetting(){			
		loadFile = new MenuItem("Load...", null);
		saveFile = new MenuItem("Save...", null);
		helpPage = new MenuItem("Help", null);
		exitItem = new MenuItem("Exit", null);    
	    setting.getItems().addAll(loadFile,saveFile,helpPage,exitItem);
	    Image image = new Image("/main/resources/images/loadFile.png");
	    setting.setGraphic(new ImageView(image));
	}
	

	private void exit() {
		exitItem.setMnemonicParsing(true);
	    exitItem.setAccelerator(new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_DOWN));
	    exitItem.setOnAction(new EventHandler<ActionEvent>() {
	      public void handle(ActionEvent event) {
	        Platform.exit();
	      }
	    });
	}
	
	public MenuItem getLoadMenu(){
		return loadFile;
	}
	
	public MenuItem getSaveMenu(){
		return saveFile;
	}
	
	public void setUpcomingLabel(String text){
		lblUpcoming.setText(text); 
	}
	
	public void setCompleteLabel(String text){
		lblComplete.setText(text); 
	}
	
//	public String saveFilename(){
//	   saveFile.setOnAction(new EventHandler<ActionEvent>(){
//        @Override
//       public void handle(ActionEvent arg0) {
//           FileChooser fileChooser = new FileChooser();
//           FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
//           fileChooser.getExtensionFilters().add(extFilter);
//           file = fileChooser.showSaveDialog(null);
//           System.out.println(file);      
//       }
//      });
//	   return file.getAbsolutePath();
//	}
	
//	public String loadFilename(){
//		
//		   loadFile.setOnAction(new EventHandler<ActionEvent>(){
//	        @Override
//	       public void handle(ActionEvent arg0) {
//	           FileChooser fileChooser = new FileChooser();
//	           FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
//	           fileChooser.getExtensionFilters().add(extFilter);
//	           file = fileChooser.showOpenDialog(null);
//	           System.out.println(file);      
//	       }
//	      });
//		   return file.getAbsolutePath();
//		}
	
	public void setUpcomingTab(Node value){
		Image icon = new Image("/main/resources/images/upcomingIcon.fw.png");
		ImageView iconView = new ImageView(icon);
		upcomingTab.setGraphic(iconView);
		upcomingTab.setContent(value);
	}
	
	public void setCompleteTab(Node value){
		Image icon = new Image("/main/resources/images/completeIcon.fw.png");
		ImageView iconView = new ImageView(icon);
	    completeTab.setGraphic(iconView);
		completeTab.setContent(value);
	}
	
	public void setEmptyCompleteTab(){
		Image icon = new Image("/main/resources/images/complete.png");
		ImageView iconView = new ImageView(icon);
		completeTab.setContent(iconView);
	}
	
	public Tab getUpcomingTab(){
		return upcomingTab;
	}
	
	public Tab getCompleteTab(){
		return completeTab;
	}
}

	