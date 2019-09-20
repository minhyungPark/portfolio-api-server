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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .problemUrl("https://acmicpc.net")
                .imgUrl("https://picsum.photos/200/300")
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
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-portfolio",
                        links(
                                linkWithRel("self").description("link to description"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        requestFields(
                                fieldWithPath("title").description("title of portfolio"),
                                fieldWithPath("content").description("content of portfolio"),
                                fieldWithPath("imgUrl").description("image url of portfolio"),
                                fieldWithPath("algoSite").description("site of algorithm problem"),
                                fieldWithPath("sourceCode").description("source code of portfolio"),
                                fieldWithPath("language").description("language of source code"),
                                fieldWithPath("problemUrl").description("url of problem"),
                                fieldWithPath("codeStyle").description("style of code theme")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of portfolio"),
                                fieldWithPath("title").description("title of portfolio"),
                                fieldWithPath("content").description("content of portfolio"),
                                fieldWithPath("imgUrl").description("content of portfolio"),
                                fieldWithPath("algoSite").description("site of algorithm problem"),
                                fieldWithPath("sourceCode").description("source code of portfolio"),
                                fieldWithPath("language").description("language of source code"),
                                fieldWithPath("problemUrl").description("url of problem"),
                                fieldWithPath("codeStyle").description("style of code theme"),
                                fieldWithPath("createdDateTime").description("created time of portfolio"),
                                fieldWithPath("modifiedDateTime").description("modified time of portfolio"),
                                fieldWithPath("_links.self.href").description("link of self"),
                                fieldWithPath("_links.profile.href").description("link of profile")
                        )
                ))
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
                .problemUrl("https://acmicpc.net")
                .imgUrl("https://picsum.photos/200/300")
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
                .problemUrl("https://acmicpc.net")
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
        IntStream.range(0,30).forEach(this::savePortfolio);

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
                .andDo(document("get-portfolios",
                            requestParameters(
                                    parameterWithName("page").description("The page to retrieve"),
                                    parameterWithName("size").description("Entries per page"),
                                    parameterWithName("sort").description("by which to sort , sort method")
                            ),
                            responseHeaders(
                                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                            ),
                            links(
                                    linkWithRel("first").description("link to first page"),
                                    linkWithRel("prev").description("link to prev page"),
                                    linkWithRel("self").description("link to self"),
                                    linkWithRel("next").description("link to next page"),
                                    linkWithRel("last").description("link to last page"),
                                    linkWithRel("profile").description("link to profile")
                            )
                        )
                )
                ;
    }

    private Portfolio savePortfolio(int index) {
        Portfolio portfolio = Portfolio.builder()
                .title("portfolio title" + index)
                .content("<p>content</p>" + index)
                .algoSite(AlgoSite.BAEKJOON)
                .sourceCode("int a=0;")
                .language("java")
                .problemUrl("https://localhost:8080")
                .imgUrl("https://picsum.photos/200/300")
                .codeStyle("default")
                .createdDateTime(LocalDateTime.now())
                .build();
        return this.portfolioRepository.save(portfolio);
    }

    @Test
    @TestDescription("portfolio 하나 조회")
    public void getPortfolio() throws Exception {
        Portfolio portfolio = this.savePortfolio(1);

        this.mockMvc.perform(get("/api/portfolios/{id}",portfolio.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-portfolio"))
        ;
    }

    @Test
    @TestDescription("없는 portfolio 요청시 Not Found")
    public void getPortfolioNotFound() throws Exception {

        this.mockMvc.perform(get("/api/portfolios/{id}",123123))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("기존의 portfolio 정상적으로 수정")
    public void updatePortfolio() throws Exception {
        // Given
        Portfolio savePortfolio = this.savePortfolio(1);

        PortfolioDto portfolioDto = PortfolioDto.builder()
                .title("Modified title")
                .content("<p>modified content</p>")
                .algoSite(AlgoSite.BAEKJOON)
                .sourceCode("int a=0; int b=0;")
                .language("java")
                .problemUrl("https://localhost:8080")
                .imgUrl("https://picsum.photos/200/300")
                .codeStyle("default")
                .build();

        this.mockMvc.perform(put("/api/portfolios/{id}",savePortfolio.getId())
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                                .content(objectMapper.writeValueAsString(portfolioDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("createdDateTime").exists())
                .andExpect(jsonPath("modifiedDateTime").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-portfolio"))
        ;

    }
}