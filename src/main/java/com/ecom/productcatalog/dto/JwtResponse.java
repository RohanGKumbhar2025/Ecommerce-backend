package com.ecom.productcatalog.dto;

import lombok.Data;
import java.util.List;

/**
 * Represents the response sent to the client upon successful authentication.
 * It contains the JWT token, user details, and roles.
 */
@Data
public class JwtResponse {

    /**
     * The JWT token for authenticating subsequent requests.
     */
    private String token;

    /**
     * The type of token, which is always "Bearer".
     */
    private String type = "Bearer";

    /**
     * The unique identifier of the authenticated user.
     */
    private Long id;

    /**
     * The email address of the authenticated user.
     */
    private String email;

    /**
     * The name of the authenticated user.
     */
    private String name;

    /**
     * The roles assigned to the user (e.g., "ROLE_USER", "ROLE_ADMIN").
     */
    private List<String> roles;

    public JwtResponse(String token, Long id, String email, String name, List<String> roles) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }
}