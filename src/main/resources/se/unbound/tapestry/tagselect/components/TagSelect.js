var TagSelect = {
	tagId: 0,
	
	addSelection: function(clientId, li) {
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

			tags = $(field.id + '-tags').childElements();
			if (tags.length > 0) {
				value = tag.select('span.u-tag-value')[0].innerHTML;
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
		origin = valueField.value;
		values = $A(valueField.value.split(';'));
		values.push(li.innerHTML);
		result = values.uniq().join(';');
		valueField.value = result;
		return origin != result;
	},

	removeFromValueField: function(clientId, value) {
		var valueField = $(clientId + '-values');
		values = $A(valueField.value.split(';'));
		valueField.value = values.without(value).join(';');
	},
	
	addTag: function(clientId, li) {
		var tagsField = $(clientId + '-tags');
		tagsField.insert("<li class='u-tag' id='u-tag-" + TagSelect.tagId + "'><button type='button' class='u-tag-button'><span><span class='u-tag-value'>" + li.innerHTML + "</span></span></button><em class='u-tag-remove' onclick='TagSelect.removeSelection(\"" + clientId + "\", \"u-tag-" + TagSelect.tagId++ + "\", \"" + li.innerHTML + "\")'></em></li>");
	},
	
	removeTag: function(tagId) {
		var tag = $(tagId);
		tag.remove();
	},
	
	updatePadding: function(clientId) {
		textArea = $(clientId);
		// Clear style set by script
		textArea.writeAttribute('style', '');

		tagContainerDimensions = $(clientId + '-tag-container').getDimensions();
		selectWidth = Element.getWidth($(clientId).getOffsetParent());
		paddingTop = textArea.measure('padding-top')
		paddingLeft = textArea.measure('padding-left');

		tags = $(clientId + '-tags').childElements();
		if (tags.length > 0) {
			tag = tags[tags.length - 1];
			offset = tag.positionedOffset();
			dimensions = tag.getDimensions();
			newPaddingLeft = paddingLeft + offset.left + dimensions.width;
			newWidth = selectWidth - (offset.left + dimensions.width);
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
