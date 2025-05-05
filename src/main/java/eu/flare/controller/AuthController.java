package eu.flare.controller;

import eu.flare.exceptions.conflicts.EmailAlreadyExistsException;
import eu.flare.exceptions.notfound.RefreshTokenNotFoundException;
import eu.flare.model.Role;
import eu.flare.model.User;
import eu.flare.model.dto.LoginDto;
import eu.flare.model.dto.SignupDto;
import eu.flare.model.dto.UserLoggedOutDto;
import eu.flare.model.response.Responses;
import eu.flare.service.AuthService;
import eu.flare.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;

    @Autowired
    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDto signupDto) {
        boolean isBodyValid = authService.validateRequestBody(signupDto);
        if (!isBodyValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.SignupRequestValidationErrorResponse("Request body is invalid"));
        } else {
            boolean userExists = authService.userExists(signupDto);
            if (userExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Responses.UsernameExistsErrorResponse("User creation failed, invalid user name"));
            } else {
                boolean emailExists = authService.emailExists(signupDto);
                if (!emailExists) {
                    User appUser = authService.createNewUser(signupDto);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new Responses.UserSignedUpResponse(
                                    appUser.getId(),
                                    appUser.getUsername(),
                                    appUser.getFirstName(),
                                    appUser.getMiddleName(),
                                    appUser.getLastName()
                            ));
                } else {
                    try {
                        throw new EmailAlreadyExistsException("Invalid email address");
                    } catch (EmailAlreadyExistsException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new Responses.SignupRequestValidationErrorResponse(e.getMessage()));
                    }
                }
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        boolean isBodyValid = authService.validateRequestBody(loginDto);
        if (!isBodyValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.LoginRequestValidationErrorResponse("Request body is invalid"));
        } else {
            boolean userExists = authService.userExists(loginDto);
            if (!userExists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Responses.UserNotFoundErrorResponse("User not found"));
            } else {
                User user = authService.authenticate(loginDto);
                String token = jwtService.generateToken(user.getUsername(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
                authService.saveRefreshToken(token);
                Responses.UserLoggedInResponse response = new Responses.UserLoggedInResponse(token, jwtService.getJwtExpiration());
                return ResponseEntity.status(HttpStatus.OK)
                        .body(response);
            }
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            UserLoggedOutDto userLoggedOut = authService.logout(request, response);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.UserLoggedOutSuccessfullyResponse(userLoggedOut));
        } catch (RefreshTokenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.RefreshTokenNotFoundResponse(e.getMessage()));
        }
    }
}
