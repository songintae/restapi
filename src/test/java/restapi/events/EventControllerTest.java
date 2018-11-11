package restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void Event_생성_테스트() throws Exception{
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value("Spring rest api"))
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
}