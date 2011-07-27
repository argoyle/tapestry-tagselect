var TagSelect = {
	tagId: 0,
	
	addSelection: function(field, li) {
		clientId = field.id;
		
		if (this.addToValueField(clientId, li)) {
			this.addTag(clientId, li);
			this.updatePadding(clientId);
		}
		this.clearField(clientId);
	},

	removeSelection: function(field, tagId, value) {
		clientId = field.id;

		this.removeFromValueField(clientId, value);
		this.removeTag(tagId);
		this.updatePadding(clientId);
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
		tagsField.insert("<li class='u-tag' id='u-tag-" + TagSelect.tagId + "'><button type='button' class='u-tag-button'><span><span class='u-tag-value'>" + li.innerHTML + "</span></span></button><em class='u-tag-remove' onclick='TagSelect.removeSelection(" + clientId + ", \"u-tag-" + TagSelect.tagId++ + "\", \"" + li.innerHTML + "\")'></em></li>");
	},
	
	removeTag: function(tagId) {
		var tag = $(tagId);
		tag.remove();
	},
	
	updatePadding: function(clientId) {
		var tagContainerWidth = $(clientId + '-tag-container').getWidth();
		var selectWidth = Element.getWidth($(clientId).getOffsetParent());
		var textArea = $(clientId).setStyle({paddingLeft: tagContainerWidth + 'px', width: selectWidth - tagContainerWidth + 'px'});
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
