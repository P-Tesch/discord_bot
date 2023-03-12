package com.tesch.games.musicle.enums;

public enum MusicGenres {
    SERTANEJO ("https://youtube.com/playlist?list=PL_1G4wR0BflkZJwiCHEWHmde66uVsI_lC"), 
    POP ("https://youtube.com/playlist?list=PL8uqnewyGmBT4Uk3SiorGyqfLkffTtXdW"),
    JPOP ("https://www.youtube.com/playlist?list=PL8uqnewyGmBQiC1elDQN_vNnYinkWoF42"),
    BRASIL2000 ("https://youtube.com/playlist?list=PL8uqnewyGmBTXHrVwCzud0o5P6V-jYlGs"),
    HOLOLIVE ("https://youtube.com/playlist?list=PL_1G4wR0BflmDyUnNrWu50ZmkPDUg_XQ5");

    private String Url;

    private MusicGenres(String Url) {
        this.Url = Url;
    }
    
    public String getUrl() {
        return this.Url;
    }
}
