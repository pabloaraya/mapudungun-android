package com.pabloaraya.mapudungun;

import java.util.List;

/**
 * Created by pablo on 8/26/15.
 */
public class ResponseTranslate {

    private int status_code;
    private String status;
    private String message;
    private List<String> words;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
