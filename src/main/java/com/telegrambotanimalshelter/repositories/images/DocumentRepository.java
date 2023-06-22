package com.telegrambotanimalshelter.repositories.images;

import com.telegrambotanimalshelter.models.images.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<AppDocument, Long> {
}
