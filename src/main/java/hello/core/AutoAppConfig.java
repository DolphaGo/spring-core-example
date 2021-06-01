package hello.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

@Configuration
@ComponentScan(
//        basePackageClasses = MemoryMemberRepository.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class) // 수동 등록은 빼준다.
)
public class AutoAppConfig {



    @Bean(name = "memoryMemberRepository")
    MemberRepository memoryMemberRepository() {
        return new MemoryMemberRepository();
    }
}
