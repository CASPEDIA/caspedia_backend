package com.cast.caspedia.admin.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.stereotype.Service;

@Service
public class NanoidGeneratorService {
    public String generateNanoid() {
        return NanoIdUtils.randomNanoId();
    }
}
