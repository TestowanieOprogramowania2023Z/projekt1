package ee.pw.testowanie1.controllers;

import ee.pw.testowanie1.models.Post;
import ee.pw.testowanie1.models.PostDTO;
import ee.pw.testowanie1.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam int page, @RequestParam int size) {
        try {
            return ResponseEntity.ok(postService.getPosts(PageRequest.of(page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(postService.getPostById(id).orElseThrow());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable UUID userId, @RequestParam int page, @RequestParam int size) {
        try {
            return ResponseEntity.ok(postService.getPostsByUserId(userId, PageRequest.of(page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<Post> createPost(@RequestBody PostDTO post) {
        try {
            return ResponseEntity.created(new URI(postService.createPost(post).toString())).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable UUID id, @RequestBody PostDTO post) {
        try {
            postService.updatePost(id, post);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable UUID id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
