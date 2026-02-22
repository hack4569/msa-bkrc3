package com.bkrc.history.entity;

import com.bkrc.member.entity.Member;
import com.bkrc.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class History extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "item_id")
    private int itemId;

    public void setMember(Member member) {
        this.member = member;
        member.getHistories().add(this);
    }

    public static History createHistory(int bookId, Member member) {
        History history = new History();
        history.setItemId(bookId);
        history.setMember(member);
        return history;
    }
}