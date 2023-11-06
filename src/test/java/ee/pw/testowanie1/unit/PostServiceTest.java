package ee.pw.testowanie1.unit;

import ee.pw.testowanie1.models.Post;
import ee.pw.testowanie1.models.PostCreateDTO;
import ee.pw.testowanie1.models.PostDTO;
import ee.pw.testowanie1.models.User;
import ee.pw.testowanie1.repositories.PostRepository;
import ee.pw.testowanie1.services.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void getPosts_CallsRepoFindAllMethod_GivenAnyPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(postRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        // when
        postService.getPosts(pageable);

        // then
        verify(postRepository).findAll(pageable);
    }

    @Test
    void getPosts_ReturnsCorrectPostDTOList_WhenThereArePostsInDB() {
        // given
        Post post1 = Post.builder().id(UUID.randomUUID()).user(User.builder().id(UUID.randomUUID()).build()).content("Content1").createdAt(new Date()).build();
        Post post2 = Post.builder().id(UUID.randomUUID()).user(User.builder().id(UUID.randomUUID()).build()).content("Content2").createdAt(new Date()).build();
        Post post3 = Post.builder().id(UUID.randomUUID()).user(User.builder().id(UUID.randomUUID()).build()).content("Content3").createdAt(new Date()).build();
        List<Post> postsInDB = Arrays.asList(post1, post2, post3);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(postsInDB));
        Pageable pageable = PageRequest.of(0, postsInDB.size());

        // when
        List<PostDTO> actual = postService.getPosts(pageable);

        // then
        List<PostDTO> expected = postsInDB.stream().map(PostDTO::fromPost).toList();
        assertIterableEquals(expected, actual);
    }

    @Test
    void getPostById_CallsRepoFindByIdMethod_GivenRandomId() {
        // given
        UUID id = UUID.randomUUID();
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Post()));

        // when
        postService.getPostById(id);

        // then
        verify(postRepository).findById(id);
    }

    @Test
    void getPostById_ReturnsEmptyOptional_WhenPostIsNotPresentInDB() {
        // given
        UUID id = UUID.randomUUID();
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // when
        Optional<PostDTO> post = postService.getPostById(id);

        // then
        assertEquals(Optional.empty(), post);
    }

    @Test
    void getPostById_ReturnsOptionalWithPost_WhenPostIsInDB() {
        // given
        UUID id = UUID.randomUUID();
        Post postInDB = Post.builder().id(id).user(User.builder().id(UUID.randomUUID()).build()).content("Content1").createdAt(new Date()).build();
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(postInDB));

        // when
        Optional<PostDTO> actual = postService.getPostById(id);

        // then
        Optional<PostDTO> expected = Optional.of(PostDTO.fromPost(postInDB));
        assertEquals(expected, actual);
    }

    @Test
    void getPostsByUserId_CallsRepoFindAllByUserId_GivenRandomPageAndRandomUserId() {
        // given
        UUID randomId = UUID.randomUUID();
        Pageable randomPage = PageRequest.of(0, 10);

        when(postRepository.findAllByUserId(randomId, randomPage)).thenReturn(Collections.emptyList());

        // when
        postService.getPostsByUserId(randomId, randomPage);

        // then
        verify(postRepository).findAllByUserId(randomId, randomPage);
    }

    @Test
    void getPostsByUserId_ReturnsCorrectListOfPostDTOWithProvidedUserId_GivenUserIdAndPostsInDB() {
        // given
        UUID wantedUserId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();

        Post post1 = Post.builder().id(UUID.randomUUID()).user(User.builder().id(wantedUserId).build()).content("Content1").createdAt(new Date()).build();
        Post post2 = Post.builder().id(UUID.randomUUID()).user(User.builder().id(anotherUserId).build()).content("Content2").createdAt(new Date()).build();
        Post post3 = Post.builder().id(UUID.randomUUID()).user(User.builder().id(wantedUserId).build()).content("Content3").createdAt(new Date()).build();
        List<Post> postsInDB = Arrays.asList(post1, post2, post3);

        List<Post> wantedPosts = postsInDB.stream().filter(post -> post.getUser().getId().equals(wantedUserId)).toList();

        when(postRepository.findAllByUserId(any(UUID.class), any(Pageable.class))).thenReturn(wantedPosts);
        Pageable pageable = PageRequest.of(0, postsInDB.size());

        // when
        List<PostDTO> actual = postService.getPostsByUserId(wantedUserId, pageable);

        // then
        List<PostDTO> expected = wantedPosts.stream().map(PostDTO::fromPost).toList();
        assertIterableEquals(expected, actual);
    }

    @Test
    public void shouldCallRepositoryDeleteMethod() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        postService.deletePost(id);

        // Assert
        verify(postRepository).deleteById(id);
    }


    //unit test
    @Test
    void updatePost_contentIsProperlyUpdated_whenPostExistsInDB() {
        //given
        UUID postID = UUID.randomUUID();
        String postContentBeforeUpdate = "Content before update";
        String postContentAfterUpdate = "Updated content";
        PostCreateDTO postCreateDTO = PostCreateDTO
                .builder()
                .content(postContentAfterUpdate)
                .userId(postID)
                .build();
        Post post = Post
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
                () -> postService.updatePost(postID, postCreateDTO));
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
        } catch (NoSuchElementException ignored) {
        }
        //then
        verify(postRepository, never()).save(any());
    }


}