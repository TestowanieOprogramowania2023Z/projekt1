package ee.pw.testowanie1.unit;

import ee.pw.testowanie1.models.User;
import ee.pw.testowanie1.models.UserCreateDTO;
import ee.pw.testowanie1.models.UserDTO;
import ee.pw.testowanie1.repositories.UserRepository;
import ee.pw.testowanie1.services.UserService;
import net.bytebuddy.asm.MemberSubstitution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void deleteUser_AnyValidUUID_ShouldCallRepositoryFunction() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        userService.deleteUser(id);

        // Assert
        verify(userRepository).deleteById(id);
    }
    
    @Test
    void deleteUser_NullUUID_ShouldThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(null);
        });
    }

    @Test
    void getUser_ValidPageableParameters_ShouldCorrectlyReturnUserList() {
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

        when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

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

    @Test
    void getUsers_EmptyResult_ShouldReturnEmptyResult() {

        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        List<UserDTO> result = userService.getUsers(pageable);

        assertEquals(0, result.size());
    }

    @Test
    void getUsers_PageRequestOfIntMaxValue_ShouldReturnCorrectResult() {

        Pageable pageable = PageRequest.of(Integer.MAX_VALUE, 1);

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        List<UserDTO> result = userService.getUsers(pageable);

        assertEquals(0, result.size());
    }
    
    @Test
    void getUserById_AnyValidUUID_ShouldCallRepositoryFunction() {
        UUID value = UUID.randomUUID();
        userService.getUserById(value.toString());
        ArgumentCaptor<UUID> argument = ArgumentCaptor.forClass(UUID.class);
        verify(userRepository).findById(argument.capture());
        
        assertEquals(value, argument.getValue());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid"})
    void getUserById_InvalidUUIDString_ShouldThrowException(String value) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(value);
        });
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "valid"})
    void getUserByUsername_AnyValidString_ShouldCallRepositoryFunction(String value) {
        userService.getUserByUsername(value);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByUsername(argument.capture());
        
        assertEquals(value, argument.getValue());
    }
    
    @Test
    void getUserByUsername_NullString_ShouldThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserByUsername(null);
        });
    }
    
    @Test
    void createUser_UserCreateDTOIsNull_ShouldThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(null);
        });
    }
    
    @Test
    void createUser_ValidUserDTO_ShouldCorrectlyCallRepoFunction() {
        UserCreateDTO user = UserCreateDTO.builder()
                .username("user")
                .email("user@gmail")
                .build();
        
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(User.builder()
                .id(UUID.randomUUID())
                .username(user.getUsername())
                .email(user.getEmail())
                .build());
        
        userService.createUser(user);

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argument.capture());

        assertEquals(user.getUsername(), argument.getValue().getUsername());
        assertEquals(user.getEmail(), argument.getValue().getEmail());
    }
    
    @Test
    void createUser_UserWithTheSameUsernameExists_ShouldThrowException() {
        UserCreateDTO user = UserCreateDTO.builder()
                .username("user")
                .email("user@gmail")
                .build();
        
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(User.builder()
                .id(UUID.randomUUID())
                .username(user.getUsername())
                .email(user.getEmail())
                .build()));
        
        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.createUser(user);
        });
    }
    
    @Test
    void createUser_UserWithTheSameEmailExists_ShouldThrowException() {
        UserCreateDTO user = UserCreateDTO.builder()
                .username("user")
                .email("user@gmail")
                .build();
        
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(User.builder()
                .id(UUID.randomUUID())
                .username(user.getUsername())
                .email(user.getEmail())
                .build()));
        
        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.createUser(user);
        });
    }

}
