package com.tecnocampus.LS2.protube_back.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {
    List<VideoComment> findByVideoIdOrderByCreatedAtAsc(String videoId);
}
