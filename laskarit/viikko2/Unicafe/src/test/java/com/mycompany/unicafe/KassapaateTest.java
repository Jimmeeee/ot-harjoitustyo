/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.unicafe;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hytonenj
 */
public class KassapaateTest {
    Kassapaate kassapaate;
    
    public KassapaateTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        kassapaate = new Kassapaate();
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void rahamaaraJaMyydytLounaat(){
        
        assertTrue(kassapaate.kassassaRahaa()==100000);
        assertTrue(kassapaate.edullisiaLounaitaMyyty()==0);
        assertTrue(kassapaate.maukkaitaLounaitaMyyty()==0);
    }
    
    @Test
    public void kateisosto(){
        assertTrue(kassapaate.syoEdullisesti(240)==0); //change
        assertTrue(kassapaate.kassassaRahaa()==100240);
        assertTrue(kassapaate.syoMaukkaasti(4)==4);
        assertTrue(kassapaate.syoMaukkaasti(400)==0);
        
        //assertTrue(kassapaate.kassassaRahaa()==100640);
        assertTrue(kassapaate.maukkaitaLounaitaMyyty()==1);
        assertTrue(kassapaate.edullisiaLounaitaMyyty()==1);
        assertTrue(kassapaate.syoEdullisesti(10)==10); //change when unable to pay
        assertTrue(kassapaate.kassassaRahaa()==100640); 
        
    }
    
    @Test
    public void korttiosto(){
        Maksukortti k = new Maksukortti(1000);
        assertTrue(kassapaate.syoEdullisesti(k));
        assertTrue(k.saldo()==760);
        assertTrue(kassapaate.syoMaukkaasti(k));
        assertTrue(k.saldo()==360);
        
        assertTrue(kassapaate.maukkaitaLounaitaMyyty()==1);
        assertTrue(kassapaate.edullisiaLounaitaMyyty()==1);
        
        assertFalse(kassapaate.syoMaukkaasti(k));
        assertTrue(k.saldo()==360);
        assertTrue(kassapaate.maukkaitaLounaitaMyyty()==1);
        
        kassapaate.syoEdullisesti(k);
        assertFalse(kassapaate.syoEdullisesti(k));
        assertTrue(k.saldo()==120);
        assertTrue(kassapaate.edullisiaLounaitaMyyty()==2);
        
        assertTrue(kassapaate.kassassaRahaa()==100000);
        
        
    }
    @Test
    public void rahanLatausKortille(){
        Maksukortti k = new Maksukortti(1000); //10e
        kassapaate.lataaRahaaKortille(k, 100);
        assertTrue(k.saldo()==1100);
        assertTrue(kassapaate.kassassaRahaa()==100100);
        kassapaate.lataaRahaaKortille(k, -100);
        assertTrue(k.saldo()==1100);
        assertTrue(kassapaate.kassassaRahaa()==100100);
    }
}
