package me.puhehe99.portfolioapiserver.posts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class PostDtoSerializer extends JsonSerializer<PostDto> {
    @Override
    public void serialize(PostDto postDto, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if (postDto.getTitle() != null) {
            jsonGenerator.writeStringField("title",postDto.getTitle());
        }
        if (postDto.getContent() != null) {
            jsonGenerator.writeStringField("content",postDto.getContent());
        }
        if (postDto.getCreatedDateTime() != null) {
            jsonGenerator.writeStringField("createdDateTime",postDto.getCreatedDateTime().toString());
        }
        jsonGenerator.writeEndObject();
    }
}
