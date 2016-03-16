package main.java.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import main.java.data.Task;
import main.java.flash.Main;

public class EmptyTableController extends BorderPane {

	private static final String FILE_STATS_FXML = "/main/resources/layouts/EmptyTable.fxml";
	
	public EmptyTableController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FILE_STATS_FXML));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	

}
