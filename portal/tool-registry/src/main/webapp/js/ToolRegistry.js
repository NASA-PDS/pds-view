var Manager;
(function ($) {

  //Initialize
  $( document ).ready(function() {
    $( "#ddSearchInput" ).focus();
    init();
  });

  $(function () {
    Manager = new AjaxSolr.Manager({
      solrUrl: 'http://pds-dev.jpl.nasa.gov:8080/search-service/'
    });

    Manager.addWidget(new AjaxSolr.ResultWidget({
      id: 'result',
      target: '#ddDocs'
    }));

    Manager.addWidget(new AjaxSolr.SearchWidget({
      id: 'text',
      target: '#ddSearch'
    }));

    Manager.addWidget(new AjaxSolr.DetailLinkSearchWidget({
      id: 'detailLink',
      target: '#ddResult'
    }));

    Manager.addWidget(new AjaxSolr.PagerWidget({
      id: 'pager',
      target: '#ddPager',
      prevLabel: '<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>',
      nextLabel: '<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>',
      innerWindow: 1,
      renderHeader: function (perPage, offset, total) {
        $('#ddPager-header').html('<span>Displaying ' + Math.min(total, offset + 1) + ' to ' + Math.min(total, offset + perPage) + ' of <span class="badge">' + total + '</span> results.' + '</br></span>');
      }
    }));

    Manager.init();
    Manager.store.addByValue('product-class', 'Product_Attribute_Definition');
    Manager.store.addByValue('sort', 'attribute_name asc');
    Manager.doRequest(false, "search");
  });

})(jQuery);

