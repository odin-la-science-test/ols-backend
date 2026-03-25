package com.odinlascience.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
class OlsBackendApplicationTest {

	@Test
	void contextLoads() {
	}

	@Test
	void main_StartsApplication() {
		assertThatNoException().isThrownBy(() -> {
			OlsBackendApplication.main(new String[]{});
		});
	}
}
