````java
public class AppConfig { // 새로운 역할. 여기서 생성한 구현체는 생성자로 주입받는다.

    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepository()); // MemoryMemberRepository를 사용하고 싶을 때(구체적인 곳은 여기서 지정한다.)
    }

    public OrderService orderService() {
        return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
    }

}
````
- AppConfig는 애플리케이션의 실제 동작에 필요한 구현 객체를 생성한다.
    - MemberServiceImpl 
        - MemoryMemberRepository 
    - OrderServiceImpl
        - MemoryMemberRepository
        - FixDiscountPolicy

AppConfig는 생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입(연결)해준다. MemberServiceImpl MemoryMemberRepository
- OrderServiceImpl
    - MemoryMemberRepository
    - FixDiscountPolicy

이제 인터페이스에만 의존하고 있기 때문에 DIP를 만족하고, 클라이언트 쪽 코드 변경이 필요 없으므로 OCP도 만족하게 되는 것.
```java
private final MemberRepository memberRepository;
private final DiscountPolicy discountPolicy;

public OrderServiceImpl(final MemberRepository memberRepository, final DiscountPolicy discountPolicy) {
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
}
```


이제 DIP가 완성됨! ㅎㅎㅎ
```java
class MemberServiceTest {
    private MemberService memberService ;

    @BeforeEach
    void setUp() {
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
    }

    @Test
    void join() {
        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);
        Member findMember = memberService.findMember(1L);
        assertEquals(findMember, member);
    }
}
```

- AppConfig로 관심사를 확실하게 분리했음
- AppConfig는 구체 클래스를 선택한다.