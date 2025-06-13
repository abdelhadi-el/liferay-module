Liferay.Loader.define("document-library-web@4.0.86/document_library/js/categorization/EditTags.es", ['module', 'exports', 'require', 'asset-taglib/asset_tags_selector/AssetTagsSelector.es', 'frontend-taglib-clay$clay-multi-select', 'frontend-taglib-clay$clay-radio', 'frontend-js-web/liferay/compat/modal/Modal.es', 'frontend-js-web', 'frontend-js-metal-web$metal-component', 'frontend-js-metal-web$metal-soy', 'frontend-js-metal-web$metal-state', './EditTags.soy'], function (module, exports, require) {
  var define = undefined;
  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports["default"] = void 0;

  require("asset-taglib/asset_tags_selector/AssetTagsSelector.es");

  require("frontend-taglib-clay$clay-multi-select");

  require("frontend-taglib-clay$clay-radio");

  require("frontend-js-web/liferay/compat/modal/Modal.es");

  var _frontendJsWeb = require("frontend-js-web");

  var _metalComponent = _interopRequireDefault(require("frontend-js-metal-web$metal-component"));

  var _metalSoy = _interopRequireDefault(require("frontend-js-metal-web$metal-soy"));

  var _metalState = require("frontend-js-metal-web$metal-state");

  var _EditTags = _interopRequireDefault(require("./EditTags.soy"));

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

  function _slicedToArray(arr, i) {
    return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest();
  }

  function _nonIterableRest() {
    throw new TypeError("Invalid attempt to destructure non-iterable instance");
  }

  function _iterableToArrayLimit(arr, i) {
    if (!(Symbol.iterator in Object(arr) || Object.prototype.toString.call(arr) === "[object Arguments]")) {
      return;
    }var _arr = [];var _n = true;var _d = false;var _e = undefined;try {
      for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) {
        _arr.push(_s.value);if (i && _arr.length === i) break;
      }
    } catch (err) {
      _d = true;_e = err;
    } finally {
      try {
        if (!_n && _i["return"] != null) _i["return"]();
      } finally {
        if (_d) throw _e;
      }
    }return _arr;
  }

  function _arrayWithHoles(arr) {
    if (Array.isArray(arr)) return arr;
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
   * Handles the tags of the selected
   * fileEntries inside a modal.
   */
  var EditTags = /*#__PURE__*/function (_Component) {
    _inherits(EditTags, _Component);

    function EditTags() {
      _classCallCheck(this, EditTags);

      return _possibleConstructorReturn(this, _getPrototypeOf(EditTags).apply(this, arguments));
    }

    _createClass(EditTags, [{
      key: "attached",

      /**
       * @inheritDoc
       */
      value: function attached() {
        this._bulkStatusComponent = Liferay.component(this.namespace + 'BulkStatus');
      }
      /**
       * Close the modal.
       */

    }, {
      key: "close",
      value: function close() {
        this._showModal = false;
      }
      /**
       * @inheritDoc
       */

    }, {
      key: "created",
      value: function created() {
        this.append = true;
      }
      /**
       * Open the modal and get the
       * commont tags.
       */

    }, {
      key: "open",
      value: function open(fileEntries, selectAll, folderId) {
        this.fileEntries = fileEntries;
        this.selectAll = selectAll;
        this.folderId = folderId;
        this._showModal = true;

        this._getCommonTags();
      }
      /**
       * Creates the ajax request.
       *
       * @param {String} url Url of the request
       * @param {Object} bodyData The body of the request
       * @param {Function} callback Callback function
       */

    }, {
      key: "_fetchTagsRequest",
      value: function _fetchTagsRequest(url, method, bodyData) {
        var _this = this;

        var init = {
          body: JSON.stringify(bodyData),
          headers: {
            'content-type': 'application/json'
          },
          method: method
        };
        return (0, _frontendJsWeb.fetch)(this.pathModule + url, init).then(function (response) {
          return response.json();
        })["catch"](function () {
          _this.close();
        });
      }
    }, {
      key: "_getDescription",
      value: function _getDescription(size) {
        if (size === 1) {
          return Liferay.Language.get('you-are-editing-the-tags-for-the-selected-item');
        }

        return Liferay.Util.sub(Liferay.Language.get('you-are-editing-the-common-tags-for-x-items.-select-edit-or-replace-current-tags'), size);
      }
    }, {
      key: "_handleSelectedItemsChange",
      value: function _handleSelectedItemsChange(event) {
        this._commonTags = event.selectedItems;
      }
      /**
       * Gets the common tags for the selected
       * file entries and updates the state.
       *
       * @private
       * @review
       */

    }, {
      key: "_getCommonTags",
      value: function _getCommonTags() {
        var _this2 = this;

        this._loading = true;

        var selection = this._getSelection();

        Promise.all([this._fetchTagsRequest(this.urlTags, 'POST', selection), this._fetchTagsRequest(this.urlSelection, 'POST', selection)]).then(function (_ref) {
          var _ref2 = _slicedToArray(_ref, 2),
              responseTags = _ref2[0],
              responseSelection = _ref2[1];

          if (responseTags && responseSelection) {
            _this2._loading = false;
            _this2._commonTags = _this2._setCommonTags((responseTags.items || []).map(function (item) {
              return item.name;
            }));
            _this2.description = _this2._getDescription(responseSelection.size);
            _this2.multiple = _this2.selectAll || _this2.fileEntries.length > 1;
          }
        });
      }
    }, {
      key: "_handleInputFocus",
      value: function _handleInputFocus(event) {
        var dataProvider = event.target.refs.autocomplete.refs.dataProvider;
        var modal = this.element.querySelector('.modal');

        if (modal && dataProvider && !modal.contains(dataProvider.element)) {
          modal.appendChild(dataProvider.element);
        }
      }
      /**
       * Sync the input radio with the state
       * @param {!Event} event
       * @private
       * @review
       */

    }, {
      key: "_handleRadioChange",
      value: function _handleRadioChange(event) {
        this.append = event.target.value === 'add';
      }
      /**
       * Sends request to backend services
       * to update the tags.
       * @param {!Event} event
       *
       * @private
       * @review
       */

    }, {
      key: "_handleFormSubmit",
      value: function _handleFormSubmit(event) {
        var _this3 = this;

        event.preventDefault();

        var finalTags = this._commonTags.map(function (tag) {
          return tag.label;
        });

        var addedTags = [];

        if (!this.append) {
          addedTags = finalTags;
        } else {
          addedTags = finalTags.filter(function (tag) {
            return _this3._initialTags.indexOf(tag) == -1;
          });
        }

        var removedTags = this._initialTags.filter(function (tag) {
          return finalTags.indexOf(tag) == -1;
        });

        var instance = this;

        this._fetchTagsRequest(this.urlUpdateTags, this.append ? 'PATCH' : 'PUT', {
          documentBulkSelection: this._getSelection(),
          keywordsToAdd: addedTags,
          keywordsToRemove: removedTags
        }).then(function () {
          instance.close();

          if (instance._bulkStatusComponent) {
            instance._bulkStatusComponent.startWatch();
          }
        });
      }
      /**
       * Transforms the tags list in the object needed
       * for the ClayMultiSelect component.
       *
       * @param {List<String>} commonTags
       * @return {List<{label, value}>} new commonTags object list
       */

    }, {
      key: "_setCommonTags",
      value: function _setCommonTags(commonTags) {
        this._initialTags = commonTags;
        var commonTagsObjList = [];

        if (commonTags.length > 0) {
          commonTags.forEach(function (tag) {
            commonTagsObjList.push({
              label: tag,
              value: tag
            });
          });
        }

        return commonTagsObjList;
      }
    }, {
      key: "_getSelection",
      value: function _getSelection() {
        return {
          documentIds: this.fileEntries,
          selectionScope: {
            folderId: this.folderId,
            repositoryId: this.repositoryId,
            selectAll: this.selectAll
          }
        };
      }
    }]);

    return EditTags;
  }(_metalComponent["default"]);
  /**
   * State definition.
   * @ignore
   * @static
   * @type {!Object}
   */

  EditTags.STATE = {
    /**
     * Tags that want to be edited.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {List<String>}
     */
    _commonTags: _metalState.Config.array().value([]).internal(),

    /**
     * Flag that indicate if loading icon must
     * be shown.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {Boolean}
     */
    _loading: _metalState.Config.bool().value(false).internal(),

    /**
     * Flag that indicate if the modal must
     * be shown.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {Boolean}
     */
    _showModal: _metalState.Config.bool().value(false).internal(),

    /**
     * Description
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {String}
     */
    description: _metalState.Config.string(),

    /**
     * List of selected file entries.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {List<String>}
     */
    fileEntries: _metalState.Config.array(),

    /**
     * Folder Id
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {String}
     */
    folderId: _metalState.Config.string(),

    /**
     * Group Ids.
     *
     * @type {List<String>}
     */
    groupIds: _metalState.Config.array().required(),

    /**
     * Flag that indicate if multiple
     * file entries has been selected.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {Boolean}
     */
    multiple: _metalState.Config.bool().value(false),

    /**
     * Portlet's namespace
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {string}
     */
    namespace: _metalState.Config.string().required(),

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
     * RepositoryId
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {String}
     */
    repositoryId: _metalState.Config.string().required(),

    /**
     * Flag that indicate if "select all" checkbox
     * is checked.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {Boolean}
     */
    selectAll: _metalState.Config.bool(),

    /**
     * Path to images.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {String}
     */
    spritemap: _metalState.Config.string().required(),

    /**
     * Url to backend service that provides
     * the selection information.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {String}
     */
    urlSelection: _metalState.Config.string().value('/bulk/v1.0/bulk-selection'),

    /**
     * Url to backend service that provides
     * the common tags info.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {String}
     */
    urlTags: _metalState.Config.string().value('/bulk/v1.0/keywords/common'),

    /**
     * Url to backend service that updates
     * the tags.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {String}
     */
    urlUpdateTags: _metalState.Config.string().value('/bulk/v1.0/keywords/batch')
  }; // Register component

  _metalSoy["default"].register(EditTags, _EditTags["default"]);

  var _default = EditTags;
  exports["default"] = _default;
  //# sourceMappingURL=EditTags.es.js.map
});
//# sourceMappingURL=EditTags.es.js.map