var express           = require('express');
var path              = require('path');
var morgan            = require('morgan');
var cookieParser      = require('cookie-parser');
var bodyParser        = require('body-parser');
var http              = require('http')
var log4js            = require('log4js');
var fs                = require('fs');
var q                 = require('q');
var favicon           = require('serve-favicon');

var config            = require('./server-config');

var app = express();
var logger = log4js.getLogger();

function loadDatabase() {
  var deferred = q.defer();

  logger.info("Loading people data...");
  var csv = require('ya-csv');
  var reader = csv.createCsvFileReader('people.csv', {
    columnsFromHeader: true
  });
  var people = new Array();
  var id = 0;

  reader.addListener('end', function() {
    logger.info("Done loading people data.");
    deferred.resolve(people);
  });
  
  reader.addListener('data', function(data) {
    id += 1;
    var person = {
      id:         id,
      first:      data.fname,
      middle:     data.mname,
      last:       data.lname,
      gender:     data.gender,
      ssn:        data.ssn,
      birthDate: new Date(data.dob)
    };
    people.push(person);
  });

  return deferred.promise;
}

function initialize() {
  var deferred = q.defer();

  var people = null;

  function reject(msg) {
    deferred.reject(new Error(msg));
  }

  loadDatabase()
  .then(function(people) {
    deferred.resolve({
      people: people,
      config: config
    })
  },
  reject);

  return deferred.promise;
}

function runApplication(initData) {

  var people = initData.people;
  var config = initData.config;

  logger.info("Starting application with " + people.length + " people.");
  logger.info("Version: " + config.version);

  app.use(bodyParser.json());
  app.use(bodyParser.urlencoded());
  app.use(cookieParser());
  
  // Note: The following static routes also ensure that "/" ends up serving
  // "public/{config.version}/index.html".
  
  app.use(express.static(path.join(__dirname, 'public/' + config.version)));
  app.use(express.static(path.join(__dirname, 'bower_components')));
  app.use(express.static(path.join(__dirname, 'public/')));
  
  app.use(favicon(__dirname + '/public/images/favicon.ico'));

  // Initialize app. logging.
  // See http://stackoverflow.com/questions/23494956/how-to-use-morgan-logger
  var httpLog = morgan({
    'format': 'dev',
    'stream': {
      write: function(str) { logger.info(str.trim()); }
    }
  });
  app.use(httpLog);
  
  // Handlers
  
  app.get('/people/:prefix', function(req, res) {
    var prefix = req.params.prefix.toLowerCase();
    var matches = null;
    if (prefix == '*') {
      matches = people;
    }
    else {
      matches = [];
      people.forEach(function(person) {
        if (person.last.toLowerCase().indexOf(prefix) === 0) {
          matches.push(person);
        }
      });
    }
    res.send(200, {people: matches})
  });
  
  app.get('/person/:id', function(req, res) {
    var person = null;
    var id = parseInt(req.params.id);
    for (var i = 0; i < people.length; i++) {
      if (people[i].id === id) {
        person = people[i];
        break;
      }
    }
    if (person === null) {
      res.send(404, {error: "Person with ID " + id + " not found."});
    }
    else {
      res.send(200, {person: person});
    }
  
  });
  
  app.post('/person/:id', function(req, res) {
    var person = req.body;
    var id = parseInt(req.params.id);
    var found = false;
    for (var i = 0; i < people.length; i++) {
      if (people[i].id === id) {
        people[i].first = person.first;
        people[i].last = person.last;
        people[i].middle = person.middle;
        people[i].gender = person.gender;
        people[i].ssn = person.ssn;
        people[i].birthDate = person.birthDate;
        person = people[i];
        found = true;
        break;
      }
    }
  
    if (found) {
      res.send({person: person})
    }
    else {
      res.send(404, {error: "Person with ID " + id + " not found."});
    }
  });
}

function abort(err) {
  console.log(err.message);
  process.exit(1);
}

initialize().then(runApplication, abort);

module.exports = app;

  
  
