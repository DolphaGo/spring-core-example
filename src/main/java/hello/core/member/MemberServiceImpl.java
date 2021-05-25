package hello.core.member;
// 구현체가 1개만 있을 때는 관례상 인터페이스+`Impl` 이라고 이름을 많이 짓곤한다.
public class MemberServiceImpl implements MemberService {

    private MemberRepository memberRepository = new MemoryMemberRepository();

    @Override
    public void join(final Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(final Long memberId) {
        return memberRepository.findById(memberId);
    }
}
