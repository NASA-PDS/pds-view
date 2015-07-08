
    create table Affected_Objects (
        event_id varchar(255) not null,
        affected_object varchar(255)
    );

    create table Association (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        associationType varchar(255),
        sourceObject varchar(255),
        targetObject varchar(255),
        primary key (guid),
        unique (lid, versionName)
    );

    create table Association_Classification (
        Association_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (Association_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table Association_ExternalIdentifier (
        Association_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (Association_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table Association_Slot (
        Association_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (Association_guid, slots_id),
        unique (slots_id)
    );

    create table AuditableEvent (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        eventType int4,
        requestId varchar(255),
        timestamp timestamp,
        userid varchar(255),
        primary key (guid),
        unique (lid, versionName)
    );

    create table AuditableEvent_Classification (
        AuditableEvent_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (AuditableEvent_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table AuditableEvent_ExternalIdentifier (
        AuditableEvent_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (AuditableEvent_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table AuditableEvent_Slot (
        AuditableEvent_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (AuditableEvent_guid, slots_id),
        unique (slots_id)
    );

    create table Classification (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        classificationScheme varchar(255),
        classificiationNode varchar(255),
        classifiedObject varchar(255),
        nodeRepresentation varchar(255),
        primary key (guid),
        unique (lid, versionName)
    );

    create table ClassificationNode (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        code varchar(255),
        parent varchar(255),
        path varchar(255),
        primary key (guid),
        unique (lid, versionName)
    );

    create table ClassificationNode_Classification (
        ClassificationNode_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (ClassificationNode_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table ClassificationNode_ExternalIdentifier (
        ClassificationNode_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (ClassificationNode_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table ClassificationNode_Slot (
        ClassificationNode_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (ClassificationNode_guid, slots_id),
        unique (slots_id)
    );

    create table ClassificationScheme (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        isInternal bool,
        nodeType int4,
        primary key (guid),
        unique (lid, versionName)
    );

    create table ClassificationScheme_Classification (
        ClassificationScheme_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (ClassificationScheme_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table ClassificationScheme_ExternalIdentifier (
        ClassificationScheme_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (ClassificationScheme_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table ClassificationScheme_Slot (
        ClassificationScheme_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (ClassificationScheme_guid, slots_id),
        unique (slots_id)
    );

    create table Classification_Classification (
        Classification_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (Classification_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table Classification_ExternalIdentifier (
        Classification_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (Classification_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table Classification_Slot (
        Classification_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (Classification_guid, slots_id),
        unique (slots_id)
    );

    create table ExternalIdentifier (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        identificationScheme varchar(255),
        registryObject varchar(255),
        value varchar(255),
        primary key (guid),
        unique (lid, versionName)
    );

    create table ExternalIdentifier_Classification (
        ExternalIdentifier_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (ExternalIdentifier_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table ExternalIdentifier_ExternalIdentifier (
        ExternalIdentifier_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (ExternalIdentifier_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table ExternalIdentifier_Slot (
        ExternalIdentifier_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (ExternalIdentifier_guid, slots_id),
        unique (slots_id)
    );

    create table ExternalLink (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        externalURI bytea,
        primary key (guid),
        unique (lid, versionName)
    );

    create table ExternalLink_Classification (
        ExternalLink_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (ExternalLink_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table ExternalLink_ExternalIdentifier (
        ExternalLink_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (ExternalLink_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table ExternalLink_Slot (
        ExternalLink_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (ExternalLink_guid, slots_id),
        unique (slots_id)
    );

    create table ExtrinsicObject (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        contentVersion varchar(255),
        mimeType varchar(255),
        primary key (guid),
        unique (lid, versionName)
    );

    create table ExtrinsicObject_Classification (
        ExtrinsicObject_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (ExtrinsicObject_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table ExtrinsicObject_ExternalIdentifier (
        ExtrinsicObject_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (ExtrinsicObject_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table ExtrinsicObject_Slot (
        ExtrinsicObject_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (ExtrinsicObject_guid, slots_id),
        unique (slots_id)
    );

    create table RegistryPackage (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        primary key (guid),
        unique (lid, versionName)
    );

    create table RegistryPackage_Classification (
        RegistryPackage_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (RegistryPackage_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table RegistryPackage_ExternalIdentifier (
        RegistryPackage_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (RegistryPackage_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table RegistryPackage_Slot (
        RegistryPackage_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (RegistryPackage_guid, slots_id),
        unique (slots_id)
    );

    create table Service (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        primary key (guid),
        unique (lid, versionName)
    );

    create table ServiceBinding (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        accessURI varchar(255),
        service varchar(255),
        targetBinding varchar(255),
        primary key (guid),
        unique (lid, versionName)
    );

    create table ServiceBinding_Classification (
        ServiceBinding_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (ServiceBinding_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table ServiceBinding_ExternalIdentifier (
        ServiceBinding_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (ServiceBinding_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table ServiceBinding_Slot (
        ServiceBinding_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (ServiceBinding_guid, slots_id),
        unique (slots_id)
    );

    create table ServiceBinding_SpecificationLink (
        ServiceBinding_guid varchar(255) not null,
        specificationLinks_guid varchar(255) not null,
        primary key (ServiceBinding_guid, specificationLinks_guid),
        unique (specificationLinks_guid)
    );

    create table Service_Classification (
        Service_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (Service_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table Service_ExternalIdentifier (
        Service_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (Service_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table Service_ServiceBinding (
        Service_guid varchar(255) not null,
        serviceBindings_guid varchar(255) not null,
        primary key (Service_guid, serviceBindings_guid),
        unique (serviceBindings_guid)
    );

    create table Service_Slot (
        Service_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (Service_guid, slots_id),
        unique (slots_id)
    );

    create table Slot (
        id int8 not null,
        name varchar(255),
        slotType varchar(255),
        primary key (id)
    );

    create table Slot_Values (
        slot_id int8 not null,
        value text
    );

    create table SpecificationLink (
        guid varchar(255) not null,
        home varchar(255),
        description varchar(255),
        lid varchar(255),
        name varchar(255),
        objectType varchar(255),
        status int4,
        versionName varchar(255),
        serviceBinding varchar(255),
        specificationObject varchar(255),
        use_desc text,
        primary key (guid),
        unique (lid, versionName)
    );

    create table SpecificationLink_Classification (
        SpecificationLink_guid varchar(255) not null,
        classifications_guid varchar(255) not null,
        primary key (SpecificationLink_guid, classifications_guid),
        unique (classifications_guid)
    );

    create table SpecificationLink_ExternalIdentifier (
        SpecificationLink_guid varchar(255) not null,
        externalIdentifiers_guid varchar(255) not null,
        primary key (SpecificationLink_guid, externalIdentifiers_guid),
        unique (externalIdentifiers_guid)
    );

    create table SpecificationLink_Slot (
        SpecificationLink_guid varchar(255) not null,
        slots_id int8 not null,
        primary key (SpecificationLink_guid, slots_id),
        unique (slots_id)
    );

    create table Usage_Parameters (
        spec_id varchar(255) not null,
        param text
    );

    alter table Affected_Objects
        add constraint FK756A8649F8E5189F
        foreign key (event_id)
        references AuditableEvent;

    alter table Association_Classification
        add constraint FK19B77BA45470FE6E
        foreign key (Association_guid)
        references Association;

    alter table Association_Classification
        add constraint FK19B77BA435B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table Association_ExternalIdentifier
        add constraint FKA07523925470FE6E
        foreign key (Association_guid)
        references Association;

    alter table Association_ExternalIdentifier
        add constraint FKA0752392D7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table Association_Slot
        add constraint FK9BA8273C5470FE6E
        foreign key (Association_guid)
        references Association;

    alter table Association_Slot
        add constraint FK9BA8273CEDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table AuditableEvent_Classification
        add constraint FK1022B780862C3242
        foreign key (AuditableEvent_guid)
        references AuditableEvent;

    alter table AuditableEvent_Classification
        add constraint FK1022B78035B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table AuditableEvent_ExternalIdentifier
        add constraint FKB4E7116E862C3242
        foreign key (AuditableEvent_guid)
        references AuditableEvent;

    alter table AuditableEvent_ExternalIdentifier
        add constraint FKB4E7116ED7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table AuditableEvent_Slot
        add constraint FK5F574618EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table AuditableEvent_Slot
        add constraint FK5F574618862C3242
        foreign key (AuditableEvent_guid)
        references AuditableEvent;

    alter table ClassificationNode_Classification
        add constraint FK88A989FD27A9EF62
        foreign key (ClassificationNode_guid)
        references ClassificationNode;

    alter table ClassificationNode_Classification
        add constraint FK88A989FD35B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table ClassificationNode_ExternalIdentifier
        add constraint FK10965D6B27A9EF62
        foreign key (ClassificationNode_guid)
        references ClassificationNode;

    alter table ClassificationNode_ExternalIdentifier
        add constraint FK10965D6BD7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table ClassificationNode_Slot
        add constraint FK54EEA0D5EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table ClassificationNode_Slot
        add constraint FK54EEA0D527A9EF62
        foreign key (ClassificationNode_guid)
        references ClassificationNode;

    alter table ClassificationScheme_Classification
        add constraint FKDF02497A92DB76C2
        foreign key (ClassificationScheme_guid)
        references ClassificationScheme;

    alter table ClassificationScheme_Classification
        add constraint FKDF02497A35B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table ClassificationScheme_ExternalIdentifier
        add constraint FK559166892DB76C2
        foreign key (ClassificationScheme_guid)
        references ClassificationScheme;

    alter table ClassificationScheme_ExternalIdentifier
        add constraint FK5591668D7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table ClassificationScheme_Slot
        add constraint FK9C6C289292DB76C2
        foreign key (ClassificationScheme_guid)
        references ClassificationScheme;

    alter table ClassificationScheme_Slot
        add constraint FK9C6C2892EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table Classification_Classification
        add constraint FKFDB7A15F35B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table Classification_Classification
        add constraint FKFDB7A15FDC9B6702
        foreign key (Classification_guid)
        references Classification;

    alter table Classification_ExternalIdentifier
        add constraint FK9825F3CDD7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table Classification_ExternalIdentifier
        add constraint FK9825F3CDDC9B6702
        foreign key (Classification_guid)
        references Classification;

    alter table Classification_Slot
        add constraint FK772E2AB7EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table Classification_Slot
        add constraint FK772E2AB7DC9B6702
        foreign key (Classification_guid)
        references Classification;

    alter table ExternalIdentifier_Classification
        add constraint FKD57280B11F6856E2
        foreign key (ExternalIdentifier_guid)
        references ExternalIdentifier;

    alter table ExternalIdentifier_Classification
        add constraint FKD57280B135B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table ExternalIdentifier_ExternalIdentifier
        add constraint FK4DDCDA1F1F6856E2
        foreign key (ExternalIdentifier_guid)
        references ExternalIdentifier;

    alter table ExternalIdentifier_ExternalIdentifier
        add constraint FK4DDCDA1FD7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table ExternalIdentifier_Slot
        add constraint FK2E06A8891F6856E2
        foreign key (ExternalIdentifier_guid)
        references ExternalIdentifier;

    alter table ExternalIdentifier_Slot
        add constraint FK2E06A889EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table ExternalLink_Classification
        add constraint FKCBDDF50099D2AD02
        foreign key (ExternalLink_guid)
        references ExternalLink;

    alter table ExternalLink_Classification
        add constraint FKCBDDF50035B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table ExternalLink_ExternalIdentifier
        add constraint FK7DC78EEED7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table ExternalLink_ExternalIdentifier
        add constraint FK7DC78EEE99D2AD02
        foreign key (ExternalLink_guid)
        references ExternalLink;

    alter table ExternalLink_Slot
        add constraint FK62F96398EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table ExternalLink_Slot
        add constraint FK62F9639899D2AD02
        foreign key (ExternalLink_guid)
        references ExternalLink;

    alter table ExtrinsicObject_Classification
        add constraint FK48FDE3AF35B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table ExtrinsicObject_Classification
        add constraint FK48FDE3AF86C980AE
        foreign key (ExtrinsicObject_guid)
        references ExtrinsicObject;

    alter table ExtrinsicObject_ExternalIdentifier
        add constraint FK76E28E1DD7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table ExtrinsicObject_ExternalIdentifier
        add constraint FK76E28E1D86C980AE
        foreign key (ExtrinsicObject_guid)
        references ExtrinsicObject;

    alter table ExtrinsicObject_Slot
        add constraint FK50A41107EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table ExtrinsicObject_Slot
        add constraint FK50A4110786C980AE
        foreign key (ExtrinsicObject_guid)
        references ExtrinsicObject;

    alter table RegistryPackage_Classification
        add constraint FK6A0951DC35B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table RegistryPackage_Classification
        add constraint FK6A0951DC199F444E
        foreign key (RegistryPackage_guid)
        references RegistryPackage;

    alter table RegistryPackage_ExternalIdentifier
        add constraint FK2B011DCAD7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table RegistryPackage_ExternalIdentifier
        add constraint FK2B011DCA199F444E
        foreign key (RegistryPackage_guid)
        references RegistryPackage;

    alter table RegistryPackage_Slot
        add constraint FK78EDE374EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table RegistryPackage_Slot
        add constraint FK78EDE374199F444E
        foreign key (RegistryPackage_guid)
        references RegistryPackage;

    alter table ServiceBinding_Classification
        add constraint FKAEF001F52E575D22
        foreign key (ServiceBinding_guid)
        references ServiceBinding;

    alter table ServiceBinding_Classification
        add constraint FKAEF001F535B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table ServiceBinding_ExternalIdentifier
        add constraint FK3E701963D7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table ServiceBinding_ExternalIdentifier
        add constraint FK3E7019632E575D22
        foreign key (ServiceBinding_guid)
        references ServiceBinding;

    alter table ServiceBinding_Slot
        add constraint FK69742ECDEDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table ServiceBinding_Slot
        add constraint FK69742ECD2E575D22
        foreign key (ServiceBinding_guid)
        references ServiceBinding;

    alter table ServiceBinding_SpecificationLink
        add constraint FK74966F2E92F75215
        foreign key (specificationLinks_guid)
        references SpecificationLink;

    alter table ServiceBinding_SpecificationLink
        add constraint FK74966F2E2E575D22
        foreign key (ServiceBinding_guid)
        references ServiceBinding;

    alter table Service_Classification
        add constraint FK8BDF72F0D0E61EE
        foreign key (Service_guid)
        references Service;

    alter table Service_Classification
        add constraint FK8BDF72F035B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table Service_ExternalIdentifier
        add constraint FK43F894DED7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table Service_ExternalIdentifier
        add constraint FK43F894DED0E61EE
        foreign key (Service_guid)
        references Service;

    alter table Service_ServiceBinding
        add constraint FKF520B91AD0E61EE
        foreign key (Service_guid)
        references Service;

    alter table Service_ServiceBinding
        add constraint FKF520B91AC5CE2EEF
        foreign key (serviceBindings_guid)
        references ServiceBinding;

    alter table Service_Slot
        add constraint FK1F328D88EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table Service_Slot
        add constraint FK1F328D88D0E61EE
        foreign key (Service_guid)
        references Service;

    alter table Slot_Values
        add constraint FKEA90AA83B0970954
        foreign key (slot_id)
        references Slot;

    alter table SpecificationLink_Classification
        add constraint FK20FEF90835B199FB
        foreign key (classifications_guid)
        references Classification;

    alter table SpecificationLink_Classification
        add constraint FK20FEF908C4DF0B8E
        foreign key (SpecificationLink_guid)
        references SpecificationLink;

    alter table SpecificationLink_ExternalIdentifier
        add constraint FK93374EF6D7BBDC37
        foreign key (externalIdentifiers_guid)
        references ExternalIdentifier;

    alter table SpecificationLink_ExternalIdentifier
        add constraint FK93374EF6C4DF0B8E
        foreign key (SpecificationLink_guid)
        references SpecificationLink;

    alter table SpecificationLink_Slot
        add constraint FK81C651A0EDD71DBD
        foreign key (slots_id)
        references Slot;

    alter table SpecificationLink_Slot
        add constraint FK81C651A0C4DF0B8E
        foreign key (SpecificationLink_guid)
        references SpecificationLink;

    alter table Usage_Parameters
        add constraint FKAADE9B88721369A2
        foreign key (spec_id)
        references SpecificationLink;

    create sequence hibernate_sequence;

    create index targetObjectIndex on Association(targetObject);
