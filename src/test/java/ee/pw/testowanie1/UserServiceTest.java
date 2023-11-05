package ee.pw.testowanie1;

import ee.pw.testowanie1.models.User;
import ee.pw.testowanie1.models.UserDTO;
import ee.pw.testowanie1.repositories.UserRepository;
import ee.pw.testowanie1.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void testGetUsers() {
        // Create some sample data
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        List<User> users = List.of(
                User.builder()
                        .id(id1)
                        .username("user1")
                        .email("user1@gmail")
                        .build(),
                User.builder()
                        .id(id2)
                        .username("user2")
                        .email("user2@gmail")
                        .build(),
                User.builder()
                        .id(id3)
                        .username("user3")
                        .email("user3@gmail")
                        .build()
        );

        Page<User> page = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        List<UserDTO> userDTOs = userService.getUsers(PageRequest.of(0, 10));

        assertEquals(users.size(), userDTOs.size());

        for (int i = 0; i < users.size(); i++) {
            User expectedUser = users.get(i);
            UserDTO actualUser = userDTOs.get(i);

            assertEquals(expectedUser.getId(), actualUser.getId());
            assertEquals(expectedUser.getUsername(), actualUser.getUsername());
            assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        }
    }
}
