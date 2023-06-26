package com.telegrambotanimalshelter.repositories.images;

import com.telegrambotanimalshelter.models.images.DogImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogImagesRepository extends JpaRepository<DogImage, Long> {

    void deleteDogImagesByCopiedReportId(Long copiedReportId);

}
