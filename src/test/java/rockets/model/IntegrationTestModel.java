package rockets.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class IntegrationTestModel {
    @Test
    public void IntegrationTestLspAndRocket(){
        LaunchServiceProvider lsp = new LaunchServiceProvider("Test",2017,"USA");
        Rocket tempRocket = new Rocket("TestRocket","USA",lsp);
        assertEquals(lsp,tempRocket.getManufacturer());
        assertEquals(lsp.getCountry(),tempRocket.getCountry());
        assertEquals(lsp.getClass(),tempRocket.getManufacturer().getClass());
    }

    @Test
    public void IntegrationTestUser(){
        User tempUser = new User();
        tempUser.setLastName("lastName");
        tempUser.setFirstName("firstName");
        tempUser.setEmail("test@gmail.com");
        tempUser.setPassword("Testpassword1$");
        assertEquals("lastName",tempUser.getLastName());
        assertEquals("firstName",tempUser.getFirstName());
        assertEquals("test@gmail.com",tempUser.getEmail());
        assertEquals("Testpassword1$",tempUser.getPassword());

    }
}
