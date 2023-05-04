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
import javafx.scene.text.FontPosture;

/**
 * Finds best match for food based on what user inputs in search bar.
 * Shows the top 5 locations with their addresses and approximate distances for reference.
 */
public class ApiApp extends Application {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * This class holds the response from ip api.
     */
    private static class IpResponse {
        String ip;
        String city;
        String longitude;
        String latitude;
    } // IpResponse

    /**
     * This class holds the addresses.
     */
    private class LocalLocation {
        @SerializedName("display_address") String[] displayAddress;
    }

    /**
     * This class holds the data from businesses.
     */
    private class LocalResult {

        String name;
        double distance;
        LocalLocation location;
    }

    /**
     * This class holds an array of business data.
     */
    private class LocalResponse {
        LocalResult[] businesses;
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
    Label disclaimer;
    Label search;
    HBox bar;
    ImageView banner;

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
        bar = new HBox();
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
        search = new Label("Search: ");
        loading.setTextAlignment(TextAlignment.RIGHT);
        choice1.setPrefHeight(50.0);
        c1details.setPrefHeight(25.0);
        c1details.setPrefWidth(200.0);
        c1address.setPrefHeight(25.0);
        choice2.setPrefHeight(50.0);
        c2details.setPrefHeight(25.0);
        c2details.setPrefWidth(200.0);
        c2address.setPrefHeight(25.0);
        choice3.setPrefHeight(50.0);
        c3details.setPrefHeight(25.0);
        c3details.setPrefWidth(200.0);
        c3address.setPrefHeight(25.0);
        choice4.setPrefHeight(50.0);
        c4details.setPrefHeight(25.0);
        c4details.setPrefWidth(200.0);
        c4address.setPrefHeight(25.0);
        choice5.setPrefHeight(50.0);
        c5details.setPrefHeight(25.0);
        c5details.setPrefWidth(200.0);
        c5address.setPrefHeight(25.0);
        locationBanner = new Label("Status: Ready");
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

        disclaimer = new Label
        ("DISCLAIMER: If you recieve results for a non-food search, it is a feature :)");
        disclaimer.setFont(Font.font("Lucida Sans Unicode", FontPosture.ITALIC, 12));

        Image bannerImage = new Image("file:resources/FasterFoodBanner.png");
        banner = new ImageView(bannerImage);
        banner.setPreserveRatio(false);
        banner.setFitWidth(500);
        banner.setFitHeight(100);
        cuisine.setPrefWidth(445);

