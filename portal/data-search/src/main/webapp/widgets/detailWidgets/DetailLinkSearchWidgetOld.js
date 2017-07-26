(function ($) {

AjaxSolr.DetailLinkSearchWidget = AjaxSolr.AbstractTextWidget.extend({
    detailLinkId: 0,

    init: function () {
        var self = this;

        $(document).on('click', 'a.detailLink', function () {
            var id = $(this).attr("detailLinkId");
            self.setDetailDiv(id);
            self.showDetailDiv();
            return false;
        });

        $(document).on('click', '#returnToSearchButtonBottom', function () {
            self.showSearchDiv();
            return false;
        });
        $(document).on('click', '#returnToSearchButtonTop', function () {
            self.showSearchDiv();
            return false;
        });

    },

    setDetailDiv: function(id){
        this.detailLinkId = id;
        var product = self.manager.response.response.docs[id];

        this.setTitle(product);
        console.log("product", product);
        $("#trDetailContainer").empty();
        $("#trDetailContainer").append(this.template(product));


        if(product.file_name){
          var fileListDivHtml = '<div class="panel panel-default">' +
              '<div class="panel-heading detailWidgetFileListTitle">Attached Files</div>' +
              '<ul class="list-group">';

          if(product.file_name.length > 0){
              var fileName = product.file_name;
              for(var i = 0; i < product.file_ref_url.length; i++){
                if(product.file_ref_url[i].endsWith(fileName)){
                    fileListDivHtml += '<li class="list-group-item"><a href="' + product.file_ref_url[i] + '"  download>' + fileName + '</a></li>';
                }
              }
          }

          fileListDivHtml += '</ul>' + '</div>' + '</div>';
          $("#trDetailContainer").append(fileListDivHtml);

        }
    },

    setTitle:function(product){
      $("#trDetailTitle").empty();
      $("#trDetailTitle").append('<span style="font-size:175%;vertical-align: middle;">Tool Detail</span>&nbsp;&nbsp;&nbsp;<button id="returnToSearchButtonTop" type="button" class="btn btn-info btn-sm"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Return To Search</button>');
    },

    template: function (product){
        var type = product.objectType
        var output = "";
        var i;
        output += '<div class="panel panel-default">'
        output += '<table class="table table-striped table-condensed ToolRegistryTable"';
        output += '<tr><td colspan="2" style="background-color:#CCCCCC">' + "<h4>" + product.title + "</h4>" + "</td></tr>";
        output += this.createRow("Abstract", product.service_abstract_desc);
        var urlOutput = "";
        for (i in product.service_url) {
            urlOutput += "<a href='" + product.service_url[i] + "' target='_blank'>" + product.service_url[i] + "</a>, "
        }
        output += this.createRow("URL", urlOutput.slice(0, urlOutput.length - 2));
        output += this.createRow("Support", product.pds_model_version);
        var categoryOutput = "";
        for (i in product.service_category) {
            categoryOutput += product.service_category[i] + ", "
        }
        output += this.createRow("Category", categoryOutput.slice(0, categoryOutput.length - 2));
        if (product.service_interface_type != undefined) {
            var interfaceOutput = "";
            for (i in product.service_interface_type) {
                interfaceOutput += product.service_interface_type[i] + ", "
            }
            output += this.createRow("Interface Type", interfaceOutput.slice(0, interfaceOutput.length - 2));
        }
        output += this.createRow("Description", product.service_description);
        output += this.createRow("Version", product.service_version_id);
        output += this.createRow("Release Date", product.service_release_date);
        if (product.service_software_language != undefined) {
            var languageOutput = "";
            for (i in product.service_software_language) {
                languageOutput += product.service_software_language[i] + ", "
            }
            output += this.createRow("Software Language", languageOutput.slice(0, languageOutput.length - 2));
        }
        output += this.createRow("Supported Operating Systems", product.service_supported_operating_system_note);
        output += this.createRow("System Requirements", product.service_system_requirements_note);
        output += this.createRow("Citation", product.citation_description);
        output += '</table></div>';

        return output;
    },

    attributeSetName: function(product){
        var output = "";

        //Name
        output += '<tr><td colspan="2" style="background-color:#CCCCCC">' + "<h4>" + product.attribute_name + "</h4>" + "</td></tr>";
        output += this.createRow("Version ID", product.attribute_version_id);
        output += this.createRow("Class Name", product.attribute_class_name);
        output += this.createRow("Local Identifier", product.attribute_local_identifier);
        output += this.createRow("Steward ID", product.attribute_steward_id);
        output += this.createRow("Type", product.attribute_type);
        output += this.createRow("Namespace ID", product.attribute_namespace_id);
        output += this.createRow("Nillable Flag", product.attribute_nillable_flag);
        output += this.createRow("Submitter Name", product.attribute_submitter_name);
        output += this.createRow("Definition", product.attribute_definition);
        output += this.createRow("Comment", product.attribute_comment);
        output += this.createRow("Registered By", product.attribute_registered_by);
        output += this.createRow("Registration Authority ID", product.attribute_registration_authority_id);
        output += this.createRow("Concept", product.attribute_concept);

        return output;
    },

    attributeSetTerminologicalEntry: function(product){
        var output = "";

        if(product.attribute_term_entry_name !== undefined){
            if(product.attribute_term_entry_name.length > 0){
                for (var i = 0; i < product.attribute_term_entry_name.length; i++){

                    output += '<tr><td colspan="2" style="background-color:#CCCCCC">' + "<h4>Terminological Entry</h4>" + "</td></tr>";

                    output += this.createRow("Name", product.attribute_term_entry_name[i]);

                    if(product.attribute_term_entry_definition !== undefined){
                        output += this.createRow("Definition", product.attribute_term_entry_definition[i]);
                    }
                    if(product.attribute_term_entry_language !== undefined){
                        output += this.createRow("Language", product.attribute_term_entry_language[i]);
                    }
                    //THIS IS NOT A LIST BUT THE SPREAD SHEET DETAILING THE FIELDS SAYS THAT IT IS
                    output += this.createRow("Preferred Flag", product.attribute_term_entry_preferred_flag);
                }
            }
        }

        return output;
    },

    attributeSetValueDomains: function(product){
        var output = "";

        output += '<tr><td colspan="2" style="background-color:#CCCCCC">' + "<h4>Value Domain</h4>" + "</td></tr>";
        output += this.createRow("Enumeration Flag", product.attribute_value_domain_enumeration_flag);
        output += this.createRow("Value Data Type", product.attribute_value_domain_value_data_type);
        output += this.createRow("Formation Rule", product.attribute_value_domain_formation_rule);
        output += this.createRow("Minimum Characters", product.attribute_value_domain_minimum_characters);
        output += this.createRow("Maxiumum Characters", product.attribute_value_domain_maximum_characters);
        output += this.createRow("Minimum Value", product.attribute_value_domain_minimum_value);
        output += this.createRow("Maximum Value", product.attribute_value_domain_maximum_value);
        output += this.createRow("Pattern", product.attribute_value_domain_pattern);
        output += this.createRow("Unit Of Measure Type", product.attribute_value_domain_unit_of_measure_type);
        output += this.createRow("Conceptual Domain", product.attribute_value_domain_conceptual_domain);
        output += this.createRow("Specified Unit ID", product.attribute_value_domain_specified_unit_id);

        return output;
    },

    attributeSetPermissibleValues: function(product){
        var output = "";

        if(product.attribute_value_domain_value !== undefined){
            if(product.attribute_value_domain_value.length > 0){
                for (var i = 0; i < product.attribute_value_domain_value.length; i++){

                    output += '<tr><td colspan="2" style="background-color:#CCCCCC">' + "<h4>Permissible Value</h4></td></tr>";

                    output += this.createRow("Value", product.attribute_value_domain_value[i]);

                    if(product.attribute_value_domain_value_meaning !== undefined){
                        output += this.createRow("Value Meaning", product.attribute_value_domain_value_meaning[i]);
                    }
                    if(product.attribute_value_domain_value_begin_date !== undefined){
                        output += this.createRow("Value Begin Date", product.attribute_value_domain_value_begin_date[i]);
                    }
                    if(product.attribute_value_domain_value_end_date !== undefined){
                        output += this.createRow("Value End Date", product.attribute_value_domain_value_end_date[i]);
                    }
                }
            }
        }
        return output;
    },

    classSetName: function(product){
        var output = "";

        //Name
        output += '<tr><td colspan="2" style="background-color:#CCCCCC">' + "<h4>" + product.class_name + "</h4>" + "</td></tr>";
        output += this.createRow("Version ID", product.class_version_id);
        output += this.createRow("Local Identifier", product.attribute_value_domain_enumeration_flag);
        output += this.createRow("Steward ID", product.class_steward_id);
        output += this.createRow("Type", product.class_type);
        output += this.createRow("Namespace ID", product.class_namespace_id);
        output += this.createRow("Submitter Name", product.class_submitter_name);
        output += this.createRow("Definition", product.class_definition);
        output += this.createRow("Comment", product.class_comment);
        output += this.createRow("Registered By", product.class_registered_by);
        output += this.createRow("Registration Authority ID", product.class_registration_authority_id);
        output += this.createRow("Abstract Flag", product.class_abstract_flag);
        output += this.createRow("Element Flag", product.class_element_flag);

        return output;
    },

    classSetAssociation: function(product){
        var output = "";

        if(product.class_association_local_identifier !== undefined){
            if(product.class_association_local_identifier.length > 0){
                for (var i = 0; i < product.class_association_local_identifier.length; i++){
                    output += '<tr><td colspan="2" style="background-color:#CCCCCC">' + "<h4>Association</h4>" + "</td></tr>";

                    output += this.createRow("Local Identifier", product.class_association_local_identifier[i]);

                    if(product.class_association_reference_type !== undefined){
                        output += this.createRow("Reference Type", product.class_association_reference_type[i]);
                    }
                    if(product.class_association_minimum_occurrences !== undefined){
                        output += this.createRow("Minimum Occurrences", product.class_association_minimum_occurrences[i]);
                    }
                    if(product.class_association_maximum_occurrences !== undefined){
                        output += this.createRow("Maxiumum Occurrences", product.class_association_maximum_occurrences[i]);
                    }
                    if(product.class_association_constant_value !== undefined){
                        output += this.createRow("Constant Value", product.class_association_constant_value[i]);
                    }
                }
            }
        }

        return output;
    },

    classSetTerminologicalEntry: function(product){
        var output = "";

        if(product.class_term_entry_name !== undefined){
            if(product.class_term_entry_name.length > 0){
                for (var i = 0; i < product.class_term_entry_name.length; i++){
                    output += '<tr><td colspan="2" style="background-color:#CCCCCC">' + "<h4>Terminological Entry</h4>" + "</td></tr>";

                    output += this.createRow("Name", product.class_term_entry_name[i]);

                    if(product.class_term_entry_definition !== undefined){
                        output += this.createRow("Definition", product.class_term_entry_definition[i]);
                    }
                    if(product.class_term_entry_language !== undefined){
                        output += this.createRow("Language", product.class_term_entry_language[i]);
                    }

                    //THIS IS NOT A LIST BUT THE SPREAD SHEET THAT DETAILS THE FIELDS SAYS THAT IT IS
                    output += this.createRow("Preferred Flag",product.class_term_entry_preferred_flag);
                }
            }
        }

        return output;
    },

    createRow: function(name, value){
        if(value !== undefined){
            return '<tr><td>' + name + "</td><td>" + value + "</td></tr>";
        }
        return "";
    },

    createListRow: function(name, value){
        var output = "";
        if(value !== undefined){
            output += '<tr><td>' + name + "</td><td>";
            output += arrayToHtmlList(value);
            output += '</td></tr>';
        }
        return output;
    },

    showDetailDiv: function(){
        $( "#trResult" ).fadeOut(400);
        $( "#trDetailDiv" ).fadeIn(800);
        $( "#trSearch" ).fadeOut(400);

        $("html, body").animate({ scrollTop: 0 }, "slow");
    },

    showSearchDiv: function(){
        var self = this;

        $( "#trDetailDiv" ).fadeOut(300);
        $( "#trResult" ).fadeIn(800);
        $( "#trSearch" ).fadeIn(400);
        setTimeout(function(){
            $("html, body").animate({'scrollTop':$("#detailLink" + self.detailLinkId).offset().top}, 400,
                function(){
                    $("#detailLink" + self.detailLinkId).parent().parent().effect("highlight", {}, 600);
                });
        }, 300);
    },

});

})(jQuery);

function formatIfUndefined(string){
    if(string !== undefined){
        return string;
    }
    else return "";
}
