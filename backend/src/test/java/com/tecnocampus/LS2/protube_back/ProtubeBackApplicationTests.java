package com.tecnocampus.LS2.protube_back;

import com.tecnocampus.LS2.protube_back.application.services.VideoService;
import com.tecnocampus.LS2.protube_back.application.port.in.VideoManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoPort;
import com.tecnocampus.LS2.protube_back.adapter.out.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.config.location=classpath:application-test.properties")
@AutoConfigureMockMvc
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProtubeBackApplicationTests {

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoManagementUseCase videoUseCase;

    @Autowired
    private VideoPort videoPort;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clearTestDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE videos RESTART IDENTITY");
    }

    @Test
    void wiresTheInputAndOutputPortsToTheirImplementations() {
        assertThat(videoUseCase).isSameAs(videoService);
        assertThat(videoPort).isSameAs(videoRepository);
    }

    @Test
    void servesAPersistedVideoThroughTheCompleteHexagonalFlow() throws Exception {
        jdbcTemplate.update("INSERT INTO videos (id, title, description) VALUES (?, ?, ?)",
                0L, "Vídeo de integración", "Desde PostgreSQL hasta HTTP");

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[0].title").value("Vídeo de integración"));

        mockMvc.perform(get("/api/videos/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.description").value("Desde PostgreSQL hasta HTTP"))
                .andExpect(jsonPath("$.url").value("/0.mp4"))
                .andExpect(jsonPath("$.thumbnailUrl").value("/0.webp"));
    }
}
