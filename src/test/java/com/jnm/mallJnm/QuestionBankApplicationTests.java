package com.jnm.mallJnm;

import com.jnm.mallJnm.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuestionBankApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(OrderStatus.CANCELLED.name());
        System.out.println(OrderStatus.CANCELLED.values());
        System.out.println(OrderStatus.CANCELLED.ordinal());
    }

}
