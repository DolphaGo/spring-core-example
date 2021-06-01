package hello.core.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemoryMemberRepository;

class OrderServiceImplTest {

    @DisplayName("생성자 주입을 사용하지 않았을 때")
    @Test
    void createOrder() {
        MemoryMemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1L, "n ame", Grade.VIP));

//        OrderService orderService = new OrderServiceImpl();//생성자가 아닌 상태에서 테스트시 문제점
//        orderService.createOrder(1L,"itemA",1000); // 의존관계를 설정하지 않아서 NPE가 난다.

        OrderService orderService = new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());//생성자일 때는 비어있으면 컴파일 오류(세상에서 제일 좋은 오류) 가 발생함
        final Order itemA = orderService.createOrder(1L, "itemA", 10000);

        assertThat(itemA.getDiscountPrice()).isEqualTo(1000);
    }

}