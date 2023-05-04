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
import com.google.gson.annotations.SerializedName;
import javafx.scene.control.TextField;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
        @SerializedName("full_address") String fullAddress;
        String name;
        String latitude;
        String longitude;

    }

    private class LocalResponse {
        LocalResult[] data;
    }

    private class DistanceResult {
        String coordinate;
        String distance;
    }

    private class DistanceResponse {
        @SerializedName("end_point_1") DistanceResult endPoint1;
        @SerializedName("end_point_2") DistanceResult endPoint2;
        @SerializedName("end_point_3") DistanceResult endPoint3;
        @SerializedName("end_point_4") DistanceResult endPoint4;
        @SerializedName("end_point_5") DistanceResult endPoint5;

//        DistanceResult end_point_1;
//        DistanceResult end_point_2;
//        DistanceResult end_point_3;
//        DistanceResult end_point_4;
//        DistanceResult end_point_5;

    }

    Stage stage;
    Scene scene;
    VBox root;
    Label choice1;
    Label c1details;
    Label c1address;
    Label choice2;
    Label c2details;
    Label c2address;
    Label choice3;
    Label c3details;
    Label c3address;
    Label choice4;
    Label c4details;
    Label c4address;
    Label choice5;
    Label c5details;
    Label c5address;
    Button find;
    HBox botHbox;
    HBox c1;
    HBox c2;
    HBox c3;
    HBox c4;
    HBox c5;
    Font fsize;
    String jsonString = "";
    Label locationBanner;
    String localJson = "";
    String distanceJson = "";
    IpResponse ipResponse;
    LocalResponse localResponse;
    Set<String> set;
    TextField cuisine;
    Label loading;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        botHbox = new HBox();
        c1 = new HBox();
        c2 = new HBox();
        c3 = new HBox();
        c4 = new HBox();
        c5 = new HBox();
        choice1 = new Label(" 1). Think about the type of food you would like." );
        c1details = new Label();
        c1address = new Label();
        choice2 = new Label(" 2). Enter the type you would like in the search bar.");
        c2details = new Label();
        c2address = new Label();
        choice3 = new Label(" 3). Click FIND!");
        c3details = new Label();
        c3address = new Label();
        choice4 = new Label("");
        c4details = new Label();
        c4address = new Label();
        choice5 = new Label("");
        c5details = new Label();
        c5address = new Label();
        choice1.setFont(new Font("Verdana", 14));
        choice2.setFont(new Font("Verdana", 14));
        choice3.setFont(new Font("Verdana", 14));
        choice4.setFont(new Font("Verdana", 14));
        choice5.setFont(new Font("Verdana", 14));
        loading = new Label("");
        loading.setTextAlignment(TextAlignment.RIGHT);
        choice1.setPrefHeight(50.0);
        c1details.setPrefHeight(25.0);
        c1details.setPrefWidth(100.0);
        c1address.setPrefHeight(25.0);

        choice2.setPrefHeight(50.0);
        c2details.setPrefHeight(25.0);
        c2details.setPrefWidth(100.0);
        c2address.setPrefHeight(25.0);

        choice3.setPrefHeight(50.0);
        c3details.setPrefHeight(25.0);
        c3details.setPrefWidth(100.0);
        c3address.setPrefHeight(25.0);

        choice4.setPrefHeight(50.0);
        c4details.setPrefHeight(25.0);
        c4details.setPrefWidth(100.0);
        c4address.setPrefHeight(25.0);

        choice5.setPrefHeight(50.0);
        c5details.setPrefHeight(25.0);
        c5details.setPrefWidth(100.0);
        c5address.setPrefHeight(25.0);

        locationBanner = new Label(" ");

        find = new Button("FIND");
        find.setPrefWidth(500);
        find.setPrefHeight(50);
        fsize = new Font(35);
        find.setFont(fsize);

        cuisine = new TextField("fast food");

    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        // demonstrate how to load local asset using "file:resources/"
        Image bannerImage = new Image("file:resources/FasterFoodBanner.png");
        ImageView banner = new ImageView(bannerImage);
        banner.setPreserveRatio(false);
        banner.setFitWidth(500);
        banner.setFitHeight(100);

        cuisine.setPrefWidth(500);

        // some labels to display information
        Label notice = new Label("Currently building! ;0");

        // setup scene
        botHbox.getChildren().addAll(locationBanner, loading);
        c1.getChildren().addAll(c1details, c1address);
        c2.getChildren().addAll(c2details, c2address);
        c3.getChildren().addAll(c3details, c3address);
        c4.getChildren().addAll(c4details, c4address);
        c5.getChildren().addAll(c5details, c5address);

        root.getChildren().addAll( banner, choice1, c1, choice2, c2,
            choice3, c3, choice4, c4, choice5,
            c5, cuisine, find, botHbox);


        Runnable task1 = () -> {
            find.setDisable(true);
            Platform.runLater(() -> locationBanner.setText(""));
            Platform.runLater(() -> loading.setText("Loading..."));
            choice1.setFont(new Font("Verdana", 18));
            choice2.setFont(new Font("Verdana", 18));
            choice3.setFont(new Font("Verdana", 18));
            choice4.setFont(new Font("Verdana", 18));
            choice5.setFont(new Font("Verdana", 18));

            c1address.setFont(new Font("Arial", 10));
            c2address.setFont(new Font("Arial", 10));
            c3address.setFont(new Font("Arial", 10));
            c4address.setFont(new Font("Arial", 10));
            c5address.setFont(new Font("Arial", 10));
            getIp();
            getLocal();
            if (localResponse.data.length >= 5) {
                Platform.runLater(() -> getDistance());
                Platform.runLater(() -> loading.setText(""));
                Platform.runLater(() -> locationBanner.setText("Device Location: "
                + ipResponse.city));
            }
            find.setDisable(false);
        };

        find.setOnAction(event -> {
            Thread trd = new Thread(task1);
            trd.setDaemon(true);
            trd.start();
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
                .uri(URI.create("https://api.ipgeolocation.io/ipgeo?apiKey=0b3393a5422748a4ba1ce594f7b7dff1"))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> responseIp = HttpClient.newHttpClient()
                .send(requestIp, HttpResponse.BodyHandlers.ofString());
            jsonString = responseIp.body();

            ipResponse = GSON.fromJson(jsonString, ApiApp.IpResponse.class);

            System.out.println("********** PRETTY JSON STRING: **********");
            System.out.println(GSON.toJson(ipResponse));


        } catch (Exception e) {
            System.out.println("exception occured in get IP");
        }
    }

    private void getLocal() {
        try {

            String query = URLEncoder.encode(cuisine.getText(), StandardCharsets.UTF_8);

            String queryUri = String.format("?query=%s", query);

            String url = "https://local-business-data.p.rapidapi.com/search-nearby" + queryUri + "&lat="
                + ipResponse.latitude + "&lng="
                + ipResponse.longitude + "&limit=5&language=en&region=us";

            System.out.println("queryURI " + queryUri);
            System.out.println("url " + url);

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


            if (localResponse.data.length < 5) {

                Platform.runLater(() -> alertError());
//                System.out.println("YOOOOO");
                Platform.runLater(() -> loading.setText(""));
            } else {

                System.out.println("********** PRETTY JSON STRING: **********");
                System.out.println(GSON.toJson(localResponse));

                System.out.println("********** Name Test: **********");
                System.out.println(localResponse.data[2].name);
                System.out.println(localResponse.data[2].fullAddress);
            }

        } catch (Exception e) {
            System.out.println("java io exception in get local");
        }
    }

    private void getDistance() {
        try {

            HttpRequest requestDistance = HttpRequest.newBuilder()
                .uri(URI.create("https://distance-calculator.p.rapidapi.com/v1/one_to_many?start_point="
                + "(" + ipResponse.latitude + "%2C" + ipResponse.longitude + ")"
                + "&end_point_1="
                + "("
                + localResponse.data[0].latitude + "%2C" + localResponse.data[0].longitude + ")"
                + "&end_point_2="
                + "("
                + localResponse.data[1].latitude + "%2C" + localResponse.data[1].longitude + ")"
                + "&end_point_3="
                + "("
                + localResponse.data[2].latitude + "%2C" + localResponse.data[2].longitude + ")"
                + "&end_point_4="
                + "("
                + localResponse.data[3].latitude + "%2C" + localResponse.data[3].longitude + ")"
                + "&end_point_5="
                + "("
                + localResponse.data[4].latitude + "%2C" + localResponse.data[4].longitude + ")"
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

            choice1.setText(localResponse.data[0].name);
            c1details.setText(distanceResponse.endPoint1.distance + " miles");
            c1address.setText(localResponse.data[0].fullAddress);
            choice2.setText(localResponse.data[1].name);
            c2details.setText(distanceResponse.endPoint2.distance + " miles");
            c2address.setText(localResponse.data[1].fullAddress);
            choice3.setText(localResponse.data[2].name);
            c3details.setText(distanceResponse.endPoint3.distance + " miles");
            c3address.setText(localResponse.data[2].fullAddress);
            choice4.setText(localResponse.data[3].name);
            c4details.setText(distanceResponse.endPoint4.distance + " miles");
            c4address.setText(localResponse.data[3].fullAddress);
            choice5.setText(localResponse.data[4].name);
            c5details.setText(distanceResponse.endPoint5.distance + " miles");
            c5address.setText(localResponse.data[4].fullAddress);
/**
            choice1.setText("STOPPING WASTE");
            choice2.setText("STOPPING WASTE");
            choice3.setText("STOPPING WASTE");
*/
        } catch (Exception e) {
            System.out.println("java io exception in get distance");
        }
    }

    public static void alertError() {
        TextArea text = new TextArea("Error: There are less than 5 available results."
            + "\n" + "Please enter another query in the search bar.");
        text.setEditable(false);
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().setContent(text);
        alert.setResizable(false);
        alert.showAndWait();
    } // alertError

}

 // ApiApp
