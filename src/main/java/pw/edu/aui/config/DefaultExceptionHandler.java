package pw.edu.aui.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pw.edu.aui.domain.ResourceNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@ControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler {
    private final RedirectConfig redirectConfig;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(final HttpServletRequest request, final ResourceNotFoundException e) {
        URI redirectUri = redirectConfig.getUriForResource(e.getId());
        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, redirectUri.toString())
                .build();
    }
}
