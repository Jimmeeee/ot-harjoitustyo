/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.minesweeper;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
    
    private static final ScrollPane sPane = new ScrollPane();
    private static Pane root;
    private static TextField display = new TextField("0");
    
    
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
        
        placeMines(root);
        return initialize();

    }
    
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
                // save leftover time not handled with the last update
             //   fraction = System.currentTimeMillis() - timestamp;
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
        
    
    public static BorderPane initialize(){
        display.setEditable(false);
        display.setPrefWidth(100);
        
        Button button = new Button("New Game");
        button.setOnMouseClicked((event) -> {
            
            gameEnd = false;
            //timer.start();
            timer.stop();
            firstClick = true;
            scene.setRoot(createContent());
        });
        
        
        Button highscoreButton = new Button("Highscores"); 
        
        highscoreButton.setOnMouseClicked((event) -> {
            timer.stop();
            highscoreScreen();
        });
        
        HBox box = new HBox();
        box.setSpacing(10);
        box.setPadding(new Insets(15, 12, 15, 12));
        box.setStyle("-fx-background-color: #336699;");
        box.getChildren().add(button);
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
    
    public static void placeMines(Pane root){
    
        int placed = 0;
        double chance = 0.1;
        
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
            chance += 0.1; //increase the chance to place a mine
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
                if(tile.text.isVisible())
                    count++;
            }
        }
        if(count == (xTiles * yTiles - mines))
            return true;
        return false;
    }
    
    public static void makeUnclickable() {
        gameEnd = true;
    }
    
//    public static void gameoverScreen(){
//        Button newGameButton = new Button("New Game");
//
//        GridPane newGameScreen = new GridPane();
//        newGameScreen.add(newGameButton, 0, 0);
//        newGameScreen.setPrefSize(300, 180);          
//        newGameScreen.setAlignment(Pos.CENTER);
//        newGameScreen.setVgap(10);
//        newGameScreen.setHgap(10);
//        newGameScreen.setPadding(new Insets(20, 20, 20, 20));
//        
//        scene.setRoot(newGameScreen); // set highscore screen
//        newGameScreen.setId("pane2");
//        File f1 = new File("src/main/java/com/mycompany/minesweeper/css/gameoverstyle.css");
//        scene.getStylesheets().clear();
//        scene.getStylesheets().add("file:///" + f1.getAbsolutePath().replace("\\", "/"));
//
//        newGameButton.setOnAction((event) -> {
//           scene.setRoot(createContent());
//           gameEnd = false;
//           firstClick = true;
//        });
//    }
    
    public static void highscoreScreen(){
        BorderPane leaderboardScreen = new BorderPane();
        Button returnButton = new Button("return");
        
        returnButton.setOnAction((event) -> {
            scene.setRoot(createContent());
            gameEnd = false;
            firstClick = true;
        });
        
        final ObservableList data = 
        FXCollections.observableArrayList();
       
        final ListView listView = new ListView(data);
        listView.setPrefSize(200, 250);
        listView.setEditable(true);
         
        List<String> leaders = new ArrayList<>();
        try{
            Connection con = DriverManager.getConnection("jdbc:h2:./leaderboard", "sa", "");
            PreparedStatement ps = con.prepareStatement("Select top 10 * FROM  Person order by score");
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
        for (int i = 0; i < leaders.size(); i++) {
            data.add(leaders.get(i));
        }
        
        listView.setItems(data);
        leaderboardScreen.setTop(returnButton);
        leaderboardScreen.setCenter(listView);
        scene.setRoot(leaderboardScreen);
        

    }
    
    
    
    public static void winScreen() {
      Label text = new Label("New HighScore!\n"+display.getText()+" seconds"+"\nPlease enter you name");
      TextField textfield = new TextField();
      Button saveButton = new Button("Save");
      Label errortext = new Label("");

      // Winning screen grid
      GridPane highscoreScreen = new GridPane();
      highscoreScreen.add(text, 0, 0);
      highscoreScreen.add(textfield, 0, 1);
      highscoreScreen.add(saveButton, 0, 2);
      highscoreScreen.add(errortext, 0, 3);

      highscoreScreen.setPrefSize(300, 180);
      highscoreScreen.setAlignment(Pos.CENTER);
      highscoreScreen.setVgap(10);
      highscoreScreen.setHgap(10);
      highscoreScreen.setPadding(new Insets(20, 20, 20, 20));
      
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
          try{
            Connection con = DriverManager.getConnection("jdbc:h2:./leaderboard", "sa", "");
            PreparedStatement ps = con.prepareStatement("Insert INTO Person (name, score) VALUES (?,?)");
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
    
    
    @Override
    public void start(Stage stage) throws Exception{
        scene = new Scene(createContent());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    } 
    
}
