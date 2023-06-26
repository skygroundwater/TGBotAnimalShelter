package com.telegrambotanimalshelter.repositories.images;

import com.telegrambotanimalshelter.models.images.CatImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatImagesRepository extends JpaRepository<CatImage, Long> {

    void deleteCatImagesByCopiedReportId(Long copiedReportId);
}
