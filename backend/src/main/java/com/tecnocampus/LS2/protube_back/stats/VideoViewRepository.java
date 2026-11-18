package com.tecnocampus.LS2.protube_back.stats;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VideoViewRepository extends JpaRepository<VideoView,Long> {
 long countByVideoId(String videoId);
}
