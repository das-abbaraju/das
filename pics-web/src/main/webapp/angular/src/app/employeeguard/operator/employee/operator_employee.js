angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, $filter, EmployeeSkills, Model) {
    var model;

    $scope.subview = 'all';

    $scope.employee = EmployeeSkills.get();

    $scope.employee.$promise.then(function (result) {
        model = new Model(result);

        $scope.projectSkills = model.getAllProjectSkills();
        $scope.projectRoles = model.getAllProjectRoles();

        $scope.highlightedStatus = $scope.employee.overallStatus;
    }, function(error) {
        console.log(error);
    });

    $scope.changeSubView = function (template_name, subview_name) {
        $scope.subview = template_name;
        $scope.subview_name = subview_name;
        console.log('model in ctrl');
        console.log(model);
        switch(template_name) {
            case 'project':
                $scope.currentProject = model.getProjectByName(subview_name, $scope.employee.projects);
                $scope.updateHighlightedStatus($scope.currentProject.status);
                break;
            case 'role':
                $scope.currentRole = model.getRoleByName(subview_name, $scope.employee.roles);
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