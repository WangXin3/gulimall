package com.wxx.gulimall.order;

import com.wxx.gulimall.order.vo.MemberAddressVO;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        MemberAddressVO memberAddressVO = new MemberAddressVO();
        memberAddressVO.setAreacode("123");
        memberAddressVO.setCity("123222");
        rabbitTemplate.convertAndSend("routeExchange","route11", memberAddressVO);
    }

}
