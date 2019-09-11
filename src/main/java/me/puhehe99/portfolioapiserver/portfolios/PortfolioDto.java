package me.puhehe99.portfolioapiserver.portfolios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PortfolioDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String imgUrl;

    private String codeStyle;

    private String sourceCode;

    private String language;

    @Enumerated(EnumType.STRING)
    private AlgoSite algoSite;

    private String problemUrl;
}
