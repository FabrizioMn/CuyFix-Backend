package com.grupo01.incident_manager.config.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.grupo01.incident_manager.dtos.auth.LoginRequest;
import com.grupo01.incident_manager.dtos.auth.RegisterRequest;
import com.grupo01.incident_manager.dtos.auth.TokenResponse;
import com.grupo01.incident_manager.exception.ResourceAlreadyExistsException;
import com.grupo01.incident_manager.exception.ResourceNotFoundException;
import com.grupo01.incident_manager.model.Role;
import com.grupo01.incident_manager.model.User;
import com.grupo01.incident_manager.model.UserToken;
import com.grupo01.incident_manager.model.UserToken.TokenType;
import com.grupo01.incident_manager.repository.RoleRepository;
import com.grupo01.incident_manager.repository.UserRepository;
import com.grupo01.incident_manager.repository.UserTokenRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;

    @SuppressWarnings("null")
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("El correo ya esta registrado");
        }

        Role role = roleRepository.findById(request.idRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        // Construimos al usuario
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        User saveUser = userRepository.save(user);

        // Genramos los tokens
        String jwtToken = jwtService.generateToken(saveUser);
        String refreshToken = jwtService.generateRefreshToken(saveUser);

        // Persistir el token de acceso en la bd
        saveUserToken(saveUser, jwtToken);

        return new TokenResponse(jwtToken, refreshToken);

    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        revokedAllUserTokens(user);
        String jwtToken = jwtService.generateToken(user);
        String jwtTokenRefresh = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);
        return new TokenResponse(jwtToken, jwtTokenRefresh);
    }

    public TokenResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token Bearer invalido");
        }

        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new IllegalArgumentException("Token Bearer invalido");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Token refresh invalido");
        }

        String accessToken = jwtService.generateToken(user);
        saveUserToken(user, accessToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    private void saveUserToken(User user, String jwtToken) {

        UserToken token = new UserToken();
        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);

        userTokenRepository.save(token);
    }

    private void revokedAllUserTokens(User user) {
        var validUserTokens = userTokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        userTokenRepository.saveAll(validUserTokens);
    }
}
