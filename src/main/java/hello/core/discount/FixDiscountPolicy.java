package hello.core.discount;

import org.springframework.stereotype.Component;

import hello.core.member.Grade;
import hello.core.member.Member;

@Component
public class FixDiscountPolicy implements DiscountPolicy {

    static private int discountFixAmount = 1000; // 1000원 할인

    @Override
    public int discount(final Member member, final int price) {
        if (member.getGrade() == Grade.VIP) {
            return discountFixAmount;
        }
        return 0;
    }
}
