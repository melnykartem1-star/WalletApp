package org.my.walletapp.aspect;

import org.my.walletapp.exception.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test-not-found")
    public void throwNotFound() { throw new ResourceNotFoundException("Not Found"); }

    @GetMapping("/test-email-exists")
    public void throwEmailExists() { throw new EmailAlreadyExistsException("Email exists"); }

    @GetMapping("/test-bad-request")
    public void throwBadRequest() { throw new IllegalArgumentException("Bad Request"); }

    @GetMapping("/test-forbidden")
    public void throwForbidden() { throw new InvalidRefreshToken("Invalid token"); }

    @GetMapping("/test-unauthorized")
    public void throwUnauthorized() { throw new BadCredentialsException("Wrong password"); }

    @GetMapping("/test-server-error")
    public void throwGenericException() { throw new RuntimeException("Generic Error"); }
}