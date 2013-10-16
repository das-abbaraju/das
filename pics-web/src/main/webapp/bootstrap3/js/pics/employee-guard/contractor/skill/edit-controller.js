// (function ($) {
//     PICS.define('employe-guard.contractor.skill.EditController', {
//         methods: {
//             init: function () {

//                 return;
//                 var $employee_guard_contractor_skill_edit_page = $('#employee_guard_contractor_skill_edit_page'),
//                     $employee_guard_contractor_skill_update_page = $('#employee_guard_contractor_skill_update_page');

//                 if ($employee_guard_contractor_skill_edit_page.length > 0) {
//                     $employee_guard_contractor_skill_edit_page
//                         .on('change', '#contractor_skill_edit_type', $.proxy(this.onTypeChange, this))
//                         .on('click', '#contractor_skill_edit_company_required', this.onRequiredForAllEmployeesClick);
//                 } else if ($employee_guard_contractor_skill_update_page.length > 0) {
//                     $employee_guard_contractor_skill_update_page
//                         .on('change', '#contractor_skill_edit_type', $.proxy(this.onTypeChange, this))
//                         .on('click', '#contractor_skill_edit_company_required', this.onRequiredForAllEmployeesClick);
//                 }


//             },

//             onTypeChange: function (event) {
//                 log('on change')
//                 var $element = $(event.currentTarget),
//                     $form = $element.closest('form');

//                 PICS.ajax({
//                     url: document.location.href,
//                     type: 'GET',
//                     data: $form.serialize(),
//                     success: function (data, textStatus, jqXHR) {
//                         $form.replaceWith(data);
//                     }
//                 });
//             },

//             onRequiredForAllEmployeesClick: function (event) {
//                 var $element = $(event.currentTarget),
//                     $contractor_skill_edit_roles = $('#contractor_skill_edit_roles');

//                 if ($element.is(':checked')) {
//                     $contractor_skill_edit_roles.attr('disabled', true);
//                 } else {
//                     $contractor_skill_edit_roles.attr('disabled', null);
//                 }
//             }
//         }
//     });
// }(jQuery));