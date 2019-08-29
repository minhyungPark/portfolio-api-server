package me.puhehe99.portfolioapiserver.posts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    public void savePost() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .createdDateTime(LocalDateTime.now())
                .build();
        Post save = postRepository.save(post);
        System.out.println(save.getId());
        System.out.println(save.getTitle());
        assertThat(save.getId()).isNotNull();
        assertThat(save.getTitle()).isNotNull();
        assertThat(save.getContent()).isNotNull();
    }
}