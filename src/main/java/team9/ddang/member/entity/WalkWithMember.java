package team9.ddang.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalkWithMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkWithMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Builder
    private WalkWithMember(Long walkWithMemberId, Member sender, Member receiver) {
        this.walkWithMemberId = walkWithMemberId;
        this.sender = sender;
        this.receiver = receiver;
    }
}
