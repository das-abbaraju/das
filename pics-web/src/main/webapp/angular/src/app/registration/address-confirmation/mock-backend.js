angular.module('PICS.registration.mock-backend', [])

.run(function ($httpBackend) {
    $httpBackend.whenGET('/address').respond(function() {
        var response = {
            strikeIronAddressFound: false,
            input: {
                postalCode: '92617',
                country: 'United States',
                countryIso: 'US'
            },
            output: {
                buildingName: 'STE 100',
                streetNumber: '17701',
                streetName: 'Cowan',
                locality: 'Irvine',
                postalCode: '92614-6061',
                province: 'CA',
                country: 'United States',
                countryIso: 'US',
                formattedAddress: '17701 Cowan STE 100\nIrvine, CA 92614-6061\nUnited States'
            }
        };

        return [
            200, 
            angular.toJson(response)
        ];
    });

    // $httpBackend.whenGET('/phoneNumber').respond(function() {
    //     var response = {
    //         number: '1-877-725-3022',
    //         country: 'United States'
    //     };

    //     return [
    //         200, 
    //         angular.toJson(response)
    //     ];
    // });

    // $httpBackend.whenGET('/mibewBaseUrl').respond(function() {
    //     var response = {
    //         mibewBaseUrl: 'https://chat.picsorganizer.com/client.php?locale=en&style=PICS'
    //     };

    //     return [
    //         200,
    //         angular.toJson(response)
    //     ];
    // });

    // $httpBackend.whenPOST(/accounts/).respond(function() {
    //     var response = {
    //         'registrationForm.addressBlob': [
    //             'Requires Manual Confirmation'
    //             // 'Address Not Found'
    //         ]
    //     };

    //     return [
    //         406,
    //         angular.toJson(response)
    //     ];
    // });

    $httpBackend.whenGET(/tpl/).passThrough();
    $httpBackend.whenGET(/time-zones/).passThrough();
    $httpBackend.whenGET(/countries/).passThrough();
    $httpBackend.whenGET(/languages/).passThrough();
    $httpBackend.whenGET(/dialects/).passThrough();
    $httpBackend.whenGET(/tax-id-info/).passThrough();
    $httpBackend.whenPOST(/accounts/).passThrough();
    $httpBackend.whenPOST(/registration\/validation/).passThrough();
    $httpBackend.whenGET(/sales-phone/).passThrough();
    $httpBackend.whenGET(/mibew-base-url/).passThrough();
    $httpBackend.whenPOST(/translations/).passThrough();
    $httpBackend.whenGET(/i18n/).passThrough();
});