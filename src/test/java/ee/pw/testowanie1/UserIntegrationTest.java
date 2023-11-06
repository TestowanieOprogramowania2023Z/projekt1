package ee.pw.testowanie1;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.pw.testowanie1.models.UserCreateDTO;
import ee.pw.testowanie1.models.UserDTO;
import ee.pw.testowanie1.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
}