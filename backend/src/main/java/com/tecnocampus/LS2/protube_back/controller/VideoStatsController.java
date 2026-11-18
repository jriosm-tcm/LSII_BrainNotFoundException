package com.tecnocampus.LS2.protube_back.controller;
import com.tecnocampus.LS2.protube_back.comments.VideoCommentRepository;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import com.tecnocampus.LS2.protube_back.stats.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/videos/{videoId}/stats")
public class VideoStatsController {
 private final VideoViewRepository views;
 private final VideoCommentRepository comments;
 private final VideoService videos;
 public VideoStatsController(VideoViewRepository views,VideoCommentRepository comments,VideoService videos){
 this.views=views;this.comments=comments;this.videos=videos;
 }
 private boolean exists(String id){return videos.getVideos().stream().anyMatch(v->id.equals(v.get("id")));}
 @GetMapping public ResponseEntity<?> stats(@PathVariable String videoId){
 if(!exists(videoId))return ResponseEntity.notFound().build();
 return ResponseEntity.ok(Map.of("views",views.countByVideoId(videoId),"comments",comments.countByVideoId(videoId)));
 }
 @PostMapping("/view") public ResponseEntity<?> view(@PathVariable String videoId){
 if(!exists(videoId))return ResponseEntity.notFound().build();
 views.save(new VideoView(videoId));
 return ResponseEntity.ok(Map.of("views",views.countByVideoId(videoId)));
 }
}
