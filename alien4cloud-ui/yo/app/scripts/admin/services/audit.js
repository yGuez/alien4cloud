'use strict';

angular.module('alienUiApp').factory('auditService', ['$resource', function($resource) {

  // faceted search for audit traces
  var auditSearch = $resource('/rest/audit/search', {}, {
    'search': {
      method: 'POST',
      isArray: false,
      headers: {
        'Content-Type': 'application/json; charset=UTF-8'
      }
    }
  });

  return {
    'search': auditSearch.search
  };

}]);
