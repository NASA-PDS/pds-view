package gov.nasa.arc.pds.lace.client.event;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import gov.nasa.arc.pds.lace.client.presenter.InsertionPointPresenter;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Provider;

public class WildcardElementSelectedEventTest {

	@Mock
	private Provider<InsertionPointPresenter> provider;
	
	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test	
	public void testConstructor() {		
		InsertionPointPresenter presenter = provider.get();		
		WildcardElementSelectedEvent event = new WildcardElementSelectedEvent(presenter, "namespace1", "element1");
		assertSame(event.getData().getPresenter(), presenter);		
		assertEquals(event.getData().getNamespace(), "namespace1");
		assertEquals(event.getData().getElementName(), "element1");
	}
}
