package restapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventDtoValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (LocalDateTime.now().isAfter(eventDto.getDate())) {
            errors.rejectValue("date", "wrong.value", "date는 현재시간보다 이전일 수 없습니다.");
        }
    }
}
