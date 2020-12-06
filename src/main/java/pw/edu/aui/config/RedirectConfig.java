package pw.edu.aui.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class RedirectConfig {
    private final LinkedMap<Integer, List<ResourceDto>> redirectConfigMap;
    private final Integer nodeNumber;
    private final Integer knownNeighbours;
    private final String baseAddressForRedirect;

    public RedirectConfig(@Value("${pw.edu.aui.config-map-path}") String resourceMapPath,
                          @Value("${pw.edu.aui.node-number}") Integer nodeNumber,
                          @Value("${pw.edu.aui.neighbours-known}") Integer knownNeighbours,
                          @Value("${pw.edu.aui.redirect.base}") String baseAddressForRedirect) throws IOException {
        log.info("Config: path {}, node {}, neighbours {}", resourceMapPath, nodeNumber, knownNeighbours);
        this.nodeNumber = nodeNumber;
        this.knownNeighbours = knownNeighbours;
        this.baseAddressForRedirect = baseAddressForRedirect + ":808";
        this.redirectConfigMap = initConfigMap(resourceMapPath, nodeNumber, knownNeighbours, this.baseAddressForRedirect);
        log.info("Initialized redirect config {}", redirectConfigMap);
    }

    @lombok.Value
    private static class ResourceDto {
        int resourceId;
        URI uri;
    }

    public URI getUriForResource(int resourceId) {
        URI target = redirectConfigMap.values().stream()
                .filter(list -> list.stream().filter(dto -> dto.resourceId == resourceId).count() != 0)
                .flatMap(List::stream)
                .map(ResourceDto::getUri)
                .findFirst()
                .orElse(buildTargetUriIfNotFound(resourceId));
        log.info("Redirecting to {}", target);
        return target;
    }

    private URI buildTargetUriIfNotFound(Integer resourceId) {
        int maxNode = redirectConfigMap.lastKey();
        return URI.create(baseAddressForRedirect + maxNode  + "/resource/" + resourceId);
    }

    private static LinkedMap<Integer, List<ResourceDto>> initConfigMap(String resourceMapPath, Integer nodeNumber, Integer knownNeighbours, String baseAddressForRedirect) throws IOException {
        Path configPath = Path.of(resourceMapPath);
        List<String> lines = Files.readAllLines(configPath);
        LinkedMap<Integer, List<ResourceDto>> configMap = new LinkedMap<>();

        List<Integer> linesNumbers = new ArrayList<>();
        int maxNodes = lines.size();
        int nodesContained = knownNeighbours + 1;
        for (int i = nodeNumber + 1; i < lines.size(); i++) {
            if (i < nodeNumber + nodesContained) {
                linesNumbers.add(i);
            }
        }
        if (nodeNumber + nodesContained >= lines.size()) {
            for (int i = 0; i < nodeNumber; i++) {
                if (i == (nodeNumber + nodesContained) % maxNodes) {
                    linesNumbers.add(i);
                }
            }
        }

        for (int i : linesNumbers) {
            String[] placement = lines.get(i).split(" ");
            for (int j = 0; j < placement.length; j++) {
                if ("1".equals(placement[j])) {
                    int finalI = i;
                    int finalJ = j;
                    Optional.ofNullable(configMap.get(Integer.valueOf(i)))
                            .ifPresentOrElse(
                                    list -> list.add(
                                            new ResourceDto(finalJ, URI.create(baseAddressForRedirect + finalI + "/resource/" + finalJ))
                                    ),
                                    () -> configMap.put(finalI, Stream.of(new ResourceDto(finalJ, URI.create(baseAddressForRedirect + finalI + "/resource/" + finalJ))).collect(Collectors.toList())));
                }
            }
        }

        return configMap;
    }

//    private static Map<String, List<URI>> initConfigMap(String resourceMapPath) throws IOException {
//        Path configPath = Path.of(resourceMapPath);
//        List<String> lines = Files.readAllLines(configPath);
//        Map<String, List<URI>> configMap = new HashMap<>();
//
//        for (int i = 0; i < lines.size(); i++) {
//            String[] placement = lines.get(i).split(" ");
//            for (int j = 0; j < placement.length; j++) {
//                if ("1".equals(placement[j])) {
//                    int finalI = i;
//                    int finalJ = j;
//                    Optional.ofNullable(configMap.get(j))
//                            .ifPresentOrElse(
//                                    list -> list.add(URI.create("http://localhost:808" + finalI + "/resource/" + finalJ)),
//                                    () -> configMap.put(String.valueOf(finalJ), Collections.singletonList(URI.create("http://localhost:808" + finalI + "/resources/" + finalJ))));
//                }
//            }
//        }
//
//        return configMap;
//    }
}
