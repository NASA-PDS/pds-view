(function ($) {
	AjaxSolr.ResultWidget = AjaxSolr.AbstractWidget.extend({
		self : null,
		lastRequestNumber:0,
		docs: null,

		init: function () {
			self = this;
			$(document).on('click', 'a.more', function () {
				var $this = $(this),
				span = $this.parent().find('span');

				if (span.is(':visible')) {
					span.hide();
					$this.text('More');
				}
				else {
					span.show();
					$this.text('Less');
				}
				return false;
			});
		},

		beforeRequest: function(){
			$(this.target).html($('<img>').attr('src', 'images/ajax-loader.gif'));
		},

		afterRequest: function(){
			var requestNumber = this.manager.response.responseHeader.params["_"];

			//Compare the request numbers. The highest is the latest search result.
			if(parseInt(requestNumber, 10) > this.lastRequestNumber){
				this.lastRequestNumber = requestNumber;
				this.docs = this.manager.response.response.docs;

				console.log("response:", this.manager.response.response.docs);
				$(this.target).empty();
				this.setResultHeaders($(this.target));
				for (var i = 0, l = this.manager.response.response.docs.length; i < l; i++){
					var doc = this.manager.response.response.docs[i];
					$(this.target).append(this.template(doc, i));
				}
				$(this.target).fadeIn();
			}
			else{
				this.manager.response.response.docs = this.docs;
			}
		},

		template: function (doc, docId){
			var snippet = '';
			var output = "";

			output = "<tr>";
			output += "<td><a id='detailLink" + docId +"' href='#' class='detailLink' detailLinkId='" + docId + "' detailLinkType='attribute'>" + doc.person_sort_name + "</a></td>";
			output += "<td>" + doc.person_telephone_number + "</td>";
			output += "<td>" + doc.person_institution_name + "</td>";
			output += "</tr>";
			return output;
		},

		setResultHeaders: function(table){
				table.append("<tr><th>Name</th><th>Telephone Number</th><th>Institution</th></tr>");

		}
	});
})(jQuery);
