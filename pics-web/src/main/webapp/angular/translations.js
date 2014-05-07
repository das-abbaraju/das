const http  = require('http'),
      fs = require('fs');
      
http.createServer(function (request, response) {
    switch (request.method) {
        case 'OPTIONS':
            doOptions(request, response);
            break;
        case 'POST':
            doPost(request, response);
            break;
        default:
            rejectRequestMethod(request, response);
    }
}).listen(8081);

console.log('Now listening on port 8081...');

function doOptions(request, response) {
    response.setHeader('Access-Control-Allow-Origin', '*');
    response.setHeader('Access-Control-Allow-Methods', 'POST');
    response.setHeader('Access-Control-Allow-Headers', 'Content-Type');
    response.writeHead(200);
    response.end();
}

function doPost(request, response) {
    var dataChunks = [];

    getPostBody(request, function (data) {
        var newKeyValuePair = data;

        getRoutePathToTranslationKeys(function (routePathToTranslationKeys) {
            updateRoutePathToTranslationKeys(routePathToTranslationKeys, newKeyValuePair);
            updateTranslationKeysFile(routePathToTranslationKeys);

            sendCorsEmptySuccess(response);
        });
    });
}

function getPostBody(request, callback) {
    var dataChunks = [];

    request.on('data', function (chunk) {
        dataChunks.push(chunk.toString());
    });

    request.on('end', function () {
        var json = dataChunks.join(''),
            data = JSON.parse(json);

        callback(data);
    });
}

function getRoutePathToTranslationKeys(callback) {
    fs.readFile('src/common/translations/translationKeys.js', function (error, data) {
        var dataString, jsonStart, json;

        if (error) {
            throw error;
        }

        dataString = data.toString();
        jsonStart = dataString.indexOf('{');
        json = dataString.substring(jsonStart, dataString.length - 2);

        callback(json ? JSON.parse(json) : {});
    });
}

function updateRoutePathToTranslationKeys(routePathToTranslationKeys, newKeyValuePair) {
    var routePath;

    for (routePath in newKeyValuePair) {
        if (routePathToTranslationKeys[routePath]) {
            delete routePathToTranslationKeys[routePath];
        }

        routePathToTranslationKeys[routePath] = newKeyValuePair[routePath];
    }
}

function updateTranslationKeysFile(routePathToTranslationKeys) {
    var fileContents = [
        'angular.module("PICS.translations")',
        '.value("routePathToTranslationKeys",',
            JSON.stringify(routePathToTranslationKeys),
        ');'
    ].join('');

    fs.writeFile('src/common/translations/translationKeys.js', fileContents);
}

function sendCorsEmptySuccess(response) {
    response.setHeader('Access-Control-Allow-Origin', '*');
    response.writeHead(200);
    response.end();
}

function rejectRequest(request, response) {
    response.writeHead(4056);
    response.end();
}