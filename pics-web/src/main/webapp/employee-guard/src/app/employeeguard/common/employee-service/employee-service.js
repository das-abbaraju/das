angular.module('PICS.employeeguard')
.factory('EmployeeService', function (EmployeeServiceFactory) {
    var factory = {},
        employee = {};

    factory.getEmployee = function(employeeId, forceReload) {
        if (forceReload || !employee.id) {
            return factory.fetchEmployee(employeeId);
        } else {
            return employee;
        }
    };

    factory.setEmployee = function(value) {
        employee = value;
    };

    factory.getId = function() {
            return employee.id;
    };

    factory.setId = function(value) {
        employee.id = value;
    };

    factory.getFirstName = function() {
        return employee.firstName;
    };

    factory.setFirstName = function(value) {
        employee.firstName = value;
    };

    factory.getLastName = function() {
        return employee.lastName;
    };

    factory.setLastName = function(value) {
        employee.lastName = value;
    };

    factory.fetchEmployee = function(employeeId) {
        return EmployeeServiceFactory.get({id: employeeId}, function(employee_info) {
            factory.setEmployee(employee_info);
        });
    };

    return factory;
})

.factory('EmployeeServiceFactory', function($resource) {
    return $resource('/employee-guard/operators/:siteId/contractors/employees/:id');
});