function init(){
  /*
  //Hide Detail Div
  $( "#ddDetailDiv" ).hide();

  //Set all radio Buttons to default:
  $('#sortOrderRadio2Div' ).attr("class", "radio");
  $('#sortOrderRadio2' ).attr('disabled', false);

  $("#sortOrderRadio1").prop("checked", true);
  $("#sortOrderRadio2").prop("checked", false);

  $("#objectTypeRadio1").prop("checked", true);
  $("#objectTypeRadio2").prop("checked", false);

  $("#pdsOrderRadio1").prop("checked", true);
  $("#pdsOrderRadio2").prop("checked", false);
  $("#pdsOrderRadio3").prop("checked", false);

  $('html,body').scrollTop(0);
  */

  var wadlInstructions = "If the service interfaces for this software are described by a Web Application Description Language (WADL) file or a Web Service Definition Language (WSDL) file, then that file can be attached and described here.";
  //var formInstructions = "Use this form to submit a new tool or service to the PDS Tool/Service Registry.";
  var formInstructions = "If the service interfaces for this software are described by a Web Application Description Language (WADL) file or a Web Service Definition Language (WSDL) file, then that file can be attached and described here.";

  $( document ).ready(function() {
    //Startup
    $( "#addToolContainer" ).hide();
    $( "#toolSoftwareInformation" ).hide();
    $( "#submitterInformation" ).hide();
    $( "#submissionCompleteContainer" ).hide();

    //Setup date picker
    var picker = new Pikaday({
      field: document.getElementById('releaseDateInput'),
      firstDay: 1,
      format: 'YYYY-MM-DD',
      minDate: new Date(2000, 0, 1),
      maxDate: new Date(2020, 12, 31),
      yearRange: [2000,2020]
    });
    //Setup WASDL/WADL file input
    $(document).on('change', '#wFileInput', function() {
      var x = document.getElementById("wFileInput");
      var txt = "";
      if ('files' in x) {
          if (x.files.length == 0) {
              txt = "Select one or more files.";
          } else {
              for (var i = 0; i < x.files.length; i++) {
                  var title = "";
                  var file = x.files[i];
                  if ('name' in file) {
                    var title = '<p class="wFileTitle">' + (i+1) + ' ' + file.name + '</p>'
                  }

                  txt += '<div class="panel panel-default">' +
                    '<div class="panel-body">' +
                    title +
                      '<div class="form-group">' +
                        '<label for="wFileName">Name <span class="asterisk">*</span></br><span class="labelDescription">Provide a name for the service interface.</span></label>' +
                        '<input type="text" class="form-control wFileName" placeholder="" maxlength="255">' +
                      '</div>' +
                      '<div class="form-group">' +
                        '<label for="wFileType">Type <span class="asterisk">*</span></br><span class="labelDescription">Identify whether the service description is in WADL or WSDL format.</span></label>' +
                        '<div class="radio wFileType">' +
                          '<label class="wFileTypeRadios">' +
                            '<input type="radio" name="wFileTypeRadios' + i +'" value="wadl"> WADL' +
                          '</label>' +
                          '<label class="wFileTypeRadios">' +
                            '<input type="radio" name="wFileTypeRadios' + i + '" value="wsdl"> WSDL' +
                          '</label>' +
                        '</div>' +
                      '</div>' +
                      '<div class="form-group">' +
                        '<label for="wFileOffset">Offset <span class="asterisk">*</span></br><span class="labelDescription">Provide the offset (in bytes) into the file for the location of this service description.</span></label>' +
                        '<input type="number" class="form-control wFileOffset" placeholder="" maxlength="255">' +
                      '</div>' +
                      '<div class="form-group">' +
                        '<label for="wFileDescription">Description</br><span class="labelDescription">Provide a desciption of the service interface.</span></label>' +
                        '<textarea class="form-control vresize wFileDescription" rows="3" placeholder=""></textarea>' +
                      '</div>' +

                    '</div>' +
                  '</div>'
              }
          }
      }
      else {
          if (x.value == "") {
              txt += "Select one or more files.";
          } else {
              txt += "The files property is not supported by your browser!";
              txt  += "<br>The path of the selected file: " + x.value; // If the browser does not support the files property, it will return the path of the selected file instead.
          }
      }
      document.getElementById("serviceInterfaceInformationDescription").innerHTML = txt;
    });

    //Allow tab use
    $('#pageControlTabs a').click(function (e) {
      console.log("tabClick");
      e.preventDefault();
      $(this).tab('show');
    });

    //On tab press
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      // newly activated tab     e.target
      // previous active tab     e.relatedTarget
      var selected = $(e.target).attr("aria-controls");
      if(selected === "add"){
          $( "#searchContainer" ).fadeOut( "slow", function() {
            $( "#addToolContainer" ).fadeIn( "slow", function() {
            });
          });
      }
      if(selected === "searchForTools"){
        $( "#addToolContainer" ).fadeOut( "slow", function() {
          $( "#searchContainer" ).fadeIn( "slow", function() {
          });
        });
      }
    });

    $( "#addToolButton" ).click(function() {
      $( "#searchContainer" ).fadeOut( "slow", function() {
        $( "#addToolContainer" ).fadeIn( "slow", function() {
        });
      });
      $('#pageControlTabs a[href="#add"]').tab('show')
    });


    $( ".toStepOneButton" ).click(function() {
      $( "#toolSoftwareInformation" ).fadeOut( "slow", function() {
        $( "#toolDescription" ).fadeIn( "slow", function() {
        });
      });
      $( "#submitterInformation" ).fadeOut( "slow", function() {
      });

      $( "#steps" ).fadeOut( "slow", function() {
        $('#steps li:nth-child(1)').removeClass('current');
        $('#steps li:nth-child(2)').removeClass('current');
        $('#steps li:nth-child(3)').removeClass('current');
        $('#steps li:nth-child(1)').addClass('current');
        $( "#steps" ).fadeIn( "slow", function() {
        });
      });

      $( "#formHeaderInstructions" ).fadeOut( "slow", function() {
        $( "#formHeaderInstructions" ).text(formInstructions);
        $( "#formHeaderInstructions" ).fadeIn( "slow", function() {
        });
      });
    });

    $( ".toStepTwoButton" ).click(function() {
      $( "#submitterInformation" ).fadeOut( "slow", function() {
      });

      $( "#toolDescription" ).fadeOut( "slow", function() {
        $( "#toolSoftwareInformation" ).fadeIn( "slow", function() {
        });
      });


      $( "#steps" ).fadeOut( "slow", function() {
        $('#steps li:nth-child(1)').removeClass('current');
        $('#steps li:nth-child(2)').removeClass('current');
        $('#steps li:nth-child(3)').removeClass('current');
        $('#steps li:nth-child(2)').addClass('current');
        $( "#steps" ).fadeIn( "slow", function() {
        });
      });

      $( "#formHeaderInstructions" ).fadeOut( "slow", function() {
        $( "#formHeaderInstructions" ).text(wadlInstructions);
        $( "#formHeaderInstructions" ).fadeIn( "slow", function() {
        });
      });
    });

    $( ".toStepThreeButton" ).click(function() {
      $( "#toolSoftwareInformation" ).fadeOut( "slow", function() {
        $( "#submitterInformation" ).fadeIn( "slow", function() {
        });
      });
      $( "#toolDescription" ).fadeOut( "slow", function() {
      });

      $( "#steps" ).fadeOut( "slow", function() {
        $('#steps li:nth-child(1)').removeClass('current');
        $('#steps li:nth-child(2)').removeClass('current');
        $('#steps li:nth-child(3)').removeClass('current');
        $('#steps li:nth-child(3)').addClass('current');
        $( "#steps" ).fadeIn( "slow", function() {
        });
      });

      $( "#formHeaderInstructions" ).fadeOut( "slow", function() {
        $( "#formHeaderInstructions" ).text(formInstructions);
        $( "#formHeaderInstructions" ).fadeIn( "slow", function() {
        });
      });
    });

    $( "#addUrlButton" ).click(function() {
      $( "#urlContainer" ).append( '<div class="form-group">' +
        '<label for="urlInput">URL</label>' +
        '<input type="text" class="form-control urlInput" placeholder="" maxlength="255">' +
        '</div>'
      );
    });

    $( "#addSoftwareLanguageButton" ).click(function() {
      $( "#softwareLanguageContainer" ).append( '<div class="form-group">' +
        '<label for="softwareLanguageInput">Software Language</label>' +
        '<input type="text" class="form-control softwareLanguageInput" placeholder="" maxlength="255">' +
        '</div>'
      );
    });

    $( "#finishButton" ).click(function(){
      var name = $('#toolNameInput').val();
      var type = $('#toolTypeInput option:selected').text();
      var abstract_desc = $("#abstractDescriptionInput").val();
      var description = $("#descriptionInput").val();

      var url = $("#urlContainer .urlInput").map(function() {
        return this.value;
      }).get().join(",");

      var version_id = $("#versionInput").val();
      var release_date = $("#releaseDateInput").val();
      var support = $('#support input[name=supportRadios]:checked').val();
      var category = $('#category input[name=categoryRadios]:checked').val();
      var interface_type = $('#interfaceType input[name=interfaceTypeRadios]:checked').val();

      var software_language = $("#softwareLanguageContainer .softwareLanguageInput").map(function() {
        return this.value;
      }).get().join(",");

      var supported_operating_systems = $("#supportedOperatingSystemsInput").val();
      var system_requirements = $("#systemRequirementsInput").val();
      var citation = $("#citationInput").val();
      var submitter_name = $("#submitterNameInput").val();
      var submitter_institution = $("#submitterInstitutionInput").val();
      var submitter_email = $("#submitterEmailInput").val();


      var wFileNames = $("#serviceInterfaceInformationDescription .wFileName").map(function() {
        return this.value;
      }).get().join(",");
      var wFileTypes = $('.wFileType input:checked').map(function() {
        return this.value;
      }).get().join(",");
      var wFileOffsets = $("#serviceInterfaceInformationDescription .wFileOffset").map(function() {
        return this.value;
      }).get().join(",");
      var wFileDescriptions = $("#serviceInterfaceInformationDescription .wFileDescription").map(function() {
        return this.value;
      }).get().join(",");

      console.log("name: ", name);
      console.log("type: ", type);
      console.log("abstract_desc: ", abstract_desc);
      console.log("description: ", description);
      console.log("url: ", url);
      console.log("version_id: ", version_id);
      console.log("release_date: ", release_date);
      console.log("support: ", support);
      console.log("category: ", category);
      console.log("interface_type: ", interface_type);
      console.log("software_language: ", software_language);
      console.log("supported_operating_systems: ", supported_operating_systems);
      console.log("system_requirements: ", system_requirements);
      console.log("citation: ", citation);
      console.log("submitter_name: ", submitter_name);
      console.log("submitter_institution: ", submitter_institution);
      console.log("submitter_email: ", submitter_email);

      console.log("wfileNames", wFileNames);
      console.log("wFileTypes", wFileTypes);
      console.log("wFileOffsets", wFileOffsets);
      console.log("wFileDescriptions", wFileDescriptions);

      //Create xml string
      var xmlString = '<?xml version="1.0" encoding="UTF-8"?>';
      xmlString += '<?xml-model href="http://pds.jpl.nasa.gov/pds4/pds/v1/PDS4_PDS_1500.sch" schematypens="http://purl.oclc.org/dsdl/schematron"?>';
      xmlString += '<Product_Service xmlns="http://pds.nasa.gov/pds4/pds/v1" ';
      xmlString += 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" ';
      xmlString += 'xsi:schemaLocation="http://pds.nasa.gov/pds4/pds/v1 https://pds.nasa.gov/pds4/pds/v1/PDS4_PDS_1500.xsd">';
        xmlString += '<Identification_Area>';
          if(support === "pds3"){
            xmlString += '<logical_identifier>urn:nasa:pds:context_pds3:service:pds4_validate_tool</logical_identifier>';
          }
          else{//if support === "pds4"
            xmlString += '<logical_identifier>urn:nasa:pds:context:service:pds4_validate_tool</logical_identifier>';
          }
          xmlString += '<version_id>1.0</version_id>';
          xmlString += '<title>PDS4 Validate Tool</title>';
          xmlString += '<information_model_version>1.5.0.0</information_model_version>';
          xmlString += '<product_class>Product_Service</product_class>';
          xmlString += '<Modification_History>';
            xmlString += '<Modification_Detail>';
              xmlString += '<modification_date>2016-01-12</modification_date>';
              xmlString += '<version_id>1.0</version_id>';
              xmlString += '<description>A new version.</description>';
            xmlString += '</Modification_Detail>';
          xmlString += '</Modification_History>';
          xmlString += '<Citation_Information>';
            xmlString += '<publication_year>2015-09-30</publication_year>';
            xmlString += '<description>';
              if(citation){
                if(citation.trim().length > 0){
                  xmlString += citation;
                }
                else{
                  xmlString += 'Copyright 2010-2016, by the California Institute of Technology. ALL RIGHTS RESERVED. United States Government sponsorship acknowledged. Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology. This software is subject to U. S. export control laws and regulations (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software is subject to U.S. export control laws and regulations, the recipient has the responsibility to obtain export licenses or other export authority as may be required before exporting such information to foreign countries or providing access to foreign nationals.';
                }
              }
              else{
                xmlString += 'Copyright 2010-2016, by the California Institute of Technology. ALL RIGHTS RESERVED. United States Government sponsorship acknowledged. Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology. This software is subject to U. S. export control laws and regulations (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software is subject to U.S. export control laws and regulations, the recipient has the responsibility to obtain export licenses or other export authority as may be required before exporting such information to foreign countries or providing access to foreign nationals.';
              }
            xmlString += '</description>'
          xmlString += '</Citation_Information>';
        xmlString += '</Identification_Area>';
        xmlString += '<Service>';
          xmlString += '<name>' + name + '</name>';
          xmlString += '<abstract_desc>' + abstract_desc + '</abstract_desc>';
          xmlString += '<description>' + description + '</description>';
          xmlString += '<version_id>' + version_id + '</version_id>';
          //xmlString += '<url>https://pds.nasa.gov/pds4/software/validate/</url>';
          for(var i = 0; i < url.split(",").length; i++){
            var u = url.split(",")[i];
            xmlString += '<url>' + u + '</url>';
          }
          xmlString += '<release_date>' + release_date + '</release_date>';
          xmlString += '<service_type>' + type + '</service_type>';
          xmlString += '<interface_type>' + interface_type + '</interface_type>';
          xmlString += '<category>' + category + '</category>';
          //xmlString += '<software_language>' + software_language + '</software_language>';
          for(var i = 0; i < software_language.split(",").length; i++){
            var softwareLanguage = software_language.split(",")[i];
            xmlString += '<software_language>' + softwareLanguage + '</software_language>';
          }
          xmlString += '<supported_operating_systems>' + supported_operating_systems + '</supported_operating_systems>';
          xmlString += '<system_requirements_note>' + system_requirements + '</system_requirements_note>';
          xmlString += '<submitter_name>' + submitter_name + '</submitter_name>';
          xmlString += '<submitter_institution>' + submitter_institution + '</submitter_institution>';
          xmlString += '<submitter_email>' + submitter_email + '</submitter_email>';
        xmlString += '</Service>';
      xmlString += '</Product_Service>';

      /*
      var xmlString = '<?xml version="1.0" encoding="UTF-8"?>';
      xmlString += '<Product_Service xmlns="http://pds.nasa.gov/pds4/pds/v1" ';
      xmlString += 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" ';
      xmlString += 'xsi:schemaLocation="http://pds.nasa.gov/pds4/pds/v1 https://pds.nasa.gov/pds4/pds/v1/PDS4_PDS_1500.xsd">';
        xmlString += '<Identification_Area>';
          xmlString += '<logical_identifier>urn:nasa:pds:service:pds4_validate_tool</logical_identifier>';
          xmlString += '<version_id>1.0</version_id>';
          xmlString += '<title>PDS4 Validate Tool</title>';
          xmlString += '<information_model_version>1.5.0.0</information_model_version>';
          xmlString += '<product_class>Product_Service</product_class>';
          xmlString += '<Modification_History>';
            xmlString += '<Modification_Detail>';
              xmlString += '<modification_date>2016-01-12</modification_date>';
              xmlString += '<version_id>1.0</version_id>';
              xmlString += '<description>A new version.</description>';
            xmlString += '</Modification_Detail>';
          xmlString += '</Modification_History>';
        xmlString += '</Identification_Area>';
        xmlString += '<Service>';
          xmlString += '<name>PDS4 Validate Tool</name>';
          xmlString += '<abstract_desc>Software for validating PDS4 product labels and product data. The associated specific schema for the product label specifies syntactic and semantic constraints. The product label itself specifies the constraints for the data. </abstract_desc>';
          xmlString += '<version_id>1.8</version_id>';
          xmlString += '<url>https://pds.nasa.gov/pds4/software/validate/</url>';
          xmlString += '<release_date>2015-09-30</release_date>';
          xmlString += '<service_type>Tool</service_type>';
          xmlString += '<interface_type>Command-Line</interface_type>';
          xmlString += '<category>Validation</category>';
          xmlString += '<software_language>Java</software_language>';
          xmlString += '<system_requirements_note>The software was specifically compiled for Java version 1.6 and has been tested with this version and version 1.7 and 1.8. Since support for Java 1.6 ended over two years ago, we suggest an environment of at least Java 1.7.</system_requirements_note>';
        xmlString += '</Service>';
      xmlString += '</Product_Service>';
      */

      var xmlDom = $.parseXML(xmlString);
      //console.log("xmlDom", xmlDom);

      var xmlReString = "";
      if(window.ActiveXObject){
        xmlReString = xmlDom.xml;
      }
      else{
        var xmlReString = (new XMLSerializer()).serializeToString(xmlDom);
      }
      console.log("xmlReString", xmlReString);
      //Upload values as file
      var file = new File([new Blob([xmlReString])], "test.xml");
      var formData = new FormData();
      formData.append("file", file, file.name);
      formData.append("path", new Date().getTime());
      $.ajax({
        url: "http://localhost:8080/transport-upload/upload",
        type: "POST",
        data: formData,
        cache: false,
        contentType: false,
        processData: false
      });


      //Upload wadl and wsdl files
      for(var i = 0; i < $('#wFileInput')[0].files.length; i++){
        file = $('#wFileInput')[0].files[i];

        formData = new FormData();
        formData.append("file", file, file.name);
        formData.append("path", new Date().getTime());
        $.ajax({
          url: "http://localhost:8080/transport-upload/upload",
          type: "POST",
          data: formData,
          cache: false,
          contentType: false,
          processData: false
        });
      }

      $( "#addToolFormContainer" ).fadeOut( "slow", function() {
        $( "#submissionCompleteContainer" ).fadeIn( "slow", function() {
        });
      });
      $( "#steps" ).fadeOut( "slow", function() {
      });
      console.log("submitted");
    });

    $( "#backToSiteButton" ).click(function() {

        $('#pageControlTabs a:first').tab('show');
        $( "#addToolContainer" ).fadeOut( "slow", function() {
          $( "#searchContainer" ).fadeIn( "slow", function() {

            $( "#submissionCompleteContainer" ).hide( "fast", function() {
            });
            $( "#addToolFormContainer" ).show("fast");
            $( "#steps" ).show("fast");

            $( "#submitterInformation" ).hide("fast");

            $( "#toolDescription" ).hide( "fast", function() {
              $( "#toolSoftwareInformation" ).show( "fast", function() {
              });
            });

            $( "#steps" ).hide( "fast", function() {
              $('#steps li:nth-child(1)').removeClass('current');
              $('#steps li:nth-child(2)').removeClass('current');
              $('#steps li:nth-child(3)').removeClass('current');
              $('#steps li:nth-child(2)').addClass('current');
              $( "#steps" ).show( "fast", function() {
              });
            });

            $( "#formHeaderInstructions" ).hide( "fast", function() {
              $( "#formHeaderInstructions" ).text(wadlInstructions);
              $( "#formHeaderInstructions" ).show( "fast", function() {
              });
            });

            $( "#toolSoftwareInformation" ).hide( "fast", function() {
              $( "#toolDescription" ).show( "fast", function() {
              });
            });
            $( "#submitterInformation" ).hide( "fast", function() {
            });

            $( "#steps" ).hide( "fast", function() {
              $('#steps li:nth-child(1)').removeClass('current');
              $('#steps li:nth-child(2)').removeClass('current');
              $('#steps li:nth-child(3)').removeClass('current');
              $('#steps li:nth-child(1)').addClass('current');
              $( "#steps" ).show( "fast", function() {
              });
            });

            $( "#formHeaderInstructions" ).hide( "fast", function() {
              $( "#formHeaderInstructions" ).text(formInstructions);
              $( "#formHeaderInstructions" ).show( "fast", function() {
              });
            });

          });
        });

    });

  });
}
