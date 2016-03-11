package main.java.gui;

import java.io.IOException;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import main.java.flash.Main;
import main.java.logic.Logic;
import main.java.data.Task;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class CommandBarController extends BorderPane {


	@FXML
	private Label feedback;

	
	@FXML
	private TextField commandBar;

	private static final String COMMAND_BAR_LAYOUT_FXML = "/main/resources/layouts/CommandBar.fxml";

	private Main mainApp;
	private TasksTableController tableControl;
	private Logic logic;

	public CommandBarController(Main mainApp) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(COMMAND_BAR_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
        searchTasks();
		this.mainApp = mainApp;
	}

	public CommandBarController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(COMMAND_BAR_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void onKeyPress(KeyEvent event) throws Exception {
		mainApp.handleKeyPress(this, event, commandBar.getText());
	}

	public void clear() {
		commandBar.clear();	
	}

	public void setFeedback(String feedbackText) {
		feedback.setText(feedbackText);
		commandBar.setEffect(new DropShadow(15.65,Color.GREEN));
	}

	public void updateUserInput(String newInput) {
        commandBar.setText(newInput);
        commandBar.end();
    }
	
	private void searchTasks(){
		commandBar.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue <?extends String> observable, String oldValue, String newValue) {
					try {
						mainApp.handleSearch(oldValue, newValue);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
	}

	public void setText(String taskname) {
		// TODO Auto-generated method stub
		commandBar.setText("edit " + taskname );
		commandBar.requestFocus();
	}
	
	


}
