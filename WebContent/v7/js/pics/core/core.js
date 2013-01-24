/**
 * Prototypal Inheritance
 * 
 * Helper method to create class instance
 * http://javascript.crockford.com/prototypal.html
 */
if (typeof Object.create !== 'function') {
    Object.create = function (o) {
        function F() {}
        
        F.prototype = o;
        
        return new F();
    };
}

/**
 * Define console.log if doesn't exist
 * Add a wrapper around console.log
 *
 * usage: log('inside coolFunc',this,arguments);
 * http://paulirish.com/2009/log-a-lightweight-wrapper-for-consolelog/
 */
if (typeof console === "undefined"){
    window.console = {};
    window.console.log = function(){};
}
window.log=function(){log.history=log.history||[];log.history.push(arguments);if(this.console){console.log(Array.prototype.slice.call(arguments))}};

(function ($) {
    /**
     * PICS Application
     * 
     * ajax()
     * define()
     * getClass()
     * getClasses()
     * modal()
     */
    PICS = Object.create((function () {
        // private storage for classes and initializers
        var _classes = {};
        var _inits = [];
        
        // get class name from a qualified class path "foo.bar.Foo"
        function getClassName(class_path) {
            var class_parts = getClassParts(class_path);
            var class_parts_length = class_parts.length;
            
            return class_parts[class_parts_length - 1];
        }
        
        // get class parts from a qualified class path separated by "." i.e. "foo.bar.Foo"
        function getClassParts(class_path) {
            return class_path.split('.');
        }
        
        return {
            /**
             * _Init
             * 
             * Initialize all init methods when the document is ready
             * Clear out init methods once they have been executed
             */
            _init: function () {
                var that = this;
                
                $(document).ready(function () {
                    for (var i in _inits) {
                        var cls = that.getClass(_inits[i]);
                        
                        if (typeof cls.init == 'function') {
                            cls.init();
                        }
                    }
                    
                    _inits = [];
                });
            },
            
            /**
             * Ajax
             * 
             * @options: An object literal configuration for an ajax request
             */
            ajax: function (options) {
                var defaults = {
                    url: window.location.href,
                    type: 'POST',
                    dataType: 'html',
                    data: {},
                    success: function(data, textStatus, jqXHR) {},
                    error: function(jqXHR, textStatus, errorThrown) {},
                    complete: function(jqXHR, textStatus) {}
                };
                
                var config = {};
                
                $.extend(config, defaults, options);
                
                if ($.browser.msie && $.browser.version <= 8) {
                    var date = new Date();
                    
                    config.data['ie_timestamp'] = date.getTime();
                }
                
                return $.ajax(config);
            },
            
            debounce: function (func, threshold) {
                var timeout;
                
                return function () {
                    var context = this,
                        args = arguments;
                    
                    if (timeout) {
                        clearTimeout(timeout);
                    }
                    
                    timeout = setTimeout(function() {
                        func.apply(context, args);
                        
                        timeout = null;
                    }, threshold || 250);
                };
            },
            
            /**
             * Define
             * 
             * @class_path: A qualified path to the class i.e. contractor.Flags or operator.flag.Criteria
             * @class_configuration: An object literal container a methods parameter and an optional extend parameter. 
             * The extend parameter must have  aqualified path to a class your extending from. The methods parameter must be
             * an object literial containing an optional init parameter, a method that will automatically be initialized on
             * document.ready
             */
            define: function (class_path, class_configuration) {
                if (typeof class_path != 'string') {
                    throw 'PICS.define() @class_path must be a string';
                }
                
                if (typeof class_configuration != 'object') {
                    throw 'PICS.define() @class_configuration must be a configuration object';
                }
                
                var that = this;
                var class_parts = getClassParts(class_path);
                var class_name = getClassName(class_path);
                var class_object = _classes;
                var class_path_string = [];
                
                // create a class based off the "methods" parameter of the class_configuration object
                function createClass() {
                    var class_methods = class_configuration.methods;
                    
                    if (typeof class_methods == 'function') {
                        return class_methods();
                    } else if (typeof class_methods == 'object') {
                        return class_methods;
                    } else {
                        throw 'class "' + class_path_string.join('.') + '" requires a "methods" parameter to return an object';
                    }
                }
                
                // create a child class  off an existing class based off the "extend" parameter of the class_configuration object
                function extendClass(cls) {
                    var class_name = class_configuration.extend;
                    
                    if (class_name != undefined) {
                        var extended_class = Object.create(that.getClass(class_name));
                        
                        for (var i in cls) {
                            extended_class[i] = cls[i]; 
                        }
                        
                        cls = extended_class;
                    }
                    
                    return cls;
                }
                
                // add the initialize method if a class has defined an init() in its "methods"
                // the init() will automatically be called when the document is loaded
                function initClass(cls, class_path_string) {
                    if (typeof cls.init == 'function') {
                        _inits.push(class_path_string.join('.'));
                    }
                }
                
                // class_name must start with a capital letter
                if (class_name.substr(0, 1).search(/[A-Z]/) === -1) {
                    throw 'PICS.define() @class_path must include a valid class ({ClassName} or {namespace}.{ClassName})';
                }
                
                for (var i in class_parts) {
                    var class_part = class_parts[i];
                    
                    class_path_string.push(class_part);
                    
                    // create undefined namespaces
                    if (class_object[class_part] == undefined && class_part != class_name) {
                        class_object[class_part] = {};
                        
                    // do not allow classes to be overwritten
                    } else if (class_object[class_part] != undefined  && class_part == class_name) {
                        throw 'class "' + class_path_string.join('.') + '" is already defined';
                        
                    } else if (class_part == class_name) {
                        // creating class
                        var cls = createClass();
                        
                        // extending class
                        var cls = extendClass(cls);
                        
                        // init class
                        initClass(cls, class_path_string);
                            
                        class_object[class_part] = cls;
                    }
                    
                    class_object = class_object[class_part];
                }
            },
            
            /**
             * Get Class
             * 
             * @class_path:
             */
            getClass: function (class_path) {
                var class_parts = getClassParts(class_path);
                var class_object = _classes;
                var class_path_string = [];
                
                for (var i in class_parts) {
                    var class_part = class_parts[i];
                    
                    class_path_string.push(class_part);
                    
                    if (class_object[class_part] == undefined) {
                        throw 'class "' + class_path_string.join('.') + '" is not defined';
                    }
                    
                    class_object = class_object[class_part]; 
                }
                
                return class_object;
            },
            
            /**
             * Get Classes
             */
            getClasses: function () {
                return _classes;
            },
            
            getRequestParameters: function (url) {
                if (typeof url != 'string') {
                    throw 'Invalid url';
                }
                
                var query_string = {},
                    regex = new RegExp('([^?=&]+)(=([^&]*))?', 'g');
                
                url.replace(regex, function(match, p1, p2, p3, offset, string) {
                    query_string[p1] = p3;
                });
                
                return query_string;
            },
            
            loading: function (selector) {
                var element = $(selector);
                var height = element.height();
                
                var loading = $('<div class="loading">');
                loading.css({
                    background: '#FFFFFF url(images/loaders/in-progress.gif) no-repeat center'
                });
                
                if (height >= 500) {
                    loading.css({
                        height: 500,
                        lineHeight: 500
                    });
                } else if (height > 50 && height < 500) {
                    loading.css({
                        height: height,
                        lineHeight: height
                    });
                } else {
                    loading.css({
                        height: 30,
                        lineHeight: 30,
                        padding: 10
                    });
                }
                
                $(selector).html(loading);
            },
            
            /**
             * Modal
             * 
             * Shorthand for creating a modal
             */
            modal: function (options) {
                var modal = this.getClass('modal.Modal');
                
                modal.create(options);
                
                return modal; 
            },
            
            throttle: function (func, delay) {
                var timer;
                
                return function () {
                    var context = this, 
                        args = arguments;
                    
                    if (!timer) {
                        timer = setTimeout(function () {
                            func.apply(context, args);
                            
                            timer = null;
                        }, delay || 250);
                    }
                };
            }
        }
    }()));
    
    PICS._init();
}(jQuery));