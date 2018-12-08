package restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
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
import org.springframework.web.context.WebApplicationContext;
import restapi.Account.Account;
import restapi.Account.AccountRepository;
import restapi.common.BasicAuthConfig;
import restapi.common.RestDocsConfig;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({RestDocsConfig.class, BasicAuthConfig.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext wac;


    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AccountRepository accountRepository;

    @After
    public void tearDown() throws Exception {
        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }

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
                .andExpect(jsonPath("_links.events").hasJsonPath())
                .andExpect(jsonPath("_links.profile").hasJsonPath())
                .andDo(print())
                .andDo(document("read-events",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("events").description("link to events"),
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


    @Test
    public void Event_리스트_조회_테스트() throws Exception {
        // given
        IntStream.range(0, 20).forEach(idx -> {
            eventRepository.save(Event.builder().name("Event " + idx).build());
        });

        // when & then
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").hasJsonPath())
                .andExpect(jsonPath("page.totalElements").value(20))
                .andExpect(jsonPath("page.size").value(10))
                .andExpect(jsonPath("page.totalPages").value(2))
                .andExpect(jsonPath("page.number").value(0))
                .andExpect(jsonPath("_links").hasJsonPath())
                .andDo(print());
    }


    @Test
    public void Event_업데이트_테스트_FORBIDEN() throws Exception {
        // given
        Event savedEvent = getEvent();

        EventDto eventDto = EventDto.builder()
                .name("Spring rest api 변경 테스트")
                .description("변경된 내용이 나와야한다.")
                .location("japan")
                .date(LocalDateTime.of(2019, 10, 10, 9, 30))
                .build();

        //when & then
        mockMvc.perform(put("/api/events/{eventId}", savedEvent.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void Event_업데이트() throws Exception {
        //given
        Event savedEvent = getEvent();

        EventDto eventDto = EventDto.builder()
                .name("Spring rest api 변경 테스트")
                .description("변경된 내용이 나와야한다.")
                .location("japan")
                .date(LocalDateTime.of(2019, 10, 10, 9, 30))
                .build();

        Account publisher = savedEvent.getPublisher();

        //when & then
        mockMvc.perform(put("/api/events/{eventId}", savedEvent.getId())
                .with(httpBasic(publisher.getEmail(), publisher.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").hasJsonPath())
                .andExpect(jsonPath("_links.events").hasJsonPath());

    }


    private Event getEvent() {
        Event event = Event.builder()
                .name("Spring rest api")
                .description("REST 다운 REST API 작성")
                .location("korea")
                .date(LocalDateTime.of(2019, 11, 10, 9, 30))
                .price(0)
                .publisher(getAccount())
                .build();

        return eventRepository.save(event);
    }


    private Account getAccount() {
        Account account = Account.builder()
                .email("kookooku@woowahan.com")
                .password("1234")
                .build();
        account.getRoles().add(Account.UserRole.USER);

        return accountRepository.save(account);
    }

}