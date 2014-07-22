describe('Employee Service', function() {
    var scope, EmployeeService;

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($httpBackend, _EmployeeService_) {
        EmployeeService = _EmployeeService_;
    }));

    it("should get an employee if none exists", function() {
        spyOn(EmployeeService, "fetchEmployee");

        EmployeeService.getEmployee();

        expect(EmployeeService.fetchEmployee).toHaveBeenCalled();
    });

    it("should force reloading employee object", function() {
        spyOn(EmployeeService, "fetchEmployee");

        EmployeeService.getEmployee('', true);

        expect(EmployeeService.fetchEmployee).toHaveBeenCalled();
    });

    describe('Employee data get and set', function() {
        beforeEach(function () {
            var employee = {
                "id":5,
                "image":"/employee-guard/employee/contractor/54578/employee-photo/5",
                "status":"Expired",
                "firstName":"Carl",
                "lastName":"Carlson",
                "companies":[
                  {
                     "id":54578,
                     "name":"ACME Co.",
                     "title":"Safety Coordinator"
                  }
            ]};

            EmployeeService.setEmployee(employee);
        });

        it("should return existing employee object", function() {
            var test_employee = {
                "id":5,
                "image":"/employee-guard/employee/contractor/54578/employee-photo/5",
                "status":"Expired",
                "firstName":"Carl",
                "lastName":"Carlson",
                "companies":[
                  {
                     "id":54578,
                     "name":"ACME Co.",
                     "title":"Safety Coordinator"
                  }
            ]};

            expect(EmployeeService.getEmployee()).toEqual(test_employee);
        });

        it("should return the id of an employee", function() {
            expect(EmployeeService.getId()).toEqual(5);
        });

        it("should set the id of an employee", function() {
            EmployeeService.setId(26);

            expect(EmployeeService.getId()).toEqual(26);
        });

        it("should return the first name of an employee", function() {
            expect(EmployeeService.getFirstName()).toEqual('Carl');
        });

        it("should set the first name an employee", function() {
            EmployeeService.setFirstName('Juniper');

            expect(EmployeeService.getFirstName()).toEqual('Juniper');
        });

        it("should return the last name of an employee", function() {
            expect(EmployeeService.getLastName()).toEqual('Carlson');
        });

        it("should set the last name an employee", function() {
            EmployeeService.setLastName('Burkin');

            expect(EmployeeService.getLastName()).toEqual('Burkin');
        });
    });
});