package com.cast.caspedia.notification.domain;

public enum NotificationTypeCode {
    REPLY_ON_RATING("REPLY_ON_RATING"),
    IMPRESSED_ON_RATING("IMPRESSED_ON_RATING"),
    IMPRESSED_ON_REPLY("IMPRESSED_ON_REPLY"),
    RATING_ON_RATED_BOARDGAME("RATING_ON_RATED_BOARDGAME"),
    RATING_ON_LIKED_BOARDGAME("RATING_ON_LIKED_BOARDGAME");
    
    private final String code;
    
    NotificationTypeCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}
