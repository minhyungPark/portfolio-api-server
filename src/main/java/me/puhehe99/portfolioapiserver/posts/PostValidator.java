package me.puhehe99.portfolioapiserver.posts;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class PostValidator {

    public void createValidate(Post post, Errors errors) {
        if (post.getCreatedDateTime() != null) {
            errors.reject("notNeedCreatedTime","created time does not need");
        }
    }

}
