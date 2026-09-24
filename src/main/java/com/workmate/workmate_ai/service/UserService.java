package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.entity.User;
import com.workmate.workmate_ai.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.workmate.workmate_ai.exception.EmailAlreadyExistsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.workmate.workmate_ai.exception.InvalidCredentialsException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public User createUser(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {

        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public User login(String email, String password) {

        User user = findUserByEmail(email);

        if (!checkPassword(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return user;
    }
}