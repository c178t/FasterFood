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
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.*;

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
    } // IpResponse

    private class LocalResult {
        String name;
        String latitude;
        String longitude;
        String full_address;
    }

    private class LocalResponse {
        LocalResult[] data;
    }

    private class DistanceResult {
        String coordinate;
        String distance;
    }

    private class DistanceResponse {
        DistanceResult end_point_1;
        DistanceResult end_point_2;
        DistanceResult end_point_3;
        DistanceResult end_point_4;
        DistanceResult end_point_5;

    }

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
    Label locationBanner;
    String localJson = "";
    String distanceJson = "";
    IpResponse ipResponse;
    LocalResponse localResponse;
    Set<String> set;


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
        locationBanner = new Label(" ");

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
        root.getChildren().addAll( banner, choice1, choice2, choice3, choice4, choice5, botHbox,
            locationBanner);


        Runnable task1 = () -> {
            getIp();

        };

        find.setOnAction(event -> {
            getIp();
            getLocal();
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

            HttpRequest requestIp = HttpRequest.newBuilder()
                .uri(URI.create("https://find-any-ip-address-or-domain-location-world-wide.p.rapidapi.com/iplocation?apikey=873dbe322aea47f89dcf729dcc8f60e8"))
                .header("X-RapidAPI-Key", "62ba22f05emshe5b84bc85a06c93p1c699ejsn41e1adb9ef37")
                .header("X-RapidAPI-Host",
                "find-any-ip-address-or-domain-location-world-wide.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> responseIp = HttpClient.newHttpClient()
                .send(requestIp, HttpResponse.BodyHandlers.ofString());
            jsonString = responseIp.body();

            ipResponse = GSON.fromJson(jsonString, ApiApp.IpResponse.class);

            System.out.println("********** PRETTY JSON STRING: **********");
            System.out.println(GSON.toJson(ipResponse));
            locationBanner.setText("Device Location: " + ipResponse.city);


        } catch (Exception e) {
            System.out.println("exception occured");
        }
    }

    private void getLocal() {
        try {
            String url = "https://local-business-data.p.rapidapi.com/search-nearby?query=fast%20food&lat="
                + ipResponse.latitude + "&lng="
                + ipResponse.longitude + "&limit=5&language=en&region=us";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", "62ba22f05emshe5b84bc85a06c93p1c699ejsn41e1adb9ef37")
                .header("X-RapidAPI-Host", "local-business-data.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
            localJson = response.body();
            System.out.println(localJson.trim());

            localResponse = GSON.fromJson(localJson, ApiApp.LocalResponse.class);

            System.out.println("********** PRETTY JSON STRING: **********");
            System.out.println(GSON.toJson(localResponse));


            System.out.println("********** Name Test: **********");
            System.out.println(localResponse.data[2].name);

            choice1.setText(localResponse.data[0].name + "                  " + "distance: ");
            choice2.setText(localResponse.data[1].name + "                  " + "distance: ");
            choice3.setText(localResponse.data[2].name + "                  " + "distance: ");
            choice4.setText(localResponse.data[3].name + "                  " + "distance: ");
            choice5.setText(localResponse.data[4].name + "                  " + "distance: ");


        } catch (Exception e) {
            System.out.println("java io exception");
        }
    }

    private void getDistance() {
        try {
            HttpRequest requestDistance = HttpRequest.newBuilder()
                .uri(URI.create("https://distance-calculator.p.rapidapi.com/v1/one_to_many?start_point="
                + "(" + ipResponse.latitude + "%2C" + ipResponse.longitude + ")"
                + "&end_point_1="
                + "(" + localResponse.data[0].latitude + "%2C" + localResponse.data[0].longitude + ")"
                + "&end_point_2="
                + "(" + localResponse.data[1].latitude + "%2C" + localResponse.data[1].longitude + ")"
                + "&end_point_3="
                + "(" + localResponse.data[2].latitude + "%2C" + localResponse.data[2].longitude + ")"
                + "&end_point_4="
                + "(" + localResponse.data[3].latitude + "%2C" + localResponse.data[3].longitude + ")"
                + "&end_point_5="
                + "(" + localResponse.data[4].latitude + "%2C" + localResponse.data[4].longitude + ")"
                + "&unit=miles&decimal_places=1"))
                .header("Content-Type", "application/json")
                .header("X-RapidAPI-Key", "62ba22f05emshe5b84bc85a06c93p1c699ejsn41e1adb9ef37")
                .header("X-RapidAPI-Host", "distance-calculator.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> responseDistance = HttpClient.newHttpClient()
                .send(requestDistance, HttpResponse.BodyHandlers.ofString());

            distanceJson = responseDistance.body();
            System.out.println(distanceJson);

            DistanceResponse distanceResponse = GSON
                .fromJson(distanceJson, ApiApp.DistanceResponse.class);

            System.out.println("********** PRETTY JSON STRING: **********");
            System.out.println(GSON.toJson(distanceResponse));
        } catch (Exception e) {
            System.out.println("java io exception");
        }
    }
}

 // ApiApp
