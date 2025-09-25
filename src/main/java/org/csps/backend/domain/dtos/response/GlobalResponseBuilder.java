package org.csps.backend.domain.dtos.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class GlobalResponseBuilder<T> {
    private String status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    
    public static <T> ResponseEntity<GlobalResponseBuilder<T>> buildResponse(String message, T data, HttpStatus status) {
        GlobalResponseBuilder<T> response = new GlobalResponseBuilder<>();
        response.setStatus(status.name());
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(status).body(response);
    }

}
