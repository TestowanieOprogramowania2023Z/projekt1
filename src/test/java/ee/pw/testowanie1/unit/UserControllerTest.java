package ee.pw.testowanie1.unit;

import ee.pw.testowanie1.controllers.UserController;
import ee.pw.testowanie1.models.UserDTO;
import ee.pw.testowanie1.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    @Test
    public void deleteUser_invokesServiceDeleteUser_whenDeleteUserInvoked() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        userController.deleteUser(id);

        // Assert
        verify(userService).deleteUser(id);
    }

    @Test
    public void getAllUsers_returnIsOk_whenCorrectDataProvided() {
        int page = 2;
        int size = 10;

        // Mock the UserService to return a list of UserDTOs
        when(userService.getUsers(PageRequest.of(page, size)))
                .thenReturn(List.of(new UserDTO(), new UserDTO()));

        ResponseEntity<List<UserDTO>> responseEntity = userController.getAllUsers(page, size);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
    }

    @Test
    public void getAllUsers_returnBadRequest_whenPageNumberIsNegative() {
        int page = -1;
        int size = 1;

        ResponseEntity<List<UserDTO>> responseEntity = userController.getAllUsers(page, size);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void getAllUsers_returnBadRequest_whenPageSizeIsNegative() {
        int page = 1;
        int size = -1;

        ResponseEntity<List<UserDTO>> responseEntity = userController.getAllUsers(page, size);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    @Test
    public void getAllUsers_returnBadRequest_whenServiceThrowsException() {
        int page = 2;
        int size = 10;

        when(userService.getUsers(PageRequest.of(page, size)))
                .thenThrow(new RuntimeException());

        ResponseEntity<List<UserDTO>> responseEntity = userController.getAllUsers(page, size);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}
