angular.module('PICS.employeeguard.skills')

.factory('SkillStatus', function() {
    return {
        getClassNameFromStatus: function(status) {
            if (!status) {
                return;
            }

            var classname = '';

            status = status.toLowerCase();

            switch (status) {
                case 'completed':
                    classname = 'success';
                    break;
                case 'pending':
                    classname = 'success';
                    break;
                case 'expiring':
                    classname = 'warning';
                    break;
                case 'expired':
                    classname = 'danger';
                    break;
                default:
                    break;
            }

            return classname;
        }
    };
});