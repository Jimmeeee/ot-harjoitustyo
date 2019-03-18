package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(10);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti!=null);      
    }
    
    @Test
    public void kortinSaldoOikein(){
        assertTrue(kortti.saldo()==10);
    }
    @Test
    public void rahanLatausKasvattaaSaldoa(){
        kortti.lataaRahaa(2);
        assertTrue(kortti.saldo()==12);
    }
    
    @Test
    public void rahanOttaminenOnnistuu(){
        assertTrue(kortti.otaRahaa(5));
        assertTrue(kortti.saldo()==5);
        assertFalse(kortti.otaRahaa(10));
        assertTrue(kortti.saldo()==5);
    }
    
    @Test
    public void palauttaaOikeanTekstin(){
        assertEquals("saldo: 0.10", kortti.toString());
        // There was a mistake in maksukortti tostring method
    }
}
