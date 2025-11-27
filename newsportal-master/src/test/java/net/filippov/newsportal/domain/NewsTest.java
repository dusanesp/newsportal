package net.filippov.newsportal.domain;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewsTest {

	private Article news;

	@BeforeEach
	public void init() {
		news = new Article();
	}

	@Test
	public void testCreatedNotNull() {
		Assertions.assertNotNull(news.getCreated());
	}

	@Test
	public void testSetModified() throws InterruptedException {
		news.setLastModified(new Date());
		Date prevModified = news.getLastModified();

		Thread.sleep(10);
		news.setLastModified(new Date());

		Assertions.assertTrue(news.getLastModified().after(prevModified));
	}
}
