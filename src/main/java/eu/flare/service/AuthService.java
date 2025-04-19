package eu.flare.service;

import eu.flare.model.User;
import eu.flare.model.Role;
import eu.flare.model.dto.LoginDto;
import eu.flare.model.dto.SignupDto;
import eu.flare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private static final int MIN_USERNAME_LENGTH = 5;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public boolean validateRequestBody(LoginDto loginDto) {
        return loginDto.username().length() >= MIN_USERNAME_LENGTH && !loginDto.password().isEmpty();
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
        appUser.setAccountNonLocked(true);
        appUser.setAccountNonExpired(true);
        appUser.setCredentialsNonExpired(true);
        appUser.setEnabled(true);
        appUser.setRoles(List.of(role));

        User savedUser = userRepository.save(appUser);
        return savedUser;
    }

    public User authenticate(LoginDto loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.username(),
                loginDto.password()
        ));

        Optional<User> userOptional = userRepository.findByUsername(loginDto.username());
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("Something went wrong");
        } else {
            return userOptional.get();
        }
    }
}
