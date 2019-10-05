package me.puhehe99.portfolioapiserver.portfolios;

import lombok.*;
import me.puhehe99.portfolioapiserver.accounts.Account;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder @EqualsAndHashCode(of = "id")
@Entity
public class Portfolio {

    @Id @GeneratedValue
    private Integer id;

    private String title;

    private String content;

    private String imgUrl;

    private String codeStyle;

    private String sourceCode;

    private String language;

    @Enumerated(EnumType.STRING)
    private AlgoSite algoSite;

    private String problemUrl;

    private LocalDateTime createdDateTime;

    private LocalDateTime modifiedDateTime;

    @ManyToOne
    private Account manager;
}
