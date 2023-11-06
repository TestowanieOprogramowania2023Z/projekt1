package ee.pw.testowanie1.services;

import ee.pw.testowanie1.models.User;
import ee.pw.testowanie1.models.UserCreateDTO;
import ee.pw.testowanie1.models.UserDTO;
import ee.pw.testowanie1.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDTO::fromUser).stream().toList();
    }

    public Optional<UserDTO> getUserByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");
        return userRepository.findByUsername(username).map(UserDTO::fromUser);
    }

    public Optional<UserDTO> getUserById(String id) {
        return userRepository.findById(UUID.fromString(id)).map(UserDTO::fromUser);
    }

    public UUID createUser(UserCreateDTO user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        
        var userWithTheSameUsername = userRepository.findByUsername(user.getUsername());
        if (userWithTheSameUsername.isPresent()) {
            throw new RuntimeException("User with this username already exists");
        }

        var userWithTheSameEmail = userRepository.findByEmail(user.getEmail());
        if (userWithTheSameEmail.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        return userRepository.save(User.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build()).getId();
    }

    public void deleteUser(UUID id) {
        if (id == null) throw new IllegalArgumentException("Id cannot be null");
        userRepository.deleteById(id);
    }
}
