package com.example.demo.events;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


public class EventTest {

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
		String name = "Spring";
		event.setName("Spring");
		event.setDescription("REST API");

		Assertions.assertEquals(name, event.getName());
	}

	@ParameterizedTest
	@MethodSource("parameterFOrTestOffline")
	public void testOffline(String location, boolean isOffline) {
		Event event = Event.builder()
				.location(location)
				.build();
		event.update();

		Assertions.assertEquals(event.isOffline(), isOffline);
	}

	@ParameterizedTest
	@MethodSource("paramsForTestFree")
	public void testFree(int basePrice, int maxPrice) {
		Event event = Event.builder()
				.basePrice(basePrice)
				.maxPrice(maxPrice)
				.build();

		event.update();
	}

	private static Object[] paramsForTestFree() {
		return new Object[][]{
				new Object[]{0, 0}
				, new Object[]{100, 0}
				, new Object[]{0, 100}
				, new Object[]{500, 200}
		};
	}

	private static Object[] parameterFOrTestOffline() {
		return new Object[]{
				new Object[]{"강남", true}
				, new Object[]{null, false}
				, new Object[]{"       ", false}
		};
	}

	@Test
	public void myMethod() {

	}

}