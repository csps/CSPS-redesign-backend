package org.csps.backend.domain.dtos.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnouncementResponseDTO {
    private String id;
    
    @JsonAlias("full_picture")
    private String fullPicture;
    
    private String message;

    @JsonProperty(value = "status_type", access = Access.WRITE_ONLY)
    private String statusType;

    @JsonProperty(value = "parent_id", access = Access.WRITE_ONLY)
    private String parentId;

    @JsonAlias("permalink_url")
    private String permaLink;
}
