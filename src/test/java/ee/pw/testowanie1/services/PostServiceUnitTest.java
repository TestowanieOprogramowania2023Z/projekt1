package ee.pw.testowanie1.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ee.pw.testowanie1.models.Post;
import ee.pw.testowanie1.models.PostCreateDTO;
import ee.pw.testowanie1.repositories.PostRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class PostServiceUnitTest {

  private final PostRepository postRepository = Mockito.mock(PostRepository.class);
  private PostService postService;

  @BeforeEach
  void initService() {
    postService = new PostService(postRepository);
  }

  //unit test
  @Test
  void updatePost_contentIsProperlyUpdated_whenPostExistsInDB() {
    //given
    UUID postID = UUID.randomUUID();
    String postContentBeforeUpdate = "Content before update";
    String postContentAfterUpdate = "Updated content";
    PostCreateDTO postCreateDTO = new PostCreateDTO()
        .builder()
        .content(postContentAfterUpdate)
        .userId(postID)
        .build();
    Post post = new Post()
        .builder()
        .id(postID)
        .content(postContentBeforeUpdate)
        .build();

    //when
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));
    postService.updatePost(postID, postCreateDTO);

    //then
    ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);

    verify(postRepository).save(postArgumentCaptor.capture());

    Post capturedPost = postArgumentCaptor.getValue();

    assertThat(capturedPost.getContent()).isEqualTo(postContentAfterUpdate);
  }

  @Test
  void updatePost_throwsCorrectException_whenPostDoesNotExistsInDB() {
    //given
    UUID postID = UUID.randomUUID();
    PostCreateDTO postCreateDTO = new PostCreateDTO();

    //when
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    //then
    Assertions.assertThrows(NoSuchElementException.class,
        () -> {
          postService.updatePost(postID, postCreateDTO);
        });
  }

  @Test
  void updatePost_doesntCallSaveInDB_whenPostDoesNotExistsInDB() {
    //given
    UUID postID = UUID.randomUUID();
    PostCreateDTO postCreateDTO = new PostCreateDTO();
    //when
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    try {
      postService.updatePost(postID, postCreateDTO);
    } catch (NoSuchElementException ex) {
    }
    //then
    verify(postRepository, never()).save(any());
  }
}