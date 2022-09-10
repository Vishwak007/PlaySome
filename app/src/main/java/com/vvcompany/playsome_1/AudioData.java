package com.vvcompany.playsome_1;

public class AudioData {
    String album;
    String artist;
    String id;
    String path;
    String duration;
    String title;

    public AudioData(String album, String artist, String id, String path, String duration, String title) {
        this.album = album;
        this.artist = artist;
        this.id = id;
        this.path = path;
        this.duration = duration;
        this.title = title;
    }

    public AudioData(){

    };

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }






}
