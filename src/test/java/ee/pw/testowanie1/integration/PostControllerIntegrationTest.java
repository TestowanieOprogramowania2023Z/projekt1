package ee.pw.testowanie1.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.pw.testowanie1.controllers.PostController;
import ee.pw.testowanie1.models.PostCreateDTO;
import ee.pw.testowanie1.services.PostService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PostController.class)
class PostControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PostService postService;

  private final UUID userId = UUID.randomUUID();
  private final String postContent = "some content";

  @Test
  void updatePost_shouldReturnIsOk_whenIdIsProvidedInPath() throws Exception {
    PostCreateDTO postCreateDTO = new PostCreateDTO().builder().build();
    mockMvc.perform(put("/api/posts/{id}", userId).contentType("application/json")
        .content(objectMapper.writeValueAsString(postCreateDTO)))
        .andExpect(status().isOk());
  }
  @Test
  void updatePost_shouldReturnIsNotFound_whenIdIsNotProvidedInPath() throws Exception {
    PostCreateDTO postCreateDTO = new PostCreateDTO().builder().content(postContent).build();
    mockMvc.perform(put("/api/posts/").contentType("application/json")
            .content(objectMapper.writeValueAsString(postCreateDTO)))
        .andExpect(status().isNotFound());
  }


}