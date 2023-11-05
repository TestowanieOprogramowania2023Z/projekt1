package ee.pw.testowanie1;

import ee.pw.testowanie1.repositories.UserRepository;
import ee.pw.testowanie1.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void shouldCallRepositoryDeleteMethod() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        userService.deleteUser(id);

        // Assert
        verify(userRepository).deleteById(id);
    }
}
