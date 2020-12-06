package pw.edu.aui.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Resource {
    @Id
    Integer id;

    @Lob
    byte[] content;
}
