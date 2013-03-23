(function ($, window, document, undefined) {
    "use strict"; // jshint ;_;
    
    var rules = [{
        key: 'validate-required',
        message: 'Cannot be blank',
        pattern: /^.+$/
    }, {
        key: 'validate-min-length',
        message: 'Minimum length is {0}',
        pattern: function (length) {
            if (parseInt(length) == NaN) return /^.*$/;
            
            return new RegExp([
                "^",
                ".{" + length + ",}",
                "$"
            ].join(''));
        }
    }, {
        key: 'validate-max-length',
        message: 'Max length is {0}',
        pattern: function (length) {
            if (parseInt(length) == NaN) return /^.*$/;
            
            return new RegExp([
                "^",
                ".{," + length + "}",
                "$"
            ].join(''));
        }
    }, {
        key: 'validate-currency',
        message: 'Please enter the currency without a symbol',
        pattern: new RegExp([
            "^",
            "(",
                "[0-9]+",
                "([.,][0-9]{2})?",
            "|",
                "[0-9]{1,3}",
                "([,][0-9]{3})*",
                "([.][0-9]{2})?",
            "|",
                "[0-9]{1,3}",
                "([.][0-9]{3})*",
                "([,][0-9]{2})?",
            ")",
            "$"
        ].join(''))
    }, {
        key: 'validate-date',
        message: 'Please use a valid YYYY-MM-DD format',
        pattern: new RegExp([
            "^",
            "(19|20)[0-9]{2}",
            "-",
            "(0[1-9]|1[012])",
            "-",
            "(0[1-9]|[12][0-9]|3[01])",
            "$"
        ].join(''))
    }, {
        key: 'validate-email',
        message: 'Please use a valid email address',
        pattern: new RegExp([
            "^",
            "[a-zA-Z0-9._%+-]+",
            "@",
            "[a-zA-Z0-9.-]+",
            "[.][a-zA-Z]{2,4}",
            "$"
        ].join(''))
    }, {
        key: 'validate-integer',
        message: 'Please enter a whole number',
        pattern: new RegExp([
            "^",
            "-?",
            "[0-9]+",
            "$"
        ].join(''))
    }, {
        key: 'validate-integer-negative',
        message: 'Please enter a negative whole number',
        pattern: new RegExp([
            "^",
            "-[0-9]+",
            "$"
        ].join(''))
    }, {
        key: 'validate-integer-positive',
        message: 'Please enter a positive whole number',
        pattern: new RegExp([
            "^",
            "[0-9]+",
            "$"
        ].join(''))
    }];
    
    function Validate(element, options) {
        this.$element = $(element);
        this.options = options;
        
        this.updateMessages();
        this.listen();
    }
    
    Validate.prototype = {
        addFieldError: function (error) {
            var $control_group = this.$input.closest('.control-group'),
                $controls = this.$input.closest('.controls'),
                error_class = this.options.etarget.replace(/[.](.*)/, '$1'),
                error = $('<span>').addClass(error_class).html(this.options.etemplate.replace('{{error}}', error));
            
            $control_group.addClass('error');
            
            $controls.append(error);
        },
        
        getFieldErrors: function () {
            var errors = [];
            
            $.each(this.options.rules, $.proxy(function (index, rule) {
                if (!this.isRuleValid(rule)) throw 'Invalid validate rule';
                
                if (this.$input.data(rule.key) != null && this.hasFieldError(rule)) {
                    errors.push({
                        key: rule.key,
                        message: this.getRuleMessage(rule)
                    });
                }
            }, this));
            
            return errors;
        },
        
        getRule: function (key) {
            var _rule = null;
            
            $.each(this.options.rules, $.proxy(function (index, rule) {
                if (this.isRuleValid(rule) && rule.key == key) return _rule = rule;
            }, this));
            
            return _rule;
        },
        
        getRuleMessage: function (rule) {
            return rule.message.replace('{0}', this.$input.data(rule.key));
        },
        
        getRulePattern: function (rule) {
            if (typeof rule.pattern == 'function') return rule.pattern(this.$input.data(rule.key));
            
            return rule.pattern;
        },
        
        hasFieldError: function (rule) {
            var pattern = this.getRulePattern(rule);
            
            if (!pattern) return false;
            
            return $.trim(this.$input.val()).match(pattern) == null;
        },
        
        isRuleValid: function (rule) {
            return rule != null 
                && typeof rule == 'object' 
                && typeof rule.key == 'string' 
                && typeof rule.message == 'string'
                && (rule.pattern instanceof RegExp || typeof rule.pattern == 'function');
        },
        
        listen: function () {
            this.$element
                .on('focusin.form.validate', 'input', $.proxy(this.focus, this))
                .on('focusout.form.validate', 'input', $.proxy(this.validate, this));
        },
        
        resetField: function () {
            var $control_group = this.$input.closest('.control-group'),
                $controls = this.$input.closest('.controls');
            
            $control_group.removeClass('error');

            $controls.find(this.options.etarget).remove();
        },
        
        updateMessages: function () {
            if (typeof this.options.messages != 'object') return;
            
            $.each(this.options.messages, $.proxy(function (key, message) {
                var rule = this.getRule(key);
                
                if (typeof message == 'string' && rule) rule.message = message;
            }, this));
        },
        
        focus: function (event) {
            this.$input = $(event.currentTarget);
            
            this.resetField();
        },
        
        validate: function (event) {
            this.$input = $(event.currentTarget);
            
            var errors = this.getFieldErrors();
            
            if (errors.length > 0) {
                var error = errors.shift();
                
                // webkit double input focus/blur events work around
                // http://stackoverflow.com/questions/9680518/google-chrome-duplicates-javascript-focus-event/9680979#9680979
                this.resetField();
                
                this.addFieldError(error.message);
                
                this.$input.trigger(error.key);
            }
        }
    };
    
    $.fn.validate = function (options) {
        return this.each(function () {
            var $this = $(this),
                options = $.extend({}, $.fn.validate.defaults, $this.data(), typeof options == 'object' && options),
                data = $this.data('validate');
            
            if (!data) $this.data('validate', (new Validate(this, options)));
        });
    }
    
    $.fn.validate.defaults = {
        etarget: '.validate-error',
        etemplate: '<span class="help-inline"><i class="icon-remove-sign icon-large"></i></span><span class="help-block">{{error}}</span>',
        rules: rules
    };
    
    $('form[data-init="validate"]').validate();
}(window.jQuery, window, document));