package com.brian.transaction_importer_spring.repository;

import com.brian.transaction_importer_spring.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByName(String name);

    Bill findById(long id);
}
