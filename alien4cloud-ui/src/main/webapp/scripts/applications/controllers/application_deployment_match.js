define(function(require) {
  'use strict';

  var modules = require('modules');
  var states = require('states');
  var _ = require('lodash');
  var angular = require('angular');
  require('scripts/orchestrators/controllers/orchestrator_location_resource_template');
  require('scripts/orchestrators/directives/orchestrator_location_resource_template');
  require('scripts/applications/services/deployment_topology_processor.js');

  states.state('applications.detail.deployment.match', {
    url: '/match',
    templateUrl: 'views/applications/application_deployment_match.html',
    controller: 'ApplicationDeploymentMatchCtrl',
    menu: {
      id: 'am.applications.detail.deployment.match',
      state: 'applications.detail.deployment.match',
      key: 'APPLICATIONS.DEPLOYMENT.MATCHING',
      icon: 'fa fa-exchange',
      roles: ['APPLICATION_MANAGER', 'APPLICATION_DEPLOYER'],
      priority: 200,
      step: {
        nextStepId: 'am.applications.detail.deployment.input',
        taskCodes: ['NODE_NOT_SUBSTITUTED', 'IMPLEMENT', 'REPLACE']
      }
    }
  });

  modules.get('a4c-applications').controller('ApplicationDeploymentMatchCtrl',
    ['$scope', 'nodeTemplateService', 'deploymentTopologyServices',
      function($scope, nodeTemplateService, deploymentTopologyServices) {
        $scope.getIcon = function(template, templateName) {
          var templateType;
          if (_.undefined(template)) {
            var templateTypeName = $scope.deploymentContext.deploymentTopologyDTO.topology.nodeTemplates[templateName].type;
            templateType = $scope.deploymentContext.deploymentTopologyDTO.nodeTypes[templateTypeName];
          } else if (!_.isEmpty($scope.deploymentContext.deploymentTopologyDTO.availableSubstitutions.substitutionTypes.nodeTypes)) {
            templateType = $scope.deploymentContext.deploymentTopologyDTO.availableSubstitutions.substitutionTypes.nodeTypes[template.template.type];
          }
          if (!_.isEmpty(templateType)) {
            return nodeTemplateService.getNodeTypeIcon(templateType);
          }
        };

        $scope.getSubstitutedTemplate = function(nodeName) {
          return $scope.deploymentContext.deploymentTopologyDTO.topology.substitutedNodes[nodeName];
        };

        $scope.selectTemplate = function(nodeName, template) {
          $scope.selectedNodeName = nodeName;
          var substitutedNode = $scope.getSubstitutedTemplate(nodeName);
          if (substitutedNode.id !== template.id) {
            $scope.selectedResourceTemplate = template;
          } else {
            $scope.selectedResourceTemplate = substitutedNode;
          }
        };

        $scope.changeSubstitution = function(nodeName, template) {
          $scope.selectTemplate(nodeName, template);
          var substitutedNode = $scope.getSubstitutedTemplate(nodeName);
          if (substitutedNode.id !== template.id) {
            deploymentTopologyServices.updateSubstitution({
              appId: $scope.application.id,
              envId: $scope.deploymentContext.selectedEnvironment.id,
              nodeId: nodeName,
              locationResourceTemplateId: template.id
            }, undefined, function(response) {
              $scope.updateScopeDeploymentTopologyDTO(response.data);
              $scope.selectedResourceTemplate = $scope.getSubstitutedTemplate(nodeName);
            });
          }
        };

        $scope.updateSubstitutionProperty = function(propertyName, propertyValue) {
          return deploymentTopologyServices.updateSubstitutionProperty({
            appId: $scope.application.id,
            envId: $scope.deploymentContext.selectedEnvironment.id,
            nodeId: $scope.selectedNodeName
          }, angular.toJson({
            propertyName: propertyName,
            propertyValue: propertyValue
          }), function(result) {
            if (_.undefined(result.error)) {
              $scope.updateScopeDeploymentTopologyDTO(result.data);
            }
            return result;
          }).$promise;
        };

        $scope.updateSubstitutionCapabilityProperty = function(capabilityName, propertyName, propertyValue) {
          return deploymentTopologyServices.updateSubstitutionCapabilityProperty({
            appId: $scope.application.id,
            envId: $scope.deploymentContext.selectedEnvironment.id,
            nodeId: $scope.selectedNodeName,
            capabilityName: capabilityName
          }, angular.toJson({
            propertyName: propertyName,
            propertyValue: propertyValue
          }), function(result) {
            if (_.undefined(result.error)) {
              $scope.updateScopeDeploymentTopologyDTO(result.data);
            }
            return result;
          }).$promise;
        };
        
        $scope.isPropertyEditable = function(propertyPath){
          if($scope.getSubstitutedTemplate($scope.selectedNodeName).id === $scope.selectedResourceTemplate.id){
            if(_.definedPath(propertyPath, 'capabilityName')){
              return isCapabilityPropertyEditable(propertyPath.capabilityName, propertyPath.propertyName);
            }else{
              return isNodePropertyEditable(propertyPath.propertyName);
            }
          }
          return false;
        };
        
        function isPropertyValueNullOrDefault(toscaType, propertyName, value){
          var propertyDef = _.result(_.find(toscaType.properties, {'key': propertyName}), 'value');
          return _.undefined(value) || _.isEqual(_.get(value, 'value'), _.get(propertyDef, 'default'));
        }
        
        function isNodePropertyEditable(propertyName){
          var originalNode =  $scope.deploymentContext.deploymentTopologyDTO.topology.originalNodes[$scope.selectedNodeName] || {};
          var locationTemplate = $scope.deploymentContext.deploymentTopologyDTO.locationResourceTemplates[$scope.selectedResourceTemplate.id].template || {};
          var originalProperty = _.result(_.find(originalNode.properties, {'key':propertyName}), 'value');
          var originalLocationTemplateProperty = _.result(_.get(locationTemplate, 'propertiesMap.'+propertyName), 'value');
          
          var toscaType = $scope.deploymentContext.deploymentTopologyDTO.availableSubstitutions.substitutionTypes.nodeTypes[locationTemplate.type];
          
          if(isPropertyValueNullOrDefault(toscaType, propertyName, originalProperty) 
              && isPropertyValueNullOrDefault(toscaType, propertyName, originalLocationTemplateProperty) ){
            return true;
          }
          return false;
        };
        
        function isCapabilityPropertyEditable(capabilityName, propertyName){
          var originalNode =  $scope.deploymentContext.deploymentTopologyDTO.topology.originalNodes[$scope.selectedNodeName] || {};
          var locationTemplate = $scope.deploymentContext.deploymentTopologyDTO.locationResourceTemplates[$scope.selectedResourceTemplate.id].template || {};
          var originalCapability = _.result(_.find(originalNode.capabilities, {'key':capabilityName}), 'value') || {};
          var locationTemplateCapability = _.result(_.get(locationTemplate, 'capabilitiesMap.'+capabilityName), 'value') || {};
          var originalCapaProp = _.result(_.find(originalCapability.properties, {'key':propertyName}), 'value');
          var originalLocationTemplateCapaProp = _.result(_.get(locationTemplateCapability, 'propertiesMap.'+propertyName), 'value');
          
          var originalToscaType = $scope.deploymentContext.deploymentTopologyDTO.capabilityTypes[originalCapability.type];
          var locationTemplateToscaType = $scope.deploymentContext.deploymentTopologyDTO.availableSubstitutions.substitutionTypes.capabilityTypes[locationTemplateCapability.type];
          
        // edit if:
          // - the initial topology value is null or default value
         //   AND
        //   - the location resource value is null or default value
          if(isPropertyValueNullOrDefault(originalToscaType, propertyName, originalCapaProp) 
              && isPropertyValueNullOrDefault(locationTemplateToscaType, propertyName, originalLocationTemplateCapaProp) ){
            return true;
          }
          return false;
        };
      }
    ]); //controller
}); //Define
