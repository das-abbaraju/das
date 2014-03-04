// Shim for HTML5 placeholder attribute
PICS.define('widget.Placeholder', {
    methods: (function () {
        function init() {
          if (placeholderSupported()) {
            return;
          }

          $('*[placeholder]').each(insertPlaceholder);

          $(document).on({
            focus: removePlaceholder,
            blur: insertPlaceholder
          }, "input[placeholder]");
        }

        function placeholderSupported() {
          var $input = $('<input>');

          return ('placeholder' in $input[0]);
        }

        function removePlaceholder() {
          var $input = $(this);

          if ($input.val() === $input.attr('placeholder')) {
            $input.val('');
          }
        }

        function insertPlaceholder() {
          var $input = $(this);

          if ($input.val() === '' || $input.val() === input.attr('placeholder')) {
            $input.val($input.attr('placeholder'));
          }
        }

        return {
            init: init,
            placeholderSupported: placeholderSupported
        };
    }())
});