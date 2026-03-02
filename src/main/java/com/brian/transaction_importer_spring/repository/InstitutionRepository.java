package com.brian.transaction_importer_spring.repository;

import com.brian.transaction_importer_spring.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    List<Institution> findAll();

    Institution findById(long id);

    List<Institution> findByName(String name);
}
