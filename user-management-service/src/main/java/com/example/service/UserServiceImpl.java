package com.example.service;

import com.example.common.dto.UserRegistrationDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.common.dto.UserUpdateDTO;
import com.example.common.model.User;
import com.example.common.repository.UserRepository;
import com.example.common.utils.ValidationUtils;
import com.example.service.validator.UserValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final List<UserValidator> validators;
    private final Validator beanValidator;

    public UserServiceImpl(UserRepository userRepository, List<UserValidator> validators, Validator beanValidator) {
        this.userRepository = userRepository;
        this.validators = validators;
        this.beanValidator = beanValidator;
    }

    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        // 1. JSR 380 Bean Validation
        validateDTO(registrationDTO);

        // 2. Mapping DTO to Domain Model
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(registrationDTO.getPassword());
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setDisplayName(registrationDTO.getDisplayName());
        user.setPhoneNumber(registrationDTO.getPhoneNumber());
        user.setDateOfBirth(registrationDTO.getDateOfBirth());

        // 3. Normalization (Structural/Format prep before business validation)
        user.setEmail(ValidationUtils.normalizeEmail(user.getEmail()));
        user.setPhoneNumber(ValidationUtils.formatPhoneNumber(user.getPhoneNumber(), "BD"));

        // 4. Business Validation (Strategy Pattern)
        validators.forEach(v -> v.validate(user));

        // 5. Security: Hashing
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        // 6. Persistence
        userRepository.save(user);

        return mapToResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(String id, UserUpdateDTO updateDTO) {
        // 1. JSR 380 Bean Validation
        validateDTO(updateDTO);

        // 2. Fetch existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Map allowed fields from DTO
        if (updateDTO.getDisplayName() != null)
            existingUser.setDisplayName(updateDTO.getDisplayName());
        if (updateDTO.getFirstName() != null)
            existingUser.setFirstName(updateDTO.getFirstName());
        if (updateDTO.getLastName() != null)
            existingUser.setLastName(updateDTO.getLastName());
        if (updateDTO.getDateOfBirth() != null)
            existingUser.setDateOfBirth(updateDTO.getDateOfBirth());
        if (updateDTO.getPhoneNumber() != null)
            existingUser.setPhoneNumber(updateDTO.getPhoneNumber());
        if (updateDTO.getUsername() != null)
            existingUser.setUsername(updateDTO.getUsername());

        // 4. Normalization logic
        existingUser.setPhoneNumber(ValidationUtils.formatPhoneNumber(existingUser.getPhoneNumber(), "BD"));

        // 5. Re-run business validation strategies
        validators.forEach(v -> v.validate(existingUser));

        // 6. Persistence
        userRepository.save(existingUser);

        return mapToResponseDTO(existingUser);
    }

    private <T> void validateDTO(T dto) {
        Set<ConstraintViolation<T>> violations = beanValidator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Validation failed: " + errorMessage);
        }
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setDisplayName(user.getDisplayName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDateOfBirth(user.getDateOfBirth());
        return response;
    }
}
