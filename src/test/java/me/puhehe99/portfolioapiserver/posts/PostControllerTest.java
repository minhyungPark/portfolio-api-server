package me.puhehe99.portfolioapiserver.posts;

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

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("post를 정상적으로 생성하는 테스트")
    public void createPost() throws Exception {
        PostDto post = PostDto.builder()
                .title("test title")
                .content("<p>test content</p>")
                .build();

        this.mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                        .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").value(post.getTitle()))
                .andExpect(jsonPath("content").value(post.getContent()))
                .andExpect(jsonPath("createdDateTime").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-posts").exists())
                .andExpect(jsonPath("_links.update-post").exists())
                .andDo(document("create-post",
                        links(
                                linkWithRel("self").description("link to description"),
                                linkWithRel("get-posts").description("link to get posts"),
                                linkWithRel("update-post").description("link to update post"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        requestFields(
                                fieldWithPath("title").description("title of post"),
                                fieldWithPath("content").description("content of post")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of post"),
                                fieldWithPath("title").description("title of post"),
                                fieldWithPath("content").description("content of post"),
                                fieldWithPath("createdDateTime").description("created time of post"),
                                fieldWithPath("_links.self.href").description("link of self"),
                                fieldWithPath("_links.get-posts.href").description("link to get posts"),
                                fieldWithPath("_links.update-post.href").description("link to update post"),
                                fieldWithPath("_links.profile.href").description("link of profile")
                        )

                        ))
        ;

    }

    @Test
    @TestDescription("들어와서는 안되는 값이 들어왔을 때 BadRequest")
    public void createPost_Bad_Request() throws Exception {
        PostDto post = PostDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .createdDateTime(LocalDateTime.now())
                .build();

        this.mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                        .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("아이디 값이 들어왔을 때 BadRequest")
    public void createPost_Bad_Request_with_Wrong_Input() throws Exception{
        Post post = Post.builder()
                .id(123123)
                .title("test title")
                .content("test content")
                .build();

        this.mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("필수값이 없거나 비어있을 경우 BadRequest")
    public void createPost_Bad_Request_With_Blank_Input() throws Exception{
        PostDto post = PostDto.builder()
                .title("  ")
                .build();

        this.mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


}