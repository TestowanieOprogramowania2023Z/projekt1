package ee.pw.testowanie1;

import ee.pw.testowanie1.controllers.UserController;
import ee.pw.testowanie1.models.UserDTO;
import ee.pw.testowanie1.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    @Test
    public void shouldCallServiceDeleteMethod() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        userController.deleteUser(id);

        // Assert
        verify(userService).deleteUser(id);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        List<UserDTO> users = List.of(
                UserDTO.builder()
                        .id(id1)
                        .username("user1")
                        .email("user1@gmail.com")
                        .build(),
                UserDTO.builder()
                        .id(id2)
                        .username("user2")
                        .email("user2@gmail.com")
                        .build(),
                UserDTO.builder()
                        .id(id3)
                        .username("user3")
                        .email("user3@gmail.com")
                        .build()
        );
        when(userService.getUsers(PageRequest.of(0, 5))).thenReturn(users);

        // Act
        ResultActions result = mockMvc.perform(get("/api/users"));

        // Assert
        result.andExpect(status().is(HttpStatus.OK.value()));
    }
}
