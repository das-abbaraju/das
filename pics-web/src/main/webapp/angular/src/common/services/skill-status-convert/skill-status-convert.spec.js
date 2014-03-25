describe('Skill Status Converter', function() {
    var skillConvert;

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function(SkillStatusConverter) {
        skillConvert = SkillStatusConverter;
    }));

    it('should return the correct css class based on a skill status', function() {
        expect(skillConvert.convert('expired')).toEqual('danger');
        expect(skillConvert.convert('expiring')).toEqual('warning');
        expect(skillConvert.convert('pending')).toEqual('success');
        expect(skillConvert.convert('complete')).toEqual('success');
    });
});