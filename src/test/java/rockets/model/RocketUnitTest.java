package rockets.model;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class RocketUnitTest {

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @DisplayName("should create rocket successfully when given right parameters to constructor")
    @Test
    public void shouldConstructRocketObject() {
        String name = "BFR";
        String country = "USA";
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");
        Rocket bfr = new Rocket(name, country, manufacturer);
        assertNotNull(bfr);
    }

    @DisplayName("should throw exception when given null manufacturer to constructor")
    @Test
    public void shouldThrowExceptionWhenNoManufacturerGiven() {
        String name = "BFR";
        String country = "USA";
        assertThrows(NullPointerException.class, () -> new Rocket(name, country, null));
    }

    @DisplayName("should set rocket massToLEO value")
    @ValueSource(strings = {"10000", "15000"})
    public void shouldSetMassToLEOWhenGivenCorrectValue(String massToLEO) {
        String name = "BFR";
        String country = "USA";
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");

        Rocket bfr = new Rocket(name, country, manufacturer);

        bfr.setMassToLEO(massToLEO);
        assertEquals(massToLEO, bfr.getMassToLEO());
    }

    @DisplayName("should throw exception when set massToLEO to null")
    @Test
    public void shouldThrowExceptionWhenSetMassToLEOToNull() {
        String name = "BFR";
        String country = "USA";
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");
        Rocket bfr = new Rocket(name, country, manufacturer);
        assertThrows(NullPointerException.class, () -> bfr.setMassToLEO(null));

    }

    @DisplayName("should throw exception when set massToGTO to null")
    @Test
    public void shouldThrowExceptionWhenSetMassToGTOToNull() {
        String name = "BFR";
        String country = "USA";
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");
        Rocket bfr = new Rocket(name, country, manufacturer);
        assertThrows(NullPointerException.class, () -> bfr.setMassToGTO(null));

    }

    @DisplayName("should throw exception when set massToOther to null")
    @Test
    public void shouldThrowExceptionWhenSetMassToOtherToNull() {
        String name = "BFR";
        String country = "USA";
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");
        Rocket bfr = new Rocket(name, country, manufacturer);
        assertThrows(NullPointerException.class, () -> bfr.setMassToOther(null));

    }

    @DisplayName("should return true when two rockets have the same details")
    @Test
    public void shouldReturnTrueWhenRocketsHaveSameDetails() {
        String name = "BFR";
        String country = "USA";
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");
        Rocket bfr = new Rocket(name, country, manufacturer);
        String name2 = "BFR";
        String country2 = "USA";
        LaunchServiceProvider manufacturer2 = new LaunchServiceProvider("SpaceX", 2002, "USA");
        Rocket anotherRocket = new Rocket(name2, country2, manufacturer2);
        assertTrue(bfr.equals(anotherRocket));
    }

    @DisplayName("should return false when two rockets have the different details")
    @Test
    public void shouldReturnTrueWhenRocketsHaveDifferentDetails() {
        String name = "BFR";
        String country = "USA";
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");
        Rocket bfr = new Rocket(name, country, manufacturer);
        String name2 = "Apollo";
        String country2 = "USA";
        LaunchServiceProvider manufacturer2 = new LaunchServiceProvider("SpaceY", 2002, "USA");
        Rocket anotherRocket = new Rocket(name2, country2, manufacturer2);
        assertFalse(bfr.equals(anotherRocket));
    }

    @DisplayName("should throw exception when given null name to constructor")
    @Test
    public void shouldThrowExceptionWhenNoNameGiven() {
        String country = "USA";
        LaunchServiceProvider lsp = new LaunchServiceProvider("test",2018,"USA");
        assertThrows(NullPointerException.class, () -> new Rocket(null, country, lsp));
    }

    @DisplayName("should throw exception when given null country to constructor")
    @Test
    public void shouldThrowExceptionWhenNoCountryGiven() {
        String name = "BFR";
        LaunchServiceProvider lsp = new LaunchServiceProvider("test",2018,"USA");
        assertThrows(NullPointerException.class, () -> new Rocket(name, null, lsp));
    }

    @DisplayName("should throw exception when given null country to constructor")
    @Test
    public void shouldBeTrueWhenRocketPointsToValidLSP() {
        String name = "BFR";
        String country ="USA";
        LaunchServiceProvider lsp = new LaunchServiceProvider("test",2018,"USA");
        Rocket target = new Rocket(name,country,lsp);
        assertEquals(lsp,target.getManufacturer());
        assertEquals(LaunchServiceProvider.class,target.getManufacturer().getClass());
    }

}