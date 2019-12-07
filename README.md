# MovieAboutCreator
This java application creates the "about.jpg" for the Mede8er mediaplayer with info about the movie

## Main screen
![Main screen](docs/screenshots/Main.jpg?raw=true "Main screen")

It relies on your movies being in a separate folder by itself. And that the name of the movie-file is "[Title] ([year])".  
It reads movies with the file-extensions configured in the movieaboutcreator.properties-file.  
File properties (container, codecs etc) are retrieved with "my" Java MediaInfo fileprober, so those are only shown for filetypes that can handle.

![Folder structure](docs/screenshots/FolderStructure.jpg?raw=true "Folder structure")

You can put the program anywhere you like.
When you start the program it will "unpack", generate some folders with the templates, pictures etc.,
then it will analyze the folder it's pointing to. (The one in the properties file, the one it pointed to when it was last closed) This might take some time if the folder holds a lot of movies.
After that you will be shown the Main screen.

![Main screen sections](docs/screenshots/Main2.jpg?raw=true "Main screen sections")

The Main screen holds several sections and options:
1. In this section you can choose the folder which holds your movie(folders). If the program was already open and you made changes to the contents, you can refresh the list.
2. In this section you can choose which collector to use to get the background, folder and details. Currently included are [TheMovieDB](https://www.themoviedb.org/) for background, folder and detail, [MovieMeter.nl](https://www.moviemeter.nl/) for folder and Dutch details.
3. After you change a collector, press Re-generate to re-generate the picture. Warning! this will overwrite any changes to the plot you made.
4. If with the current title/year/query the collectors can't retrieve the data of the movie, you can change the way the search is performed.
5. If the details-collector retrieved a plot summary which doesn't fit in the templates reserved space you can modify it to your liking.
6. If you are satisfied with the way the preview looks, you can generate the actual files (about.jpg, folder.jpg, "[Title] ([year]).xml" and copy them to the Movie folder.

### Change Query
![Change Query sections](docs/screenshots/ChangeQueryBefore.jpg?raw=true "Change Query sections")

The Change Query screen holds the following sections:  
1. Here you can adjust the title and year being used.  
2. After you changed the values you can Test them. If the collectors can find an Id, they can find a unique movie. If you want you can verify the Id's on the web.  
3. When you are satisfied with the Query, you can either choose to Use them and Rename the Movie File accordingly. It will be renamed to "[Title] ([year])". Any Files with the same base-name as the original (like srt, xml, info) will be renamed to the new value. You can also to just Use the values leaving the File untouched.

![Change Query after](docs/screenshots/ChangeQueryAfter.jpg?raw=true "Change Query after")

### Change Plot
Sometimes the return plot summary doesn't fit in the reserved space.

![Change Plot necessary](docs/screenshots/ChangePlotNecessary.jpg?raw=true "Change Plot necessary")

![Change Plot before](docs/screenshots/ChangePlotBefore.jpg?raw=true "Change Plot before")

In the Change Plot screen you can change the plot summary to your liking. The orange line gives an indication of how much room there is in the default template.

### Advanced
![Changed the template](docs/screenshots/Advanced.jpg?raw=true "Changed the template")

If you want, you can change the template (in the generated folder), pictures and/or css to your liking.  
An additional property is available in movieaboutcreator.properties, namely max.plot.length for if you want a bigger plot-box. 

