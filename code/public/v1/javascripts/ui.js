/**
 * Pure jQuery version of our application.
 */
$(document).ready(function() {
  // start-excerpt warning
  var warning = {
    show: function(msg) {
      $("#warning-message").text(msg);
      $("#warning").show();
    },
    hide: function() {
      $("#warning").hide();
    }
  };

  $("#warning-close-button").click(function(e) {
    e.preventDefault();
    warning.hide();
  });
  // end-excerpt warning

  // start-excerpt results-box
  function clearSearchResults() {
    $("#output").text("");
  }

  function setSearchResults(html) {
    clearSearchResults();
    $("#output").append(html);
  }
  // end-excerpt results-box

  // start-excerpt button
  $("#search-button").click(function(e) {
    e.preventDefault();
    warning.hide();
    clearSearchResults();
    var prefix = $("#search-input").val();
    var realPrefix = prefix.trim();
    if (realPrefix.length === 0) {
      warning.show("Please enter a search term.");
    }
    else if (realPrefix.length < 2) {
      warning.show("You must enter at least two characters.");
    }
    else {
      search(realPrefix);
    }
  });
  // end-excerpt button

  // start-excerpt search
  function search(prefix) {
    var url = "/people/" + prefix.toLowerCase();
    $.get(url, processSearchResults);
    // $.get calls the supplied function on success, passing it the JSON
    // results converted to Javascript.
  }
  // end-excerpt search

  // start-excerpt process-results
  function processSearchResults(data) {
    clearSearchResults();

    var templateTable = $("#table-template").clone();
    var templateRow = templateTable.find("#template-row");
    templateTable.attr("id", "");
    templateRow.attr("id", "");
    for (var i = 0; i < data.people.length; i++) {
      var newRow = templateRow.clone();
      var person = data.people[i];
      newRow.find("td.name").text(person.first + " " + person.last);
      newRow.find("td.SSN").text(person.ssn);
      newRow.find("td.gender").text(person.gender);
      var birthDate = moment(person.birthDate);
      newRow.find("td.DOB").text(birthDate.format("DD MMM, YYYY"));

      templateTable.find("tbody").append(newRow);
    }

    templateRow.remove();
    templateTable.show();
    setSearchResults(templateTable);
  }
  // end-excerpt process-results
});
