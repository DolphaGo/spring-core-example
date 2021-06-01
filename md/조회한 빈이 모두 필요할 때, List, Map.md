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