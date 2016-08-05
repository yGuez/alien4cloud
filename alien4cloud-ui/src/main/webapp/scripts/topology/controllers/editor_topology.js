// Topology editor controller
define(function (require) {
  'use strict';

  var modules = require('modules');
  var $ = require('jquery');
  var joint = require('jointjs');

  require('bower_components/jquery-ui/ui/resizable');
  require('scripts/topology/services/topology_joint_shapes');

  modules.get('a4c-topology-editor', ['a4c-common', 'ui.bootstrap', 'a4c-tosca', 'a4c-styles']).controller('TopologyVisualEditorCtrl',
    ['$scope', 'topoEditDisplay', 'topologyJointShapes',
    function($scope, topoEditDisplay, topologyJointShapes) {

      $scope.displays = {
        side: { active: true, size: 400, selector: '#side-box', only: [] },
      };
      topoEditDisplay($scope, '#topology-editor');

      var graph = new joint.dia.Graph();
      var paper = new joint.dia.Paper({
          el: $('#topology-paper-box'),
          width: 600,
          height: 200,
          model: graph,
          gridSize: 1
      });
      var nodeTemplate = topologyJointShapes.abstractNodeTempalte({
        position: { x: 100, y: 30 },
        attrs: {
          '.title': { text: 'Compute' },
          '.version': { text: '1.0.2' },
          '.scaling-text': { text: '1 - 5 - 20' }
        }
      });

      graph.addCells([nodeTemplate]);
      console.log('created paper');
    }
  ]);
}); // define
