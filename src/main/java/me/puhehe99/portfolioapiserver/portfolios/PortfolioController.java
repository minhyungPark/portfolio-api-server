package me.puhehe99.portfolioapiserver.portfolios;

import me.puhehe99.portfolioapiserver.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/portfolios")
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
        portfolioResource.add(new Link("/docs/index.html#resources-portfolios-create").withRel("profile"));
        return ResponseEntity.created(uri).body(portfolioResource);
    }

    @GetMapping
    public ResponseEntity getPortfolios(Pageable pageable, PagedResourcesAssembler<Portfolio> assembler) {
        Page<Portfolio> portfolioPage = this.portfolioRepository.findAll(pageable);
        PagedResources pagedResources = assembler.toResource(portfolioPage, entity -> new PortfolioResource(entity));
        pagedResources.add(new Link("/docs/index.html#resources-portfolios-list").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPortfolio(@PathVariable Integer id) {
        Optional<Portfolio> optionalPortfolio = this.portfolioRepository.findById(id);
        if (optionalPortfolio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Portfolio portfolio = optionalPortfolio.get();
        PortfolioResource portfolioResource = new PortfolioResource(portfolio);
        portfolioResource.add(new Link("/docs/index.html#resources-portfolios-get").withRel("profile"));

        return ResponseEntity.ok(portfolioResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePortfolio(@PathVariable Integer id,
                                          @RequestBody @Valid PortfolioDto portfolioDto,
                                          Errors errors) {
        Optional<Portfolio> optionalPortfolio = this.portfolioRepository.findById(id);
        if (optionalPortfolio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        Portfolio existingPortfolio = optionalPortfolio.get();
        this.modelMapper.map(portfolioDto, existingPortfolio);
        existingPortfolio.setModifiedDateTime(LocalDateTime.now());
        Portfolio savedPortfolio = this.portfolioRepository.save(existingPortfolio);

        PortfolioResource portfolioResource = new PortfolioResource(savedPortfolio);
        portfolioResource.add(new Link("/docs/index.html#resources-portfolios-update").withRel("profile"));
        return ResponseEntity.ok(portfolioResource);
    }

}
