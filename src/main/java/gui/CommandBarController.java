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
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import main.java.flash.Main;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class CommandBarController extends BorderPane {

	@FXML
	private Label feedback;
	
	@FXML
	private Shape feedbackBg;
	
	
	@FXML
	private TextField commandBar;

	private Main mainApp;
	private Timeline feedbackTimeline;
	private static final String COMMAND_BAR_LAYOUT_FXML = "/main/resources/layouts/CommandBar.fxml";
	private static final int FADE_IN_TIME = 1000;
	private static final int FADE_OUT_TIME = 1000;
	private static final int FEEDBACK_DISPLAY = 3;

	public CommandBarController(Main mainApp) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(COMMAND_BAR_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		search();
		this.mainApp = mainApp;
		commandBar.getStyleClass().add("default-commandBar");
		feedbackTimeline = new Timeline();
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
		search();
		commandBar.getStyleClass().add("default-commandBar");
		feedbackTimeline = new Timeline();
	}

	@FXML
	public void onKeyPress(KeyEvent event) throws Exception {

		mainApp.handleKeyPress(this, event, commandBar.getText());
	}

	public void clear() {
		commandBar.clear();
		commandBar.getStyleClass().add("default-commandBar");
	}

	public TextField getCommandBar() {
		return commandBar;
	}

	public void updateUserInput(String newInput) {
		commandBar.setText(newInput);
		commandBar.end();
	}

	private void search() {

		commandBar.textProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				Platform.runLater(() -> {
					try {
						mainApp.trySearch((String) oldValue, (String) newValue);
						mainApp.showColourCommand((String) oldValue, (String) newValue);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		});

	}

	
	public void getFocus() {
		commandBar.requestFocus();
		commandBar.positionCaret(0);
		commandBar.selectAll();
	}

	public void setText(String taskname) {
		commandBar.setText(taskname);
		commandBar.requestFocus();
	}

	public void setBgColour(String colour) {
		commandBar.getStyleClass().add(colour);
	}

	public void setFeedback(String feedbackText, Color color) {
		FadeTransition fadeIn = startFadeIn(feedback, FADE_IN_TIME);
		FadeTransition fadeOut = startFadeOut(feedback, FADE_OUT_TIME);
		feedback.setTextFill(color);
		feedbackTimeline.stop();
		feedbackTimeline = startTimeline(feedbackText, fadeIn, fadeOut);
		feedbackTimeline.play();
	}

	private FadeTransition startFadeIn(Node node, int duration) {
		FadeTransition fadeIn = new FadeTransition(new Duration(duration));
		fadeIn.setNode(node);
		fadeIn.setToValue(1);
		return fadeIn;
	}

	private FadeTransition startFadeOut(Node node, int duration) {
		FadeTransition fadeOut = new FadeTransition(new Duration(duration));
		fadeOut.setNode(node);
		fadeOut.setToValue(0);
		return fadeOut;
	}

	private Timeline startTimeline(String feedbackText, FadeTransition fadeIn, FadeTransition fadeOut) {
		return new Timeline(new KeyFrame(new Duration(1), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setFeedbackLabel(feedbackText);
				fadeIn.play();
			}
		}), new KeyFrame(Duration.seconds(FEEDBACK_DISPLAY), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				fadeOut.play();
			}
		}));
	}

	private void setFeedbackLabel(String feedbackText) {
		feedback.setOpacity(0);
		feedback.setText(feedbackText);
	}

}
