package me.puhehe99.portfolioapiserver.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.puhehe99.portfolioapiserver.common.TestDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
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