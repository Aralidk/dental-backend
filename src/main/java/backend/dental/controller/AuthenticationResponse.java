package backend.dental.controller;
import backend.dental.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private  String firstName;
    private String lastName;
    private String email;
    private String department;
    private Role role;
    private int id;
}
