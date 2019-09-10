package me.puhehe99.portfolioapiserver.portfolios;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.time.LocalDateTime;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/portfolio")
public class PortfolioController {

    PortfolioRepository portfolioRepository;

    public PortfolioController(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @PostMapping
    public ResponseEntity createPortfolio(@RequestBody Portfolio portfolio) {
        portfolio.setCreatedDateTime(LocalDateTime.now());
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        URI uri = linkTo(PortfolioController.class).slash(savedPortfolio.getId()).toUri();
        PortfolioResource portfolioResource = new PortfolioResource(savedPortfolio);
        return ResponseEntity.created(uri).body(portfolioResource);
    }
}
