angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, $filter, EmployeeSkills, Employee) {
    var model;

    $scope.subview = 'all';

    $scope.employee = EmployeeSkills.get();

    $scope.onSuccess = function (result) {
        model = new Employee(result);

        $scope.projectSkills = model.getAllProjectSkills();
        $scope.projectRoles = model.getAllProjectRoles();

        $scope.highlightedStatus = $scope.employee.overallStatus;
    };

    $scope.onError = function (error) {
        console.log(error);
    };

    $scope.employee.$promise.then($scope.onSuccess, $scope.onError);

    $scope.changeSubView = function (template_name, subview_name) {
        $scope.subview = template_name;
        $scope.subview_name = subview_name;

        switch(template_name) {
            case 'project':
                $scope.currentProject = model.getProjectByName(subview_name);
                $scope.updateHighlightedStatus($scope.currentProject.status);
                break;
            case 'role':
                $scope.currentRole = model.getRoleByName(subview_name);
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