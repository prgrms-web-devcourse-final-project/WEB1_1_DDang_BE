package team9.ddang.member.oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.oauth2.OAuthAttributes;
import team9.ddang.member.repository.MemberRepository;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//
//        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");
//
//        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        Provider provider = getProvider(registrationId);
//        String userNameAttributeName = userRequest.getClientRegistration()
//                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        OAuthAttributes extractAttributes = OAuthAttributes.of(provider, userNameAttributeName, attributes);
//
//        Member createdMember = getMember(extractAttributes, provider);
//
//        return new CustomOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority(createdMember.getRole().toString())),
//                attributes,
//                extractAttributes.getNameAttributeKey(),
//                createdMember
//        );
//
//    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = getProvider(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("attributes: {}", attributes);

        OAuthAttributes extractAttributes = OAuthAttributes.of(provider, userNameAttributeName, attributes);
        log.info("extractAttributes: {}", extractAttributes);

        // 이메일을 통해 Member 조회
        String email = extractAttributes.getOauth2UserInfo().getEmail();
        Member findMember = memberRepository.findByEmail(email).orElse(null);

        if (findMember == null) {
            // GUEST 상태에서만 이메일과 provider를 가진 CustomOAuth2User 생성
            log.info("GUEST 상태의 OAuth2 사용자 생성 - 이메일: {}", email);
            return new CustomOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_GUEST")),
                    attributes,
                    extractAttributes.getNameAttributeKey(),
                    email,
                    provider
            );
        }

        // 이미 회원가입이 완료된 사용자 (ROLE_USER 등)
        log.info("USER 상태의 OAuth2 사용자 로드 - 이메일: {}", findMember.getEmail());
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(findMember.getRole().toString())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                findMember
        );
    }

    private Provider getProvider(String registrationId) {
        if(NAVER.equals(registrationId)) {
            return Provider.NAVER;
        }
        if(KAKAO.equals(registrationId)) {
            return Provider.KAKAO;
        }
        return Provider.GOOGLE;
    }

    private Member getMember(OAuthAttributes attributes, Provider provider) {
        String email = attributes.getOauth2UserInfo().getEmail();
        Member findMember = memberRepository.findByEmail(email).orElse(null);

        if(findMember == null) {
            return saveMember(attributes, provider);
        }
        return findMember;
    }

    private Member saveMember(OAuthAttributes attributes, Provider provider) {
//        Member createdMember = attributes.toEntity(provider, attributes.getOauth2UserInfo());
//        return memberRepository.save(createdMember);
        log.info("GUEST 상태의 회원 생성 - 이메일: {}", attributes.getOauth2UserInfo().getEmail());
        return attributes.toEntity(provider, attributes.getOauth2UserInfo());
    }
}
