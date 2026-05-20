package org.my.walletapp.aspect;

import org.junit.jupiter.api.Test;
import org.my.walletapp.exception.EmailAlreadyExistsException;
import org.my.walletapp.exception.InvalidRefreshToken;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.security.JwtAuthenticationFilter;
import org.my.walletapp.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TestController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {
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

    @Test
    void handleResourceNotFound_Returns404() throws Exception {
        mockMvc.perform(get("/test-not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleEmailExists_Returns409() throws Exception {
        mockMvc.perform(get("/test-email-exists"))
                .andExpect(status().isConflict());
    }

    @Test
    void handleBadRequests_Returns400() throws Exception {
        mockMvc.perform(get("/test-bad-request"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleInvalidToken_Returns403() throws Exception {
        mockMvc.perform(get("/test-forbidden"))
                .andExpect(status().isForbidden());
    }

    @Test
    void handleUnauthorized_Returns401() throws Exception {
        mockMvc.perform(get("/test-unauthorized"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void handleGenericException_Returns500() throws Exception {
        mockMvc.perform(get("/test-server-error"))
                .andExpect(status().isInternalServerError());
    }
}
