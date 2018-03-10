(function ($) {

AjaxSolr.SearchWidget = AjaxSolr.AbstractTextWidget.extend({
  lastInputKeyword : "",
  lastType: "attribute",
  lastSort: "ascAlpha",
  lastPdsVersion: "both",
  lastSearchToggle: "search",

  init: function () {
    var self = this;

    var oldVal = this.lastInputKeyword;
    $(this.target).find('#ddSearchInput').on("propertychange change click keyup input paste", function(event){
      var keyword = $(this).val();
      if(keyword !== oldVal){
        oldVal = keyword;
        self.processSearch(keyword, self.lastType, self.lastSort, self.lastPdsVersion, self.lastSearchToggle);
      }
    });

    $(this.target).find( '#ddToggleSearchingMethodSearch' ).on( "click", function() {
        $(self.target).find( '#ddSearchToggleButton' ).html("Search <span class='caret'></span>");

        //Enable all ordering radio buttons
        $(self.target).find( '#sortOrderRadio2Div' ).attr("class", "radio");
        $(self.target).find( '#sortOrderRadio2' ).attr('disabled', false);

        if (!self.isSameAsLastSearch(self.lastInputKeyword, self.lastType, self.lastSort, self.lastPdsVersion, "search")) {
          self.processSearch(self.lastInputKeyword, self.lastType, self.lastSort, self.lastPdsVersion, "search");
        }
    });

    $(this.target).find( '#ddToggleSearchingMethodMatch' ).on( "click", function() {
        $(self.target).find( '#ddSearchToggleButton' ).html("Match <span class='caret'></span>");

        //Disable all ordering radio buttons
        $(self.target).find( '#sortOrderRadio2Div' ).attr("class", "radio disabled");
        $(self.target).find( '#sortOrderRadio2' ).attr('disabled', 'disabled');

        //select alphabetical sort
        $(self.target).find( '#sortOrderRadio1' ).prop('checked', true);
        $(self.target).find( '#sortOrderRadio2' ).attr('checked', false);

        if (!self.isSameAsLastSearch(self.lastInputKeyword, self.lastType, "ascAlpha", self.lastPdsVersion, "match")) {
          self.processSearch(self.lastInputKeyword, self.lastType, "ascAlpha", self.lastPdsVersion, "match");
        }
    });
    
    $("input[name=objectTypeRadios]:radio").change(function () {
      var type = self.getObjectType();
      if (!self.isSameAsLastSearch(self.lastInputKeyword, type, self.lastSort, self.lastPdsVersion, self.lastSearchToggle)) {
        self.processSearch(self.lastInputKeyword, type, self.lastSort, self.lastPdsVersion, self.lastSearchToggle);
      }
    });

    $("input[name=sortOrderRadios]:radio").change(function () {
      var sort = self.getSort();
      if (!self.isSameAsLastSearch(self.lastInputKeyword, self.lastType, sort, self.lastPdsVersion, self.lastSearchToggle)) {
        self.processSearch(self.lastInputKeyword, self.lastType, sort, self.lastPdsVersion, self.lastSearchToggle);
      }
    });

    $("input[name=pdsVersionRadios]:radio").change(function () {
      var pdsVersion = self.getPdsVersion();
      if (!self.isSameAsLastSearch(self.lastInputKeyword, self.lastType, self.lastSort, pdsVersion, self.lastSearchToggle)) {
        self.processSearch(self.lastInputKeyword, self.lastType, self.lastSort, pdsVersion, self.lastSearchToggle);
      }
    });
  },

  setLastSearchArguments: function(keyword, type, sort, pdsVersion, searchToggle){
    this.lastInputKeyword = keyword;
    this.lastType = type;
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

  getObjectType: function(){
    var selectedVal = "";
    var selected = $("input[type='radio'][name='objectTypeRadios']:checked");
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

  isSameAsLastSearch: function(value, type, sort, pdsVersion, lastSearchToggle){
    if (this.lastInputKeyword === value &&
        this.lastType === type &&
        this.lastSort === sort &&
        this.lastPdsVersion === pdsVersion &&
        this.lastSearchToggle === lastSearchToggle){
      return true;
    }
    else
      return false;
  },

  processSearch: function(keyword, type, sort, pdsVersion, searchToggle){
    this.setLastSearchArguments(keyword, type, sort, pdsVersion, searchToggle);
    keyword = this.replaceSpecialCharacters(keyword);

    var objectName = "";
    var objectType = "";
    var qString = "";
    var fqString = "";

    if(type === "attribute"){
      self.manager.store.addByValue('product-class', 'Product_Attribute_Definition');

      //if keyword search:
      qString = "attribute_name:" + "*" + keyword + "*";
      fqString = "product-class:product_attribute_definition AND (attribute_name:*" + keyword + "*)";
      if (searchToggle === "match"){
        qString = "attribute_name:" + keyword + "*";
        fqString = "product-class:product_attribute_definition AND (attribute_name:" + keyword + "*)";
      }

      objectName = "attribute_name";
      objectType = "attribute_type";
    }
    else if(type === "class"){
      self.manager.store.addByValue('product-class', 'Product_Class_Definition');

      //if keyword search:
      qString = "class_name:" + "*" + keyword + "*";
      fqString = "product-class:product_class_definition AND (class_name:*" + keyword + "*)";
      if (searchToggle === "match"){
        qString = "class_name:" + keyword + "*";
        fqString = "product-class:product_class_definition AND (class_name:" + keyword + "*)";
      }

      objectName = "class_name";
      objectType = "class_type";
    }

    if(sort === 'ascAlpha'){
      self.manager.store.addByValue('sort', objectName + ' asc');
    }
    else if(sort === 'descAlpha'){
      self.manager.store.addByValue('sort', objectName + ' desc');
    }

    if(pdsVersion === "both"){
    }
    else if(pdsVersion === "pds3"){
      qString += " " + objectType + ":PDS3";
      fqString += " AND " + objectType + ":PDS3";
    }
    else if(pdsVersion === "pds4"){
      qString += " " + objectType + ":PDS4";
      fqString += " AND " + objectType + ":PDS4";
    }

    if(qString.length > 0){
      self.manager.store.addByValue('q', qString);
    }

    if(fqString.length > 0){
        console.log("fq", fqString);

        var lastFq = self.manager.store.get('fq');
        for(var i = 0; i < lastFq.length; i++){
            self.manager.store.removeByValue(lastFq[i].name, lastFq[i].value);
        }

        self.manager.store.addByValue('fq', fqString);
    }

    //Reset pager widget to start on page 1
    self.manager.store.get('start').val(0);
    
    self.doRequest();
  }

});

})(jQuery);
