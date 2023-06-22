package com.telegrambotanimalshelter.models.images;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class Image {

    @Column(name = "name")
    private String name;

    @Column(name = "original_name")
    private String originalFileName;

    @Column(name = "size")
    private Long size;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "is_preview")
    private boolean isPreviewImage;

    @Lob
    private byte[] bytes;
}


