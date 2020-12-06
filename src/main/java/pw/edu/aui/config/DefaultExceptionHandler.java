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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler {
    private static final String VISITED_LOCATIONS_HEADER = "x-locations-visited";
    private final RedirectConfig redirectConfig;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(final HttpServletRequest request, final ResourceNotFoundException e) {
        URI redirectUri = redirectConfig.getUriForResource(e.getId());
        return Optional.ofNullable(request.getHeader(VISITED_LOCATIONS_HEADER))
                .map(value -> value.split(";"))
                .map(array -> handleVisitedLocationsHeader(redirectUri, array))
                .orElse(this.handleNoVisitedLocationsHeader(redirectUri));
    }

    private ResponseEntity handleNoVisitedLocationsHeader(URI redirectUri) {
        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, redirectUri.toString())
                .header(VISITED_LOCATIONS_HEADER, redirectUri.toString())
                .build();
    }

    private ResponseEntity handleVisitedLocationsHeader(URI redirectUri, String[] array) {
        List<String> visitedURIs = Arrays.asList(array);
        if (visitedURIs.contains(redirectUri.toString())) {
            return ResponseEntity.notFound().build();
        } else {
            visitedURIs.add(redirectUri.toString());
            return ResponseEntity
                    .status(HttpStatus.PERMANENT_REDIRECT)
                    .header(HttpHeaders.LOCATION, redirectUri.toString())
                    .header(VISITED_LOCATIONS_HEADER, String.join(";", visitedURIs))
                    .build();
        }
    }
}
