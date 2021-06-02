package hello.core.scope;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

public class SingletonWithPrototypeTest1 {

    @Test
    void prototypeFind() {
        final AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        final PrototypeBean prototypeBean1 = ac.getBean(PrototypeBean.class);
        prototypeBean1.addCount();
        assertThat(prototypeBean1.getCount()).isEqualTo(1);

        final PrototypeBean prototypeBean2 = ac.getBean(PrototypeBean.class);
        prototypeBean2.addCount();
        assertThat(prototypeBean2.getCount()).isEqualTo(1);
    }

    @Test
    void singletonClientUsePrototype() {
        final AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class); // 2개 다 자동 빈 등록에 의해서 빈이 등록이 된다.
        final ClientBean clientBean1 = ac.getBean(ClientBean.class);
        final int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        final ClientBean clientBean2 = ac.getBean(ClientBean.class);
        final int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(2); // 프로토타입이 새로 생성되는 것이 아님~!
    }

    // 프로토 타입을 쓸 때는, 새로 만드는 것을 원했을 텐데, 의도한 대로 동작하지는 않음
    @Scope("singleton")
    static class ClientBean {
        private final PrototypeBean prototypeBean; // 생성 시점에 주입이 되어있다. <- prototype 0x1

        public ClientBean(final PrototypeBean prototypeBean) {
            this.prototypeBean = prototypeBean;
        }

        public int logic() {
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    // 프로토 타입을 쓸 때는, 새로 만드는 것을 원했을 텐데, 의도한 대로 동작하지는 않음
    @Scope("singleton")
    static class ClientBean2 {
        private final PrototypeBean prototypeBean; // 생성 시점에 주입이 되어있다.<- prototype 0x2

        public ClientBean2(final PrototypeBean prototypeBean) {
            this.prototypeBean = prototypeBean;
        }

        public int logic() {
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }


    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init" + this);
        }

        @PreDestroy
        public void destroy() { // 프로토타입 빈이라 호출이 안될 것임
            System.out.println("PrototypeBean.destroy");
        }
    }

}
