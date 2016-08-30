// Definition of shapes for topology renderig using jointjs
define(function (require) {
  'use strict';

  var modules = require('modules');
  var joint = require('jointjs');

  modules.get('a4c-common-graph').factory('topologyJointShapes', function() {
    var AbstractNodeTemplate = joint.dia.Element.extend({
      markup: `<g class="node-template">
        <rect x="0" y="0" width="200" height="50" rx="0" ry="0" class="background"></rect>
        <image x="8" y="8" width="32" height="32"></image>
        <image x="44" y="26" width="16" height="16" xlink:href="images/abstract_ico.png"></image>
        <text text-anchor="start" class="title" x="44" y="20">Compute</text>
        <text text-anchor="end" class="version" x="190" y="40"></text>
        <g id="scalingPolicy">
          <text class="topology-svg-icon topology-svg-icon-center" transform="rotate(90 70 35)" x="70" y="35"></text>
          <text class="scaling-text" text-anchor="start" x="80" y="40"></text>
        </g>
      </g>`,

      defaults: joint.util.deepSupplement({
          type: 'AbstractNodeTemplate',
          size: { width: 200, height: 50 },
          attrs: {
            rect: { width: 200, height: 50, 'pointer-events': 'visiblePainted', rx: 0, ry: 0 }
          }
      }, joint.dia.Element.prototype.defaults)
    });

    return {
      abstractNodeTempalte: function(parameters) {
        return new AbstractNodeTemplate(parameters);
      }
    };
  }); // factory
}); // define
