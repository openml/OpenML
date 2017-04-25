package openmlweka;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.openml.weka.experiment.RandomBot;

public class TestRandomBot 
{
	RandomBot bot;
	@Before
	public void setUp()
	{
		bot = new RandomBot();
	}

	@Test
	public void testTaskType()
	{
		try
		{
			RandomBot.main(new String[]{"sdsd", "study_41", "bayes"});
		}
		catch(NumberFormatException e)
		{
			fail("Should not have thrown exception");
		}
	}
	
	@Test
	public void testClassifierType()
	{
		try
		{
			bot.startTask(3550, "Trash");
		}
		catch(NullPointerException e)
		{
			fail("Should not have thrown exception");
		}
		
	}
	@Test
	public void testTaskId() 
	{
		int taskId = bot.getRandomTaskId(1, "study_41");
		assertTrue("Task id number is bigger than 0", taskId > 0);
	}
	
	
}
