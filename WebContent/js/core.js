if (typeof Object.create !== 'function') {
    Object.create = function (o) {
        function F() {}
        
        F.prototype = o;
        
        return new F();
    };
}

(function ($) {
    if (!window.PICS) {
        PICS = {};
    }
    
    /**
     * PICS Application
     * 
     * ajax()
     * define()
     * getClass()
     * getClasses()
     */
    PICS = Object.create((function () {
        var _classes = {};
        var _inits = [];
        
        function getClassName(class_path) {
            var class_parts = getClassParts(class_path);
            var class_parts_length = class_parts.length;
            
            return class_parts[class_parts_length - 1];
        }
        
        function getClassParts(class_path) {
            return class_path.split('.');
        }
        
        return {
            /**
             * Init
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
             * @options: An object literial configuration for an ajax request
             */
            ajax: function (options) {
                var defaults = {
                    url: window.location.href,
                    type: 'POST',
                    dataType: 'html',
                    data: {},
                    success: function(data, textStatus, XMLHttpRequest) {},
                    error: function(XMLHttpRequest, textStatus, errorThrown) {},
                    complete: function(XMLHttpRequest, textStatus) {}
                };
                
                var config = {};
                
                $.extend(config, defaults, options);
                
                return $.ajax(config);
            },
            
            /**
             * Define
             * 
             * @class_path: A qualified path to the class i.e. contractor.Flags or operator.flag.Criteria
             * @class_configuration: An object literal container a methods parameter and an optional extend parameter. 
             * The extend parameter must have  aqualified path to a class your extending from. The methods parameter must be
             * an object literial containing an optional init parameter, a method that will automatically be initialized on
             * document.ready
             * 
             * 
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
                
                function initClass(cls, class_path_string) {
                    if (typeof cls.init == 'function') {
                        _inits.push(class_path_string.join('.'));
                    }
                }
                
                // class_name must start with a capital letter
                if (class_name.substr(0, 1).search(/[A-Z]/) === -1) {
                    throw 'PICS.define() @class_path must include a valid class (PICS.{ClassName} or PICS.{namespace}.{ClassName})';
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
            
            /**
             * Modal
             */
            modal: function (options) {
                var modal = this.getClass('modal.Modal');
                
                modal.create(options);
                
                return modal; 
            }
        }
    }()));
    
    PICS._init();
}(jQuery));