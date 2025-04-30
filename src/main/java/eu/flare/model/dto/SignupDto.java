package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.service.validation.UserValidator;
import eu.flare.service.validation.UserValidatorVisitor;

public record SignupDto(
        @JsonProperty("username") String username,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password,
        @JsonProperty("role") String role
) implements UserValidator {
    @Override
    public void accept(UserValidatorVisitor visitor) {
        visitor.visitSignupValidation(this);
    }
}
