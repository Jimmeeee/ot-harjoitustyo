/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mycompany.minesweeper.Tile;
import de.saxsys.javafx.test.JfxRunner;
import javafx.scene.shape.Rectangle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author hytonenj
 */
@RunWith(JfxRunner.class)
public class TileTest {
    

    @Before
    public void setUp() {
        
    }
    
    @Test
    public void tileworks() {
        Tile tile = new Tile(0,0,true);
        assertTrue(tile.x==0);
        assertTrue(tile.y==0);
        assertTrue(tile.mine);
        assertFalse(tile.visible);
    
    }
    
}
