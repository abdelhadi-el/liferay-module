Liferay.Loader.define("document-library-web@4.0.86/document_library/js/InfoPanel.es", ['module', 'exports', 'require', 'document-library-web$clipboard', 'frontend-js-web'], function (module, exports, require) {
  var define = undefined;
  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports["default"] = void 0;

  var _clipboard = _interopRequireDefault(require("document-library-web$clipboard"));

  var _frontendJsWeb = require("frontend-js-web");

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

  function _defineProperties(target, props) {
    for (var i = 0; i < props.length; i++) {
      var descriptor = props[i];descriptor.enumerable = descriptor.enumerable || false;descriptor.configurable = true;if ("value" in descriptor) descriptor.writable = true;Object.defineProperty(target, descriptor.key, descriptor);
    }
  }

  function _createClass(Constructor, protoProps, staticProps) {
    if (protoProps) _defineProperties(Constructor.prototype, protoProps);if (staticProps) _defineProperties(Constructor, staticProps);return Constructor;
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

  function _get(target, property, receiver) {
    if (typeof Reflect !== "undefined" && Reflect.get) {
      _get = Reflect.get;
    } else {
      _get = function _get(target, property, receiver) {
        var base = _superPropBase(target, property);if (!base) return;var desc = Object.getOwnPropertyDescriptor(base, property);if (desc.get) {
          return desc.get.call(receiver);
        }return desc.value;
      };
    }return _get(target, property, receiver || target);
  }

  function _superPropBase(object, property) {
    while (!Object.prototype.hasOwnProperty.call(object, property)) {
      object = _getPrototypeOf(object);if (object === null) break;
    }return object;
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

  /**
   * @class InfoPanel
   */
  var InfoPanel = /*#__PURE__*/function (_PortletBase) {
    _inherits(InfoPanel, _PortletBase);

    function InfoPanel() {
      _classCallCheck(this, InfoPanel);

      return _possibleConstructorReturn(this, _getPrototypeOf(InfoPanel).apply(this, arguments));
    }

    _createClass(InfoPanel, [{
      key: "attached",

      /**
       * @inheritdoc
       * @review
       */
      value: function attached() {
        this._clipboard = new _clipboard["default"]('.dm-infopanel-copy-clipboard');

        this._clipboard.on('success', this._handleClipboardSuccess.bind(this));
      }
      /**
       * @inheritdoc
       * @review
       */

    }, {
      key: "disposeInternal",
      value: function disposeInternal() {
        _get(_getPrototypeOf(InfoPanel.prototype), "disposeInternal", this).call(this);

        this._clipboard.destroy();
      }
    }, {
      key: "_handleClipboardSuccess",
      value: function _handleClipboardSuccess() {
        (0, _frontendJsWeb.openToast)({
          message: Liferay.Language.get('copied-link-to-the-clipboard')
        });
      }
    }]);

    return InfoPanel;
  }(_frontendJsWeb.PortletBase);

  var _default = InfoPanel;
  exports["default"] = _default;
  //# sourceMappingURL=InfoPanel.es.js.map
});
//# sourceMappingURL=InfoPanel.es.js.map