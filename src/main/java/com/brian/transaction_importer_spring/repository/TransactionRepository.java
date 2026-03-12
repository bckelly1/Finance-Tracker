package com.brian.transaction_importer_spring.repository;

import com.brian.transaction_importer_spring.entity.Category;
import com.brian.transaction_importer_spring.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findAll();

    List<Transaction> findTop100ByOrderByIdDesc();

    Transaction findById(long id);

    @Query("FROM Transaction t where t.category = :category")
    List<Transaction> findTransactionByCategory(@Param("category") Category category);

    @Query("FROM Transaction t where t.category = :category AND t.date > :start AND t.date <= :end")
    List<Transaction> findTransactionsByCategoryBetweenDates(@Param("category") Category category, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Transaction> findTransactionsByDescriptionAfter(String descriptionAfter, LocalDateTime start);
}
