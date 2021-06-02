package hello.core.scope;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;


public class PrototypeTest {

    @Test
    void prototypeBeanFind() {
        final AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class); // 뭐 컴포넌트가 없어도, 이 곳에 등록된 것은 컴포넌트 스캔의 대상이 됨
        System.out.println("find prototypeBean1");
        final PrototypeBean prototypeBean1 = ac.getBean(PrototypeBean.class);
        System.out.println("find prototypeBean2");
        final PrototypeBean prototypeBean2 = ac.getBean(PrototypeBean.class);
        assertThat(prototypeBean1).isNotSameAs(prototypeBean2);
        ac.close();
    }

    @Scope("prototype") // default가 싱글톤임
    static class PrototypeBean {

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
