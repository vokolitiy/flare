package eu.flare.service;

import eu.flare.exceptions.notfound.RefreshTokenNotFoundException;
import eu.flare.model.RefreshToken;
import eu.flare.model.RefreshTokenStatus;
import eu.flare.model.Role;
import eu.flare.model.User;
import eu.flare.model.dto.LoginDto;
import eu.flare.model.dto.SignupDto;
import eu.flare.model.dto.UserLoggedOutDto;
import eu.flare.repository.RefreshTokenRepository;
import eu.flare.repository.UserRepository;
import eu.flare.service.validation.UserValidatorVisitor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserValidatorVisitor validatorVisitor;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final int MIN_USERNAME_LENGTH = 5;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserValidatorVisitor validatorVisitor,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.validatorVisitor = validatorVisitor;
        this.refreshTokenRepository = refreshTokenRepository;
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

    public boolean userExists(SignupDto signupDto) {
        return validatorVisitor.visitSignupValidation(signupDto);
    }

    public boolean userExists(LoginDto loginDto) {
        return validatorVisitor.visitLoginValidation(loginDto);
    }

    public boolean emailExists(SignupDto signupDto) {
        return validatorVisitor.visitEmailValidation(signupDto);
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

    @Transactional
    public UserLoggedOutDto logout(HttpServletRequest request, HttpServletResponse response) throws RefreshTokenNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                throw new RefreshTokenNotFoundException("Refresh token not found");
            }

            RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                    .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));
            refreshToken.setRefreshTokenStatus(RefreshTokenStatus.REVOKED);
            refreshTokenRepository.save(refreshToken);
            authentication.setAuthenticated(false);
            return new UserLoggedOutDto("User logged out successfully");
        } else {
            throw new UsernameNotFoundException("Username not found");
        }
    }

    public void saveRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(token);
        if (refreshTokenOptional.isEmpty()) {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(token);
            refreshToken.setRefreshTokenStatus(RefreshTokenStatus.GRANTED);
            refreshTokenRepository.save(refreshToken);
        }
    }
}
