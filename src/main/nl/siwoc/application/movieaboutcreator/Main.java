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
package nl.siwoc.application.movieaboutcreator;
	
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import nl.siwoc.application.movieaboutcreator.utils.Logger;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = FXMLLoader.load(getClass().getResource("gui/Main.fxml"));
			Scene scene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
			scene.getStylesheets().add(getClass().getResource("gui/application.css").toExternalForm());
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("gui/Movie128.png")));
			primaryStage.setTitle("MovieAboutCreator");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	@Override
	public void init() throws Exception {
		Logger.logInfo("Initializing");
		createPropertiesFile();
		//Logger.setLogLevel(Properties.getProperty("logging.level"));
		mkDir("generated");
		mkDir("css");
		mkDir("pictures");
	}

	private void createPropertiesFile() {
		File propertiesFile = new File ("movieaboutcreator.properties");
		if (!propertiesFile.exists()) {
			Logger.logInfo("Creating properties file");
			try (InputStream is = getClass().getClassLoader().getResourceAsStream("resources/movieaboutcreator.org.properties")) {
				FileUtils.copyInputStreamToFile(is, propertiesFile);
			} catch (IOException e) {
				// too bad
				Logger.logError("Error creating properties file: ", e);
			}
		}
		
	}

	private void mkDir(String folderName) throws IOException {
		File directoryToCreate = new File("./" + folderName);
		Logger.logInfo("Creating " + folderName + " exists=" + directoryToCreate.exists());
		if (!directoryToCreate.exists()) {
			List<String> files = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream("resources/html/" + folderName + ".txt"), Charset.forName("UTF-8"));
			for (String string : files) {
				
				File outputFile = new File(folderName + "/" + string);
				Logger.logInfo("Creating " + outputFile.getCanonicalPath());
				try (InputStream is = getClass().getClassLoader().getResourceAsStream("resources/html/" + folderName + "/" + string)) {
					Logger.logInfo("Reading " + "resources/html/" + folderName + "/" + string);
					FileUtils.copyInputStreamToFile(is, outputFile);
					is.close();
				} catch (Exception e) {
					Logger.logError("Error creating template files: ", e);
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
