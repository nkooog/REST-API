package com.example.demo.events;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

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

    @Test
    public void testOffline() {
        Event event = Event.builder()
                .location("강남역")
                .build();
        event.update();

        Assertions.assertTrue(event.isOffline());
    }

}