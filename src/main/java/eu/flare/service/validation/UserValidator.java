package eu.flare.service.validation;

public interface UserValidator {
    void accept(UserValidatorVisitor visitor);
}
