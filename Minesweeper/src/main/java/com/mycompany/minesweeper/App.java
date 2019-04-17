/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.minesweeper;

import javafx.scene.paint.Color;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author hytonenj
 */
public class App extends Application{
    private static int width = 400;
    private static int height = 400;
    public static int tileSize = 40; 
    public static int mines = 10;
    private static int xTiles = width/tileSize;
    private static int yTiles = height/tileSize;
    private static Scene scene;
    private static Tile[][] grid = new Tile[xTiles][yTiles];
    public static boolean gameEnd = false; 
    public static boolean firstClick = true;
    private static Text difficulty = new Text();
    private static Text top10Mode = new Text();
    private static final ScrollPane sPane = new ScrollPane();
    private static Pane root;
    public static TextField display = new TextField("0");
    
    
    public static Parent createContent(){
        root = new Pane();
        root.setPrefSize(width, height);
        
        for(int row = 0; row < xTiles; row++){
            for(int col = 0; col < yTiles; col++){
                Tile tile = new Tile(row, col, false);
                grid[row][col] = tile;
                root.getChildren().add(tile);
                
            }
        }
        numberOfMines();
        placeMines(root);
        return initialize();
    }
        
    
    public static BorderPane initialize(){
        display.setEditable(false);
        display.setPrefWidth(80);
        
        Button button = new Button("New Game");
        button.setPrefWidth(90);
        button.setOnMouseClicked((event) -> {
            gameEnd = false;
            timer.stop();
            firstClick = true;
            scene.setRoot(createContent());
        });
        
        
        Button highscoreButton = new Button("Leaderboard"); 
        highscoreButton.setOnMouseClicked((event) -> {
            timer.stop();
            highscoreScreen();
        });
        
        ObservableList<String> options = 
            FXCollections.observableArrayList(
                "Easy",
                "Medium",
                "Hard"
            );
        ComboBox comboBox = new ComboBox(options);
        comboBox.setPrefWidth(100);
        
        if(difficulty.getText().isEmpty())
            comboBox.setValue("Easy");
        else
            comboBox.setValue(difficulty.getText());
        
        comboBox.getSelectionModel().selectedItemProperty().addListener((opts, oldValue, newValue) -> {
            difficulty.setText(newValue.toString());
        });
        
        HBox box = new HBox();
        box.setSpacing(3);
        box.setPadding(new Insets(15, 12, 15, 12));
        box.setStyle("-fx-background-color: #2699AE;");
        box.getChildren().add(button);
        box.getChildren().add(comboBox);
        box.getChildren().add(display);
        box.getChildren().add(highscoreButton);
        
        BorderPane bpane = new BorderPane();
        sPane.setContent(root);
        sPane.setVbarPolicy(ScrollBarPolicy.NEVER);
        sPane.setHbarPolicy(ScrollBarPolicy.NEVER);

        bpane.setTop(box);
        bpane.setCenter(sPane);
        return bpane;
    } 
    
    public static void numberOfMines(){
        if(difficulty.getText().equals("Medium")) {
            mines = 15;
        }else if(difficulty.getText().equals("Hard")) {
            mines = 20;
        }else {
            mines = 10;
        }
    }
    
    public static void placeMines(Pane root){
        int placed = 0;
        double chance = 0.05;
        
        loop:
        while(placed <= mines){
            for(int row = 0; row < xTiles; row++){
                for(int col = 0; col < yTiles; col++){
                   
                    if(grid[row][col].mine)
                        ;
                    else{
                        double r = Math.random();
                        Tile tile = new Tile(row, col, r < chance); // chance to place mine
                        grid[row][col] = tile;
                        root.getChildren().add(tile);
                        
                        if(r<chance)
                            placed++;
                        if(placed==mines)
                            break loop;
                        
                    }
                }
            }
            chance += 0.05; //increase the chance to place a mine
        }
        
        //count number of nearby mines and place text-element based on that count
        for(int row = 0; row < xTiles; row++){
            for(int col = 0; col < yTiles; col++){
                if(grid[row][col].mine)
                    continue;
                Tile tile = grid[row][col];
                long mines = getNeighbors(tile).stream().filter(t -> t.mine).count();
                if(mines > 0)
                    tile.text.setText(String.valueOf(mines));
            }
        }
    } 
    
    
    public static List<Tile> getNeighbors(Tile tile){
        List<Tile> neighbors = new ArrayList<>();
        
        int[] coords = new int[]{
                -1,-1,
                -1, 0,
                -1, 1,
                 0,-1,
                 0, 1,
                 1,-1,
                 1, 0,
                 1, 1
        };
        
        for(int i = 0; i< coords.length; i++){
            int dx = coords[i];
            int dy = coords[++i];
            
            int newX = tile.x + dx;
            int newY = tile.y + dy;
            
            if(isValid(newX, newY))
                neighbors.add(grid[newX][newY]);
        }
        
        return neighbors;
        
    }
    
