package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlanetTest {
    private Planet gasPlanet;
    private Planet rockyPlanet;
    private Planet planetWithMoon;
    private double rhoGas = 0.04; // gas planet density
    private double rhoRocky = 0.18; // rocky planet density

    @BeforeEach
    public void runBefore(){
        rockyPlanet = new Planet("Rocky Planet");
        gasPlanet = new Planet("Gas Planet", 10, 4, false);
        planetWithMoon = new Planet("Planet with moon", 2, 2, true);
    }

    @Test
    public void constructorTest(){
        assertEquals(10, gasPlanet.getRadius());
        assertEquals(calculateMass(false, 10), gasPlanet.getMass());
        assertEquals("Gas Planet", gasPlanet.getName());
        assertEquals(4, gasPlanet.getOrbitSize());
        assertFalse(gasPlanet.isMoon());
        assertFalse(gasPlanet.isRocky());

        assertEquals(1, rockyPlanet.getRadius());
        assertEquals(calculateMass(true, 1), rockyPlanet.getMass());
        assertEquals("Rocky Planet", rockyPlanet.getName());
        assertEquals(1, rockyPlanet.getOrbitSize());
        assertFalse(rockyPlanet.isMoon());
        assertTrue(rockyPlanet.isRocky());

        assertEquals(2, planetWithMoon.getRadius());
        assertEquals(calculateMass(true, 2), planetWithMoon.getMass());
        assertEquals("Planet with moon", planetWithMoon.getName());
        assertEquals(2, planetWithMoon.getOrbitSize());
        assertTrue(planetWithMoon.isMoon());
        assertTrue(planetWithMoon.isRocky());

    }

    @Test
    public void collideTest(){}

    private double calculateMass(boolean isRocky, double radius){
        double mass;
        double base;

        if (isRocky){
            mass = 4 * 3.14 * rhoRocky * Math.pow(radius, 3);
        }else{
            mass = 4 * 3.14 * rhoGas * Math.pow(radius, 3);
        }
        return mass;
    }
}
