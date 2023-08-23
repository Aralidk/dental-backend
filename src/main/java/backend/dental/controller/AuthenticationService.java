package backend.dental.controller;

import backend.dental.config.JwtService;
import backend.dental.enums.Role;
import backend.dental.worker.Worker;
import backend.dental.worker.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final WorkerRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = Worker.builder().firstName(request.getFirstName()).
                lastName(request.getLastName())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role(Role.CUSTOMER).department(request.getDepartment()).build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        //saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.password));
        Worker user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).email(user.getEmail()).firstName(user.getFirstName())
                .lastName(user.getLastName()).department(user.getDepartment()).role(user.getRole()).id(user.getId()).build();
    }
}

//    private void saveUserToken(Worker user, String jwtToken) {
//        var token = Token.builder()
//                .user(user)
//                .token(jwtToken)
//                .tokenType(TokenType.BEARER)
//                .expired(false)
//                .revoked(false)
//                .build();
//        tokenRepository.save(token);
//    }
//
//    private void revokeAllUserTokens(Worker user) {
//        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
//        if (!validUserTokens.isEmpty()) {
//            validUserTokens.forEach(token -> {
//                token.setExpired(true);
//                token.setRevoked(true);
//            });
//            tokenRepository.saveAll(validUserTokens);
//        }
//    }
