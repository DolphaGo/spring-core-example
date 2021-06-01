- 의도적으로 해당 타입의 스프링 빈이 다 필요한 경우도 있음
- `전략 패턴` (클라이언트가 선택하는 전략)으로 쉽게 구현하면 된다.

```java
public class AllBeanTest {

    @Test
    void findAllBean() {
        final ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class); // 스프링 빈에 등록

        final DiscountService discountService = ac.getBean(DiscountService.class);
        final Member member = new Member(1L, "userA", Grade.VIP);
        final int discountPrice = discountService.discount(member, 10000, "fixDiscountPolicy");

        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(discountPrice).isEqualTo(1000);

        final int rateDiscountPrice = discountService.discount(member, 20000, "rateDiscountPolicy");

        assertThat(rateDiscountPrice).isEqualTo(2000);
    }

    static class DiscountService {
        private final Map<String, DiscountPolicy> policyMap;
        private final List<DiscountPolicy> policies;

        public DiscountService(final Map<String, DiscountPolicy> policyMap, final List<DiscountPolicy> policies) {
            this.policyMap = policyMap;
            this.policies = policies;
            System.out.println("policyMap = " + policyMap);
            System.out.println("policies = " + policies);
        }

        public int discount(final Member member, final int price, final String discountCode) {
            final DiscountPolicy discountPolicy = policyMap.get(discountCode);
            return discountPolicy.discount(member, price);
        }
    }
}
```

- Map<String, DiscountPolicy> : map의 키에 스프링 빈의 이름을 넣어주고, 그 값으로 DiscountPolicy 타입으로 조회한 모든 스프링 빈을 담아준다.
- List<DiscountPolicy> : DiscountPolicy 타입으로 조회한 모든 스프링 빈을 담아준다.
- 만약 해당하는 타입의 스프링 빈이 없으면, 빈 컬렉션이나 Map을 주입한다.

---

**애플리케이션에 광범위하게 영향을 미치는 기술 지원 객체는 수동 빈으로 등록해서 딱 설정 정보에 바로 나타나게 하는 것이 유지보수에 좋다.**

- 다형성을 적극 활용할 때 수동 등록으로 한 곳에 모아서 설정 정보에서 관리하는 것이 좋을 *수도* 있음

```java
@Configuration
public class DiscountPolicyConfig {
    @Bean
    public DiscountPolicy rateDiscountPolicy() {
        return new RateDiscountPolicy();
    }

    @Bean
    public DiscountPolicy fixDiscountPolicy() {
        return new FixDiscountPolicy();
    }
}
```
- 사실 답은 없음 ㅎㅎ, 핵심은 딱 보고 확실하게 이해가 되냐가 중요한 것

> 정리
- 편리한 자동 기능을 기본으로 사용하자
- 직접 등록하는 기술 지원 객체는 수동 등록
- 다형성을 적극 활용하는 비즈니스 로직은 수동 등록을 고민해보자