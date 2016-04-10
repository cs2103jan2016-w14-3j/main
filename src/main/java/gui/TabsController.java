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

	/** Set content for all tab
	 * @param value
	 */
	public void setAllTab(Node value) {
		allTab.setContent(value);
	}

	/** Set content for floating tab
	 * @param value
	 */
	public void setFloatingTab(Node value) {
		floatingTab.setContent(value);
	}

	/** Set content for pending tab
	 * @param value
	 */
	public void setPendingTab(Node value) {
		pendingTab.setContent(value);
	}

	/** Set content for overdue tab
	 * @param value
	 */
	public void setOverdueTab(Node value) {
		overdueTab.setContent(value);
	}

	/** Set content for complete tab
	 * @param value
	 */
	public void setCompleteTab(Node value) {
		completeTab.setContent(value);
	}

	/** Set Empty content for complete tab
	 */
	public void setEmptyCompleteTab() {
		Image icon = new Image(COMPLETE_IMAGE);
		ImageView iconView = new ImageView(icon);
		completeTab.setContent(iconView);
	}

	/** Set notification for all tab
	 * @param size
	 */
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

	/** Set notification for pending tab
	 * @param size
	 */
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

	/** Set notification for overdue tab
	 * @param size
	 */
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

	/** Set notification for floating tab
	 * @param size
	 */
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

	/** Set notification for complete tab
	 * @param value
	 */
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

	
	/** Get and return all tab
	 * @return
	 */
	public Tab getAllTab() {
		return allTab;
	}

	/** Get and return floating tab
	 * @return
	 */
	public Tab getFloatingTab() {
		return floatingTab;
	}

	/** Get and return pending tab
	 * @return
	 */
	public Tab getPendingTab() {
		return pendingTab;
	}

	/** Get and return overdue tab
	 * @return
	 */
	public Tab getOverdueTab() {
		return overdueTab;
	}

	/** Get and return complete tab
	 * @return
	 */
	public Tab getCompleteTab() {
		return completeTab;
	}

	/** Get and return tab pane
	 * @return
	 */
	public TabPane getTabPane() {
		return tabPane;
	}

}

/* @@author A0124078H */