    public static void openMines(){
        File file = new File("css/mine_revealed.png");
        Image image = new Image(file.toURI().toString(), App.tileSize - 2, App.tileSize - 2, false, false);
        ImageInput imageInput = new ImageInput(); 
        imageInput.setX(0); 
        imageInput.setY(0);
        imageInput.setSource(image); 
        
        for(int row = 0; row < xTiles; row++){
            for(int col = 0; col < yTiles; col++){
                Tile tile = grid[row][col];
                if(tile.mine && !tile.visible) {
                    tile.visible = true;
                    tile.text.setVisible(true);
                    tile.border.setEffect(imageInput);
                }
                
            }
        }
    }
    
    
    public static boolean isValid(int x, int y){
        return (x >= 0 && x < xTiles && y >= 0 && y < yTiles);
    }
    
    public static boolean checkWin(){
        int count = 0;
        for(int row = 0; row < xTiles; row++){
            for(int col = 0; col < yTiles; col++){
                if(grid[row][col].mine)
                    continue;
                Tile tile = grid[row][col];
                if(tile.text.isVisible()) {
                    count++;
                }
            }
        }
        if(count == (xTiles * yTiles - mines))
            return true;
        else
            return false;
    }
    
    public static void gameOver() {
        gameEnd = true;
    }
    
    public static void noHighscoreWinScreen(){
        Button newGameButton = new Button("New Game");
        
        Label label = new Label("YOU WIN !");
        label.setFont(new Font(16));
        label.setStyle("-fx-font-weight: bold;");
        label.setTextFill(Color.web("#FF69B4"));
        
        GridPane newGameScreen = new GridPane();
        newGameScreen.add(label, 0, 0);
        newGameScreen.add(newGameButton, 0, 1);
        newGameScreen.setPrefSize(300, 180);          
        newGameScreen.setAlignment(Pos.TOP_CENTER);
        newGameScreen.setVgap(10);
        newGameScreen.setHgap(10);
        newGameScreen.setPadding(new Insets(20, 20, 20, 20));
        
        scene.setRoot(newGameScreen); // set win screen
        newGameScreen.setId("pane2");
        File f1 = new File("css/win.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f1.getAbsolutePath().replace("\\", "/"));

        newGameButton.setOnAction((event) -> {
           scene.setRoot(createContent());
           gameEnd = false;
           firstClick = true;
        });
    }
    
    public static void highscoreScreen(){
        BorderPane leaderboardScreen = new BorderPane();
        Button returnButton = new Button("return");
        returnButton.setOnAction((event) -> {
            scene.setRoot(createContent());
            gameEnd = false;
            firstClick = true;
        });
        
        ObservableList<String> options = 
            FXCollections.observableArrayList(
                "Top10 Easy",
                "Top10 Medium",
                "Top10 Hard"
            );
        ComboBox comboBox = new ComboBox(options);
        comboBox.setValue("Top10 Easy");
        if(top10Mode.getText().isEmpty())
            comboBox.setValue("Top10 Easy");
        else
            comboBox.setValue(top10Mode.getText());
        
        comboBox.getSelectionModel().selectedItemProperty().addListener((opts, oldValue, newValue) -> {
            top10Mode.setText(newValue.toString());
            highscoreScreen();
        });
        
        
        //sql
        String[] pieces = comboBox.getValue().toString().split(" ");
        String tableName = pieces[1];
        List<String> leaders = new ArrayList<>();
        try{
            Connection con = DriverManager.getConnection("jdbc:h2:./leaderboard", "sa", "");
            PreparedStatement ps = con.prepareStatement("Select top 10 * FROM "+ tableName +" order by score");
            ResultSet rs = ps.executeQuery();
            
            int number = 1;
            while(rs.next()){
                leaders.add(number+". "+rs.getString("name")+": "+rs.getLong("score")+"s");
                number++;
            }
            ps.close();
            rs.close();
            con.close();
        }catch(Exception e){
            
        }
        
        //add data
        final ObservableList data = 
        FXCollections.observableArrayList();
       
        final ListView listView = new ListView(data);
        listView.setPrefSize(200, 250);
        listView.setEditable(true);
        
        for (int i = 0; i < leaders.size(); i++) {
            data.add(leaders.get(i));
        }
        
        //set elements
        HBox box = new HBox();
        box.setSpacing(3);
        box.setPadding(new Insets(15, 12, 15, 12));
        box.setStyle("-fx-background-color: #2699AE;");
        box.getChildren().add(returnButton);
        box.getChildren().add(comboBox);
        
        listView.setItems(data);
        leaderboardScreen.setTop(box);
        leaderboardScreen.setCenter(listView);
        scene.setRoot(leaderboardScreen);
    }
    
    
    public static void winScreen() {
        Label label = new Label("New Highscore !");
        label.setFont(new Font(16));
        label.setStyle("-fx-font-weight: bold;");
        label.setTextFill(Color.web("#FF69B4"));

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.1), label);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.play();
        
