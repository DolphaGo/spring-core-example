package hello.core.beanfind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import hello.core.AppConfig;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;

class ApplicationContextBasicFindTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @DisplayName("빈 이름으로 조회")
    @Test
    void findBeanByName() {
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @DisplayName("이름없이 타입으로만 조회")
    @Test
    void findBeanByType() {
        MemberService memberService = ac.getBean(MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }


    @DisplayName("구체 타입으로 조회")
    @Test
    void findBeanByName2() {
        MemberService memberService = ac.getBean("memberService", MemberServiceImpl.class); // 구체에 의존하지 마라~ 역할에 의존하는 것이 좋습니다.
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @DisplayName("빈 이름으로 조회했는데 없을 때")
    @Test
    void findBeanByNameX() {
//        MemberService memberService = ac.getBean("xxxxxxx", MemberService.class);
        /**
         * org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'xxxxxxx' available
         *
         * at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeanDefinition(DefaultListableBeanFactory.java:863)
         * 	at org.springframework.beans.factory.support.AbstractBeanFactory.getMergedLocalBeanDefinition(AbstractBeanFactory.java:1344)
         * 	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:309)
         * 	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:213)
         * 	at org.springframework.context.support.AbstractApplicationContext.getBean(AbstractApplicationContext.java:1160)
            ...
         */

        assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean("xxxxxxx", MemberService.class));
    }
}
