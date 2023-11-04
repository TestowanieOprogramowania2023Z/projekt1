package ee.pw.testowanie1.services;

import ee.pw.testowanie1.models.User;
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

    public List<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(UUID.fromString(id));
    }

    public UUID createUser(UserDTO user) {
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
        userRepository.deleteById(id);
    }
}
