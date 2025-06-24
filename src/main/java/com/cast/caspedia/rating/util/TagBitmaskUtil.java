package com.cast.caspedia.rating.util;

import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.domain.RatingTag;
import com.cast.caspedia.rating.repository.TagRepository;
import org.springframework.stereotype.Component;

@Component
public class TagBitmaskUtil {
    private final TagRepository tagRepository;

    public TagBitmaskUtil(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // 태그 비트마스크 생성
    public String getTagBitmask(Rating rating) {
        long totalTagCount = tagRepository.count(); // 전체 태그 수
        boolean[] bits = new boolean[(int) totalTagCount];

        for (RatingTag rt : rating.getRatingTags()) {
            int tagKey = rt.getTag().getTagKey();
            if (tagKey >= 1 && tagKey <= totalTagCount) {
                bits[tagKey - 1] = true;
            }
        }

        StringBuilder tagKeyBuilder = new StringBuilder();
        for (boolean bit : bits) {
            tagKeyBuilder.append(bit ? '1' : '0');
        }
        return tagKeyBuilder.toString();
    }
}
