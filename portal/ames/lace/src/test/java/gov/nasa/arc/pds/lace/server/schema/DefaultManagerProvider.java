package gov.nasa.arc.pds.lace.server.schema;

import gov.nasa.arc.pds.lace.server.IDGenerator;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Implements a schema manager provider that uses only the default
 * schemas, and constructs the schema manager as a singleton.
 * This provider should only be used for tests that do not need
 * to have a test-specific schema manager.
 */
public class DefaultManagerProvider implements Provider<SchemaManager> {

	private static SchemaManager manager = null;

	private IDGenerator idFactory;
	private SchemaDefaults schemaDefaults;
	
	@Inject
	public DefaultManagerProvider(IDGenerator idFactory, SchemaDefaults defaults) {
		this.idFactory = idFactory;
		this.schemaDefaults = defaults;
	}
	
	@Override
	public SchemaManager get() {
		return getSchemaManager(idFactory, schemaDefaults);
	}
	
	private static synchronized SchemaManager getSchemaManager(IDGenerator factory, SchemaDefaults defaults) {
		if (manager == null) {
			manager = new SchemaManager(factory);
			for (URI uri : defaults.getSchemaURIs()) {
				manager.addSchemaFile(uri);
			}
			
			manager.loadSchemas();
		}
		
		return manager;
	}
	
}