package gov.nasa.arc.pds.lace.server.schema;

import gov.nasa.arc.pds.lace.server.IDGenerator;

import com.google.inject.AbstractModule;

public class DefaultSchemaManagerModule extends AbstractModule {

	@Override
	protected void configure() {
		final IDGenerator idFactory = new IDGenerator();

		final SchemaDefaults schemaDefaults = new SchemaDefaults();
		schemaDefaults.addSchemaFile("pds4/1201/PDS4_PDS_1201.xsd"); // PDS4 1.2.0.1 schema
		schemaDefaults.addSchemaFile("pds4/1201/PDS4_PDS_1201.sch"); // PDS4 1.2.0.1 Schematron
		schemaDefaults.addSchemaFile("spase/2.2.2/spase-2_2_2.xsd"); // SPASE 2.2.2
		schemaDefaults.addSchemaFile("pds4/0300a/PDS4_OPS_0300a.xsd"); // PDS4 0.3.0.0.a schema
		schemaDefaults.addSchemaFile("pds4/0300a/PDS4_OPS_0300a.sch"); // PDS4 0.3.0.0.a Schematron

		// MGS local schema, not yet released for PDS4, for testing <xs:any> elements.
		schemaDefaults.addSchemaFile("test/PDS4_MGS_0001.xsd");
		schemaDefaults.addSchemaFile("test/PDS4_MGS_0001.sch");

		schemaDefaults.addSchemaFile("test/test-schema.xsd"); // For label reader unit testing.
		schemaDefaults.addSchemaFile("test/model-analyzer-schema.xsd"); // For analyzer unit testing.

		bind(SchemaDefaults.class).toInstance(schemaDefaults);

		bind(IDGenerator.class).toInstance(idFactory);
		bind(SchemaManager.class).toProvider(DefaultManagerProvider.class);
	}

}
