package team9.ddang.member.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.entity.Role;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private Member member; // Role이 USER일 때 사용
    private String email; // Role이 GUEST일 때 사용
    private Provider provider; // Role이 GUEST일 때 사용

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            Member member) {
        super(authorities, attributes, nameAttributeKey);
        this.member = member;
    }

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String email, Provider provider) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.provider = provider;
    }

    public boolean isGuest() {
        return member == null;
    }
}
