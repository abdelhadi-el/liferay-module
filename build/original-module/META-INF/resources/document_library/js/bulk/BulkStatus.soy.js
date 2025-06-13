Liferay.Loader.define("document-library-web@4.0.86/document_library/js/bulk/BulkStatus.soy", ['module', 'exports', 'require', 'frontend-js-metal-web$metal-component', 'frontend-js-metal-web$metal-soy'], function (module, exports, require) {
  var define = undefined;
  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports["default"] = exports.templates = exports.comliferaydocumentlibrarywebBulkStatus = void 0;

  var _metalComponent = _interopRequireDefault(require("frontend-js-metal-web$metal-component"));

  var _metalSoy = _interopRequireDefault(require("frontend-js-metal-web$metal-soy"));

  function _interopRequireDefault(obj) {
    return obj && obj.__esModule ? obj : { "default": obj };
  }

  function _typeof(obj) {
    "@babel/helpers - typeof";
    if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") {
      _typeof = function _typeof(obj) {
        return typeof obj;
      };
    } else {
      _typeof = function _typeof(obj) {
        return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
      };
    }return _typeof(obj);
  }

  function _classCallCheck(instance, Constructor) {
    if (!(instance instanceof Constructor)) {
      throw new TypeError("Cannot call a class as a function");
    }
  }

  function _possibleConstructorReturn(self, call) {
    if (call && (_typeof(call) === "object" || typeof call === "function")) {
      return call;
    }return _assertThisInitialized(self);
  }

  function _assertThisInitialized(self) {
    if (self === void 0) {
      throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
    }return self;
  }

  function _getPrototypeOf(o) {
    _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) {
      return o.__proto__ || Object.getPrototypeOf(o);
    };return _getPrototypeOf(o);
  }

  function _inherits(subClass, superClass) {
    if (typeof superClass !== "function" && superClass !== null) {
      throw new TypeError("Super expression must either be null or a function");
    }subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } });if (superClass) _setPrototypeOf(subClass, superClass);
  }

  function _setPrototypeOf(o, p) {
    _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) {
      o.__proto__ = p;return o;
    };return _setPrototypeOf(o, p);
  }

  var templates;
  exports.templates = templates;
  goog.loadModule(function (exports) {
    var soy = goog.require('soy');

    goog.require('soydata');

    // This file was automatically generated from BulkStatus.soy.
    // Please don't edit this file by hand.

    /**
     * @fileoverview Templates in namespace com.liferay.document.library.web.BulkStatus.
     * @public
     */

    goog.module('com.liferay.document.library.web.BulkStatus.incrementaldom');

    goog.require('goog.string');

    var incrementalDom = goog.require('incrementaldom');

    goog.require('soy.asserts');

    goog.require('soy.idom');

    /**
     * @param {{
     *  bulkInProgress: boolean
     * }} opt_data
     * @param {Object<string, *>=} opt_ijData
     * @param {Object<string, *>=} opt_ijData_deprecated
     * @return {void}
     * @suppress {checkTypes}
     */

    function $render(opt_data, opt_ijData, opt_ijData_deprecated) {
      opt_ijData = opt_ijData_deprecated || opt_ijData;
      /** @type {boolean} */

      var bulkInProgress = soy.asserts.assertType(goog.isBoolean(opt_data.bulkInProgress) || opt_data.bulkInProgress === 1 || opt_data.bulkInProgress === 0, 'bulkInProgress', opt_data.bulkInProgress, 'boolean');
      incrementalDom.elementOpenStart('div');
      incrementalDom.attr('class', 'bulk-status-container');
      incrementalDom.elementOpenEnd();
      incrementalDom.elementOpenStart('div');
      incrementalDom.attr('class', 'bulk-status ' + (!bulkInProgress ? 'closed' : ''));
      incrementalDom.elementOpenEnd();
      incrementalDom.elementOpenStart('div');
      incrementalDom.attr('class', 'bulk-status-content');
      incrementalDom.elementOpenEnd();
      incrementalDom.elementOpenStart('span');
      incrementalDom.attr('aria-hidden', 'true');
      incrementalDom.attr('class', 'loading-animation loading-animation-light loading-animation-sm');
      incrementalDom.elementOpenEnd();
      incrementalDom.elementClose('span');
      incrementalDom.elementOpen('span');
      /** @desc  */

      var MSG_EXTERNAL_137312238327862904 = Liferay.Language.get('processing-actions');
      incrementalDom.text(goog.string.unescapeEntities(MSG_EXTERNAL_137312238327862904));
      incrementalDom.elementClose('span');
      incrementalDom.elementClose('div');
      incrementalDom.elementClose('div');
      incrementalDom.elementClose('div');
    }

    exports.render = $render;
    /**
     * @typedef {{
     *  bulkInProgress: boolean
     * }}
     */

    $render.Params;

    if (goog.DEBUG) {
      $render.soyTemplateName = 'com.liferay.document.library.web.BulkStatus.render';
    }

    exports.render.params = ["bulkInProgress"];
    exports.render.types = {
      "bulkInProgress": "bool"
    };
    exports.templates = templates = exports;
    return exports;
  });

  var comliferaydocumentlibrarywebBulkStatus = /*#__PURE__*/function (_Component) {
    _inherits(comliferaydocumentlibrarywebBulkStatus, _Component);

    function comliferaydocumentlibrarywebBulkStatus() {
      _classCallCheck(this, comliferaydocumentlibrarywebBulkStatus);

      return _possibleConstructorReturn(this, _getPrototypeOf(comliferaydocumentlibrarywebBulkStatus).apply(this, arguments));
    }

    return comliferaydocumentlibrarywebBulkStatus;
  }(_metalComponent["default"]);

  exports.comliferaydocumentlibrarywebBulkStatus = comliferaydocumentlibrarywebBulkStatus;

  _metalSoy["default"].register(comliferaydocumentlibrarywebBulkStatus, templates);

  var _default = templates;
  /* jshint ignore:end */

  exports["default"] = _default;
  //# sourceMappingURL=BulkStatus.soy.js.map
});
//# sourceMappingURL=BulkStatus.soy.js.map