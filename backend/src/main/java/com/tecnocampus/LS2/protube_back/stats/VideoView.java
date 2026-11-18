package com.tecnocampus.LS2.protube_back.stats;
import jakarta.persistence.*;
@Entity
@Table(name="video_views")
public class VideoView {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String videoId;
 protected VideoView() {}
 public VideoView(String videoId){this.videoId=videoId;}
}
