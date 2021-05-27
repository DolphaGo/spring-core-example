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
- 객체의 생성과 연결은 AppConfig 가 담당한다.
- DIP 완성: MemberServiceImpl 은 MemberRepository 인 추상에만 의존하면 된다. 이제 구체 클래스를 몰라도 된다.
- 관심사의 분리: 객체를 생성하고 연결하는 역할과 실행하는 역할이 명확히 분리되었다.

### AppConfig 리팩토링

- 지금 역할이 드러나지 않고 있음
- 역할을 드러나게 하는 것이 중요함
```java
public class AppConfig { // 새로운 역할. 여기서 생성한 구현체는 생성자로 주입받는다.

    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository()); // MemoryMemberRepository를 사용하고 싶을 때(구체적인 곳은 여기서 지정한다.)
    }

    private MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    public DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }

}
```

- 메서드 명으로 역할이 드러남
- 나중에 MemoryMemberRepository -> JdbcMemberRepository 이렇게 바뀐다고 한다면 위의 구현체쪽만 바꿔주면 됨
- 유지 보수에도 도움이 된다.
- 역할과 구현이 한 눈에 보인다.
- new MemoryMemberRepository() 이 부분이 중복 제거되었다. 
- 이제 MemoryMemberRepository 를 다 른 구현체로 변경할 때 한 부분만 변경하면 된다.
- AppConfig 를 보면 역할과 구현 클래스가 한눈에 들어온다. 
- 애플리케이션 전체 구성이 어떻게 되어있는지 빠르게 파악할 수 있다.


> 구성영역과 사용영역이 명확히 구분되었다는 것. 이게 진짜 큰 장점임
> 구성 영역은 당연히 변경된다! 공연 기획자는 공연 참여자인 구현 객체들을 모두 알아야하는 것은 팩트이니!
> 대신 사용 영역에 있는 코드는 전혀 손 댈 필요가 없다!!!

- 덕분에 DIP / OCP를 모두 지키게 되었다.
- AppConfig는 애플리케이션 전체 동작 방식을 구성하기 위해 구현 객체를 생성하고 연결하는 책임을 가짐!(관심사의 분리)
- 클라이언트 객체는 자신의 역할을 실행하는 것만 집중, 책임이 명확해짐
