package main.java.gui;

import java.io.IOException;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;

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

public class CommandBarController extends BorderPane {


	@FXML
	private Label feedback;

	@FXML
	private TextField commandBar;

	private static final String COMMAND_BAR_LAYOUT_FXML = "/main/resources/layouts/CommandBar.fxml";

	private Main mainApp;
	private TasksTableController tableControl;

	public CommandBarController(Main mainApp) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(COMMAND_BAR_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.mainApp = mainApp;
	}

	@FXML
	public void onKeyPress(KeyEvent event) {
		mainApp.handleKeyPress(this, event.getCode(), commandBar.getText());
	}

	public void clear() {
		commandBar.clear();	
	}

	public void setFeedback(String feedbackText) {
		feedback.setText(feedbackText);
		commandBar.setEffect(new DropShadow(15.65,Color.GREEN));
	}


}
