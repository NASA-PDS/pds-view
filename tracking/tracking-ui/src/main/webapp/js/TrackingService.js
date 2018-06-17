var Manager;
var investigationFilters = [];
var instrumentFilters = [];
var lastInstrument = "all";
var lastInvestigation = "all";

var productsUrl = "https://pds-gamma.jpl.nasa.gov/services/tracking/json/products";
var deliveryUrl = "https://pds-gamma.jpl.nasa.gov/services/tracking/json/delivery";
var submissionsUrl = "https://pds-gamma.jpl.nasa.gov/services/tracking/json/submissionstatus";

(function ($) {
    $( document ).ready(function() {
        $.ajax({
            type: "GET",
            url: productsUrl + "/null/null",
            datatype: "json",
            success: function(data) {
              displayTrackingList(data);
              setUpFilters(data);
            }
        });

        $("#showTrackingServiceButton").on("click", function(){
            showTrackingTable();
        });

        $("#showDeliverySubmissionsButton").on("click", function(){
            showDeliveryTable();
        });

        $( "#trackingServiceTable" ).on("click", ".listTitle", function() {
            var logicalIdentifier = $(this).data("id");
            var dataVersion = $(this).data("version");

            console.log("tracking table clicked", logicalIdentifier);
            $.ajax({
                type: "GET",
                url: deliveryUrl + "/" + logicalIdentifier + "/" + dataVersion,
                datatype: "json",
                success: function(data) {
                  displayDeliveryList(data);
                }
            });
        });

        $( "#deliveryTable" ).on("click", ".listTitle", function() {
          var deliveryIdentifier = $(this).data("delivery_identifier");
          console.log("deliveryTableClicked " + deliveryIdentifier, submissionsUrl + "/" + deliveryIdentifier);

          $.ajax({
              type: "GET",
              //url: submissionsUrl + "/" + /*deliveryIdentifier*/173,
              url: submissionsUrl + "/" + deliveryIdentifier,
              datatype: "json",
              success: function(data) {
                displaySubmissionsList(data);
              }
          });
        });

    });
})(jQuery);

function init(){
}

function displayTrackingList(json){
    $("#trackingServiceTable").empty();
    showTrackingTable();

    json = json.products;

    if (json){
      if(json.length > 0){

        var table = $('<table></table>').addClass('table table-striped');
        var thead = $('<thead><tr><th>Title</th><th>type</th><th>version</th></tr></thead>');

        var tbody = $('<tbody></tbody');
        for(i=0; i<json.length; i++){
            var row = $('<tr>' +
                        '<td><a class="listTitle" data-version=' + json[i].version_id + ' data-id=' + json[i].logical_identifier + '>' + json[i].title + '</a></td>' +
                        '<td>' + json[i].type + '</td>' +
                        '<td>' + json[i].version_id + '</td>' +
                        '</tr>');
            tbody.append(row);
        }

        table.append(thead);
        table.append(tbody);

        $('#trackingServiceTable').append(table);
      }
    }
}

function displayDeliveryList(data){
  $("#deliveryTable").empty();
  showDeliveryTable();

  json = data.delivery;
  if (json){
    if(json.length > 0){

      var table = $('<table></table>').addClass('table table-striped');
      var thead = $('<thead><tr><th>Name</th><th>Start Date</th><th>Stop Date</th><th>Source</th><th>Target</th><th>Due Date</th></tr></thead>');

      var tbody = $('<tbody></tbody');
      for(i=0; i<json.length; i++){
          var row = $('<tr>' +
                      '<td><a class="listTitle" data-delivery_identifier=' + json[i].delivery_identifier + '>' + json[i].name + '</a></td>' +
                      '<td>' + json[i].start_date_time + '</td>' +
                      '<td>' + json[i].stop_date_time + '</td>' +
                      '<td>' + json[i].source + '</td>' +
                      '<td>' + json[i].target + '</td>' +
                      '<td>' + json[i].due_date + '</td>' +
                      '</tr>');
          tbody.append(row);
      }

      table.append(thead);
      table.append(tbody);

      $('#deliveryTable').append(table);
    }
  }
}

function displaySubmissionsList(json){
    $("#submissionsTable").empty();
    showSubmissionsTable();
    console.log("submissionJson", json);
    json = json["Submission Status"];


    if (json){
      if(json.length > 0){

        var table = $('<table></table>').addClass('table');
        var thead = $('<thead><tr><th>Date</th><th>Status</th><th>Comment</th><th>Email</th></tr></thead>');

        var tbody = $('<tbody></tbody');

        for(i=0; i<json.length; i++){
            var rowClass = "";
            var status = json[i].status;
            if(status === "Submitted"){
                rowClass = "";
            }
            if(status === "Rejected"){
                rowClass = "danger";
            }
            if(status === "Withdrawn"){
                rowClass = "active";
            }
            if(status === "Accepted"){
                rowClass = "success";
            }

            var row = $('<tr class="' + rowClass + '">' +
                        '<td>' + json[i].status_date_time + '</td>' +
                        '<td>' + json[i].status + '</td>' +
                        '<td>' + json[i].comment + '</td>' +
                        '<td>' + json[i].electronic_mail_address + '</td>' +
                        '</tr>');
            tbody.append(row);
        }

        table.append(thead);
        table.append(tbody);

        $('#submissionsTable').append(table);
      }
      else{
        showNoSubmissionResultsText();
      }
    }
    else{
      showNoSubmissionResultsText();
    }
}

