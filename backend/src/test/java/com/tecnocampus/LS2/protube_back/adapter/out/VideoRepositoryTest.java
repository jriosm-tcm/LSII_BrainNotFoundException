package com.tecnocampus.LS2.protube_back.adapter.out;

import com.tecnocampus.LS2.protube_back.domain.model.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@Testcontainers
class VideoRepositoryTest {

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16")
            .withInitScript("init.sql");

    private JdbcTemplate jdbcTemplate;
    private VideoRepository repository;

    @BeforeEach
    void setUp() {
        var dataSource = new DriverManagerDataSource(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword()
        );
        jdbcTemplate = new JdbcTemplate(dataSource);
        repository = new VideoRepository(JdbcClient.create(dataSource));
        // This database belongs exclusively to this test container.
        jdbcTemplate.execute("TRUNCATE TABLE videos RESTART IDENTITY");
    }

    @Test
    void returnsAnEmptyListWhenTheTableIsEmpty() {
        assertThat(repository.getVideos()).isEmpty();
    }

    @Test
    void mapsEveryCatalogRowIncludingMediaReferences() {
        insertVideo(0L, "Vídeo cero", "Descripción con acentos");
        insertVideo(7L, "Vídeo siete", "Otra descripción");

        assertThat(repository.getVideos())
                .extracting(Video::getId, Video::getTitle, Video::getDescription,
                        Video::getUrl, Video::getThumbnailUrl)
                .containsExactlyInAnyOrder(
                        tuple(0L, "Vídeo cero", "Descripción con acentos", "/0.mp4", "/0.webp"),
                        tuple(7L, "Vídeo siete", "Otra descripción", "/7.mp4", "/7.webp")
                );
    }

    @Test
    void findsOnlyTheRequestedVideoAndSupportsIdentifierZero() {
        insertVideo(0L, "Vídeo cero", "Primero");
        insertVideo(7L, "Vídeo siete", "Segundo");

        Video video = repository.getVideoById(0L).orElseThrow();

        assertThat(video)
                .extracting(Video::getId, Video::getTitle, Video::getDescription,
                        Video::getUrl, Video::getThumbnailUrl)
                .containsExactly(0L, "Vídeo cero", "Primero", "/0.mp4", "/0.webp");
    }

    @Test
    void returnsAnEmptyOptionalWhenTheIdentifierDoesNotExist() {
        insertVideo(0L, "Otro vídeo", "Existe, pero tiene otro ID");

        assertThat(repository.getVideoById(99L)).isEmpty();
    }

    @Test
    void preservesAnOptionalDescription() {
        insertVideo(3L, "Sin descripción", null);

        Video video = repository.getVideoById(3L).orElseThrow();

        assertThat(video.getTitle()).isEqualTo("Sin descripción");
        assertThat(video.getDescription()).isNull();
    }

    private void insertVideo(Long id, String title, String description) {
        jdbcTemplate.update("INSERT INTO videos (id, title, description) VALUES (?, ?, ?)",
                id, title, description);
    }
}
