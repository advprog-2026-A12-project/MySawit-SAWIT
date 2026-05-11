package id.ac.ui.cs.advprog.mysawit.garden.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO yang memetakan respons dari Auth Service endpoint GET /users/{userId}.
 * Hanya mengambil field yang dibutuhkan oleh Garden module.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserResponse {

    private String id;
    private String username;
    private String email;
    private String name;
    private String role;
    private Boolean isActive;
    private String mandorCertificationNumber;
}
