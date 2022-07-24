package utils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws URISyntaxException {

        Main main = new Main();

        String mapFileName = "inputFiles/map.txt";
        String AventurierFileName = "inputFiles/aventurier.txt";

        File mapFile = main.getFileFromResource(mapFileName);
        File aventurierFile = main.getFileFromResource(AventurierFileName);

        GameController gameController = new GameController(mapFile,aventurierFile);

        System.out.println("======== Game Start =========\n");
        gameController.play();
        System.out.println("\n======== Game End =========");

    }


    /*
       The resource URL is not working in the JAR
       If we try to access a file that is inside a JAR,
       It throws NoSuchFileException (linux), InvalidPathException (Windows)

       Resource URL Sample: file:java-io.jar!/json/file1.json
    */
    private File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }
}
