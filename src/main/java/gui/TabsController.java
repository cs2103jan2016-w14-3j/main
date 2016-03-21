package main.java.gui;

import java.io.IOException;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import main.java.flash.Main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;


public class TabsController extends BorderPane {
	
	@FXML
	private Tab upcomingTab;	
	
	@FXML
	private Tab completeTab;	
	
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

	