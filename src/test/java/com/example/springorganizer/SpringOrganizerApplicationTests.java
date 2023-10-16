package com.example.springorganizer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringOrganizerApplicationTests {

	@LocalServerPort
	private int port;

	private final TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	void contextLoads() {
		// Проверка, что приложение успешно запускается

		String url = "http://localhost:" + port + "/api/tasks"; // Замените на фактический путь вашего приложения
		String response = restTemplate.getForObject(url, String.class);

		// Добавьте дополнительные проверки, если необходимо
		assertEquals("[]", response);
	}
	@Test
	void main_DoesNotThrowException() {
		// Проверяем, что метод main не выбрасывает исключений
		assertDoesNotThrow(() -> SpringOrganizerApplication.main(new String[]{}));
	}
}
