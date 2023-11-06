package ee.pw.testowanie1.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.pw.testowanie1.controllers.UserController;
import ee.pw.testowanie1.models.UserCreateDTO;
import ee.pw.testowanie1.models.UserDTO;
import ee.pw.testowanie1.repositories.PostRepository;
import ee.pw.testowanie1.repositories.UserRepository;
import ee.pw.testowanie1.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repoUser;

    @Autowired
    private PostRepository repoPost;


    @Autowired
    private ObjectMapper objectMapper;

    private final UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Test
    public void shouldDeleteTheUser() {
        //Arrange
        UserCreateDTO user = UserCreateDTO.builder()
                .username("user")
                .email("user@user")
                .build();

        //Act
        userService.createUser(user);
        Optional<UserDTO> userDTO = userService.getUserByUsername("user");
        assertTrue(userDTO.isPresent(), "User should exist after creation");
        UUID id = userDTO.get().getId();
        userService.deleteUser(id);
        Optional<UserDTO> deletedUser = userService.getUserByUsername("user");

        //Assert
        assertFalse(deletedUser.isPresent(), "User should be deleted");
    }

    @Test
    public void shouldReturnIsOkBecauseOfCorrectIdTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestBecauseOfBadIdTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/wrong"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllUsersIntegration() throws Exception {
        int page = 0;
        int size = 5;

        UserCreateDTO user = UserCreateDTO.builder()
                .username("user")
                .email("user@user")
                .build();

        //Act
        userService.createUser(user);

        // Make sure you have a database with test data or use an in-memory database for testing
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserialize the JSON response into an array of UserDTO objects
        UserDTO[] userDTOs = objectMapper.readValue(jsonResponse, UserDTO[].class);

        // Perform assertions on the userDTOs array
        assertEquals(1, userDTOs.length);
    }

    @Test
    public void integralTestUserControllerCreateUser() throws Exception {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("Maciej");
        user.setEmail("test@test.com");
        userService = new UserService(repoUser);
        UserController userController = new UserController(userService);


        userController.createUser(user);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        boolean userAdded = responseContent.contains("Maciej");

        assertTrue(userAdded);
    }

    @Test
    public void acceptanceTestUserControllerCreateUser() throws Exception {
        UserCreateDTO user = UserCreateDTO.builder().username("Maciej").build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
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

        // Act
        ResultActions result = mockMvc.perform(get("/api/users"));

        // Assert
        result.andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void userServiceCreateUserException() {
        UUID generatedUUID = userService.createUser(UserCreateDTO.builder().username("Maciej").build());

        assertThat(generatedUUID != null);

        assertThrows(RuntimeException.class, () -> {
            UUID redundantUUID = userService.createUser(UserCreateDTO.builder().username("Maciej").build());
        });

        generatedUUID = userService.createUser(UserCreateDTO.builder().email("test@test.pl").build());
        assertThat(generatedUUID != null);

        assertThrows(RuntimeException.class, () -> {
            UUID redundantUUID = userService.createUser(UserCreateDTO.builder().email("test@test.pl").build());
        });
    }
}
