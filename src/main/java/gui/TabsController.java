/* @@author A0124078H */
package main.java.gui;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Shape;

public class TabsController extends BorderPane {

	@FXML private TabPane tabPane;
	@FXML private Tab allTab;
	@FXML private Tab pendingTab;
	@FXML private Tab floatingTab;
	@FXML private Tab overdueTab;
	@FXML private Tab completeTab;
	@FXML private Label allNotify;
	@FXML private Label pendingNotify;
	@FXML private Label floatingNotify;
	@FXML private Label overdueNotify;
	@FXML private Label completeNotify;
	@FXML private MenuButton setting;
	@FXML private Button btnNew;
	@FXML private Button btnLoad;
	@FXML private Button btnSave;
	@FXML private Button btnHelp;
	@FXML private Button btnSetting;
	@FXML private Button btnExit;
	@FXML private Shape circleAll;
	@FXML private Shape circlePending;
	@FXML private Shape circleOverdue;
	@FXML private Shape circleFloating;
	@FXML private Shape circleComplete;

	protected File file;

	private static final String COMMAND_BAR_LAYOUT_FXML = "/main/resources/layouts/TasksTabs.fxml";
	private static final String COMPLETE_IMAGE = "/main/resources/images/complete.png";

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

	public void setAllTab(Node value) {
		allTab.setContent(value);
	}

	public void setFloatingTab(Node value) {
		floatingTab.setContent(value);
	}

	public void setPendingTab(Node value) {
		pendingTab.setContent(value);
	}

	public void setOverdueTab(Node value) {
		overdueTab.setContent(value);
	}

	public void setCompleteTab(Node value) {
		completeTab.setContent(value);
	}

	public void setEmptyCompleteTab() {
		Image icon = new Image(COMPLETE_IMAGE);
		ImageView iconView = new ImageView(icon);
		completeTab.setContent(iconView);
	}

	public void setAllNotification(int size) {
		if (size == 0) {
			circleAll.managedProperty().bind(circleAll.visibleProperty());
			circleAll.setVisible(false);
			allNotify.setText("");
		} else {
			allNotify.setText(String.valueOf(size));
			circleAll.managedProperty().bind(circleAll.visibleProperty());
			circleAll.setVisible(true);
		}
	}

	public void setPendingNotification(int size) {
		if (size == 0) {
			circlePending.managedProperty().bind(circlePending.visibleProperty());
			circlePending.setVisible(false);
			pendingNotify.setText("");
		} else {
			pendingNotify.setText(String.valueOf(size));
			circlePending.managedProperty().bind(circlePending.visibleProperty());
			circlePending.setVisible(true);
		}

	}

	public void setOverdueNotification(int size) {
		if (size == 0) {
			circleOverdue.managedProperty().bind(circleOverdue.visibleProperty());
			circleOverdue.setVisible(false);
			overdueNotify.setText("");
		} else {
			overdueNotify.setText(String.valueOf(size));
			circleOverdue.managedProperty().bind(circleOverdue.visibleProperty());
			circleOverdue.setVisible(true);
		}

	}

	public void setFloatingNotification(int size) {
		if (size == 0) {
			circleFloating.managedProperty().bind(circleFloating.visibleProperty());
			circleFloating.setVisible(false);
			floatingNotify.setText("");
		} else {
			floatingNotify.setText(String.valueOf(size));
			circleFloating.managedProperty().bind(circleFloating.visibleProperty());
			circleFloating.setVisible(true);
		}

	}

	public void setCompletedNotification(int size) {
		if (size == 0) {
			circleComplete.managedProperty().bind(circleComplete.visibleProperty());
			circleComplete.setVisible(false);
			completeNotify.setText("");
		} else {
			completeNotify.setText(String.valueOf(size));
			circleComplete.managedProperty().bind(circleComplete.visibleProperty());
			circleComplete.setVisible(true);
		}
	}

	public Tab getAllTab() {
		return allTab;
	}

	public Tab getFloatingTab() {
		return floatingTab;
	}

	public Tab getPendingTab() {
		return pendingTab;
	}

	public Tab getOverdueTab() {
		return overdueTab;
	}

	public Tab getCompleteTab() {
		return completeTab;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

}

/* @@author A0124078H */