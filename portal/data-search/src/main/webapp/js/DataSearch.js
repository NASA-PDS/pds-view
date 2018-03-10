var getTargetsUrl = "getTargets.php";
var getTargetUrl = "getTarget.php";
var getInvestigationsUrl = "getInvestigations.php";
var getInvestigationUrl = "getInvestigation.php";
var targetList = [];
var investigationList = [];
var lastTarget = "";
var lastInvestigation = "";

var queryToolsTitlePanel = $(
    '<h4 class="resultsTitlePanelTitle">Search Tools</h4>' +
    '<hr class="resultsTitleHr">'
  );

var additionalToolsTitlePanel = $(
    '<h4 class="resultsTitlePanelTitle">Resources (Archive Pages, Online Repositories, etc.)</h4>' +
    '<hr class="resultsTitleHr">'
  );

(function ($) {
    $( document ).ready(function() {

        $.ajax({
            type: "GET",
            url: getTargetsUrl,
            datatype: "json",
            success: function(data) {
              data = JSON.parse(data);
              setUpTargetDropDownList(data);
            }
        });

        $.ajax({
            type: "GET",
            url: getInvestigationsUrl,
            datatype: "json",
            success: function(data) {
              data = JSON.parse(data);
              setUpInvestigationsDropDownList(data);
            }
        });

    });
})(jQuery);

function setUpTargetDropDownList(data){
  createTargetList(data);
  createTargetDropDownList();
  createTargetDropDownListListener();
}

function createTargetList(data){
  for(var i = 0; i < data.length; i++){
    if(!targetExistsInTargetList(data[i].target)){
      addTargetToTargetList(data[i].target);
    }
  }
}

function targetExistsInTargetList(target){
  for(var i = 0; i < targetList.length; i++){
    if(targetList[i] === target){
      return true;
    }
  }

  return false;
}

function addTargetToTargetList(target){
    targetList.push(target);
}

function createTargetDropDownList(){
  $('#targetSelect').find('option').remove();

  $('#targetSelect').append('<option style="display:none" value="" disabled selected></option>');
  for(var i = 0; i < targetList.length; i++){
      $('#targetSelect').append('<option value="' + targetList[i] + '">' + targetList[i] + '</option>');
  }

  //$('#targetSelect').val('');
  lastTarget = "";
}

function createTargetDropDownListListener(){
  $('#targetSelect').on('change', function() {
    resetInvestigationDropdown();
    filterTargets(this.value);
  });
}

function resetInvestigationDropdown(){
    document.getElementById('investigationSelect').value = '';
}

function filterTargets(target){

  $.ajax({
      type: "POST",
      url: getTargetUrl,
      data: {
        "target":target,
        "type": "query"
      },
      datatype: "json",
      success: function(data) {
        data = JSON.parse(data);
        console.log("targetQUery", data);
        displayTargetQueryTools(data);
      }
  });

  $.ajax({
      type: "POST",
      url: getTargetUrl,
      data: {
        "target":target,
        "type": "additional"
      },
      datatype: "json",
      success: function(data) {
        data = JSON.parse(data);
        console.log("targetAddional", data);
        displayTargetAdditionalTools(data);
      }
  });

}

function displayTargetQueryTools(json){
  $("#searchResultsQueryTools").empty();

  if (json){
    if(json.length > 0){
      $('#searchResultsQueryTools').append(queryToolsTitlePanel);

      for(var i = 0; i < json.length; i++){
        var titleLink = $('<a href="'  + json[i].url + '" target="_blank"><h4 class="resultTitle">' + json[i].title + '</h4></a>');
        var description = $('<p class="searchResultDescription">' + json[i].description + '</p>');

        $('#searchResultsQueryTools').append(titleLink);
        $('#searchResultsQueryTools').append(description);
      }
    }
  }
}

