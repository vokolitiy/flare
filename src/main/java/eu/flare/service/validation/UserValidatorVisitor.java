package eu.flare.service.validation;

import eu.flare.model.dto.LoginDto;
import eu.flare.model.dto.SignupDto;

public interface UserValidatorVisitor {
    boolean visitSignupValidation(SignupDto signupDto);
    boolean visitLoginValidation(LoginDto loginDto);
    boolean visitEmailValidation(SignupDto signupDto);
}
