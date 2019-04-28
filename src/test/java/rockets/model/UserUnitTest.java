package rockets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UserUnitTest {
    private User target;

    @BeforeEach
    public void setUp() {
        target = new User();
    }


    @DisplayName("should throw exception when pass a empty email address to setEmail function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetEmailToEmpty(String email) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setEmail(email));
        assertEquals("email cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass null to setEmail function")
    @Test
    public void shouldThrowExceptionWhenSetEmailToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setEmail(null));
        assertEquals("email cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exceptions when pass a null password to setPassword function")
    @Test
    public void shouldThrowExceptionWhenSetPasswordToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setPassword(null));
        assertEquals("password cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should return true when two users have the same email")
    @Test
    public void shouldReturnTrueWhenUsersHaveSameEmail() {
        String email = "abc@example.com";
        target.setEmail(email);
        User anotherUser = new User();
        anotherUser.setEmail(email);
        assertTrue(target.equals(anotherUser));
    }


    @DisplayName("should return false when two users have different emails")
    @Test
    public void shouldReturnFalseWhenUsersHaveDifferentEmails() {
        target.setEmail("abc@example.com");
        User anotherUser = new User();
        anotherUser.setEmail("def@example.com");
        assertFalse(target.equals(anotherUser));
    }

    @DisplayName("should return true when email entered is valid")
    @ParameterizedTest
    @ValueSource(strings = {"abc@example.com", "abcexamp@le.com", "123@test.com"})
    public void shouldReturnTrueForValidEmail(String email) {
        target.setEmail(email);
        assertTrue(email.equals(target.getEmail()));
    }

    @DisplayName("should return false when email entered is invalid")
    @ParameterizedTest
    @ValueSource(strings = {"abexample.com", "abcexamplecom", "abc"})
    public void shouldReturnFalseForInvalidEmail(String email) {
        target.setEmail(email);
        assertFalse(email.equals(target.getEmail()));
    }

    @DisplayName("should return true when valid password is entered")
    @ParameterizedTest
    @ValueSource(strings = {"Tes@12345", "@bcd65Efgh", "Quest!0nss"})
    public void shouldReturnTrueForValidPasswords(String password) {
        target.setPassword(password);
        assertTrue(password.equals(target.getPassword()));
    }

    @DisplayName("should return false when invalid password is entered")
    @ParameterizedTest
    @ValueSource(strings = {"test1234", "Test1234", "test@Test"})
    public void shouldReturnFalseForInvalidPasswords(String password) {
        target.setPassword(password);
        assertFalse(password.equals(target.getPassword()));
    }

    @DisplayName("should return true when firstName entered is valid")
    @ParameterizedTest
    @ValueSource(strings = {"TestName", "Testnameagain", "Test"})
    public void shouldReturnTrueForValidFirstName(String firstName) {
        target.setFirstName(firstName);
        assertTrue(firstName.equals(target.getFirstName()));
    }

    @DisplayName("should return false when firstName entered is invalid")
    @ParameterizedTest
    @ValueSource(strings = {"TestName1", "Testnameagain2", "Test123"})
    public void shouldReturnFalseForInvalidFirstName(String firstName) {
        target.setFirstName(firstName);
        assertFalse(firstName.equals(target.getFirstName()));
    }

    @DisplayName("should return true when lastName entered is valid")
    @ParameterizedTest
    @ValueSource(strings = {"Testname", "Testnameagain", "Test"})
    public void shouldReturnTrueForValidLastName(String lastName) {
        target.setLastName(lastName);
        assertTrue(lastName.equals(target.getLastName()));
    }

    @DisplayName("should return false when lastName entered is invalid")
    @ParameterizedTest
    @ValueSource(strings = {"Testname1", "Testnameagain2", "Test123"})
    public void shouldReturnFalseForInvalidLastName(String lastName) {
        target.setLastName(lastName);
        assertFalse(lastName.equals(target.getLastName()));
    }

    @DisplayName("should throw exception when pass null to setLastName function")
    @Test
    public void shouldThrowExceptionWhenSetLastNameToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setLastName(null));
        assertEquals("lastName cannot be null or empty", exception.getMessage());
    }
}
