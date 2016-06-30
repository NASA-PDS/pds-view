package gov.nasa.arc.pds.lace.client.presenter;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class EnumeratedRadioGroupTest {

	public static enum TestEnum {

		ONE, TWO, THREE;

	}

	private static final Map<String, String> itemTextMap = new HashMap<String, String>();
	static {
		itemTextMap.put(TestEnum.ONE.toString(), "one");
		itemTextMap.put(TestEnum.TWO.toString(), "two");
		itemTextMap.put(TestEnum.THREE.toString(), "three");
	}

	@Mock
	private EnumeratedRadioGroup.Display view;

	private EnumeratedRadioGroup group;

	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
		group = new EnumeratedRadioGroup(view);
	}

	@Test
	public void testInitialization() {
		verify(view).setPresenter(group);
	}

	@Test
	public void testSetItems() {
		group.setValues(new TestEnum[] { TestEnum.ONE, TestEnum.TWO }, itemTextMap);
		verify(view).addItem(anyString(), eq("one"), eq(TestEnum.ONE.toString()));
		verify(view).addItem(anyString(), eq("two"), eq(TestEnum.TWO.toString()));
	}

	@Test
	public void testSetSelectedItem() {
		when(view.getSelectedValue()).thenReturn(TestEnum.TWO.toString(), TestEnum.ONE.toString());
		group.setValues(new TestEnum[] { TestEnum.ONE, TestEnum.TWO }, itemTextMap);

		group.setSelectedValue(TestEnum.TWO);
		assertEquals(group.getSelectedValue(TestEnum.class), TestEnum.TWO);

		group.setSelectedValue(TestEnum.ONE);
		assertEquals(group.getSelectedValue(TestEnum.class), TestEnum.ONE);
	}

	@Test
	public void testAddHandler() {
		MyChangeHandler handler = new MyChangeHandler();
		group.addValueChangeHandler(handler);
		group.handleValueChanged();
		assertTrue(handler.getGroupName().startsWith("enum-radio-group-"));
	}

	@Test(dataProvider="TrueFalse")
	public void testSetEnabled(boolean enabled) {
		group.setEnabled(enabled);
		verify(view).setEnabled(enabled);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="TrueFalse")
	private Object[][] getTrueFalse() {
		return new Object[][] {
				{ Boolean.TRUE },
				{ Boolean.FALSE },
		};
	}

	@Test
	public void testGetStringValue() {
		when(view.getSelectedValue()).thenReturn("MARS");
		assertEquals(group.getStringValue(), "MARS");
	}

	@Test
	public void testSetStringValue() {
		group.setStringValue("MARS");
		verify(view).setSelectedValue("MARS");
	}

	private static class MyChangeHandler implements ValueChangeHandler<String> {

		private String groupName;

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			groupName = event.getValue();
		}

		public String getGroupName() {
			return groupName;
		}

	}

}
