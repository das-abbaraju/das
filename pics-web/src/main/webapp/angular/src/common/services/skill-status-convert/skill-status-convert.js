angular.module('PICS.utility')

.factory('SkillStatusConverter', function() {
    return {
        convert: function(status) {
            var classname = '';

            if (status === 'Expired') {
                classname = "danger";
            } else if (status === 'Expiring') {
                classname = 'warning';
            } else if (status === 'Pending') {
                classname = 'success';
            } else if (status === 'Complete') {
                classname = 'success';
            }

            return classname;
        }
    };
});