
package com.example.campusachieve.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/ml")
public class MLController {

    private final RestClient restClient;

    public MLController(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://127.0.0.1:5000")
                .build();
    }

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(
            @RequestBody Map<String, String> request) {

        String certificateName = request.get("certificate_name");

        if (certificateName == null || certificateName.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Certificate name is required"));
        }

        Map<String, String> mlRequest = new LinkedHashMap<>();
        mlRequest.put("certificate_name", certificateName);
        mlRequest.put("description",
                request.getOrDefault("description", ""));

        Map<String, Object> result = restClient.post()
                .uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .body(mlRequest)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        return ResponseEntity.ok(result);
    }
}
