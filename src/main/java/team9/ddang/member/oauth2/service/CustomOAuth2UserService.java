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
import team9.ddang.member.entity.Role;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.member.oauth2.OAuth2Attributes;
import team9.ddang.member.oauth2.userinfo.OAuth2UserInfo;
import team9.ddang.member.repository.MemberRepository;

import java.util.Collections;
import java.util.Map;

import static team9.ddang.member.oauth2.OAuth2Attributes.getProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Provider provider = getProvider(registrationId);

        log.info("registrationId={}", registrationId);
        log.info("userNameAttributeName={}", userNameAttributeName);
        log.info("provider={}", provider);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(provider, userNameAttributeName, attributes);

        OAuth2UserInfo oauth2UserInfo = oAuth2Attributes.getOauth2UserInfo();
        String email = oauth2UserInfo.getEmail();

        // 소셜 타입과 소셜 ID 로 조회된다면 이전에 로그인을 한 유저
        // DB 에 조회되지 않는다면 Role 을 GUEST 로 설정하여 반환 -> LoginSuccessHandler 에서 회원가입으로 리다이렉트 후 추가 정보를 받는다
        Member member = memberRepository.findByEmail(email)
                .orElse(Member.builder().email(email).role(Role.GUEST).provider(provider).build());

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                attributes,
                oAuth2Attributes.getNameAttributeKey(),
                member
        );
    }
}
