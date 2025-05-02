package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.service.validation.UserValidator;
import eu.flare.service.validation.UserValidatorVisitor;
import jakarta.validation.constraints.NotBlank;

public record LoginDto(
        @JsonProperty("username")
        @NotBlank(message = "Username can't be blank")
        String username,
        @JsonProperty("password")
        @NotBlank(message = "Password can't be blank")
        String password) implements UserValidator {
    @Override
    public void accept(UserValidatorVisitor visitor) {
        visitor.visitLoginValidation(this);
    }
}
