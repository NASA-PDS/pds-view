    alter table Association 
        add constraint AssociationUniqueLidVersion 
        unique (lid, versionName);
        
   alter table AuditableEvent
        add constraint AuditableEventUniqueLidVersion 
        unique (lid, versionName);
        
   alter table Classification
        add constraint ClassificationUniqueLidVersion 
        unique (lid, versionName);
        
   alter table ClassificationNode
        add constraint ClassificationNodeUniqueLidVersion 
        unique (lid, versionName);
        
   alter table ClassificationScheme
        add constraint ClassificationSchemeUniqueLidVersion 
        unique (lid, versionName);
        
   alter table ExternalIdentifier
        add constraint ExternalIdentifierUniqueLidVersion 
        unique (lid, versionName);
        
   alter table ExternalLink
        add constraint ExternalLinkUniqueLidVersion 
        unique (lid, versionName);
        
   alter table ExtrinsicObject
        add constraint ExtrinsicObjectUniqueLidVersion 
        unique (lid, versionName);
        
   alter table RegistryPackage
        add constraint RegistryPackageUniqueLidVersion 
        unique (lid, versionName);
        
   alter table Service
        add constraint ServiceUniqueLidVersion 
        unique (lid, versionName);
        
   alter table ServiceBinding
        add constraint ServiceBindingUniqueLidVersion 
        unique (lid, versionName);
        
   alter table SpecificationLink
        add constraint SpecificationLinkUniqueLidVersion 
        unique (lid, versionName);
        