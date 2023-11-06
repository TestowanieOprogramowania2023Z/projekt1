package ee.pw.testowanie1;


import ee.pw.testowanie1.models.PostCreateDTO;
import ee.pw.testowanie1.models.PostDTO;
import ee.pw.testowanie1.models.UserCreateDTO;
import ee.pw.testowanie1.models.UserDTO;
import ee.pw.testowanie1.services.PostService;
import ee.pw.testowanie1.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class PostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;
    private final UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Test
    public void shouldDeleteThePost() {

        //Arrange
        UserCreateDTO user = UserCreateDTO.builder()
                .username("user")
                .email("user@user")
                .build();
        userService.createUser(user);
        PostCreateDTO postCreateDTO = PostCreateDTO.builder()
                .userId(id)
                .content("message")
                .build();

        //Act
        Optional<UserDTO> userDTO = userService.getUserByUsername("user");
        assertTrue(userDTO.isPresent(), "User should exist after creation");

        UUID postId = postService.createPost(postCreateDTO);
        postService.deletePost(postId);
        Optional<PostDTO> deletedPost = postService.getPostById(postId);

        //Assert
        assertFalse(deletedPost.isPresent(), "Post should be deleted");
    }

    @Test
    public void shouldReturnIsOkBecauseOfCorrectId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestBecauseOfBadId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/wrong"))
                .andExpect(status().isBadRequest());
    }

}
