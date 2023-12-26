package com.example.demo.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
//TODO: SpringBoot 2.2 이상부터는 ResourceSupport -> RepresentationModel로 변경됨.
public class EventResource extends RepresentationModel<EventResource> {

	@JsonUnwrapped
	private Event event;
	public EventResource(Event event) {
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
		this.event = event;
	}

	public Event getEvent() {
		return this.event;
	}

}