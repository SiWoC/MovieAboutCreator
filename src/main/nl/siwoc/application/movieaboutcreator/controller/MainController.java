/*******************************************************************************
 * Copyright (c) 2019 Niek Knijnenburg
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import nl.siwoc.application.movieaboutcreator.Properties;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoBackgroundCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoDetailsCollector;
import nl.siwoc.application.movieaboutcreator.collector.MovieInfoFolderCollector;
import nl.siwoc.application.movieaboutcreator.model.Movie;
import nl.siwoc.application.movieaboutcreator.service.MovieService;

public class MainController {

	private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
	
	private File htmlFile = new File ("generated/template.html");
	private File folderImageFile = new File("generated/folder.jpg");
	private File backgroundImageFile = new File("generated/background.jpg");
	private File aboutImageFile = new File("generated/about.png");
	private File setValuesFile = new File("generated/setvalues.js");

	@FXML
	AnchorPane rootPane;

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
			LOGGER.info(statusLineText);
			txtStatusArea.setText(statusLineText + "\r\n" + txtStatusArea.getText().substring(0, Math.min(txtStatusArea.getLength(), 2000)));
		}
		if (e != null) {
			LOGGER.log(Level.SEVERE, statusLineText, e);
		}
	}

	public void initialize() {
		setStatusLine("Initializing");
		model = new MovieService();
		model.setController(this);
		txtMoviesFolderName.setText(Properties.getProperty("movies.foldername"));
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
		folderChooser.setInitialDirectory(new File(txtMoviesFolderName.getText()));
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

	public void refreshMovies(ActionEvent event) {
		model.setMoviesFolderName(txtMoviesFolderName.getText());
		model.refreshMovies();
	}

	public void getMovieInfo(Movie movie) {
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
	
	private class ReloadListener implements ChangeListener<Worker.State> {
		  @Override
		  public void changed(
		    ObservableValue<? extends Worker.State> observable,
		    Worker.State oldValue, Worker.State newValue ) {

		    if( newValue != Worker.State.SUCCEEDED ) {
		      return;
		    }

		    // Your logic here
		  }
	}
		
	ReloadListener reloadListener = new ReloadListener();

	
	public void generateAndCopy(ActionEvent event) {
		Movie selectedMovie = lvMovies.getSelectionModel().getSelectedItem();
		if (selectedMovie != null) {
			//File aboutFile = new File (selectedMovie.getFile().getParentFile(), "about.jpg");
			generateAndCopy(selectedMovie, htmlFile, aboutImageFile, 1280, 720);
		}
	}
		
	public void generateAndCopy(Movie selectedMovie, File htmlFile, File outputFile, double width, double height) {
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
					LOGGER.info("Generated aboutImageFile with length: " + outputFile.length());
					model.generateAndCopy(selectedMovie);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

}
