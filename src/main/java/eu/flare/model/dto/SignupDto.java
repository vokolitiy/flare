package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.service.validation.UserValidator;
import eu.flare.service.validation.UserValidatorVisitor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupDto(
        @JsonProperty("username")
        @NotBlank @Size(max = 20, message = "Username is too long")
        String username,
        @JsonProperty("first_name")
        @NotBlank(message = "First name can't be blank")
        String firstName,
        @JsonProperty("last_name")
        @NotBlank(message = "Last name can't be blank")
        String lastName,
        @JsonProperty("email")
        @NotBlank(message = "Email can't be blank")
        @Size(max = 20, message = "Email is too long")
        String email,
        @JsonProperty("password")
        @NotBlank(message = "Password can't be blank")
        String password,
        @JsonProperty("role")
        @NotBlank(message = "Role can't be blank")
        String role
) implements UserValidator {
    @Override
    public void accept(UserValidatorVisitor visitor) {
        visitor.visitSignupValidation(this);
    }
}
