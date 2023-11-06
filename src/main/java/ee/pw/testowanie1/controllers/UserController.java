package ee.pw.testowanie1.controllers;

import ee.pw.testowanie1.models.User;
import ee.pw.testowanie1.models.UserCreateDTO;
import ee.pw.testowanie1.models.UserDTO;
import ee.pw.testowanie1.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    
    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "5") int size) {
        try {
            return ResponseEntity.ok(userService.getUsers(PageRequest.of(page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/by-username")
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam String username) {
        try {
            return ResponseEntity.ok(userService.getUserByUsername(username).orElseThrow());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@RequestParam String id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id).orElseThrow());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody @Valid UserCreateDTO user) {
        try {
            return ResponseEntity.created(new URI(userService.createUser(user).toString())).build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
