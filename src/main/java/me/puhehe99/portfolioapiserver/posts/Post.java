package me.puhehe99.portfolioapiserver.posts;

import lombok.*;
import me.puhehe99.portfolioapiserver.accounts.Account;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder @EqualsAndHashCode(of = "id")
@Entity
public class Post {

    @Id @GeneratedValue
    private Integer id;

    private String title;

    private String content;

    private LocalDateTime createdDateTime;

    private LocalDateTime modifiedDateTime;

    @ManyToOne
    private Account manager;
}
