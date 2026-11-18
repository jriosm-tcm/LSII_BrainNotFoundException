package com.tecnocampus.LS2.protube_back.comments;
import jakarta.persistence.*;
@Entity
@Table(name="comment_likes", uniqueConstraints=@UniqueConstraint(columnNames={"comment_id","username"}))
public class CommentLike {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="comment_id",nullable=false) private Long commentId;
 @Column(nullable=false) private String username;
 protected CommentLike() {}
 public CommentLike(Long commentId,String username){this.commentId=commentId;this.username=username;}
}
