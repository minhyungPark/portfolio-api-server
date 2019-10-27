package me.puhehe99.portfolioapiserver.configs;

import me.puhehe99.portfolioapiserver.accounts.Account;
import me.puhehe99.portfolioapiserver.accounts.AccountRole;
import me.puhehe99.portfolioapiserver.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {

                Account admin = Account.builder()
                        .email("admin@admin.com")
                        .password("123123")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                accountService.saveAccount(admin);

                Account user = Account.builder()
                        .email("user@email.com")
                        .password("1212")
                        .roles(Set.of(AccountRole.USER))
                        .build();
                accountService.saveAccount(user);

            }
        };
    }
}