        Label text = new Label(display.getText()+" seconds"+"\nPlease enter your name");
        TextField textfield = new TextField();
        Button saveButton = new Button("Save");
        Label errortext = new Label("");

        // Winning screen grid
        GridPane highscoreScreen = new GridPane();
        highscoreScreen.add(label, 0, 0);
        highscoreScreen.add(text, 0, 1);
        highscoreScreen.add(textfield, 0, 2);
        highscoreScreen.add(saveButton, 0, 3);
        highscoreScreen.add(errortext, 0, 4);

        highscoreScreen.setPrefSize(300, 180);
        highscoreScreen.setAlignment(Pos.CENTER);
        highscoreScreen.setVgap(10);
        highscoreScreen.setHgap(10);
        highscoreScreen.setPadding(new Insets(20, 20, 20, 20));

        //get style
        highscoreScreen.setId("pane");
        File f = new File("css/winstyle.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        scene.setRoot(highscoreScreen); // set grid

        // new highscore screen
        Label highscoreText = new Label("Highscore saved");
        Button newGameButton = new Button("New Game");

        GridPane newGameScreen = new GridPane();
        newGameScreen.add(highscoreText, 0, 0);
        newGameScreen.add(newGameButton, 0, 1);
        newGameScreen.setPrefSize(300, 180);          
        newGameScreen.setAlignment(Pos.CENTER);
        newGameScreen.setVgap(10);
        newGameScreen.setHgap(10);
        newGameScreen.setPadding(new Insets(20, 20, 20, 20));

        newGameButton.setOnAction((event) -> {
        scene.setRoot(createContent());
        gameEnd = false;
        firstClick = true;
        });

        saveButton.setOnAction((event) -> {
        if (textfield.getText().trim().isEmpty()) {
            errortext.setText("You must enter name");
            return;
        }
          //save highscore to repository
        String name = textfield.getText();
        Long score = Long.parseLong(display.getText());
        String tableName = getTableName();
        try{
            Connection con = DriverManager.getConnection("jdbc:h2:./leaderboard", "sa", "");

            PreparedStatement ps = con.prepareStatement("Insert INTO "+ tableName +"(name, score) VALUES (?,?)");
            ps.setString(1, name);
            ps.setLong(2, score);
            ps.executeUpdate();
            con.close();
            ps.close();

        }catch(Exception e){

        }

        scene.setRoot(newGameScreen); // set highscore screen
        newGameScreen.setId("pane");
        File f1 = new File("css/winstyle.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f1.getAbsolutePath().replace("\\", "/"));

        });
    }
    
    public static String getTableName(){
        if(mines == 10)
            return "Easy";
        if(mines == 15)
            return "Medium";
        else
            return "Hard";
        
    }
    
    //AnimationTimer
    static AnimationTimer timer = new AnimationTimer() {
        private long timestamp;
        private long time = 0;
        private long fraction = 0;



        @Override
        public void start() {
            // current time adjusted by remaining time from last run
            time = 0;
            timestamp = System.currentTimeMillis() - fraction;
            super.start();
        }

        @Override
        public void stop() {
            time = 0;
            fraction = 0;

            super.stop();
            time = 0;
        }

        @Override
        public void handle(long now) {
            long newTime = System.currentTimeMillis();
            if (timestamp + 1000 <= newTime) {
                long deltaT = (newTime - timestamp) / 1000;
                time += deltaT;
                timestamp += 1000 * deltaT;
                display.setText(Long.toString(time));
            }
        }
    };
    
    @Override
    public void start(Stage stage) throws Exception{
        scene = new Scene(createContent());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    } 
    
}
