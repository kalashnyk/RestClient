package ru.kao.rest;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

public class RestClientTest {

	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		Project project = new Project(Long.valueOf(123456789), "tetris", Long.valueOf(2), Long.valueOf(2),
				Long.valueOf(0), Long.valueOf(12453), "java", new Date());

		Date d1 = new Date(2013, 9, 16);
		Date d2 = new Date(2015, 9, 17);

		if (!project.diffDate(d1, d2)) {
			fail();
		}

		d1 = new Date(2013, 9, 17);

		if (project.diffDate(d1, d2)) {
			fail();
		}
	}
}
