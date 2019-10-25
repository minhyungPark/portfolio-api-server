package me.puhehe99.portfolioapiserver.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void findByUsername() {

        // Given
        String email = "puhehe99@email.com";
        String password = "puhehe99";
        Account puhehe99 = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        accountRepository.save(puhehe99);

        //When
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(userDetails.getPassword()).isEqualTo(puhehe99.getPassword());
    }

    @Test
    public void findByUsernameFail() {
        String randomUser = "random@email.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(randomUser));

        UserDetailsService userDetailsService = accountService;
        userDetailsService.loadUserByUsername(randomUser);
    }
}