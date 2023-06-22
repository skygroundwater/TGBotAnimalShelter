package com.telegrambotanimalshelter.repositories.images;

import com.telegrambotanimalshelter.models.images.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinaryContentRepository extends JpaRepository<BinaryContent, Long> {
}