function displayTargetAdditionalTools(json){
  $("#searchResultsAdditionalTools").empty();

  if (json){
    if(json.length > 0){
      $('#searchResultsAdditionalTools').append(additionalToolsTitlePanel);

      for(var i = 0; i < json.length; i++){
        var titleLink = $('<a href="'  + json[i].url + '" target="_blank"><h4 class="resultTitle">' + json[i].title + '</h4></a>');
        var description = $('<p class="searchResultDescription">' + json[i].description + '</p>');
        var br = $('<br>');

        $('#searchResultsAdditionalTools').append(titleLink);
        $('#searchResultsAdditionalTools').append(description);
      }
    }
  }
}

function setUpInvestigationsDropDownList(data){
  createInvestigationList(data);
  createInvestigationDropDownList();
  createInvestigationDropDownListener();
}

function createInvestigationList(data){
  for(var i = 0; i < data.length; i++){
    if(!investigationExistsInInvestigationList(data[i].investigation)){
      addInvestigationToInvestigationList(data[i].investigation);
    }
  }
}

function investigationExistsInInvestigationList(investigation){
  for(var i = 0; i < investigationList.length; i++){
    if(investigationList[i] === investigation){
      return true;
    }
  }

  return false;
}

function addInvestigationToInvestigationList(investigation){
    investigationList.push(investigation);
}

function createInvestigationDropDownList(){
  $('#investigationSelect').find('option').remove();

  $('#investigationSelect').append('<option style="display:none" value="" disabled selected></option>');
  for(var i = 0; i < investigationList.length; i++){
      $('#investigationSelect').append('<option value="' + investigationList[i] + '">' + investigationList[i] + '</option>');
  }

  //$('#investigationSelect').val('');
  lastInvestigation = "";
}

function createInvestigationDropDownListener(){
  $('#investigationSelect').on('change', function() {
    resetTargetDropdown();
    filterInvestigations(this.value, null);
  });
}

function resetTargetDropdown(){
    document.getElementById('targetSelect').value = '';
}

function filterInvestigations(investigation){

  $.ajax({
      type: "POST",
      url: getInvestigationUrl,
      data: {
        "investigation":investigation,
        "type": "query"
      },
      datatype: "json",
      success: function(data) {
        data = JSON.parse(data);
        console.log("InvestigaionQuery", data);
        displayInvestigationQueryTools(data);
      }
  });

  $.ajax({
      type: "POST",
      url: getInvestigationUrl,
      data: {
        "investigation":investigation,
        "type": "additional"
      },
      datatype: "json",
      success: function(data) {
        data = JSON.parse(data);
        console.log("InvestigationAddtional", data);
        displayInvestigationAdditionalTools(data);
      }
  });

}

function displayInvestigationQueryTools(json){
  $("#searchResultsQueryTools").empty();

  if (json){
    if(json.length > 0){
      $('#searchResultsQueryTools').append(queryToolsTitlePanel);

      for(var i = 0; i < json.length; i++){
        var titleLink = $('<a href="'  + json[i].url + '" target="_blank"><h4 class="resultTitle">' + json[i].title + '</h4></a>');
        var description = $('<p class="searchResultDescription">' + json[i].description + '</p>');

        $('#searchResultsQueryTools').append(titleLink);
        $('#searchResultsQueryTools').append(description);
      }
    }
  }
}

function displayInvestigationAdditionalTools(json){
  $("#searchResultsAdditionalTools").empty();

  if (json){
    if(json.length > 0){
      $('#searchResultsAdditionalTools').append(additionalToolsTitlePanel);

      for(var i = 0; i < json.length; i++){
        var titleLink = $('<a href="'  + json[i].url + '" target="_blank"><h4 class="resultTitle">' + json[i].title + '</h4></a>');
        var description = $('<p class="searchResultDescription">' + json[i].description + '</p>');

        $('#searchResultsAdditionalTools').append(titleLink);
        $('#searchResultsAdditionalTools').append(description);
      }
    }
  }
}
