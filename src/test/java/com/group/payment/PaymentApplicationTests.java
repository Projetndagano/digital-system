package com.group.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ActiveProfiles("test")
class PaymentApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("Automated Test: Spring Context Loaded Successfully!");
	}

}
