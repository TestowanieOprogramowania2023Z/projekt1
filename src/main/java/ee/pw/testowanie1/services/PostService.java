package ee.pw.testowanie1.services;

import ee.pw.testowanie1.models.Post;
import ee.pw.testowanie1.models.PostDTO;
import ee.pw.testowanie1.models.User;
import ee.pw.testowanie1.repositories.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    public List<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable).getContent();
    }

    public Optional<Post> getPostById(UUID id) {
        return postRepository.findById(id);
    }

    public UUID createPost(PostDTO post) {
        return postRepository.save(Post.builder()
                .content(post.getContent())
                .user(User.builder().id(post.getUserId()).build())
                .build()).getId();
    }

    public void updatePost(UUID id, PostDTO post) {
        var postInDB = postRepository.findById(id).orElseThrow();
        postInDB.setContent(post.getContent());
        postRepository.save(postInDB);
    }

    public void deletePost(UUID id) {
        postRepository.deleteById(id);
    }

    public List<Post> getPostsByUserId(UUID userId, Pageable pageable) {
        return postRepository.findAllByUserId(userId, pageable);
    }
}
