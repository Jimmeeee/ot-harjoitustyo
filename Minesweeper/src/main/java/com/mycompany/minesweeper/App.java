/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.minesweeper;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author hytonenj
 */
public class App extends Application{
    private final int width = 400;
    private final int heigth = 400;
    private final int tileSize = 40; // do not change tile size. Or it must also be changed from Tile-class.
    private final int mines = 10;
    private final int xTiles = width/tileSize;
    private final int yTiles = heigth/tileSize;
    
    private Tile[][] grid = new Tile[xTiles][yTiles];

    
    public Parent createContent(){
        Pane root = new Pane();
        root.setPrefSize(width, heigth);
        
        
        for(int row = 0; row < xTiles; row++){
            for(int col = 0; col < yTiles; col++){
                Tile tile = new Tile(row,col,false);
                grid[row][col] = tile;
                root.getChildren().add(tile);
                
                
            }
        }
        
        placeMines(root);
        return root;

    }
    public void placeMines(Pane root){
    
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
            chance += 0.1; //grow the chance to place a mine
        }
        
        //count number of nearby mines and place text-element based on that count
        for(int row = 0; row < xTiles; row++){
            for(int col = 0; col < yTiles; col++){
                if(grid[row][col].mine)
                    continue;
                Tile tile = grid[row][col];
                long mines = getNeighbors(tile).stream().filter(t -> t.mine).count();
                tile.text.setText(String.valueOf(mines));
            }
        }
        
        
    }
          
    
    
    
    private List<Tile> getNeighbors(Tile tile){
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
    
    
    private boolean isValid(int x, int y){
        return (x >= 0 && x < xTiles && y>=0 && y< yTiles);
    }
    
    
    @Override
    public void start(Stage stage) throws Exception{
        
        stage.setScene(new Scene(createContent()));
        stage.show();
    }
//     public static void main(String[] args){
//        launch(App.class);
//    }
//    
    
}
