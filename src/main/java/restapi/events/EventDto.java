package restapi.events;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class EventDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotEmpty
    private String location;
    private int price;
    private LocalDateTime date = LocalDateTime.now();
}
