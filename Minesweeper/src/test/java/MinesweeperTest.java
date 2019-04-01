/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mycompany.minesweeper.App;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Test;


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author hytonenj
 */
public class MinesweeperTest{
    private Stage stage;

    

    @Test
    public void periiLuokanApplication(){
        App sovellus = new App();

        try {
            Application app = Application.class.cast(sovellus);
            assertNotNull(app);
        } catch (Throwable t) {
        }

        this.stage = stage;
//        Scene scene = stage.getScene();
 //       assertNotNull(scene);
        

    }
}
