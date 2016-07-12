(function ($) {

AjaxSolr.SearchWidget = AjaxSolr.AbstractTextWidget.extend({
  lastInputKeyword : "",
  lastInterfaceType: "All",
  lastCategory: "All",
  lastSort: "ascAlpha",
  lastPdsVersion: "both",
  lastSearchToggle: "search",

  init: function () {
    var self = this;

    var oldVal = this.lastInputKeyword;
    $(this.target).find('#trSearchInput').on("propertychange change click keyup input paste", function(event){
      var keyword = $(this).val();
      if(keyword !== oldVal){
        oldVal = keyword;
        self.processSearch(keyword, self.lastInterfaceType, self.lastCategory, self.lastSort, self.lastPdsVersion, self.lastSearchToggle);
      }
    });

    $(this.target).find( '#trToggleSearchingMethodSearch' ).on( "click", function() {
        $(self.target).find( '#trSearchToggleButton' ).html("Search <span class='caret'></span>");

        //Enable all ordering radio buttons
        $(self.target).find( '#sortOrderRadio2Div' ).attr("class", "radio");
        $(self.target).find( '#sortOrderRadio2' ).attr('disabled', false);

        if (!self.isSameAsLastSearch(self.lastInputKeyword, self.lastInterfaceType, self.lastCategory, self.lastSort, self.lastPdsVersion, "search")) {
          self.processSearch(self.lastInputKeyword, self.lastInterfaceType, self.lastCategory, self.lastSort, self.lastPdsVersion, "search");
        }
    });

    $(this.target).find( '#trToggleSearchingMethodMatch' ).on( "click", function() {
        $(self.target).find( '#trSearchToggleButton' ).html("Match <span class='caret'></span>");

        //Disable all ordering radio buttons
        $(self.target).find( '#sortOrderRadio2Div' ).attr("class", "radio disabled");
        $(self.target).find( '#sortOrderRadio2' ).attr('disabled', 'disabled');

        //select alphabetical sort
        $(self.target).find( '#sortOrderRadio1' ).prop('checked', true);
        $(self.target).find( '#sortOrderRadio2' ).attr('checked', false);

        if (!self.isSameAsLastSearch(self.lastInputKeyword, self.lastInterfaceType, self.lastCategory, "ascAlpha", self.lastPdsVersion, "match")) {
          self.processSearch(self.lastInputKeyword, self.lastInterfaceType, self.lastCategory, "ascAlpha", self.lastPdsVersion, "match");
        }
    });

    $("input[name=interfaceTypeRadios]:radio").change(function () {
      var interfaceType = self.getInterfaceType();

      if (!self.isSameAsLastSearch(self.lastInputKeyword, interfaceType, self.lastCategory, self.lastSort, self.lastPdsVersion, self.lastSearchToggle)) {
        self.processSearch(self.lastInputKeyword, interfaceType, self.lastCategory, self.lastSort, self.lastPdsVersion, self.lastSearchToggle);
      }
    });

    $("input[name=categoryRadios]:radio").change(function () {
      var category = self.getCategory();

      if (!self.isSameAsLastSearch(self.lastInputKeyword, self.lastInterfaceType, category, self.lastSort, self.lastPdsVersion, self.lastSearchToggle)) {
        self.processSearch(self.lastInputKeyword, self.lastInterfaceType, category, self.lastSort, self.lastPdsVersion, self.lastSearchToggle);
      }
    });

    $("input[name=sortOrderRadios]:radio").change(function () {
      var sort = self.getSort();

      if (!self.isSameAsLastSearch(self.lastInputKeyword, self.lastInterfaceType, self.lastCategory, sort, self.lastPdsVersion, self.lastSearchToggle)) {
        self.processSearch(self.lastInputKeyword, self.lastInterfaceType, self.lastCategory, sort, self.lastPdsVersion, self.lastSearchToggle);
      }
    });

    $("input[name=pdsVersionRadios]:radio").change(function () {
      var pdsVersion = self.getPdsVersion();

      if (!self.isSameAsLastSearch(self.lastInputKeyword, self.lastInterfaceType, self.lastCategory, self.lastSort, pdsVersion, self.lastSearchToggle)) {
        self.processSearch(self.lastInputKeyword, self.lastInterfaceType, self.lastCategory, self.lastSort, pdsVersion, self.lastSearchToggle);
      }
    });
  },

  setLastSearchArguments: function(keyword, interfaceType, category, sort, pdsVersion, searchToggle){
    this.lastInputKeyword = keyword;
    this.lastInterfaceType = interfaceType;
    this.lastCategory = category;
    this.lastSort = sort;
    this.lastPdsVersion  = pdsVersion;
    this.lastSearchToggle = searchToggle;
  },

  replaceSpecialCharacters: function(str) {
    return (str+'').replace(/[:.?*+^$[\]\\(){}|-]/g, "\\$&");
  },

  getSort: function(){
    var selectedVal = "";
    var selected = $("input[type='radio'][name='sortOrderRadios']:checked");
    if (selected.length > 0) {
      selectedVal = selected.val();
    }
    return selectedVal;
  },

  getInterfaceType: function(){
    var selectedVal = "";
    var selected = $("input[type='radio'][name='interfaceTypeRadios']:checked");
    if (selected.length > 0) {
      selectedVal = selected.val();
    }
    return selectedVal;
  },

  getCategory: function(){
    var selectedVal = "";
    var selected = $("input[type='radio'][name='categoryRadios']:checked");
    if (selected.length > 0) {
      selectedVal = selected.val();
    }
    return selectedVal;
  },

  getPdsVersion: function(){
    var selectedVal = "";
    var selected = $("input[type='radio'][name='pdsVersionRadios']:checked");
    if (selected.length > 0) {
      selectedVal = selected.val();
    }
    return selectedVal;
  },

  isSameAsLastSearch: function(value, interfaceType, category, sort, pdsVersion, lastSearchToggle){
    if (this.lastInputKeyword === value &&
        this.lastInterfaceType === interfaceType &&
        this.lastCategory === category &&
        this.lastSort === sort &&
        this.lastPdsVersion === pdsVersion &&
        this.lastSearchToggle === lastSearchToggle){
      return true;
    }
    else
      return false;
  },

  processSearch: function(keyword, interfaceType, category, sort, pdsVersion, searchToggle){
    this.setLastSearchArguments(keyword, interfaceType, category, sort, pdsVersion, searchToggle);
    keyword = this.replaceSpecialCharacters(keyword);
    console.log("keyword", keyword);
    console.log("interfaceType", interfaceType);
    console.log("category", category);

    var objectName = "";
    var qString = "";

    self.manager.store.addByValue('product-class', 'Product_Service');

    qString = "title:" + "*" + keyword + "*";
    if (searchToggle === "match"){
      qString = "title:" + keyword + "*";
    }

    if(sort === 'ascAlpha'){
      self.manager.store.addByValue('sort', 'service_name asc');
    }
    else if(sort === 'descAlpha'){
      self.manager.store.addByValue('sort', 'service_name desc');
    }

    if(pdsVersion === "both"){
    }
    else if(pdsVersion === "pds3"){
      qString += " " + "pds_model_version:PDS3";
    }
    else if(pdsVersion === "pds4"){
      qString += " " + "pds_model_version:PDS4";
    }

    if(interfaceType !== "All"){
      qString += " " + "service_interface_type:" + interfaceType;
    }


    if(category !== "All"){
      qString += " " + "service_category:" + category;
    }



    if(qString.length > 0){
      console.log("qString", qString);
      self.manager.store.addByValue('q', qString);
    }

    //Reset pager widget to start on page 1
    self.manager.store.get('start').val(0);
    console.log("self", self);

    self.doRequest(false, "search");
  }


});

})(jQuery);
