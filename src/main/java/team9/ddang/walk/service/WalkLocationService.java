package team9.ddang.walk.service;

import team9.ddang.walk.service.request.walk.DecisionWalkServiceRequest;
import team9.ddang.walk.service.request.walk.ProposalWalkServiceRequest;
import team9.ddang.walk.service.request.walk.StartWalkServiceRequest;

public interface WalkLocationService {

    void startWalk(String email, StartWalkServiceRequest startWalkServiceRequest);

    void proposalWalk(String email, ProposalWalkServiceRequest proposalWalkServiceRequest);

    void decisionWalk(String email, DecisionWalkServiceRequest service);

    void startWalkWith(String email, StartWalkServiceRequest service);
}
