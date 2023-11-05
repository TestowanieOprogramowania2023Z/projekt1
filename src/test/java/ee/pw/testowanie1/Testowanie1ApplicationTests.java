package ee.pw.testowanie1;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.pw.testowanie1.models.PostCreateDTO;
import ee.pw.testowanie1.models.UserCreateDTO;
import ee.pw.testowanie1.repositories.PostRepository;
import ee.pw.testowanie1.repositories.UserRepository;
import ee.pw.testowanie1.services.PostService;
import ee.pw.testowanie1.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Testowanie1ApplicationTests {

    @Autowired
    private UserRepository repoUser;

    @Autowired
    private PostRepository repoPost;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Test
    @Transactional
    @Rollback(value = true)
    public void unitTestPostServiceCreatePost() {
        PostService postService = new PostService(repoPost);
        UUID generatedUUID = postService.createPost(PostCreateDTO.builder().content("test").build());

        assertThat(generatedUUID != null);
    }

    //checking correct creating users, and correct exceptions throwing
    @Test
    @Transactional
    @Rollback(value = true)
    public void unitTestUserServiceCreateUserException() {
        UserService userService = new UserService(repoUser);
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

    //checking correct integration of controller and userService
    @Test
    @Transactional
    @Rollback(value = true)
    public void integralTestUserControllerCreateUser() throws Exception {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("Maciej");
        user.setEmail("test@test.com");

        MvcResult result;

        result =  mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                    .andReturn();

        String requestContent = result.getRequest().getContentAsString();
        boolean userAdded = requestContent.contains("Maciej");
        assertTrue(userAdded);
    }

    @Test
    @Transactional
    @Rollback(value = true)
    public void acceptanceTestUserControllerCreateUser() throws Exception {
        UserCreateDTO user = UserCreateDTO.builder().username("Maciej").build();


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

    }

    @Test
    @Transactional
    @Rollback(value = true)
    public void integralTestPostControllerCreatePost() throws Exception {
        PostCreateDTO post = new PostCreateDTO();
        post.setContent("test");

        MvcResult result;

        result = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andReturn();

        String requestContent = result.getRequest().getContentAsString();
        boolean postAdded = requestContent.contains("test");
        assertTrue(postAdded);
    }

    @Test
    @Transactional
    @Rollback(value = true)
    public void acceptanceTestPostControllerCreateUser() throws Exception {
        PostCreateDTO post = PostCreateDTO.builder().content("Maciej").build();


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated());

    }

}


