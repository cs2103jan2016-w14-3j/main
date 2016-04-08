/* @@author A0124078H */
package main.java.gui;

import javafx.animation.Animation;

import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Animates a node on and off screen to the left. */
public class SideBarController extends VBox {

	private final Button controlButton;
	private Animation hideSidebar;
	private Animation showSidebar;
	private HBox header;

	/** creates a sidebar containing a vertical alignment of the given nodes */
	public SideBarController(final double expandedWidth, Node... nodes) {

		getStyleClass().add("sidebar");
		this.setPrefWidth(expandedWidth);
		this.setMinWidth(0);

		// create a bar to hide and show.
		setAlignment(Pos.CENTER);
		getChildren().addAll(nodes);

		// create a button to hide and show the sidebar.
		controlButton = new Button();
		controlButton.getStyleClass().add("sideButton");

		// apply the animations when the button is pressed.
		setMenuControlButton(expandedWidth);

	}
	/* @@author A0124078H */
	private void setMenuControlButton(final double expandedWidth) {
		controlButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				// create an animation to hide sidebar.
				hideSidebar = new Transition() {
					{
						setCycleDuration(Duration.millis(150));
					}

					protected void interpolate(double frac) {
						final double curWidth = expandedWidth * (1.0 - frac);
						setPrefWidth(curWidth);
						setTranslateX(-expandedWidth + curWidth);
					}
				};

				hideSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						setVisible(false);
						controlButton.getStyleClass().remove("sideButton");
						controlButton.getStyleClass().add("sideButton1");
					}
				});

				// create an animation to show a sidebar.
				showSidebar = new Transition() {
					{
						setCycleDuration(Duration.millis(150));
					}

					protected void interpolate(double frac) {
						final double curWidth = expandedWidth * frac;
						setPrefWidth(curWidth);
						setTranslateX(-expandedWidth + curWidth);
					}
				};
				
				showSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						controlButton.getStyleClass().add("sideButton");
						controlButton.getStyleClass().remove("sideButton1");
					}
				});

				if (showSidebar.statusProperty().get() == Animation.Status.STOPPED
						&& hideSidebar.statusProperty().get() == Animation.Status.STOPPED) {
					if (isVisible()) {
						hideSidebar.play();
					} else {
						setVisible(true);
						showSidebar.play();
					}
				}

			}
		});
	}
	/* @@author A0124078H */
	/** @return a control button to hide and show the sidebar */
	public HBox getControlButton() {
		header = new HBox();
		header.getChildren().add(controlButton);
		return header;
	}

	public void hideSidebar() {
		controlButton.fire();
	}

}
/* @@author A0124078H */