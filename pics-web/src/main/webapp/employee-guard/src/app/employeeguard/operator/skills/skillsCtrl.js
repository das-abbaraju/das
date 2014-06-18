angular.module('PICS.skills')

.controller('operatorSkillListCtrl', function ($scope, OperatorSkillList, $filter, WhoAmI) {
    $scope.user = WhoAmI.get();

    $scope.user.$promise.then(function(user) {
        if (user.type.toLowerCase() === 'corporate') {
            $scope.orderByField = 'skill';
        }
    });

    OperatorSkillList.query(function (skills) {
        $scope.skills = skills;
        $scope.requiredSkills = $filter('filter')(skills, { isRequiredSkill: true });
    });
});