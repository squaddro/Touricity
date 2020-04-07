package com.squadro.touricity.message.types;

public class Comment{
    private String commentId;
    private String commentDesc;

    public Comment() {}

    public Comment(String commentId, String commentDesc) {
        this.commentId = commentId;
        this.commentDesc = commentDesc;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getCommentDesc() {
        return commentDesc;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setCommentDesc(String commentDesc) {
        this.commentDesc = commentDesc;
    }
}