function showNoSubmissionResultsText(){
    var noSubmissionMessage = $('<p><span class="label label-warning">There are no submissions.</span><p>').addClass('noSubmissionMessage');
    $('#submissionsTable').append(noSubmissionMessage);
}

function showTrackingTable(){
  $("#trackingServiceTable").removeClass("hidden");
  $("#deliveryTable").addClass("hidden");
  $("#submissionsTable").addClass("hidden");
  $("#showTrackingServiceButton").addClass("hidden");
  $("#showDeliverySubmissionsButton").addClass("hidden");
}

function showDeliveryTable(){
  $("#deliveryTable").removeClass("hidden");
  $("#showTrackingServiceButton").removeClass("hidden");
  $("#showDeliverySubmissionsButton").addClass("hidden");
  $("#trackingServiceTable").addClass("hidden");
  $("#submissionsTable").addClass("hidden");
}

function showSubmissionsTable(){
  $("#submissionsTable").removeClass("hidden");
  $("#showDeliverySubmissionsButton").removeClass("hidden");
  $("#showTrackingServiceButton").addClass("hidden");
  $("#trackingServiceTable").addClass("hidden");
  $("#deliveryTable").addClass("hidden");
}

function setUpFilters(data){
  var products = data.products;

  for(var i = 0; i < products.length; i++){
    addToInvestigationsFilter(products[i]);
    addToInstrumentsFilter(products[i]);
  }

  createFilterSelects();
}

function addToInvestigationsFilter(product){
  var title = product.investigation[0].title;
  var reference = product.investigation[0].reference;

  var investigation = {
    "title": title,
    "reference": reference
  };

  if(!titleExistsInInvestigationFilter(title)){
    investigationFilters.push(investigation);
  }
}

function titleExistsInInvestigationFilter(title){
  for(var i = 0; i < investigationFilters.length; i++){
    if(investigationFilters[i].title === title){
      return true;
    }
  }

  return false;
}

function addToInstrumentsFilter(product){
  var title = product.instrument[0].title;
  var reference = product.instrument[0].reference;

  var instrument = {
    "title": title,
    "reference": reference
  };

  if(!titleExistsInInstrumentFilter(title)){
    instrumentFilters.push(instrument);
  }
}

function titleExistsInInstrumentFilter(title){
  for(var i = 0; i < instrumentFilters.length; i++){
    if(instrumentFilters[i].title === title){
      return true;
    }
  }

  return false;
}

function createFilterSelects(){
  createInvestigationFilterSelectList();
  createInstrumentsFilterSelectList();

  addInvestigationFilterSelectListener();
  addInstrumentFilterSelectListener();
}

function createInvestigationFilterSelectList(){
  $('#investigationSelect').find('option').remove();

  $('#investigationSelect').append('<option value="all">All</option>');
  for(var i = 0; i < investigationFilters.length; i++){
      $('#investigationSelect').append('<option value=' + investigationFilters[i].reference + '>' + investigationFilters[i].title + '</option>');
  }

  $('#investigationSelect').val('all');
  lastInvestigation = "all";
}

function createInstrumentsFilterSelectList(){
  $('#instrumentSelect').find('option').remove();

  $('#instrumentSelect').append('<option value="all">All</option>');
  for(var i = 0; i < instrumentFilters.length; i++){
      $('#instrumentSelect').append('<option value=' + instrumentFilters[i].reference + '>' + instrumentFilters[i].title + '</option>');
  }

  $('#instrumentSelect').val('all');
  lastInstrument = "all";
}

function addInvestigationFilterSelectListener(){
  $('#investigationSelect').on('change', function() {
    filterProducts(null, this.value);
  });
}

function addInstrumentFilterSelectListener(){
  $('#instrumentSelect').on('change', function() {
    filterProducts(this.value, null);
  });
}

function filterProducts(instrument, investigation){
  if(instrument === null){
    instrument = lastInstrument;
  }
  if(instrument === "all"){
    instrument = "null";
  }

  if(investigation === null){
    investigation = lastInvestigation;
  }
  if(investigation === "all"){
    investigation = "null";
  }

  lastInstrument = instrument;
  lastInvestigation = investigation;

  $.ajax({
      type: "GET",
      url: productsUrl + "/" + instrument + "/" + investigation,
      datatype: "json",
      success: function(data) {
        displayTrackingList(data);
      }
  });
}
