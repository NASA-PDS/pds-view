(function ($) {
	AjaxSolr.ResultWidget = AjaxSolr.AbstractWidget.extend({
		self : null,
		lastRequestNumber:0,

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
			console.log("response this.manager", this.manager);
			var requestNumber = this.manager.response.responseHeader.params["_"];

			//Compare the request numbers. The highest is the latest search result.
			if(parseInt(requestNumber, 10) > this.lastRequestNumber){
				this.lastRequestNumber = requestNumber;

				console.log("response:", this.manager.response.response.docs);
				$(this.target).empty();
				this.setResultHeaders($(this.target));
				for (var i = 0, l = this.manager.response.response.docs.length; i < l; i++){
					var doc = this.manager.response.response.docs[i];
					$(this.target).append(this.template(doc, i));
				}
				$(this.target).fadeIn();
			}
		},

		template: function (doc, docId){
			var snippet = '';
			var type = self.manager.store.get('product-class').value;
			var output = "";

			if(type === "Product_Service"){
				if(doc.description !== null){

				if (doc.description.length > 300) {
						snippet += doc.description.substring(0, 300);
						snippet += '<span style="display:none;">' + doc.description.substring(300);
						snippet += '</span> <a href="#" class="more">More</a>';
					}
					else {
						snippet += doc.description;
					}
				}

				var linkText = doc.title;
				/*
				if(doc.attribute_type === "PDS4"){
					linkText = doc.attribute_name + "&nbsp;(" + doc.attribute_class_name + ")";
				}*/

				output = "<tr>";
				output += "<td><a id='detailLink" + docId +"' href='#' class='detailLink' detailLinkId='" + docId + "' detailLinkType='attribute'>" + linkText + "</a><br/><span class='ToolRegistryTableDescription'>" + snippet + "</span></td>";
				//<tr><td>Chandrayaan-1 PDS Viewer<br/><span class="ToolRegistryTableDescription">A stanalone tool for Visulaisation of Chandrayaan-1 TMC & HYSI PDS data sets.</span></td><td>1.0.0</td></tr>
				//output += "<td>" + snippet + "</td>";
				output += "<td>" + doc.version_id + "</td>";
				output += "</tr>";
				return output;
			}
			/*
			else if(type === "Product_Class_Definition"){
				if(doc.class_definition !== null){
				if (doc.class_definition.length > 300) {
						snippet += doc.class_definition.substring(0, 300);
						snippet += '<span style="display:none;">' + doc.class_definition.substring(300);
						snippet += '</span> <a href="#" class="more">More</a>';
					}
					else {
						snippet += doc.class_definition;
					}
				}
				output = "<tr>";
				output += "<td><a id='detailLink" + docId + "' href='#' class='detailLink' detailLinkId='" + docId + "' detailLinkType='class'>" + doc.class_name + "</a></td>";
				output += "<td>" + snippet + "</td>";
				output += "</tr>";
				return output;
			}*/
		},

		setResultHeaders: function(table){
			table.append("<tr><th>Name</th><th>Version&nbsp;ID</th></tr>");
			/*
			var type = self.manager.store.get('product-class').value;
			if(type === "Product_Attribute_Definition"){
				table.append("<tr><th>Attribute Name</th><th>Description</th></tr>");
			}
			else if(type === "Product_Class_Definition"){
				table.append("<tr><th>Class Name</th><th>Description</th></tr>");
			}
			*/
		}
	});
})(jQuery);
