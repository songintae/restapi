package restapi.events;


import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import restapi.Account.Account;
import restapi.Account.CurrentUser;

import javax.transaction.Transactional;
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
    @Transactional
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
    public ResponseEntity get(@CurrentUser Account currentUser, @PathVariable Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(RuntimeException::new);
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("events"));
        eventResource.add(new Link("/docs/index.html#resources-events-read").withRel("profile"));

        if(currentUser != null) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update"));
        }
        return ResponseEntity.ok(eventResource);
    }

    @GetMapping("")
    public ResponseEntity events(Pageable pageable
            , PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = eventRepository.findAll(pageable);
        PagedResources<EventResource> pagedResources =
                assembler.toResource(page, e -> new EventResource(e));
        return ResponseEntity.ok(pagedResources);
    }


    @PutMapping("/{eventId}")
    @Transactional
    public ResponseEntity update(@CurrentUser Account account, @PathVariable Integer eventId, @Valid @RequestBody EventDto eventDto, Errors errors) {
        eventDtoValidator.validate(eventDto, errors);

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Event findEvent = eventRepository.findById(eventId).get();
        if(!findEvent.isPublisher(account)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }


        modelMapper.map(eventDto, findEvent);
        EventResource eventResource = new EventResource(findEvent);
        eventResource.add(linkTo(EventController.class).withRel("events"));
        return ResponseEntity.ok(eventResource);
    }
}
