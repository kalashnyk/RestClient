import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import ru.kao.rest.Project;

public class RestClientTest {

	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		//fail("Not yet implemented");
		Double cost;
		Project project = new Project(Long.valueOf(14), "tetris/tetris", Long.valueOf(10), Long.valueOf(10),
				Long.valueOf(2), Long.valueOf(1024), "java", new Date(115, 9, 17));
		
		cost = project.getCost();
		if (43.1d != cost) {
			fail("1. Не правильно расчитывается вес: " + cost);
		}
		
		project = new Project(Long.valueOf(14), "tetris/tetris", Long.valueOf(10), Long.valueOf(10),
				Long.valueOf(2), Long.valueOf(1024), "java", new Date(113, 8, 10, 10, 29, 10));
		
		cost = project.getCost();
		
		if (23.1d != project.getCost()) {
			fail("2. Не правильно расчитывается вес: " + cost);
		}
	}

}
