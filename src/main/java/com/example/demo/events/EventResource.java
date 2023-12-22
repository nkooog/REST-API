package com.example.demo.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

//TODO: SpringBoot 2.2 이상부터는 ResourceSupport -> RepresentationModel로 변경됨.
public class EventResource extends RepresentationModel<EventResource> {

	@JsonUnwrapped
	private final Event event;
	public EventResource( Event event ) {
		this.event = event;
	}

}