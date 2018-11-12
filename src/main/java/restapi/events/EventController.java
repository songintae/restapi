package restapi.events;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventDtoValidator eventDtoValidator;

    @PostMapping("")
    public ResponseEntity create(@RequestBody @Valid EventDto eventDto, Errors errors) {

        eventDtoValidator.validate(eventDto, errors);

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        Event savedEvent = eventRepository.save(event);
        event.update();

        URI locationUri = linkTo(EventController.class).slash(savedEvent.getId()).toUri();

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(linkTo(EventController.class).withRel("events"));
        eventResource.add(linkTo(EventController.class).slash(savedEvent.getId()).withRel("update"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(locationUri).body(eventResource);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity get(@PathVariable Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(RuntimeException::new);

        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update"));
        eventResource.add(linkTo(EventController.class).withRel("events"));
        eventResource.add(new Link("/docs/index.html#resources-events-read").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }
}
