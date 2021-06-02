package hello.core.scope;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

public class SingletonTest {

    @Test
    void singletonBeanFind() {
        final AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class);
        final SingletonBean singletonBean1 = ac.getBean(SingletonBean.class);
        final SingletonBean singletonBean2 = ac.getBean(SingletonBean.class);
        assertThat(singletonBean1).isSameAs(singletonBean2);
        singletonBean1.destroy();
        singletonBean2.destroy();
        ac.close();
    }

    @Scope("singleton") // default가 싱글톤이긴함
    static class SingletonBean {

        @PostConstruct
        public void init() {
            System.out.println("SingletonBean.init");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("SingletonBean.destroy");
        }
    }
}
