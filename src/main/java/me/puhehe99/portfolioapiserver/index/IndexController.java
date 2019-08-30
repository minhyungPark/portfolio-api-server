package me.puhehe99.portfolioapiserver.index;

import me.puhehe99.portfolioapiserver.posts.PostController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class IndexController {

    @GetMapping("/api")
    public ResourceSupport index() {
        ResourceSupport resourceSupport = new ResourceSupport();
        resourceSupport.add(linkTo(PostController.class).withRel("posts"));
        return resourceSupport;
    }
}
