package me.puhehe99.portfolioapiserver.portfolios;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio,Integer> {
}
