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
import javafx.scene.control.TabPane;
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
	private TabPane tabPane;	
	
	@FXML
	private Tab upcomingTab;
	@FXML
	private Label pendingNotify;	
	@FXML
	private Tab allTab;	
	@FXML
	private Label allNotify;
	@FXML
	private Tab floatingTab;	
	@FXML
	private Label floatingNotify;
	@FXML
	private Tab overdueTab;	
	@FXML
	private Label overdueNotify;	
	@FXML
	private Tab completeTab;
	@FXML
	private Label completeNotify;	
	
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

	}
	
	
	public void setUpcomingLabel(String text){
		lblUpcoming.setText(text); 
	}
	
	public void setCompleteLabel(String text){
		lblComplete.setText(text); 
	}
	
	public void setUpcomingTab(Node value){
//		Image icon = new Image("/main/resources/images/upcomingIcon.fw.png");
//		ImageView iconView = new ImageView(icon);
//		upcomingTab.setGraphic(iconView);
		upcomingTab.setContent(value);
	}
	
	public void setAllTab(Node value){
		Image icon = new Image("/main/resources/images/upcomingIcon.fw.png");
		ImageView iconView = new ImageView(icon);
		//allTab.setGraphic(iconView);
		allNotify.setText("11");
		allTab.setContent(value);
	}
	
	public void setFloatingTab(Node value){
		Image icon = new Image("/main/resources/images/upcomingIcon.fw.png");
		ImageView iconView = new ImageView(icon);
		//allTab.setGraphic(iconView);
		floatingNotify.setText("11");
		floatingTab.setContent(value);
	}
	
	public void setPendingTab(Node value){
		Image icon = new Image("/main/resources/images/upcomingIcon.fw.png");
		ImageView iconView = new ImageView(icon);
		//allTab.setGraphic(iconView);
		pendingNotify.setText("11");
		upcomingTab.setContent(value);
	}
	
	public void setOverdueTab(Node value){
		Image icon = new Image("/main/resources/images/upcomingIcon.fw.png");
		ImageView iconView = new ImageView(icon);
		//allTab.setGraphic(iconView);
		overdueNotify.setText("11");
		overdueTab.setContent(value);
	}
	
	public void setCompleteTab(Node value){
//		Image icon = new Image("/main/resources/images/completeIcon.fw.png");
//		ImageView iconView = new ImageView(icon);
//	    completeTab.setGraphic(iconView);
		completeNotify.setText("11");
		completeTab.setContent(value);
	}
	
	public void setEmptyCompleteTab(){
		Image icon = new Image("/main/resources/images/complete.png");
		ImageView iconView = new ImageView(icon);
		completeTab.setContent(iconView);
	}

	
	public void setAllNotification(int size){
		allNotify.setText(String.valueOf(size));
	}
	public void setPendingNotification(int size){
		pendingNotify.setText(String.valueOf(size));
	}
	public void setOverdueNotification(int size){
		overdueNotify.setText(String.valueOf(size));
	}
	public void setFloatingNotification(int size){
		floatingNotify.setText(String.valueOf(size));
	}
	public void setCompletedNotification(int size){
		completeNotify.setText(String.valueOf(size));
	}
	
	
	
	public Tab getAllTab(){
		return allTab;
	}
	
	public Tab getFloatingTab(){
		return completeTab;
	}
	public Tab getPendingTab(){
		return upcomingTab;
	}
	
	public Tab getOverdueTab(){
		return completeTab;
	}
	
	public Tab getCompleteTab(){
		return completeTab;
	}
	
	public TabPane getTabPane(){
		return tabPane;
	}

}

	