package pw.edu.aui.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pw.edu.aui.domain.ResourceNotFoundException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class DefaultExceptionHandler {
    private static final String VISITED_LOCATIONS_COOKIE = "Location-Visited-";
    private final RedirectConfig redirectConfig;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(final HttpServletRequest request, final ResourceNotFoundException e) {
        URI redirectUri = redirectConfig.getUriForResource(e.getId());
        String currentUri = request.getRequestURL().toString();
        String cookieName = VISITED_LOCATIONS_COOKIE + currentUri.replace("/", "").replace(":", "");
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst()
                .map(array -> handleVisitedLocationsHeader(cookieName))
                .orElseGet(() -> this.handleNoVisitedLocationsHeader(redirectUri, cookieName));
    }

    private ResponseEntity handleNoVisitedLocationsHeader(URI redirectUri, String cookieName) {
        log.info("No {} cookie", cookieName);
        String setCookie = cookieName + "=\"" + true + "\"; Max-Age=30";
        log.info("Setting {} cookie: {}", VISITED_LOCATIONS_COOKIE, setCookie);
        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, redirectUri.toString())
                .header(HttpHeaders.SET_COOKIE, setCookie)
                .build();
    }

    private ResponseEntity handleVisitedLocationsHeader(String cookieName) {
        log.info("Has {} cookie. Already was here", cookieName);
        return ResponseEntity.notFound().build();
    }
}
