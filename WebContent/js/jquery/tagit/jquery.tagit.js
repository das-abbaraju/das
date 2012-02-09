(function ($) {
    /**
     * Stringify
     * 
     * Takes an array of object(s) and stringifies it to JSON
     * 
     * @objects
     * 
     * @returns "[{key: value}]"
     */
    function stringify(objects) {
        if (!(objects instanceof Array)) {
            throw 'tagit: Invalid objects in stringify';
        }
        
        var items = [];
        
        for (var i in objects) {
            var object = objects[i];
            var item = '';
            
            item += '{';
                
            var properties = [];
            for (var j in object) {
                var key = j;
                var value = object[j];
                var property = '';
                
                if (object.hasOwnProperty(j)) {
                    property = '"' + key + '"' + ':' + '"' + value.replace('"', '\\"') + '"';
                }
                
                properties.push(property);
            }
            
            item += properties.join(',');
            
            item += '}';
            
            items.push(item);
        }
        
        return '[' + items.join(',') + ']';
    }
    
    /**
     * Tagit
     * 
     * Tagit class attached to the element initialized.
     * This class contains the current options / state of the initialized element.
     * 
     * @element
     * @options
     */
    function Tagit(element, options) {
        this.original_element = element;
        this.config = options;
        
        this.container; // container of the widget
        this.select; // select generated
        this.input; // input generated
        
        this.items;
        this.items_selected;
        
        // flags to hold ajax request execution - true if ajax request is active
        this.items_xhr = null;
        this.items_selected_xhr = null;
    }
    
    Tagit.prototype = {
        /**
         * Add Item
         * 
         * @event
         * @element
         */
        addItem: function (event, element) {
            // flag the added item as being selected
            var select_option = this.select.find('option[value="' + element.attr(this.config.data_id) + '"]');
            select_option.attr('selected', 'selected');
            
            // detach added item then re-attach below
            var drop_down = element.closest('ul');
            var drop_down_row = element.closest('li').detach();
            
            // find input item
            var item_input = this.container.find('.' + this.config.class_item_input);
            var item_input_row = item_input.closest('li');
            
            // create remove item link
            var item_remove = $('<span>').addClass(this.config.class_item_remove).html('&#215;');
            
            // re-attach added item + rename class
            item_input_row.before(drop_down_row);
            drop_down_row.attr('class', this.config.class_item_row);
            
            // update name of added tag + append to dom
            var tag_name = element.attr(this.config.data_tag_name);
            element.html(tag_name);
            element.append(item_remove);
            
            // initialize default input item settings
            item_input.width(30);
            item_input.val('');
            
            // hide drop down
            drop_down.hide();
            
            // reposition 'fake' drop down
            this.moveList(drop_down);
        },
        
        /**
         * Get Items
         */
        getItems: function (is_selected) {
            /**
             * Get Items From Source
             * 
             * @source
             * 
             * @returns source
             */
            function getItemsFromSource(source) {
                var that = this;
                var attempt = 1;
                
                /**
                 * Fetch Items
                 * 
                 * Create ajax request to pull up sources. Attempts to try 3 times if there are errors.
                 */
                function fetchItems() {
                    if (attempt > 3) {
                        // set source to empty if not found after 3 attempts
                        if (is_selected == undefined) {
                            that.config.source = [];
                        } else {
                            that.config.source_selected = [];
                        }
                    } else {
                        $.ajax({
                            url: source,
                            dataType: 'json',
                            success: function (data, textStatus, XMLHttpRequest) {
                                if (!(data instanceof Array)) {
                                    throw 'tagit: Invalid data returned from getItemsFromSource()';
                                }
                                
                                // set class source to fetched data
                                if (is_selected == undefined) {
                                    that.config.source = data;
                                } else {
                                    that.config.source_selected = data;
                                }
                                
                                // re-initialize plugin after the source has been set
                                that.init();
                            },
                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                                if (typeof console.log === 'function') {
                                    console.log('tagit: ' + errorThrown + "\n" + 'response: ' + XMLHttpRequest.responseText);
                                } else {
                                    throw 'tagit: ' + errorThrown;
                                }
                                
                                // increase attempt
                                attempt++;
                                
                                // try again
                                fetchItems();
                            },
                            complete: function(XMLHttpRequest, textStatus) {
                                // flag stop ajax request
                                if (is_selected == undefined) {
                                    that.items_xhr = false;
                                } else {
                                    that.items_selected_xhr = false;
                                }
                            }
                        });
                        
                        // flag start ajax request - used so that multiple ajax requests are not started if one is currently being executed
                        if (is_selected == undefined) {
                            that.items_xhr = true;
                        } else {
                            that.items_selected_xhr = true;
                        }
                    }
                }
                
                fetchItems();
            }
            
            /**
             * Is Valid
             * 
             * @items
             * 
             * @returns boolean
             */
            function isValid(items) {
                if (!(items instanceof Array)) {
                    return false;
                }
                
                // items must all be of the same type
                // items must be either string or object with value key
                if (items.length) {
                    var item_type;
                    
                    for (var i in items) {
                        var item = items[i];
                        
                        if (!(typeof item == 'string' || (typeof item == 'object' && item.value) || (item_type && typeof item != item_type))) {
                            return false;
                        }
                        
                        item_type = typeof item;
                    }
                }
                
                return true;
            }
            
            /**
             * Configure
             * 
             * Construct expected format of items from given items / source
             * 
             * @items
             * 
             * @returns items
             */
            function configure(items) {
                var item = items[0];
                
                for (var i in items) {
                    var item = items[i];
                    
                    if (typeof item == 'string') {
                        items[i] = {
                            id: item,
                            value: item
                        };
                    } else if (typeof item == 'object') {
                        if (!item.id) {
                            items[i].id = item.value;
                        }
                    }
                }
                
                return items;
            }
            
            /**
             * Alphabetize
             * 
             * @item_a
             * @item_b
             * 
             * @returns sort
             */
            function alphabetize(item_a, item_b) {
                var item_name_a;
                var item_name_b;
                
                if (item_a.value) {
                    var item_name_a = item_a.value.toLowerCase();
                }
                
                if (item_b.value) {
                    var item_name_b = item_b.value.toLowerCase();
                }
                
                if (item_name_a < item_name_b) {
                    return -1;
                } else if (item_name_a > item_name_b) {
                    return 1;
                } else {
                    return 0;
                }
            }
            
            var items;
            
            if (is_selected == undefined) {
                // if items already exist and have been initialized return same list
                if (this.items instanceof Array && this.items.length) {
                    return this.items;
                }
                
                var source = this.config.source;
                
                if (typeof source == 'string' && !this.items_xhr) {
                    getItemsFromSource.apply(this, [source]);
                }
                
                if (source instanceof Array) {
                    var items = source;
                    
                    if (!isValid(items)) {
                        throw 'tagit: Source does not return valid items';
                    }
                    
                    items = configure(items);
                    
                    items.sort(alphabetize);
                    
                    this.items = items;
                }
            } else {
                // if items already exist and have been initialized return same list
                if (this.items_selected instanceof Array && this.items_selected.length) {
                    return this.items_selected;
                }
                
                var source = this.config.source_selected;
                
                if (typeof source == 'string' && !this.items_selected_xhr) {
                    getItemsFromSource.apply(this, [source]);
                }
                
                if (source instanceof Array) {
                    var items = source;
                    
                    if (!isValid(items)) {
                        throw 'tagit: Source does not return valid items';
                    }
                    
                    items = configure(items);
                    
                    items.sort(alphabetize);
                    
                    this.items_selected = items;
                }
            }
            
            return items;
        },
        
        /**
         * Get Selected Objects
         * 
         * Returns JSON for all of the objects that are 'currently'
         * selected in the widget.
         * 
         * @returns Array of JSON Objects
         */
        getSelectedObjects: function () {
            var that = this;
            
            var objects = [];
            
            $.each(that.select.find('option:selected'), function (key, value) {
                var element = $(this);
                
                var items = $.grep(that.config.source, function (item) {
                    return item.id == element.attr('value');
                });
                
                objects.push(items[0]);
            });
            
            return objects;
        },
        
        /**
         * Init
         * 
         * Initialize plugin + container + features + events
         */
        init: function () {
            function initContainer() {
                // create html elements
                var container = $('<div>').addClass(this.config.class_container);
                var items = $('<ul>').addClass(this.config.class_items);
                var item_input_row = $('<li>').addClass(this.config.class_item_row);
                var item_input = $('<input type="text" autocomplete="off">').addClass(this.config.class_item_input);
                var drop_down_toggle = $('<a>').addClass(this.config.class_drop_down_toggle);
                
                container.css('width', this.original_element.width());
                container.append(items);
                container.prepend(drop_down_toggle);
                
                items.append(item_input_row);
                item_input_row.append(item_input);
                
                // hide original element
                this.original_element.hide();
                this.original_element.before(container);
                
                this.container = container;
            }
            
            function initSelectTag() {
                /**
                 * Get Formatted Name From item
                 * 
                 * @item
                 * @formatter: combination of string replacements i.e. "%id% - %value% %country%" -> 12 - BASF United States
                 * 
                 * @returns label
                 */
                function getFormattedNameFromItem(item, formatter) {
                    var matches = formatter.match(/%.*?%/g);
                    var label = formatter;
                    
                    for (var j in matches) {
                        var match = matches[j].replace(/%/g, '');
                        var value = item[match];
                        
                        if (value) {
                            label = label.replace(matches[j], value);
                        }
                    }
                    
                    return label;
                }
                
                // create select
                var select = $('<select>');
                
                select.attr('multiple', 'multiple');
                select.addClass(this.config.class_select);
                
                if (this.config.postType == 'list') {
                    select.attr('id', this.original_element.attr('id'));
                    select.attr('name', this.original_element.attr('name'));
                } else if (this.config.postType == 'string') {
                    select.attr('disabled', 'disabled');
                } else {
                    throw 'tagit: Unknown postType:' + ' ' + this.config.postType;
                }
                
                select.hide();
                
                // create options for select
                for (var i in items) {
                    var item = items[i];
                    var select_option = $('<option>').attr('value', item.id);
                    
                    var drop_down_name = getFormattedNameFromItem(item, this.config.formatter_drop_down)
                    var tag_name = getFormattedNameFromItem(item, this.config.formatter_tag)
                    
                    select_option.text(items[i].value);
                    select_option.attr(this.config.data_drop_down_name, drop_down_name);
                    select_option.attr(this.config.data_tag_name, tag_name)
                    
                    select.append(select_option);
                }
                
                // append to dom
                this.container.after(select);
                
                // update element to be referenced + attach dom element to class
                this.original_element.remove();
                this.select = select;
                
                if (this.config.postType == 'list') {
                    this.select.data('Tagit', this);
                }
            }
            
            function initInputTag() {
                // create input tag
                var input = $('<input>');
                
                input.attr('id', this.original_element.attr('id'));
                input.attr('name', this.original_element.attr('name'));
                
                input.hide();
                
                // append to dom
                this.select.after(input);
                
                // attach dom element to class
                this.input = input;
                
                if (this.config.postType == 'string') {
                    this.input.data('Tagit', this);
                }
            }
            
            function initItemsSelected() {
                var that = this;
                
                // find input item
                var item_input = this.container.find('.' + this.config.class_item_input);
                var item_input_row = item_input.closest('li');
                
                $.each(items_selected, function (key, value) {
                    var option = that.select.find('option[value="' + value.id + '"]');
                    option.attr('selected', 'selected');
                    
                    var drop_down_row = $('<li>');
                    drop_down_row.attr('class', that.config.class_item_row);
                    
                    var drop_down_name = option.attr(that.config.data_drop_down_name);
                    var tag_name = option.attr(that.config.data_tag_name);
                    
                    // configure drop down item
                    var drop_down_item = $('<a>').html(drop_down_name);
                    drop_down_item.attr(that.config.data_drop_down_name, drop_down_name);
                    drop_down_item.attr(that.config.data_tag_name, tag_name);
                    drop_down_item.attr(that.config.data_id, option.val());
                    
                    // create remove item link
                    var item_remove = $('<span>').addClass(that.config.class_item_remove).html('&#215;');
                    drop_down_item.append(item_remove);
                    
                    drop_down_row.append(drop_down_item);
                    
                    // append to dom
                    item_input_row.before(drop_down_row);
                });
            }
            
            function initEvents() {
                var that = this;
                
                // key code bindings
                var KEY_LEFT = 37; 
                var KEY_UP = 38;
                var KEY_RIGHT = 39;
                var KEY_DOWN = 40; 
                var KEY_ENTER = 13; 
                var KEY_ESC = 27;
                var KEY_TAB = 9;
                
                var html = $('html');
                var container = this.container;
                
                // defining selectors to be used below
                var items_selector = '.' + this.config.class_items;
                var item_input_selector = '.' + this.config.class_item_input;
                var item_remove_selector = '.' + this.config.class_item_remove;
                
                var drop_down_selector = '.' + this.config.class_drop_down;
                var drop_down_item_selector = '.' + this.config.class_drop_down + ' ' + 'a';
                var drop_down_item_hover_selector = '.' + this.config.class_drop_down + ' ' + 'a' + '.' + this.config.class_drop_down_item_hover;
                var drop_down_toggle_selector = '.' + this.config.class_drop_down_toggle;
                
                /**
                 * Drop Down Item Hover
                 * 
                 * Fire item hover with mouse
                 * 
                 * @event
                 */
                function drop_down_item_hover(event) {
                    var hover_class = that.config.class_drop_down_item_hover;
                    
                    container.find(drop_down_item_hover_selector).removeClass(hover_class);
                    
                    $(this).addClass(hover_class);
                }
                
                /**
                 * Drop Down Item Navigate
                 * 
                 * Firing left, up, right, down, enter, esc, or tab
                 * will fire a navigation command to the drop down list
                 * 
                 * @event
                 * 
                 * @returns boolean
                 */
                function drop_down_item_navigate(event) {
                    var key = event.keyCode;
                    
                    if (key == KEY_LEFT || key == KEY_UP || key == KEY_RIGHT || key == KEY_DOWN || key == KEY_ENTER || key == KEY_ESC || key == KEY_TAB) {
                        if (key == KEY_ESC || key == KEY_TAB) {
                            var drop_down = container.find(drop_down_selector);
                            var item_input = container.find('.' + that.config.class_item_input);

                            // initialize default input item settings
                            item_input.width(30);
                            item_input.val('');
                            
                            // prevent esc key from unfocusing input (if focused)
                            if (key == KEY_ESC) {
                                event.preventDefault();
                            }
                            
                            // hide drop down
                            drop_down.hide();
                            
                            // unbind navigation since drop down is gone
                            html.unbind('keydown', drop_down_item_navigate);
                        } else {
                            event.preventDefault();
                            
                            var drop_down_item = container.find(drop_down_item_hover_selector);
                            var drop_down_row = drop_down_item.closest('li');
                            var drop_down = drop_down_row.closest('ul');
                            
                            var hover_class = that.config.class_drop_down_item_hover;
                            
                            // "click" item selected
                            if (key == KEY_ENTER) {
                                drop_down_item.click();
                                
                                return false;
                            }
                            
                            var drop_down_height = drop_down.height();
                            var scroll_top = drop_down.scrollTop();
                            
                            // highlight previous item
                            if (key == KEY_LEFT || key == KEY_UP) {
                                var move_to_this_row = drop_down_row.prev('li');
                                
                                if (move_to_this_row.length) {
                                    var scroll_offset = move_to_this_row.position().top + scroll_top;
                                    
                                    // unhighlight - highlight items
                                    drop_down_item.removeClass(hover_class);
                                    move_to_this_row.find('a').addClass(hover_class);
                                    
                                    if (scroll_offset <= scroll_top) {
                                        var drop_down_item_previous_position = scroll_offset;
                                        
                                        drop_down.scrollTop(drop_down_item_previous_position);
                                    }
                                }
                                
                            // highlight next item
                            } else if (key == KEY_RIGHT || key == KEY_DOWN) {
                                var move_to_this_row = drop_down_row.next('li');
                                
                                if (move_to_this_row.length) {
                                    var row_to_calculate_scroll_offset = move_to_this_row.next('li');
                                    
                                    // unhighlight - highlight items
                                    drop_down_item.removeClass(hover_class);
                                    move_to_this_row.find('a').addClass(hover_class);
                                    
                                    if (row_to_calculate_scroll_offset.length) {
                                        var scroll_bottom = scroll_top + drop_down_height;
                                        var scroll_offset = row_to_calculate_scroll_offset.position().top + scroll_top;
                                        
                                        if (scroll_offset >= scroll_bottom) {
                                            var drop_down_item_next_position = scroll_offset - drop_down_height;
                                            
                                            drop_down.scrollTop(drop_down_item_next_position);
                                        }
                                    } else { 
                                        drop_down.scrollTop(drop_down[0].scrollHeight);
                                    }
                                }
                            }
                        }
                        
                        // remove mouse enter event (which will also highlight items - enhance usability)
                        container.undelegate(drop_down_item_selector, 'mouseenter', drop_down_item_hover);
                    }
                }
                
                // close drop down when clicking outside widget
                html.bind('click', function () {
                    var drop_down = container.find(drop_down_selector);
                    
                    // hide drop down
                    drop_down.hide();
                    
                    // unbind navigation since drop down is gone
                    html.unbind('keydown', drop_down_item_navigate);
                });
                
                // prevent dorp down from closing when clicking inside widget
                container.bind('click', function (event) {
                    event.stopPropagation();
                });
                
                // focus input on container click
                container.delegate(items_selector, 'click', function (event) {
                    // focus input
                    container.find(':input').focus();
                });
                
                // search item list on key up
                container.delegate(item_input_selector, 'keyup', function () {
                    var timer;
                    
                    return function (event) {
                        var key = event.keyCode;
                        
                        // prevent certain keys that are used for item navigation to do anything but their designated keydown action
                        if (key == KEY_LEFT || key == KEY_UP || key == KEY_RIGHT || key == KEY_DOWN || key == KEY_ENTER || key == KEY_ESC) {
                            event.stopPropagation();
                        } else if (key != KEY_TAB && !timer) {
                            var item_input = $(this);
                            
                            timer = setTimeout(function () {
                                var drop_down = container.find(drop_down_selector);
                                var search_value = item_input.val();
                                
                                // dynamically increase input based on the value's character length
                                item_input.width(30 + (search_value.length * 12));
                                
                                if (search_value == '') {
                                    // hide drop down
                                    drop_down.hide();
                                    
                                    // unbind navigation since drop down is gone
                                    html.unbind('keydown', drop_down_item_navigate);
                                } else {
                                    // populate + show drop down
                                    that.initList(search_value);
                                    
                                    // enable navigation since drop down becomes present
                                    html.bind('keydown', drop_down_item_navigate);
                                }
                                
                                timer = null;
                            }, 150);
                        }
                    };
                }());
                
                // remove items
                container.delegate(item_remove_selector, 'click', function (event) {
                    event.stopPropagation();
                    
                    var element = $(this);
                    
                    that.removeItem.apply(that, [event, element]);
                    
                    if (that.config.postType == 'string') {
                        var objects = that.getSelectedObjects();
                        
                        that.input.val(stringify(objects));
                    }
                });
                
                // toggle item list view
                container.delegate(drop_down_toggle_selector, 'click', function (event) {
                    var drop_down = container.find(drop_down_selector);
                    var item_input = container.find(item_input_selector); 
                        
                    if (!drop_down.is(':visible')) {
                        // populate + display drop down
                        that.initList(item_input.val());
                        
                        // focus input
                        item_input.focus();
                        
                        // enable navigation since drop down becomes present
                        html.bind('keydown', drop_down_item_navigate);
                    } else {
                        // hide drop down
                        drop_down.hide();
                        
                        // unbind navigation since drop down is gone
                        html.unbind('keydown', drop_down_item_navigate);
                    }
                });
                
                // add items
                container.delegate(drop_down_item_selector, 'click', function (event) {
                    var element = $(this);
                    
                    // add item to items list
                    that.addItem.apply(that, [event, element]);
                    
                    if (that.config.postType == 'string') {
                        var objects = that.getSelectedObjects();
                        
                        that.input.val(stringify(objects));
                    }
                    
                    // unbind navigation since drop down is gone
                    html.unbind('keydown', drop_down_item_navigate);
                });
                
                // track mouse movement and hover
                container.bind('mousemove', function () {
                    var timer;
                    var move = 0;
                    
                    return function (event) {
                        move++;
                        
                        if (!timer) {
                            timer = setTimeout(function () {
                                // closure to track mouse movements - if mouse is causing
                                // mouse movements then reset the bindings of "mouse" executing highlights
                                if (move > 2) {
                                    container.undelegate(drop_down_item_selector, 'mouseenter', drop_down_item_hover);
                                    container.delegate(drop_down_item_selector, 'mouseenter', drop_down_item_hover);
                                }
                                
                                timer = null;
                                move = 0;
                            }, 50);
                        }
                    };
                }());
                
                container.delegate(drop_down_item_selector, 'mouseenter', drop_down_item_hover);
            }
            
            var items = this.getItems();
            var items_selected = this.getItems(true);
            
            if (items instanceof Array && items.length && items_selected instanceof Array) {
                initContainer.apply(this);
                initSelectTag.apply(this);
                initItemsSelected.apply(this);
                
                if (this.config.postType == 'string') {
                    initInputTag.apply(this);
                }
                
                initEvents.apply(this);
            }
        },
        
        /**
         * Init List
         * 
         * @search_value
         */
        initList: function (search_value) {
            var that = this;
            
            var container = this.container;
            
            // remove any existent drop down and just recreate it
            var current_drop_down = container.find('.' + this.config.class_drop_down);
            current_drop_down.remove();
            
            // create + configure drop down
            var drop_down = $('<ul>').addClass(this.config.class_drop_down);
            drop_down.css('width', container.width());
            
            // fetch list of items to be displayed
            if (search_value != null) {
                var items = this.select.find('option:not(:selected)').filter(function () {
                    var regex = new RegExp(search_value, 'gi');
                    
                    return $(this).attr(that.config.data_drop_down_name).search(regex) != -1;
                });
            } else {
                var items = this.select.find('option');
            }
            
            // create drop down items
            if (items.length) {
                $.each(items, function (key, value) {
                    var element = $(value);
                    
                    var drop_down_name = element.attr(that.config.data_drop_down_name);
                    var tag_name = element.attr(that.config.data_tag_name);
                    
                    var drop_down_row = $('<li>').addClass(that.config.class_drop_down_row);
                    
                    // configure drop down item
                    var drop_down_item = $('<a>').html(drop_down_name);
                    drop_down_item.attr(that.config.data_drop_down_name, drop_down_name);
                    drop_down_item.attr(that.config.data_tag_name, tag_name);
                    drop_down_item.attr(that.config.data_id, element.val());
                    
                    drop_down.append(drop_down_row);
                    drop_down_row.append(drop_down_item);
                });
            
            // no items found
            } else {
                var drop_down_row = $('<li>').addClass('empty').html('No Matches');
                drop_down.append(drop_down_row); 
            }
            
            // reposition 'fake' drop down
            this.moveList(drop_down);
            
            container.append(drop_down);
            
            // show drop down
            drop_down.show();
            
            // highlight first item in drop down
            drop_down.find('a:visible').first().addClass(that.config.class_drop_down_item_hover);
        },
        
        /**
         * Option
         * 
         * @option
         * @value
         * 
         * @returns option
         */
        option: function (option, value) {
            if (typeof option !== 'string') {
                throw 'tagit: Option must be a string';
            }
            
            function setOption(option, value) {
                this.config[option] = value;
            }
            
            function getOption(option) {
                return this.config[option];
            }
            
            if (!value) {
                return getOption.apply(this, [option]);
            } else {
                setOption.apply(this, [option, value]);
            }
        },
        
        /**
         * Options
         * 
         * @options
         * 
         * @returns options
         */
        options: function (options) {
            function setOptions(options) {
                this.config = options;
            }
            
            function getOptions() {
                return this.config;
            }
            
            if (!options) {
                return getOptions.apply(this);
            } else {
                setOptions.apply(this, [options]);
            }
        },
        
        /**
         * Move List
         * 
         * @drop_down
         */
        moveList: function (drop_down) {
            var items = drop_down.closest('.' + this.config.class_items);
            
            drop_down.css({
                top: items.height()
            });
        },
        
        /**
         * Remove Item
         * 
         * @event
         * @element
         */
        removeItem: function (event, element) {
            var container = this.container;
            var drop_down = container.find('.' + this.config.class_drop_down);
            var item_row = element.closest('li');
            
            var select_option = this.select.find('option[value="' + element.closest('a').attr(this.config.data_id) + '"]');
            select_option.attr('selected', null);
            
            item_row.remove();
            
            this.moveList(drop_down);
        }
    };
    
    $.fn.tagit = function (method) {
        var cls = this.data('Tagit');
        
        if (!cls) {
            if (method instanceof Object || !method) {
                var options = method;
                
                var defaults = {
                    class_container: 'tagit-container',
                    class_drop_down: 'tagit-drop-down',
                    class_drop_down_item_hover: 'tagit-hover',
                    class_drop_down_row: 'tagit-drop-down-row',
                    class_drop_down_toggle: 'tagit-drop-down-toggle',
                    class_item_input: 'tagit-item-input',
                    class_item_row: 'tagit-item-row',
                    class_item_remove: 'tagit-item-remove',
                    class_items: 'tagit-items',
                    class_select: 'tagit-select',
                    
                    data_drop_down_name: 'data-drop-down-name',
                    data_tag_name: 'data-tag-name',
                    data_id: 'data-id',
                    
                    formatter_drop_down: '%value%',
                    formatter_tag: '%value%',
                    
                    postType: 'list', // list or string
                    
                    width: 'auto'
                };
                
                var config = {};
                $.extend(config, defaults, options);
                
                var cls = new Tagit(this, config);
                
                cls.init();
            }
        } else {
            if (typeof method == 'string') {
                var args = [];
                
                for (var i in arguments) {
                    args.push(arguments[i]);
                }
                
                var arguments = args.slice(1, arguments.length);
                
                return cls[method].apply(cls, arguments);
            } else {
                throw 'tagit: Tagit plugin is already initialized on this element';
            }
        }
    };
}(jQuery));