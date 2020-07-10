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

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import nl.siwoc.application.movieaboutcreator.Main;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoBackgroundCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoDetailsCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoFolderCollector;
import nl.siwoc.application.movieaboutcreator.model.Movie;
import nl.siwoc.application.movieaboutcreator.service.MovieService;
import nl.siwoc.application.movieaboutcreator.utils.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainController {

	protected static final Logger LOG = LoggerFactory.getLogger(MainController.class);

	private File htmlFile = new File ("generated/template.html");
	private File folderImageFile = new File("generated/folder.jpg");
	private File backgroundImageFile = new File("generated/background.jpg");
	private File aboutImageFile = new File("generated/about.png");
	private File setValuesFile = new File("generated/setvalues.js");

	@FXML
	AnchorPane rootPane;

	@FXML
	CheckBox chkOnlyNew;

	@FXML
	TextField txtMoviesFolderName;

	@FXML
	ListView<Movie> lvMovies;

	@FXML
	TextArea txtStatusArea;

	@FXML
	WebView wvHtmlPreview;

	@FXML
	AnchorPane rightSidePane;

	@FXML
	ChoiceBox<MovieInfoBackgroundCollector> cbBackgroundCollector;

	@FXML
	ChoiceBox<MovieInfoFolderCollector> cbFolderCollector;

	@FXML
	ChoiceBox<MovieInfoDetailsCollector> cbDetailsCollector;

	private MovieService model;

	public File getFolderImageFile() {
		return folderImageFile;
	}

	public void setFolderImageFile(File folderImageFile) {
		this.folderImageFile = folderImageFile;
	}

	public File getBackgroundImageFile() {
		return backgroundImageFile;
	}

	public void setBackgroundImageFile(File backgroundImageFile) {
		this.backgroundImageFile = backgroundImageFile;
	}

	public File getAboutImageFile() {
		return aboutImageFile;
	}

	public void setAboutImageFile(File aboutImageFile) {
		this.aboutImageFile = aboutImageFile;
	}

	public File getSetValuesFile() {
		return setValuesFile;
	}

	public void setSetValuesFile(File setValuesFile) {
		this.setValuesFile = setValuesFile;
	}

	public String getStatusLine() {
		return txtStatusArea.getText();
	}

	public void setStatusLine(String statusLineText) {
		setStatusLine(statusLineText, null);
	}

	public void setStatusLine(String statusLineText, Throwable e) {
		if (txtStatusArea != null) {
			LOG.info(statusLineText);
			txtStatusArea.setText(statusLineText + "\r\n" + txtStatusArea.getText().substring(0, Math.min(txtStatusArea.getLength(), 2000)));
		}
		if (e != null) {
			LOG.error(statusLineText, e);
		}
	}

	public void initialize() {
		setStatusLine("Initializing");
		wvHtmlPreview.setContextMenuEnabled(true);
		model = new MovieService();
		model.setController(this);
		txtMoviesFolderName.setText(Properties.getMoviesFolderName());
		chkOnlyNew.setSelected(Properties.getOnlyNew());
		model.setMoviesFolderName(txtMoviesFolderName.getText());
		lvMovies.setItems(model.getMovies());
		lvMovies.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Movie>() {
			@Override
			public void changed(ObservableValue<? extends Movie> observable, Movie oldValue, Movie selectedMovie) {
				if (selectedMovie != null) {
					getMovieInfo(selectedMovie);
				}
			}
		});
		cbBackgroundCollector.setItems(MovieService.getBackgroundCollectors());
		cbBackgroundCollector.getSelectionModel().select(model.getBackgroundCollector());
		cbBackgroundCollector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MovieInfoBackgroundCollector>() {
			@Override
			public void changed(ObservableValue<? extends MovieInfoBackgroundCollector> observable, MovieInfoBackgroundCollector oldValue, MovieInfoBackgroundCollector selectedBackgroundCollector) {
				if (selectedBackgroundCollector != null) {
					model.setBackgroundCollector(selectedBackgroundCollector);
					Properties.setBackgroundCollectorName(selectedBackgroundCollector.toString());
				}
			}
		});
		cbFolderCollector.setItems(MovieService.getFolderCollectors());
		cbFolderCollector.getSelectionModel().select(model.getFolderCollector());
		cbFolderCollector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MovieInfoFolderCollector>() {
			@Override
			public void changed(ObservableValue<? extends MovieInfoFolderCollector> observable, MovieInfoFolderCollector oldValue, MovieInfoFolderCollector selectedFolderCollector) {
				if (selectedFolderCollector != null) {
					model.setFolderCollector(selectedFolderCollector);
					Properties.setFolderCollectorName(selectedFolderCollector.toString());
				}
			}
		});
		cbDetailsCollector.setItems(MovieService.getDetailsCollectors());
		cbDetailsCollector.getSelectionModel().select(model.getDetailsCollector());
		cbDetailsCollector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MovieInfoDetailsCollector>() {
			@Override
			public void changed(ObservableValue<? extends MovieInfoDetailsCollector> observable, MovieInfoDetailsCollector oldValue, MovieInfoDetailsCollector selectedDetailsCollector) {
				if (selectedDetailsCollector != null) {
					model.setDetailsCollector(selectedDetailsCollector);
					Properties.setDetailsCollectorName(selectedDetailsCollector.toString());
				}
			}
		});
		rightSidePane.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				setPreviewZoom();
			}
		});
		setPreviewZoom();
		setStatusLine("Initialized");
	}

	private void setPreviewZoom() {
		wvHtmlPreview.setZoom((rightSidePane.getWidth() - 4) / 1280);
	}

	public void browseMoviesFolder(ActionEvent event) {
		DirectoryChooser folderChooser = new DirectoryChooser();
		File moviesFolder = new File(txtMoviesFolderName.getText());
		if (moviesFolder.isDirectory()) {
			folderChooser.setInitialDirectory(moviesFolder);
		}
		folderChooser.setTitle("Where are your movies stored?");
		File selectedFolder = folderChooser.showDialog((Stage)rootPane.getScene().getWindow());
		if (selectedFolder != null) {
			try {
				txtMoviesFolderName.setText(selectedFolder.getCanonicalPath());
			} catch (IOException e) {
				// ???
			}
			refreshMovies(event);
		}
	}
	
	public void toggleOnlyNew (ActionEvent event) {
		Properties.setOnlyNew(chkOnlyNew.isSelected());
		model.refreshMovies();
	}

	public void refreshMovies(ActionEvent event) {
		model.setMoviesFolderName(txtMoviesFolderName.getText());
		model.refreshMovies();
		Properties.setMoviesFolderName(txtMoviesFolderName.getText());
	}

	private void getMovieInfo(Movie movie) {
		if (movie != null) {
			model.getMovieInfo(movie);
			// didn't want to implement boolean firstload
			if (wvHtmlPreview.getEngine().getHistory().getEntries().size() != 0) {
				wvHtmlPreview.getEngine().reload();
			} else {
				wvHtmlPreview.getEngine().load(htmlFile.toURI().toString());
			}
		}
	}

	public void reGenerate(ActionEvent event) {
		Movie selectedMovie = lvMovies.getSelectionModel().getSelectedItem();
		if (selectedMovie != null) {
			getMovieInfo(selectedMovie);
		}
	}

	public void generateAndCopy(ActionEvent event) {
		Movie selectedMovie = lvMovies.getSelectionModel().getSelectedItem();
		if (selectedMovie != null) {
			//File aboutFile = new File (selectedMovie.getFile().getParentFile(), "about.jpg");
			generateAndCopy(selectedMovie, htmlFile, aboutImageFile, 1280, 720);
		}
	}

	private void generateAndCopy(Movie selectedMovie, File htmlFile, File outputFile, double width, double height) {
		// rootPane is the root of original scene in an FXML controller you get this when you assign it an id
		rootPane.setEffect(new GaussianBlur());
		Stage primaryStage = (Stage)rootPane.getScene().getWindow();
		// creating separate webview holding same html content as in original scene
		WebView webView = new WebView();
		// with the size I want the snapshot
		webView.setPrefSize(width, height);
		AnchorPane snapshotRoot = new AnchorPane(webView);
		webView.getEngine().load(htmlFile.toURI().toString());
		Stage popupStage = new Stage(StageStyle.TRANSPARENT);
		popupStage.initOwner(primaryStage);
		popupStage.initModality(Modality.APPLICATION_MODAL);
		// this popup doesn't really show anything size = 1x1, it just holds the snapshot-webview
		popupStage.setScene(new Scene(snapshotRoot, 1, 1));
		// pausing to make sure the webview/picture is completely rendered
		PauseTransition pt = new PauseTransition(Duration.seconds(2));
		pt.setOnFinished(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				WritableImage image = webView.snapshot(null, null);
				// writing a png to outputFile
				// writing a JPG like this will result in a pink JPG, see other posts
				// if somebody can scrape me simple code to convert it ARGB to RGB or something
				String format = "png";
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, outputFile);
					LOG.info("Generated aboutImageFile with length: " + outputFile.length());
					model.generateAndCopy(selectedMovie);
				} catch (IOException e) {
					LOG.debug("Unable to generate aboutImageFile", e);
				} finally {
					rootPane.setEffect(null);
					popupStage.hide();
				}
			}
		});
		// pausing, after pause onFinished event will take + write snapshot
		pt.play();
		// GO!
		popupStage.show();
	}

	public void changeQuery(ActionEvent event) {
		Movie selectedMovie = lvMovies.getSelectionModel().getSelectedItem();
		if (selectedMovie != null) {
			changeQuery(selectedMovie);
		}
	}

	private void changeQuery(Movie selectedMovie) {
		// rootPane is the root of original scene in an FXML controller you get this when you assign it an id
		Stage primaryStage = (Stage)rootPane.getScene().getWindow();
		// creating separate TitledPane to hold the changeQuery controls
		FXMLLoader changeQueryLoader = new FXMLLoader(Main.class.getResource("/resources/gui/ChangeQuery.fxml"));
		TitledPane changeQueryPane;
		try {
			changeQueryPane = (TitledPane)changeQueryLoader.load();
			Stage changeQueryStage = new Stage(StageStyle.TRANSPARENT);
			changeQueryStage.initOwner(primaryStage);
			changeQueryStage.initModality(Modality.APPLICATION_MODAL);
			Scene changeQueryScene = new Scene(changeQueryPane);
			changeQueryScene.getStylesheets().add(getClass().getResource("/resources/gui/application.css").toExternalForm());
			changeQueryStage.setScene(changeQueryScene);
			ChangeQueryController changeQueryController = changeQueryLoader.getController();
			changeQueryController.initData(rootPane, this, changeQueryStage, selectedMovie);
		} catch (IOException e) {
			LOG.debug("Unable to start ChangeQueryPane", e);
		}
	}

	public void renameMovie(String newName) {
		int selectedIndex = lvMovies.getSelectionModel().getSelectedIndex();
		Movie selectedMovie = lvMovies.getSelectionModel().getSelectedItem();
		selectedMovie.setName(newName);
		try {
			selectedMovie.renameFile(newName);
		} catch (Exception e) {
			setStatusLine("Unable to rename movie file", e);
		}
		model.getMovies().set(selectedIndex, selectedMovie);
	}

	public void changePlot(ActionEvent event) {
		Movie selectedMovie = lvMovies.getSelectionModel().getSelectedItem();
		if (selectedMovie != null) {
			changePlot(selectedMovie);
		}
	}

	private void changePlot(Movie selectedMovie) {
		// rootPane is the root of original scene in an FXML controller you get this when you assign it an id
		Stage primaryStage = (Stage)rootPane.getScene().getWindow();
		// creating separate TitledPane to hold the changePlot controls
		FXMLLoader changePlotLoader = new FXMLLoader(Main.class.getResource("/resources/gui/ChangePlot.fxml"));
		TitledPane changePlotPane;
		try {
			changePlotPane = (TitledPane)changePlotLoader.load();
			Stage changePlotStage = new Stage(StageStyle.TRANSPARENT);
			changePlotStage.initOwner(primaryStage);
			changePlotStage.initModality(Modality.APPLICATION_MODAL);
			Scene changePlotScene = new Scene(changePlotPane);
			changePlotScene.getStylesheets().add(getClass().getResource("/resources/gui/application.css").toExternalForm());
			changePlotStage.setScene(changePlotScene);
			ChangePlotController changePlotController = changePlotLoader.getController();
			changePlotController.initData(rootPane, this, changePlotStage, selectedMovie);
		} catch (IOException e) {
			LOG.debug("Unable to start ChangePlotPane", e);
		}
	}

	public void valuesChanged(Movie movie) {
		model.writeSetValues(movie, false);
		wvHtmlPreview.getEngine().reload();
	}

}
