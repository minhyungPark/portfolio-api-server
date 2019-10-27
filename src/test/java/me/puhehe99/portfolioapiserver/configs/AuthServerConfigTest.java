package me.puhehe99.portfolioapiserver.configs;


import me.puhehe99.portfolioapiserver.accounts.Account;
import me.puhehe99.portfolioapiserver.accounts.AccountRole;
import me.puhehe99.portfolioapiserver.accounts.AccountService;
import me.puhehe99.portfolioapiserver.common.BaseControllerTest;
import me.puhehe99.portfolioapiserver.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급받는 테스트")
    public void getAuthToken() throws Exception {

        String password = "1234";
        String email = "min@email.com";
        Account testUser = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRole.USER))
                .build();

        this.accountService.saveAccount(testUser);

        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic("myApp","pass"))
                .param("username",email)
                .param("password",password)
                .param("grant_type","password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

    }


}