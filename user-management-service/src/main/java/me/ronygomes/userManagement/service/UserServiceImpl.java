package me.ronygomes.userManagement.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import me.ronygomes.userManagement.common.dto.UserRegistrationDto;
import me.ronygomes.userManagement.common.dto.UserResponseDto;
import me.ronygomes.userManagement.common.dto.UserUpdateDto;
import me.ronygomes.userManagement.common.exception.ValidationException;
import me.ronygomes.userManagement.common.model.User;
import me.ronygomes.userManagement.common.repository.UserRepository;
import me.ronygomes.userManagement.common.utils.ValidationUtils;
import me.ronygomes.userManagement.service.validator.UserValidator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private static final String DEFAULT_COUNTRY_CODE = "BD";

    private final UserRepository userRepository;
    private final List<UserValidator> validators;
    private final Validator beanValidator;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, List<UserValidator> validators, Validator beanValidator,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.validators = validators;
        this.beanValidator = beanValidator;
        this.emailService = emailService;
    }

    @Override
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        validateDto(registrationDto);

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setDisplayName(registrationDto.getDisplayName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setDateOfBirth(registrationDto.getDateOfBirth());

        user.setEmail(ValidationUtils.normalizeEmail(user.getEmail()));
        user.setPhoneNumber(ValidationUtils.formatPhoneNumber(user.getPhoneNumber(),
                DEFAULT_COUNTRY_CODE));

        validators.forEach(v -> v.validate(user));
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);

        try {
            emailService.sendWelcomeEmail(user);
        } catch (RuntimeException e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return mapToResponseDto(user);
    }

    @Override
    public UserResponseDto findUser(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("User not found"));

        return mapToResponseDto(existingUser);
    }

    @Override
    public UserResponseDto updateUser(String id, UserUpdateDto updateDto) {
        validateDto(updateDto);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("User not found"));

        if (updateDto.getDisplayName() != null)
            existingUser.setDisplayName(updateDto.getDisplayName());
        if (updateDto.getFirstName() != null)
            existingUser.setFirstName(updateDto.getFirstName());
        if (updateDto.getLastName() != null)
            existingUser.setLastName(updateDto.getLastName());
        if (updateDto.getDateOfBirth() != null)
            existingUser.setDateOfBirth(updateDto.getDateOfBirth());
        if (updateDto.getPhoneNumber() != null)
            existingUser.setPhoneNumber(updateDto.getPhoneNumber());
        if (updateDto.getUsername() != null)
            existingUser.setUsername(updateDto.getUsername());

        existingUser.setPhoneNumber(ValidationUtils.formatPhoneNumber(existingUser.getPhoneNumber(), "BD"));
        validators.forEach(v -> v.validate(existingUser));
        userRepository.save(existingUser);

        return mapToResponseDto(existingUser);
    }

    private <T> void validateDto(T dto) {
        Set<ConstraintViolation<T>> violations = beanValidator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new ValidationException("Validation failed: " + errorMessage);
        }
    }

    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto response = new UserResponseDto();
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
