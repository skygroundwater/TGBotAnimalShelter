package com.telegrambotanimalshelter.models.images;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class AppImage {

    private String telegramFileId;

    private Long fileSize;

    @Column(name = "file_as_array_of_bytes")
    private byte[] fileAsArrayOfBytes;

    private boolean isPreview;

}
