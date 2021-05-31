package hello.core.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 구현체가 1개만 있을 때는 관례상 인터페이스+`Impl` 이라고 이름을 많이 짓곤한다.
@Component("DolphaGoMemberService")
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Autowired // 자동 의존관계 주입 : ac.getBean(MemberRepository.class)와 같은 방식으로 들어가는 것이다.
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
