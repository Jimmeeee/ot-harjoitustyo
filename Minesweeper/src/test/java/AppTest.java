/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mycompany.minesweeper.App;
import com.mycompany.minesweeper.Tile;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author hytonenj
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class AppTest {
    
    @Autowired
    private App app = new App();
    
    @Test
    public void isValid() {
        assertTrue(app.isValid(0, 0));
        assertFalse(app.isValid(11, 11));
        assertFalse(app.isValid(-1, 0));
    }
    
    @Test
    public void rightNumberOfNeighbours() {
        Tile tile = new Tile(0,0,true);
        List<Tile> list = app.getNeighbors(tile);
        assertTrue(list.size()==3);
        
        Tile tile2 = new Tile(1,1,true);
        List<Tile> list2 = app.getNeighbors(tile2);
        assertTrue(list2.size()==8);
    }
}
