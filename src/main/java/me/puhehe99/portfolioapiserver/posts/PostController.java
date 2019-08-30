package me.puhehe99.portfolioapiserver.posts;


import me.puhehe99.portfolioapiserver.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;

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

}
