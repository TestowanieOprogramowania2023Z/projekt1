package ee.pw.testowanie1.unit;

import ee.pw.testowanie1.controllers.PostController;
import ee.pw.testowanie1.models.PostDTO;
import ee.pw.testowanie1.services.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Test
    void getAllPosts_CallsServiceFindAllMethod_GivenAnyPageParams() {
        // given
        int anyPage = 0;
        int anySize = 10;

        // when
        postController.getAllPosts(anyPage, anySize);

        // then
        verify(postService).getPosts(PageRequest.of(anyPage, anySize));
    }

    @Test
    void getAllPosts_ShouldReturnOkResponse_WhenGetPostsDoesntThrowError() {
        // given
        int anyPage = 0;
        int anySize = 10;

        // when
        ResponseEntity<List<PostDTO>> response = postController.getAllPosts(anyPage, anySize);

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    void getAllPosts_ShouldReturnBadRequestResponse_WhenProvidingNegativePageNumber() {
        // given
        int page = -1;
        int size = 10;

        // when
        ResponseEntity<List<PostDTO>> response = postController.getAllPosts(page, size);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    void getAllPosts_ShouldReturnBadRequestResponse_WhenProvidingNegativeSize() {
        // given
        int page = 10;
        int size = -1;

        // when
        ResponseEntity<List<PostDTO>> response = postController.getAllPosts(page, size);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    void getPostById_ShouldReturnOkResponse_WhenFoundOptionalPostIsNotEmpty() {
        // given
        UUID id = UUID.randomUUID();
        PostDTO postDTO = PostDTO.builder()
                .id(id)
                .createdAt(new Date())
                .content("content")
                .build();

        when(postService.getPostById(any(UUID.class))).thenReturn(Optional.of(postDTO));

        // when
        ResponseEntity<PostDTO> response = postController.getPostById(id);

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    void getPostById_ShouldReturnCorrectPostDTO_WhenPostWasFound() {
        // given
        UUID id = UUID.randomUUID();
        PostDTO postDTO = PostDTO.builder()
                .id(id)
                .createdAt(new Date())
                .content("content")
                .build();

        when(postService.getPostById(any(UUID.class))).thenReturn(Optional.of(postDTO));

        // when
        ResponseEntity<PostDTO> response = postController.getPostById(id);
        PostDTO actual = response.getBody();

        // then

        assertEquals(postDTO, actual);
    }

    @Test
    void getPostById_ShouldReturnBadRequestResponse_WhenPostWasNotFound() {
        // given
        UUID id = UUID.randomUUID();

        when(postService.getPostById(any(UUID.class))).thenReturn(Optional.empty());

        // when
        ResponseEntity<PostDTO> response = postController.getPostById(id);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    void getPostsByUserId_ShouldCallServiceGetPostsByUserId_GivenAnyIdAndPage() {
        // given
        UUID id = UUID.randomUUID();
        int pageNumber = 0;
        int pageSize = 10;

        // when
        postController.getPostsByUserId(id, pageNumber, pageSize);

        // then
        verify(postService).getPostsByUserId(id, PageRequest.of(pageNumber, pageSize));
    }

    @Test
    void getPostsByUserId_ShouldReturnBadRequestResponse_GivenNegativePageNumber() {
        // given
        int pageNumber = -1;
        int anyPageSize = 10;
        UUID anyUUID = UUID.randomUUID();

        // when
        ResponseEntity<List<PostDTO>> response = postController.getPostsByUserId(anyUUID, pageNumber, anyPageSize);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    void getPostsByUserId_ShouldReturnBadRequestResponse_GivenNegativePageSize() {
        // given
        int pageNumber = -1;
        int anyPageSize = 10;
        UUID anyUUID = UUID.randomUUID();

        // when
        ResponseEntity<List<PostDTO>> response = postController.getPostsByUserId(anyUUID, pageNumber, anyPageSize);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    void getPostsByUserId_ShouldReturnOkResponse_WhenServiceReturnsPosts() {
        // given
        UUID id = UUID.randomUUID();
        PostDTO postDTO = PostDTO.builder()
                .id(id)
                .createdAt(new Date())
                .content("content")
                .build();

        int pageNumber = 0;
        int pageSize = 10;

        when(postService.getPostsByUserId(any(UUID.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(postDTO));

        // when
        ResponseEntity<List<PostDTO>> response = postController.getPostsByUserId(id, pageNumber, pageSize);

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

}