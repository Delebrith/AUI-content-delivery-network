package pw.edu.aui.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class ImpairmentConfig {
    private final Integer bandwidth;
    private final Integer bufferSize;
    private final Integer delay;
    private final Integer loss;


    public ImpairmentConfig(@Value("${pw.edu.aui.impairment.bandwidth}") Integer bandwidth,
                            @Value("${pw.edu.aui.impairment.buffer-size}") Integer bufferSize,
                            @Value("${pw.edu.aui.impairment.delay}") Integer delay,
                            @Value("${pw.edu.aui.impairment.loss}") Integer loss) {
        this.bandwidth = bandwidth;
        this.bufferSize = bufferSize;
        this.delay = delay;
        this.loss = loss;
        this.configure();
    }

    private void configure() {
        try {
            Process process = Runtime.getRuntime()
                    .exec(String.format("bash /impairment.sh add eth0 %d %d %d %d", bandwidth, bufferSize, delay, loss));
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("Could not configure impairment", e);
        }
    }
}
