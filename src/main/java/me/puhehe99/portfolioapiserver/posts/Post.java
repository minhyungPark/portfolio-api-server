package me.puhehe99.portfolioapiserver.posts;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder @EqualsAndHashCode(of = "id")
@Entity
public class Post {

    @Id @GeneratedValue
    private Integer id;
    private String title;
    private String body;
    private LocalDateTime createdDateTime;

}
