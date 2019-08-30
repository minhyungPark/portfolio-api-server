package me.puhehe99.portfolioapiserver.posts;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class PostResource extends Resource<Post> {

    public PostResource(Post post, Link... links) {
        super(post, links);
        add(linkTo(PostController.class).slash(post.getId()).withSelfRel());
    }
}
