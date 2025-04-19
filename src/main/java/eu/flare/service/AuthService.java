package eu.flare.service;

import eu.flare.model.User;
import eu.flare.model.Role;
import eu.flare.model.dto.SignupDto;
import eu.flare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MIN_USERNAME_LENGTH = 5;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean validateRequestBody(SignupDto signupDto) {
        return signupDto.username().length() >= MIN_USERNAME_LENGTH
                && !signupDto.password().isEmpty()
                && !signupDto.email().isEmpty()
                && !signupDto.firstName().isEmpty()
                && !signupDto.lastName().isEmpty();
    }

    public boolean checkIfUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User createNewUser(SignupDto signupDto) {
        User appUser = new User();
        Role role = new Role();
        role.setName(signupDto.role());
        appUser.setEmail(signupDto.email());
        appUser.setPassword(passwordEncoder.encode(signupDto.password()));
        appUser.setUsername(signupDto.username());
        appUser.setFirstName(signupDto.firstName());
        appUser.setLastName(signupDto.lastName());
        appUser.setRoles(List.of(role));

        User savedUser = userRepository.save(appUser);
        return savedUser;
    }
}
