package gov.nasa.arc.pds.lace.test.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class GetterSetterTest {

	private Class<?> clazz;

	public GetterSetterTest(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void test(Object o) throws Exception {
		assert clazz.isAssignableFrom(o.getClass());

		for (String property : findProperties(clazz)) {
			testGetterSetter(o, property);
		}
	}

	private List<String> findProperties(Class<?> clazz) {
		List<String> properties = new ArrayList<String>();

		for (Method m : clazz.getMethods()) {
			if (m.getName().startsWith("get") && m.getParameterTypes().length==0) {
				Class<?> returnType = m.getReturnType();
				if (TEST_VALUES.containsKey(returnType)) {
					String setterName = m.getName().replaceFirst("get", "set");
					try {
						@SuppressWarnings("unused")
						Method setter = clazz.getMethod(setterName, returnType);

						// If we get here, the setter exists, so add the property.
						properties.add(m.getName().replaceFirst("get", ""));
					} catch (NoSuchMethodException ex) {
						// ignore
					}
				}
			}
		}

		return properties;
	}

	private static final Map<Class<?>, Object[]> TEST_VALUES = new HashMap<Class<?>, Object[]>();
	static {
		TEST_VALUES.put(int.class, new Object[] { 0, -1, 1, Integer.MIN_VALUE, Integer.MAX_VALUE });
		TEST_VALUES.put(Integer.class, new Object[] { 0, -1, 1, Integer.MIN_VALUE, Integer.MAX_VALUE });
		TEST_VALUES.put(
				double.class,
				new Object[] {
					0.0, -1.0, 1.0, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_NORMAL,
					Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
				}
		);
		TEST_VALUES.put(
				Double.class,
				new Object[] {
					0.0, -1.0, 1.0, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_NORMAL,
					Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
				}
		);
		TEST_VALUES.put(
				String.class,
				new Object[] { "", "A", "ABCDEFGHJIJKLMNOPQRSTUVWXYZ", "\u0000\u0001\u0080"
				}
		);
		TEST_VALUES.put(
				Date.class,
				new Object[] {
					getUTCDate(1970, 1, 1),
					getUTCDate(1969, 12, 31, 11, 59, 59),
					getUTCDate(2012, 2, 29),
					getUTCDate(1900, 2, 29),
					getUTCDate(2000, 3, 1),
				}
		);
		TEST_VALUES.put(
				File.class,
				new Object[] {
					new File("src/test/resources/util/test-file"),
					new File("src/test/resources/util/test-dir")
				}
		);
		TEST_VALUES.put(
				Object.class,
				new Object[] { null, "", new Integer(1)
				}
		);

	}

	private static Date getUTCDate(int year, int month, int day) {
		return getUTCDate(year, month, day, 0, 0, 0);
	}

	private static Date getUTCDate(int year, int month, int day, int hour, int minute, int second) {
		GregorianCalendar cal = new GregorianCalendar(year, month, day, hour, minute, second);
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		return cal.getTime();
	}

	private void testGetterSetter(Object o, String property) throws Exception {
		assert clazz.isAssignableFrom(o.getClass());

		Method getter = clazz.getMethod("get" + property, new Class<?>[0]);
		Method setter = clazz.getMethod("set" + property, new Class<?>[] { getter.getReturnType() });

		Object[] testValues = TEST_VALUES.get(getter.getReturnType());
		if (testValues != null) {
			for (Object value : testValues) {
				testGetterSetter( o, getter, setter, value);
			}
		}
	}

	private void testGetterSetter(Object o, Method getter, Method setter, Object value) throws Exception {
		try {
			setter.setAccessible(true);
			setter.invoke(o, new Object[] {value});
		} catch (Exception e) {
			throw new Exception("Exception testing " + setter.getName() + "(" + value.toString() + ")"
					+ " in class " + clazz.getName() + ": " + e.toString(), e);
		}
		try {
			Object actual = getter.invoke(o);
			if (actual!=value && !actual.equals(value)) {
				throw new Exception(getter.getName() + "/" + setter.getName() + " value mismatch: "
						+ "set <" + value + "> but got <" + actual + ">");
			}
		} catch (Exception e) {
			throw new Exception("Exception testing " + getter.getName() + "()"
					+ " in class " + clazz.getName() + ": " + e.toString(), e);
		}
	}

}
