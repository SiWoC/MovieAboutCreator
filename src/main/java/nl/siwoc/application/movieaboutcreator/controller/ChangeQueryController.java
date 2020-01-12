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

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoBackgroundCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoDetailsCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoFolderCollector;
import nl.siwoc.application.movieaboutcreator.model.Movie;
import nl.siwoc.application.movieaboutcreator.service.MovieService;

public class ChangeQueryController {

	@FXML
	AnchorPane changeQueryPane;

	@FXML
	TextField txtQuery;

	@FXML
	TextField txtYear;

	@FXML
	ListView<String> lvResults;
	private ObservableList<String> observableResultsList = FXCollections.observableArrayList();

	private Pane rootPane;
	private MainController mainController;
	private Stage changeQueryStage;
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

	public Stage getChangeQueryStage() {
		return changeQueryStage;
	}

	public void setChangeQueryStage(Stage changeQueryStage) {
		this.changeQueryStage = changeQueryStage;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public void initialize() {
		lvResults.setItems(observableResultsList);
	}

	public void initData(Pane _rootPane, MainController _mainController, Stage _changeQueryStage, Movie _movie) {
		setRootPane(_rootPane);
		setMainController(_mainController);
		setChangeQueryStage(_changeQueryStage);
		setMovie(_movie);
		txtQuery.setText(getMovie().getQuery());
		txtYear.setText(String.valueOf(getMovie().getYear()));
		getRootPane().setEffect(new GaussianBlur());
		getChangeQueryStage().show();

	}

	public void testQuery(ActionEvent event) {
		ArrayList<String> tested = new ArrayList<String>();
		observableResultsList.clear();
		Movie testMovie = new Movie();
		testMovie.setQuery(txtQuery.getText());
		testMovie.setYear(Integer.valueOf(txtYear.getText()));
		boolean result = false;
		// Background
		for (MovieInfoBackgroundCollector bc : MovieService.getBackgroundCollectors() ) {
			result = false;
			if (!tested.contains(bc.getName())) {
				tested.add(bc.getName());
				try {
					result = (bc.getBackground(testMovie).length > 0);
				} catch (Exception e) {
				}
				if (result) {
					observableResultsList.add(bc.getName() + " Id " + testMovie.getId(bc.getIdType()));
				} else {
					observableResultsList.add(bc.getName() + " no result");
				}
			}
		}
		// Folder
		for (MovieInfoFolderCollector fc : MovieService.getFolderCollectors() ) {
			result = false;
			if (!tested.contains(fc.getName())) {
				tested.add(fc.getName());
				try {
					result = (fc.getFolder(testMovie).length > 0);
				} catch (Exception e) {
				}
				if (result) {
					observableResultsList.add(fc.getName() + " Id " + testMovie.getId(fc.getIdType()));
				} else {
					observableResultsList.add(fc.getName() + " no result");
				}
			}
		}
		// Details
		for (MovieInfoDetailsCollector dc : MovieService.getDetailsCollectors() ) {
			result = false;
			if (!tested.contains(dc.getName())) {
				tested.add(dc.getName());
				try {
					result = dc.getDetails(testMovie);
				} catch (Exception e) {
				}
				if (result) {
					observableResultsList.add(dc.getName() + " Id " + testMovie.getId(dc.getIdType()));
				} else {
					observableResultsList.add(dc.getName() + " no result");
				}
			}
		}
	}

	public void useQueryAndRenameFile(ActionEvent event) {
		getMainController().renameMovie(txtQuery.getText() + " (" + txtYear.getText() + ")");
		use();
		close();
	}

	public void useQuery(ActionEvent event) {
		use();
		close();

	}

	private void use() {
		getMovie().clearIds();
		getMovie().setQuery(txtQuery.getText());
		getMovie().setYear(Integer.valueOf(txtYear.getText()));
		getMainController().reGenerate(null);
	}

	public void cancel(ActionEvent event) {
		close();
	}

	public void close() {
		rootPane.setEffect(null);
		getChangeQueryStage().hide();
	}

}
