package com.example.unityapp;

public class TutorialVData {

    private String tutorialId;
    private String title;
    private String url;

    // Required default constructor for Firebase
    public TutorialVData() {
    }

    public TutorialVData(String tutorialId, String title, String url) {
        this.tutorialId = tutorialId;
        this.title = title;
        this.url = url;
    }

    public String getTutorialId() {
        return tutorialId;
    }

    public void setTutorialId(String tutorialId) {
        this.tutorialId = tutorialId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
