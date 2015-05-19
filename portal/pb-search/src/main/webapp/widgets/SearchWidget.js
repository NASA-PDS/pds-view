(function ($) {

AjaxSolr.SearchWidget = AjaxSolr.AbstractTextWidget.extend({
  lastInputKeyword : "",
  lastSearchToggle: "search",
  lastNode: "-All-",
  nodeList: null,

  init: function () {
    var self = this;
    this.createNodeList();

    //Detect changes to input box
    var oldVal = this.lastInputKeyword;
    $(this.target).find('#pbSearchInput').on("propertychange change click keyup input paste", function(event){
      var keyword = $(this).val();
      if(keyword !== oldVal){
        oldVal = keyword;
        console.log("keyword", keyword);
        self.processSearch(keyword, self.lastSearchToggle, self.lastNode);
      }
    });


    //Detect Changes to search toggle selection
    $(this.target).find( '#pbToggleSearchingMethodSearch' ).on( "click", function() {
        $(self.target).find( '#pbSearchToggleButton' ).html("Search <span class='caret'></span>");
        if (!self.isSameAsLastSearch(self.lastInputKeyword, "search", self.lastNode)) {
          self.processSearch(self.lastInputKeyword, "search", self.lastNode);
        }
    });

    $(this.target).find( '#pbToggleSearchingMethodMatch' ).on( "click", function() {
        $(self.target).find( '#pbSearchToggleButton' ).html("Match <span class='caret'></span>");
        if (!self.isSameAsLastSearch(self.lastInputKeyword, "match", self.lastNode)) {
          self.processSearch(self.lastInputKeyword, "match", self.lastNode);
        }
    });

    //Detect changes to selection elements
    $('#pbNodeSelect').on('change', function() {
      var node = this.value;
      console.log("nodeSelect", node);
      self.processSearch(self.lastInputKeyword, self.lastSearchToggle, node);
    });

    $('#pbLocationSelect').on('change', function() {
      console.log("nodeSelect", this.value);
    });

  },

  escapeSpecialCharacters: function(str) {
    return (str+'').replace(/[:.?*+^$[\]\\(){}|-],/g, "\\$&");
  },

  escapeSpaceCharacters: function(str){
    return (str+'').replace(/ /g, "\\ ");
  },

  setLastSearchArguments: function(keyword, searchToggle, node){
    this.lastInputKeyword = keyword;
    this.lastSearchToggle = searchToggle;
    this.lastNode = node;
  },

  isSameAsLastSearch: function(value, lastSearchToggle, node){
    if (this.lastInputKeyword === value &&
        this.lastSearchToggle === lastSearchToggle &&
        this.lastNode === node){
      return true;
    }
    else
      return false;
  },

  processSearch: function(keyword, searchToggle, node){
    this.setLastSearchArguments(keyword, searchToggle, node);
    keyword = this.escapeSpecialCharacters(keyword);
    keyword = this.escapeSpaceCharacters(keyword);
    var qString = "";
    console.log("searchToggle", searchToggle);
    if(node === "-All-"){
      qString = "data_class:PDS_Affiliate AND ( person_sort_name:" + "*" + keyword + "* OR person_institution_name:" + "*" + keyword + "*)";
      if (searchToggle === "match"){
        console.log("match");
        qString = "data_class:PDS_Affiliate AND person_sort_name:" + keyword + "*";
      }
    }
    else{
      var nodeFullName = this.nodeList[node];
      qString = 'data_class:PDS_Affiliate AND person_team_name:"' + nodeFullName + '" AND ( person_sort_name:' + '*' + keyword + '* OR person_institution_name:' + "*" + keyword + "*)";
      if (searchToggle === "match"){
        console.log("match");
        qString = 'data_class:PDS_Affiliate AND person_team_name:"' + nodeFullName + '" AND person_sort_name:' + keyword + '*';
      }
    }



    if(qString.length > 0){
      self.manager.store.addByValue('q', qString);
      console.log("q", qString);
    }

    //Reset pager widget to start on page 1
    self.manager.store.get('start').val(0);
    //console.log("self", self);
    
    self.doRequest();
  },

  createNodeList: function(){
      this.nodeList = {
        "ATMOS" : "Planetary Atmospheres",
        "EN" : "Engineering",
        "GEOSCIENCE" : "Geosciences",
        "HQ" : "Headquarters",
        "IMAGING" : "Imaging",
        "NAIF" : "Navigation and Ancillary Information Facility",
        "NSSDC" : "National Space Science Data Center",
        "PDS-MGT" : "Management",
        "PPI-UCLA" : "Planetary Plasma Interactions",
        "RINGS" : "Planetary Rings",
        "RS" : "Radio Science",
        "SBN" : "Small Bodies"
      }
  }

});

})(jQuery);
