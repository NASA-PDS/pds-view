var Manager;
(function ($) {

  //Initialize
  $( document ).ready(function() {
    $( "#dsSearchInput" ).focus();
    init();
  });

  $(function () {
    Manager = new AjaxSolr.Manager({
        solrUrl: 'http://pds-dev.jpl.nasa.gov:8080/search-service/pds/product-search'
    });

    Manager.addWidget(new AjaxSolr.ResultWidget({
      id: 'result',
      target: '#dsDocs'
    }));

    Manager.addWidget(new AjaxSolr.SearchWidget({
      id: 'text',
      target: '#dsSearch'
    }));

    Manager.addWidget(new AjaxSolr.DetailLinkSearchWidget({
      id: 'detailLink',
      target: '#trResult'
    }));

    Manager.addWidget(new AjaxSolr.PagerWidget({
      id: 'pager',
      target: '#dsPager',
      prevLabel: '<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>',
      nextLabel: '<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>',
      innerWindow: 1,
      renderHeader: function (perPage, offset, total) {
        $('#dsPager-header').html('<span>Displaying ' + Math.min(total, offset + 1) + ' to ' + Math.min(total, offset + perPage) + ' of <span class="badge">' + total + '</span> results.' + '</br></span>');
      }
    }));

    Manager.init();
    //Manager.store.addByValue('q','mars');
    /*
    Manager.store.addByValue('product-class', 'Product_Service');
    Manager.store.addByValue('sort', 'service_name asc');
    */
    //Manager.doRequest(false, "search");
    //Manager.doRequest(false, "");
  });

})(jQuery);

function init() {

    /*$(document).ready(function () {
        console.log("doc ready");
        $("#sidebar").addClass("hidden");
    });*/

    /*
    $(document).ready(function () {
        var url = "http://pds-dev.jpl.nasa.gov:8080/search-service/pds/product-search?q=mars&wt=json";
        console.log("sending request for tools mars", url);
        $.ajax({
            type: "GET",
            url: url,
            dataType: "json",
            cache: false,
            contentType: "multipart/form-data",
            success: function (data) {
                console.log("mars data", data);
            },
            error: function (xhr, status, error) {
                console.log("error", error);
                //alert(xhr.status);
            }
        });
    });
    */
}
