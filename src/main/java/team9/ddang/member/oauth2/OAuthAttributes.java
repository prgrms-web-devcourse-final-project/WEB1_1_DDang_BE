package team9.ddang.member.oauth2;

import lombok.Builder;
import lombok.Getter;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.entity.Role;
import team9.ddang.member.oauth2.userinfo.GoogleOAuth2UserInfo;
import team9.ddang.member.oauth2.userinfo.KakaoOAuth2UserInfo;
import team9.ddang.member.oauth2.userinfo.NaverOAuth2UserInfo;
import team9.ddang.member.oauth2.userinfo.OAuth2UserInfo;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey; 
    private OAuth2UserInfo oauth2UserInfo; 

    @Builder
    private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OAuthAttributes of(Provider provider,
                                     String userNameAttributeName, Map<String, Object> attributes) {

        if (provider == Provider.NAVER) {
            return ofNaver(userNameAttributeName, attributes);
        }
        if (provider == Provider.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    public Member toEntity(Provider provider, OAuth2UserInfo oauth2UserInfo) {
        return Member.builder()
                .provider(provider)
                .email(oauth2UserInfo.getEmail())
                .name(oauth2UserInfo.getName())
                .role(Role.ROLE_GUEST)
                .build();
    }
}

