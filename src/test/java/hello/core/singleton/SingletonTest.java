package hello.core.singleton;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.core.AppConfig;
import hello.core.member.MemberService;

public class SingletonTest {

    @DisplayName("스프링이 없는 순수한 DI 컨테이너")
    @Test
    void pureContainer() {
        AppConfig appConfig = new AppConfig();
        // 1. 조회 : 호출할 때마다 객체를 생성
        MemberService memberService1 = appConfig.memberService();

        // 2. 조회 : 호출할 때마다 객체를 생성
        MemberService memberService2 = appConfig.memberService();

        // 참조값이 다른 것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);

        // memberService != memberService2
        assertThat(memberService1).isNotSameAs(memberService2);
    }

//    public static void main(String[] args) {
//        SingletonService singletonService = new SingletonService(); // 이와 같이 만들 수 있는 것을 막아야 한다. < private 생성자 >
//    }

    @DisplayName("싱글톤 패턴을 적용한 객체 사용")
    @Test
    void singletonServiceTest() {
        SingletonService singletonService1 = SingletonService.getInstance();
        SingletonService singletonService2 = SingletonService.getInstance();

        assertThat(singletonService1).isEqualTo(singletonService2); // 값 비교
        assertThat(singletonService1).isSameAs(singletonService2); // 인스턴스 비교
        /**
         * same과 equal의 차이
         *
         * same : == 비교
         * equal : equal 비교
         */
    }
}
