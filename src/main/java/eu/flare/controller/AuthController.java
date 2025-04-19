package eu.flare.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.model.User;
import eu.flare.model.dto.SignupDto;
import eu.flare.service.AuthService;
import eu.flare.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Object> signup(@RequestBody SignupDto signupDto) {
        boolean isBodyValid = authService.validateRequestBody(signupDto);
        if (!isBodyValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginRequestValidationError("Request body is invalid"));
        } else {
            boolean userExists = authService.checkIfUserExists(signupDto.username());
            if (userExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new UsernameExistsError("User creation failed, this username is already taken"));
            } else {
                User appUser = authService.createNewUser(signupDto);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(appUser);
            }
        }
    }

    public record LoginRequestValidationError(@JsonProperty("error") String reason) { }
    public record UsernameExistsError(@JsonProperty("error") String reason) {}
}
