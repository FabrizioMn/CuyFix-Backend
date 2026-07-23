package com.grupo01.incident_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.grupo01.incident_manager.config.security.AuthService;
import com.grupo01.incident_manager.config.security.JwtService;
import com.grupo01.incident_manager.dtos.auth.LoginRequest;
import com.grupo01.incident_manager.dtos.auth.RegisterRequest;
import com.grupo01.incident_manager.dtos.auth.TokenResponse;
import com.grupo01.incident_manager.model.Role;
import com.grupo01.incident_manager.model.User;
import com.grupo01.incident_manager.repository.RoleRepository;
import com.grupo01.incident_manager.repository.UserRepository;
import com.grupo01.incident_manager.repository.UserTokenRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserTokenRepository userTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private Role mockRole;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockRole = new Role(1L, "ROLE_USER", "Rol usuario final");
        mockUser = User.builder()
                .id(1L)
                .name("Carlos Perez")
                .email("carlos@test.com")
                .password("encoded_password")
                .role(mockRole)
                .build();
    }

    @Test
    @DisplayName("Debe registrar un nuevo usuario y retornar los tokens correctamente")
    void register_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest("carlos@test.com", "password123", "Carlos Perez", 1L);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode(request.password())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("access_token_jwt");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh_token_jwt");

        // Act
        TokenResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("access_token_jwt", response.accessToken());
        assertEquals("refresh_token_jwt", response.refreshToken());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userTokenRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe lanzar una excepción cuando el email ya está registrado")
    void register_ThrowsException_WhenEmailExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("carlos@test.com", "password123", "Carlos Perez", 1L);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("El correo ya esta registrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe autenticar correctamente y devolver un nuevo token de acceso")
    void login_Success() {
        // Arrange
        LoginRequest request = new LoginRequest("carlos@test.com", "password123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser)).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken(mockUser)).thenReturn("new_refresh_token");

        // Act
        TokenResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("new_access_token", response.accessToken());
        assertEquals("new_refresh_token", response.refreshToken());
        verify(authenticationManager, times(1)).authenticate(any());
    }
}
