Liferay.Loader.define("document-library-web@4.0.86/document_library/js/bulk/BulkStatus.es", ['module', 'exports', 'require', 'frontend-js-web', 'frontend-js-metal-web$metal-component', 'frontend-js-metal-web$metal-soy', 'frontend-js-metal-web$metal-state', './BulkStatus.soy'], function (module, exports, require) {
  var define = undefined;
  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports["default"] = void 0;

  var _frontendJsWeb = require("frontend-js-web");

  var _metalComponent = _interopRequireDefault(require("frontend-js-metal-web$metal-component"));

  var _metalSoy = _interopRequireDefault(require("frontend-js-metal-web$metal-soy"));

  var _metalState = require("frontend-js-metal-web$metal-state");

  var _BulkStatus = _interopRequireDefault(require("./BulkStatus.soy"));

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
   * Shows the bulk actions status
   *
   * @abstract
   * @extends {Component}
   */
  var BulkStatus = /*#__PURE__*/function (_Component) {
    _inherits(BulkStatus, _Component);

    function BulkStatus() {
      _classCallCheck(this, BulkStatus);

      return _possibleConstructorReturn(this, _getPrototypeOf(BulkStatus).apply(this, arguments));
    }

    _createClass(BulkStatus, [{
      key: "attached",

      /**
       * @inheritDoc
       */
      value: function attached() {
        Liferay.component(this.portletNamespace + 'BulkStatus', this);

        if (this.bulkInProgress) {
          this.startWatch();
        }
      }
      /**
       * Clears the interval to stop sending ajax requests.
       *
       * @protected
       */

    }, {
      key: "_clearInterval",
      value: function _clearInterval() {
        if (this.intervalId_) {
          clearInterval(this.intervalId_);
        }
      }
      /**
       * Clears the timeout that shows the component.
       *
       * @protected
       */

    }, {
      key: "_clearTimeout",
      value: function _clearTimeout() {
        if (this.visibleTimeOut) {
          clearTimeout(this.visibleTimeOut);
        }
      }
      /**
       * Sends a request to get the status
       * of bulk actions.
       *
       * @protected
       */

    }, {
      key: "_getBulkStatus",
      value: function _getBulkStatus() {
        var _this = this;

        (0, _frontendJsWeb.fetch)(this.pathModule + this.bulkStatusUrl).then(function (response) {
          return response.json();
        }).then(function (response) {
          if (!response.actionInProgress) {
            _this._onBulkFinish(false);
          }
        })["catch"](function () {
          _this._onBulkFinish(true);
        });
      }
      /**
       * Stops sending ajax request and hides the component.
       *
       * @protected
       */

    }, {
      key: "_onBulkFinish",
      value: function _onBulkFinish(error) {
        this._clearInterval();

        this._clearTimeout();

        this.bulkInProgress = false;

        this._showNotification(error);
      }
      /**
       * Shows a toast notification.
       *
       * @param {boolean} error Flag indicating if is an error or not
       * @protected
       * @review
       */

    }, {
      key: "_showNotification",
      value: function _showNotification(error) {
        var message;

        if (error) {
          message = Liferay.Language.get('an-unexpected-error-occurred');
        } else {
          message = Liferay.Language.get('changes-saved');
        }

        var openToastParams = {
          message: message
        };

        if (error) {
          openToastParams.title = Liferay.Language.get('error');
          openToastParams.type = 'danger';
        }

        (0, _frontendJsWeb.openToast)(openToastParams);
      }
      /**
       * Watch the status of bulk actions.
       * It shows the component if it takes
       * longer than 'waitingTime'.
       */

    }, {
      key: "startWatch",
      value: function startWatch() {
        var _this2 = this;

        this._clearInterval();

        this._getBulkStatus();

        this.intervalId_ = setInterval(this._getBulkStatus.bind(this), this.intervalSpeed);

        if (!this.bulkInProgress) {
          this.visibleTimeOut = setTimeout(function () {
            _this2.bulkInProgress = true;
          }, this.waitingTime);
        }
      }
    }]);

    return BulkStatus;
  }(_metalComponent["default"]);
  /**
   * BulkStatus State definition.
   * @ignore
   * @static
   * @type {!Object}
   */

  BulkStatus.STATE = {
    /**
     * Wether to show the component or not
     * @type {Boolean}
     */
    bulkInProgress: _metalState.Config.bool().value(false),

    /**
     * Uri to send the bulk status fetch request.
     * @instance
     * @memberof BulkStatus
     * @type {String}
     */
    bulkStatusUrl: _metalState.Config.string().value('/bulk/v1.0/status'),

    /**
     * The interval (in milliseconds) on how often
     * we will check if there are bulk actions in progress.
     *
     * @instance
     * @memberof BulkStatus
     * @type {Number}
     */
    intervalSpeed: _metalState.Config.number().value(1000),

    /**
     * PathModule
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {String}
     */
    pathModule: _metalState.Config.string().required(),

    /**
     * Portlet's namespace
     *
     * @instance
     * @memberof BulkStatus
     * @review
     * @type {string}
     */
    portletNamespace: _metalState.Config.string().required(),

    /**
     * The time (in milliseconds) we have to wait to
     * show the component.
     *
     * @instance
     * @memberof BulkStatus
     * @type {Number}
     */
    waitingTime: _metalState.Config.number().value(1000)
  }; // Register component

  _metalSoy["default"].register(BulkStatus, _BulkStatus["default"]);

  var _default = BulkStatus;
  exports["default"] = _default;
  //# sourceMappingURL=BulkStatus.es.js.map
});
//# sourceMappingURL=BulkStatus.es.js.map