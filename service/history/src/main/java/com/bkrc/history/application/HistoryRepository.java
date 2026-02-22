package com.bkrc.history.application;

import com.bkrc.history.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findHistoriesByMemberId(long memberId);
    int deleteHistoriesByMemberId(long memberId);
}
