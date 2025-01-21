package com.example.demo.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demo.DTO.LoginRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LoginServiceImpl implements LoginService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.realm}")
    private String realm;

    public Map<String, String> getKeycloakToken(LoginRequestDTO loginRequest) {
        String url = keycloakAuthServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        // Cấu hình các tham số trong body của request
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", loginRequest.getEmail());
        map.add("password", loginRequest.getPassword());
        map.add("grant_type", "password");
        map.add("scope", "openid");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse JSON response để lấy access_token và refresh_token
            String responseBody = response.getBody();
            String accessToken = jsonDecode(responseBody, "access_token");
            String refreshToken = jsonDecode(responseBody, "refresh_token");

            // Trả về cả access_token và refresh_token trong một Map
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            return tokens;
        } else {
            return null;
        }
    }

    // Lấy thông tin người dùng từ Admin API sau khi có token
    public String getUserInfo(String token) {
        String url = keycloakAuthServerUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Set the bearer token

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return jsonDecode(response.getBody(), "preferred_username"); // Trả về thông tin người dùng từ Keycloak
        } else {
            return null;
        }
    }

    public String jsonDecode(String response, String field) {
        try {
            // Chuyển đổi phản hồi JSON thành đối tượng JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);

            // Lấy access_token từ phản hồi
            return jsonResponse.get(field).asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
