package restapi.events;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @PostMapping("")
    public ResponseEntity create(@RequestBody Event event) {
        Event savedEvent = eventRepository.save(event);
        URI uri = linkTo(EventController.class).slash(savedEvent.getId()).toUri();
        return ResponseEntity.created(uri).body(event);
    }
}
