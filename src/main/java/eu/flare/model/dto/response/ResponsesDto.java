package eu.flare.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ResponsesDto {

    public record CreateProjectResponseDto(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("created_at") Date createdAt,
            @JsonProperty("updated_at") Date updatedAt
    ){}
}
