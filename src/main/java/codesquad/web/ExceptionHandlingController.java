package codesquad.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class ExceptionHandlingController extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingController.class);

    @ExceptionHandler(value = AuthenticationException.class)
    public String loginError(AuthenticationException e) {
        log.debug("UserController login : {}", e.getMessage());
        return "/user/login_failed";
    }
}
