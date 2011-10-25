(function ($) {
    // Title: surge.core.js
    //
    // Provides common functionality used by other components. The functions and classes in this file
    // define the basis of the Surge Platform JS libraries, and it must be included before any others.
    //
    // Core Features:
    // The most pervasive features provided by the core module are data-widget, templates and 
    // viewReady, all of which are closely related, and oriented around providing a highly
    // structured and declarative way to initialize parts of the page, not just on the initial
    // page load (as with jQuery's ready event) but also when loading additional content using
    // ajax methods and widgets.
    //
    // Widgets:
    // The first of these is data-widget. This is an attribute that may be applied to any element,
    // and when a page, ajax view or template loads, the element will be constructed as a jQuery UI
    // widget of the given type. For example:
    //
    // (code)
    // <input data-widget="datepicker" name="Birthday"/>
    // (end)
    //
    // This will automatically initialize the jQuery UI 'datepicker' widget on this element when
    // the page loads. We can also specify an options dictionary directly in markup, if necessary:
    //
    // (code)
    // <input data-widget="datepicker" data-options="minDate: '-1m', maxDate: '+1m'/>
    // (end)
    //
    // data-options is interpreted as the body of a JavaScript object constructor and may contain
    // any JS expressions.
    //
    // View ready:
    // When dealing with content loaded into a page with an ajax request, one frequently runs into
    // a problem: some kind of initialization code needs to happen for that content, but the only
    // real event available is .ready, which doesn't actually provide any context - it is triggered
    // on the document element, not on the specific content being loaded, so you have to do extra
    // work to locate the specific elements that were loaded. This can be fragile or require hacky
    // server side solutions like auto-generated unique element IDs. The <jQuery.viewReady>
    // function provides an alternative solution. Whenever an ajax view is loaded, after all
    // data-widgets have been initialized, this event is triggered on that view, passing the
    // container element as 'this', so any further queries may be easily scoped.
    //
    // Templates:
    // TBD

    // Function: Class
    // Defines a new class. Details TBD.
    //
    // Example:
    // (code)
    // Class("MyApp.NiftyThing", Surge.Component, {
    //   MyProperty: new Surge.Property(), // auto property
    //   MyTrackedProperty: new Surge.Property({changeTracked: true}), // see below
    //
    //   constructor: function (myProperty) {
    //     this.setMyProperty(myProperty); // setter is defined automatically
    //   },
    //
    //   someMethod: function () {
    //     return this.getMyProperty() * 100;
    //   },
    //
    //   $staticMethod: function () {
    //     alert("It's super effective!");
    //   }
    // });
    //
    // var instance = new MyApp.NiftyThing(10);
    // alert(instance.someMethod());
    // MyApp.NiftyThing.staticMethod();
    // instance.listen('valueChanged', function (propertyName, oldValue, newValue) {
    //   alert(newValue);
    // });
    // instance.setMyTrackedProperty(7);
    // (end)
    function Class(className, superClass, newMethods) {
        if (arguments.length == 2) {
            return AnonymousClass(className, superClass);
        } else {
            var klass = Class(superClass, newMethods);

            var components = className.split('.');
            var ns = window;
            for (var i = 0; i < components.length - 1; ++i) {
                if (!ns[components[i]])
                    ns[components[i]] = {};

                ns = ns[components[i]];
            }

            ns[components[components.length - 1]] = klass;
            klass.className = components.pop();
            klass.classNamespace = components.join('.');
            return klass;
        }
    };

    function AnonymousClass(superClass, newMethods) {
        var scp = superClass.prototype;
        var fn = function () { }, subProto;
        // subClass = newMethods.constructor if provided or the execution of superClass (passing through subClass context and aruguments)
        var subClass = newMethods.constructor != Object.prototype.constructor ? newMethods.constructor : function () { superClass.apply(this, arguments); };
        // Merge any properties from superClass into subClass
        $.extend(subClass, superClass);
        
        // Create new function with a prototype of superClass.prototype and set it as
        // subClass.prototype
        fn.prototype = superClass.prototype;
        subClass.prototype = subProto = new fn();
        subClass.prototype.constructor = subClass;
        subClass.superclass = superClass;

        // Copy superClass properties into subClass
        subClass.properties = $.extend({}, superClass.properties);

        // setup subclasses properties
        if (!superClass.subclasses)
            superClass.subclasses = [];
        subClass.subclasses = [];
        superClass.subclasses.push(subClass);

        // Push members to new prototype
        for (var method in newMethods) {
            var item = newMethods[method];
            if (Surge.Property && item instanceof Surge.Property) {
                item.extendClass(subClass, method);
            } else if (method.charAt(0) == '$') {
                subClass[method.substr(1)] = item;
            } else {
                subClass.prototype[method] = item;
            }
        }

        if (subClass.initializeClass)
            subClass.initializeClass();

        return subClass;
    };

    // Function: Define
    //
    // Adds a dictionary of items at a named location, creating any intermediate objects required.
    //
    // Parameters:
    //  ns - The name of a namespace. This should be a dotted list of components, e.g. Surge.Math.Geometry.
    //  dict - An object. Each property of the object will be added to the namespace referenced by ns.
    //
    // Example:
    // (begin code)
    // Define("Foo.Bar", {
    //     hello: function () {
    //         alert("Hello, World!");
    //     }
    // });
    //
    // Foo.Bar.hello();
    // (end)

    function Define(ns, dict) {
        var components = ns.split("."), location = window;

        for (var i = 0; i < components.length; ++i) {
            if (!location[components[i]])
                location[components[i]] = {};

            location = location[components[i]];
        }

        return $.extend(location, dict);
    }

    // Namespace: Surge
    // The root namespace for classes and functions provided by the Surge Platform.

    $.extend(true, window, {
        Class: Class,
        Define: Define,

        Surge: {
            // During view load and template initialization events, this variable stores the containing element
            // into which content is loading. It should not generally be referenced directly by user code,
            // as its value is provided as the 'this' variable to those load events.
            _loadContext: document,

            _getModelFromContext: function () {
                return $(this._loadContext).data("model");
            },

            // Function: setModel
            // Used to set the internal javascript model for the current page.
            //
            // Parameters:
            // model -
            setModel: function (model) {
                $(Surge._loadContext).data("model", model);
            },

            // Function: initContext
            // Initialize the widgets in the current context  
            initContext: function () {
                var model = Surge._getModelFromContext();
                Surge.initializeWidgets(Surge._loadContext, $.extend({}, model, { Model: model }));
            },

            // Function: initializeWidgets
            // For all elements in the context tagged with a 'widget' data-attribute 
            //   execute the associated widget constructor and remove the widget data-attribute
            //   if an 'options' string data-attribute is provided then use it to as a parameter for the constructor
            // Set the view as ready
            //
            // Parameters:
            // context - area to scan and initialize
            // values = Optional set of values containing an options property with string contents representing the inside of an object literal
            initializeWidgets: function (context, values) {
                $("[data-widget]", context).each(function () {
                    var options = $(this).data("options");

                    if (options)
                        with (values || {}) options = [eval('({' + options + '})')];

                    var plugin = $.fn[$(this).data("widget")];

                    if (plugin == null) {
                        if (window.console) console.log('missing widget: ' + $(this).data("widget"));
                        return;
                    }

                    plugin.apply($(this), options || []);
                    $(this).removeAttr("data-widget");
                });

                $(context).triggerHandler('viewready', [Surge._getModelFromContext()]);
            }
        }
    });

    (function () {
        var identifierTable = {};

        // Function: generateIdentifier
        // Generates a unique identifier with the given prefix. Each prefix has its own counter.
        //
        // Example:
        // (code)
        // Surge.generateIdentifier("foo"); // foo-1
        // Surge.generateIdentifier("foo"); // foo-2
        // Surge.generateIdentifier("foo"); // foo-3
        // Surge.generateIdentifier("bar"); // bar-1
        // (end)
        Surge.generateIdentifier = function (prefix) {
            if (identifierTable[prefix])
                return prefix + "-" + (++identifierTable[prefix]);

            return prefix + "-" + (identifierTable[prefix] = 1);
        }
    })();

    // Class: Surge.Component
    // A base class which provides common mechanisms for events, properties, and naming.
    //
    // Constructor Parameters:
    // properties - optional hash to set property values
    Class("Surge.Component", Object, {
        $initializeClass: function () {
            var defaults = {};

            for (var propertyName in this.properties) {
                var property = this.properties[propertyName];

                if (property.options['default']) {
                    defaults[property.name] = property.options['default'];
                    property.setValue(this.prototype, property.options['default']);
                }
            }

            this.prototype.defaults = defaults;
        },

        constructor: function (properties) {
            if (properties)
                this.setPropertyValues(properties);
        },

        // Method: componentId
        // Returns a unique string identifier for the component. The ID is generated the first
        // time it is requested.
        componentId: function () {
            if (!this.__componentId)  
                this.__componentId = Surge.generateIdentifier(this.constructor.className ? this.constructor.className : "Component");

            return this.__componentId;
        },

        // Method: setPropertyValues
        // Sets multiple property values simultaneously from a dictionary.
        setPropertyValues: function (propertyValues) {
            for (var member in propertyValues) {
                var val = propertyValues[member];
                this.constructor.properties[member].setValue(this, val);
            }
        },

        // Method: listen
        // Listens for an event.
        //
        // Parameters:
        // eventName - The name of the event to listen for. This must exactly match the event name passed to <signal>. 
        //             A special name, *, may be used to listen for any event.
        // handler - A function to be invoked when the event is signalled. The handler function is provided
        //           the target object as its 'this' value, and any additional arguments to <signal> will also
        //           be passed to the handler function.
        listen: function (eventName, handler) {
            if (!this._listeners)
                this._listeners = {};

            var listeners = this._listeners[eventName];
            if (!listeners)
                listeners = this._listeners[eventName] = [];

            listeners.push(handler);
        },

        // Method: unlisten
        // Removes an event handler that was previously added using <listen>.
        unlisten: function (eventName, listener) {
            if (!this._listeners)
                return;

            var listeners = this._listeners[eventName];

            if (listeners) {
                for (var i = 0; i < listeners.length; ++i) {
                    if (listeners[i] == listener) {
                        listeners.splice(i, 1);
                        return true;
                    }
                }
            }

            return false;
        },

        // Method: signal
        // Signals an event.
        signal: function (eventName) {
            var eventArgs = [];

            for (var i = 1; i < arguments.length; ++i)
                eventArgs.push(arguments[i]);

            if (!this._listeners)
                return;

            if (this._eventSuppressions && this._eventSuppressions[eventName])
                return;

            if (eventName != '*')
                this.signal.apply(this, ['*', arguments]);

            var listeners = this._listeners[eventName];
            if (!listeners)
                return;

            for (var i = listeners.length - 1; i >= 0; --i)
                listeners[i].apply(this, eventArgs);
        },

        // Method: suppress
        // Suppresses all handlers for an event until <restore> is called. If suppress
        // is called for an event name which is already suppressed, the method does nothing:
        // the next call to restore will still end the suppression (no nesting is allowed).
        suppress: function (eventName) {
            if (!this._eventSuppressions)
                this._eventSuppressions = {};

            this._eventSuppressions[eventName] = true;
        },

        // Method: restore
        // Remove an event suppression set by <suppress>.
        restore: function (eventName) {
            if (!this._eventSuppressions)
                return;

            this._eventSuppressions[eventName] = false;
        },

        dispose: function () {
        }
    });

    // Class: Surge.Property
    // Property generator used to generate get and set methods. Can also generate properies which will raise 'change' events
    // Once instantiated use extendClass() to extend an object with these abilities
    // see <Class>
    //
		// Constructor Paremters Object Hash:
		//						changeTracked - inject extra code to signal 'valueChanged' when the setter is invoked
		//						nameOfOperation - 'private' indicates methods for that operation will be marked as private to the best of the frameworks abilities
		//						getter - custom function that will act as the property getter
		//						setter - custom function that wil act as the property setter
    Class("Surge.Property", Object, {
        $defaultOptions: {},

        constructor: function (options) {
            this.options = $.extend({}, this.constructor.defaultOptions, options);
        },

				// Method: getMethodName
				// Get the method name of preforming the given operation following conventions
        getMethodName: function (operation) {
            return (this.options[operation] == 'private' ? "_" : "") + operation + this.name;
        },

				// Method: getValue
				// Use the getter to get value of this property on the given object
        getValue: function (obj) {
            return this._getFn.call(obj);
        },

				// Method: setValue
				// Use the setter to set the value of this property on the given object
        setValue: function (obj, val) {
            return this._setFn.call(obj, val);
        },

				// Method: isLoaded
        isLoaded: function (obj) {
            return true;
        },

        _updateSubclasses: function (klass, name) {
            for (var i = 0; i < klass.subclasses.length; ++i) {
                var subClass = klass.subclasses[i];

                if (!subClass.properties[name])
                    subClass.properties[name] = this;

                if (subClass.subclasses)
                    this._updateSubclasses(subClass, name);
            }
        },

				// Method: extendClass
				// Inject property into the given class' prototype
				// including appending this to the class' list of properties and updating any subclasses
				// 
				// Parameters:
				// klass - The class to be extended
				// name - The name of this property
        extendClass: function (klass, name) {
            this.name = name;
            this.generateCode(klass.prototype);
            klass.properties[name] = this;

            if (klass.subclasses)
                this._updateSubclasses(klass, name);
        },

				// Method: generateCode
				// Inject code for property operations (eg getters and setters) into the provided function prototype
				// Prefer extendClass
        generateCode: function (proto) {
            var getId = this.getMethodName('get');
            var setId = this.getMethodName('set');
            var fieldId = '_' + this.name;
            var THIS = this;

            if (this.options.getter)
                proto[getId] = this._getFn = this.options.getter;
            else
                proto[getId] = this._getFn = new Function("return this." + fieldId + ";");

            if (this.options.setter) {
                proto[setId] = this._setFn = this.options.setter;
            } else {
                if (this.options.changeTracked) {
                    proto[setId] = this._setFn = function (val) {
                        var oldval = this[fieldId];
                        this[fieldId] = val;
                        this.signal('valueChanged', THIS.name, val, oldval);
                    };
                } else {
                    proto[setId] = this._setFn = function (val) {
                        this[fieldId] = val;
                    }
                }
            }
        }
    });


		// Function: jQuery.fn.loadView
		// For each selected element, load the contents of the url and then initialize the context/widgets
		//
		// Params:
		// url 
    $.fn.loadView = function (url) {
        return this.each(function () {
            var each = $(this);

            each.load(url, function () {
                Surge.initContext();
            });
        });
    };

    // Class: jQuery
    // Extensions to the global jQuery object, known as jQuery or $.
    $.extend($, {
        // Function: jQuery.viewReady
        // Registers a function to be invoked when a view's content loads. 
        // Inside the body of the callback provided, the value of 'this' refers to the container into which content is loading.
        //
        // Parameters:
        //  fn - A callback which will be invoked when the view is ready.
        viewReady: function (fn) {
            $(Surge._loadContext).one('viewready', fn);
        },

        // Function: jQuery.postJSON
        // Posts an object in JSON format to the given URL.
        //
        // Parameters:
        //	urlOrOptions - The URL to post to. Alternatively, an object containing a standard set of options which
        //                 indicate how the request should be proccessed. See below for details.
        //	data - A JavaScript object. Must be JSON-serializable (no circular references, simple data types, etc).
        //	success - A callback to be called when the operation completes successfully. 
        //            The function receives any returned JSON data as its first argument.
        //	error - A callback to be called when the operation fails to complete successfully.
        //
        // Example:
        // (code)
        // $.postJSON("/todolists/1", {
        //   Name: "My Critical Items!",
        //   Items: [
        //     { Id: 1, Name: "Buy plane tickets" },
        //     { Id: 2, Name: "Build death ray" }
        //   ]
        // }, function (result) {
        //   alert("TODO list saved.");
        // });
        // (end)
        //
        // Additional Options:
        // The urlOrOptions parameter ay be an objectm with properties that define standard behaviors for a
        // request. The following options are currently supported:
        //
        //  url - Required. The URL to post to.
        //  validate - (default true) If true, a collection of validation results returned from the server
        //             using the platform SurgeController.Status() method will automatically be displayed
        //             to the user if present. Additionally, the success method will be passed an additional
        //             argument, isValid, which indicates whether or not the posted data was valid.
        postJSON: function (urlOrOptions, data, success, error) {
            var requestedSuccess = success || urlOrOptions.success,
                options = {
                    url: urlOrOptions,
                    type: "POST",
                    data: $.toJSON(data || urlOrOptions.data),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: requestedSuccess,
                    error: error
                };

            if ($.isPlainObject(urlOrOptions)) {
                options.url = urlOrOptions.url;
            }

            if (urlOrOptions.validate !== false) {
                options.success = function (result) {
                    if (result && result.ValidationResults != undefined && result.ValidationResults.length) {
                        var status = "";

                        for (var i = 0; i < result.ValidationResults.length; ++i) {
                            var validation = result.ValidationResults[i];

                            status += validation.ErrorMessage + "<br/>";
                            $('#' + result.ValidationResults[i].MemberNames[0])
                                .addClass('ui-state-error')
                                .change(function () { $(this).removeClass('ui-state-error'); });
                        }

                        Surge.Notify.error(status);

                        requestedSuccess && requestedSuccess(result, false);

                        return;
                    }

                    requestedSuccess && requestedSuccess(result, true);
                };
            }

            if (!error) {
                options.error = function (error) {
                    Surge.Notify.error("An error occurred while processing your request.");
                };
            }

            return $.ajax(options);
        }
    });

    // Namespace: Surge.Console
    // Encapsulates a backlogging wrapper to the standard window.console object.
    //
    // The purpose of wrapping window.console into Surge.Console is that window.console is not
    // gaurenteed to be defined. Another draw back is that not all browsers support the
    // modern feature set that Firebug/Chrome support (see: http://getfirebug.com/logging )
    // This prevents fatal exceptions from occuring in code in some browsers.
    //
    // Surge.Console.log and Surge.Console.error are aliased as _log and _error.
    Define("Surge.Console", {
        _backlog: [],
        _groupDepth: 0, // Used by software emulated begin/end group
        _times: {},
        _groupEmulation: false,
        _call: function (type, args) {
            if (!window.console)
                Surge.Console._backlog.push({ type: type, args: args });
            else {
                if (Surge.Console._backlog.length > 0)
                    Surge.Console.backlog();

                // Emulate unsupported features or throw an error.
                if (!window.console[type]) {
                    var joinedArgs = Array.prototype.join.call(args, " ");
                    if (type == "group") {
                        Surge.Console._groupEmulation = true;
                        Surge.Console._groupDepth++;
                        Surge.Console.log("Group: " + Array.prototype.join.call(args, " "));
                    } else if (type == "groupEnd") {
                        if (--Surge.Console._groupDepth <= 0) {
                            Surge.Console._groupDepth = 0;
                            Surge.Console._groupEmulation = false;
                        }
                    } else if (type == "time") {
                        Surge.Console._times[joinedArgs] = new Date().getTime();
                    } else if (type == "timeEnd") {
                        Surge.Console.log([joinedArgs, ": ", new Date().getTime() - (Surge.Console._times[joinedArgs] || 0), "ms"].join(""));
                    } else if (window.console.error_) { console.error("Unsupported console event of type '" + type + "' with arguments [" + Array.prototype.join.call(args, ", ") + "]"); }
                } else if (window.console[type]) {
                    if (type == "log" && Surge.Console._groupEmulation)
                        args[0] = [new Array(Surge.Console._groupDepth + 1).join("-"), "> ", args[0]].join("");

                    if (console[type].apply)
                        console[type].apply(console, args); // IE8 does not support .apply like this.
                    else
                        console[type](Array.prototype.join.call(args, " "));
                }
            }
        },

        // Function: .backlog
        // Flushes the backlog to the display. This will happen automatically the first time Surge.Console is called and window.console exists.
        backlog: function () {
            if (!Surge.Console._backlog.length)
                return console.log("** Backlog is empty");
            Surge.Console.group("** Begin backlog of " + Surge.Console._backlog.length + " items");
            $.each(Surge.Console._backlog, function () { Surge.Console[this.type].apply(Surge.Console, this.args); })
            Surge.Console.groupEnd();
            Surge.Console._backlog = [];
        },

        // Function .dumpObject
        // Recursively dumps an object/array structure to Surge.Console.log
        dumpObject: function (object, maxDepth, curDepth, name) {
            function canRecurseObject() { return $.isArray(object) || $.isPlainObject(object); }
            function __log(text) { Surge.Console.log(text.join("")); }
            if (curDepth == undefined) curDepth = 0;
            if (maxDepth && ++curDepth >= maxDepth) return;

            var showName = name != undefined ? name : "unnamed";
            if ($.isArray(object)) {
                Surge.Console.group(["Array (length: ", object.length, ", name: ", showName, ")"].join(""));
                $.each(object, function (index, value) {
                    if (canRecurseObject(value))
                        Surge.Console.dumpObject(value, maxDepth, curDepth, index);
                    else
                        __log(["[", index, "] ", value]);
                });
                Surge.Console.groupEnd();
            } else if ($.isPlainObject(object)) {
                var keys = _(object).keys();
                Surge.Console.group(["Object (length: ", keys.length, ", name: ", showName, "):"].join(""));
                $.each(keys, function () {
                    if (canRecurseObject(object[this]))
                        Surge.Console.dumpObject(object[this], maxDepth, curDepth, this);
                    else
                        __log(["[", this, "] ", object[this]]);
                });
                Surge.Console.groupEnd();
            } else
                __log(["[", showName, "] ", object]);
        }
    });

    $.each(["log", "error", "debug", "info", "warn", "time", "timeEnd", "profile", "profileEnd", "trace", "group", "groupEnd", "dir", "dirxml"], function (index, functionName) {
        Surge.Console[functionName] = function () { Surge.Console._call(functionName, arguments); };
    });

    $.extend(true, window, {
        _log: Surge.Console.log,
        _error: Surge.Console.error
    });

    $.extend($.fn, {
        dumpObject: function (detailed) {
            function _getSelector(obj) {
                var selector = obj.attr("id") ? "#" + obj.attr("id") : "";
                if (obj.attr("class"))
                    selector += "." + obj.attr("class").split(" ").join(".");

                return selector;
            }
            Surge.Console.group(['$("', this.selector, '")'].join(""));
            var selfTop = this;
            this.each(function (index) {
                Surge.Console.group(['$("', selfTop.selector, '")[', index, "]"].join(""));
                var self = $(this);
                if ($(this).data("events")) {
                    Surge.Console.group("Events");
                    $.each($(this).data("events"), function (name, object) {
                        var description = ["type: ", name, ", amount: ", object.length].join("");
                        if (detailed === true) {
                            Surge.Console.group(description);
                            function _hasSameEvent(obj) { return $(obj).data("events") && ($(obj).data("events")[name] !== undefined || ($(obj).data("events").live && _.any($(obj).data("events").live, function (f) { f.origType == name; }))); }
                            var parents = _.select(self.parents(), _hasSameEvent);
                            var children = _.select(self.find("*"), _hasSameEvent);
                            Surge.Console.log(parents.length + " parents have this event" + (parents.length ? (": " + _.map(parents, function (obj) { return '$("' + _getSelector($(obj)) + '")' })) : ""));
                            Surge.Console.log(children.length + " children have this event" + (children.length ? (": " + _.map(children, function (obj) { return '$("' + _getSelector($(obj)) + '")' })) : ""));
                            $.each(object, function (index, event) {
                                Surge.Console.log(["[", index, "]: ", event.handler].join(""));
                            });
                            Surge.Console.groupEnd();
                        } else
                            Surge.Console.log(description);
                    });
                    Surge.Console.groupEnd();
                } else
                    Surge.Console.log("No events");
                Surge.Console.groupEnd();
            });
            Surge.Console.groupEnd();
            Surge.Console.groupEnd();
        }
    });

    $.extend($.fn, {
        // Function: .tmplTo
        // Instantiates a template using some given data and appends it to the selected container.
        //
        // The target of the jQuery selector should be a script element of type text/x-jquery-tmpl. Templates
        // may not be nested within each other, although a template may invoke another template using the jQuery template
        // syntax -- see the details of the jquery-tmpl plugin for more information.
        //
        // The selected script element may optionally have a data-oninstance attribute. If present, the function
        // named by the value of this attribute will be invoked whenever the template is instantiated into a container.
        // Within the body of the oninstance function, 'this' refers to the container into which the template
        // was instantiated. This handler runs after any data-widget attributes have been initialized.
        //
        // Parameters:
        //  target - a jQuery object, DOM element, or selector specifying the location at which content should be added.
        //  data - The data to use when instantiating the template. See the jquery-tmpl documentation for further details.
        //
        // Example:
        // (begin code)
        // <script type='text/x-jquery-tmpl' id='Test-Template'>
        //  <div>Name: ${FirstName} ${LastName}</div>
        //  <div>Age: ${Age}</div>
        // </script>
        //
        // <div class="container">
        // </div>
        //
        // <script type='text/javascript'>
        //  $(function () {
        //    $("#Test-Template").tmplTo(".container", {FirstName: "Bob", LastName: "Bobson", Age: 23});
        //  });
        // </script>
        tmplTo: function (target, data, options) {
            var tmpl = this.data("template");

            if (!tmpl) {
                tmpl = this.template();
                this.data("template", tmpl);
            }

            if (!tmpl)
                return this;

            var expr = this.data("oninstance") || "";
            tmpl.onInstance = eval(expr);

            if (expr && !tmpl.onInstance)
                throw new Error("Invalid template onInstance method: " + expr);

            return $.tmplTo(target, tmpl, data, options);
        },

        // Function: .tmplDialog
        // Creates a dialog, using a template to provide its content.
        //
        // The target of the selector should be a template element suitable for use with .tmpl or .tmplTo.
        // When the dialog is closed, it will automatically be destroyed and its content removed from the document.
        //
        // Use of this method is strongly recommended over direct instantiation of .dialog. .dialog
        // is very error prone due to its behavior of moving the dialog element to the body element,
        // which prevents the dialog from being automatically destroyed when its original parent element
        // is removed. This can cause serious resource leaks, especially in Internet Explorer.
        //
        // Parameters:
        //    options - The options to pass to the dialog constructor.
        //      model - The model data to be used to instantiate the template.
        //
        // Example:
        // (code)
        // <script type='text/x-jquery-tmpl' id='Confirm-Missiles'>
        //   <strong>Warning:</strong> ${Count} missiles will be launched at ${Target}. This operation cannot be undone. Continue?
        // </script>
        //
        // <script>
        // $.viewReady(function() {
        //    var dlg = $("#Confirm-Missiles").tmplDialog({
        //      title: "Confirm Launch", modal: true, resizable: false, buttons: {
        //        "Launch!": function() { alert("Missiles launched."); dlg.dialog("close"); },
        //        "Maybe Later": function() { alert("Aww, OK."); dlg.dialog("close"); }
        //      }
        //    }, {
        //      Count: 2, Target: "Doctor Evil"
        //    });
        // });
        // </script>
        // (end)
        tmplDialog: function (options, model) {
            var dlg = $("<div>").dialog(options)
            .bind("dialogclose", function () { dlg.dialog("destroy").remove(); });

            this.tmplTo(dlg, model);

            dlg.dialog('option', 'position', dlg.dialog('option', 'position'));
            return dlg;
        },

        startLoading: function (timeout) {
            var self = $(this[0]);
            var tid = setTimeout(function () {
                if ($.blockUI)
                    self.block({ message: "Loading" });
            }, timeout || 250);

            self.data("loadingTimeout", tid);
        },

        stopLoading: function () {
            var self = $(this[0]);
            var tid = self.data("loadingTimeout");

            if ($.blockUI)
                self.unblock();

            if (tid)
                clearTimeout(tid);

            self.data("loadingTimeout", null);
        }
    });

    $.extend($, {
        tmplTo: function (target, tmpl, data, options) {
            options = $.extend({
                initializeWidgets: true
            }, options);

            var tmplArgs = $.extend({}, data, { Model: data }), oldContext = Surge._loadContext;
            try {
                var currentModel = Surge._getModelFromContext();
                Surge._loadContext = $(target)[0];

                if (Surge._getModelFromContext() != currentModel)
                    Surge.setModel(currentModel);

                var result = $.tmpl(tmpl, tmplArgs).appendTo(Surge._loadContext);
                if (options.initializeWidgets)
                    Surge.initializeWidgets(target, tmplArgs);

                if ($.isFunction(tmpl.onInstance))
                    tmpl.onInstance.call(Surge._loadContext, data, result.tmplItem());

                return result;
            } finally {
                Surge._loadContext = oldContext;
            }
        }
    });

    // Namespace: Surge.Notify
    // Provides helper functions for displaying commonly used notice messages.
    //
    // Dependencies:
    //  * ~/_surge/Scripts/jquery.pnotify.js
    //  * ~/_surge/CSS/jquery.pnotify.default.css
    Define("Surge.Notify", {

        // Function: message
        // Display a notification message. This function should be used to display notices
        // pertaining to successfully completed operations or other non-error conditions.
        //
        // Parameters:
        //   title - The title of the message.
        //   message - The body of the message.
        message: function (title, message) {
            $.pnotify({
                pnotify_title: title,
                pnotify_text: message
            });
        },

        // Function: error
        // Display an error message. This function should be used to display notices
        // pertaining to failed operations or other error conditions.
        //
        // Parameters:
        //  message - The body of the message.
        error: function (message, location) {
            $.pnotify({
                pnotify_title: "Error",
                pnotify_text: message,
                pnotify_type: "error"
            });
        }
    });

    RegExp.escape = function (text) {
        return text.replace(/[-[\]{}()*+?.,\\^$|#]/g, "\\$&");
    }

    // Initialize data-widgets and run viewready for the top level page.
    $(Surge.initContext);

    // Do not cache ajax requests.
    $.ajaxSetup({ cache: false });

})(jQuery);
