package ee.pw.testowanie1;

import ee.pw.testowanie1.repositories.PostRepository;
import ee.pw.testowanie1.services.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Test
    public void CheckIfServiceCallsRepositoryDeleteMethodTest() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        postService.deletePost(id);

        // Assert
        verify(postRepository).deleteById(id);
    }
}
