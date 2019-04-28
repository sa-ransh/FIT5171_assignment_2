package rockets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;


public class LaunchServiceProviderUnitTest {
    private LaunchServiceProvider target;
    String name ="java";
    Integer yearFounded = 2009;
    String country = "Australia";

    @BeforeEach
    public void setUp() {
        target = new LaunchServiceProvider(name, yearFounded, country);
    }
    /*
    @DisplayName("should throw exception when pass a empty headquarters to setHeadquarters function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetHeadquartersToEmpty(String headquarters) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setHeadquarters(headquarters));
        assertEquals("headquarter cannot be null or empty", exception.getMessage());
    }
    */

    @DisplayName("should throw exception when pass null to setHeadquarters function")
    @Test
    public void shouldThrowExceptionWhenSetHeadquartersToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setHeadquarters(null));
        assertEquals("headquarters cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should return true when 2 different objects have same hash code")
    @Test
    public void shouldReturnTrueWhenHashCodesAreSame() {
        LaunchServiceProvider anotherLaunchServiceProvider = new LaunchServiceProvider("java",2009,"Australia");
        assertTrue(anotherLaunchServiceProvider.hashCode() == target.hashCode());
    }

    @DisplayName("should return false when 2 different objects have different hash code")
    @Test
    public void shouldReturnFalseWhenHashCodesAreDifferent() {
        LaunchServiceProvider anotherLaunchServiceProvider = new LaunchServiceProvider("java",2010,"Australia");
        assertFalse(anotherLaunchServiceProvider.hashCode() == target.hashCode());
    }

    @DisplayName("should throw exception when year founded is more than current year")
    @Test
    public void shouldThrowExceptionWhenYearFoundedIsGreaterThanCurrentYear() {
        LaunchServiceProvider anotherLaunchServiceProvider = new LaunchServiceProvider("java", 2022, "Australia");
        assertFalse(Year.now().getValue() > anotherLaunchServiceProvider.getYearFounded());
    }

    @DisplayName("should return true when yearFounded is less than current year ie 2019")
    @Test
    public void shouldReturnTrueWhenYearFoundedIsLessThanCurrentYear() {
        assertTrue(target.checkValidYear(yearFounded));
    }

    @DisplayName("should throw exception when pass null to revenue function")
    @Test
    public void shouldThrowExceptionWhenSetRevenueToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setRevenue(null));
        assertEquals("Revenue cannot be null or empty", exception.getMessage());
    }
}
