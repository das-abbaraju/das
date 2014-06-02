describe('Employee Assignments', function() {
    var scope, $http, $httpBackend, httpMock, assignments;

    var employee_assignments_url = '/employee-guard/employee/summary/assignments';
    var employee_assignments_dev_url = '/employee-guard/json/employee/assignments.json';

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $http, $httpBackend, $routeParams, EmployeeAssignment) {

        assignments = [{
            "name": "BASF Houston Texas",
            "roles": 2,
            "status": "Expired"
        }, {
            "name": "Dynamic Reporting",
            "status": "Expired",
            "site": "BASF Houston Texas"
        }, {
            "name": "Ninja Dojo",
            "status": "Expiring",
            "site": "BASF Houston Texas"
        }, {
            "name": "BP Oil",
            "status": "Complete",
            "site": "BASF Raleigh"
        }, {
            "name": "PICS",
            "status": "Expired",
            "groups": 2}
        ];

        //Backend definition common for all tests
        $httpBackend.when('GET', employee_assignments_url).respond(assignments);
        $httpBackend.when('GET', employee_assignments_dev_url).respond(assignments);

        scope = $rootScope.$new();

        httpMock = $httpBackend;

        scope.assignments = EmployeeAssignment.query();

        httpMock.flush();
    }));

    afterEach(function() {
        httpMock.verifyNoOutstandingExpectation();
        httpMock.verifyNoOutstandingRequest();
    });

    it('should be an array', function() {
        expect(angular.isArray(scope.assignments)).toBeTruthy();
    });

    it('should have a name', function() {
        expect(scope.assignments[0].name).toBeDefined();
    });

    it('should have a status', function() {
        expect(scope.assignments[0].status).toBeDefined();
    });
});