package com.example.demo.Service;

import java.util.Arrays;
import java.util.List;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.RegisterRequestDTO;

import jakarta.ws.rs.core.Response;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public Keycloak keycloakInstance() {
        return Keycloak.getInstance(
                authServerUrl,
                "master", // Realm của Keycloak admin
                "khoingao", // Tài khoản admin Keycloak
                "Khoi20022654@", // Mật khẩu admin Keycloak
                "myclient",
                "pskOESASosXeU8RgV8g8wS40WzWHn7l3");

    }

    public void register(RegisterRequestDTO registerRequest) {

        Keycloak keycloak = keycloakInstance();

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD); // Đảm bảo đây là mật khẩu
        credential.setValue(registerRequest.getPassword()); // Đặt mật khẩu
        credential.setTemporary(false); // Chỉ định mật khẩu không phải là tạm thời

        UserRepresentation user = new UserRepresentation();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setCredentials(Arrays.asList(credential));
        user.setEnabled(true);
        user.setRealmRoles(Arrays.asList("user"));

        // Tạo người dùng mới
        usersResource.create(user);
    }

    public boolean checkEmail(String email) {
        Keycloak keycloak = keycloakInstance();

        RealmResource realmResource = keycloak.realm(realm);

        // Tìm kiếm người dùng với email
        List<UserRepresentation> users = realmResource.users().search(email);

        // Kiểm tra nếu có người dùng với email
        return !users.isEmpty();
    }

}
