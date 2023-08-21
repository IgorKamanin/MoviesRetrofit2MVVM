package com.example.moviesretrofit2mvvm.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "comments")
public class Comment {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String author;
    private String reaction;

    public Comment(int id, String author, String reaction) {
        this.id = id;
        this.author = author;
        this.reaction = reaction;
    }

    @Ignore
    public Comment(String author, String reaction) {
        this.author = author;
        this.reaction = reaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
