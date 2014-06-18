angular.module('PICS.skills')

.controller('operatorSkillListCtrl', function ($scope, OperatorSkillList, WhoAmI) {
    $scope.skills = OperatorSkillList.query();
    $scope.user = WhoAmI.get();

    $scope.user.$promise.then(function(user) {
        if (user.type.toLowerCase() === 'corporate') {
            $scope.orderByField = 'site';
        }
    });
});