package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {
    private final ConcurrentMap<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    public Optional<Post> getById(long id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(posts.get(id));
    }

    public Post save(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }

        if (post.getId() == 0) {
            long newId = idGenerator.getAndIncrement();
            Post newPost = new Post(newId, post.getContent());
            posts.put(newId, newPost);
            return newPost;
        } else {
            Post existingPost = posts.get(post.getId());
            if (existingPost == null) {
                throw new NotFoundException("Post with id " + post.getId() + " not found");
            }

            // Обновляем content
            existingPost.setContent(post.getContent());
            return existingPost;
        }
    }

    public void removeById(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        Post removed = posts.remove(id);
        if (removed == null) {
            throw new NotFoundException("Post with id " + id + " not found");
        }
    }
}
