package hello.core.singleton;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import hello.core.AppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberServiceImpl;
import hello.core.order.OrderServiceImpl;

public class ConfigurationSingletonTest {

    @DisplayName("Configuration의 마법 테스트")
    @Test
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        // 구체 타입으로 꺼내면 좋진 않지만, 테스트 용이니까 ^^;
        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();

        assertThat(memberRepository1).isSameAs(memberRepository2); // 같다!!

        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class); // 스프링 컨테이너에 등록된 진짜 Bean
        assertThat(memberRepository1).isSameAs(memberRepository); // 같다!!
    }

    @Test
    void configurationDeep() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class); // AppConfig 자체도 빈으로 등록이 됩니다.
        AppConfig bean = ac.getBean(AppConfig.class);
        System.out.println("bean.getClass() = " + bean.getClass()); // bean.getClass() = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$fd1df726
    }
}
