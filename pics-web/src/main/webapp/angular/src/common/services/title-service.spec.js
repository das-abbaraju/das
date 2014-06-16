describe('titleService', function () {
    beforeEach(angular.mock.module('PICS.services', function (titleServiceProvider) {
        titleServiceProvider.setPrefix('PICS');
    }));

    var $rootScope, titleService;

    beforeEach(inject(function(_$rootScope_, _$document_, _titleService_) {
        $rootScope = _$rootScope_;
        $document = _$document_;
        titleService = _titleService_;
    }));

    function setUp() {
        titleService.init();

        $rootScope.$broadcast('$routeChangeStart', {
            $$route: {
                title: 'test.key'
            }
        });

         $rootScope.text = {
            'test.key': 'test title'
        };

        $rootScope.$digest();
    }

    it('should update the document title when the title translation value changes', function () {
        setUp();

        expect($document[0].title).toEqual('PICS - test title');
    });

    it('should allow the setting of the title prefix', function () {
        setUp();

        expect($document[0].title).toEqual('PICS - test title');
    });
});