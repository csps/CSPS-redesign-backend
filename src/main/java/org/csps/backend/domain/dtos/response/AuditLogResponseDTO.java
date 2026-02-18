package org.csps.backend.domain.dtos.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.csps.backend.domain.enums.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponseDTO {

    private Long id;

    @JsonAlias("admin_id")
    private Long adminId;

    @JsonAlias("admin_name")
    private String adminName;

    @JsonAlias("action")
    private AuditAction action;

    @JsonAlias("resource_type")
    private String resourceType;

    @JsonAlias("resource_id")
    private String resourceId;

    @JsonAlias("description")
    private String description;

    @JsonAlias("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
