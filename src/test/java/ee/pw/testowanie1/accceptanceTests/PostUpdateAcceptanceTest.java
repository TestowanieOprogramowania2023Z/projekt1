package ee.pw.testowanie1.accceptanceTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.pw.testowanie1.models.PostCreateDTO;
import ee.pw.testowanie1.services.PostService;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class PostUpdateAcceptanceTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PostService postService;

  private final UUID userId = UUID.randomUUID();
  private final String postContent = "some content";

  @Test
  void updatePost_shouldReturnIsBadRequest_whenDontProvidePostContent() throws Exception {
    PostCreateDTO postCreateDTO = new PostCreateDTO().builder().userId(userId).build();

    mockMvc.perform(put("/api/posts/{id}", userId).contentType("application/json")
            .content(objectMapper.writeValueAsString(postCreateDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updatePost_shouldReturnIsBadRequest_whenDontProvidePostId() throws Exception {
    PostCreateDTO postCreateDTO = new PostCreateDTO().builder().content(postContent).build();

    mockMvc.perform(put("/api/posts/{id}", userId).contentType("application/json")
            .content(objectMapper.writeValueAsString(postCreateDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updatePost_shouldReturnIsBadRequest_whenProvideIdOfNotExistingPost() throws Exception {
    PostCreateDTO postCreateDTO = new PostCreateDTO().builder().userId(userId).content(postContent)
        .build();

    mockMvc.perform(put("/api/posts/{id}", userId).contentType("application/json")
            .content(objectMapper.writeValueAsString(postCreateDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updatePost_shouldReturnIsOk_whenProvideIdOfExistingPost() throws Exception {

    PostCreateDTO postCreateDTO = new PostCreateDTO().builder().userId(userId).content(postContent)
        .build();
    UUID createdPostId = postService.createPost(postCreateDTO);

    mockMvc.perform(put("/api/posts/{id}", createdPostId).contentType("application/json")
            .content(objectMapper.writeValueAsString(postCreateDTO)))
        .andExpect(status().isOk());

  }
}
