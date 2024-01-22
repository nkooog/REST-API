package com.example.demo.common;

import com.example.demo.events.EventResource;
import com.example.demo.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorResource extends RepresentationModel<EventResource> {

    public ErrorResource(Errors errors, Link... links) {
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }

}
