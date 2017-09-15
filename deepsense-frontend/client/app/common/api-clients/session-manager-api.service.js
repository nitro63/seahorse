'use strict';

/* @ngInject */
function SessionManagerApi($http, config) {

  const service = this;

  service.downloadSessions = downloadSessions;
  service.downloadSessionByWorkflowId = downloadSessionByWorkflowId;
  service.deleteSessionById = deleteSessionById;
  service.startSession = startSession;

  const URL = config.sessionApiPort ?
    `${config.apiHost}:${config.sessionApiPort}/${config.urlApiVersion}/sessions` :
    `${config.apiHost}/${config.urlApiVersion}/sessions`;

  function downloadSessions() {
    return $http.get(URL).then(function processResult(result) {
      console.log('SessionManagerApi downloadSessions result', result);
      return result.data.sessions;
    });
  }

  function downloadSessionByWorkflowId(workflowId) {
    return $http.get(`${URL}/${workflowId}`).then(function processResult(result) {
      console.log('SessionManagerApi downloadSessionById result', result);
      return result.data;
    });
  }

  function deleteSessionById(workflowId) {
    return $http.delete(`${URL}/${workflowId}`).then(function processResult(result) {
      return result;
    });
  }

  function startSession(config) {
    return $http.post(URL, config).then(function processResult(result) {
      return result;
    });
  }

  return service;

}

exports.inject = function(module) {
  module.service('SessionManagerApi', SessionManagerApi);
};
