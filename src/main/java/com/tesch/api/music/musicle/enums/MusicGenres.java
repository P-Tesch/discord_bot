package com.tesch.api.music.musicle.enums;

public enum MusicGenres {
    SERTANEJO ("https://youtube.com/playlist?list=PL_1G4wR0BflkZJwiCHEWHmde66uVsI_lC"), 
    SERTANEJO2 ("https://www.youtube.com/playlist?list=PL8uqnewyGmBTDkIjGigR0TFahPomoHg1K"),
    POP ("https://youtube.com/playlist?list=PL8uqnewyGmBT4Uk3SiorGyqfLkffTtXdW"),
    JPOP ("https://www.youtube.com/playlist?list=PL8uqnewyGmBQiC1elDQN_vNnYinkWoF42"),
    BRASIL2000 ("https://youtube.com/playlist?list=PL8uqnewyGmBTXHrVwCzud0o5P6V-jYlGs");

    private String Url;

    private MusicGenres(String Url) {
        this.Url = Url;
    }
    
    public String getUrl() {
        return this.Url;
    }
}
