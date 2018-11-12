package restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import restapi.common.RestDocsConfig;

import java.time.LocalDateTime;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(RestDocsConfig.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private EventRepository eventRepository;

    @Test
    public void Event_생성_테스트() throws Exception {
        // given
        EventDto event = EventDto.builder()
                .name("Spring rest api")
                .description("REST 다운 REST API 작성")
                .location("korea")
                .date(LocalDateTime.of(2019, 11, 10, 9, 30))
                .price(0)
                .build();

        // when & then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andDo(document("create-events",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("events").description("link to events"),
                                linkWithRel("update").description("link to update"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Event 이름"),
                                fieldWithPath("description").description("Event 설명"),
                                fieldWithPath("location").description("Event 지역"),
                                fieldWithPath("date").description("Event 일자"),
                                fieldWithPath("price").description("Event 가격")),
                        relaxedResponseFields(
                                fieldWithPath("name").description("Event 이름"),
                                fieldWithPath("description").description("Event 설명"),
                                fieldWithPath("location").description("Event 지역"),
                                fieldWithPath("date").description("Event 일자"),
                                fieldWithPath("price").description("Event 가격"),
                                fieldWithPath("free").description("Event 무료 여부"),
                                fieldWithPath("online").description("Event 온라인 여부")))
                )
                .andExpect(header().exists("Location"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value("Spring rest api"))
                .andExpect(jsonPath("_links.self").hasJsonPath())
                .andExpect(jsonPath("_links.events").hasJsonPath())
                .andExpect(jsonPath("_links.update").hasJsonPath())
                .andExpect(jsonPath("_links.profile").hasJsonPath())
        ;
    }

    @Test
    public void Event_생성_검증_입력필드_기본값_테스트() throws Exception {
        // given
        EventDto event = EventDto.builder()
                .name("")
                .description("")
                .location("korea")
                .date(LocalDateTime.of(2018, 11, 10, 9, 30))
                .price(0)
                .build();

        // when & then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andDo(document("errors",
                        responseFields(
                                fieldWithPath("[]").description("An Array of Errors"))
                                .andWithPrefix("[].",
                                        fieldWithPath("field").description("Error Field 명"),
                                        fieldWithPath("objectName").description("Error objectName 명"),
                                        fieldWithPath("defaultMessage").description("Error 메시지"),
                                        fieldWithPath("rejectedValue").description("Error 요청 값")
                                )

                ))
                .andExpect(status().isBadRequest())

        ;

    }

    @Test
    public void Event_생성_검증_입력필드_비즈니스_오류_테스트() throws Exception {
        // given
        EventDto event = EventDto.builder()
                .name("Spring rest api")
                .description("REST 다운 REST API 작성")
                .location("korea")
                .date(LocalDateTime.of(2018, 11, 10, 9, 30))
                .price(0)
                .build();

        // when & then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void Event_조회_테스트() throws Exception {
        // given
        Event event = Event.builder()
                .name("Spring rest api")
                .description("REST 다운 REST API 작성")
                .location("korea")
                .date(LocalDateTime.of(2019, 11, 10, 9, 30))
                .price(0)
                .build();

        Event savedEvent = eventRepository.save(event);

        // when & then
        mockMvc.perform(get("/api/events/{eventId}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(savedEvent.getId()))
                .andExpect(jsonPath("name").value(event.getName()))
                .andExpect(jsonPath("location").value(event.getLocation()))
                .andExpect(jsonPath("_links.update").hasJsonPath())
                .andExpect(jsonPath("_links.events").hasJsonPath())
                .andExpect(jsonPath("_links.profile").hasJsonPath())
                .andDo(document("read-events",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("events").description("link to events"),
                                linkWithRel("update").description("link to update"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        pathParameters(
                          parameterWithName("eventId").description("조회 대상 Event Id")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("name").description("Event 이름"),
                                fieldWithPath("description").description("Event 설명"),
                                fieldWithPath("location").description("Event 지역"),
                                fieldWithPath("date").description("Event 일자"),
                                fieldWithPath("price").description("Event 가격"),
                                fieldWithPath("free").description("Event 무료 여부"),
                                fieldWithPath("online").description("Event 온라인 여부")))
                )
                ;
    }
}