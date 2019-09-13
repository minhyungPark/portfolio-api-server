package me.puhehe99.portfolioapiserver.portfolios;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.puhehe99.portfolioapiserver.common.RestDocsConfiguration;
import me.puhehe99.portfolioapiserver.common.TestDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class PortfolioControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PortfolioRepository portfolioRepository;

    @Test
    @TestDescription("portfolio 를 정상적으로 생성")
    public void createPortfolio() throws Exception{
        PortfolioDto portfolio = PortfolioDto.builder()
                .title("portfolio title")
                .content("<p>content</p>")
                .algoSite(AlgoSite.BAEKJOON)
                .sourceCode("int a=0;")
                .language("java")
                .problemUrl("https://localhost:8080")
                .imgUrl("https://0gichul.com/files/attach/images/204/125/877/003/79160512de6dcb7eab93212a13d56fad.jpg")
                .codeStyle("default")
                .build();

        this.mockMvc.perform(post("/api/portfolios")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(portfolio)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("content").exists())
                .andExpect(jsonPath("algoSite").exists())
                .andExpect(jsonPath("sourceCode").exists())
                .andExpect(jsonPath("language").exists())
                .andExpect(jsonPath("problemUrl").exists())
                .andExpect(jsonPath("codeStyle").exists())
                .andExpect(jsonPath("imgUrl").exists())
                .andExpect(jsonPath("createdDateTime").exists())
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    @TestDescription("portfolio 생성시 입력값 제한")
    public void createPortfolioWithBadRequest() throws Exception{
        Portfolio portfolio = Portfolio.builder()
                .id(100)
                .title("portfolio title")
                .content("<p>content</p>")
                .algoSite(AlgoSite.BAEKJOON)
                .sourceCode("int a=0;")
                .language("java")
                .problemUrl("https://localhost:8080")
                .imgUrl("https://0gichul.com/files/attach/images/204/125/877/003/79160512de6dcb7eab93212a13d56fad.jpg")
                .codeStyle("default")
                .createdDateTime(LocalDateTime.now())
                .build();

        this.mockMvc.perform(post("/api/portfolios")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(portfolio)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @TestDescription("생성시 필수 값이 빠져있는 경우 BadRequest")
    public void createPortfolio_Empty_Input_with_BadRequest() throws Exception{
        PortfolioDto portfolio = PortfolioDto.builder()
                .title("")
                .content("<p>content</p>")
                .algoSite(AlgoSite.BAEKJOON)
                .sourceCode("int a=0;")
                .language("java")
                .problemUrl("https://localhost:8080")
                .imgUrl("https://0gichul.com/files/attach/images/204/125/877/003/79160512de6dcb7eab93212a13d56fad.jpg")
                .codeStyle("default")
                .build();

        this.mockMvc.perform(post("/api/portfolios")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(portfolio)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("portfolio 리스트를 불러오기")
    public void getPortfolios() throws Exception {
        // Given
        IntStream.range(0,30).forEach(this::generatePortfolio);

        // When & Then
        this.mockMvc.perform(get("/api/portfolios")
                    .param("page","1")
                    .param("size","10")
                    .param("sort","title,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.portfolioList").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.self").exists())
                ;
    }

    private void generatePortfolio(int index) {
        Portfolio portfolio = Portfolio.builder()
                .title("portfolio title" + index)
                .content("<p>content</p>" + index)
                .algoSite(AlgoSite.BAEKJOON)
                .sourceCode("int a=0;")
                .language("java")
                .problemUrl("https://localhost:8080")
                .imgUrl("https://0gichul.com/files/attach/images/204/125/877/003/79160512de6dcb7eab93212a13d56fad.jpg")
                .codeStyle("default")
                .createdDateTime(LocalDateTime.now())
                .build();
        this.portfolioRepository.save(portfolio);
    }


}