package me.puhehe99.portfolioapiserver.portfolios;

import me.puhehe99.portfolioapiserver.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/portfolio")
public class PortfolioController {

    private PortfolioRepository portfolioRepository;

    private ModelMapper modelMapper;

    public PortfolioController(PortfolioRepository portfolioRepository, ModelMapper modelMapper) {
        this.portfolioRepository = portfolioRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity createPortfolio(@RequestBody @Valid PortfolioDto portfolioDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        Portfolio portfolio = modelMapper.map(portfolioDto, Portfolio.class);
        portfolio.setCreatedDateTime(LocalDateTime.now());
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        URI uri = linkTo(PortfolioController.class).slash(savedPortfolio.getId()).toUri();
        PortfolioResource portfolioResource = new PortfolioResource(savedPortfolio);
        return ResponseEntity.created(uri).body(portfolioResource);
    }
}
