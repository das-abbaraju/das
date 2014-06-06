angular.module('PICS.employeeguard')
.service('EmployeeService', function (EmployeeServiceFactory) {
    var employee = {};

    return {
        getEmployee: function (employeeId) {
            var that = this;

            if (employee.id) {
                // console.log('employee object is present');
                that.setTitle(employee);
                return employee;
            } else {
                // console.log('no employee object');
                var temp = EmployeeServiceFactory.get({id: employeeId}, function(employee_info) {
                    employee = employee_info;
                    that.setEmployee(employee);
                    that.setTitle(employee);
                });
                return temp;
            }
        },
        setEmployee: function (value) {
            // console.log('setting employee');
            // console.log(value);
            employee = value;
        },
        getId: function () {
            return employee.id;
        },
        setId: function(value) {
            employee.id = value;
        },
        getFirstName: function () {
            return employee.firstName;
        },
        setFirstName: function (value) {
            employee.firstName = value;
        },
        getLastName: function () {
            return employee.lastName;
        },
        setLastName: function (value) {
            employee.lastName = value;
        },
        getSiteId: function () {
            return employee.siteId;
        },
        setSiteId: function (value) {
            employee.siteId = value;
        },
        setTitle: function (employee_info) {
            //this gets the first company title from companies list
            if (employee_info.companies) {
                employee.title = employee_info.companies[0].title;
            }
        }
    };
})

.factory('EmployeeServiceFactory', function($resource) {
    return $resource('/employee-guard/operators/:siteId/contractors/employees/:id');
});