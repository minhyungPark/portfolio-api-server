package me.puhehe99.portfolioapiserver.common;

import me.puhehe99.portfolioapiserver.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class ErrorsResource extends Resource<Errors> {

    public ErrorsResource(Errors errors, Link... links) {
        super(errors, links);
        add(linkTo(IndexController.class).withRel("index"));
    }
}
