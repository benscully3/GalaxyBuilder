package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BlackHoleTest {
    private BlackHole blackHole;
    private double radius;

    @Test
    public void constructorTest(){
        blackHole = new BlackHole("black hole", 10);
        radius = 4.2e-6 * 10;
        assertEquals(10, blackHole.getMass());
        assertEquals(radius, blackHole.getRadius());
    }

}