        setScene();
        Runnable task1 = () -> {
            find.setDisable(true);
            Platform.runLater(() -> locationBanner.setText(""));
            Platform.runLater(() -> loading.setText("Loading..."));
            choice1.setText("");
            choice2.setText("");
            choice3.setText("");
            choice4.setText("");
            choice5.setText("");
            choice1.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
            choice2.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
            choice3.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
            choice4.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
            choice5.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

            c1address.setFont(new Font("Arial", 10));
            c2address.setFont(new Font("Arial", 10));
            c3address.setFont(new Font("Arial", 10));
            c4address.setFont(new Font("Arial", 10));
            c5address.setFont(new Font("Arial", 10));
            getIp();
            getLocal();
            if (localResponse.businesses.length >= 5) {
                Platform.runLater(() -> setGUI());
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

    /**
     * This method calls the ipgeolocation API to find location of calling device.
     */
    private void getIp()  {
        try {
            HttpRequest requestIp = HttpRequest.newBuilder()
                .uri(URI.create
                ("https://api.ipgeolocation.io/ipgeo?apiKey=0b3393a5422748a4ba1ce594f7b7dff1"))
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

    /**
     * This method calls the Yelp API to gather local business data.
     */
    private void getLocal() {
        try {

            String term = URLEncoder.encode(cuisine.getText(), StandardCharsets.UTF_8);

            String termUri = String.format("term=%s", term);

            String url = "https://api.yelp.com/v3/businesses/search?latitude="
                + ipResponse.latitude + "&longitude="
                + ipResponse.longitude + "&" + termUri + "&categories=&sort_by=best_match&limit=5";

            System.out.println("termURI " + termUri);
            System.out.println("url " + url);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .header("Authorization",
                "Bearer AMQwZRqCeVzqgaIWTmZ4ojvgv_L7sy-87QV2vAQPA4aqPOqrb-pfY5g"
                + "DsnVTKo5705khygJqPY22iUFpOZ3L_sg48hAW3mGrka0LQktaRo5kSaQL0Spn2yUU8h9TZHYx")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());


            localJson = response.body();
            System.out.println(localJson.trim());

            localResponse = GSON.fromJson(localJson, ApiApp.LocalResponse.class);


            if (localResponse.businesses.length < 5) {

                Platform.runLater(() -> alertError());

                Platform.runLater(() -> loading.setText(""));
                Platform.runLater(() -> locationBanner.setText("Status: Ready"));

            } else {

                System.out.println("********** PRETTY JSON STRING: **********");
                System.out.println(GSON.toJson(localResponse));

                System.out.println("********** Name Test: **********");
                System.out.println(localResponse.businesses[2].name);
                System.out.println(localResponse.businesses[2].location);
            }

        } catch (Exception e) {
            System.out.println("java io exception in get local");
        }
    }

    /**
     * This method sets all the text in the GUI.
     */
    private void setGUI() {
        try {

            String[] address = new String[5];

            for (int x = 0; x < 5; x++) {
                address[x] = "";
                for (int y = 0; y < localResponse.businesses[x]
                        .location.displayAddress.length; y++) {
                    address[x] += localResponse.businesses[x].location.displayAddress[y] + " ";
                }
            }

            choice1.setText(localResponse.businesses[0].name);
            c1details.setText("~" + (int)localResponse.businesses[0].distance + " meters");
            c1address.setText(address[0]);
            choice2.setText(localResponse.businesses[1].name);
            c2details.setText("~" + (int)localResponse.businesses[1].distance + " meters");
            c2address.setText(address[1]);
            choice3.setText(localResponse.businesses[2].name);
            c3details.setText("~" + (int)localResponse.businesses[2].distance + " meters");
            c3address.setText(address[2]);
            choice4.setText(localResponse.businesses[3].name);
            c4details.setText("~" + (int)localResponse.businesses[3].distance + " meters");
            c4address.setText(address[3]);
            choice5.setText(localResponse.businesses[4].name);
            c5details.setText("~" + (int)localResponse.businesses[4].distance + " meters");
            c5address.setText(address[4]);

/**
            choice1.setText("STOPPING WASTE");
            choice2.setText("STOPPING WASTE");
            choice3.setText("STOPPING WASTE");
*/
        } catch (Exception e) {
            System.out.println("java io exception in get distance");
        }
    }

    /**
     * This method pops up a new error window if there are less than 5 relevant results.
     */
    public static void alertError() {
        TextArea text = new TextArea("Error: There are less than 5 available results."
            + "\n" + "Please enter another query in the search bar.");
        text.setEditable(false);
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().setContent(text);
        alert.setResizable(false);
        alert.showAndWait();
    } // alertError

    /**
     * This methods sets up the scene.
     */
    private void setScene() {
   // setup scene
        botHbox.getChildren().addAll(locationBanner, loading);
        c1.getChildren().addAll(c1details, c1address);
        c2.getChildren().addAll(c2details, c2address);
        c3.getChildren().addAll(c3details, c3address);
        c4.getChildren().addAll(c4details, c4address);
        c5.getChildren().addAll(c5details, c5address);

        bar.getChildren().addAll(search, cuisine);

        root.getChildren().addAll( disclaimer, banner, choice1, c1, choice2, c2,
            choice3, c3, choice4, c4, choice5,
            c5, bar, find, botHbox);

    }
}

 // ApiApp
