package ee.pw.testowanie1.services;

import ee.pw.testowanie1.models.Post;
import ee.pw.testowanie1.models.PostCreateDTO;
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


    public List<PostDTO> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostDTO::fromPost).stream().toList();
    }

    public Optional<PostDTO> getPostById(UUID id) {
        if (id == null) throw new IllegalArgumentException("Id cannot be null");
        return postRepository.findById(id).map(PostDTO::fromPost);
    }

    public UUID createPost(PostCreateDTO post) {
        if (post == null) throw new IllegalArgumentException("Post cannot be null");
        return postRepository.save(Post.builder()
                .content(post.getContent())
                .user(User.builder().id(post.getUserId()).build())
                .createdAt(new java.util.Date())
                .build()).getId();
    }

    public void updatePost(UUID id, PostCreateDTO post) {
        if (id == null) throw new IllegalArgumentException("Id cannot be null");
        if (post == null) throw new IllegalArgumentException("Post cannot be null");
        var postInDB = postRepository.findById(id).orElseThrow();
        postInDB.setContent(post.getContent());
        postRepository.save(postInDB);
    }

    public void deletePost(UUID id) {
        if (id == null) throw new IllegalArgumentException("Id cannot be null");
        postRepository.deleteById(id);
    }

    public List<PostDTO> getPostsByUserId(UUID userId, Pageable pageable) {
        if (userId == null) throw new IllegalArgumentException("Id cannot be null");
        return postRepository.findAllByUserId(userId, pageable).stream().map(PostDTO::fromPost).toList();
    }
}
