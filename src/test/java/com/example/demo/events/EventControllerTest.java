package com.example.demo.events;

import com.example.demo.common.BaseControllerTest;
import com.example.demo.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



class EventControllerTest extends BaseControllerTest {

	@Autowired
	EventRepository repository;

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
			.andExpect(jsonPath("_links.update-events").exists())
			.andDo(document("create-event"
			,	links(
						 linkWithRel("self").description("link to self")
						,linkWithRel("query-events").description("link to query")
						,linkWithRel("update-events").description("link to update")
						,linkWithRel("profile").description("link to profile")
					),
					requestHeaders(
						 headerWithName(HttpHeaders.ACCEPT).description("APPLICATION_JSON")
						,headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type")
					),
					requestFields(
						 fieldWithPath("name").description("Name of new event")
						,fieldWithPath("description").description("description of new event")
						,fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event")
						,fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event")
						,fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event")
						,fieldWithPath("endEventDateTime").description("endEventDateTime of new event")
						,fieldWithPath("basePrice").description("basePrice of new event")
						,fieldWithPath("maxPrice").description("maxPrice of new event")
						,fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
						,fieldWithPath("location").description("location of new event")
					),
					responseHeaders(
						  headerWithName(HttpHeaders.LOCATION).description("/api/events")
						 ,headerWithName(HttpHeaders.CONTENT_TYPE).description("hal_json")
					),
					responseFields(
						 fieldWithPath("id").description("id of new event")
						,fieldWithPath("name").description("Name of new event")
						,fieldWithPath("description").description("description of new event")
						,fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event")
						,fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event")
						,fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event")
						,fieldWithPath("endEventDateTime").description("endEventDateTime of new event")
						,fieldWithPath("basePrice").description("basePrice of new event")
						,fieldWithPath("maxPrice").description("maxPrice of new event")
						,fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
						,fieldWithPath("location").description("location of new event")
						,fieldWithPath("free").description("it tells if this event is free")
						,fieldWithPath("offline").description("it tells if this event is offline")
						,fieldWithPath("eventStatus").description("event status")
						,fieldWithPath("_links.self.href").description("links.self")
						,fieldWithPath("_links.query-events.href").description("links.query-events")
						,fieldWithPath("_links.update-events.href").description("links.update-events")
						,fieldWithPath("_links.profile.href").description("links.profile")
					)
			));
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
				.basePrice(10000)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("도화두손지젤시티")
				.build();

		this.mockMvc.perform(post("/api/events")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
//				.andExpect(jsonPath("$[0].objectName").exists())
//				.andExpect(jsonPath("$[0].defaultMessage").exists())
//				.andExpect(jsonPath("$[0].code").exists())
				.andExpect(jsonPath("_links.index").exists());
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

	@Test
	@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	public void queryEvent() throws Exception {

		// Given
		IntStream.range(0, 30).forEach(this::generateEvent);

		// When
		this.mockMvc.perform(get("/api/events")
						.param("page", "1")
						.param("size", "10")
						.param("sort", "name,DESC"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("page").exists())
				.andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				.andDo(document("query-events"));

	}

	@Test
	@TestDescription("기존의 이벤트 하나 조회하기")
	public void getEvent() throws Exception {

		// Given
		Event event = this.generateEvent(100);

		// When & Then
		this.mockMvc.perform(get("/api/events/{id}", event.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").exists())
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				.andDo(document("get-an-event"))
		;
	}

	@Test
	@TestDescription("없는 이벤트를 조회했을 때 404 응답받기")
	public void getEvent404() throws Exception {
		// When & Then
		this.mockMvc.perform(get("/api/event/{id}", 13123))
				.andExpect(status().isNotFound())
		;
	}
	@Test
	@TestDescription("이벤트를 정상적으로 수정하기")
	public void updateEvent() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		String eventName = "Update Event";
		eventDto.setName(eventName);

		// When & Then
		this.mockMvc.perform(put("/api/events/{id}", event.getId())
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").value(eventName))
				.andExpect(jsonPath("_links.self").exists())
		;
	}

	@Test
	@TestDescription("입력값이 비어있는 경우에 이벤트 수정 실패")
	public void updateEvent400_Empty() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = new EventDto();

		// When & Then
		this.mockMvc.perform(put("/api/events/{id}", event.getId())
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}

	@Test
	@TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
	public void updateEvent400_Wrong() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);

		// When & Then
		this.mockMvc.perform(put("/api/events/{id}", event.getId())
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}

	@Test
	@TestDescription("존재하지 않는 이벤트 수정 실패")
	public void updateEvent404() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);

		// When & Then
		this.mockMvc.perform(put("/api/events/99999", event.getId())
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isNotFound())
		;
	}


	private Event generateEvent(int index) {
		Event event = Event.builder()
				.name("event " + index )
				.description("test event")
				.beginEnrollmentDateTime(LocalDateTime.of(2023, 10, 18, 21, 28))
				.closeEnrollmentDateTime(LocalDateTime.of(2023, 11, 18, 21, 28))
				.beginEventDateTime(LocalDateTime.of(2023, 12, 15, 21, 28))
				.endEventDateTime(LocalDateTime.of(2023, 12, 16, 21, 28))
				.basePrice(0)
				.maxPrice(0)
				.limitOfEnrollment(100)
				.location("Inchon City")
				.free(false)
				.offline(true)
				.eventStatus(EventStatus.DRAFT)
				.build();
		this.repository.save(event);
		return event;
	}


}