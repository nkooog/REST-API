package com.example.demo.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
//@WebMvcTest
class EventControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("정상적으로 이벤트를 생성하는 테스트")
	public void createEvent() throws Exception {

		EventDto event = EventDto.builder()
				.name("Spring")
				.description("REST API Develoment with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2023, 10, 18, 21, 28))
				.closeEnrollmentDateTime(LocalDateTime.of(2023, 11, 18, 21, 28))
				.beginEventDateTime(LocalDateTime.of(2023, 12, 15, 21, 28))
				.endEventDateTime(LocalDateTime.of(2023, 12, 16, 21, 28))
				.basePrice(0)
				.maxPrice(0)
				.limitOfEnrollment(100)
				.location("Inchon City")
				.build();

		mockMvc.perform(post("/api/events")
						.contentType(MediaType.APPLICATION_JSON) // 이 요청 본문에 json을 담아서 보내주고 있다
						.accept(MediaTypes.HAL_JSON_VALUE) // 원하는 응답
						.content(objectMapper.writeValueAsString(event)) // Response Body에 작성
				)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(header().exists(HttpHeaders.LOCATION)) // ??
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
				.andExpect(jsonPath("free").exists())
				.andExpect(jsonPath("offline").exists())
				.andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.DRAFT)))
				.andExpect(jsonPath("_links.query-events").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.update-events").exists());
	}

	@Test
	public void createEvent_Bad_Request() throws Exception {

		Event event = Event.builder()
				.id(100)
				.name("Spring")
				.description("REST API Develoment with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2023, 12, 12, 22, 57, 22))
				.closeEnrollmentDateTime(LocalDateTime.of(2023, 12, 12, 22, 57, 22))
				.endEventDateTime(LocalDateTime.of(2023, 12, 12, 22, 57, 22))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("도화두손지젤시티")
				.free(true)
				.offline(true)
				.eventStatus(EventStatus.PUBLISHED)
				.build();

		mockMvc.perform(post("/api/events")
						.contentType(MediaType.APPLICATION_JSON) // 이 요청 본문에 json을 담아서 보내주고 있다
						.accept(MediaTypes.HAL_JSON_VALUE) // 원하는 응답
						.content(objectMapper.writeValueAsString(event)) // Response Body에 작성
				)
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventDto eventDto = EventDto.builder()
				.name("Spring")
				.description("REST API Develoment with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2023, 12, 11, 22, 57, 22))
				.closeEnrollmentDateTime(LocalDateTime.of(2023, 12, 12, 22, 57, 22))
				.beginEventDateTime(LocalDateTime.of(2023, 12, 13, 22, 57, 22))
				.endEventDateTime(LocalDateTime.of(2023, 12, 14, 22, 57, 22))
				.basePrice(1)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("도화두손지젤시티")
				.build();

		this.mockMvc.perform(post("/api/events")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print());
//				.andExpect(status().isBadRequest())
//				.andExpect(jsonPath("$[0].objectName").exists())
//				.andExpect(jsonPath("$[0].defaultMessage").exists())
//				.andExpect(jsonPath("$[0].code").exists());
	}

	@Test
	public void testFree() {
		// Given
		Event event = Event.builder()
				.basePrice(0)
				.maxPrice(0)
				.build();
		// When
		event.update();

		// Then
		Assertions.assertTrue(event.isFree());

		// Given
		event = event.builder()
				.basePrice(0)
				.maxPrice(20000)
				.build();
		// When
		event.update();
		// Then
		Assertions.assertFalse(event.isFree());
	}

}