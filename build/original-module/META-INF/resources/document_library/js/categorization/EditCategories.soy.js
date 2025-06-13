Liferay.Loader.define("document-library-web@4.0.86/document_library/js/categorization/EditCategories.soy", ['module', 'exports', 'require', 'frontend-js-metal-web$metal-component', 'frontend-js-metal-web$metal-soy'], function (module, exports, require) {
  var define = undefined;
  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports["default"] = exports.templates = exports.EditCategories = void 0;

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

    // This file was automatically generated from EditCategories.soy.
    // Please don't edit this file by hand.

    /**
     * @fileoverview Templates in namespace EditCategories.
     * @public
     */

    goog.module('EditCategories.incrementaldom');

    goog.require('goog.soy.data.SanitizedContent');

    goog.require('goog.string');

    var incrementalDom = goog.require('incrementaldom');

    goog.require('soy.asserts');

    var soyIdom = goog.require('soy.idom');

    var $templateAlias2 = _metalSoy["default"].getTemplate('ClayAlert.incrementaldom', 'render');

    var $templateAlias3 = _metalSoy["default"].getTemplate('ClayRadio.incrementaldom', 'render');

    var $templateAlias4 = _metalSoy["default"].getTemplate('com.liferay.asset.taglib.AssetCategoriesSelector.incrementaldom', 'render');

    var $templateAlias1 = _metalSoy["default"].getTemplate('liferay.frontend.Modal.incrementaldom', 'render');
    /**
     * @param {{
     *  groupIds: !Array<!goog.soy.data.SanitizedContent|string>,
     *  hiddenInput: (!goog.soy.data.SanitizedContent|string),
     *  loading: boolean,
     *  selectCategoriesUrl: (!goog.soy.data.SanitizedContent|string),
     *  showModal: boolean,
     *  spritemap: (!goog.soy.data.SanitizedContent|string),
     *  _handleFormSubmit: (*|null|undefined),
     *  _handleInputFocus: (*|null|undefined),
     *  _handleRadioChange: (*|null|undefined),
     *  _handleSelectedItemsChange: (*|null|undefined),
     *  description: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  multiple: (boolean|null|undefined),
     *  namespace: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  selectAll: (boolean|null|undefined),
     *  vocabularies: (!Array<?>|null|undefined)
     * }} opt_data
     * @param {Object<string, *>=} opt_ijData
     * @param {Object<string, *>=} opt_ijData_deprecated
     * @return {void}
     * @suppress {checkTypes}
     */

    function $render(opt_data, opt_ijData, opt_ijData_deprecated) {
      opt_ijData = opt_ijData_deprecated || opt_ijData;
      /** @type {!Array<!goog.soy.data.SanitizedContent|string>} */

      soy.asserts.assertType(goog.isArray(opt_data.groupIds), 'groupIds', opt_data.groupIds, '!Array<!goog.soy.data.SanitizedContent|string>');
      soy.asserts.assertType(goog.isString(opt_data.hiddenInput) || opt_data.hiddenInput instanceof goog.soy.data.SanitizedContent, 'hiddenInput', opt_data.hiddenInput, '!goog.soy.data.SanitizedContent|string');

      /** @type {boolean} */

      var loading = soy.asserts.assertType(goog.isBoolean(opt_data.loading) || opt_data.loading === 1 || opt_data.loading === 0, 'loading', opt_data.loading, 'boolean');
      /** @type {!goog.soy.data.SanitizedContent|string} */

      soy.asserts.assertType(goog.isString(opt_data.selectCategoriesUrl) || opt_data.selectCategoriesUrl instanceof goog.soy.data.SanitizedContent, 'selectCategoriesUrl', opt_data.selectCategoriesUrl, '!goog.soy.data.SanitizedContent|string');

      /** @type {boolean} */

      var showModal = soy.asserts.assertType(goog.isBoolean(opt_data.showModal) || opt_data.showModal === 1 || opt_data.showModal === 0, 'showModal', opt_data.showModal, 'boolean');
      /** @type {!goog.soy.data.SanitizedContent|string} */

      var spritemap = soy.asserts.assertType(goog.isString(opt_data.spritemap) || opt_data.spritemap instanceof goog.soy.data.SanitizedContent, 'spritemap', opt_data.spritemap, '!goog.soy.data.SanitizedContent|string');
      /** @type {*|null|undefined} */

      var _handleFormSubmit = opt_data._handleFormSubmit;
      /** @type {*|null|undefined} */

      opt_data._handleInputFocus;
      opt_data._handleRadioChange;
      opt_data._handleSelectedItemsChange;
      soy.asserts.assertType(opt_data.description == null || goog.isString(opt_data.description) || opt_data.description instanceof goog.soy.data.SanitizedContent, 'description', opt_data.description, '!goog.soy.data.SanitizedContent|null|string|undefined');
      soy.asserts.assertType(opt_data.multiple == null || goog.isBoolean(opt_data.multiple) || opt_data.multiple === 1 || opt_data.multiple === 0, 'multiple', opt_data.multiple, 'boolean|null|undefined');
      soy.asserts.assertType(opt_data.namespace == null || goog.isString(opt_data.namespace) || opt_data.namespace instanceof goog.soy.data.SanitizedContent, 'namespace', opt_data.namespace, '!goog.soy.data.SanitizedContent|null|string|undefined');

      /** @type {boolean|null|undefined} */

      var selectAll = soy.asserts.assertType(opt_data.selectAll == null || goog.isBoolean(opt_data.selectAll) || opt_data.selectAll === 1 || opt_data.selectAll === 0, 'selectAll', opt_data.selectAll, 'boolean|null|undefined');
      /** @type {!Array<?>|null|undefined} */

      soy.asserts.assertType(opt_data.vocabularies == null || goog.isArray(opt_data.vocabularies), 'vocabularies', opt_data.vocabularies, '!Array<?>|null|undefined');


      if (showModal) {
        incrementalDom.elementOpenStart('form');
        incrementalDom.attr('data-onsubmit', _handleFormSubmit);
        incrementalDom.elementOpenEnd();

        var param79 = function param79() {
          if (loading) {
            $loading(null, null, opt_ijData);
          } else {
            $categories(opt_data, null, opt_ijData);
          }
        };

        var param109 = function param109() {
          /** @desc  */
          var MSG_EXTERNAL_5558485503200476847 = Liferay.Language.get('edit-categories');
          incrementalDom.text(goog.string.unescapeEntities(MSG_EXTERNAL_5558485503200476847));
        };

        var param115 = function param115() {
          incrementalDom.elementOpenStart('button');
          incrementalDom.attr('class', 'btn btn-secondary');
          incrementalDom.attr('data-onclick', 'hide');
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
          body: param79,
          dialogClasses: 'edit-tags modal-dialog-sm' + (selectAll ? ' modal-dialog-expanded' : ''),
          header: param109,
          footer: param115,
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
     *  groupIds: !Array<!goog.soy.data.SanitizedContent|string>,
     *  hiddenInput: (!goog.soy.data.SanitizedContent|string),
     *  loading: boolean,
     *  selectCategoriesUrl: (!goog.soy.data.SanitizedContent|string),
     *  showModal: boolean,
     *  spritemap: (!goog.soy.data.SanitizedContent|string),
     *  _handleFormSubmit: (*|null|undefined),
     *  _handleInputFocus: (*|null|undefined),
     *  _handleRadioChange: (*|null|undefined),
     *  _handleSelectedItemsChange: (*|null|undefined),
     *  description: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  multiple: (boolean|null|undefined),
     *  namespace: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  selectAll: (boolean|null|undefined),
     *  vocabularies: (!Array<?>|null|undefined)
     * }}
     */

    $render.Params;

    if (goog.DEBUG) {
      $render.soyTemplateName = 'EditCategories.render';
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
      $loading.soyTemplateName = 'EditCategories.loading';
    }
    /**
     * @param {{
     *  groupIds: !Array<!goog.soy.data.SanitizedContent|string>,
     *  hiddenInput: (!goog.soy.data.SanitizedContent|string),
     *  selectCategoriesUrl: (!goog.soy.data.SanitizedContent|string),
     *  spritemap: (!goog.soy.data.SanitizedContent|string),
     *  _handleInputFocus: (*|null|undefined),
     *  _handleRadioChange: (*|null|undefined),
     *  _handleSelectedItemsChange: (*|null|undefined),
     *  description: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  multiple: (boolean|null|undefined),
     *  namespace: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  selectAll: (boolean|null|undefined),
     *  vocabularies: (!Array<?>|null|undefined)
     * }} opt_data
     * @param {Object<string, *>=} opt_ijData
     * @param {Object<string, *>=} opt_ijData_deprecated
     * @return {void}
     * @suppress {checkTypes}
     */

    function $categories(opt_data, opt_ijData, opt_ijData_deprecated) {
      opt_ijData = opt_ijData_deprecated || opt_ijData;
      /** @type {!Array<!goog.soy.data.SanitizedContent|string>} */

      var groupIds = soy.asserts.assertType(goog.isArray(opt_data.groupIds), 'groupIds', opt_data.groupIds, '!Array<!goog.soy.data.SanitizedContent|string>');
      /** @type {!goog.soy.data.SanitizedContent|string} */

      var hiddenInput = soy.asserts.assertType(goog.isString(opt_data.hiddenInput) || opt_data.hiddenInput instanceof goog.soy.data.SanitizedContent, 'hiddenInput', opt_data.hiddenInput, '!goog.soy.data.SanitizedContent|string');
      /** @type {!goog.soy.data.SanitizedContent|string} */

      var selectCategoriesUrl = soy.asserts.assertType(goog.isString(opt_data.selectCategoriesUrl) || opt_data.selectCategoriesUrl instanceof goog.soy.data.SanitizedContent, 'selectCategoriesUrl', opt_data.selectCategoriesUrl, '!goog.soy.data.SanitizedContent|string');
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
      /** @type {!goog.soy.data.SanitizedContent|null|string|undefined} */

      var namespace = soy.asserts.assertType(opt_data.namespace == null || goog.isString(opt_data.namespace) || opt_data.namespace instanceof goog.soy.data.SanitizedContent, 'namespace', opt_data.namespace, '!goog.soy.data.SanitizedContent|null|string|undefined');
      /** @type {boolean|null|undefined} */

      var selectAll = soy.asserts.assertType(opt_data.selectAll == null || goog.isBoolean(opt_data.selectAll) || opt_data.selectAll === 1 || opt_data.selectAll === 0, 'selectAll', opt_data.selectAll, 'boolean|null|undefined');
      /** @type {!Array<?>|null|undefined} */

      var vocabularies = soy.asserts.assertType(opt_data.vocabularies == null || goog.isArray(opt_data.vocabularies), 'vocabularies', opt_data.vocabularies, '!Array<?>|null|undefined');

      if (selectAll) {
        var msg_15281__soy15282 = '';
        /** @desc  */

        var MSG_EXTERNAL_116764904270097223 = Liferay.Language.get('this-operation-will-not-be-applied-to-any-of-the-selected-folders');
        msg_15281__soy15282 += MSG_EXTERNAL_116764904270097223;
        $templateAlias2({
          message: '' + msg_15281__soy15282,
          spritemap: spritemap,
          title: ''
        }, null, opt_ijData);
      }

      incrementalDom.elementOpen('p');
      soyIdom.print(description);
      incrementalDom.elementClose('p');

      if (multiple) {
        var msg_15287__soy15288 = '';
        /** @desc  */

        var MSG_EXTERNAL_7300848098821660663 = Liferay.Language.get('replace');
        msg_15287__soy15288 += MSG_EXTERNAL_7300848098821660663;
        var msg_15284__soy15285 = '';
        /** @desc  */

        var MSG_EXTERNAL_9095134843628689709 = Liferay.Language.get('edit');
        msg_15284__soy15285 += MSG_EXTERNAL_9095134843628689709;

        var param175 = function param175() {
          incrementalDom.elementOpenStart('div');
          incrementalDom.attr('class', 'form-text');
          incrementalDom.elementOpenEnd();
          /** @desc  */

          var MSG_EXTERNAL_520788524830952212 = Liferay.Language.get('add-new-categories-or-remove-common-categories');
          incrementalDom.text(goog.string.unescapeEntities(MSG_EXTERNAL_520788524830952212));
          incrementalDom.elementClose('div');
        };

        $templateAlias3({
          checked: true,
          events: {
            change: _handleRadioChange
          },
          label: ' ' + msg_15284__soy15285 + ' ',
          labelContent: param175,
          name: 'add-replace',
          value: 'add'
        }, null, opt_ijData);

        var param192 = function param192() {
          incrementalDom.elementOpenStart('div');
          incrementalDom.attr('class', 'form-text');
          incrementalDom.elementOpenEnd();
          /** @desc  */

          var MSG_EXTERNAL_2007753903579905097 = Liferay.Language.get('these-categories-replace-all-existing-categories');
          incrementalDom.text(goog.string.unescapeEntities(MSG_EXTERNAL_2007753903579905097));
          incrementalDom.elementClose('div');
        };

        $templateAlias3({
          events: {
            change: _handleRadioChange
          },
          label: ' ' + msg_15287__soy15288 + ' ',
          labelContent: param192,
          name: 'add-replace',
          value: 'replace'
        }, null, opt_ijData);
      }

      if (vocabularies && vocabularies.length > 0) {
        $templateAlias4({
          elementClasses: 'clay-multi-select',
          eventName: namespace + 'selectCategories',
          events: {
            selectedItemsChange: _handleSelectedItemsChange,
            inputFocus: _handleInputFocus
          },
          groupIds: groupIds,
          id: namespace + 'assetTagsSelector',
          inputName: namespace + hiddenInput,
          portletURL: selectCategoriesUrl,
          spritemap: spritemap,
          useFallbackInput: true,
          vocabularies: vocabularies
        }, null, opt_ijData);
      }
    }

    exports.categories = $categories;
    /**
     * @typedef {{
     *  groupIds: !Array<!goog.soy.data.SanitizedContent|string>,
     *  hiddenInput: (!goog.soy.data.SanitizedContent|string),
     *  selectCategoriesUrl: (!goog.soy.data.SanitizedContent|string),
     *  spritemap: (!goog.soy.data.SanitizedContent|string),
     *  _handleInputFocus: (*|null|undefined),
     *  _handleRadioChange: (*|null|undefined),
     *  _handleSelectedItemsChange: (*|null|undefined),
     *  description: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  multiple: (boolean|null|undefined),
     *  namespace: (!goog.soy.data.SanitizedContent|null|string|undefined),
     *  selectAll: (boolean|null|undefined),
     *  vocabularies: (!Array<?>|null|undefined)
     * }}
     */

    $categories.Params;

    if (goog.DEBUG) {
      $categories.soyTemplateName = 'EditCategories.categories';
    }

    exports.render.params = ["groupIds", "hiddenInput", "loading", "selectCategoriesUrl", "showModal", "spritemap", "_handleFormSubmit", "_handleInputFocus", "_handleRadioChange", "_handleSelectedItemsChange", "description", "multiple", "namespace", "selectAll", "vocabularies"];
    exports.render.types = {
      "groupIds": "list<string>",
      "hiddenInput": "string",
      "loading": "bool",
      "selectCategoriesUrl": "string",
      "showModal": "bool",
      "spritemap": "string",
      "_handleFormSubmit": "any",
      "_handleInputFocus": "any",
      "_handleRadioChange": "any",
      "_handleSelectedItemsChange": "any",
      "description": "string",
      "multiple": "bool",
      "namespace": "string",
      "selectAll": "bool",
      "vocabularies": "list<?>"
    };
    exports.loading.params = [];
    exports.loading.types = {};
    exports.categories.params = ["groupIds", "hiddenInput", "selectCategoriesUrl", "spritemap", "_handleInputFocus", "_handleRadioChange", "_handleSelectedItemsChange", "description", "multiple", "namespace", "selectAll", "vocabularies"];
    exports.categories.types = {
      "groupIds": "list<string>",
      "hiddenInput": "string",
      "selectCategoriesUrl": "string",
      "spritemap": "string",
      "_handleInputFocus": "any",
      "_handleRadioChange": "any",
      "_handleSelectedItemsChange": "any",
      "description": "string",
      "multiple": "bool",
      "namespace": "string",
      "selectAll": "bool",
      "vocabularies": "list<?>"
    };
    exports.templates = templates = exports;
    return exports;
  });

  var EditCategories = /*#__PURE__*/function (_Component) {
    _inherits(EditCategories, _Component);

    function EditCategories() {
      _classCallCheck(this, EditCategories);

      return _possibleConstructorReturn(this, _getPrototypeOf(EditCategories).apply(this, arguments));
    }

    return EditCategories;
  }(_metalComponent["default"]);

  exports.EditCategories = EditCategories;

  _metalSoy["default"].register(EditCategories, templates);

  var _default = templates;
  /* jshint ignore:end */

  exports["default"] = _default;
  //# sourceMappingURL=EditCategories.soy.js.map
});
//# sourceMappingURL=EditCategories.soy.js.map