var TagSelect = {
	tagId: 0,
	
	addSelection: function(clientId, li) {
		this.clearIfSingleSelect(clientId);
		if (this.addToValueField(clientId, li)) {
			this.addTag(clientId, li);
			this.updatePadding(clientId);
		}
		this.clearField(clientId);
	},

	removeSelection: function(clientId, tagId, value) {
		this.removeFromValueField(clientId, value);
		this.removeTag(tagId);
		this.updatePadding(clientId);
	},

	registerKeyevent: function(clientId) {
		Event.on(clientId, 'keydown', TagSelect.handleBackspace);
	},
	
	handleBackspace: function(event, field) {
		if (!field.value && event.keyCode == 8) {
			event.stop();

			var tags = $(field.id + '-tags').childElements();
			if (tags.length > 0) {
				var value = tag.select('span.u-tag-value')[0].innerHTML;
				TagSelect.removeSelection(field.id, tags[tags.length - 1].id, value);
			}
			
			return false;
		}
	},
	
	triggerCompletion: function(field) {
		this.fireEvent(field, 'keydown');
		field.focus();
	},
	
	addToValueField: function(clientId, li) {
		var valueField = $(clientId + '-values');
		var origin = valueField.value;
		if (origin.length == 0) {
			var values = new Array();
		} else {
			var values = $A(valueField.value.split(';'));
		}
		values.push(li.readAttribute('id'));
		var result = values.uniq().join(';');
		valueField.value = result;
		return origin != result;
	},

	removeFromValueField: function(clientId, value) {
		var valueField = $(clientId + '-values');
		var values = $A(valueField.value.split(';'));
		valueField.value = values.without(value).join(';');
	},
	
	addTag: function(clientId, li) {
		var tagsField = $(clientId + '-tags');
		tagsField.insert("<li class='u-tag' id='u-tag-" + TagSelect.tagId + "'><button type='button' class='u-tag-button'><span><span class='u-tag-value'>" + li.innerHTML + "</span></span></button><em class='u-tag-remove' onclick='TagSelect.removeSelection(\"" + clientId + "\", \"u-tag-" + TagSelect.tagId++ + "\", \"" + li.readAttribute('id') + "\")'></em></li>");
	},
	
	removeTag: function(tagId) {
		var tag = $(tagId);
		tag.remove();
	},

	clearIfSingleSelect: function(clientId) {
		if ($(clientId).readAttribute('u:type') == 'single') {
			var valueField = $(clientId + '-values');
			valueField.value = '';
			var tags = $(clientId + '-tags').childElements();
			if (tags.length > 0) {
				tags.each(function(tag) {
					tag.remove();
				});
			}
		}
	},
	
	updatePadding: function(clientId) {
		var textArea = $(clientId);
		// Clear style set by script
		textArea.writeAttribute('style', '');

		var tagContainerDimensions = $(clientId + '-tag-container').getDimensions();
		var selectWidth = Element.getWidth($(clientId).getOffsetParent());
		var paddingTop = textArea.measure('padding-top')
		var paddingLeft = textArea.measure('padding-left');

		var tags = $(clientId + '-tags').childElements();
		if (tags.length > 0) {
			var tag = tags[tags.length - 1];
			var offset = tag.positionedOffset();
			var dimensions = tag.getDimensions();
			var newPaddingLeft = paddingLeft + offset.left + dimensions.width;
			var newWidth = selectWidth - (offset.left + dimensions.width);
			textArea.height = paddingTop + offset.top + dimensions.height + 'px';
			textArea.setStyle({paddingLeft: newPaddingLeft + 'px', width: newWidth + 'px', paddingTop: paddingTop + offset.top + 'px'});
		}
	},
	
	clearField: function(clientId) {
		$(clientId).setValue('');
	},
	
	fireEvent: function(element, event){
	    if (document.createEventObject){
		    // dispatch for IE
		    var evt = document.createEventObject();
		    return element.fireEvent('on'+event, evt)
	    }
	    else{
		    // dispatch for firefox + others
		    var evt = document.createEvent("HTMLEvents");
		    // event type,bubbling,cancelable
		    evt.initEvent(event, true, true );
		    return !element.dispatchEvent(evt);
	    }
	}
}
