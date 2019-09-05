package me.puhehe99.portfolioapiserver.posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class PostDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
