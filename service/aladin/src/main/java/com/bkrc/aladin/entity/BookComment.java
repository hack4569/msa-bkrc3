package com.bkrc.aladin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_comment")
public class BookComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCommentId;

    @Lob
    private String comment;

    private String type;

    private Integer aladinBookItemId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private AladinBook aladinBook;

    public static BookComment create(String comment, String type) {
        BookComment bookComment = new BookComment();
        bookComment.setComment(comment);
        bookComment.setType(type);
        return bookComment;
    }
}
