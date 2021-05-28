package hello.core.beanfind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import hello.core.AppConfig;

class ApplicationContextInfoTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @DisplayName("모든 빈 출력하기")
    @Test
    void bean_print() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName);
            System.out.println("bean = " + bean);
        }
    }

    @DisplayName("애플리케이션 빈 출력하기")
    @Test
    void find_Application_Bean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            //Role ROLE_APPLICATION : 직접 등록한 애플리케이션 빈
            //ROLE ROLE_INFRASTRUCTURE : 스프링이 내부에서 사용하는 빈

            // 애플리케이션 개발을 위해서 (스프링 내부적으로 등록된 Bean이 아니라) 만든 Bean을 출력하려면 다음과 같이 하면 된다.
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name = " + beanDefinitionName + " object = " + bean);
            }
        }
    }

    @DisplayName("스프링이 내부에서 사용하는 빈 출력하기")
    @Test
    void find_spring_infra_Bean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            //Role ROLE_APPLICATION : 직접 등록한 애플리케이션 빈
            //ROLE ROLE_INFRASTRUCTURE : 스프링이 내부에서 사용하는 빈

            // 애플리케이션 개발을 위해서 (스프링 내부적으로 등록된 Bean이 아니라) 만든 Bean을 출력하려면 다음과 같이 하면 된다.
            if (beanDefinition.getRole() == BeanDefinition.ROLE_INFRASTRUCTURE) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name = " + beanDefinitionName + " object = " + bean);
            }
        }
    }

}
