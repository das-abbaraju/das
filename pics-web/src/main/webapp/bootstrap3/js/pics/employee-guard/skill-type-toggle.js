PICS.define('employee-guard.SkillTypeToggle', {
    methods: (function () {
        function init() {
            $('body').on('change', '.skillType', changeSkillType);
        }

        function getSkillTypeURL($element) {
            var $section = $element.closest('section'),
                url;

            if ($section.length > 0) {
                url = $section.attr('data-url');
            } else {
                url = document.location.href;
            }

            return url;
        }

        function changeSkillType(event) {
            var $element = $(event.target),
                $form = $element.closest('form'),
                url = getSkillTypeURL($element);

            PICS.ajax({
                url: url,
                type: 'GET',
                data: $form.serialize(),
                context: $form,
                success: updateSkillForm
            });
        }

        function updateSkillForm(data) {
            var $form = this;

            $form.replaceWith(data);
            PICS.getClass('employee-guard.BindjQueryElements').tooltips();
            PICS.getClass('employee-guard.BindjQueryElements').select2();
            PICS.getClass('employee-guard.BindjQueryElements').formDisable();
        }

        return {
            init: init
        };

    }())
});