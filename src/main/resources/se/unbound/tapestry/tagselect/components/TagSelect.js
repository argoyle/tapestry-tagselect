var TagSelect = {
    tagId: 0,
    
    initialize: function(clientId, reset) {
        var uType = $(clientId).readAttribute('u:type'); 
        var focus = uType != 'single' ? '-uiTokenizer-container' : '-tags';

        Event.on($(clientId), 'keydown', TagSelect.onKeyDownT); 
        Event.on($(clientId), 'keyup', TagSelect.onKeyUpT);
        Event.on($(clientId + focus), 'click', function(){TagSelect.onFocusT(clientId)});
        
        this.defaultInputSize(clientId);
        this.resetValue(clientId, reset); 
        this.showLabelT(clientId, uType);
    },

    onKeyUpT: function(event, field) {
        TagSelect.onTabT(event, field);
        TagSelect.updateInputSize(field.id, null);
    },
    
    onKeyDownT: function(event, field) {
        var type = $(field.id).readAttribute('u:type');
        
        if(type == 'multi') {
            TagSelect.backSpace(event, field)
        }
        
        if(type == 'single') {
            TagSelect.clearSingle(event, field.id); 
        }
    },
    
    onFocusT: function(clientId) {
        //Iterates page for all existing uiTypeaheadFocused classes.
        $$('.uiTypeaheadFocused').each(function(x) {
            //Removes all existing uiTypeaheadFocused classes.
            x.removeClassName('uiTypeaheadFocused'); 
        });   

        //Sets focus and adds uiTypeaheadFocused on selected component container.
        $(clientId + '-uiTokenizer-container').addClassName('uiTypeaheadFocused'); 
        $(clientId).activate();  
    },
    
    //Handles auto focus when using the tab key.
    onTabT: function(event, field) {
        if(event.keyCode == 9) {
            TagSelect.onFocusT(field.id);  
        }        
    },     

    showLabelT: function(clientId, uType) {
        // Set focus and blur handlers to hide and show 
        // labels with 'placeholder-label' class names.
        if($(clientId + '-uiTokenLabel')) {            
            
            // Hide any placeholder labels where value field has an initial value.
            if($(clientId + "-values").value != '' && uType != null) {
                this.toggleLabelT(clientId, true);
            }   

            // Set handlers to show and hide labels.
            $(clientId).onfocus = function () {
                //Hides label
                TagSelect.toggleLabelT(clientId, true);
            };

            $(clientId).onblur = function () { 
                //checks hidden value field for values.
                //checks to ensure the vertical configuration is excluded.
                //checks to be sure no text is present in client value field.                
                if (($(clientId + "-values").value == '' || uType == null) && this.value == '') {
                    //Show labels
                    TagSelect.toggleLabelT(clientId, false);
                }
            };        
        }
    },
    
    toggleLabelT: function(clientId, hide) {
        //toggles label place holder
        $(clientId + '-uiTokenLabel').style.textIndent = (hide) ? '-10000px' : '0px';
    },
	
    addToken: function(clientId, span) {
        var single = ($(clientId).readAttribute('u:type') == 'single');
        
        if(single) {
            this.resetValue(clientId, "");
        }
        
        if (this.addToValueField(clientId, span)) {
            if (single) {                
                this.addTagSingle(clientId, span);
                this.updateInputSize(clientId, false);
                $(clientId).focus();
            } else {
                this.addTag(clientId, span);  
                this.updateInputSize(clientId, true);
                this.clearField(clientId);
            }            
        }        
    },

    removeToken: function(clientId, tagId, value) {
        this.removeFromValueField(clientId, value);
        this.removeTag(clientId, tagId);
        this.updateInputSize(clientId, null);
        this.defaultInputSize(clientId);
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
                TagSelect.removeToken(field.id, tag.id, value);
            } else {
                this.defaultInputSize(field);
            }

            return false;
        }
        return null;
    },     
    
    //Sets the default width of the input field to size 48 when no values are present.
    defaultInputSize: function(clientId) {
        var tags = $(clientId + '-tags').childElements();

        if (tags.length == 0) {
            $(clientId).writeAttribute('size', 48); 
        }
        
        this.updateDropdownHeight(clientId);   
    },
    
    //On keyup, updates input box sizes
    updateInputSize: function(clientId, reset) {  
        if(clientId != undefined) {
            //resets value to null after new tag has been added.
            if(reset) {
                this.clearField(clientId);
            }
            
            var length = $(clientId).value.length;

            $(clientId).writeAttribute('size', 1); 
            //this sets the width of the input field to the length of the field value.
            if(length >= 1) {
                $(clientId).writeAttribute('size', length); 
            }              

            this.updateDropdownHeight(clientId);   

        }
    },
    
    //Updates dropdown menu height
    updateDropdownHeight: function(clientId) {
        //checks to see if dropdown exist
        if($(clientId + '-trigger')) {
            var cssClass = $(clientId).readAttribute('u:type') != null ? '-uiTokenizer-container' : '-uiTypeahead';
            var dropheight = $(clientId + cssClass).getLayout().get('height') - 2;
            
            $(clientId + '-trigger').setStyle('height:' + dropheight + 'px');
        }
    },
	
    triggerCompletion: function(field) {
        this.fireEvent(field, 'keydown');
    },
	
    addToValueField: function(clientId, span) {
        var valueField = $(clientId + '-values');
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

    removeFromValueField: function(clientId, value) {
        var valueField = $(clientId + '-values');
        var values = $A(valueField.value.split(';'));
        valueField.value = values.without(value).join(';');
    },
	
    addTag: function(clientId, span) { 
        //Gets label from nested span tag within autocomplete results
        var label = $(span.readAttribute('id') + '-label').innerHTML
        var tagsField = $(clientId + '-tags');
        tagsField.insert("<span title='"+ label + "' id='u-tag-" + TagSelect.tagId + "'>" + label + "<a onclick='TagSelect.removeToken(\"" + clientId + "\", \"u-tag-" + TagSelect.tagId++ + "\", \"" + span.readAttribute('id') + "\")'></a></span>");
    },
    
    addTagSingle: function(clientId, span) {
        $(clientId + '-tags').addClassName('selected');
        $(clientId).setValue(this.entityDecoder(span.firstChild.innerHTML));   
        $(clientId + '-tags').down(1).setAttribute('onClick', 'TagSelect.removeToken(\''+ clientId +'\', \'u-tag-' + TagSelect.tagId++ +'\', \'' + span.readAttribute('id') + '\')');
        
        //Sets dropdown status to display none;
        this.dropdownStatus(clientId, false); 
    },
    
    //Decodes special characters in value labels.
    entityDecoder: function(str) {
        var ta = document.createElement("textarea");
        ta.innerHTML=str;
        return ta.value; 
    }, 
	
    removeTag: function(clientId, tagId) {
        if($(clientId).readAttribute('u:type') == 'single') {
            this.clearField(clientId);
            this.resetValue(clientId, "");
            this.dropdownStatus(clientId, true);
            $(clientId + '-tags').removeClassName('selected'); 
        } else {
            var tag = $(tagId);
            tag.remove();
        }
    },

    //Clears input field values.
    clearField: function(clientId) {
        $(clientId).setValue("");        
    },   
    
    //Clears hidden field values.
    resetValue: function(clientId, reset) {
        $(clientId + '-values').setValue(reset);
    },    
    
    //clears single value when auto focus is present. Ignors custom ignor keys. 
    clearSingle: function(event, clientId) {
        if(!TagSelect.ignorKeys(event) && $(clientId + '-tags').hasClassName('selected')) {
            $(clientId + '-tags').removeClassName('selected');
            //Sets hidden value to null.
            TagSelect.resetValue(clientId, ""); 
            //Resets dropdown menu status to display block
            TagSelect.dropdownStatus(clientId, true);              
        }     
    },
    
    dropdownStatus: function(clientId, status) {
        var displayStatus = status ? "block" : "none";
        
        //Checks for existing drop down menu
        if($(clientId+ '-trigger')) {
            //If dropdown menu exist, sets menu container to display block.
            $(clientId+ '-trigger').setStyle({'display': displayStatus});
        }         
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
    },
    
    //handles keys that need to be ignored with clear single
    ignorKeys: function(event) {
        var keyCodes = new Array(9, 16, 17, 18, 20, 33, 34, 35, 36, 45, 91, 92, 93, 144);        
        return keyCodes.indexOf(event.keyCode) != -1;
    }  
}
