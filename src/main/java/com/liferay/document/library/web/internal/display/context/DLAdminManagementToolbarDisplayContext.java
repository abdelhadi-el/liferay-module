/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.web.internal.constants.DLWebKeys;
// import com.liferay.document.library.web.internal.display.context.logic.DLPortletInstanceSettingsHelper;
// import com.liferay.document.library.web.internal.display.context.util.DLRequestHelper;
import com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryPermission;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
// import com.liferay.document.library.web.internal.util.DLTrashUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.trash.service.TrashEntryLocalServiceUtil;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.util.GetterUtil;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Alejandro Tard??n
 */
public class DLAdminManagementToolbarDisplayContext {

	public DLAdminManagementToolbarDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		HttpServletRequest httpServletRequest,
		DLAdminDisplayContext dlAdminDisplayContext) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_httpServletRequest = httpServletRequest;
		_dlAdminDisplayContext = dlAdminDisplayContext;

		_currentURLObj = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);

		// _dlRequestHelper = new DLRequestHelper(_httpServletRequest);

		// _dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(
		// 	_dlRequestHelper);

		// _dlTrashUtil = (DLTrashUtil)_httpServletRequest.getAttribute(
		// 	DLWebKeys.DOCUMENT_LIBRARY_TRASH_UTIL);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public boolean isTrashEnabled() throws PortalException {
		Folder folder = _getFolder();

		if (((folder == null) ||
			folder.isRepositoryCapabilityProvided(TrashCapability.class)) &&
			_isTrashEnabled(_themeDisplay.getScopeGroupId(), _getRepositoryId())) {

			return true;
		}

		return false;
	}

	private Folder _getFolder() {
		if (_folder != null) {
			return _folder;
		}

		ResultRow row = (ResultRow)_httpServletRequest.getAttribute(
			WebKeys.SEARCH_CONTAINER_RESULT_ROW);

		if (row == null) {
			_folder = (Folder)_httpServletRequest.getAttribute(
				"info_panel.jsp-folder");
		}
		else {
			if (row.getObject() instanceof Folder) {
				_folder = (Folder)row.getObject();
			}
		}

		return _folder;
	}

	private boolean _isTrashEnabled(long groupId, long repositoryId) {
		try {
			// 1. Repositories externes = pas de corbeille
			if (repositoryId != groupId) {
				return false;
			}
			
			// 2. Vérifier que le groupe existe et est actif
			Group group = GroupLocalServiceUtil.fetchGroup(groupId);
			if (group == null || !group.isActive()) {
				return false;
			}
			
			// 3. Vérifications multiples pour s'assurer que la corbeille est vraiment activée
			
			// 3a. Vérifier via TrashHandler
			TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
				DLFolder.class.getName());
			if (trashHandler == null) {
				return false;
			}
			
			// 3b. Vérifier qu'on peut accéder aux services de corbeille
			try {
				TrashEntryLocalServiceUtil.getEntriesCount(groupId);
			} catch (Exception e) {
				return false;
			}
			
			// 3c. Vérifier la configuration globale si possible
			try {
				CompanyLocalServiceUtil.getCompany(group.getCompanyId());
				// Si on arrive ici, la company existe et les services sont disponibles
			} catch (Exception e) {
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}


	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();
		DLPortletInstanceSettings dlPortletInstanceSettings = 
			DLPortletInstanceSettings.getInstance(_themeDisplay.getLayout(), portletDisplay.getId());

		if (!dlPortletInstanceSettings.isShowActions()) {
			return null;
		}

		return new DropdownItemList() {
			{
				boolean stagedActions = false;

				Group scopeGroup = _themeDisplay.getScopeGroup();
				StagingGroupHelper stagingGroupHelper =
					StagingGroupHelperUtil.getStagingGroupHelper();

				if (!stagingGroupHelper.isLiveGroup(scopeGroup) ||
					!stagingGroupHelper.isStagedPortlet(
						scopeGroup, DLPortletKeys.DOCUMENT_LIBRARY)) {

					stagedActions = true;
				}

				if (stagedActions) {
					add(
						dropdownItem -> {
							dropdownItem.putData("action", "download");
							dropdownItem.setIcon("download");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "download"));
							dropdownItem.setQuickAction(true);
						});
				}

				User user = _themeDisplay.getUser();

				if (stagedActions && !user.isDefaultUser()) {
					add(
						dropdownItem -> {
							dropdownItem.putData("action", "move");
							dropdownItem.setIcon("move-folder");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "move"));
							dropdownItem.setQuickAction(true);
						});

					// Chintan - Copy Functionality
					add(
						dropdownItem -> {
							dropdownItem.putData("action", "copy");
							dropdownItem.setIcon("documents-and-media");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "copy"));
							dropdownItem.setQuickAction(true);
						});

					boolean enableOnBulk = _isEnableOnBulk();

					add(
						dropdownItem -> {
							dropdownItem.putData("action", "editTags");

							if (enableOnBulk) {
								dropdownItem.putData(
									"enableOnBulk", Boolean.TRUE.toString());
							}

							dropdownItem.setIcon("tag");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "edit-tags"));
							dropdownItem.setQuickAction(true);
						});

					if (_hasValidAssetVocabularies(
							_themeDisplay.getScopeGroupId())) {

						add(
							dropdownItem -> {
								dropdownItem.putData(
									"action", "editCategories");

								if (enableOnBulk) {
									dropdownItem.putData(
										"enableOnBulk",
										Boolean.TRUE.toString());
								}

								dropdownItem.setIcon("categories");
								dropdownItem.setLabel(
									LanguageUtil.get(
										_httpServletRequest,
										"edit-categories"));
								dropdownItem.setQuickAction(true);
							});
					}
				}

				if (!user.isDefaultUser()) {
					add(
						dropdownItem -> {
							dropdownItem.putData("action", "deleteEntries");

							if ( isTrashEnabled() /*_dlTrashUtil.isTrashEnabled(
									scopeGroup.getGroupId(),
									_getRepositoryId()) */ ) {

								dropdownItem.setIcon("trash");
								dropdownItem.setLabel(
									LanguageUtil.get(
										_httpServletRequest,
										"move-to-recycle-bin"));
							}
							else {
								dropdownItem.setIcon("times-circle");
								dropdownItem.setLabel(
									LanguageUtil.get(
										_httpServletRequest, "delete"));
							}

							dropdownItem.setQuickAction(true);
						});
				}

				if (stagedActions && !user.isDefaultUser()) {
					add(
						dropdownItem -> {
							dropdownItem.putData("action", "checkin");
							dropdownItem.setIcon("unlock");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "checkin"));
							dropdownItem.setQuickAction(false);
						});

					add(
						dropdownItem -> {
							dropdownItem.putData("action", "checkout");
							dropdownItem.setIcon("lock");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "checkout[document]"));
							dropdownItem.setQuickAction(false);
						});
				}
			}
		};
	}

	public List<String> getAvailableActions(FileEntry fileEntry)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		if (DLFileEntryPermission.contains(
				permissionChecker, fileEntry, ActionKeys.DELETE)) {

			availableActions.add("deleteEntries");
		}

		if (DLFileEntryPermission.contains(
				permissionChecker, fileEntry, ActionKeys.UPDATE)) {

			availableActions.add("move");
			// Chintan - Copy Functionality
			availableActions.add("copy");

			if (fileEntry.isCheckedOut()) {
				availableActions.add("checkin");
			}
			else {
				availableActions.add("checkout");
			}

			if (!RepositoryUtil.isExternalRepository(
					fileEntry.getRepositoryId()) &&
				!_hasWorkflowDefinitionLink(fileEntry)) {

				if (_hasValidAssetVocabularies(
						_themeDisplay.getScopeGroupId())) {

					availableActions.add("editCategories");
				}

				availableActions.add("editTags");
			}
		}

		if (DLFileEntryPermission.contains(
				permissionChecker, fileEntry, ActionKeys.VIEW)) {

			availableActions.add("download");
		}

		return availableActions;
	}

	public List<String> getAvailableActions(Folder folder)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		if (DLFolderPermission.contains(
				permissionChecker, folder, ActionKeys.DELETE)) {

			availableActions.add("deleteEntries");
		}

		if (DLFolderPermission.contains(
				permissionChecker, folder, ActionKeys.UPDATE) &&
			!folder.isMountPoint()) {

			availableActions.add("move");
			// Chintan - Copy Functionality
			availableActions.add("copy");
		}

		if (DLFolderPermission.contains(
				permissionChecker, folder, ActionKeys.VIEW) &&
			!RepositoryUtil.isExternalRepository(folder.getRepositoryId())) {

			availableActions.add("download");
		}

		return availableActions;
	}

	public String getClearResultsURL() {
		PortletURL clearResultsURL = _liferayPortletResponse.createRenderURL();

		clearResultsURL.setParameter(
			"mvcRenderCommandName", "/document_library/view");
		clearResultsURL.setParameter(
			"folderId", String.valueOf(_getFolderId()));

		return clearResultsURL.toString();
	}

	public String getComponentId() {
		return _liferayPortletResponse.getNamespace() +
			"entriesManagementToolbar";
	}

	public CreationMenu getCreationMenu() {
		String portletName = _liferayPortletRequest.getPortletName();

		if (!portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {
			return null;
		}

		PortletToolbarContributor dlPortletToolbarContributor =
			(PortletToolbarContributor)_httpServletRequest.getAttribute(
				DLWebKeys.DOCUMENT_LIBRARY_PORTLET_TOOLBAR_CONTRIBUTOR);

		List<Menu> menus = dlPortletToolbarContributor.getPortletTitleMenus(
			_liferayPortletRequest, _liferayPortletResponse);

		if (menus.isEmpty()) {
			return null;
		}

		CreationMenu creationMenu = new CreationMenu();

		creationMenu.setItemsIconAlignment("left");

		for (Menu menu : menus) {
			List<URLMenuItem> urlMenuItems =
				(List<URLMenuItem>)(List<?>)menu.getMenuItems();

			for (URLMenuItem urlMenuItem : urlMenuItems) {
				creationMenu.addDropdownItem(
					dropdownItem -> {
						dropdownItem.setData(urlMenuItem.getData());
						dropdownItem.setHref(urlMenuItem.getURL());
						dropdownItem.setIcon(urlMenuItem.getIcon());
						dropdownItem.setLabel(urlMenuItem.getLabel());
						dropdownItem.setSeparator(urlMenuItem.hasSeparator());
					});
			}
		}

		return creationMenu;
	}

	public List<DropdownItem> getFilterDropdownItems() {
		if (_isSearch()) {
			return null;
		}

		return new DropdownItemList() {
			{
				addGroup(
					dropdownGroupItem -> {
						dropdownGroupItem.setDropdownItems(
							_getFilterNavigationDropdownItems());
						dropdownGroupItem.setLabel(
							LanguageUtil.get(
								_httpServletRequest, "filter-by-navigation"));
					});

				addGroup(
					dropdownGroupItem -> {
						dropdownGroupItem.setDropdownItems(
							_getOrderByDropdownItems());
						dropdownGroupItem.setLabel(
							LanguageUtil.get(_httpServletRequest, "order-by"));
					});
			}
		};
	}

	public List<LabelItem> getFilterLabelItems() {
		return new LabelItemList() {
			{
				long fileEntryTypeId = _getFileEntryTypeId();

				if (fileEntryTypeId != -1) {
					add(
						labelItem -> {
							PortletURL removeLabelURL = PortletURLUtil.clone(
								_currentURLObj, _liferayPortletResponse);

							removeLabelURL.setParameter(
								"fileEntryTypeId", (String)null);

							labelItem.putData(
								"removeLabelURL", removeLabelURL.toString());

							labelItem.setCloseable(true);

							String fileEntryTypeName = LanguageUtil.get(
								_httpServletRequest, "basic-document");

							if (fileEntryTypeId !=
									DLFileEntryTypeConstants.
										FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

								DLFileEntryType fileEntryType =
									DLFileEntryTypeLocalServiceUtil.
										getFileEntryType(fileEntryTypeId);

								fileEntryTypeName = fileEntryType.getName(
									_httpServletRequest.getLocale());
							}

							String label = String.format(
								"%s: %s",
								LanguageUtil.get(
									_httpServletRequest, "document-type"),
								fileEntryTypeName);

							labelItem.setLabel(label);
						});
				}

				String navigation = _getNavigation();

				if (navigation.equals("mine")) {
					add(
						labelItem -> {
							PortletURL removeLabelURL = PortletURLUtil.clone(
								_currentURLObj, _liferayPortletResponse);

							removeLabelURL.setParameter(
								"navigation", (String)null);

							labelItem.putData(
								"removeLabelURL", removeLabelURL.toString());

							labelItem.setCloseable(true);

							User user = _themeDisplay.getUser();

							String label = String.format(
								"%s: %s",
								LanguageUtil.get(_httpServletRequest, "owner"),
								HtmlUtil.escape(user.getFullName()));

							labelItem.setLabel(label);
						});
				}
			}
		};
	}

	public PortletURL getSearchURL() {
		PortletURL searchURL = _liferayPortletResponse.createRenderURL();

		searchURL.setParameter(
			"mvcRenderCommandName", "/document_library/search");

		long repositoryId = _getRepositoryId();

		searchURL.setParameter("repositoryId", String.valueOf(repositoryId));

		long searchRepositoryId = ParamUtil.getLong(
			_httpServletRequest, "searchRepositoryId", repositoryId);

		searchURL.setParameter(
			"searchRepositoryId", String.valueOf(searchRepositoryId));

		long folderId = _getFolderId();

		searchURL.setParameter("folderId", String.valueOf(folderId));

		long searchFolderId = ParamUtil.getLong(
			_httpServletRequest, "searchFolderId", folderId);

		searchURL.setParameter(
			"searchFolderId", String.valueOf(searchFolderId));

		searchURL.setParameter("showSearchInfo", Boolean.TRUE.toString());

		return searchURL;
	}

	public String getSortingOrder() {
		if (_isSearch()) {
			return null;
		}

		return _dlAdminDisplayContext.getOrderByType();
	}

	public PortletURL getSortingURL() {
		if (_isSearch()) {
			return null;
		}

		PortletURL sortingURL = _getCurrentSortingURL();

		sortingURL.setParameter(
			"orderByType",
			Objects.equals(_getOrderByType(), "asc") ? "desc" : "asc");

		return sortingURL;
	}

	public int getTotalItems() {
		SearchContainer searchContainer =
			_dlAdminDisplayContext.getSearchContainer();

		return searchContainer.getTotal();
	}

	public ViewTypeItemList getViewTypes() {
		if (_isSearch()) {
			return null;
		}

		String navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "home");

		int curEntry = ParamUtil.getInteger(_httpServletRequest, "curEntry");
		int deltaEntry = ParamUtil.getInteger(
			_httpServletRequest, "deltaEntry");

		long folderId = _getFolderId();

		long fileEntryTypeId = _getFileEntryTypeId();

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		PortletURL displayStyleURL = _liferayPortletResponse.createRenderURL();

		String mvcRenderCommandName = "/document_library/search";

		if (Validator.isNull(keywords)) {
			if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				mvcRenderCommandName = "/document_library/view";
			}
			else {
				mvcRenderCommandName = "/document_library/view_folder";
			}
		}

		displayStyleURL.setParameter(
			"mvcRenderCommandName", mvcRenderCommandName);

		displayStyleURL.setParameter(
			"navigation", HtmlUtil.escapeJS(navigation));

		if (curEntry > 0) {
			displayStyleURL.setParameter("curEntry", String.valueOf(curEntry));
		}

		if (deltaEntry > 0) {
			displayStyleURL.setParameter(
				"deltaEntry", String.valueOf(deltaEntry));
		}

		displayStyleURL.setParameter("folderId", String.valueOf(folderId));

		if (fileEntryTypeId != -1) {
			displayStyleURL.setParameter(
				"fileEntryTypeId", String.valueOf(fileEntryTypeId));
		}

		return new ViewTypeItemList(displayStyleURL, _getDisplayStyle()) {
			{
				if (ArrayUtil.contains(_getDisplayViews(), "icon")) {
					addCardViewTypeItem();
				}

				if (ArrayUtil.contains(_getDisplayViews(), "descriptive")) {
					addListViewTypeItem();
				}

				if (ArrayUtil.contains(_getDisplayViews(), "list")) {
					addTableViewTypeItem();
				}
			}
		};
	}

	public boolean isDisabled() {
		try {
			int count =
				DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcutsCount(
					_getRepositoryId(), _getFolderId(),
					WorkflowConstants.STATUS_ANY, true);

			if (count <= 0) {
				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	public boolean isSelectable() {
		return true;
	}

	// public boolean isShowSearch() {
	// 	PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();
    // 	DLPortletInstanceSettings dlPortletInstanceSettings = 
	// 	 DLPortletInstanceSettings.getInstance(_themeDisplay.getLayout(), portletDisplay.getId());
    
	// 	return dlPortletInstanceSettings.isShowSearch();
	// 	// return _dlPortletInstanceSettingsHelper.isShowSearch();
	// }

	// public boolean isShowSearch() {
	// 	// Accès direct à la configuration du portlet
	// 	PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();
		
	// 	// Récupération des préférences comme dans les autres méthodes
	// 	PortletPreferences portletPreferences = portletDisplay.getPortletSetup();
		
	// 	// La clé exacte utilisée par l'ancien helper
	// 	return GetterUtil.getBoolean(
	// 		portletPreferences.getValue("showSearch", "true"), 
	// 		true
	// 	);
	// }

	// TODO check this
	public boolean isShowSearch() {
		try {
			PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();
			
			// Accès via PortletRequest comme dans les autres patterns
			PortletRequest portletRequest = (PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
			
			if (portletRequest != null) {
				PortletPreferences portletPreferences = portletRequest.getPreferences();
				return GetterUtil.getBoolean(
					portletPreferences.getValue("showSearch", "true"), 
					true
				);
			}
			
			return true; // Valeur par défaut
			
		} catch (Exception e) {
			return true;
		}
	}

	private PortletURL _getCurrentSortingURL() {
		int deltaEntry = ParamUtil.getInteger(
			_httpServletRequest, "deltaEntry");

		PortletURL sortingURL = _liferayPortletResponse.createRenderURL();

		long folderId = _getFolderId();

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			sortingURL.setParameter(
				"mvcRenderCommandName", "/document_library/view");
		}
		else {
			sortingURL.setParameter(
				"mvcRenderCommandName", "/document_library/view_folder");
		}

		sortingURL.setParameter("navigation", _getNavigation());

		if (deltaEntry > 0) {
			sortingURL.setParameter("deltaEntry", String.valueOf(deltaEntry));
		}

		sortingURL.setParameter("folderId", String.valueOf(folderId));

		long fileEntryTypeId = _getFileEntryTypeId();

		sortingURL.setParameter(
			"fileEntryTypeId", String.valueOf(fileEntryTypeId));

		return sortingURL;
	}

	private String _getDisplayStyle() {
		return _dlAdminDisplayContext.getDisplayStyle();
	}

	private String[] _getDisplayViews() {
		// DLPortletInstanceSettings dlPortletInstanceSettings =
		// 	_dlRequestHelper.getDLPortletInstanceSettings();
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();
		DLPortletInstanceSettings dlPortletInstanceSettings = null;

		try {
			dlPortletInstanceSettings = DLPortletInstanceSettings.getInstance(_themeDisplay.getLayout(), portletDisplay.getId());
		} catch (PortalException e) {
			_log.warn("Failed to get DLPortletInstanceSettings", e);
			return null; // Default to false if exception occurs
		}

		return dlPortletInstanceSettings.getDisplayViews();
	}


	private long _getFileEntryTypeId() {
		return ParamUtil.getLong(_httpServletRequest, "fileEntryTypeId", -1);
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems() {
		long fileEntryTypeId = _getFileEntryTypeId();
		final String navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "home");

		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive(
							navigation.equals("home") &&
							(fileEntryTypeId == -1));

						PortletURL viewAllDocumentsURL = PortletURLUtil.clone(
							_currentURLObj, _liferayPortletResponse);

						viewAllDocumentsURL.setParameter(
							"mvcRenderCommandName", "/document_library/view");
						viewAllDocumentsURL.setParameter("navigation", "home");
						viewAllDocumentsURL.setParameter(
							"browseBy", (String)null);
						viewAllDocumentsURL.setParameter(
							"fileEntryTypeId", (String)null);

						dropdownItem.setHref(viewAllDocumentsURL);

						dropdownItem.setLabel(
							LanguageUtil.get(_httpServletRequest, "all"));
					});

				if (_themeDisplay.isSignedIn()) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(navigation.equals("mine"));

							PortletURL viewMyDocumentsURL =
								PortletURLUtil.clone(
									_currentURLObj, _liferayPortletResponse);

							viewMyDocumentsURL.setParameter(
								"mvcRenderCommandName",
								"/document_library/view");
							viewMyDocumentsURL.setParameter(
								"navigation", "mine");

							dropdownItem.setHref(viewMyDocumentsURL);

							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "mine"));
						});
				}

				add(
					dropdownItem -> {
						dropdownItem.setActive(fileEntryTypeId != -1);

						dropdownItem.putData(
							"action", "openDocumentTypesSelector");

						String label = LanguageUtil.get(
							_httpServletRequest, "document-type");

						if (fileEntryTypeId != -1) {
							String fileEntryTypeName = LanguageUtil.get(
								_httpServletRequest, "basic-document");

							if (fileEntryTypeId !=
									DLFileEntryTypeConstants.
										FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

								DLFileEntryType fileEntryType =
									DLFileEntryTypeLocalServiceUtil.
										getFileEntryType(fileEntryTypeId);

								fileEntryTypeName = fileEntryType.getName(
									_httpServletRequest.getLocale());
							}

							label = String.format(
								"%s: %s", label, fileEntryTypeName);
						}

						dropdownItem.setLabel(label);
					});
			}
		};
	}

	private long _getFolderId() {
		if (_isSearch()) {
			return ParamUtil.getLong(_httpServletRequest, "folderId");
		}

		return _dlAdminDisplayContext.getFolderId();
	}

	private String _getNavigation() {
		return ParamUtil.getString(_httpServletRequest, "navigation", "home");
	}

	private String _getOrderByCol() {
		return _dlAdminDisplayContext.getOrderByCol();
	}

	private List<DropdownItem> _getOrderByDropdownItems() {
		final Map<String, String> orderColumns = HashMapBuilder.put(
			"creationDate", "create-date"
		).build();

		if (_getFileEntryTypeId() == -1) {
			orderColumns.put("downloads", "downloads");
		}

		orderColumns.put("modifiedDate", "modified-date");
		orderColumns.put("size", "size");
		orderColumns.put("title", "title");

		return new DropdownItemList() {
			{
				for (Map.Entry<String, String> orderByColEntry :
						orderColumns.entrySet()) {

					String orderByCol = orderByColEntry.getKey();

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								orderByCol.equals(_getOrderByCol()));
							dropdownItem.setHref(
								_getCurrentSortingURL(), "orderByCol",
								orderByCol);
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									orderByColEntry.getValue()));
						});
				}
			}
		};
	}

	private String _getOrderByType() {
		return _dlAdminDisplayContext.getOrderByType();
	}

	private long _getRepositoryId() {
		return _dlAdminDisplayContext.getRepositoryId();
	}

	private boolean _hasValidAssetVocabularies(long scopeGroupId)
		throws PortalException {

		if (_hasValidAssetVocabularies != null) {
			return _hasValidAssetVocabularies;
		}

		List<AssetVocabulary> assetVocabularies =
			AssetVocabularyServiceUtil.getGroupVocabularies(
				PortalUtil.getCurrentAndAncestorSiteGroupIds(scopeGroupId));

		Stream<AssetVocabulary> stream = assetVocabularies.stream();

		_hasValidAssetVocabularies = stream.anyMatch(
			assetVocabulary -> {
				if (!assetVocabulary.isAssociatedToClassNameId(
						ClassNameLocalServiceUtil.getClassNameId(
							DLFileEntry.class.getName()))) {

					return false;
				}

				int count =
					AssetCategoryServiceUtil.getVocabularyCategoriesCount(
						assetVocabulary.getGroupId(),
						assetVocabulary.getVocabularyId());

				if (count > 0) {
					return true;
				}

				return false;
			});

		return _hasValidAssetVocabularies;
	}

	private boolean _hasWorkflowDefinitionLink(FileEntry fileEntry)
		throws PortalException {

		if (!(fileEntry.getModel() instanceof DLFileEntry)) {
			return false;
		}

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		if (_hasWorkflowDefinitionLink(
				dlFileEntry.getFolderId(), dlFileEntry.getFileEntryTypeId())) {

			return true;
		}

		return false;
	}

	private boolean _hasWorkflowDefinitionLink(
			long folderId, long fileEntryTypeId)
		throws PortalException {

			return DLUtil.hasWorkflowDefinitionLink(
				_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId(),
				folderId, fileEntryTypeId);
	}

	private boolean _isEnableOnBulk() throws PortalException {
		long folderId = ParamUtil.getLong(_httpServletRequest, "folderId");

		if (_hasWorkflowDefinitionLink(
				folderId, DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL)) {

			return false;
		}

		return true;
	}

	private boolean _isSearch() {
		return _dlAdminDisplayContext.isSearch();
	}

	private final PortletURL _currentURLObj;
	private final DLAdminDisplayContext _dlAdminDisplayContext;
	// private final DLPortletInstanceSettingsHelper
	// 	_dlPortletInstanceSettingsHelper;
	// private final DLRequestHelper _dlRequestHelper;
	// private final DLTrashUtil _dlTrashUtil;
	private Boolean _hasValidAssetVocabularies;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;
	private Folder _folder;

	private static final Log _log = LogFactoryUtil.getLog(
		DLAdminManagementToolbarDisplayContext.class);

}