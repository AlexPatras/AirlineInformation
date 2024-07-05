package businessLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dataRecords.ExtrasData;

public class ExtrasTest {
    @Test
    public void testGetId() {
        ExtrasData extrasData = new ExtrasData(1, "Extra legroom", 20);

        Extras extras = new Extras(extrasData);
        int result = extras.getId();

        assertEquals(1, result);
    }

    @Test
    public void testGetDescription() {
        ExtrasData extrasData = new ExtrasData(1, "Extra legroom", 10);

        Extras extras = new Extras(extrasData);
        String result = extras.getDescription();

        assertEquals("Extra legroom", result);
    }

    @Test
    public void testGetPrice() {
        ExtrasData extrasData = new ExtrasData(1, "Extra legroom", 10);

        Extras extras = new Extras(extrasData);
        int result = extras.getPrice();

        assertEquals(10, result);
    }

}
