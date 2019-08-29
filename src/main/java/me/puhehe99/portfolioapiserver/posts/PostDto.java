package me.puhehe99.portfolioapiserver.posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class PostDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private LocalDateTime createdDateTime;
}
