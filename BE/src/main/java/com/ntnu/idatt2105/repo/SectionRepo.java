package com.ntnu.idatt2105.repo;

import com.ntnu.idatt2105.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepo extends JpaRepository<Section, Long> {
    Section findById(long id);
}
