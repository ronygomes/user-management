package me.ronygomes.userManagement.service;

import me.ronygomes.userManagement.common.dto.UserRegistrationDto;
import me.ronygomes.userManagement.common.dto.UserResponseDto;
import me.ronygomes.userManagement.common.model.User;
import me.ronygomes.userManagement.common.repository.UserRepository;
import me.ronygomes.userManagement.service.validator.UserValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator beanValidator;

    @Mock
    private UserValidator businessValidator;

    @Mock
    private EmailService emailService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, List.of(businessValidator), beanValidator, emailService);
    }

    @Test
    void registerUser_ShouldSucceed_WhenInputIsValid() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("StrongPass123!");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setPhoneNumber("+8801700000000");

        when(beanValidator.validate(any())).thenReturn(new HashSet<>());

        // Act
        UserResponseDto result = userService.registerUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(beanValidator).validate(dto);
        verify(businessValidator).validate(any(User.class));
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenBeanValidationFails() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        Set<ConstraintViolation<UserRegistrationDto>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));

        when(beanValidator.validate(dto)).thenReturn(violations);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.registerUser(dto));
        verify(userRepository, never()).save(any());
    }
}
