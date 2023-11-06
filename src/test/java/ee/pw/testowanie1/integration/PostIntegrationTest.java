package ee.pw.testowanie1.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import ee.pw.testowanie1.controllers.UserController;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class PostIntegrationTest {

    private final UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private final String postContent = "some content";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldDeleteThePost() {
        //Arrange
        UserCreateDTO user = UserCreateDTO.builder()
                .username("user")
                .email("user@user")
                .build();
        userService.createUser(user);
        PostCreateDTO postCreateDTO = PostCreateDTO.builder()
                .userId(userId)
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestBecauseOfBadId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/wrong"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePost_shouldReturnIsBadRequest_whenDontProvidePostContent() throws Exception {
        PostCreateDTO postCreateDTO = PostCreateDTO.builder().userId(userId).build();

        mockMvc.perform(put("/api/posts/{id}", userId).contentType("application/json")
                        .content(objectMapper.writeValueAsString(postCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePost_shouldReturnIsBadRequest_whenDontProvidePostId() throws Exception {
        PostCreateDTO postCreateDTO = PostCreateDTO.builder().content(postContent).build();

        mockMvc.perform(put("/api/posts/{id}", userId).contentType("application/json")
                        .content(objectMapper.writeValueAsString(postCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePost_shouldReturnIsBadRequest_whenProvideIdOfNotExistingPost() throws Exception {
        PostCreateDTO postCreateDTO = PostCreateDTO.builder().userId(userId).content(postContent)
                .build();

        mockMvc.perform(put("/api/posts/{id}", userId).contentType("application/json")
                        .content(objectMapper.writeValueAsString(postCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePost_shouldReturnIsOk_whenProvideIdOfExistingPost() throws Exception {

        PostCreateDTO postCreateDTO = PostCreateDTO.builder().userId(userId).content(postContent)
                .build();
        UUID createdPostId = postService.createPost(postCreateDTO);

        mockMvc.perform(put("/api/posts/{id}", createdPostId).contentType("application/json")
                        .content(objectMapper.writeValueAsString(postCreateDTO)))
                .andExpect(status().isOk());

    }

    @Test
    public void integralTestPostControllerCreatePost() throws Exception {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("Maciej");
        user.setEmail("test@test.com");

        UserController userController = new UserController(userService);
        userController.createUser(user);
        UUID userUUID = userController.getUserByUsername("Maciej").getBody().getId();

        PostCreateDTO post = new PostCreateDTO();
        post.setContent("test");
        post.setUserId(userUUID);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/posts"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        boolean postAdded = responseContent.contains("test");

        assertTrue(postAdded);
    }

    @Test
    public void acceptanceTestPostControllerCreateUser() throws Exception {
        PostCreateDTO post = PostCreateDTO.builder().content("Maciej").build();


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated());

    }

    @Test
    public void unitTestPostServiceCreatePost() {
        UUID generatedUUID = postService.createPost(PostCreateDTO.builder().content("test").build());

        assertNotNull(generatedUUID);
    }
}
