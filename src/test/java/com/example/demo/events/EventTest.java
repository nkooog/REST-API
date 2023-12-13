package com.example.demo.events;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Inflean Spring REST API")
                .description("REST API develoment with Spring").build();
        Assertions.assertNotNull(event);
    }

    @Test
    public void javaBean() {
        Event event = new Event();
        String name="Spring";
        event.setName("Spring");
        event.setDescription("REST API");

        Assertions.assertEquals(name, event.getName());
    }

}