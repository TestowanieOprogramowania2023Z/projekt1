package ee.pw.testowanie1;

import ee.pw.testowanie1.controllers.PostController;
import ee.pw.testowanie1.services.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@SpringBootTest
public class PostControllerTest {

    @InjectMocks
    private PostController postController;
    @Mock
    private PostService postService;

    @Test
    public void CheckIfPostControllerCallsServiceDeleteMethodTest() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        postController.deletePost(id);

        // Assert
        verify(postService).deletePost(id);
    }

}
