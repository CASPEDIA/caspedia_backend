package com.cast.caspedia.boardgame.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class KoreanMatcher {

    private final Pattern pattern = Pattern.compile(".*[ㄱ-ㅎ|ㅏ-ㅣ|가-힣].*");

    public boolean isKorean(String str) {
        return pattern.matcher(str).matches();
    }
}
