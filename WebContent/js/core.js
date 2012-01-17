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
             * Define
             * 
             * @class_path:
             * @class_configuration: {
             *     extend: // optional
             *     methods: {
             *         init: // optional
             *         
             *         ...
             *     }
             * }
             */
            define: function (class_path, class_configuration) {
                if (typeof class_path != 'string') {
                    throw 'PICS.define() @class_path must be a string';
                }
                
                if (typeof class_configuration != 'object') {
                    throw 'PICS.define() @class_configuration must be a configuration object';
                }
                
                var class_parts = getClassParts(class_path);
                var class_name = getClassName(class_path);
                
                // class_name must start with a capital letter
                if (class_name.substr(0, 1).search(/[A-Z]/) === -1) {
                    throw 'PICS.define() @class_path must include a valid class (PICS.{ClassName} or PICS.{namespace}.{ClassName})';
                }
                
                var class_object = _classes;
                var class_path_string = [];
                
                for (var i in class_parts) {
                    var key = class_parts[i];
                    
                    class_path_string.push(key);
                    
                    // create undefined namespaces
                    if (class_object[key] == undefined && key != class_name) {
                        class_object[key] = {};
                        
                    // do not allow classes to be overwritten
                    } else if (class_object[key] != undefined  && key == class_name) {
                        throw class_path_string.join('.') + ' is already defined';
                        
                    } else if (key == class_name) {
                        var new_class;
                        
                        var class_extend = class_configuration.extend;
                        var class_methods = class_configuration.methods;
                        
                        // creating class
                        if (typeof class_methods == 'function') {
                            new_class = class_methods();
                        } else if (typeof class_methods == 'object') {
                            new_class = class_methods;
                        } else {
                            throw class_path_string.join('.') + ' requires a "methods" parameter to return an object';
                        }
    
                        // extending class
                        if (class_extend != undefined) {
                            var extended_class = Object.create(this.getClass(class_extend));
                            
                            for (var i in new_class) {
                                extended_class[i] = new_class[i]; 
                            }
                            
                            new_class = extended_class;
                        }
                        
                        // init class
                        if (typeof new_class.init == 'function') {
                            _inits.push(class_path_string.join('.'));
                        }
                            
                        class_object[key] = new_class;
                    }
                    
                    class_object = class_object[key];
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
                    var key = class_parts[i];
                    
                    class_path_string.push(key);
                    
                    if (class_object[key] == undefined) {
                        throw class_path_string.join('.') + ' is not defined';
                    }
                    
                    class_object = class_object[key]; 
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
             * Init
             * 
             * Initialize all init methods when the document is ready
             * Clear out init methods once they have been executed
             */
            init: function () {
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
            }
        }
    }()));
    
    PICS.init();
}(jQuery));