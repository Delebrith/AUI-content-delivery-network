package pw.edu.aui.api;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pw.edu.aui.domain.Resource;
import pw.edu.aui.domain.ResourceNotFoundException;
import pw.edu.aui.domain.ResourceRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceRepository resourceRepository;

    @GetMapping(value = "{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getResource(@PathVariable Integer id) {
        byte[] resourceContent = resourceRepository.findById(id)
                .map(Resource::getContent)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return ResponseEntity.ok(new InputStreamResource(new ByteArrayInputStream(resourceContent)));
    }

    @PutMapping("{id}")
    public ResponseEntity<InputStreamResource> getResource(@PathVariable Integer id,
                                                           @RequestPart MultipartFile content) throws IOException {
        Resource resource = Resource.builder()
                .id(id)
                .content(content.getBytes())
                .build();
        resourceRepository.save(resource);
        return ResponseEntity.created(URI.create("/resource/" + id)).build();
    }
}
