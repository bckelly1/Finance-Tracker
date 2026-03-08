package com.brian.transaction_importer_spring.repository;

import com.brian.transaction_importer_spring.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByName(String name);

    Budget findById(long id);

}
