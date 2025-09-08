package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 이 클래스를 상속받는 자식 클래스에게 필드만 매핑하도록 설정
@EntityListeners(AuditingEntityListener.class) // Auditing 기능을 엔티티에 적용
public abstract class BaseTimeEntity {

    @CreatedDate // 엔티티가 생성될 때 시간이 자동으로 저장됩니다.
    @Column(name = "created_at", nullable = false, updatable = false) // 생성 시간은 업데이트되지 않도록 설정
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 시간이 자동으로 저장됩니다.
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}