package com.jnm.mallJnm;

import com.jnm.mallJnm.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuestionBankApplicationTests {

    @Test
    void contextLoads() {
        String no = StringUtil.createNo();
        System.out.println(no);
    }

}
