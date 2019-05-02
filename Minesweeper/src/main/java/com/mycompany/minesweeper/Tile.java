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
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


/**
 *
 * @author hytonenj
 */
public class Tile extends StackPane {
    public int x;
    public int y;
    public boolean mine;
    public boolean visible;
    public boolean flag;
    
    public Rectangle border = new Rectangle(App.tileSize - 2, App.tileSize - 2); 
    public Text text = new Text();
    
    public Tile(int x, int y, boolean mine) { 
        this.mine = mine;
        this.x = x;
        this.y = y;
        this.visible = false;
        this.flag = false;
        border.setFill(Color.DARKGRAY);
        text.setVisible(false);
                
        getChildren().addAll(border);
        setTranslateX(x * App.tileSize);
        setTranslateY(y * App.tileSize);
        
        //mouse click event
        this.setOnMouseClicked((event) -> {
            MouseButton mb = event.getButton();
            if (mb == MouseButton.PRIMARY) {
                open();
            } else if (mb == MouseButton.SECONDARY) {
                if (visible) { //can't click open tile
                    return;
                }
                if (App.gameEnd) { //if game ended can't open more tiles
                    return;
                }
                if (!this.flag) {
                    setFlag();
                    flag = true;
                } else {
                    removeFlag();
                    flag = false;
                }
            }
        });
    }
    
 /**
 * Metodi asettaa numeroidun kuvan tiileen sen ympärillä olevien miinojen perusteella.
 *
 * @param   text Laatan ympärillä olevien miinojen määrä
 * 
 */
    public void setTileImage(String text) {
        String number = "";
        if (text.contains("1")) {
            number = "1";
        } else if (text.contains("2")) {
            number = "2";
        } else if (text.contains("3")) {
            number = "3";
        } else if (text.contains("4")) {
            number = "4";
        } else if (text.contains("5")) {
            number = "5";
        } else if (text.contains("6")) {
            number = "6";
        } else if (text.contains("7")) {
            number = "7";
        } else if (text.contains("8")) {
            number = "8";
        } else {
            number = "empty";
        }
        tileImage(number);
    }
   
    /**
 * Metodi asettaa numeroidun kuvan syötteen perusteella.
 * 
 */
    public void tileImage(String number) {
        File file = new File("css/numbers/" + number + ".PNG");
        Image image = new Image(file.toURI().toString(), App.tileSize - 2, App.tileSize - 2, false, false);
        ImageInput imageInput = new ImageInput(); 
        imageInput.setX(0); 
        imageInput.setY(0);
        imageInput.setSource(image); 
        border.setEffect(imageInput); 
    }
    
    /**
 * Metodi asettaa lipullisen tiilen harmaan tiilen tilalle.
 * 
 */
    public void removeFlag() {
        File file = new File("css/blank.png");
        Image image = new Image(file.toURI().toString(), App.tileSize - 2, App.tileSize - 2, false, false);
        ImageInput imageInput = new ImageInput(); 
        imageInput.setX(0); 
        imageInput.setY(0);
        imageInput.setSource(image); 
        border.setEffect(imageInput); 
        
    }

    /**
    * Metodi asettaa lipun harmaan tiilen tilalle.
    * 
    */
    
    public void setFlag() {
        File file = new File("css/flag.png");
        Image image = new Image(file.toURI().toString(), App.tileSize - 2, App.tileSize - 2, false, false);

        ImageInput imageInput = new ImageInput(); 
        imageInput.setX(0); 
        imageInput.setY(0);
        imageInput.setSource(image); 
        border.setEffect(imageInput); 
        
        
    }
    /**
    * Metodi asettaa miinallisen tiilen harmaan tiilen tilalle.
    * 
    */
    public void setMineImage() {
        File file = new File("css/mine.jpg");
        Image image = new Image(file.toURI().toString(), App.tileSize - 2, App.tileSize - 2, false, false);
        ImageInput imageInput = new ImageInput(); 
        imageInput.setX(0); 
        imageInput.setY(0);
        imageInput.setSource(image); 
        border.setEffect(imageInput); 
        
    }
    
    /**
    * Metodi avaa avaamattoman tiilen.
    *  
    */
    public void open() {
        if (visible) { //can't open tile
            return;
        }
        
        if (App.gameEnd) { //can't open more tiles
            return;
        }
        
        if (App.firstClick) { //timer starts when first tile is opened
            App.timer.start();
            App.firstClick = false;
        }
        
        visible = true;   //make clicked tile visible
        text.setVisible(true);  //set text visible
        
        String[] elements = this.text.toString().split(",");
        setTileImage(elements[0]);
        if (this.mine) {    //if mine is clicked game over
            setMineImage();
            App.gameOver();
            App.timer.stop();
            App.openMines();
            return;
        }
        if (App.checkWin()) {   //if you win, timer stops and winscreen opens
            App.gameOver();
            App.timer.stop();
            checkScore(Long.parseLong(App.display.getText()));
            return;
        }
        if (text.getText().isEmpty()) { //open nearby tiles if current tile is empty
            App.getNeighbors(this).forEach(Tile::open);
        }
    }
    
    /**
    * Metodi hakee tietokannan kymmenennellä paikalla olevan henkilön ajan, vertaa sitä käyttäjän saamaan aikaan
    * ja asettaa uuden ruudun sen perusteella.
    *  
    * @param   newScore Saatu aika pelin loppuessa
    * 
    * @see  App#winScreen() 
    * @see  App#noHighscoreScreen()
    */
    
    public void checkScore(Long newScore) {
        Long score = (long) 0;
        try {
            Connection con = DriverManager.getConnection("jdbc:h2:./leaderboard", "sa", "");
            PreparedStatement ps = con.prepareStatement("Select * FROM " + App.getTableName() + " order by score limit 1 offset 9");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                score = rs.getLong("score");
            }
            ps.close();
            rs.close();
            con.close();
        } catch (Exception e) {
        }
        if (newScore < score || score == 0) {
            App.winScreen();
        } else {
            App.noHighscoreWinScreen();
        }
        
    }
}