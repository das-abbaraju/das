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
                    property = '"' + key + '"' + ':' + '"' + value.replace(/"/g, '\\"') + '"';
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
     * GUID Generator
     *
     * @format:
     */
    function guidGenerator(format) {
        if (typeof format != 'string') {
            throw 'guidGenerator:format must be a string (xx-xx combinations)';
        }

        function S4() {
            return (((1 + Math.random()) * 0x10000)|0).toString(16).substring(1);
        }

        var format_array = $.trim(format).split('');

        for (i = 0; i < format_array.length; i++) {
            if (format_array[i] == 'x') {
                format_array[i] = S4();
            }
        }

        return format_array.join('');
    }

    // key code bindings
    var KEY_LEFT = 37;
    var KEY_UP = 38;
    var KEY_RIGHT = 39;
    var KEY_DOWN = 40;
    var KEY_ENTER = 13;
    var KEY_ESC = 27;
    var KEY_TAB = 9;

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
        this.drop_down; // list generated

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
        addItem: function (event) {
            var element = $(event.target);

            var html = $('html');
            var container = this.container;

            var element_id = element.attr(this.config.data_id);

            // flag the added item as being selected
            var select_option = this.select.find('option[value="' + element_id + '"]');
            select_option.attr('selected', 'selected');

            // detach added item then re-attach below
            var drop_down = this.drop_down;
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
            item_input.focus();

            this.hideList.apply(this);

            if (this.config.postType == 'string') {
                var objects = this.getSelectedObjects();

                this.input.val(stringify(objects));
            }

            var selected_item = this.findItemById(element_id);

            this.original_element.trigger('add-item', selected_item);
        },

        /**
         * Create GUID
         *
         * @returns tagit_id
         */
        createGUID: function () {
            do {
                var tagit_id = 'tagit_' + guidGenerator('x');
            } while ($('#' + tagit_id).length);

            return tagit_id;
        },

        /**
         * Find Item by Id
         *
         * @returns selected object
         **/
        findItemById: function (selected) {
            var items = this.getItems();

            for (x = 0; x < items.length; x++) {
                if (items[x].id == selected) {
                    return items[x];
                }
            }
        },

        /**
         * Get ID
         *
         * Return unique tagit id that is bound on the container.
         * If the container has tagit_ab2c, then ab2c is returned as the id.
         *
         * @returns container_id
         */
        getId: function () {
            var container = this.container;

            if (container) {
                return container.attr('id').replace('tagit_', '');
            } else {
                return null;
            }
        },

        /**
         * Get Namespaced Event
         *
         * Accepts a event name (click, keyup, keydown, change, etc.)
         * Uses the containers id: tagit_ab2c (for example) to namespace the type of event.
         * We need to namespace events because of the object oriented nature of events and
         * bypassing class method references so that we can both bind and unbind events in an
         * object oriented nature.
         *
         * @event:
         *
         * @returns namespaced_event
         */
        getNamespacedEvent: function (event) {
            if (typeof event != 'string') {
                throw 'tagit: event must be a string';
            }

            return event + '.' + this.container.attr('id');
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

            var items = [];

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

        hideList: function () {
            var html = $('html');
            var drop_down = this.drop_down;

            if (drop_down) {
                drop_down.hide();
            }

            // unbind navigation since drop down is gone
            html.unbind(this.getNamespacedEvent('keydown'));

            // unbind click (to close drop down) since drop down is gone
            html.unbind(this.getNamespacedEvent('click'));
        },

        /**
         * Drop Down Item Hover
         *
         * Fire item hover with mouse
         *
         * @event
         */
        hoverList: function (event) {
            var element = $(event.target);
            var drop_down = this.drop_down;
            var hover_class = this.config.class_drop_down_item_hover;

            drop_down.find('a' + '.' + this.config.class_drop_down_item_hover).removeClass(hover_class);

            element.addClass(hover_class);
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

                container.attr('id', this.createGUID());
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

                    $.each(matches, function (key, value) {
                        var match = matches[key];

                        if (match) {
                            match = match.replace(/%/g, '');

                            var value = item[match];

                            if (value) {
                                label = label.replace(matches[key], value);
                            } else {
                                label = item.value;
                            }
                        } else {
                            label = item.value;
                        }
                    });

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
                this.original_element.hide();
                this.select = select;

                if (this.config.postType == 'list') {
                    this.select.data('Tagit', this);
                } else if (this.config.postType == 'string') {
                    this.input = this.original_element;

                    this.input.data('Tagit', this);

                    var objects = this.getSelectedObjects();

                    this.input.val(stringify(objects));
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

                var html = $('html');
                var container = this.container;

                // defining selectors to be used below
                var items_selector = '.' + this.config.class_items;
                var item_input_selector = '.' + this.config.class_item_input;
                var item_remove_selector = '.' + this.config.class_item_remove;

                var drop_down_toggle_selector = '.' + this.config.class_drop_down_toggle;

                // focus input on container click
                container.delegate(items_selector, 'click', function (event) {
                    // focus input
                    container.find(':input').focus();
                });

                // search item list
                container.delegate(item_input_selector, 'keyup', function (event) {
                    var timer;

                    return function (event) {
                        var key = event.keyCode;

                        // prevent certain keys that are used for item navigation to do anything but their designated keydown action
                        if (key == KEY_LEFT || key == KEY_UP || key == KEY_RIGHT || key == KEY_DOWN || key == KEY_ENTER || key == KEY_ESC) {
                            event.stopPropagation();
                        } else if (key != KEY_TAB && !timer) {
                            timer = setTimeout(function () {
                                that.searchItem.apply(that, [event]);

                                timer = null;
                            }, 150);
                        }
                    };
                }());

                // remove items
                container.delegate(item_remove_selector, 'click', function (event) {
                    event.stopPropagation();

                    var item_input = container.find(item_input_selector);

                    that.removeItem.apply(that, [event]);

                    // focus input
                    item_input.focus();

                    if (that.config.postType == 'string') {
                        var objects = that.getSelectedObjects();

                        that.input.val(stringify(objects));
                    }
                });

                // toggle item list view
                container.delegate(drop_down_toggle_selector, 'click', function (event) {
                    event.stopPropagation();

                    var item_input = container.find(item_input_selector);

                    if (!that.drop_down || !that.drop_down.is(':visible')) {
                        // populate + display drop down
                        that.initList(item_input.val());

                        // focus input
                        item_input.focus();
                    } else {
                        that.hideList.apply(that);
                    }
                });
            }

            var items = this.getItems();
            var items_selected = this.getItems(true);

            if (items instanceof Array && items.length && items_selected instanceof Array) {
                initContainer.apply(this);
                initSelectTag.apply(this);
                initItemsSelected.apply(this);

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

            var html = $('html');
            var container = this.container;

            // remove any existent drop down and just recreate it
            var id = this.getId();

            var current_drop_down = $('#tagit_drop_down_' + id);
            current_drop_down.remove();

            // create + configure drop down
            var drop_down = $('<ul>').addClass(this.config.class_drop_down);

            drop_down.attr('id', 'tagit_drop_down_' + container.attr('id').replace('tagit_', ''));
            drop_down.css('width', container.width());

            // fetch list of items to be displayed
            if (search_value != null) {
                var search_value = search_value.replace(/[#-?]/g, '\\$&');
                var regex = new RegExp(search_value, 'gi');

                var items = this.select.find('option:not(:selected)').filter(function () {
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

            // drop down item hover
            drop_down.delegate('a', that.getNamespacedEvent('mouseenter'), function (event) {
                that.hoverList.apply(that, [event]);
            });

            // enable navigation since drop down becomes present (must unbind first)
            html.unbind(that.getNamespacedEvent('keydown'));

            html.bind(that.getNamespacedEvent('keydown'), function (event) {
                that.navigateList.apply(that, [event]);
            });

            // hack to update hover event when scrolling drop down
            drop_down.bind('mousemove', function () {
                var timer;
                var move = 0;

                return function (event) {
                    move++;

                    if (!timer) {
                        timer = setTimeout(function () {
                            // closure to track mouse movements - if mouse is causing
                            // mouse movements then reset the bindings of "mouse" executing highlights
                            if (move > 2) {
                                drop_down.undelegate('a', that.getNamespacedEvent('mouseenter'));
                                drop_down.delegate('a', that.getNamespacedEvent('mouseenter'), function (event) {
                                    that.hoverList.apply(that, [event]);
                                });
                            }

                            timer = null;
                            move = 0;
                        }, 50);
                    }
                };
            }());

            // add items
            drop_down.delegate('a', 'click', function (event) {
                that.addItem.apply(that, [event]);
            });

            // close drop down when clicking outside widget
            html.bind(that.getNamespacedEvent('click'), function (event) {
                that.hideList.apply(that);
            });

            $('body').append(drop_down);

            this.drop_down = drop_down;

            // reposition 'fake' drop down
            this.moveList();

            // show drop down
            drop_down.show();

            // highlight first item in drop down
            drop_down.find('a:visible').first().addClass(this.config.class_drop_down_item_hover);
        },

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
        navigateList: function (event) {
            var that = this;

            var key = event.keyCode;

            var html = $('html');
            var container = this.container;
            var drop_down = this.drop_down;

            if (key == KEY_LEFT || key == KEY_UP || key == KEY_RIGHT || key == KEY_DOWN || key == KEY_ENTER || key == KEY_ESC || key == KEY_TAB) {
                if (key == KEY_ESC || key == KEY_TAB) {
                    var drop_down = this.drop_down;
                    var item_input = container.find('.' + this.config.class_item_input);

                    // initialize default input item settings
                    item_input.width(30);
                    item_input.val('');

                    // prevent esc key from unfocusing input (if focused)
                    if (key == KEY_ESC) {
                        event.preventDefault();
                    }

                    this.hideList.apply(this);
                } else {
                    event.preventDefault();

                    var drop_down_item = drop_down.find('a' + '.' + this.config.class_drop_down_item_hover);
                    var drop_down_row = drop_down_item.closest('li');
                    var drop_down = drop_down_row.closest('ul');

                    var hover_class = this.config.class_drop_down_item_hover;

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
                drop_down.undelegate('a', that.getNamespacedEvent('mouseenter'));
            }
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
        moveList: function () {
            var drop_down = this.drop_down;

            if (drop_down) {
                var container = this.container;

                drop_down.css({
                    left: container.offset().left,
                    top: container.offset().top + container.height() + 1
                });
            }
        },

        /**
         * Remove Item
         *
         * @event
         * @element
         */
        removeItem: function (event) {
            var element = $(event.target);
            var element_id = element.closest('a').attr(this.config.data_id);
            var item_row = element.closest('li');

            var select_option = this.select.find('option[value="' + element_id + '"]');
            select_option.attr('selected', null);

            item_row.remove();

            this.moveList();

            var selected_item = this.findItemById(element_id);

            this.original_element.trigger('remove-item', selected_item);
        },

        searchItem: function (event) {
            var html = $('html');
            var container = this.container;

            var item_input = $(event.target);
            var search_value = item_input.val();

            // dynamically increase input based on the value's character length
            item_input.width(30 + (search_value.length * 12));

            if (search_value == '') {
                this.hideList.apply(this);
            } else {
                // populate + show drop down
                this.initList(search_value);
            }
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