# Configuration의 마법

다음과 같은 AppConfig가 있다.
````java
@Configuration
public class AppConfig { // 새로운 역할. 여기서 생성한 구현체는 생성자로 주입받는다. (애플리케이션의 설정 정보)

    @Bean // 각 메서드에다가 Bean을 적어주면, 스프링 컨테이너에 등록이 된다.
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository()); // MemoryMemberRepository를 사용하고 싶을 때(구체적인 곳은 여기서 지정한다.)
    }

    @Bean
    public MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    // FixDiscount 정책에서 RateDiscount 정책으로 변경하고 싶다면 이 부분만 변경해주면 된다.
    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
````

- `@Bean` memberService -> new MemoryMemberRepository()
- `@Bean` orderService -> new MemoryMemberRepository(), new RateDiscountPolicy()

어? memberService, orderService를 각각 만들면, MemoryMemberRepository는 2개의 new를 하니까 2번 생성되는 것 아닌가요? 즉, 싱글톤이 깨지는 것 아닌가요?

자, 한 번 테스트로 확인해봅시다.

MemberServiceImpl
```java
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(final Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(final Long memberId) {
        return memberRepository.findById(memberId);
    }

    //테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
```

OrderServiceImpl
```java
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(final MemberRepository memberRepository, final DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(final Long memberId, final String itemName, final int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // 테스트 용
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
```

테스트를 해봅니다.
```java
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
```
- 분명히 자바 코드인데, new로 호출하는 것인데 어찌 인스턴스가 모두 같은가?
    - 총 3번의 `new MemoryMemberRepository`가 불러졌을 것 같은데 아니다?!
    