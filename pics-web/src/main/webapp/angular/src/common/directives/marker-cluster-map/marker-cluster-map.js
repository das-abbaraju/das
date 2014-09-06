angular.module('PICS.directives')

.directive('markerClusterMap', function (mapMarkerService, whoAmI, $sce, addCompanyService) {
    return {
        restrict: 'E',
        scope: {
            googleMapConfig: '=',
            markerClustererConfig: '='
        },
        replace: true,
        templateUrl: '/angular/src/common/directives/marker-cluster-map/marker-cluster-map.tpl.html',
        link: function (scope, element, attrs) {
            var googleMapConfig = scope.googleMapConfig,
                map = googleMapConfig.map,
                info_window = $('.info-window'),
                currentInfoWindow = null,
                infoWindowVisible = false,
                selectedMarker = null,
                currentMarkerList = [],
                markerClusterer,
                loader = $(element.children()[2]),
                markerIconBase = '/angular/src/app/company-finder/img/';

            whoAmI.get(function (result) {
                scope.userInfo = result;
            });

            scope.processingAdd = false;

            scope.$emit('loader-ready', loader, scope.markerClustererConfig);

            if (!map || isEmptyObject(map)) {
                map = scope.googleMapConfig.map = createGoogleMap(element, googleMapConfig);
            }

            scope.$watch('markerClustererConfig', function (newConfig) {
                if (!newConfig) return;

                selectedMarker = null;

                refreshMarkerClusterer(map, newConfig);
            }, true);

            scope.safeApply = function(fn) {
                var phase = this.$root.$$phase;
                if(phase == '$apply' || phase == '$digest') {
                    if(fn && (typeof(fn) === 'function')) {
                      fn();
                    }
                } else {
                    this.$apply(fn);
                }
            };

            scope.addCompany = function() {
                scope.processingAdd = true;

                addCompanyService.get({id:scope.location.id}).$promise.then(function () {
                    scope.location.worksForOperator = true;
                    scope.processingAdd = false;
                }).catch(function () {
                    scope.addFailed = true;
                    scope.processingAdd = false;
                });
            };

            google.maps.event.addListener(
                map,
                'click',
                closeInfoWindow()
            );

            google.maps.event.addListener(
                map,
                'center_changed',
                closeInfoWindow()
            );

            google.maps.event.addListener(
                map,
                'zoom_changed',
                closeInfoWindow()
            );

            function initMarkerClusterer(map, config) {
                markerClusterer = createMarkerClusterer(map, config);
            }

            function createMarkerClusterer(map, config) {
                currentMarkerList = createMarkers(map, config);

                mapMarkerService.setMarkers(currentMarkerList);

                return new MarkerClusterer(map, currentMarkerList, config);
            }

            function createGoogleMap($mapOuterContainer, config) {
                var mapContainer = $mapOuterContainer.children()[1];

                $mapOuterContainer.addClass('map-container');

                scope.$emit('map-created', scope.googleMapConfig);

                return new google.maps.Map(mapContainer, config);
            }

            function createMarkers(map, config) {
                var locations = config.locations,
                    markers = [],
                    location_index, marker,
                    redMarkerImage = new google.maps.MarkerImage(
                        config.redMarkerImageUrl,
                        new google.maps.Size(24, 32)
                    ),
                    yellowMarkerImage = new google.maps.MarkerImage(
                        config.yellowMarkerImageUrl,
                        new google.maps.Size(24, 32)
                    ),
                    greenMarkerImage = new google.maps.MarkerImage(
                        config.greenMarkerImageUrl,
                        new google.maps.Size(24, 32)
                    ),
                    grayMarkerImage = new google.maps.MarkerImage(
                        config.grayMarkerImageUrl,
                        new google.maps.Size(24, 32)
                    );

                angular.forEach(locations, function (location, index) {
                    if (location.flagColor == 'Red') {
                        marker = createMarker(map, location, redMarkerImage, index);
                    } else if (location.flagColor == 'Amber') {
                        marker = createMarker(map, location, yellowMarkerImage, index);
                    } else if (location.flagColor == 'Green') {
                        marker = createMarker(map, location, greenMarkerImage, index);
                    } else {
                        marker = createMarker(map, location, grayMarkerImage, index);
                    }

                    markers.push(marker);
                });

                return markers;
            }

            function createMarker(map, location, markerImage, index) {
                var latLng = new google.maps.LatLng(
                    location.coordinates.latitude,
                    location.coordinates.longitude
                );

                var googleMarker = new MarkerWithLabel({
                    map: map,
                    position: latLng,
                    icon: markerImage,
                    labelContent: index + 1,
                    labelInBackground: false,
                    labelAnchor: new google.maps.Point(6, 27),
                    labelClass: 'marker-label',
                    id: index,
                    flagColor: location.flagColor
                });

                google.maps.event.addListener(
                    googleMarker,
                    'click',
                    createMarkerClickHandler(map, googleMarker, location, index)
                );

                google.maps.event.addListener(
                    googleMarker,
                    'mouseover',
                    createMouseOverHandler(googleMarker)
                );

                var mouseoutListener = google.maps.event.addListener(
                    googleMarker,
                    'mouseout',
                    createMouseOutHandler(googleMarker)
                );

                return googleMarker;
            }

            function getOtherTradesLabel(location) {
                var nonPrimaryTrades = location.trades.length - 1;

                if (nonPrimaryTrades == 1) {
                    return nonPrimaryTrades + ' other trade&hellip;';
                } else if (nonPrimaryTrades > 1) {
                    return nonPrimaryTrades + ' other trades&hellip;';
                } else {
                    return '';
                }
            }

            function createMarkerClickHandler(map, googleMarker, location, index) {
                return function () {
                    scope.addFailed = false;

                    scope.location = location;

                    scope.otherTrades = $sce.trustAsHtml(getOtherTradesLabel(location));

                    scope.address = $sce.trustAsHtml(location.formattedAddressBlock.replace('\n', '<br/>'));

                    info_window.css('display', 'block');

                    currentInfoWindow = index;

                    infoWindowVisible = true;

                    if (selectedMarker) {
                        google.maps.event.trigger(currentMarkerList[selectedMarker.id], 'mouseout');
                    }

                    selectedMarker = googleMarker;

                    scope.safeApply();
                };
            }

            function createMouseOverHandler(googleMarker) {
                return function () {
                    googleMarker.labelAnchor = new google.maps.Point(5, 37);
                    googleMarker.label.setAnchor();

                    googleMarker.labelClass = 'marker-label-large';
                    googleMarker.label.setStyles();

                    googleMarker.setIcon(getMarkerIconUrl(googleMarker.flagColor, true));
                };
            }

            function getMarkerIconUrl(flagColor, large) {
                var marker;

                switch (flagColor) {
                    case 'Red':
                        if (large) {
                            marker = 'marker-danger-light-large.png';
                        } else {
                            marker = 'marker-danger-light.png';
                        }
                        break;
                    case 'Amber':
                        if (large) {
                            marker = 'marker-warning-light-large.png';
                        } else {
                            marker = 'marker-warning-light.png';
                        }
                        break;
                    case 'Green':
                        if (large) {
                            marker = 'marker-success-light-large.png';
                        } else {
                            marker = 'marker-success-light.png';
                        }
                        break;
                    default:
                        if (large) {
                            marker = 'marker-generic-large.png';
                        } else {
                            marker = 'marker-generic.png';
                        }
                }

                return markerIconBase + marker;
            }

            function createMouseOutHandler(googleMarker) {
                return function () {
                    if ((currentInfoWindow != googleMarker.id) || (!infoWindowVisible)) {
                        useSmallMarker(googleMarker);
                    }
                };
            }

            function closeInfoWindow() {
                return function () {
                    if (selectedMarker && infoWindowVisible) {
                        info_window.css('display', 'none');
                        useSmallMarker(currentMarkerList[selectedMarker.id]);

                        infoWindowVisible = false;
                    }
                };
            }

            function useSmallMarker(googleMarker) {
                googleMarker.labelAnchor = new google.maps.Point(5, 28);
                googleMarker.label.setAnchor();

                googleMarker.labelClass = 'marker-label';
                googleMarker.label.setStyles();

                googleMarker.setIcon(getMarkerIconUrl(googleMarker.flagColor, false));
            }

            function refreshMarkerClusterer(map, config) {
                if (markerClusterer) {
                    markerClusterer.clearMarkers();
                }

                initMarkerClusterer(map, config);
            }

            function isEmptyObject(obj){
                for(var prop in obj){ return false;}
                return true;
            }
        }
    };
});