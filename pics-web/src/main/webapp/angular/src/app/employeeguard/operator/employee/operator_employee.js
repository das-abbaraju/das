angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, $filter, EmployeeSkills, Model) {
    $scope.subview = 'all';

    $scope.employee = EmployeeSkills.get();

    $scope.employee.$promise.then(function (result) {
        $scope.model = new Model(result);

        $scope.projectSkills = $scope.model.getAllProjectSkills();
        console.log($scope.projectSkills);
        $scope.projectRoles = $scope.model.getAllProjectRoles();

        $scope.highlightedStatus = $scope.employee.overallStatus;
    }, function(error) {
        console.log(error);
    });

    $scope.changeSubView = function (template_name, subview_name) {
        $scope.subview = template_name;
        $scope.subview_name = subview_name;

        switch(template_name) {
            case 'project':
                $scope.currentProject = $scope.model.getProjectByName(subview_name);
                $scope.updateHighlightedStatus($scope.currentProject.status);
                break;
            case 'role':
                $scope.currentRole = $scope.model.getRoleByName(subview_name);
                $scope.updateHighlightedStatus($scope.currentRole.status);
                break;
            case 'all':
                $scope.updateHighlightedStatus($scope.employee.overallStatus);
                break;
            default: break;
        }
    };

    $scope.updateHighlightedStatus = function (status) {
        $scope.highlightedStatus = status;
    };
});