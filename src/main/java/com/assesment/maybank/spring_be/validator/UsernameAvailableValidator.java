package com.assesment.maybank.spring_be.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.assesment.maybank.spring_be.annotation.UsernameAvailable;
import com.assesment.maybank.spring_be.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UsernameAvailableValidator implements ConstraintValidator<UsernameAvailable, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.isBlank()) {
            return true;
        }
        return !userRepository.existsByUsername(username);
    }
}
