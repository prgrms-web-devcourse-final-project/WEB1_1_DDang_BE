package team9.ddang.walk.controller.request;

import jakarta.validation.constraints.NotNull;
import team9.ddang.walk.service.request.ProposalWalkServiceRequest;

public record ProposalWalkRequest(
        @NotNull(message = "상대 이메일은 입력해주셔야 해요")
        String otherMemberEmail,
        String comment

) {
    public ProposalWalkServiceRequest toService() {
        return new ProposalWalkServiceRequest(otherMemberEmail, comment);
    }
}
