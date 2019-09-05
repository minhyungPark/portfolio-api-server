package me.puhehe99.portfolioapiserver.posts;


import me.puhehe99.portfolioapiserver.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/posts",produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class PostController {

    private final PostRepository postRepository;

    private final PostValidator postValidator;

    private final ModelMapper modelMapper;

    public PostController(PostRepository postRepository, PostValidator postValidator, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.postValidator = postValidator;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity createPost(@RequestBody @Valid PostDto postDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }
        Post post = modelMapper.map(postDto, Post.class);
        postValidator.createValidate(post,errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        post.setCreatedDateTime(LocalDateTime.now());
        Post savedPost = this.postRepository.save(post);
        ControllerLinkBuilder linkBuilder = linkTo(PostController.class).slash(savedPost.getId());
        URI uri = linkBuilder.toUri();
        PostResource postResource = new PostResource(savedPost);
        postResource.add(linkTo(PostController.class).withRel("get-posts"));
        postResource.add(linkBuilder.withRel("update-post"));
        postResource.add(new Link("/docs/index.html#resources-posts-create").withRel("profile"));
        return ResponseEntity.created(uri).body(postResource);
    }

    @GetMapping
    public ResponseEntity getPosts(Pageable pageable, PagedResourcesAssembler<Post> assembler) {
        Page<Post> postPage = this.postRepository.findAll(pageable);
        PagedResources<PostResource> postResources = assembler.toResource(postPage, entity -> new PostResource(entity));
        postResources.add(new Link("/docs/index.html#resources-posts-list").withRel("profile"));
        return ResponseEntity.ok(postResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPost(@PathVariable Integer id) {
        Optional<Post> optionalPost = this.postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Post post = optionalPost.get();
        PostResource postResource = new PostResource(post);
        postResource.add(new Link("/docs/index.html#resources-posts-get").withRel("profile"));
        return ResponseEntity.ok(postResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePost(@PathVariable Integer id, @RequestBody PostDto postDto, Errors errors) {

        Optional<Post> optionalPost = this.postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Post post = this.modelMapper.map(postDto, Post.class);
        post.setId(id);
        post.setModifiedDateTime(LocalDateTime.now());
        post.setCreatedDateTime(optionalPost.get().getCreatedDateTime());
        Post updatedPost = this.postRepository.save(post);
        PostResource postResource = new PostResource(updatedPost);
        postResource.add(new Link("docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(postResource);
    }


}
