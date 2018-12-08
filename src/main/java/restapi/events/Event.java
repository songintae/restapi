package restapi.events;

import lombok.*;
import restapi.Account.Account;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private String location;
    private int price;
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();
    private boolean online;    //장소 정보가 없을경우 online
    private boolean free;      //price 가격이 없을경우 free
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private EventStatus eventType = EventStatus.DRAFT;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private Account publisher;

    public void update() {
        if (price == 0)
            free = true;
        if (location == null || location.isEmpty())
            online = true;

    }

    public boolean isPublisher(Account account) {
        return publisher.equals(account);
    }
}
