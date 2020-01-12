/*******************************************************************************
 * Copyright (c) 2019-2020 Niek Knijnenburg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package nl.siwoc.application.movieaboutcreator.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nl.siwoc.application.movieaboutcreator.model.Movie;

public class ChangePlotController {

	@FXML
	AnchorPane changePlotPane;
	
	@FXML
	TextArea txtPlot;
	
	
	private Pane rootPane;
	private MainController mainController;
	private Stage changePlotStage;
	private Movie movie;
	
	public Pane getRootPane() {
		return rootPane;
	}

	public void setRootPane(Pane rootPane) {
		this.rootPane = rootPane;
	}

	public MainController getMainController() {
		return mainController;
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public Stage getChangePlotStage() {
		return changePlotStage;
	}

	public void setChangePlotStage(Stage changePlotStage) {
		this.changePlotStage = changePlotStage;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	
	public void initData(Pane _rootPane, MainController _mainController, Stage _changePlotStage, Movie _movie) {
		setRootPane(_rootPane);
		setMainController(_mainController);
		setChangePlotStage(_changePlotStage);
		setMovie(_movie);
		txtPlot.setText(getMovie().getPlot());
		getRootPane().setEffect(new GaussianBlur());
		getChangePlotStage().show();
		
	}
	

	public void usePlot(ActionEvent event) {
		use();
		close();
		
	}
	
	private void use() {
		getMovie().setPlot(txtPlot.getText());
		getMainController().valuesChanged(getMovie());
	}
	
	public void cancel(ActionEvent event) {
		close();
	}
	
	public void close() {
		rootPane.setEffect(null);
        getChangePlotStage().hide();
	}

}
