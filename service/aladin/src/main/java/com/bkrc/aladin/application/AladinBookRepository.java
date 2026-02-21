package com.bkrc.aladin.application;

import com.bkrc.aladin.entity.AladinBook;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AladinBookRepository extends JpaRepository<AladinBook, Integer> {

    List<AladinBook> findAll(Sort sort);
}
