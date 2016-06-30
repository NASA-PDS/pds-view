package gov.nasa.arc.pds.lace.server;

import java.util.Map;

import com.google.inject.Injector;
import com.opensymphony.xwork2.ObjectFactory;

/**
 * Implements an object factory for Struts that uses Guice to
 * create object instances.
 */
@SuppressWarnings("serial")
public class StrutsObjectFactory extends ObjectFactory {

	private static Injector injector;

	/**
	 * Sets the Guice injector to use for creating object instances.
	 *
	 * @param injector the injector
	 */
	static synchronized void setInjector(Injector injector) {
		StrutsObjectFactory.injector = injector;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
		return injector.getInstance(clazz);
	}

}
