package com.squadro.touricity.message.types;

public class Like {

    private String like_id;
    private String account_id;
    private int score;

    public Like(){

    }

    public Like(String like_id, String account_id, int score){
        this.account_id = account_id;
        this.like_id = like_id;
        this.score = score;
    }

    public String getLike_id() {
        return like_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public int getScore() {
        return score;
    }

    public void setLike_id(String like_id) {
        this.like_id = like_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
