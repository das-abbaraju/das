describe('translationValues filter', function () {
    var $filter;

    beforeEach(angular.mock.module('PICS.translations'));

    beforeEach(inject(function(_$filter_) {
        $filter = _$filter_;
    }));

    it('should replace parameters in a translation value with provided literals', function () {
        var translationValuesFilter = $filter('translationValues'),
            result = translationValuesFilter('My {1} value for translation #{2}', ['translation', '1']);

        expect(result).toEqual('My translation value for translation #1');
    });
});