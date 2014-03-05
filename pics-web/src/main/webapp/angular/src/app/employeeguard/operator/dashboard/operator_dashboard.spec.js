describe('The Operator Dashboard', function() {
    var scope, $http, $httpBackend;

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $http, $httpBackend, $routeParams) {
        var result = {
            "id": 1,
            "employees": 852,
            "completed": 714,
            "pending": 0,
            "expiring": 24,
            "expired": 114,
            "projects": [{
                    "name": "Test Project",
                    "startDate": "01-01-2013",
                    "endDate": "01-02-2015",
                    "location": "Main Building",
                    "completed": 3,
                    "pending": 2,
                    "expiring": 4,
                    "expired": 2
                }, {
                    "name": "Red Dwarf",
                    "startDate": "02-28-2013",
                    "endDate": "03-02-2015",
                    "location": "Offsite",
                    "completed": 1078,
                    "pending": 2,
                    "expiring": 345,
                    "expired": 992
                }]
            };

        $routeParams.id = Math.floor((Math.random()*1000)+1);

        //This needs to come first!!
        $httpBackend.when('GET', /\/employee-guard\/operators\/summary/).respond(result);

        scope = $rootScope.$new();
        $controller("operatorDashboardCtrl", {
            $scope: scope
        });

        //This needs to come after controller is loaded
        $httpBackend.flush();

        scope.site = result;

    }));

    it('should calculate a percentage', function() {
        expect(scope.calculateStatusPercentage(5, 10)).toEqual(50);
    });

    describe('call to getProjectStatus', function() {
        it('should return a progress bar object', function() {
            var project = {
                "name": "Red Dwarf",
                "startDate": "02-28-2013",
                "endDate": "03-02-2015",
                "location": "Offsite",
                "completed": 18,
                "pending": 2,
                "expiring": 40,
                "expired": 20
            };

            var progress_bar = {
                success: {
                    amount: 20,
                    width: 25
                },
                warning: {
                    amount: 40,
                    width: 50
                },
                danger: {
                    amount: 20,
                    width: 25
                }
            };

            expect(scope.getProjectStatus(project)).toEqual(progress_bar);
        });
    });
});