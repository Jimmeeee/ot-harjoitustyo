/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.minesweeper;

import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
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
    public Rectangle border = new Rectangle(38, 38); // tile size - 2
    public Text text = new Text();
    
    public Tile(int x, int y, boolean mine) { 
        this.mine = mine;
        this.x = x;
        this.y = y;
        
        border.setStroke(Color.DARKGRAY);
        border.setFill(Color.WHITE);
        
        if (mine) {
            text.setText("x");
        } else {
            text.setText("");
        }
        getChildren().addAll(border, text);
        setTranslateX(x * 40);
        setTranslateY(y * 40);
    }
}