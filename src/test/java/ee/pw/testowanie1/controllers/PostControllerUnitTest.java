package ee.pw.testowanie1.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import ee.pw.testowanie1.models.Post;
import ee.pw.testowanie1.models.PostCreateDTO;
import ee.pw.testowanie1.services.PostService;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PostControllerUnitTest {
  private final PostService postService = Mockito.mock(PostService.class);
  private PostController postController;

  @BeforeEach
  void initService() {
    postController = new PostController(postService);
  }

  @Test
  void updatePost_returnedStatusIsOk_whenProvidedCorrectData(){
    //given
    UUID properId = UUID.randomUUID();
    PostCreateDTO properPostCreateDTO = new PostCreateDTO( "content",properId);
    //when
    doNothing().when(postService).updatePost(any(UUID.class), any(PostCreateDTO.class));
    ResponseEntity<Post> result = postController.updatePost(properId, properPostCreateDTO);
    //then
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  @Test
  void updatePost_returnedStatusIsBadRequest_whenProvidedIncorrectData(){
    //given
    UUID correctId = UUID.randomUUID();
    PostCreateDTO incorrectPostCreateDTO = new PostCreateDTO();
    //when
    doThrow(new NoSuchElementException()).when(postService).updatePost(any(UUID.class), any(PostCreateDTO.class));
    ResponseEntity<Post> result = postController.updatePost(correctId, incorrectPostCreateDTO);
    //then
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
  }

}