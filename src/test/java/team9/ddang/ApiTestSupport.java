package team9.ddang;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import team9.ddang.family.controller.FamilyController;
import team9.ddang.family.service.FamilyService;
import team9.ddang.member.controller.FriendController;
import team9.ddang.member.controller.MainController;
import team9.ddang.member.controller.MemberController;
import team9.ddang.member.jwt.filter.JwtAuthenticationProcessingFilter;
import team9.ddang.member.jwt.service.JwtService;
import team9.ddang.member.service.CookieService;
import team9.ddang.member.service.FriendService;
import team9.ddang.member.service.MainService;
import team9.ddang.member.service.MemberService;

@WebMvcTest(controllers = {
        MemberController.class,
        FamilyController.class,
        FriendController.class,
        MainController.class
})
@AutoConfigureMockMvc(addFilters = false)
public abstract class ApiTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected CookieService cookieService;

    @MockBean
    protected JwtService jwtService;

    @MockBean
    protected FamilyService familyService;

    @MockBean
    protected FriendService friendService;

    @MockBean
    protected MainService mainService;

    // MockBean 통해서 실제 빈을 대체하는 가짜 빈을 주입
    // 사용하는 서비스들은 모두 MockBean으로 주입
    /*
     * ex)
     * @MockBean
     * private ProductService productService;
     */

}
