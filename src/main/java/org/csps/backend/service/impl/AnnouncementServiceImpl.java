package org.csps.backend.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.csps.backend.domain.dtos.response.AnnouncementResponseDTO;
import org.csps.backend.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String ORIGINAL_POST_FILTER = "added_photos";

    @Value("${metagraph.api.url}")
    private String metaGraphApiUrl;

    @Value("${metagraph.api.graphapikey}")
    private String graphApiKey;

    @Override
    public List<AnnouncementResponseDTO> getAllAnnouncements() {
        try {
            
            System.out.println("Fetching announcements from Facebook Graph API... " + graphApiKey);
            // Build the Facebook Graph API URL
            String url = metaGraphApiUrl + "/feed?access_token=" + graphApiKey 
                        + "&fields=id,full_picture,message,status_type,parent_id,permalink_url&limit=25";
            // Call the Facebook API
            String response = restTemplate.getForObject(url, String.class);

            System.out.println("Facebook API Response: " + response);
            // Parse the response
            JsonNode rootNode = objectMapper.readTree(response);

            JsonNode feedData = rootNode.path("data");

            List<JsonNode> originalPosts = new ArrayList<>();
            
            
            if (feedData.isArray()) {
                for (JsonNode postNode: feedData) 
                {
                    String statusType = postNode.path("status_type").asText();
                    if (ORIGINAL_POST_FILTER.equals(statusType)) {
                        originalPosts.add(postNode);
                    }   
                    
                }
            }

            List<AnnouncementResponseDTO> announcements = objectMapper.convertValue(originalPosts,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, AnnouncementResponseDTO.class));


            return announcements;
        } catch (Exception e) {
            System.out.println("Error fetching announcements: " + e.getMessage());
            throw new RuntimeException("Failed to fetch announcements from Facebook API",e);
            
        }
    }
}
