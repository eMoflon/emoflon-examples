package LearningBoxLanguage;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link Box}.
 */
public class BoxTests {

	private LearningBoxLanguageFactory factory;
	private Box box;

	@Before
	public void setUp() {
		factory = LearningBoxLanguageFactory.eINSTANCE;
		box = factory.createBox();
		box.initializeBox();
	}

	@Test
	public void testCreateBox() throws Exception {
		Assert.assertEquals(2, box.getContainedPartition().size());

		Partition p0 = box.getContainedPartition().get(0);
		Partition p1 = box.getContainedPartition().get(1);
		Assert.assertSame(p0.getNext(), p1);
		Assert.assertSame(p1.getPrevious(), p0);

		Assert.assertThat(p0.getCard().size(), is(0));
		Assert.assertThat(p1.getCard().size(), is(0));
	}

	@Test
	public void testAddPartitionToNewBox() throws Exception {
		box.grow();
		Assert.assertThat(box.getContainedPartition().size(), is(3));

		Partition p0 = box.getContainedPartition().get(0);
		Partition p1 = box.getContainedPartition().get(1);
		Partition p2 = box.getContainedPartition().get(2);

		Assert.assertThat(p1.getNext(), is(p2));
		// This ensures that 'failed' cards are moved to the very front of the
		// box.
		Assert.assertThat(p2.getPrevious(), is(p0));
	}
}
