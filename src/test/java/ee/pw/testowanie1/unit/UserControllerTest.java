package ee.pw.testowanie1.unit;

import ee.pw.testowanie1.controllers.UserController;
import ee.pw.testowanie1.models.User;
import ee.pw.testowanie1.models.UserCreateDTO;
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
import static org.mockito.ArgumentMatchers.any;
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
    
    @Test
    void getUserById_returnIsOk_whenCorrectDataProvided() {
        UUID id = UUID.randomUUID();

        when(userService.getUserById(any())).thenReturn(java.util.Optional.of(new UserDTO()));

        ResponseEntity<UserDTO> responseEntity = userController.getUserById(id.toString());

        verify(userService).getUserById(id.toString());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
    
    @Test
    void getUserById_returnBadRequest_whenServiceThrowsException() {
        UUID id = UUID.randomUUID();

        when(userService.getUserById(any())).thenThrow(new RuntimeException());

        ResponseEntity<UserDTO> responseEntity = userController.getUserById(id.toString());

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    
    @Test
    void getUserByUsername_returnIsOk_whenCorrectDataProvided() {
        String username = "username";

        when(userService.getUserByUsername(any())).thenReturn(java.util.Optional.of(new UserDTO()));

        ResponseEntity<UserDTO> responseEntity = userController.getUserByUsername(username);

        verify(userService).getUserByUsername(username);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
    
    @Test
    void getUserByUsername_returnBadRequest_whenServiceThrowsException() {
        String username = "username";

        when(userService.getUserByUsername(any())).thenThrow(new RuntimeException());

        ResponseEntity<UserDTO> responseEntity = userController.getUserByUsername(username);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    
    @Test
    void createUser_returnIsCreated_whenCorrectDataProvided() {
        var user = new UserCreateDTO();

        when(userService.createUser(any())).thenReturn(UUID.randomUUID());

        ResponseEntity<User> responseEntity = userController.createUser(user);

        verify(userService).createUser(user);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
    
    @Test
    void createUser_returnBadRequest_whenServiceThrowsException() {
        var user = new UserCreateDTO();

        when(userService.createUser(any())).thenThrow(new RuntimeException());

        ResponseEntity<User> responseEntity = userController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}
