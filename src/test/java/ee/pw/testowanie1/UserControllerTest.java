package ee.pw.testowanie1;

import ee.pw.testowanie1.controllers.UserController;
import ee.pw.testowanie1.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    @Test
    public void CheckIfControllerCallsServiceDeleteMethodTest() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        userController.deleteUser(id);

        // Assert
        verify(userService).deleteUser(id);
    }
}
