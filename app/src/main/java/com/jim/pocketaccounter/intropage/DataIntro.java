package com.jim.pocketaccounter.intropage;

/**
 * Created by DEV on 21.06.2016.
 */

public class DataIntro {
    private String intoTitle;
    private String contentText;
    private int imageRes;


    public DataIntro(String intoTitle, String contentText, int imageRes) {
        this.intoTitle = intoTitle;
        this.contentText = contentText;
        this.imageRes = imageRes;
    }

    public String getIntoTitle() {
        return intoTitle;
    }

    public void setIntoTitle(String intoTitle) {
        this.intoTitle = intoTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }
}
