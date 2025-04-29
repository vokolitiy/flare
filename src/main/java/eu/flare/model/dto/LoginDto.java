package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.service.validation.UserValidator;
import eu.flare.service.validation.UserValidatorVisitor;

public record LoginDto(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password) implements UserValidator {
    @Override
    public void accept(UserValidatorVisitor visitor) {
        visitor.visitLoginValidation(this);
    }
}
