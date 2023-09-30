package comp5216.sydney.edu.au.mentalhealth.entities;

import com.google.firebase.Timestamp;

public class PostComment {
    private String commentId;
    private String postId;
    private String userId;
    public PostComment() {
        // 空构造函数，必须存在
    }

    public PostComment(String commentId, String postId, String userId, String text, Timestamp timestamp) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
    }

    private String text;
    private Timestamp timestamp;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}