package team9.ddang.walk.service;

import org.springframework.web.multipart.MultipartFile;
import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.request.walk.CompleteWalkServiceRequest;
import team9.ddang.walk.service.response.walk.CompleteWalkResponse;

import java.io.IOException;

public interface WalkService {

    CompleteWalkResponse completeWalk(Member member, CompleteWalkServiceRequest completeWalkServiceRequest, MultipartFile walkImgFile) throws IOException;
}
