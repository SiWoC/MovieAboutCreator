package nl.siwoc.application.movieaboutcreator;
	
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	static {
		try {
			new File("log").mkdir();
			Handler handler = new FileHandler("log/MovieAboutCreator.log", 50000, 1);
			handler.setFormatter(new SimpleFormatter());
			Logger.getLogger("").addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = FXMLLoader.load(getClass().getResource("gui/Main.fxml"));
			Scene scene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
			scene.getStylesheets().add(getClass().getResource("gui/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	@Override
	public void init() throws Exception {
		LOGGER.log(Level.INFO, "Initializing");
		createPropertiesFile();
		mkDir("generated");
		mkDir("css");
		mkDir("pictures");
	}

	private void createPropertiesFile() {
		File propertiesFile = new File ("movieaboutcreator.properties");
		if (!propertiesFile.exists()) {
			LOGGER.log(Level.INFO, "Creating properties file");
			try (InputStream is = getClass().getClassLoader().getResourceAsStream("resources/movieaboutcreator.org.properties")) {
				FileUtils.copyInputStreamToFile(is, propertiesFile);
			} catch (IOException e) {
				// too bad
				LOGGER.log(Level.SEVERE, "Error creating properties file", e);
			}
		}
		
	}

	private void mkDir(String folderName) throws IOException {
		File directoryToCreate = new File("./" + folderName);
		LOGGER.log(Level.INFO, "Creating " + folderName + " exists=" + directoryToCreate.exists());
		if (!directoryToCreate.exists()) {
			List<String> files = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream("resources/html/" + folderName + ".txt"), Charset.forName("UTF-8"));
			for (String string : files) {
				
				File outputFile = new File(folderName + "/" + string);
				LOGGER.log(Level.INFO, "Creating " + outputFile.getCanonicalPath());
				try (InputStream is = getClass().getClassLoader().getResourceAsStream("resources/html/" + folderName + "/" + string)) {
					LOGGER.log(Level.INFO, "Reading " + "resources/html/" + folderName + "/" + string);
					FileUtils.copyInputStreamToFile(is, outputFile);
					is.close();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Error creating template files", e);
				}
			}
			/*
			URL resourceFolderURL = getClass().getClassLoader().getResource("resources/html/" + folderName);
			FileUtils.copyDirectory(new File(resourceFolderURL.getFile()), directoryToCreate);
			*/
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
