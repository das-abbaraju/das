describe('A Role List', function() {
    var scope;

    var groups_url = '/employee-guard/groups';
    var groups_url_dev = '/employee-guard/src/app/employeeguard/groups/json/group_list.json';
    var whoami_url = '/employee-guard/who-am-i';

    var groups = [{
        "id": 4,
        "name": "Medical",
        "skills": [{
                "id": 22,
                "name": "Professional Safety Training"
            }
        ],
        "employees": [{
            "id": 3,
            "firstName": "Bob",
            "lastName": "Jones",
            "companies":[
              {
                 "id":54578,
                 "title":"Demo Op",
                 "name":"ACME Co."
              }
            ]
        }, {
            "id": 23,
            "firstName": "Jim",
            "lastName": "Johnson",
            "companies":[]
        }]
    }];

    var contractor_user = {
        "userId":116680,
        "accountId":55654,
        "name":"Lurleen Lumpkin",
        "type":"CONTRACTOR"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $httpBackend) {
        scope = $rootScope.$new();
        $controller("groupListCtrl", {
            $scope: scope
        });

        $httpBackend.when('GET', groups_url).respond(groups);
        $httpBackend.when('GET', groups_url_dev).respond(groups);
        $httpBackend.when('GET', whoami_url).respond(contractor_user);
        $httpBackend.flush();
    }));

    it("should have groups", function() {
        expect(scope.groups).toBeDefined();
    });

    it("should have a user object", function() {
        expect(scope.userType).toBeDefined();
    });
});