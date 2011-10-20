var TagSelect = Class.create({
    initialize: function(clientId, reset, autocompleter) {
    	this.clientId = clientId;
    	this.autocompleter = autocompleter;
        var uType = $(clientId).readAttribute('u:type'); 
        var focus = uType != 'single' ? '-uiTokenizer-container' : '-tags';

        var instance = this;
        Event.on($(clientId), 'keydown', this.onKeyDownT); 
        Event.on($(clientId), 'keyup', this.onKeyUpT);
        Event.on($(clientId + focus), 'click', function(){instance.onFocusT()});
        
        this.defaultInputSize();
        this.resetValue(reset); 
        this.showLabelT(uType);
    },

    onKeyUpT: function(event, field) {
        var instance = $(field.id).tagSelect;
        instance.onTabT(event, field);
        instance.updateInputSize(null);
    },
    
    onKeyDownT: function(event, field) {
        var type = $(field.id).readAttribute('u:type');

        var instance = $(field.id).tagSelect;
        if(type == 'multi') {
        	instance.backSpace(event, field)
        }
        
        if(type == 'single') {
        	instance.clearSingle(event, field.id); 
        }
    },
    
    onFocusT: function() {
        //Iterates page for all existing uiTypeaheadFocused classes.
        $$('.uiTypeaheadFocused').each(function(x) {
            //Removes all existing uiTypeaheadFocused classes.
            x.removeClassName('uiTypeaheadFocused'); 
        });   

        //Sets focus and adds uiTypeaheadFocused on selected component container.
        $(this.clientId + '-uiTokenizer-container').addClassName('uiTypeaheadFocused'); 
        $(this.clientId).activate();  
    },
    
    //Handles auto focus when using the tab key.
    onTabT: function(event, field) {
        if(event.keyCode == 9) {
        	this.onFocusT(field.id);  
        }        
    },     

    showLabelT: function(uType) {
        // Set focus and blur handlers to hide and show 
        // labels with 'placeholder-label' class names.
        if($(this.clientId + '-uiTokenLabel')) {            
            
            // Hide any placeholder labels where value field has an initial value.
            if($(this.clientId + "-values").value != '' && uType != null) {
                this.toggleLabelT(true);
            }   

            var instance = this;
            
            // Set handlers to show and hide labels.
            $(this.clientId).onfocus = function () {
                //Hides label
                instance.toggleLabelT(true);
            };

            $(this.clientId).onblur = function () { 
                //checks hidden value field for values.
                //checks to ensure the vertical configuration is excluded.
                //checks to be sure no text is present in client value field.                
                if (($(instance.clientId + "-values").value == '' || uType == null) && this.value == '') {
                    //Show labels
                    instance.toggleLabelT(false);
                }
            };        
        }
    },
    
    toggleLabelT: function(hide) {
        //toggles label place holder
        $(this.clientId + '-uiTokenLabel').style.textIndent = (hide) ? '-10000px' : '0px';
    },
	
    addToken: function(span) {
        var single = ($(this.clientId).readAttribute('u:type') == 'single');
        
        if(single) {
            this.resetValue("");
        }
        
        if (this.addToValueField(span)) {
            if (single) {                
                this.addTagSingle(span);
                this.updateInputSize(false);
                $(this.clientId).focus();
            } else {
                this.addTag(span);  
                this.updateInputSize(true);
                this.clearField();
            }            
        }        
    },

    removeToken: function(tagId, value) {
        this.removeFromValueField(value);
        this.removeTag(tagId);
        this.updateInputSize(null);
        this.defaultInputSize();
    },    
    
    //handles backspace key
    backSpace: function(event, field) { 
        if (!field.value && event.keyCode == 8) {            
            event.stop();

            var tags = $(field.id + '-tags').childElements();
            if (tags.length > 0) {
                var tag = tags[tags.length - 1];
                var values = $A($(field.id + '-values').value.split(';'));
                var value = values[values.length - 1];
                this.removeToken(tag.id, value);
            } else {
                this.defaultInputSize();
            }

            return false;
        }
        return true;
    },     
    
    //Sets the default width of the input field to size 48 when no values are present.
    defaultInputSize: function() {
        var tags = $(this.clientId + '-tags').childElements();

        if (tags.length == 0) {
            $(this.clientId).writeAttribute('size', 48); 
        }
        
        this.updateDropdownHeight();   
    },
    
    //On keyup, updates input box sizes
    updateInputSize: function(reset) {  
        if(this.clientId != undefined) {
            //resets value to null after new tag has been added.
            if(reset) {
                this.clearField();
            }
            
            var length = $(this.clientId).value.length;

            $(this.clientId).writeAttribute('size', 1); 
            //this sets the width of the input field to the length of the field value.
            if(length >= 1) {
                $(this.clientId).writeAttribute('size', length); 
            }              

            this.updateDropdownHeight();   

        }
    },
    
    //Updates dropdown menu height
    updateDropdownHeight: function() {
        //checks to see if dropdown exist
        if($(this.clientId + '-trigger')) {
            var cssClass = $(this.clientId).readAttribute('u:type') != null ? '-uiTokenizer-container' : '-uiTypeahead';
            var dropheight = $(this.clientId + cssClass).getLayout().get('height') - 2;
            
            $(this.clientId + '-trigger').setStyle('height:' + dropheight + 'px');
        }
    },
	
    triggerCompletion: function(field) {
    	this.autocompleter.activate();
    },
	
    addToValueField: function(span) {
        var valueField = $(this.clientId + '-values');
        var origin = valueField.value;
        if (origin.length == 0) {
            var values = new Array();
        } else {
            var values = $A(valueField.value.split(';'));
        }
        values.push(span.readAttribute('id'));
        var result = values.uniq().join(';');
        valueField.value = result;
        return origin != result;
    },

    removeFromValueField: function(value) {
        var valueField = $(this.clientId + '-values');
        var values = $A(valueField.value.split(';'));
        valueField.value = values.without(value).join(';');
    },
	
    addTag: function(span) { 
        //Gets label from nested span tag within autocomplete results
        var label = $(span.readAttribute('id') + '-label').innerHTML
        var tagsField = $(this.clientId + '-tags');
        tagsField.insert("<span title='"+ label + "' id='u-tag-" + TagSelect.tagId + "'>" + label + "<a onclick='$(\"" + this.clientId + "\").tagSelect.removeToken(\"u-tag-" + TagSelect.tagId++ + "\", \"" + span.readAttribute('id') + "\")'></a></span>");
    },
    
    addTagSingle: function(span) {
        $(this.clientId + '-tags').addClassName('selected');
        $(this.clientId).setValue(this.entityDecoder(span.firstChild.innerHTML));   
        $(this.clientId + '-tags').down(1).setAttribute('onClick', '$(\"' + this.clientId + '\").tagSelect.removeToken(\'u-tag-' + TagSelect.tagId++ +'\', \'' + span.readAttribute('id') + '\')');
        
        //Sets dropdown status to display none;
        this.dropdownStatus(this.clientId, false); 
    },
    
    //Decodes special characters in value labels.
    entityDecoder: function(str) {
        var ta = document.createElement("textarea");
        ta.innerHTML=str;
        return ta.value; 
    }, 
	
    removeTag: function(tagId) {
        if($(this.clientId).readAttribute('u:type') == 'single') {
            this.clearField();
            this.resetValue("");
            this.dropdownStatus(true);
            $(this.clientId + '-tags').removeClassName('selected'); 
        } else {
            var tag = $(tagId);
            tag.remove();
        }
    },

    //Clears input field values.
    clearField: function() {
        $(this.clientId).setValue("");        
    },   
    
    //Clears hidden field values.
    resetValue: function(reset) {
        $(this.clientId + '-values').setValue(reset);
    },    
    
    //clears single value when auto focus is present. Ignors custom ignor keys. 
    clearSingle: function(event) {
        if(!this.ignoreKeys(event) && $(this.clientId + '-tags').hasClassName('selected')) {
            $(this.clientId + '-tags').removeClassName('selected');
            //Sets hidden value to null.
            this.resetValue(""); 
            //Resets dropdown menu status to display block
            this.dropdownStatus(true);              
        }     
    },
    
    dropdownStatus: function(status) {
        var displayStatus = status ? "block" : "none";
        
        //Checks for existing drop down menu
        if($(this.clientId+ '-trigger')) {
            //If dropdown menu exist, sets menu container to display block.
            $(this.clientId+ '-trigger').setStyle({'display': displayStatus});
        }         
    },

    //handles keys that need to be ignored with clear single
    ignoreKeys: function(event) {
        var keyCodes = new Array(9, 16, 17, 18, 20, 33, 34, 35, 36, 45, 91, 92, 93, 144);        
        return keyCodes.indexOf(event.keyCode) != -1;
    }  
});

TagSelect.tagId = 0;
