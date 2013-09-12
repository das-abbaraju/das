/**
 * Validate
 *
 * A plugin for client side validation for Twitter Bootstrap forms.
 *
 * Twitter Bootstrap:  v2.3.1
 * Plugin: v0.04
 *
 * @author: Carey Hinoki
 * @date: 2013-03-25
 * @updated: 2013-03-26
 */
(function ($, window, document, undefined) {
    "use strict"; // jshint ;_;

    var validators = {
        required: {
            name: 'required',
            type: 'required',
            message: 'Field is required'
        },
        minlength: {
            name: 'minlength',
            type: 'minlength',
            message: 'Field is less than {0} characters'
        },
        maxlength: {
            name: 'maxlength',
            type: 'maxlength',
            message: 'Field is longer than {0} characters'
        },
        compareeq: {
            name: 'compareeq',
            type: 'compareeq',
            message: 'Field does not equal {0}'
        },
        comparelt: {
            name: 'comparelt',
            type: 'comparelt',
            message: 'Field is not less than {0}'
        },
        comparegt: {
            name: 'comparegt',
            type: 'comparegt',
            message: 'Field is not greater than {0}'
        },
        min: {
            name: 'min',
            type: 'min',
            message: 'Field is less than {0}'
        },
        max: {
            name: 'max',
            type: 'max',
            message: 'Field is more than {0}'
        },
        currency: {
            name: 'currency',
            type: 'regex',
            regex: new RegExp([
                '^',
                '(',
                    '[0-9]+',
                    '([.,][0-9]{2})?',
                '|',
                    '[0-9]{1,3}',
                    '([,][0-9]{3})*',
                    '([.][0-9]{2})?',
                '|',
                    '[0-9]{1,3}',
                    '([.][0-9]{3})*',
                    '([,][0-9]{2})?',
                ')',
                '$'
            ].join('')),
            message: 'Field is not a valid amount'
        },
        date: {
            name: 'date',
            type: 'regex',
            regex: new RegExp([
                '^',
                '(19|20)[0-9]{2}',
                '-',
                '(0[1-9]|1[012])',
                '-',
                '(0[1-9]|[12][0-9]|3[01])',
                '$'
            ].join('')),
            message: 'Field is not a valid amount'
        },
        email: {
            name: '',
            type: 'regex',
            regex: new RegExp([
                '^',
                '[a-zA-Z0-9._%+-]+',
                '@',
                '[a-zA-Z0-9.-]+',
                '[.][a-zA-Z]{2,4}',
                '$'
            ].join('')),
            message: 'Field is not a valid email address'
        },
        integer: {
            name: 'integer',
            type: 'regex',
            regex: '^(0|[-]?[1-9]([0-9]+)?)$',
            message: 'Field is not a valid number'
        },
        integerpositive: {
            name: 'integerpositive',
            type: 'regex',
            regex: '^[1-9]([0-9]+)?$',
            message: 'Field is not a positive number'
        },
        integernegative: {
            name: 'integernegative',
            type: 'regex',
            regex: '^[-][1-9]([0-9]+)?$',
            message: 'Field is not a negative number'
        },
        match: {
            name: 'match',
            type: 'match',
            message: 'Field does not match {0}'
        },
        pattern: {
            name: 'pattern',
            type: 'regex',
            message: 'Field does not match pattern {0}'
        }
    };

    var validator_types = {
        compareeq: {
            init: function ($this, name) {
                return { value: $this.data(name) };
            },
            validate: function (value, validator) {
                return value === validator.value;
            }
        },
        comparelt: {
            init: function ($this, name) {
                return { value: $this.data(name) };
            },
            validate: function (value, validator) {
                return value < validator.value;
            }
        },
        comparegt: {
            init: function ($this, name) {
                return { value: $this.data(name) };
            },
            validate: function (value, validator) {
                return value > validator.value;
            }
        },
        max: {
            init: function ($this, name) {
                return { max: $this.data(name) };
            },
            validate: function (value, validator) {
                return !isNaN(parseFloat(validator.max, 10)) && parseFloat(value, 10) <= parseFloat(validator.max, 10);
            }
        },
        min: {
            init: function ($this, name) {
                return { min: $this.data(name) };
            },
            validate: function (value, validator) {
                return !isNaN(parseFloat(validator.min, 10)) && parseFloat(value, 10) >= parseFloat(validator.min, 10);
            }
        },
        maxlength: {
            init: function ($this, name) {
                return { size: $this.data(name) };
            },
            validate: function (value, validator) {
                return !isNaN(parseInt(validator.size, 10)) && value.length <= parseInt(validator.size, 10);
            }
        },
        minlength: {
            init: function ($this, name) {
                return { size: $this.data(name) };
            },
            validate: function (value, validator) {
                return !isNaN(parseInt(validator.size, 10)) && value.length >= parseInt(validator.size, 10);
            }
        },
        match: {
            init: function ($this, name) {
                return { element: $($this.data(name)) };
            },
            validate: function (value, validator) {
                return validator.element instanceof $ && value === validator.element.val();
            },
            message: function (value, validator) {
                return validator.message.replace('{0}', $(value).closest('.control-group').find('label').text());
            },
            force: true
        },
        regex: {
            init: function ($this, name) {
                return { regex: $this.data(name) };
            },
            validate: function (value, validator) {
                return (typeof validator.regex === 'string' ? new RegExp(validator.regex) : validator.regex).test(value);
            }
        },
        required: {
            init: function ($this, name) {
                return false;
            },
            validate: function (value, validator) {
                return value.length > 0;
            },
            force: true
        }
    };

    function Validate(element, options) {
        this.$element = $(element);
        this.options = options;

        this.updateMessages();
        this.listen();
    }

    Validate.prototype = {
        addError: function ($input, error_message) {
            var $control_group = $input.closest('.control-group'),
                $controls = $input.closest('.controls'),
                icon_class = this.options.itarget.replace(/[.](.*)/, '$1'),
                icon = $(this.options.itemplate).addClass(icon_class),
                error_class = this.options.etarget.replace(/[.](.*)/, '$1'),
                error = $(this.options.etemplate.replace('{{error}}', error_message)).addClass(error_class);

            $control_group.addClass('error');

            if (this.options.icon === true) $controls.append(icon);

            $controls.append(error);
        },

        focusin: function (event) {
            this.$input = $(event.currentTarget);

            this.reset(this.$input);
        },

        focusout: function (event) {
            this.$input = $(event.currentTarget);

            var errors = this.getErrors(this.$input),
                error = errors.shift();

            if (error) {
                // webkit double input focus/blur events work around
                // http://stackoverflow.com/questions/9680518/google-chrome-duplicates-javascript-focus-event/9680979#9680979
                this.reset(this.$input);

                this.addError(this.$input, error.message);

                this.$input.trigger(error.name);
            }
        },

        getError: function ($input, name) {
            var validator = this.options.validators[name],
                validator_type = validator && this.options.validator_types[validator.type],
                message;
    
            if (!(validator || validator_type)) return null;
            
            if (typeof validator_type.message === 'function') {
                message = validator_type.message($input.data(name), validator); 
            } else {
                message = validator.message.replace('{0}', $input.data(name));
            }

            return {
                name: name,
                message: message
            };
        },

        getErrors: function ($input) {
            var errors = [];

            $.each($input.data(), $.proxy(function (name, value) {
                if (!this.hasError($input, name)) return;
                
                var error = this.getError($input, name);
                
                if (error) errors.push(error);
            }, this));

            return errors;
        },

        hasError: function ($input, name) {
            var validator = this.options.validators[name],
                validator_type = validator && this.options.validator_types[validator.type];

            if (!(validator || validator_type)) return true;

            var validator = $.extend({}, validator, $input.data(name) !== '' ? validator_type.init($input, name) : false),
                value = $.trim($input.val());
            
            if (value.length === 0 && validator_type.force !== true) return false;
            
            return validator_type.validate(value, validator) === false;
        },

        listen: function () {
            this.$element
                .on('focusin.form.validate', 'input', $.proxy(this.focusin, this))
                .on('focusout.form.validate', 'input', $.proxy(this.focusout, this));
        },

        reset: function ($input) {
            var $control_group = $input.closest('.control-group'),
                $controls = $input.closest('.controls');

            $control_group.removeClass('error');

            if (this.options.icon === true) $controls.find(this.options.itarget).remove();

            $controls.find(this.options.etarget).remove();
        },

        updateMessages: function () {
            if (typeof this.options.messages !== 'object') return;

            $.each(this.options.messages, $.proxy(function (name, message) {
                var validator = this.options.validators[name];

                if (typeof message === 'string' && validator) validator.message = message;
            }, this));
        }
    };

    $.fn.validate = function (options) {
        return this.each(function () {
            var $this = $(this),
                options = $.extend({}, $.fn.validate.defaults, $this.data(), typeof options === 'object' && options),
                data = $this.data('validate');

            if (!data) $this.data('validate', (new Validate(this, options)));
        });
    }

    $.fn.validate.defaults = {
        etarget: '.validate-error',
        etemplate: '<span class="help-block">{{error}}</span>',
        icon: true,
        itarget: '.validate-icon',
        itemplate: '<span class="help-inline"><i class="icon-remove-sign icon-large"></i></span>',
        validators: validators,
        validator_types: validator_types
    };

    $('form[data-init="validate"]').validate();
}(window.jQuery, window, document));