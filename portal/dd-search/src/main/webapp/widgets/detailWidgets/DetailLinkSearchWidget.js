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
        $("#ddDetailContainer").empty();
        $("#ddDetailContainer").append(this.template(product));
    },

    setTitle:function(product){
        var type = product.objectType 

        if(type === "Product_Attribute_Definition"){
            $("#ddDetailTitle").empty();
            $("#ddDetailTitle").append('<span style="font-size:175%;vertical-align: middle;">Attribute Detail&nbsp;</span><button id="returnToSearchButtonTop" type="button" class="btn btn-info btn-sm"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Return To Search</button>');
        }
        else if(type === "Product_Class_Definition"){
            $("#ddDetailTitle").empty();
            $("#ddDetailTitle").append('<span style="font-size:175%;vertical-align: middle;">Class Detail&nbsp;</span><button id="returnToSearchButtonTop" type="button" class="btn btn-info btn-sm"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Return To Search</button>');
        }
        
    },

    template: function (product){
        var type = product.objectType 
        var output = "";

        if(type === "Product_Attribute_Definition"){
            output += '<div class="panel panel-default">'
            output += '<table class="table table-striped table-condensed"';
            output += this.attributeSetName(product);
            output += this.attributeSetTerminologicalEntry(product);
            output += this.attributeSetValueDomains(product);
            output += this.attributeSetPermissibleValues(product);
            output += '</table></div>';
            return output;
        }
        else if(type === "Product_Class_Definition"){
            output += '<div class="panel panel-default">'
            output += '<table class="table table-striped table-condensed"';
            output += this.classSetName(product);
            output += this.classSetAssociation(product);
            output += this.classSetTerminologicalEntry(product);
            //Attribute Reference
            //Class Reference
            output += '</table></div>';

            return output;
        }

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
        $( "#searchDiv" ).fadeOut(400);
        $( "#ddDetailDiv" ).fadeIn(800);

        $("html, body").animate({ scrollTop: 0 }, "slow");
    },

    showSearchDiv: function(){
        var self = this;
        
        $( "#ddDetailDiv" ).fadeOut(300);
        $( "#searchDiv" ).fadeIn(800);
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