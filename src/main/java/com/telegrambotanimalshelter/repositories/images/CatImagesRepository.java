package com.telegrambotanimalshelter.repositories.images;

import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.reports.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatImagesRepository<R extends Report> extends JpaRepository<CatImage, Long> {


}
