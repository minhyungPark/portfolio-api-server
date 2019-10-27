package me.puhehe99.portfolioapiserver.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.puhehe99.portfolioapiserver.accounts.AccountService;
import me.puhehe99.portfolioapiserver.common.BaseControllerTest;
import me.puhehe99.portfolioapiserver.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @Autowired
    AccountService accountService;

    public String getBearerToken() throws Exception {
        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic("myApp", "pass"))
                .param("username", "user@email.com")
                .param("password", "1212")
                .param("grant_type", "password"));
        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return "Bearer "+ parser.parseMap(responseBody).get("access_token").toString();
    }

    @Test
    @TestDescription("post를 정상적으로 생성하는 테스트")
    public void createPost() throws Exception {
        PostDto post = PostDto.builder()
                .title("test title")
                .content("<p>test content</p>")
                .build();

        this.mockMvc.perform(post("/api/posts")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                                fieldWithPath("modifiedDateTime").description("modified time of post"),
                                fieldWithPath("manager").description("manager of post"),
                                fieldWithPath("_links.self.href").description("link of self"),
                                fieldWithPath("_links.get-posts.href").description("link to get posts"),
                                fieldWithPath("_links.update-post.href").description("link to update post"),
                                fieldWithPath("_links.profile.href").description("link of profile")
                        )

                        ))
        ;

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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getPosts() throws Exception {
        // Given
        for(int i=0;i<30;++i) {
            Post post = Post.builder()
                    .title("test title" + i)
                    .content("test content" + i)
                    .createdDateTime(LocalDateTime.now())
                    .build();
            this.postRepository.save(post);
        }

        this.mockMvc.perform(get("/api/posts")
                .param("page","1")
                .param("size","10")
                .param("sort","title,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.postList").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-posts"))
        ;
    }

    @Test
    @TestDescription("이벤트 하나를 받는 테스트")
    public void getPost() throws Exception {
        // Given
        Post testPost = Post.builder()
                .title("test title")
                .content("<p>test content</p>")
                .createdDateTime(LocalDateTime.now())
                .build();
        Post savedPost = this.postRepository.save(testPost);

        // When & Then
        this.mockMvc.perform(get("/api/posts/{id}",savedPost.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(savedPost.getId()))
                .andExpect(jsonPath("title").value(savedPost.getTitle()))
                .andExpect(jsonPath("content").value(savedPost.getContent()))
                .andExpect(jsonPath("createdDateTime").value(savedPost.getCreatedDateTime().toString()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-post"))
        ;
    }

    @Test
    @TestDescription("없는 포스트를 조회했을 경우 Not Found")
    public void getPost404() throws Exception {

        this.mockMvc.perform(get("/api/posts/321123"))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @TestDescription("포스트를 정상적으로 수정")
    public void updatePost() throws Exception {
        // Given

        Post post = Post.builder()
                .title("test title")
                .content("test content")
                .createdDateTime(LocalDateTime.now())
                .build();
        Post savedPost = this.postRepository.save(post);

        PostDto updatePost = PostDto.builder()
                .title("update title")
                .content("update content")
                .build();

        // When
        this.mockMvc.perform(put("/api/posts/{id}",savedPost.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(updatePost)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").value(updatePost.getTitle()))
                .andExpect(jsonPath("content").value(updatePost.getContent()))
                .andExpect(jsonPath("createdDateTime").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    @TestDescription("없는 post를 수정할 때 404")
    public void updatePost_404() throws Exception {

        PostDto postDto = PostDto.builder()
                .title("test title")
                .content("test content")
                .build();

        this.mockMvc.perform(put("/api/posts/1231231")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(postDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }



}