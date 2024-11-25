package team9.ddang.walk.service;

import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.request.DecisionWalkServiceRequest;
import team9.ddang.walk.service.request.ProposalWalkServiceRequest;
import team9.ddang.walk.service.request.StartWalkServiceRequest;

public interface WalkLocationService {

    void startWalk(String email, StartWalkServiceRequest startWalkServiceRequest);

    void proposalWalk(Member member, ProposalWalkServiceRequest proposalWalkServiceRequest);

    void decisionWalk(Member member, DecisionWalkServiceRequest service);
}
