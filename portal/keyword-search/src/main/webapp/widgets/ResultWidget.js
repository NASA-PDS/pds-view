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

				this.updateFacets(this.manager.response);
			}
		},

		template: function (doc, docId){
			var snippet = '';
			var output = "";


				if(doc.description){
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
				var objectType = doc.objectType;

				output = '<div class="resultListItem">';
				output += '<strong>' + objectType + '</strong>:<a id="detailLink' + docId + '" href="#" class="detailLink resultItemTitle" detailLinkId="' + docId + '" detailLinkType="attribute">' + linkText + '</a>';
            	output += '<p class="resultItemDescription">' + snippet + '</p>';
				output += '</div>';
				return output;
		},

		setResultHeaders: function(domObject){
			//table.append("<tr><th>Name</th><th>PDS&nbsp;Version</th></tr>");
		},

		updateFacets: function(response){
			var $facetString = $('<div></div>').addClass("facetContainer");


			console.log("RESPONSE", response);
			var facetFields = response.facet_counts.facet_fields;
			console.log("FACET FIELDS", facetFields);

			for (var key in facetFields){
				if (facetFields.hasOwnProperty(key)) {
					var fieldTitle = key.replace("facet_", "");
					fieldTitle = this.replaceAll(fieldTitle, "_", " ");
					fieldTitle = fieldTitle;

					var field = key;

					if(!this.IsFieldInFqParams(response, field)){
						if(facetFields[field].length > 0){
							$facetString.append($('<div>' + fieldTitle + '</div>').addClass("fieldTitle"));
							for(var i = 0; i < facetFields[field].length; i=i+2){
								var facet = facetFields[field][i];
								var facetTitle = facetFields[field][i].split(",")[1];
								facetTitle = this.replaceAll(facetTitle, "_", " ");
								var facetCount = facetFields[field][i + 1];

								var $facetDiv = $('<div data-facet="' + facetFields[field][i] + '" data-field="' + key + '"></div>').addClass("facetDiv");
								$facetDiv.append(facetTitle);
								$facetDiv.append( " (" + facetCount + ')');

								$facetString.append($facetDiv);
							}
						}
					}
				}

			}

			$("#facetsContainer").empty();
			if(response.responseHeader.params.fq){
				var $refinementsContainer = $('<div class="refinementsContainer"></div>');
				var $refinementsListDiv = $('<div class="refinementsListDiv"></div>');

				var fqObj = response.responseHeader.params.fq;
				if( typeof fqObj === 'string' ) {
					var $refinementDiv = this.createRefinementDiv(fqObj);

					$refinementsListDiv.append($refinementDiv);
					$refinementsContainer.append($refinementsListDiv);
				}
				else{
					for(var i = 0; i < fqObj.length; i++){
						var $refinementDiv = this.createRefinementDiv(fqObj[i]);

						$refinementsListDiv.append($refinementDiv);
						$refinementsContainer.append($refinementsListDiv);
					}
				}

				$("#facetsContainer").append($('<div class="refinementsTitleDiv">Refinements</div>'));
				$("#facetsContainer").append($refinementsContainer);
			}

			$("#facetsContainer").append($('<div class="facetContainerTitle">Refine By:</div>'));
			$("#facetsContainer").append($facetString);
		},

		replaceAll: function(fullString, replace, replaceWith){
			var find = replace;
			var re = new RegExp(find, 'g');
			return fullString.replace(re, replaceWith);
		},

		escapeQuotes: function(fullString){
			return fullString.replace(/"/g, "&quot;");
		},

		toTitleCase: function(str){
		    return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
		},

		IsFieldInFqParams: function(response, field){
			var fqs = response.responseHeader.params.fq;
			if(fqs){
				if(this.fieldInFqs(fqs, field)){
					return true;
				}
			}
			return false;
		},

		fieldInFqs: function(fqs, field){
			if( typeof fqs === 'string' ) {
				return this.doesFieldMatchFq(fqs, field);
			}
			else{
				for(var i = 0; i < fqs.length; i++){
					if(this.doesFieldMatchFq(fqs[i], field)){
						return true;
					}
				}
				return false;
			}
		},

		doesFieldMatchFq: function(fq, field){
			if(fq.split(":")[0] === field){
				return true;
			}
			else{
				return false;
			}
		},

		createRefinementDiv: function(fqObj){
			var refinementField = fqObj.split(",")[0];
			var refinementFacet = fqObj.split(",")[1];
			var refinementFq = this.escapeQuotes(fqObj);

			var refinementFieldDisplay = refinementField.replace("facet_", "");
			refinementFieldDisplay = this.replaceAll(refinementFieldDisplay, "_", " ");
			refinementFieldDisplay = this.replaceAll(refinementFieldDisplay, ':"1', "");

			var refinementFacetDisplay = this.replaceAll(refinementFacet, "1", "");
			var refinementFacetDisplay = this.replaceAll(refinementFacet, '_', " ");
			refinementFacetDisplay = refinementFacetDisplay.slice(0, -1);

			var $refinementDiv = $('<div class="refinementDiv"></div>');
			$refinementDiv.append($('<span class="refinementRemoveLink">' +
			' ' + refinementFieldDisplay + ':' +
			' ' + refinementFacetDisplay + " " +
			'<span class="facetUndoLink" data-value="' + refinementFq + '">[undo]</span></span>'));

			return $refinementDiv;
		}

	});
})(jQuery);
