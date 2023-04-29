package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static class IpResponse {
        String ip;
        String city;
        String longitude;
        String latitude;
    } // IpResult

    Stage stage;
    Scene scene;
    VBox root;
    Label choice1;
    Label choice2;
    Label choice3;
    Label choice4;
    Label choice5;
    Button find;
    HBox botHbox;
    Font fsize;
    String jsonString = "";

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        botHbox = new HBox();

        choice1 = new Label("Choice 1" + "            " + "distance: ");
        choice2 = new Label("Choice 2" + "            " + "distance: ");
        choice3 = new Label("Choice 3" + "            " + "distance: ");
        choice4 = new Label("Choice 4" + "            " + "distance: ");
        choice5 = new Label("Choice 5" + "            " + "distance: ");
        choice1.setPrefHeight(50.0);
        choice2.setPrefHeight(50.0);
        choice3.setPrefHeight(50.0);
        choice4.setPrefHeight(50.0);
        choice5.setPrefHeight(50.0);

        find = new Button("FIND");
        find.setPrefWidth(400);
        find.setPrefHeight(50);
        fsize = new Font(35);
        find.setFont(fsize);


    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        // demonstrate how to load local asset using "file:resources/"
        Image bannerImage = new Image("file:resources/FasterFoodBanner.png");
        ImageView banner = new ImageView(bannerImage);
        banner.setPreserveRatio(false);
        banner.setFitWidth(400);
        banner.setFitHeight(100);

        // some labels to display information
        Label notice = new Label("Currently building! ;0");

        // setup scene
        botHbox.getChildren().addAll(find);
        HBox.setHgrow(find, Priority.ALWAYS);
        root.getChildren().addAll( banner, choice1, choice2, choice3, choice4, choice5, botHbox);


        Runnable task1 = () -> {
            getIp();
        };

        find.setOnAction(event -> {
            getIp();
//            Thread trd = new Thread(task1);
//            trd.setDaemon(true);
//            trd.start();
        });

        scene = new Scene(root);

        // setup stage
        stage.setTitle("FasterFood");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

    private void getIp()  {
        try {
            URL ipapi = new URL("https://ipapi.co/json/");

            URLConnection c = ipapi.openConnection();
            c.setRequestProperty("User-Agent", "java-ipapi-v1.02");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(c.getInputStream())
                );
            String line;
            while ((line = reader.readLine()) != null)        {
                System.out.println("this is loop " + line);
                jsonString += line;
            }
            reader.close();

            IpResponse ipResponse = GSON.fromJson(jsonString, ApiApp.IpResponse.class);
            System.out.println("********** PRETTY JSON STRING: **********");
            System.out.println(GSON.toJson(ipResponse));
            System.out.println("********** PARSED RESULTS: **********");

        } catch (Exception e) {
            System.out.println("exception occured");
        }
    }
}

 // ApiApp
