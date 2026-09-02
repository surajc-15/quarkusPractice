package com.example.service;

import com.example.dtos.*;
import com.example.entity.User;
import com.example.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.http.Response;

import java.time.Duration;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.logging.Logger;

@ApplicationScoped
public class AuthService {

    Logger logger = Logger.getLogger(AuthService.class.getName());

    private final UserRepository userRepository;
    private JsonWebToken jwt;

    public AuthService(
            UserRepository userRepository,JsonWebToken jwt
    ) {
        this.userRepository = userRepository;
        this.jwt = jwt;
    }
    public LoginResponse login(LoginRequest request) {

        // 1. Find user
        User user = userRepository.findByUsername(request.username());

        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }
        // 2. Verify password
        if (!BcryptUtil.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new RuntimeException("Invalid username or password");
        }

        //3,generate new token and send
        String accessToken= generateAccessToken(user);

    return new LoginResponse(
            accessToken,
            user.getUsername()
    );
    }

    private String generateAccessToken(User user) {
        return Jwt.issuer("quarkus-auth")
                .subject(user.getUsername())
                .groups(user.getRole().name())
                .issuedAt(Instant.now())
                .expiresAt(
                        Instant.now().plus(Duration.ofMinutes(60))
                )
                .sign();
    }

    private String hashPassword(String password) {
        return BcryptUtil.bcryptHash(password);
    }

    @Transactional
    public AuthResponse register(UserDto userRequest) {

        // 1. Hash password
        String hashedPassword = hashPassword(userRequest.getPassword());

        // 2. Create user entity
        User newUser = new User(
                userRequest.getUsername(),
                hashedPassword,
                userRequest.getRole(),
                userRequest.getEmail()
        );

        // 3. Save user
        userRepository.persist(newUser);

        // 4. Generate access token
        String accessToken = generateAccessToken(newUser);

        // 5. Build response
        return new AuthResponse(
                newUser.getUsername(),
                newUser.getRole(),
                newUser.getEmail(),
                accessToken
        );
    }


    public AutoLoginResponse autoLogin(){
        String username=jwt.getSubject();
        logger.info("Extracting the username from the Token: " + username);

        //check if the user exist sin the database
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }
        logger.info("User found: " + user.getUsername());

        // Build response
        return new AutoLoginResponse(
                user.getEmail(),
                user.getRole(),
                user.getUsername()
        );
    }
}