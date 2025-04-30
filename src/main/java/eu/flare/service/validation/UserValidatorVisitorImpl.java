package eu.flare.service.validation;

import eu.flare.model.User;
import eu.flare.model.dto.LoginDto;
import eu.flare.model.dto.SignupDto;
import eu.flare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserValidatorVisitorImpl implements UserValidatorVisitor {

    private final UserRepository userRepository;

    @Autowired
    public UserValidatorVisitorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean visitSignupValidation(SignupDto signupDto) {
        Optional<User> userOptional = userRepository.findByUsername(signupDto.username());
        return userOptional.isPresent();
    }

    @Override
    public boolean visitLoginValidation(LoginDto loginDto) {
        return userRepository.findByUsername(loginDto.username()).isPresent();
    }

    @Override
    public boolean visitEmailValidation(SignupDto signupDto) {
        return userRepository.findByEmail(signupDto.email()).isPresent();
    }
}
