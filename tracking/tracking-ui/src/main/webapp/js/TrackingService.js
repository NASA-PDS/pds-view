var Manager;
var investigationFilters = [];
var instrumentFilters = [];
var lastInstrument = "all";
var lastInvestigation = "all";

var productsUrl = "https://pds-gamma.jpl.nasa.gov/services/tracking/json/products";
var deliveryUrl = "https://pds-gamma.jpl.nasa.gov/services/tracking/json/delivery";

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

        $( "#trackingServiceTable" ).on("click", ".listTitle", function() {
            var logicalIdentifier = $(this).data("id");
            var dataVersion = $(this).data("version");
            $.ajax({
                type: "GET",
                url: deliveryUrl + "/" + logicalIdentifier + "/" + dataVersion,
                datatype: "json",
                success: function(data) {
                  displayDeliveryList(data);
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

function showTrackingTable(){
  $("#trackingServiceTable").removeClass("hidden");
  $("#deliveryTable").addClass("hidden");
  $("#showTrackingServiceButton").addClass("hidden");
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
                      '<td>' + json[i].name + '</td>' +
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

function showDeliveryTable(){
  $("#deliveryTable").removeClass("hidden");
  $("#showTrackingServiceButton").removeClass("hidden");
  $("#trackingServiceTable").addClass("hidden");
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
