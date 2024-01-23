package com.example.demo.events;

import com.example.demo.common.ErrorResource;
import jakarta.validation.Valid;
import org.hibernate.event.spi.EventSource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	private final EventValidator eventValidator;

	public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator = eventValidator;
	}


	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) { //@Valid 바로 오른쪽에 있는 Errors 객체에 에러를 던져준다.
		// Errors 는 자바빈 스펙을 준수하고 있지 않다 Event는 자바 빈 스펙을 준수하여 시리얼라이즈가 자동으로 진행된다.
		if (errors.hasErrors()) {
			return getErrorResourceResponseEntity(errors);
		}

		eventValidator.validate(eventDto, errors);
		if (errors.hasErrors()) {
			return getErrorResourceResponseEntity(errors);
		}

        /*Event event = Event.builder()
                .name(eventDto.getName())
                .build();*/
		// ModelMapper 라이브러리 사용하여 위에 과정생략함
		Event event = modelMapper.map(eventDto, Event.class);
		event.update();
		Event newEvent = this.eventRepository.save(event);

		EventResource eventResource = new EventResource(newEvent);
		WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());

        URI createUri = selfLinkBuilder.toUri();

		eventResource.add(linkTo(EventController.class).withRel("query-events"));
		eventResource.add(selfLinkBuilder.withRel("update-events"));
		eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));

		return ResponseEntity.created(createUri).body(eventResource);
		// created() 보낸땐 항상 URI 가 필요하다
	}

	private static ResponseEntity<ErrorResource> getErrorResourceResponseEntity(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorResource(errors));
	}

	@GetMapping
	public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
		Page<Event> page = this.eventRepository.findAll(pageable);
		var pagedResources = assembler.toModel(page, EventResource::new);
		pagedResources.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
		return ResponseEntity.ok(pagedResources);
	}

	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);

		if(optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Event event = optionalEvent.get();
		EventResource eventResource = new EventResource(event);
		eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
		return ResponseEntity.ok(eventResource);

	}

	@PutMapping("{id}")
	public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto ,Errors erros) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);

		if(optionalEvent.isEmpty()) return ResponseEntity.notFound().build();
		if(erros.hasErrors()) return ResponseEntity.badRequest().build();

		this.eventValidator.validate(eventDto, erros);

		if(erros.hasErrors()) return ResponseEntity.badRequest().build();

		Event existingEvent = optionalEvent.get();
		// 어디에서 어디로
		this.modelMapper.map(eventDto, existingEvent);
		Event saveEvent = this.eventRepository.save(existingEvent);
		EventResource eventResource = new EventResource(saveEvent);
		eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
		return ResponseEntity.ok(eventResource);
	}

}
