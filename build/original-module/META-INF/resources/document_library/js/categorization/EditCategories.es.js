Liferay.Loader.define("document-library-web@4.0.86/document_library/js/categorization/EditCategories.es", ['module', 'exports', 'require', 'asset-taglib/asset_categories_selector/AssetCategoriesSelector.es', 'frontend-taglib-clay$clay-multi-select', 'frontend-taglib-clay$clay-radio', 'frontend-js-web/liferay/compat/modal/Modal.es', 'frontend-js-web', 'frontend-js-metal-web$metal-component', 'frontend-js-metal-web$metal-soy', 'frontend-js-metal-web$metal-state', './EditCategories.soy'], function (module, exports, require) {
  var define = undefined;
  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports["default"] = void 0;

  require("asset-taglib/asset_categories_selector/AssetCategoriesSelector.es");

  require("frontend-taglib-clay$clay-multi-select");

  require("frontend-taglib-clay$clay-radio");

  require("frontend-js-web/liferay/compat/modal/Modal.es");

  var _frontendJsWeb = require("frontend-js-web");

  var _metalComponent = _interopRequireDefault(require("frontend-js-metal-web$metal-component"));

  var _metalSoy = _interopRequireDefault(require("frontend-js-metal-web$metal-soy"));

  var _metalState = require("frontend-js-metal-web$metal-state");

  var _EditCategories = _interopRequireDefault(require("./EditCategories.soy"));

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
   * Handles the categories of the selected
   * fileEntries inside a modal.
   */
  var EditCategories = /*#__PURE__*/function (_Component) {
    _inherits(EditCategories, _Component);

    function EditCategories() {
      _classCallCheck(this, EditCategories);

      return _possibleConstructorReturn(this, _getPrototypeOf(EditCategories).apply(this, arguments));
    }

    _createClass(EditCategories, [{
      key: "attached",

      /**
       * @inheritDoc
       */
      value: function attached() {
        this._assetVocabularyCategories = new Map();
        this._bulkStatusComponent = Liferay.component(this.namespace + 'BulkStatus');
      }
      /**
       * Close the modal.
       */

    }, {
      key: "close",
      value: function close() {
        this.showModal = false;
      }
      /**
       * @inheritDoc
       */

    }, {
      key: "created",
      value: function created() {
        this.append = true;
        this.dataSource = [];
        this.urlCategories = "/bulk/v1.0/sites/".concat(this.groupIds[0], "/taxonomy-vocabularies/common");
        this._feedbackErrorClass = 'form-feedback-item';
        this._requiredVocabularyErrorMarkupText = '<div class="' + this._feedbackErrorClass + '">' + Liferay.Language.get('this-field-is-required') + '</div>';
      }
      /**
       * Open the modal and get the
       * commont categories.
       */

    }, {
      key: "open",
      value: function open(fileEntries, selectAll, folderId) {
        this.fileEntries = fileEntries;
        this.selectAll = selectAll;
        this.folderId = folderId;
        this.showModal = true;

        this._getCommonCategories();
      }
      /**
       * Checks if the vocabulary is empty or not.
       *
       * @param  {String} vocabularyId
       * @return {Boolean} true if it has a category, false if is empty.
       */

    }, {
      key: "_checkRequiredVocabulary",
      value: function _checkRequiredVocabulary(vocabularyId) {
        var inputNode = this._getVocabularyInputNode(vocabularyId);

        var valid = true;

        if (inputNode.value) {
          inputNode.parentElement.parentElement.classList.remove('has-error');
        } else {
          inputNode.parentElement.parentElement.classList.add('has-error');
          var feedbackErrorNode = inputNode.parentElement.querySelector('.' + this._feedbackErrorClass);

          if (!feedbackErrorNode) {
            inputNode.parentElement.insertAdjacentHTML('beforeend', this._requiredVocabularyErrorMarkupText);
          }

          valid = false;
        }

        return valid;
      }
      /**
       * Checks if the vocabulary have errors
       *
       * @private
       * @review
       * @return {Boolean} true if it has a error, false if has not error.
       */

    }, {
      key: "_checkErrors",
      value: function _checkErrors() {
        return !!this.element.querySelector('.has-error');
      }
      /**
       * Creates the ajax request.
       *
       * @param {String} url Url of the request
       * @param {Object} bodyData The body of the request
       * @param {Function} callback Callback function
       */

    }, {
      key: "_fetchCategoriesRequest",
      value: function _fetchCategoriesRequest(url, method, bodyData) {
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
      /**
       * Gets the common categories for the selected
       * file entries and updates the state.
       *
       * @private
       * @review
       */

    }, {
      key: "_getCommonCategories",
      value: function _getCommonCategories() {
        var _this2 = this;

        this.loading = true;

        var selection = this._getSelection();

        Promise.all([this._fetchCategoriesRequest(this.urlCategories, 'POST', selection), this._fetchCategoriesRequest(this.urlSelection, 'POST', selection)]).then(function (_ref) {
          var _ref2 = _slicedToArray(_ref, 2),
              responseCategories = _ref2[0],
              responseSelection = _ref2[1];

          if (responseCategories && responseSelection) {
            _this2.loading = false;
            _this2.description = _this2._getDescription(responseSelection.size);
            _this2.multiple = _this2.selectAll || _this2.fileEntries.length > 1;
            _this2.vocabularies = _this2._parseVocabularies(responseCategories.items || []);
          }
        });
      }
    }, {
      key: "_getDescription",
      value: function _getDescription(size) {
        if (size === 1) {
          return Liferay.Language.get('you-are-editing-the-categories-for-the-selected-item');
        }

        return Liferay.Util.sub(Liferay.Language.get('you-are-editing-the-common-categories-for-x-items.-select-edit-or-replace-current-categories'), size);
      }
      /**
       * Get all the categoryIds selected for all
       * the vocabularies.
       *
       * @return {List<Long>} List of categoryIds.
       */

    }, {
      key: "_getFinalCategories",
      value: function _getFinalCategories() {
        var finalCategories = [];

        this._assetVocabularyCategories.forEach(function (category) {
          var categoryIds = category.map(function (item) {
            return item.value;
          });
          finalCategories = finalCategories.concat(categoryIds);
        });

        return finalCategories;
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
      /**
       * Gets the input where categories are saved for a vocabulary.
       *
       * @param  {String} vocabularyId [description]
       * @return {DOMElement} input node.
       */

    }, {
      key: "_getVocabularyInputNode",
      value: function _getVocabularyInputNode(vocabularyId) {
        return document.getElementById(this.namespace + this.hiddenInput + vocabularyId);
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
    }, {
      key: "_handleSelectedItemsChange",
      value: function _handleSelectedItemsChange(event) {
        var _this3 = this;

        var vocabularyId = event.vocabularyId;

        this._assetVocabularyCategories.set(vocabularyId, event.selectedItems);

        if (this._requiredVocabularies.includes(parseInt(vocabularyId, 10))) {
          setTimeout(function () {
            _this3._checkRequiredVocabulary(vocabularyId);
          }, 0);
        }
      }
      /**
       * Sends request to backend services
       * to update the categories.
       * @param {!Event} event
       *
       * @private
       * @review
       */

    }, {
      key: "_handleFormSubmit",
      value: function _handleFormSubmit(event) {
        var _this4 = this;

        event.preventDefault();
        setTimeout(function () {
          if (_this4._checkErrors()) {
            return;
          }

          var finalCategories = _this4._getFinalCategories();

          var addedCategories = [];

          if (!_this4.append) {
            addedCategories = finalCategories;
          } else {
            addedCategories = finalCategories.filter(function (categoryId) {
              return _this4.initialCategories.indexOf(categoryId) == -1;
            });
          }

          var removedCategories = _this4.initialCategories.filter(function (category) {
            return finalCategories.indexOf(category) == -1;
          });

          var instance = _this4;

          _this4._fetchCategoriesRequest(_this4.urlUpdateCategories, _this4.append ? 'PATCH' : 'PUT', {
            documentBulkSelection: _this4._getSelection(),
            taxonomyCategoryIdsToAdd: addedCategories,
            taxonomyCategoryIdsToRemove: removedCategories
          }).then(function () {
            instance.close();

            if (instance._bulkStatusComponent) {
              instance._bulkStatusComponent.startWatch();
            }
          });
        }, 250);
      }
    }, {
      key: "_parseVocabularies",
      value: function _parseVocabularies(vocabularies) {
        var _this5 = this;

        var initialCategories = [];
        var requiredVocabularies = [];
        var vocabulariesList = [];
        vocabularies.forEach(function (vocabulary) {
          var categories = _this5._parseCategories(vocabulary.taxonomyCategories || []);

          var categoryIds = categories.map(function (item) {
            return item.value;
          });
          var obj = {
            id: vocabulary.taxonomyVocabularyId.toString(),
            required: vocabulary.required,
            selectedCategoryIds: categoryIds.join(','),
            selectedItems: categories,
            singleSelect: !vocabulary.multiValued,
            title: vocabulary.name
          };
          vocabulariesList.push(obj);

          if (vocabulary.required) {
            requiredVocabularies.push(vocabulary.taxonomyVocabularyId);
          }

          initialCategories = initialCategories.concat(categoryIds);
        });
        this.initialCategories = initialCategories;
        this._requiredVocabularies = requiredVocabularies;
        return vocabulariesList;
      }
      /**
       * Transforms the categories list in the object needed
       * for the ClayMultiSelect component.
       *
       * @param {List<Long, String>} categories
       * @return {List<{label, value}>} new commonItems object list
       */

    }, {
      key: "_parseCategories",
      value: function _parseCategories(categories) {
        var categoriesObjList = [];

        if (categories.length > 0) {
          categories.forEach(function (item) {
            var itemObj = {
              label: item.taxonomyCategoryName,
              value: item.taxonomyCategoryId
            };
            categoriesObjList.push(itemObj);
          });
        }

        return categoriesObjList;
      }
    }]);

    return EditCategories;
  }(_metalComponent["default"]);
  /**
   * State definition.
   * @ignore
   * @static
   * @type {!Object}
   */

  EditCategories.STATE = {
    /**
     * Description
     *
     * @instance
     * @memberof EditCategories
     * @review
     * @type {String}
     */
    description: _metalState.Config.string(),

    /**
     * List of selected file entries.
     *
     * @instance
     * @memberof EditCategories
     * @review
     * @type {List<String>}
     */
    fileEntries: _metalState.Config.array(),

    /**
     * Folder Id
     *
     * @instance
     * @memberof EditCategories
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
     * Hidden input name
     *
     * @type {String}
     */
    hiddenInput: _metalState.Config.string().value('assetCategoryIds_').internal(),

    /**
     * Original categoryIds
     *
     * @type {List<Long>}
     */
    initialCategories: _metalState.Config.array().internal(),

    /**
     * Flag that indicate if loading icon must
     * be shown.
     *
     * @instance
     * @memberof EditCategories
     * @review
     * @type {Boolean}
     */
    loading: _metalState.Config.bool().value(false).internal(),

    /**
     * Flag that indicate if multiple
     * file entries has been selected.
     *
     * @instance
     * @memberof EditCategories
     * @review
     * @type {Boolean}
     */
    multiple: _metalState.Config.bool().value(false),

    /**
     * Portlet's namespace
     *
     * @instance
     * @memberof EditCategories
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
     * @memberof EditCategories
     * @review
     * @type {String}
     */
    repositoryId: _metalState.Config.string().required(),

    /**
     * Flag that indicate if "select all" checkbox
     * is checked.
     *
     * @instance
     * @memberof EditCategories
     * @review
     * @type {Boolean}
     */
    selectAll: _metalState.Config.bool(),

    /**
     * Url to the categories selector page
     * @type {String}
     */
    selectCategoriesUrl: _metalState.Config.string().required(),

    /**
     * Flag that indicate if the modal must
     * be shown.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {Boolean}
     */
    showModal: _metalState.Config.bool().value(false).internal(),

    /**
     * Path to images.
     *
     * @instance
     * @memberof EditCategories
     * @review
     * @type {String}
     */
    spritemap: _metalState.Config.string().required(),

    /**
     * Url to backend service that provides
     * the common categories info.
     *
     * @instance
     * @memberof EditCategories
     * @review
     * @type {String}
     */
    urlCategories: _metalState.Config.string(),

    /**
     * Url to backend service that provides
     * the selection description.
     *
     * @instance
     * @memberof EditTags
     * @review
     * @type {String}
     */
    urlSelection: _metalState.Config.string().value('/bulk/v1.0/bulk-selection'),

    /**
     * Url to backend service that updates
     * the categories.
     *
     * @instance
     * @memberof EditCategories
     * @review
     * @type {String}
     */
    urlUpdateCategories: _metalState.Config.string().value('/bulk/v1.0/taxonomy-categories/batch'),

    /**
     * List of vocabularies
     *
     * @type {Array}
     */
    vocabularies: _metalState.Config.array().value([])
  }; // Register component

  _metalSoy["default"].register(EditCategories, _EditCategories["default"]);

  var _default = EditCategories;
  exports["default"] = _default;
  //# sourceMappingURL=EditCategories.es.js.map
});
//# sourceMappingURL=EditCategories.es.js.map