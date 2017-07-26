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
    $(this.target).find('#dsSearchInput').on("propertychange change click keyup input paste", function(event){
      var keyword = $(this).val();
      if(keyword !== oldVal){
        oldVal = keyword;

        if(keyword.length > 0){
          self.hideSidebar();
        }
        else{
          self.showSidebar();
        }

        self.processSearch(keyword);
      }
    });

    $( document ).on('click', '.facetDiv', function(){
       var facet = $(this).attr("data-facet");
       var field = $(this).attr("data-field");

       self.manager.store.addByValue('fq', field + ':"' + facet + '"');
       self.processSearch(self.lastInputKeyword);
    });

    $( document ).on('click', '.resetSearchButton', function(){
       if(self.manager.store.params.fq){
         self.removeAllFq(self.manager.store);
         //self.processSearch(self.lastInputKeyword);
       }

       $('#dsSearchInput').val("");
       self.processSearch("");
       self.showSidebar();

    });

    $( document ).on('click', '.facetUndoLink', function(){
       var value = $(this).attr("data-value");
       console.log("REMOVE VALUE", value);

       self.manager.store.removeByValue("fq", value);
       self.processSearch(self.lastInputKeyword);

       //self.manager.store.addByValue('fq', field + ':"' + facet + '"');
       //self.processSearch(self.lastInputKeyword);
    });

  },

  processSearch: function(keyword){
      console.log("Search keyword:", keyword);

      var qString = "";
      qString = "*" + keyword + "*";

      if(qString.length > 0){
          console.log("qString", qString);

          self.manager.store.addByValue('q', qString);
      }

      //Reset pager widget to start on page 1
      self.manager.store.get('start').val(0);
      console.log("self", self);

      //Manager.doRequest(false, "");
      self.manager.doRequest(false, "");
  },

  removeAllFq: function(store){
    var fqs = store.params.fq;

    while (fqs.length > 0) {
      var name = fqs[0].name;
      var value = fqs[0].value;

      store.removeByValue(name, value);
    }
  },

  hideSidebar: function(){
      $("#sidebarContainer").addClass("hidden");
      $("#facetsContainer").removeClass("hidden");

      $("#searchInstructions").addClass("hidden");
			$("#dataSearchIntro").addClass("hidden");

      $("#dsResult").removeClass("hidden");
  },

  showSidebar: function(){
    $("#sidebarContainer").removeClass("hidden");
    $("#facetsContainer").addClass("hidden");

    $("#searchInstructions").removeClass("hidden");
    $("#dataSearchIntro").removeClass("hidden");

    $("#dsResult").addClass("hidden");
  }
});

})(jQuery);
