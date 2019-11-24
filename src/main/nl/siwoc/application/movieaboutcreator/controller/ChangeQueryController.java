package nl.siwoc.application.movieaboutcreator.controller;

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
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoDetailsCollector;
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
		observableResultsList.clear();
		for (MovieInfoDetailsCollector dc : MovieService.getDetailsCollectors() ) {
			Movie testMovie = new Movie();
			testMovie.setQuery(txtQuery.getText());
			testMovie.setYear(Integer.valueOf(txtYear.getText()));
			boolean result = false;
			try {
				result = dc.getDetails(testMovie);
			} catch (Exception e) {
			}
			if (result) {
				observableResultsList.add(dc.getName() + " Id " + testMovie.getId(dc.getName().toLowerCase()));
			} else {
				observableResultsList.add(dc.getName() + " no result");
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
