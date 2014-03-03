angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, $filter, EmployeeSkills, Employee) {
    var subview = 'all',
        employee = EmployeeSkills.get(),
        model;

    function onSuccess(result) {
        model = new Employee(result);

        $scope.projectSkills = model.getAllProjectSkills();
        $scope.projectRoles = model.getAllProjectRoles();

        $scope.highlightedStatus = $scope.employee.status;
    }

    function onError(error) {
        console.log(error);
    }

    function changeSubView(template_name, subview_name) {
        $scope.subview = template_name;
        $scope.subview_name = subview_name;

        switch (template_name) {
            case 'project':
                $scope.currentProject = model.getProjectByName(subview_name);
                $scope.updateHighlightedStatus($scope.currentProject.status);
                break;
            case 'role':
                $scope.currentRole = model.getRoleByName(subview_name);
                $scope.updateHighlightedStatus($scope.currentRole.status);
                break;
            case 'all':
                $scope.updateHighlightedStatus($scope.employee.status);
                break;
            default:
                break;
        }
    }

    function updateHighlightedStatus(status) {
        $scope.highlightedStatus = status;        
    }

    employee.$promise.then(onSuccess, onError);

    angular.extend($scope, {
        subview: subview,
        employee: employee,
        onSuccess: onSuccess,
        onError: onError,
        changeSubView: changeSubView,
        updateHighlightedStatus: updateHighlightedStatus
    });
});