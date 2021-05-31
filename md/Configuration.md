# `@Configuration`

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
    
- 이 비밀은 `@Configuration`에 있습니다.
````java
@Test
void configurationDeep() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class); // AppConfig 자체도 빈으로 등록이 됩니다.
    AppConfig bean = ac.getBean(AppConfig.class);
    System.out.println("bean.getClass() = " + bean.getClass()); // bean.getClass() = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$fd1df726
}
````
순수한 클래스라면 다음과 같이 출력되는 것이 맞을 터
`class hello.core.AppConfig`

- 그런데, 스프링이 빈을 등록하는 과정에서 조작을 한다.
- 이것은 내가 만든 클래스가 아니라, 스프링이 CGLIB라는 바이트코드 조작 라이브러리를 사용해서 AppConfig 클래스를 상속받은 임의의 다른 클래스를 만들고, 그 다른 클래스를 스프링 빈으로 등록한 것이다.

- 내가 등록한 것은 `AppConfig`인데 스프링이 `AppConfig@CGLIB`
- 내가 만든 것은 사라지고, 등록된 인스턴스는 `CGLIB`로 되어 있다.
- 이는 임의의 다른 클래스가 바로 싱글톤이 되도록 보장을 해줍니다.
- 아마도 바이트 코드를 다음과 같이 조작해서 되어있을 것입니다.

```java
@Bean
public MemberRepository memberRepository() {
    if(memoryMemberRepository가 이미 스프링 컨테이너에 등록되어 있으면) {
        return 스프링 컨테이너에서 찾아서 반환;
    }
    return 기존 로직을 호출해서 MemoryMemberRepository를 생성하고 스프링 컨테이너에 등록후 반환
}
```

> 참고로, `AppConfig@CGLIB`은 AppConfig의 자식타입이므로, AppConfig 타입으로 조회할 수 있다.

# `@Configuration`을 적용하지 않고, `@Bean`만 사용하면 어떻게 될까?
- 안붙여도 스프링 컨테이너에 의해서 스프링 빈으로 등록됩니다.
- 대신에 문제가 있습니다.

`@Configuration`을 제거해보겠습니다.
```java
//@Configuration
public class AppConfig { // 새로운 역할. 여기서 생성한 구현체는 생성자로 주입받는다. (애플리케이션의 설정 정보)

  @Bean // 각 메서드에다가 Bean을 적어주면, 스프링 컨테이너에 등록이 된다.
  public MemberService memberService() {
    System.out.println("AppConfig.memberService");
    return new MemberServiceImpl(memberRepository()); // MemoryMemberRepository를 사용하고 싶을 때(구체적인 곳은 여기서 지정한다.)
  }

  @Bean
  public MemoryMemberRepository memberRepository() {
    System.out.println("AppConfig.memberRepository");
    return new MemoryMemberRepository();
  }

  @Bean
  public OrderService orderService() {
    System.out.println("AppConfig.orderService");
    return new OrderServiceImpl(memberRepository(), discountPolicy());
  }

  // FixDiscount 정책에서 RateDiscount 정책으로 변경하고 싶다면 이 부분만 변경해주면 된다.
  @Bean
  public DiscountPolicy discountPolicy() {
    System.out.println("AppConfig.discountPolicy");
//        return new FixDiscountPolicy();
    return new RateDiscountPolicy();
  }
}
```

출력
```
bean.getClass() = class hello.core.AppConfig
```
- 이제서야 제가 직접 만든 객체가 보이네요.
- 사실 이것보다 더 큰 문제는 다음과 같습니다.
다음 테스트는 실패합니다. 😄
```java
@Test
void configurationTest() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    // 구체 타입으로 꺼내면 좋진 않지만, 테스트 용이니까 ^^;
    MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
    OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);

    MemberRepository memberRepository1 = memberService.getMemberRepository();
    MemberRepository memberRepository2 = orderService.getMemberRepository();

    assertThat(memberRepository1).isSameAs(memberRepository2); // 다르다!!

    MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class); // 스프링 컨테이너에 등록된 진짜 Bean
    assertThat(memberRepository1).isSameAs(memberRepository); // 다르다!!
}
```

```text
AppConfig.memberService
AppConfig.memberRepository // 1
23:09:44.982 [main] DEBUG org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating shared instance of singleton bean 'memberRepository'
AppConfig.memberRepository // 2
23:09:44.983 [main] DEBUG org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating shared instance of singleton bean 'orderService'
AppConfig.orderService
AppConfig.memberRepository // 3
AppConfig.discountPolicy
23:09:44.984 [main] DEBUG org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating shared instance of singleton bean 'discountPolicy'
AppConfig.discountPolicy
```
- memberRepository가 3번이나 호출이 됩니다.
  - `@Bean`으로 스프링에 등록하기 위한 1번이고
  - MemberService 1번, OrderService 1번 각각 호출하는 것.
- 이렇게 되면, `MemberService`, `OrderService`에 주입된 `MemberRepository`는 스프링 컨테이너에서 관리하는 빈이 아니라는 것

스프링에 등록된 빈을 끌어다가 쓰도록 바꿔보면 다음과 같다.
````java
//@Configuration
public class AppConfig { // 새로운 역할. 여기서 생성한 구현체는 생성자로 주입받는다. (애플리케이션의 설정 정보)

    @Autowired
    private MemberRepository memberRepository; // Bean에 등록된 MemberRepository를 가져와서 주입

    @Bean // 각 메서드에다가 Bean을 적어주면, 스프링 컨테이너에 등록이 된다.
    public MemberService memberService() {
        System.out.println("AppConfig.memberService");
        return new MemberServiceImpl(memberRepository);
    }

    @Bean
    public MemoryMemberRepository memberRepository() {
        System.out.println("AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        System.out.println("AppConfig.orderService");
        return new OrderServiceImpl(memberRepository, discountPolicy());
    }

    // FixDiscount 정책에서 RateDiscount 정책으로 변경하고 싶다면 이 부분만 변경해주면 된다.
    @Bean
    public DiscountPolicy discountPolicy() {
        System.out.println("AppConfig.discountPolicy");
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
````

이 테스트는 성공한다.
```java
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

> 정리
- `@Bean`만 사용해도 스프링 빈으로 등록되지만, 싱글톤 보장 X
- 크게 고민할 것이 없음. 스프링 설정 정보는 항상 `@Configuration` 사용한다.
