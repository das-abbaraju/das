describe('An Employee', function() {
    var scope, $http, $httpBackend, httpMock, employee, location;

    var employee_info_url = '/employee-guard/employee/summary/employee-info';
    var employee_assignments_url = '/employee-guard/employee/summary/assignments';
    var employee_assignments_dev_url = '/angular/json/employee/assignments.json';

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $httpBackend, $location) {
        employee = {
            "id": 1,
            "firstName": "John",
            "lastName": "Smith",
            "slug": "PBANDJ",
            "email": "thelennyleonard@gmail.com"
        };

        location = $location;

        //To capture assignment call only since assignments tested in resource spec
        $httpBackend.when('GET', employee_assignments_url).respond('');
        $httpBackend.when('GET', employee_assignments_dev_url).respond('');

        scope = $rootScope.$new();
        $controller("employeeDashboardCtrl", {
            $scope: scope
        });

        httpMock = $httpBackend;
    }));

    afterEach(function() {
        httpMock.verifyNoOutstandingExpectation();
        httpMock.verifyNoOutstandingRequest();
    });

    it('should use the email as a slug if no one defined', function() {
        var employee = {
            "id": 1,
            "firstName": "John",
            "lastName": "Smith",
            "email": "thelennyleonard@gmail.com"
        };

        httpMock.when('GET', employee_info_url).respond(employee);
        httpMock.flush();

        expect(scope.employee.slug).toBeDefined();
        expect(scope.employee.slug).toEqual(scope.employee.email);
    });

    describe('Viewing an Assigned Skill', function() {
        beforeEach(function() {
            httpMock.when('GET', employee_info_url).respond(employee);
            httpMock.flush();
        });

        it('should go to the employee skills site page when selecting a site assignment', function() {
            var assignment = {
                "name": "BASF Houston Texas",
                "roles": 2,
                "status": "Expired"
            };

            scope.viewAssignedSkills(assignment);

            expect(location.path()).toEqual('/employee-guard/employee/skills/sites/basf-houston-texas');
        });

        it('should go to the employee skills site page when selecting a site assignment', function() {
            var assignment = {
                "name": "Ninja Dojo",
                "status": "Expiring",
                "site": "BASF Houston Texas"
            };

            scope.viewAssignedSkills(assignment);

            expect(location.path()).toEqual('/employee-guard/employee/skills/sites/basf-houston-texas/projects/ninja-dojo');
        });
    });
});