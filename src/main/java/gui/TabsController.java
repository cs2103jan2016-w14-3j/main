package main.java.gui;

import java.io.IOException;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
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
		upcomingTab.setContent(value);
	}
	
	public void setCompleteTab(Node value){
		completeTab.setContent(value);
	}
}

	