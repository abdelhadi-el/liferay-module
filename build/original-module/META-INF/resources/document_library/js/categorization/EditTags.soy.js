Liferay.Loader.define("document-library-web@4.0.86/document_library/js/categorization/EditTags.soy", ['module', 'exports', 'require', 'frontend-js-metal-web$metal-component', 'frontend-js-metal-web$metal-soy'], function (module, exports, require) {
  var define = undefined;
  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports["default"] = exports.templates = exports.EditTags = void 0;

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

    // This file was automatically generated from EditTags.soy.
    // Please don't edit this file by hand.

    /**
     * @fileoverview Templates in namespace EditTags.
     * @public
     */

    goog.module('EditTags.incrementaldom');

    goog.require('goog.soy.data.SanitizedContent');

    goog.require('goog.string');

    var incrementalDom = goog.require('incrementaldom');

    goog.require('soy.asserts');

    var soyIdom = goog.require('soy.idom');

    var $templateAlias2 = _metalSoy["default"].getTemplate('ClayAlert.incrementaldom', 'render');

    var $templateAlias3 = _metalSoy["default"].getTemplate('ClayRadio.incrementaldom', 'render');

    var $templateAlias4 = _metalSoy["default"].getTemplate('com.liferay.asset.taglib.AssetTagsSelector.incrementaldom', 'render');

    var $templateAlias1 = _metalSoy["default"].getTemplate('liferay.frontend.Modal.incrementaldom', 'render');
    /**
     * @param {{
     *  _commonTags: !Array<?>,
     *  _loading: boolean,
     *  _showModal: boolean,
     *  groupIds: !Array<?>,
     *  namespace: (!goog.soy.data.SanitizedContent|string),
     *  spritemap: (!goog.soy.data.SanitizedContent|string),
     *  close: (*|null|undefined),
     *  _handleFormSubmit: (*|null|undefined),
     *  _handleInputFocus: (*|null|undefined),
     *  _handleRadioChange: (*|null|undefined),
     *  _handleSelectedItemsChange: (*|null|undefined),
     *  description: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  multiple: (boolean|null|undefined),
     *  selectAll: (boolean|null|undefined)
     * }} opt_data
     * @param {Object<string, *>=} opt_ijData
     * @param {Object<string, *>=} opt_ijData_deprecated
     * @return {void}
     * @suppress {checkTypes}
     */

    function $render(opt_data, opt_ijData, opt_ijData_deprecated) {
      opt_ijData = opt_ijData_deprecated || opt_ijData;
      /** @type {!Array<?>} */

      soy.asserts.assertType(goog.isArray(opt_data._commonTags), '_commonTags', opt_data._commonTags, '!Array<?>');

      /** @type {boolean} */

      var _loading = soy.asserts.assertType(goog.isBoolean(opt_data._loading) || opt_data._loading === 1 || opt_data._loading === 0, '_loading', opt_data._loading, 'boolean');
      /** @type {boolean} */

      var _showModal = soy.asserts.assertType(goog.isBoolean(opt_data._showModal) || opt_data._showModal === 1 || opt_data._showModal === 0, '_showModal', opt_data._showModal, 'boolean');
      /** @type {!Array<?>} */

      soy.asserts.assertType(goog.isArray(opt_data.groupIds), 'groupIds', opt_data.groupIds, '!Array<?>');
      soy.asserts.assertType(goog.isString(opt_data.namespace) || opt_data.namespace instanceof goog.soy.data.SanitizedContent, 'namespace', opt_data.namespace, '!goog.soy.data.SanitizedContent|string');

      /** @type {!goog.soy.data.SanitizedContent|string} */

      var spritemap = soy.asserts.assertType(goog.isString(opt_data.spritemap) || opt_data.spritemap instanceof goog.soy.data.SanitizedContent, 'spritemap', opt_data.spritemap, '!goog.soy.data.SanitizedContent|string');
      /** @type {*|null|undefined} */

      var close = opt_data.close;
      /** @type {*|null|undefined} */

      var _handleFormSubmit = opt_data._handleFormSubmit;
      /** @type {*|null|undefined} */

      opt_data._handleInputFocus;
      opt_data._handleRadioChange;
      opt_data._handleSelectedItemsChange;
      soy.asserts.assertType(opt_data.description == null || goog.isString(opt_data.description) || opt_data.description instanceof goog.soy.data.SanitizedContent, 'description', opt_data.description, '!goog.soy.data.SanitizedContent|null|string|undefined');
      soy.asserts.assertType(opt_data.multiple == null || goog.isBoolean(opt_data.multiple) || opt_data.multiple === 1 || opt_data.multiple === 0, 'multiple', opt_data.multiple, 'boolean|null|undefined');

      /** @type {boolean|null|undefined} */

      var selectAll = soy.asserts.assertType(opt_data.selectAll == null || goog.isBoolean(opt_data.selectAll) || opt_data.selectAll === 1 || opt_data.selectAll === 0, 'selectAll', opt_data.selectAll, 'boolean|null|undefined');

      if (_showModal) {
        incrementalDom.elementOpenStart('form');
        incrementalDom.attr('data-onsubmit', _handleFormSubmit);
        incrementalDom.elementOpenEnd();

        var param316 = function param316() {
          if (_loading) {
            $loading(null, null, opt_ijData);
          } else {
            $tags(opt_data, null, opt_ijData);
          }
        };

        var param344 = function param344() {
          /** @desc  */
          var MSG_EXTERNAL_525777701036415049 = Liferay.Language.get('edit-tags');
          incrementalDom.text(goog.string.unescapeEntities(MSG_EXTERNAL_525777701036415049));
        };

        var param350 = function param350() {
          incrementalDom.elementOpenStart('button');
          incrementalDom.attr('class', 'btn btn-secondary');
          incrementalDom.attr('data-onclick', close);
          incrementalDom.attr('type', 'button');
          incrementalDom.elementOpenEnd();
          /** @desc  */

          var MSG_EXTERNAL_503445309622982213 = Liferay.Language.get('cancel');
          incrementalDom.text(goog.string.unescapeEntities(MSG_EXTERNAL_503445309622982213));
          incrementalDom.elementClose('button');
          incrementalDom.elementOpenStart('button');
          incrementalDom.attr('class', 'btn btn-primary');
          incrementalDom.attr('type', 'submit');
          incrementalDom.elementOpenEnd();
          /** @desc  */

          var MSG_EXTERNAL_8640257554156088424 = Liferay.Language.get('save');
          incrementalDom.text(goog.string.unescapeEntities(MSG_EXTERNAL_8640257554156088424));
          incrementalDom.elementClose('button');
        };

        $templateAlias1({
          body: param316,
          dialogClasses: 'edit-tags modal-dialog-sm' + (selectAll ? ' modal-dialog-expanded' : ''),
          header: param344,
          footer: param350,
          ref: 'modal',
          spritemap: spritemap,
          visible: true
        }, null, opt_ijData);
        incrementalDom.elementClose('form');
      }
    }

    exports.render = $render;
    /**
     * @typedef {{
     *  _commonTags: !Array<?>,
     *  _loading: boolean,
     *  _showModal: boolean,
     *  groupIds: !Array<?>,
     *  namespace: (!goog.soy.data.SanitizedContent|string),
     *  spritemap: (!goog.soy.data.SanitizedContent|string),
     *  close: (*|null|undefined),
     *  _handleFormSubmit: (*|null|undefined),
     *  _handleInputFocus: (*|null|undefined),
     *  _handleRadioChange: (*|null|undefined),
     *  _handleSelectedItemsChange: (*|null|undefined),
     *  description: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  multiple: (boolean|null|undefined),
     *  selectAll: (boolean|null|undefined)
     * }}
     */

    $render.Params;

    if (goog.DEBUG) {
      $render.soyTemplateName = 'EditTags.render';
    }
    /**
     * @param {Object<string, *>=} opt_data
     * @param {Object<string, *>=} opt_ijData
     * @param {Object<string, *>=} opt_ijData_deprecated
     * @return {void}
     * @suppress {checkTypes}
     */

    function $loading(opt_data, opt_ijData, opt_ijData_deprecated) {
      opt_ijData = opt_ijData_deprecated || opt_ijData;
      incrementalDom.elementOpenStart('div');
      incrementalDom.attr('class', 'align-items-center d-flex h-100 loading-indicator');
      incrementalDom.elementOpenEnd();
      incrementalDom.elementOpenStart('span');
      incrementalDom.attr('aria-hidden', 'true');
      incrementalDom.attr('class', 'loading-animation');
      incrementalDom.elementOpenEnd();
      incrementalDom.elementClose('span');
      incrementalDom.elementClose('div');
    }

    exports.loading = $loading;

    if (goog.DEBUG) {
      $loading.soyTemplateName = 'EditTags.loading';
    }
    /**
     * @param {{
     *  _commonTags: !Array<?>,
     *  groupIds: !Array<?>,
     *  namespace: (!goog.soy.data.SanitizedContent|string),
     *  spritemap: (!goog.soy.data.SanitizedContent|string),
     *  _handleInputFocus: (*|null|undefined),
     *  _handleRadioChange: (*|null|undefined),
     *  _handleSelectedItemsChange: (*|null|undefined),
     *  description: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  multiple: (boolean|null|undefined),
     *  selectAll: (boolean|null|undefined)
     * }} opt_data
     * @param {Object<string, *>=} opt_ijData
     * @param {Object<string, *>=} opt_ijData_deprecated
     * @return {void}
     * @suppress {checkTypes}
     */

    function $tags(opt_data, opt_ijData, opt_ijData_deprecated) {
      opt_ijData = opt_ijData_deprecated || opt_ijData;
      /** @type {!Array<?>} */

      var _commonTags = soy.asserts.assertType(goog.isArray(opt_data._commonTags), '_commonTags', opt_data._commonTags, '!Array<?>');
      /** @type {!Array<?>} */

      var groupIds = soy.asserts.assertType(goog.isArray(opt_data.groupIds), 'groupIds', opt_data.groupIds, '!Array<?>');
      /** @type {!goog.soy.data.SanitizedContent|string} */

      var namespace = soy.asserts.assertType(goog.isString(opt_data.namespace) || opt_data.namespace instanceof goog.soy.data.SanitizedContent, 'namespace', opt_data.namespace, '!goog.soy.data.SanitizedContent|string');
      /** @type {!goog.soy.data.SanitizedContent|string} */

      var spritemap = soy.asserts.assertType(goog.isString(opt_data.spritemap) || opt_data.spritemap instanceof goog.soy.data.SanitizedContent, 'spritemap', opt_data.spritemap, '!goog.soy.data.SanitizedContent|string');
      /** @type {*|null|undefined} */

      var _handleInputFocus = opt_data._handleInputFocus;
      /** @type {*|null|undefined} */

      var _handleRadioChange = opt_data._handleRadioChange;
      /** @type {*|null|undefined} */

      var _handleSelectedItemsChange = opt_data._handleSelectedItemsChange;
      /** @type {!goog.soy.data.SanitizedContent|null|string|undefined} */

      var description = soy.asserts.assertType(opt_data.description == null || goog.isString(opt_data.description) || opt_data.description instanceof goog.soy.data.SanitizedContent, 'description', opt_data.description, '!goog.soy.data.SanitizedContent|null|string|undefined');
      /** @type {boolean|null|undefined} */

      var multiple = soy.asserts.assertType(opt_data.multiple == null || goog.isBoolean(opt_data.multiple) || opt_data.multiple === 1 || opt_data.multiple === 0, 'multiple', opt_data.multiple, 'boolean|null|undefined');
      /** @type {boolean|null|undefined} */

      var selectAll = soy.asserts.assertType(opt_data.selectAll == null || goog.isBoolean(opt_data.selectAll) || opt_data.selectAll === 1 || opt_data.selectAll === 0, 'selectAll', opt_data.selectAll, 'boolean|null|undefined');

      if (selectAll) {
        var msg_15290__soy15291 = '';
        /** @desc  */

        var MSG_EXTERNAL_116764904270097223 = Liferay.Language.get('this-operation-will-not-be-applied-to-any-of-the-selected-folders');
        msg_15290__soy15291 += MSG_EXTERNAL_116764904270097223;
        $templateAlias2({
          message: '' + msg_15290__soy15291,
          spritemap: spritemap,
          title: ''
        }, null, opt_ijData);
      }

      incrementalDom.elementOpen('p');
      soyIdom.print(description);
      incrementalDom.elementClose('p');

      if (multiple) {
        var msg_15296__soy15297 = '';
        /** @desc  */

        var MSG_EXTERNAL_7300848098821660663 = Liferay.Language.get('replace');
        msg_15296__soy15297 += MSG_EXTERNAL_7300848098821660663;
        var msg_15293__soy15294 = '';
        /** @desc  */

        var MSG_EXTERNAL_9095134843628689709 = Liferay.Language.get('edit');
        msg_15293__soy15294 += MSG_EXTERNAL_9095134843628689709;

        var param410 = function param410() {
          incrementalDom.elementOpenStart('div');
          incrementalDom.attr('class', 'form-text');
          incrementalDom.elementOpenEnd();
          /** @desc  */

          var MSG_EXTERNAL_206703150202658230 = Liferay.Language.get('add-new-tags-or-remove-common-tags');
          incrementalDom.text(goog.string.unescapeEntities(MSG_EXTERNAL_206703150202658230));
          incrementalDom.elementClose('div');
        };

        $templateAlias3({
          checked: true,
          events: {
            change: _handleRadioChange
          },
          label: ' ' + msg_15293__soy15294 + ' ',
          labelContent: param410,
          name: 'add-replace',
          value: 'add'
        }, null, opt_ijData);

        var param427 = function param427() {
          incrementalDom.elementOpenStart('div');
          incrementalDom.attr('class', 'form-text');
          incrementalDom.elementOpenEnd();
          /** @desc  */

          var MSG_EXTERNAL_3120706377916325722 = Liferay.Language.get('these-tags-replace-all-existing-tags');
          incrementalDom.text(goog.string.unescapeEntities(MSG_EXTERNAL_3120706377916325722));
          incrementalDom.elementClose('div');
        };

        $templateAlias3({
          events: {
            change: _handleRadioChange
          },
          label: ' ' + msg_15296__soy15297 + ' ',
          labelContent: param427,
          name: 'add-replace',
          value: 'replace'
        }, null, opt_ijData);
      }

      $templateAlias4({
        elementClasses: 'clay-multi-select',
        events: {
          inputFocus: _handleInputFocus,
          itemAdded: _handleSelectedItemsChange,
          itemRemoved: _handleSelectedItemsChange
        },
        groupIds: groupIds,
        id: namespace + 'assetTagsSelector',
        inputName: namespace + '_hiddenInput',
        selectedItems: _commonTags,
        showSelectButton: false,
        spritemap: spritemap
      }, null, opt_ijData);
    }

    exports.tags = $tags;
    /**
     * @typedef {{
     *  _commonTags: !Array<?>,
     *  groupIds: !Array<?>,
     *  namespace: (!goog.soy.data.SanitizedContent|string),
     *  spritemap: (!goog.soy.data.SanitizedContent|string),
     *  _handleInputFocus: (*|null|undefined),
     *  _handleRadioChange: (*|null|undefined),
     *  _handleSelectedItemsChange: (*|null|undefined),
     *  description: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  multiple: (boolean|null|undefined),
     *  selectAll: (boolean|null|undefined)
     * }}
     */

    $tags.Params;

    if (goog.DEBUG) {
      $tags.soyTemplateName = 'EditTags.tags';
    }

    exports.render.params = ["_commonTags", "_loading", "_showModal", "groupIds", "namespace", "spritemap", "close", "_handleFormSubmit", "_handleInputFocus", "_handleRadioChange", "_handleSelectedItemsChange", "description", "multiple", "selectAll"];
    exports.render.types = {
      "_commonTags": "list<?>",
      "_loading": "bool",
      "_showModal": "bool",
      "groupIds": "list<?>",
      "namespace": "string",
      "spritemap": "string",
      "close": "any",
      "_handleFormSubmit": "any",
      "_handleInputFocus": "any",
      "_handleRadioChange": "any",
      "_handleSelectedItemsChange": "any",
      "description": "string",
      "multiple": "bool",
      "selectAll": "bool"
    };
    exports.loading.params = [];
    exports.loading.types = {};
    exports.tags.params = ["_commonTags", "groupIds", "namespace", "spritemap", "_handleInputFocus", "_handleRadioChange", "_handleSelectedItemsChange", "description", "multiple", "selectAll"];
    exports.tags.types = {
      "_commonTags": "list<?>",
      "groupIds": "list<?>",
      "namespace": "string",
      "spritemap": "string",
      "_handleInputFocus": "any",
      "_handleRadioChange": "any",
      "_handleSelectedItemsChange": "any",
      "description": "string",
      "multiple": "bool",
      "selectAll": "bool"
    };
    exports.templates = templates = exports;
    return exports;
  });

  var EditTags = /*#__PURE__*/function (_Component) {
    _inherits(EditTags, _Component);

    function EditTags() {
      _classCallCheck(this, EditTags);

      return _possibleConstructorReturn(this, _getPrototypeOf(EditTags).apply(this, arguments));
    }

    return EditTags;
  }(_metalComponent["default"]);

  exports.EditTags = EditTags;

  _metalSoy["default"].register(EditTags, templates);

  var _default = templates;
  /* jshint ignore:end */

  exports["default"] = _default;
  //# sourceMappingURL=EditTags.soy.js.map
});
//# sourceMappingURL=EditTags.soy.js.map