/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.minesweeper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 *
 * @author hytonenj
 */
public class Tile extends StackPane {
    public int x;
    public int y;
    public boolean mine;
    public boolean visible;
    
    
    public Rectangle border = new Rectangle(App.tileSize - 2, App.tileSize - 2); 
    public Text text = new Text();
    
    public Tile(int x, int y, boolean mine) { 
        this.mine = mine;
        this.x = x;
        this.y = y;
        this.visible = false;
        
        border.setStroke(Color.LIGHTGRAY);
        
        if (mine) {
            text.setText(" ");
        } else {
            text.setText("");
        }
        border.setFill(Color.DARKGRAY);
        text.setVisible(false);
                
        getChildren().addAll(border, text);
        setTranslateX(x * App.tileSize);
        setTranslateY(y * App.tileSize);
        
        //mouse click event
        this.setOnMouseClicked((event) -> {
            open();
            
        });
        
    }
    
    public void open() {
        if (visible) { //can't click open tile
            return;
        }
        
        if (App.gameEnd) { //if game ended can't open more tiles
            return;
        }
        
        if (App.firstClick) { //timer starts when first tile is opened
            
            App.timer.start();
            App.firstClick = false;
        }
        
        visible = true;   //make clicked tile visible
        text.setVisible(true);  //make text visible
        border.setFill(Color.WHITE);    //change color
        
        if (this.mine) {    //if mine is clicked game over
            //System.out.println("Game Over");
            File file = new File("src/main/java/com/mycompany/minesweeper/css/test.gif");
            Image image = new Image(file.toURI().toString(), App.tileSize - 2, App.tileSize - 2, false, false);
            
            ImageInput imageInput = new ImageInput(); 
            imageInput.setX(0); 
            imageInput.setY(0);
            
            imageInput.setSource(image); 
            border.setEffect(imageInput); 
            App.makeUnclickable();
            App.timer.stop();
            return;
        }
        if (App.checkWin()) {   //if you win, timer stops and winscreen opens
            App.makeUnclickable();
            App.timer.stop();
            App.winScreen();
            return;
        }
        if (text.getText().isEmpty()) { //open nearby tiles if current tile is empty
           App.getNeighbors(this).forEach(Tile::open);
        }
    }

}