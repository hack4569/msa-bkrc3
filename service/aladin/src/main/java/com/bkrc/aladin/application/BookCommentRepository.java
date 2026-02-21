package com.bkrc.aladin.application;

import com.bkrc.aladin.entity.BookComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCommentRepository extends JpaRepository<BookComment, Long> {
    List<BookComment> findBookCommentsByAladinBookItemId(int aladinItemId);
}
