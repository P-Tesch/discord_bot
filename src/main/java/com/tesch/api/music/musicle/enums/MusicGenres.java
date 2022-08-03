package com.tesch.api.music.musicle.enums;

public enum MusicGenres {
    SERTANEJO ("https://youtube.com/playlist?list=PL_1G4wR0BflkZJwiCHEWHmde66uVsI_lC"), 
    POP (null),
    JPOP (null);

    private String Url;

    private MusicGenres(String Url) {
        this.Url = Url;
    }
    
    public String getUrl() {
        return this.Url;
    }
}
