var Manager;
(function ($) {

  //Initialize
  $( document ).ready(function() {
    $( "#ddSearchInput" ).focus();
    init();
  });

  $(function () {
    Manager = new AjaxSolr.Manager({
      //solrUrl: 'http://pds-dev.jpl.nasa.gov:8080/search-service/'
      solrUrl: 'http://pds-gamma.jpl.nasa.gov/services/search/'
      //search?product-class=Product_Service&return-type=xml
    });

    Manager.addWidget(new AjaxSolr.ResultWidget({
      id: 'result',
      target: '#trDocs'
    }));

    Manager.addWidget(new AjaxSolr.SearchWidget({
      id: 'text',
      target: '#trSearch'
    }));

    Manager.addWidget(new AjaxSolr.DetailLinkSearchWidget({
      id: 'detailLink',
      target: '#trResult'
    }));

    Manager.addWidget(new AjaxSolr.PagerWidget({
      id: 'pager',
      target: '#trPager',
      prevLabel: '<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>',
      nextLabel: '<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>',
      innerWindow: 1,
      renderHeader: function (perPage, offset, total) {
        $('#ddPager-header').html('<span>Displaying ' + Math.min(total, offset + 1) + ' to ' + Math.min(total, offset + perPage) + ' of <span class="badge">' + total + '</span> results.' + '</br></span>');
      }
    }));

    Manager.init();
    Manager.store.addByValue('product-class', 'Product_Service');
    Manager.store.addByValue('sort', 'service_name asc');
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
  var wadlInstructions = "Use this form to submit a new tool or service to the PDS Tool/Service Registry.";
  var formInstructions = "Use this form to submit a new tool or service to the PDS Tool/Service Registry.";
  //var formInstructions = "If the service interfaces for this software are described by a Web Application Description Language (WADL) file or a Web Service Definition Language (WSDL) file, then that file can be attached and described here.";

  $( document ).ready(function() {
    //Startup
    $( "#addToolContainer" ).hide();
    $( "#toolSoftwareInformation" ).hide();
    $( "#submitterInformation" ).hide();
    $( "#submissionCompleteContainer" ).hide();
    $( "#removeUrlButton" ).hide();
    $( "#removeSoftwareLanguageButton" ).hide();

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
                        '<label for="wFileDescription">Description</br><span class="labelDescription">Provide a description of the service interface.</span></label>' +
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
          /*
          $( "#trSearch" ).fadeOut( "slow", function() {
          });*/
      }
      if(selected === "searchForTools"){
        $( "#addToolContainer" ).fadeOut( "slow", function() {
          $( "#searchContainer" ).fadeIn( "slow", function() {
          });
        });
        /*
        $( "#trSearch" ).fadeIn( "slow", function() {
        });*/
      }
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
        '<input type="text" class="form-control urlInput" placeholder="" maxlength="255">' +
        '</div>'
      );

      $( "#removeUrlButton" ).show();
    });

    $( "#removeUrlButton" ).click(function() {
      var urlInputs = $("#urlContainer .form-group");

      var length = urlInputs.length;

      urlInputs[urlInputs.length - 1].remove();

      if( (length - 1) < 2){
        $( "#removeUrlButton" ).hide();
      }
    });

    $( "#addSoftwareLanguageButton" ).click(function() {
      $( "#softwareLanguageContainer" ).append( '<div class="form-group">' +
        '<input type="text" class="form-control softwareLanguageInput" placeholder="" maxlength="255">' +
        '</div>'
      );

      $( "#removeSoftwareLanguageButton" ).show();
    });

    $( "#removeSoftwareLanguageButton" ).click(function() {
      var softwareLanguageInputs = $("#softwareLanguageContainer .form-group");

      var length = softwareLanguageInputs.length;

      softwareLanguageInputs[softwareLanguageInputs.length - 1].remove();

      if( (length - 1) < 2){
        $( "#removeSoftwareLanguageButton" ).hide();
      }
    });

    $( "#finishButton" ).click(function(){
      var name = $('#toolNameInput').val();
      var type = $('#toolTypeInput option:selected').text();
      var abstract_desc = $("#abstractDescriptionInput").val();
      var description = $("#descriptionInput").val();

      var url = $("#urlContainer .urlInput").map(function() {
        return this.value;
      }).get().join(",");

      var user_input_version_id = $("#versionInput").val();
      var release_date = $("#releaseDateInput").val();
      var support = $('#support .radio input[name=supportRadios]:checked').val();

      var category = $("#category .checkbox input[name=categoryCheckboxes]:checked").map(function() {
        return this.value;
      }).get().join(",");

      var interface_type = $("#interfaceType .checkbox input[name=interfaceTypeCheckboxes]:checked").map(function() {
        return this.value;
      }).get().join(",");

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
      console.log("version_id: ", user_input_version_id);
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

      var version_id = "1.0";

      var uploadDate = new Date();
      var dateString = formatDateString(uploadDate);
      var currentDateString = formatCurrentDateString(uploadDate);

      var xmlString = '<?xml version="1.0" encoding="UTF-8"?>';
      xmlString += '<?xml-model href="http://pds.jpl.nasa.gov/pds4/pds/v1/PDS4_PDS_1600.sch" schematypens="http://purl.oclc.org/dsdl/schematron"?>';
      xmlString += '<Product_Service xmlns="http://pds.nasa.gov/pds4/pds/v1" ';
      xmlString += 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" ';
      xmlString += 'xsi:schemaLocation="http://pds.nasa.gov/pds4/pds/v1 https://pds.nasa.gov/pds4/pds/v1/PDS4_PDS_1600.xsd">';
        xmlString += '<Identification_Area>';
          if(support === "pds3"){
            xmlString += '<logical_identifier>urn:nasa:pds:context_pds3:service:' + formatLogicalIdentifier(name) + '</logical_identifier>';
          }
          else{//if support === "pds4"
            xmlString += '<logical_identifier>urn:nasa:pds:context:service:' + formatLogicalIdentifier(name) + '</logical_identifier>';
          }
          xmlString += '<version_id>' + version_id + '</version_id>';
          xmlString += '<title>' + name + '</title>';
          xmlString += '<information_model_version>1.6.0.0</information_model_version>';
          xmlString += '<product_class>Product_Service</product_class>';
          if(!isInputEmpty(citation)){
            xmlString += '<Citation_Information>';
                xmlString += '<publication_year>' + release_date + '</publication_year>';
                xmlString += '<description>' + citation + '</description>';
            xmlString += '</Citation_Information>';
          }
          xmlString += '<Modification_History>';
            xmlString += '<Modification_Detail>';
              xmlString += '<modification_date>' + currentDateString + '</modification_date>';
              xmlString += '<version_id>' + version_id + '</version_id>';
              xmlString += '<description>A new version.</description>';
            xmlString += '</Modification_Detail>';
          xmlString += '</Modification_History>';
        xmlString += '</Identification_Area>';
        xmlString += '<Service>';
          xmlString += '<name>' + name + '</name>';
          xmlString += '<abstract_desc>' + abstract_desc + '</abstract_desc>';
          if(!isInputEmpty(user_input_version_id)){
            xmlString += '<version_id>' + user_input_version_id + '</version_id>';
          }
          for(var i = 0; i < url.split(",").length; i++){
            var string = url.split(",")[i];
            xmlString += '<url>' + string + '</url>';
          }
          if(!isInputEmpty(release_date)){
            xmlString += '<release_date>' + release_date + '</release_date>';
          }
          xmlString += '<service_type>' + type + '</service_type>';

          if(!isMultivalInputEmpty(interface_type)){
            for(var i = 0; i < interface_type.split(",").length; i++){
              var string = interface_type.split(",")[i];
              xmlString += '<interface_type>' + string + '</interface_type>';
            }
          }

          for(var i = 0; i < category.split(",").length; i++){
            var string = category.split(",")[i];
            xmlString += '<category>' + string + '</category>';
          }


          if(!isMultivalInputEmpty(software_language)){
            for(var i = 0; i < software_language.split(",").length; i++){
              var softwareLanguage = software_language.split(",")[i];
              xmlString += '<software_language>' + softwareLanguage + '</software_language>';
            }
          }

          if(!isInputEmpty(supported_operating_systems)){
            xmlString += '<supported_operating_system_note>' + supported_operating_systems + '</supported_operating_system_note>';
          }
          if(!isInputEmpty(system_requirements)){
            xmlString += '<system_requirements_note>' + system_requirements + '</system_requirements_note>';
          }
          if(!isInputEmpty(description)){
            xmlString += '<description>' + description + '</description>';
          }
        xmlString += '</Service>';


        var wFiles = $('#wFileInput')[0].files;
        if( wFiles.length > 0){
          for(var i = 0; i < wFiles.length; i++){
            xmlString += '<File_Area_Service_Description>';

            xmlString += '<File>';
              xmlString += '<file_name>' + wFiles[i].name + '</file_name>';
              xmlString += '<file_size unit="byte">' + wFiles[i].size + '</file_size>';
            xmlString += '</File>';

            xmlString += '<Service_Description>';
              xmlString += '<name>' + wFileNames.split(",")[i] + '</name>';
              xmlString += '<offset unit="byte">' + wFileOffsets.split(",")[i] + '</offset>';
              xmlString += '<parsing_standard_id>' + convertWFileType(wFileTypes.split(",")[i]) + '</parsing_standard_id>';
              xmlString += '<description>' + wFileDescriptions.split(",")[i] + '</description>';
            xmlString += '</Service_Description>';

          xmlString += '</File_Area_Service_Description>';
        }
      }

      xmlString += '<!--' + '\n'
        xmlString += 'Submitter Information\n'
        xmlString += 'Submitter Name:        ' + submitter_name + '\n';
        xmlString += 'Submitter Institution: ' + submitter_institution + '\n';
        xmlString += 'Submitter Email:       ' + submitter_email + '\n';
      xmlString += '-->' + '\n'

      xmlString += '</Product_Service>';
      var xmlDom = $.parseXML(xmlString);
      //console.log("xmlDom", xmlDom);

      var xmlReString = "";
      if(window.ActiveXObject){
        xmlReString = xmlDom.xml;
      }
      else{
        var xmlReString = (new XMLSerializer()).serializeToString(xmlDom);
      }

      var bxml = vkbeautify.xml(xmlReString);
      console.log(bxml);

      //Upload values as file
      var fileName = formatFileName(name, version_id);
      var file = new File([new Blob([bxml])], "test.xml");

      var formData = new FormData();
      formData.append("file", file, fileName);
      formData.append("path", dateString);

      $.ajax({
        //url: "http://localhost:8080/transport-upload/upload",
        url: "http://pds-gamma.jpl.nasa.gov/services/transport-upload/upload",
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
        formData.append("path", dateString);
        $.ajax({
          //url: "http://pds-gamma.jpl.nasa.gov/services/transport-upload/upload",
          url: "http://pds-gamma.jpl.nasa.gov/services/transport-upload/upload",
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

      //Clear input fields
      $('#toolNameInput').val("");

      var optionValues = [];
      $('#toolTypeInput option').each(function() {
          optionValues.push($(this).val());
      });
      $("#toolTypeInput").val(optionValues[0]);

      $("#abstractDescriptionInput").val("");
      $("#descriptionInput").val("");
      $('#support .radio input[name=supportRadios]').prop("checked", false);
      $("#category .checkbox input[name=categoryCheckboxes]").prop("checked", false);
      $("#interfaceType .checkbox input[name=interfaceTypeCheckboxes]").prop("checked", false);
      $("#versionInput").val("");
      $("#releaseDateInput").val("");

      $("#softwareLanguageContainer .softwareLanguageInput").val("");
      var softwareLanguageInputs = $("#softwareLanguageContainer .form-group");
      var softwareLanguageInputsLength = softwareLanguageInputs.length;
      for( var i = softwareLanguageInputsLength - 1; i > 0; i--){
        softwareLanguageInputs[i].remove();
        if(i <= 1 ){
          $( "#removeSoftwareLanguageButton" ).hide();
        }
      }

      $("#supportedOperatingSystemsInput").val("");
      $("#systemRequirementsInput").val("");
      $("#citationInput").val("");

      $("#urlContainer .urlInput").val("");
      var urlInputs = $("#urlContainer .urlInput");
      var urlInputsLength = urlInputs.length;
      for( var i = urlInputsLength - 1; i > 0; i--){
        urlInputs[i].remove();
        if(i <= 1 ){
          $( "#removeUrlButton" ).hide();
        }
      }

      var wFileInputs = $("#wFileInput");
      wFileInputs.replaceWith(wFileInputs.val('').clone(true));
      serviceInterfaceInformationDescription.innerHTML = "";

      $("#submitterNameInput").val("");
      $("#submitterInstitutionInput").val("");
      $("#submitterEmailInput").val("");

      /*
      var type = $('#toolTypeInput option:selected').text();
      var abstract_desc = $("#abstractDescriptionInput").val();
      var description = $("#descriptionInput").val();

      var url = $("#urlContainer .urlInput").map(function() {
        return this.value;
      }).get().join(",");

      var user_input_version_id = $("#versionInput").val();
      var release_date = $("#releaseDateInput").val();
      var support = $('#support .radio input[name=supportRadios]:checked').val();

      var category = $("#category .checkbox input[name=categoryCheckboxes]:checked").map(function() {
        return this.value;
      }).get().join(",");

      var interface_type = $("#interfaceType .checkbox input[name=interfaceTypeCheckboxes]:checked").map(function() {
        return this.value;
      }).get().join(",");

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
      */
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

function formatFileName(fileName, version){
  fileName = fileName.toLowerCase();
  fileName = fileName.replace(/ /g,"_");
  fileName = fileName + "_" + version + ".xml";

  return fileName;
}

function formatLogicalIdentifier(name){
  name = name.toLowerCase();
  name = name.replace(/ /g,"_");

  return name;
}

function formatDateString(uploadDate){
  var year = uploadDate.getFullYear();
  year = padDigit(year);
  var month = uploadDate.getMonth() + 1;
  month = padDigit(month);
  var day = uploadDate.getDate();
  day = padDigit(day);
  var hours = uploadDate.getHours();
  hours = padDigit(hours);
  var minutes = uploadDate.getMinutes();
  minutes = padDigit(minutes);
  var seconds = uploadDate.getSeconds();
  seconds = padDigit(seconds);

  return year + "" + month + "" + day + "-" + hours + "" + minutes + "" + seconds;
}

function formatCurrentDateString(uploadDate){
  var year = uploadDate.getFullYear();
  year = padDigit(year);
  var month = uploadDate.getMonth() + 1;
  month = padDigit(month);
  var day = uploadDate.getDate();
  day = padDigit(day);

  return year + "-" + month + "-" + day
}


function padDigit(digit){
  if(digit.toString().length < 1){
    return "00";
  }
  if(digit.toString().length < 2){
    return "0" + digit;
  }

  return digit;
}

function isInputEmpty(string){
  if(string.trim().length < 1){
    return true;
  }

  return false;
}

function isMultivalInputEmpty(string){
  if(string.trim().split(",").length < 1){
    return true;
  }
  if(string.trim().split(",").length === 1){
    if(string.trim().split(",")[0] === ""){
      return true;
    }
  }
  return false;
}

function convertWFileType(string){
  if (string === "wadl"){
    return "WADL";
  }
  if (string === "wsdl"){
    return "WSDL 2.n";
  }
  return string;
}
