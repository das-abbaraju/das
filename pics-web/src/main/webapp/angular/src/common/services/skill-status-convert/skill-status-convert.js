angular.module('PICS.utility')

.factory('SkillStatusConverter', function() {
    return {
        convert: function(status) {
            var classname = '';

            status = status.toLowerCase();

            if (status === 'expired') {
                classname = "danger";
            } else if (status === 'expiring') {
                classname = 'warning';
            } else if (status === 'pending') {
                classname = 'success';
            } else if (status === 'complete') {
                classname = 'success';
            }

            return classname;
        }
    };
});