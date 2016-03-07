package main.java.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import main.java.flash.Main;

public class HistoryLogsController extends BorderPane {

	private Main mainApp;

	@FXML
	private ListView<String> msgLog;
	
	@FXML
	private ListView<String> listView;

//	@FXML
//	private Label title;

	private static final String HISTORY_LOG_FXML = "/main/resources/layouts/HistoryLog.fxml";

	private ArrayList<String> logs;
	
	public HistoryLogsController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(HISTORY_LOG_FXML));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//title.setText(taskName);
		initialise();
	}
	
	private void initialise() {
		logs = new ArrayList<String>();
		msgLog.setItems(FXCollections.observableArrayList(logs));
	}
	public void showLog(){
		
	}

	public void addLog(String taskName) {	
	
		addFileStatsItem(taskName);
		msgLog.setItems(FXCollections.observableArrayList(logs));
	}

	/**
	 * Each FileStatsItem corresponds to a source file and is displayed as a row
	 * in this custom view.
	 * 
	 * @param currentFile
	 * @param currentNumLines
	 */
	private void addFileStatsItem(String taskName) {
		logs.add(new String(taskName));
	}

}



