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

package com.liferay.document.library.web.internal.display.context.logic;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLUIItemKeys;
import com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.versioning.VersioningStrategy;
import com.liferay.document.library.util.DLURLHelper;
// import com.liferay.document.library.web.internal.util.DLTrashUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.language.UnicodeLanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
// import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.DeleteMenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.JavaScriptMenuItem;
// import com.liferay.portal.kernel.servlet.taglib.ui.JavaScriptToolbarItem;
import com.liferay.portal.kernel.servlet.taglib.ui.JavaScriptUIItem;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.ToolbarItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
// import com.liferay.portal.kernel.servlet.taglib.ui.URLToolbarItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLUIItem;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.URLTemplateResource;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.WindowStateException;

import javax.servlet.http.HttpServletRequest;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.trash.service.TrashEntryLocalServiceUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
// import com.liferay.portal.kernel.util.BrowserSnifferUtil;
// import com.liferay.portal.kernel.servlet.taglib.ui.JavaScriptMenuItem;
// import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
// import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;

/**
 * @author Iv??n Zaera
 */
public class UIItemsBuilder {

	public UIItemsBuilder(
		HttpServletRequest httpServletRequest, FileEntry fileEntry,
		FileVersion fileVersion, ResourceBundle resourceBundle,
		/* DLTrashUtil dlTrashUtil, */ VersioningStrategy versioningStrategy,
		DLURLHelper dlURLHelper) {

		this(
			httpServletRequest, fileEntry, null, fileVersion, resourceBundle,
			/* dlTrashUtil, */ versioningStrategy, dlURLHelper);
	}

	public UIItemsBuilder(
			HttpServletRequest httpServletRequest, FileShortcut fileShortcut,
			ResourceBundle resourceBundle, /*DLTrashUtil dlTrashUtil,*/
			VersioningStrategy versioningStrategy, DLURLHelper dlURLHelper)
		throws PortalException {

		this(
			httpServletRequest, null, fileShortcut,
			fileShortcut.getFileVersion(), resourceBundle, /* dlTrashUtil, */
			versioningStrategy, dlURLHelper);
	}

	public UIItemsBuilder(
		HttpServletRequest httpServletRequest, FileVersion fileVersion,
		ResourceBundle resourceBundle, /*DLTrashUtil dlTrashUtil,*/
		VersioningStrategy versioningStrategy, DLURLHelper dlURLHelper) {

		this(
			httpServletRequest, null, null, fileVersion, resourceBundle,
			/* dlTrashUtil, */ versioningStrategy, dlURLHelper);
	}

	public void addCancelCheckoutMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if ((_fileShortcut != null) ||
			!_isCancelCheckoutDocumentActionAvailable()) {
			// !_fileEntryDisplayContextHelper.isCancelCheckoutDocumentActionAvailable()) {

			return;
		}

		PortletURL portletURL = _getActionURL(
			"/document_library/edit_file_entry", Constants.CANCEL_CHECKOUT);

		portletURL.setParameter(
			"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));

		_addURLUIItem(
			new URLMenuItem(), menuItems, DLUIItemKeys.CANCEL_CHECKOUT,
			"cancel-checkout[document]", portletURL.toString());
	}

	public void addCancelCheckoutToolbarItem(List<ToolbarItem> toolbarItems)
		throws PortalException {

		if (!_isCancelCheckoutDocumentActionAvailable()) {
		// if (!_fileEntryDisplayContextHelper.isCancelCheckoutDocumentActionAvailable()) {

			return;
		}

		// _addJavaScriptUIItemForToolbar(
		// 	(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */toolbarItems,
		// 	DLUIItemKeys.CANCEL_CHECKOUT,
		// 	LanguageUtil.get(_resourceBundle, "cancel-checkout[document]"),
		// 	getSubmitFormJavaScript(Constants.CANCEL_CHECKOUT, null));
		
		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), toolbarItems, DLUIItemKeys.CANCEL_CHECKOUT,
        // LanguageUtil.get(_resourceBundle, "cancel-checkout[document]"), getSubmitFormJavaScript(
		// 		Constants.CANCEL_CHECKOUT, null));
		
		toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.CANCEL_CHECKOUT;
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "cancel-checkout[document]");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add JavaScript behavior via a custom method or attribute if supported
			public String getOnClick() {
				return getSubmitFormJavaScript(Constants.CANCEL_CHECKOUT, null);
			}
		});
	}

	public void addCheckinMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if ((_fileShortcut != null) ||
			!_isCheckinActionAvailable()) {
			// !_fileEntryDisplayContextHelper.isCheckinActionAvailable()) {

			return;
		}

		menuItems.add(getCheckinMenuItem());
	}

	public void addCheckinToolbarItem(List<ToolbarItem> toolbarItems)
		throws PortalException {

		if (!_isCheckinActionAvailable()) {
		// if (!_fileEntryDisplayContextHelper.isCheckinActionAvailable()) {
			return;
		}

		PortletURL portletURL = _getActionURL(
			"/document_library/edit_file_entry", Constants.CHECKIN);

		portletURL.setParameter(
			"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));

		// Build initial JavaScript
    	String initialJavaScript = StringBundler.concat(
        getNamespace(), "showVersionDetailsDialog('",
        HtmlUtil.escapeJS(portletURL.toString()), "');");

		// JavaScriptToolbarItem javaScriptToolbarItem = _addJavaScriptUIItem(
		// JavaScriptMenuItem javaScriptToolbarItem = _addJavaScriptUIItemForToolbar(
		// 	(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */
		// 	toolbarItems, DLUIItemKeys.CHECKIN,
		// 	LanguageUtil.get(_resourceBundle, "checkin"),
		// 	StringBundler.concat(
		// 		getNamespace(), "showVersionDetailsDialog('",
		// 		HtmlUtil.escapeJS(portletURL.toString()), "');"));



		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptToolbarItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), toolbarItems, DLUIItemKeys.CHECKIN,
        // LanguageUtil.get(_resourceBundle, "checkin"), StringBundler.concat(
		// 		getNamespace(), "showVersionDetailsDialog('",
		// 		HtmlUtil.escapeJS(portletURL.toString()), "');"));

		String javaScript =
			"/com/liferay/document/library/web/display/context/dependencies" +
				"/checkin_js.ftl";

		Class<?> clazz = getClass();

		URLTemplateResource urlTemplateResource = new URLTemplateResource(
			javaScript, clazz.getResource(javaScript));

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL, urlTemplateResource, false);

		template.put("namespace", getNamespace());

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);
	
		// Combine initial and template-generated JavaScript
		String finalJavaScript = initialJavaScript + unsyncStringWriter.toString();
		
		// TODO CHECK THIS
		// javaScriptToolbarItem.setJavaScript(unsyncStringWriter.toString());

		toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.CHECKIN;
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "checkin");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return finalJavaScript;
			}
		});
	}

	public void addCheckoutMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if ((_fileShortcut != null) ||
			!_isCheckoutDocumentActionAvailable()) {
			// !_fileEntryDisplayContextHelper.isCheckoutDocumentActionAvailable()) {

			return;
		}

		PortletURL portletURL = _getActionURL(
			"/document_library/edit_file_entry", Constants.CHECKOUT);

		portletURL.setParameter(
			"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));

		_addURLUIItem(
			new URLMenuItem(), menuItems, DLUIItemKeys.CHECKOUT,
			"checkout[document]", portletURL.toString());
	}

	// Dans UIItemsBuilder.java
	// private void _addJavaScriptToolbarItem(
	// 	List<ToolbarItem> toolbarItems, String key, String label, String onClick) {
	// 	JavaScriptMenuItem item = new JavaScriptMenuItem();
	// 	item.setKey(key);
	// 	item.setLabel(label);
	// 	item.setOnClick(onClick);
	// 	toolbarItems.add((ToolbarItem)item);
	// }

	public void addCheckoutToolbarItem(List<ToolbarItem> toolbarItems)
		throws PortalException {

		// if (!_fileEntryDisplayContextHelper.isCheckoutDocumentActionAvailable()) {
		if (!_isCheckoutDocumentActionAvailable()) {

			return;
		}

		// _addJavaScriptUIItemForToolbar(
		// 	(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */ toolbarItems, DLUIItemKeys.CHECKOUT,
		// 	LanguageUtil.get(_resourceBundle, "checkout[document]"),
		// 	getSubmitFormJavaScript(Constants.CHECKOUT, null));

		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), toolbarItems, DLUIItemKeys.CHECKOUT,
        // LanguageUtil.get(_resourceBundle, "checkout[document]"), getSubmitFormJavaScript(
		// 		Constants.CANCEL_CHECKOUT, null));

				toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.CHECKOUT;
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "checkout[document]");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return getSubmitFormJavaScript(Constants.CHECKOUT, null);
			}
		});
	}

	// public void addCheckoutToolbarItem(List<ToolbarItem> toolbarItems)
	// 	throws PortalException {

	// 	if (!_isCheckoutDocumentActionAvailable()) {
	// 		return;
	// 	}

	// 	_addJavaScriptToolbarItem(
	// 		toolbarItems, DLUIItemKeys.CHECKOUT,
	// 		LanguageUtil.get(_resourceBundle, "checkout[document]"),
	// 		getSubmitFormJavaScript(Constants.CHECKOUT, null));
	// }


	public void addCompareToMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if (!DocumentConversionUtil.isComparableVersion(
				_fileVersion.getExtension())) {

			return;
		}

		PortletURL viewFileEntryURL = _getRenderURL(
			"/document_library/view_file_entry", _getRedirect());

		PortletURL selectFileVersionURL = _getRenderURL(
			"/document_library/select_file_version",
			viewFileEntryURL.toString());

		try {
			selectFileVersionURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			throw new PortalException(windowStateException);
		}

		selectFileVersionURL.setParameter("version", _fileVersion.getVersion());

		Map<String, Object> data = new HashMap<>();

		data.put("uri", selectFileVersionURL);

		PortletURL compareVersionURL = _getRenderURL(
			"/document_library/compare_versions", null);

		compareVersionURL.setParameter("backURL", _getCurrentURL());

		String jsNamespace = getNamespace() + _fileVersion.getFileVersionId();

		StringBundler sb = new StringBundler(4);

		sb.append(jsNamespace);
		sb.append("compareVersionDialog('");
		sb.append(HtmlUtil.escapeJS(selectFileVersionURL.toString()));
		sb.append("');");

		JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
			(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */ 
			menuItems, DLUIItemKeys.COMPARE_TO,
			"compare-to", sb.toString());
		
		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), menuItems, DLUIItemKeys.COMPARE_TO,
        // "compare-to", sb.toString());

		javaScriptMenuItem.setData(data);

		String javaScript =
			"/com/liferay/document/library/web/display/context/dependencies" +
				"/compare_to_js.ftl";

		Class<?> clazz = getClass();

		URLTemplateResource urlTemplateResource = new URLTemplateResource(
			javaScript, clazz.getResource(javaScript));

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL, urlTemplateResource, false);

		template.put("compareVersionURL", compareVersionURL.toString());
		template.put(
			"dialogTitle",
			UnicodeLanguageUtil.get(_httpServletRequest, "compare-versions"));
		template.put("jsNamespace", jsNamespace);
		template.put("namespace", getNamespace());

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		javaScriptMenuItem.setJavaScript(unsyncStringWriter.toString());
	}

	public void addDeleteMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		String cmd = null;

		if (isDeleteActionAvailable()) {
			cmd = Constants.DELETE;
		}
		else if (isMoveToTheRecycleBinActionAvailable()) {
			cmd = Constants.MOVE_TO_TRASH;
		}
		else {
			return;
		}

		DeleteMenuItem deleteMenuItem = new DeleteMenuItem();

		deleteMenuItem.setKey(DLUIItemKeys.DELETE);

		if (cmd.equals(Constants.MOVE_TO_TRASH)) {
			deleteMenuItem.setTrash(true);
		}

		String mvcActionCommandName = "/document_library/edit_file_entry";

		if (_fileShortcut != null) {
			mvcActionCommandName = "/document_library/edit_file_shortcut";
		}

		PortletURL portletURL = _getDeleteActionURL(mvcActionCommandName, cmd);

		if (_fileShortcut == null) {
			portletURL.setParameter(
				"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));
		}
		else {
			portletURL.setParameter(
				"fileShortcutId",
				String.valueOf(_fileShortcut.getFileShortcutId()));
		}

		deleteMenuItem.setURL(portletURL.toString());

		menuItems.add(deleteMenuItem);
	}

	public void addDeleteToolbarItem(List<ToolbarItem> toolbarItems)
		throws PortalException {

		if (!isDeleteActionAvailable()) {
			return;
		}

		StringBundler sb = new StringBundler(5);

		sb.append("if (confirm('");
		sb.append(
			UnicodeLanguageUtil.get(
				_resourceBundle, "are-you-sure-you-want-to-delete-this"));
		sb.append("')) {");

		LiferayPortletResponse liferayPortletResponse =
			_getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		long folderId = _fileEntry.getFolderId();

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/view");
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/view_folder");
		}

		portletURL.setParameter("folderId", String.valueOf(folderId));

		sb.append(
			getSubmitFormJavaScript(Constants.DELETE, portletURL.toString()));

		sb.append("}");

		// _addJavaScriptUIItemForToolbar(
		// 	(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */
		// 	 toolbarItems, DLUIItemKeys.DELETE,
		// 	LanguageUtil.get(_resourceBundle, "delete"), sb.toString());

		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), toolbarItems, DLUIItemKeys.DELETE,
        // LanguageUtil.get(_resourceBundle, "delete"), sb.toString());

		toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.DELETE;
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "delete");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return sb.toString();
			}
		});
	}

	public void addDeleteVersionMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if ((_fileEntry == null) ||
			(_fileVersion.getStatus() != WorkflowConstants.STATUS_APPROVED) ||
			!_hasDeletePermission() ||
			// !_fileEntryDisplayContextHelper.hasDeletePermission() ||
			!(_fileEntry.getModel() instanceof DLFileEntry)) {

			return;
		}

		int fileVersionsCount = _fileEntry.getFileVersionsCount(
			WorkflowConstants.STATUS_APPROVED);

		if (fileVersionsCount <= 1) {
			return;
		}

		PortletURL viewFileEntryURL = _getRenderURL(
			"/document_library/view_file_entry", _getRedirect());

		DeleteMenuItem deleteMenuItem = new DeleteMenuItem();

		deleteMenuItem.setKey(DLUIItemKeys.DELETE_VERSION);
		deleteMenuItem.setLabel("delete-version");

		PortletURL portletURL = _getActionURL(
			"/document_library/edit_file_entry", Constants.DELETE,
			viewFileEntryURL.toString());

		portletURL.setParameter(
			"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));
		portletURL.setParameter("version", _fileVersion.getVersion());

		deleteMenuItem.setURL(portletURL.toString());

		menuItems.add(deleteMenuItem);
	}

	public void addDownloadMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		// if (!_fileEntryDisplayContextHelper.isDownloadActionAvailable()) {
		if (!_isDownloadActionAvailable()) {
			return;
		}

		String label = LanguageUtil.formatStorageSize(
			_fileVersion.getSize(), _themeDisplay.getLocale());

		label = StringBundler.concat(
			_themeDisplay.translate("download"), " (", label, ")");

		final boolean appendVersion;

		if (StringUtil.equalsIgnoreCase(
				_fileEntry.getVersion(), _fileVersion.getVersion())) {

			appendVersion = false;
		}
		else {
			appendVersion = true;
		}

		String url = _dlURLHelper.getDownloadURL(
			_fileEntry, _fileVersion, _themeDisplay, StringPool.BLANK,
			appendVersion, true);

		URLMenuItem urlMenuItem = _addURLUIItem(
			new URLMenuItem(), menuItems, DLUIItemKeys.DOWNLOAD, label, url);

		Map<String, Object> data = new HashMap<>();

		data.put("senna-off", "true");

		urlMenuItem.setData(data);

		urlMenuItem.setMethod("get");
	}

	public void addDownloadToolbarItem(List<ToolbarItem> toolbarItems)
		throws PortalException {

		// if (!_fileEntryDisplayContextHelper.isDownloadActionAvailable()) {
		if (!_isDownloadActionAvailable()) {
			return;
		}

		String label = LanguageUtil.formatStorageSize(
			_fileVersion.getSize(), _themeDisplay.getLocale());

		// _addURLUIItem(
		// 	new URLToolbarItem(), toolbarItems, DLUIItemKeys.DOWNLOAD,
		// 	StringBundler.concat(
		// 		LanguageUtil.get(_resourceBundle, "download"), " (", label,
		// 		")"),
		// 	_dlURLHelper.getDownloadURL(
		// 		_fileEntry, _fileVersion, _themeDisplay, StringPool.BLANK));

		// _addURLUIItemForToolbar(
        // new URLMenuItem(), toolbarItems, DLUIItemKeys.DOWNLOAD,
        // StringBundler.concat(
        //     LanguageUtil.get(_resourceBundle, "download"), " (", label,
        //     ")"),
        // _dlURLHelper.getDownloadURL(
        //     _fileEntry, _fileVersion, _themeDisplay, StringPool.BLANK));
		
		toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.DOWNLOAD;
			}

			@Override
			public String getLabel() {
				return StringBundler.concat(
					LanguageUtil.get(_resourceBundle, "download"), " (", label, ")");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return _dlURLHelper.getDownloadURL(
					_fileEntry, _fileVersion, _themeDisplay, StringPool.BLANK);
			}
		});
	}

	public void addEditMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if (((_fileShortcut != null) &&
			 !_isFileShortcutEditActionAvailable()) ||
			//  !_fileShortcutDisplayContextHelper.isEditActionAvailable()) ||
			((_fileShortcut == null) &&
			 !_isEditActionAvailable())) {
			//  !_fileEntryDisplayContextHelper._isEditActionAvailable())) {

			return;
		}

		PortletURL portletURL = null;

		if (_fileShortcut == null) {
			portletURL = _getRenderURL("/document_library/edit_file_entry");
		}
		else {
			portletURL = _getRenderURL("/document_library/edit_file_shortcut");
		}

		portletURL.setParameter("backURL", _getCurrentURL());

		_addURLUIItem(
			new URLMenuItem(), menuItems, DLUIItemKeys.EDIT, "edit",
			portletURL.toString());
	}

	public void addEditToolbarItem(List<ToolbarItem> toolbarItems)
		throws PortalException {

		if (!_isEditActionAvailable()) {
		// if (!_fileEntryDisplayContextHelper.isEditActionAvailable()) {
			return;
		}

		PortletURL portletURL = _getRenderURL(
			"/document_library/edit_file_entry");

		// _addURLUIItem(
		// 	new URLToolbarItem(), toolbarItems, DLUIItemKeys.EDIT,
		// 	LanguageUtil.get(_resourceBundle, "edit"), portletURL.toString());

		// _addURLUIItemForToolbar(
		// 	new URLMenuItem(), toolbarItems, DLUIItemKeys.EDIT,
		// 	LanguageUtil.get(_resourceBundle, "edit"), portletURL.toString());

		toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.EDIT;
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "edit");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return portletURL.toString();
			}
		});
	}

	public void addMoveMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if (((_fileShortcut != null) &&
			 !_isFileShortcutMoveActionAvailable()) ||
			//  !_fileShortcutDisplayContextHelper.isMoveActionAvailable()) ||
			((_fileShortcut == null) &&
			 !_isMoveActionAvailable())) {
			//  !_fileEntryDisplayContextHelper.isMoveActionAvailable())) {

			return;
		}

		_addJavaScriptUIItem(
			(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */
			menuItems, DLUIItemKeys.MOVE,
			LanguageUtil.get(_resourceBundle, "move"),
			_getMoveEntryOnClickJavaScript());
		
		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), menuItems, DLUIItemKeys.MOVE,
        // LanguageUtil.get(_resourceBundle, "move"), _getMoveEntryOnClickJavaScript());
	}

	//custom copy functionality implemented in DMS
	public void addCopyMenuItem(List<MenuItem> menuItems)
			throws PortalException {
		if (((_fileShortcut != null)
				&& !_isFileShortcutMoveActionAvailable())
				// && !_fileShortcutDisplayContextHelper.isMoveActionAvailable())
				|| ((_fileShortcut == null) && !_isMoveActionAvailable())) {

			return;
		}

		_addJavaScriptUIItem(new JavaScriptMenuItem(), menuItems,
				DLUIItemKeys.class.getName() + "#copy",
				LanguageUtil.get(_resourceBundle, "copy"),
				_getCopyEntryOnClickJavaScript());


		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), menuItems, DLUIItemKeys.class.getName() + "#copy",
        // LanguageUtil.get(_resourceBundle, "copy"), _getCopyEntryOnClickJavaScript());
	}

	public void addMoveToolbarItem(List<ToolbarItem> toolbarItems)
		throws PortalException {
		if (!_isMoveActionAvailable()) {
		// if (!_fileEntryDisplayContextHelper.isMoveActionAvailable()) {
			return;
		}
		// _addJavaScriptUIItemForToolbar(
		// 	(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */toolbarItems, DLUIItemKeys.MOVE,
		// 	LanguageUtil.get(_resourceBundle, "move"),
		// 	_getMoveEntryOnClickJavaScript());

		
		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), toolbarItems, DLUIItemKeys.MOVE,
        // LanguageUtil.get(_resourceBundle, "move"), _getMoveEntryOnClickJavaScript());

			toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.MOVE;
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "move");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return _getMoveEntryOnClickJavaScript();
			}
		});
	}

	// custom copy functionality implemented in DMS
	public void addCopyToolbarItem(List<ToolbarItem> toolbarItems)
			throws PortalException {
		if (!_isMoveActionAvailable()) {
		// if (!_fileEntryDisplayContextHelper.isMoveActionAvailable()) {
			return;
		}
		// _addJavaScriptUIItemForToolbar(	(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */ toolbarItems,
		// 		DLUIItemKeys.class.getName() + "#copy",
		// 		LanguageUtil.get(_resourceBundle, "copy"),
		// 		_getCopyEntryOnClickJavaScript());

		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), toolbarItems, DLUIItemKeys.class.getName() + "#copy",
        // LanguageUtil.get(_resourceBundle, "copy"), _getCopyEntryOnClickJavaScript());

		toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.class.getName() + "#copy";
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "copy");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return _getCopyEntryOnClickJavaScript();
			}
		});
	}

	public void addMoveToTheRecycleBinToolbarItem(
			List<ToolbarItem> toolbarItems)
		throws PortalException {

		if (!isMoveToTheRecycleBinActionAvailable()) {
			return;
		}

		LiferayPortletResponse liferayPortletResponse =
			_getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		long folderId = _fileEntry.getFolderId();

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/view");
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/view_folder");
		}

		portletURL.setParameter("folderId", String.valueOf(folderId));
		portletURL.setParameter(
			"folderId", String.valueOf(_fileEntry.getFolderId()));

		// _addJavaScriptUIItemForToolbar(
		// 	(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */ toolbarItems,
		// 	DLUIItemKeys.MOVE_TO_THE_RECYCLE_BIN,
		// 	LanguageUtil.get(_resourceBundle, "move-to-recycle-bin"),
		// 	getSubmitFormJavaScript(
		// 		Constants.MOVE_TO_TRASH, portletURL.toString()));
		
		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), toolbarItems, DLUIItemKeys.MOVE_TO_THE_RECYCLE_BIN,
        // LanguageUtil.get(_resourceBundle, "move-to-recycle-bin"), getSubmitFormJavaScript(
		// 		Constants.MOVE_TO_TRASH, portletURL.toString()));

			toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.MOVE_TO_THE_RECYCLE_BIN;
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "move-to-recycle-bin");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return getSubmitFormJavaScript(Constants.MOVE_TO_TRASH, portletURL.toString());
			}
		});
	}

	public void addOpenInMsOfficeMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if (!isOpenInMsOfficeActionAvailable()) {
			return;
		}

		String webDavURL = _dlURLHelper.getWebDavURL(
			_themeDisplay, _fileEntry.getFolder(), _fileEntry,
			PropsValues.
				DL_FILE_ENTRY_OPEN_IN_MS_OFFICE_MANUAL_CHECK_IN_REQUIRED,
			true);

		String onClick = StringBundler.concat(
			getNamespace(), "openDocument('", HtmlUtil.escapeJS(webDavURL),
			"');");

		JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
			new JavaScriptMenuItem(), menuItems, DLUIItemKeys.OPEN_IN_MS_OFFICE,
			"open-in-ms-office", onClick);

		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
    	// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
        // new JavaScriptMenuItem(), menuItems, DLUIItemKeys.OPEN_IN_MS_OFFICE,
        // "open-in-ms-office", onClick);

		String javaScript =
			"/com/liferay/document/library/web/display/context/dependencies" +
				"/open_in_ms_office_js.ftl";

		Class<?> clazz = getClass();

		URLTemplateResource urlTemplateResource = new URLTemplateResource(
			javaScript, clazz.getResource(javaScript));

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL, urlTemplateResource, false);

		template.put(
			"errorMessage",
			UnicodeLanguageUtil.get(
				_resourceBundle,
				"cannot-open-the-requested-document-due-to-the-following-" +
					"reason"));
		template.put("namespace", getNamespace());

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		javaScriptMenuItem.setJavaScript(unsyncStringWriter.toString());
	}

	public void addOpenInMsOfficeToolbarItem(List<ToolbarItem> toolbarItems)
		throws PortalException {

		if (!isOpenInMsOfficeActionAvailable()) {
			return;
		}

		String webDavURL = _dlURLHelper.getWebDavURL(
			_themeDisplay, _fileEntry.getFolder(), _fileEntry,
			PropsValues.
				DL_FILE_ENTRY_OPEN_IN_MS_OFFICE_MANUAL_CHECK_IN_REQUIRED);

		StringBundler sb = new StringBundler(4);

		sb.append(getNamespace());
		sb.append("openDocument('");
		sb.append(HtmlUtil.escapeJS(webDavURL));
		sb.append("');");

		// _addJavaScriptUIItemForToolbar(
		// 		(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */ toolbarItems,
		// 	DLUIItemKeys.OPEN_IN_MS_OFFICE,
		// 	LanguageUtil.get(_resourceBundle, "open-in-ms-office"),
		// 	sb.toString());

		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
		// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
		// 	new JavaScriptMenuItem(), toolbarItems, DLUIItemKeys.OPEN_IN_MS_OFFICE,
		// 	LanguageUtil.get(_resourceBundle, "open-in-ms-office"), sb.toString());

		toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.OPEN_IN_MS_OFFICE;
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "open-in-ms-office");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return sb.toString();
			}
		});
	}

	public void addPermissionsMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if (((_fileShortcut != null) &&
			 !_isFileShortcutPermissionsButtonVisible()) ||
			//  !_fileShortcutDisplayContextHelper.isPermissionsButtonVisible()) ||
			((_fileShortcut == null) &&
			 !_isPermissionsButtonVisible())) {
			//  !_fileEntryDisplayContextHelper.isPermissionsButtonVisible())) {

			return;
		}

		String url = null;

		try {
			if (_fileShortcut != null) {
				url = PermissionsURLTag.doTag(
					null, DLFileShortcutConstants.getClassName(),
					HtmlUtil.unescape(_fileShortcut.getToTitle()), null,
					String.valueOf(_fileShortcut.getFileShortcutId()),
					LiferayWindowState.POP_UP.toString(), null,
					_httpServletRequest);
			}
			else {
				url = PermissionsURLTag.doTag(
					null, DLFileEntryConstants.getClassName(),
					HtmlUtil.unescape(_fileEntry.getTitle()), null,
					String.valueOf(_fileEntry.getFileEntryId()),
					LiferayWindowState.POP_UP.toString(), null,
					_httpServletRequest);
			}
		}
		catch (Exception exception) {
			throw new SystemException(
				"Unable to create permissions URL", exception);
		}

		URLMenuItem urlMenuItem = _addURLUIItem(
			new URLMenuItem(), menuItems, DLUIItemKeys.PERMISSIONS,
			"permissions", url);

		urlMenuItem.setMethod("get");
		urlMenuItem.setUseDialog(true);
	}

	public void addPermissionsToolbarItem(List<ToolbarItem> toolbarItems)
		throws PortalException {

		if (!_isPermissionsButtonVisible()) {
		// if (!_fileEntryDisplayContextHelper.isPermissionsButtonVisible()) {
			return;
		}

		String permissionsURL = null;

		try {
			permissionsURL = PermissionsURLTag.doTag(
				null, DLFileEntryConstants.getClassName(),
				HtmlUtil.unescape(_fileEntry.getTitle()), null,
				String.valueOf(_fileEntry.getFileEntryId()),
				LiferayWindowState.POP_UP.toString(), null,
				_httpServletRequest);
		}
		catch (Exception exception) {
			throw new SystemException(
				"Unable to create permissions URL", exception);
		}

		StringBundler sb = new StringBundler(6);

		sb.append("Liferay.Util.openWindow({dialogIframe: {bodyCssClass: ");
		sb.append("'dialog-with-footer'}, title: '");
		sb.append(UnicodeLanguageUtil.get(_resourceBundle, "permissions"));
		sb.append("', uri: '");
		sb.append(HtmlUtil.escapeJS(permissionsURL));
		sb.append("'});");

		// _addJavaScriptUIItemForToolbar(
		// 		(JavaScriptMenuItem) new JavaScriptMenuItem(), /*new JavaScriptToolbarItem(), */ toolbarItems, DLUIItemKeys.PERMISSIONS,
		// 	LanguageUtil.get(_resourceBundle, "permissions"), sb.toString());

		// Utiliser JavaScriptMenuItem au lieu de JavaScriptToolbarItem
		// JavaScriptMenuItem javaScriptMenuItem = _addJavaScriptUIItem(
		// 	new JavaScriptMenuItem(), toolbarItems, DLUIItemKeys.PERMISSIONS,
		// 	LanguageUtil.get(_resourceBundle, "permissions"), sb.toString());

		toolbarItems.add(new ToolbarItem() {
			@Override
			public String getKey() {
				return DLUIItemKeys.PERMISSIONS;
			}

			@Override
			public String getLabel() {
				return LanguageUtil.get(_resourceBundle, "permissions");
			}

			@Override
			public String getIcon() {
				return null; // Optional, set if needed
			}

			// @Override
			public boolean isEnabled() {
				return true; // Adjust logic if needed
			}

			// Add URL behavior via a custom method or attribute if supported
			public String getUrl() {
				return sb.toString();
			}
		});
	}

	public void addPublishMenuItem(
			List<MenuItem> menuItems, boolean latestVersion)
		throws PortalException {

		if (!_isFileVersionExportable(latestVersion)) {
			return;
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (!stagingGroupHelper.isStagingGroup(
				_themeDisplay.getScopeGroupId()) ||
			!stagingGroupHelper.isStagedPortlet(
				_themeDisplay.getScopeGroupId(),
				DLPortletKeys.DOCUMENT_LIBRARY)) {

			return;
		}

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		if (!portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {
			return;
		}

		if (((_fileEntry == null) ||
			 !_hasExportImportPermission()) &&
			//  !_fileEntryDisplayContextHelper.hasExportImportPermission()) &&
			((_fileShortcut == null) ||
			 _hasFileShortcutExportImportPermission())) {
			//  _fileShortcutDisplayContextHelper.hasExportImportPermission())) {

			return;
		}

		StringBundler sb = new StringBundler(5);

		sb.append("javascript:if (confirm('");
		sb.append(
			UnicodeLanguageUtil.get(
				_resourceBundle,
				"are-you-sure-you-want-to-publish-the-selected-document"));
		sb.append("')){location.href = '");

		PortletURL portletURL = null;

		if (_fileShortcut == null) {
			portletURL = _getActionURL("/document_library/publish_file_entry");

			portletURL.setParameter(
				"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));
		}
		else {
			portletURL = _getActionURL(
				"/document_library/publish_file_shortcut");

			portletURL.setParameter(
				"fileShortcutId",
				String.valueOf(_fileShortcut.getFileShortcutId()));
		}

		portletURL.setParameter("redirect", StringPool.BLANK);
		portletURL.setParameter("backURL", _getCurrentURL());

		sb.append(portletURL);

		sb.append("';}");

		_addURLUIItem(
			new URLMenuItem(), menuItems, DLUIItemKeys.PUBLISH,
			"publish-to-live", sb.toString());
	}

	public void addRevertToVersionMenuItem(List<MenuItem> menuItems)
		throws PortalException {

		if ((_fileVersion.getStatus() != WorkflowConstants.STATUS_APPROVED) ||
			!_hasUpdatePermission()) {
			// !_fileEntryDisplayContextHelper.hasUpdatePermission()) {

			return;
		}

		FileVersion latestFileVersion = _fileEntry.getLatestFileVersion();

		String latestFileVersionVersion = latestFileVersion.getVersion();

		if (latestFileVersionVersion.equals(_fileVersion.getVersion())) {
			return;
		}

		PortletURL viewFileEntryURL = _getRenderURL(
			"/document_library/view_file_entry", _getRedirect());

		PortletURL portletURL = _getActionURL(
			"/document_library/edit_file_entry", Constants.REVERT,
			viewFileEntryURL.toString());

		portletURL.setParameter(
			"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));
		portletURL.setParameter("version", _fileVersion.getVersion());

		_addURLUIItem(
			new URLMenuItem(), menuItems, DLUIItemKeys.REVERT, "revert",
			portletURL.toString());
	}

	public void addViewOriginalFileMenuItem(List<MenuItem> menuItems) {
		if (_fileShortcut == null) {
			return;
		}

		PortletURL portletURL = _getRenderURL(
			"/document_library/view_file_entry");

		portletURL.setParameter(
			"fileEntryId", String.valueOf(_fileShortcut.getToFileEntryId()));

		_addURLUIItem(
			new URLMenuItem(), menuItems, DLUIItemKeys.VIEW_ORIGINAL_FILE,
			"view-original-file", portletURL.toString());
	}

	public void addViewVersionMenuItem(List<MenuItem> menuItems) {
		if (_fileShortcut != null) {
			return;
		}

		PortletURL portletURL = _getRenderURL(
			"/document_library/view_file_entry", _getRedirect());

		portletURL.setParameter("version", _fileVersion.getVersion());

		_addURLUIItem(
			new URLMenuItem(), menuItems, DLUIItemKeys.VIEW_VERSION,
			"view[action]", portletURL.toString());
	}

	public MenuItem getCheckinMenuItem() throws PortalException {
		PortletURL portletURL = _getActionURL(
			"/document_library/edit_file_entry", Constants.CHECKIN);

		portletURL.setParameter(
			"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));

		if (!_versioningStrategy.isOverridable()) {
			URLMenuItem urlMenuItem = new URLMenuItem();

			urlMenuItem.setKey(DLUIItemKeys.CHECKIN);
			urlMenuItem.setLabel("checkin");
			urlMenuItem.setURL(portletURL.toString());

			return urlMenuItem;
		}

		JavaScriptMenuItem javaScriptMenuItem = new JavaScriptMenuItem();

		javaScriptMenuItem.setKey(DLUIItemKeys.CHECKIN);
		javaScriptMenuItem.setLabel("checkin");
		javaScriptMenuItem.setOnClick(
			StringBundler.concat(
				getNamespace(), "showVersionDetailsDialog('",
				HtmlUtil.escapeJS(portletURL.toString()), "');"));

		String javaScript =
			"/com/liferay/document/library/web/display/context/dependencies" +
				"/checkin_js.ftl";

		Class<?> clazz = getClass();

		URLTemplateResource urlTemplateResource = new URLTemplateResource(
			javaScript, clazz.getResource(javaScript));

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL, urlTemplateResource, false);

		template.put("namespace", getNamespace());

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		javaScriptMenuItem.setJavaScript(unsyncStringWriter.toString());

		return javaScriptMenuItem;
	}

	public boolean isOpenInMsOfficeActionAvailable() throws PortalException {
		// if (_fileEntryDisplayContextHelper.hasViewPermission() &&
		if (_hasViewPermission() &&
			// _fileVersionDisplayContextHelper.isMsOffice() &&
			_isMsOffice() &&
			_isWebDAVEnabled() && _isIEOnWin32()) {

			return true;
		}

		return false;
	}

	private boolean _isMsOffice() {
		if (_fileVersion == null) {
			return false;
		}
		
		String mimeType = _fileVersion.getMimeType();
		String extension = _fileVersion.getExtension();
		
		if (mimeType == null && extension == null) {
			return false;
		}
		
		// Vérification des types Office
		return _isMsOfficeExtension(extension) || _isMsOfficeMimeType(mimeType);
	}

	private boolean _isMsOfficeExtension(String extension) {
		if (extension == null) {
			return false;
		}
		
		extension = extension.toLowerCase();
		
		// Extensions Microsoft Office
		return extension.equals("doc") || extension.equals("docx") ||
			extension.equals("xls") || extension.equals("xlsx") ||
			extension.equals("ppt") || extension.equals("pptx") ||
			extension.equals("docm") || extension.equals("xlsm") ||
			extension.equals("pptm");
	}

	private boolean _isMsOfficeMimeType(String mimeType) {
		if (mimeType == null) {
			return false;
		}
		
		// Types MIME Microsoft Office
		return mimeType.startsWith("application/vnd.ms-") ||
			mimeType.startsWith("application/vnd.openxmlformats-officedocument") ||
			mimeType.equals("application/msword") ||
			mimeType.equals("application/vnd.ms-excel") ||
			mimeType.equals("application/vnd.ms-powerpoint");
	}

	protected String getNamespace() {
		LiferayPortletResponse liferayPortletResponse =
			_getLiferayPortletResponse();

		return liferayPortletResponse.getNamespace();
	}

	protected String getSubmitFormJavaScript(String cmd, String redirect) {
		StringBundler sb = new StringBundler(18);

		sb.append("document.");
		sb.append(getNamespace());
		sb.append("fm.");
		sb.append(getNamespace());
		sb.append(Constants.CMD);
		sb.append(".value = '");
		sb.append(cmd);
		sb.append("';");

		if (redirect != null) {
			sb.append("document.");
			sb.append(getNamespace());
			sb.append("fm.");
			sb.append(getNamespace());
			sb.append("redirect.value = '");
			sb.append(HtmlUtil.escapeJS(redirect));
			sb.append("';");
		}

		sb.append("submitForm(document.");
		sb.append(getNamespace());
		sb.append("fm);");

		return sb.toString();
	}

	protected boolean isDeleteActionAvailable() throws PortalException {
		if (((_fileShortcut != null) &&
			 _isFileShortcutDeletable() &&
			//  _fileShortcutDisplayContextHelper.isFileShortcutDeletable() &&
			 !_isFileShortcutTrashable()) ||
			((_fileShortcut == null) &&
			_isFileEntryDeletable() &&
			//  _fileEntryDisplayContextHelper.isFileEntryDeletable() &&
			 !_isFileEntryTrashable())) {

			return true;
		}

		return false;
	}

	protected boolean isMoveToTheRecycleBinActionAvailable()
		throws PortalException {

		if (!isDeleteActionAvailable() &&
			(((_fileShortcut != null) &&
			  _isFileShortcutDeletable()) ||
			//   _fileShortcutDisplayContextHelper.isFileShortcutDeletable()) ||
			 ((_fileShortcut == null) &&
			  _isFileEntryDeletable()))) {
			//   _fileEntryDisplayContextHelper.isFileEntryDeletable()))) {

			return true;
		}

		return false;
	}

	private UIItemsBuilder(
		HttpServletRequest httpServletRequest, FileEntry fileEntry,
		FileShortcut fileShortcut, FileVersion fileVersion,
		ResourceBundle resourceBundle,/* dlTrashUtil, */
		VersioningStrategy versioningStrategy, DLURLHelper dlURLHelper) {

		try {
			_httpServletRequest = httpServletRequest;

			if ((fileEntry == null) && (fileVersion != null)) {
				fileEntry = fileVersion.getFileEntry();
			}

			_fileEntry = fileEntry;

			_fileShortcut = fileShortcut;
			_fileVersion = fileVersion;
			_resourceBundle = resourceBundle;
			// _dlTrashUtil = dlTrashUtil;
			_versioningStrategy = versioningStrategy;
			_dlURLHelper = dlURLHelper;

			_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
			_permissionChecker = _themeDisplay.getPermissionChecker();

			// _fileEntryDisplayContextHelper = new FileEntryDisplayContextHelper(
			// 	_themeDisplay.getPermissionChecker(), _fileEntry);

			// _fileShortcutDisplayContextHelper =
			// 	new FileShortcutDisplayContextHelper(
			// 		_themeDisplay.getPermissionChecker(), _fileShortcut);

			// _fileVersionDisplayContextHelper =
			// 	new FileVersionDisplayContextHelper(fileVersion);
		}
		catch (PortalException portalException) {
			throw new SystemException(
				"Unable to build UIItemsBuilder for " + fileVersion,
				portalException);
		}
	}

	private <T extends JavaScriptUIItem> T _addJavaScriptUIItem(
		T javascriptUIItem, List<? super T> javascriptUIItems, String key,
		String label, String onClick) {

		javascriptUIItem.setKey(key);
		javascriptUIItem.setLabel(label);
		javascriptUIItem.setOnClick(onClick);

		javascriptUIItems.add(javascriptUIItem);
		// Cast to ToolbarItem to maintain compatibility with the method signature
    	// ((List<ToolbarItem>) javascriptUIItems).add((ToolbarItem) javascriptUIItem);
		return javascriptUIItem;
	}

	// private JavaScriptMenuItem _addJavaScriptUIItemForToolbar(
	// 	JavaScriptMenuItem javascriptMenuItem, List<ToolbarItem> toolbarItems,
	// 	String key, String label, String onClick) {

	// 	javascriptMenuItem.setKey(key);
	// 	javascriptMenuItem.setLabel(label);
	// 	javascriptMenuItem.setOnClick(onClick);

	// 	toolbarItems.add(javascriptMenuItem);

	// 	return javascriptMenuItem;
	// }

	// private URLMenuItem _addURLUIItemForToolbar(
	// 	URLMenuItem urlMenuItem, List<ToolbarItem> toolbarItems,
	// 	String key, String label, String url) {

	// 	urlMenuItem.setKey(key);
	// 	urlMenuItem.setLabel(label);
	// 	urlMenuItem.setURL(url);

	// 	toolbarItems.add((ToolbarItem) urlMenuItem); // Explicit cast to ToolbarItem

	// 	return urlMenuItem;
	// }

	private <T extends URLUIItem> T _addURLUIItem(
		T urlUIItem, List<? super T> urlUIItems, String key, String label,
		String url) {

		urlUIItem.setKey(key);
		urlUIItem.setLabel(label);
		urlUIItem.setURL(url);

		urlUIItems.add(urlUIItem);

		return urlUIItem;
	}

	private PortletURL _getActionURL(String mvcActionCommandName) {
		return _getActionURL(mvcActionCommandName, null);
	}

	private PortletURL _getActionURL(String mvcActionCommandName, String cmd) {
		return _getActionURL(mvcActionCommandName, cmd, _getCurrentURL());
	}

	private PortletURL _getActionURL(
		String mvcActionCommandName, String cmd, String redirect) {

		LiferayPortletResponse liferayPortletResponse =
			_getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createActionURL();

		portletURL.setParameter(
			ActionRequest.ACTION_NAME, mvcActionCommandName);

		if (Validator.isNotNull(cmd)) {
			portletURL.setParameter(Constants.CMD, cmd);
		}

		portletURL.setParameter("redirect", redirect);

		return portletURL;
	}

	private String _getCurrentURL() {
		if (_currentURL != null) {
			return _currentURL;
		}

		LiferayPortletRequest liferayPortletRequest =
			_getLiferayPortletRequest();

		LiferayPortletResponse liferayPortletResponse =
			_getLiferayPortletResponse();

		PortletURL portletURL = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);

		_currentURL = portletURL.toString();

		return _currentURL;
	}

	private PortletURL _getDeleteActionURL(
		String mvcActionCommandName, String cmd) {

		String currentMVCRenderCommandName = ParamUtil.getString(
			_httpServletRequest, "mvcRenderCommandName");

		if (currentMVCRenderCommandName.equals(
				"/document_library/view_file_entry")) {

			String redirect = ParamUtil.getString(
				_httpServletRequest, "redirect");

			if (Validator.isNull(redirect)) {
				LiferayPortletResponse liferayPortletResponse =
					_getLiferayPortletResponse();

				PortletURL portletURL =
					liferayPortletResponse.createRenderURL();

				redirect = portletURL.toString();
			}

			return _getActionURL(mvcActionCommandName, cmd, redirect);
		}

		return _getActionURL(mvcActionCommandName, cmd);
	}

	private LiferayPortletRequest _getLiferayPortletRequest() {
		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		return PortalUtil.getLiferayPortletRequest(portletRequest);
	}

	private LiferayPortletResponse _getLiferayPortletResponse() {
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		return PortalUtil.getLiferayPortletResponse(portletResponse);
	}

	private String _getMoveEntryOnClickJavaScript() {
		StringBundler sb = new StringBundler(5);

		sb.append(getNamespace());
		sb.append("move(1, ");

		if (_fileShortcut != null) {
			sb.append("'rowIdsDLFileShortcut', ");
			sb.append(_fileShortcut.getFileShortcutId());
		}
		else {
			sb.append("'rowIdsFileEntry', ");
			sb.append(_fileEntry.getFileEntryId());
		}

		sb.append(");");
		return sb.toString();
	}

	//custom copy functionality implemented in DMS
	private String _getCopyEntryOnClickJavaScript() {
		StringBundler sb = new StringBundler(5);
		sb.append(getNamespace());
		sb.append("copy(1, ");

		if (_fileShortcut != null) {
			sb.append("'rowIdsDLFileShortcut', ");
			sb.append(_fileShortcut.getFileShortcutId());
		} else {
			sb.append("'rowIdsFileEntry', ");
			sb.append(_fileEntry.getFileEntryId());
		}

		sb.append(");");
		return sb.toString();
	}

	private String _getRedirect() {
		if (_redirect == null) {
			_redirect = ParamUtil.getString(_httpServletRequest, "redirect");
		}

		return _redirect;
	}

	private PortletURL _getRenderURL(String mvcRenderCommandName) {
		return _getRenderURL(mvcRenderCommandName, _getCurrentURL());
	}

	private PortletURL _getRenderURL(
		String mvcRenderCommandName, String redirect) {

		LiferayPortletResponse liferayPortletResponse =
			_getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		portletURL.setParameter("mvcRenderCommandName", mvcRenderCommandName);

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		if (_fileShortcut != null) {
			portletURL.setParameter(
				"fileShortcutId",
				String.valueOf(_fileShortcut.getFileShortcutId()));
		}
		else {
			portletURL.setParameter(
				"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));
		}

		return portletURL;
	}

	private boolean _isFileEntryTrashable() throws PortalException {
		if (_fileEntry.isRepositoryCapabilityProvided(TrashCapability.class) &&
			_isTrashEnabled(_themeDisplay.getScopeGroupId(), _getRepositoryId())) {

			return true;
		}

		return false;
	}

	private boolean _isFileShortcutTrashable() throws PortalException {
		// if (_fileShortcutDisplayContextHelper.isDLFileShortcut() &&
		if (_isDLFileShortcut() &&
			_isTrashEnabled(_themeDisplay.getScopeGroupId(), _getRepositoryId())) {

			return true;
		}

		return false;
	}

	private boolean _isFileVersionExportable(boolean latestVersion) {
		try {
			FileVersion fileVersion = _fileVersion;

			if (latestVersion) {
				if (_fileEntry == null) {
					return false;
				}

				fileVersion = _fileEntry.getLatestFileVersion();
			}

			if (fileVersion == null) {
				return false;
			}

			StagedModelDataHandler stagedModelDataHandler =
				StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
					FileEntry.class.getName());

			if (ArrayUtil.contains(
					stagedModelDataHandler.getExportableStatuses(),
					fileVersion.getStatus())) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			return false;
		}
	}

	// private boolean _isIEOnWin32() {
	// 	if (_ieOnWin32 == null) {
	// 		_ieOnWin32 = BrowserSnifferUtil.isIeOnWin32(_httpServletRequest);
	// 	}

	// 	return _ieOnWin32;
	// }


	private boolean _isIEOnWin32() {
		if (_ieOnWin32 == null) {
			// Implémentation directe sans BrowserSnifferUtil
			try {
				String userAgent = _httpServletRequest.getHeader("User-Agent");
				if (userAgent != null) {
					userAgent = userAgent.toLowerCase();
					_ieOnWin32 = (userAgent.contains("msie") || userAgent.contains("trident")) 
							&& userAgent.contains("windows");
				} else {
					_ieOnWin32 = false;
				}
			} catch (Exception e) {
				_ieOnWin32 = false;
			}
		}
		return _ieOnWin32;
	}

	// private boolean _isTrashEnabled() throws PortalException {
	// 	if (_trashEnabled != null) {
	// 		return _trashEnabled;
	// 	}

	// 	_trashEnabled = false;

	// 	if (_dlTrashUtil == null) {
	// 		return _trashEnabled;
	// 	}

	// 	_trashEnabled = _dlTrashUtil.isTrashEnabled(
	// 		_themeDisplay.getScopeGroupId(), _fileEntry.getRepositoryId());

	// 	return _trashEnabled;
	// }

	private boolean _isWebDAVEnabled() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return portletDisplay.isWebDAVEnabled();
	}



	private boolean _isTrashEnabled(long groupId, long repositoryId) {
		try {
			if (_trashEnabled != null) {
				return _trashEnabled;
			}
			
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

	private long _getRepositoryId() {
		if (_repositoryId != null) {
			return _repositoryId;
		}

		Folder folder = _getFolder();

		if (folder != null) {
			_repositoryId = folder.getRepositoryId();
		}
		else {
			_repositoryId = GetterUtil.getLong(
				(String)_httpServletRequest.getAttribute(
					"view.jsp-repositoryId"));
		}

		return _repositoryId;
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

	private boolean _isDownloadActionAvailable() throws PortalException {
		try {
			if (_fileEntry == null) {
				return false;
			}
			
			// Si le fichier est dans la corbeille, pas de téléchargement
			if (_fileEntry.isInTrash()) {
				return false;
			}
			
			PermissionChecker permissionChecker = _themeDisplay.getPermissionChecker();
			
			// Vérification des permissions VIEW sur le fichier
			if (!permissionChecker.hasPermission(
					_fileEntry.getGroupId(),
					DLFileEntry.class.getName(),
					_fileEntry.getFileEntryId(),
					ActionKeys.VIEW)) {
				return false;
			}
	
			// Vérification des permissions VIEW sur le dossier parent
			if (!permissionChecker.hasPermission(
					_fileEntry.getGroupId(),
					DLFolder.class.getName(),
					_fileEntry.getFolderId(),
					ActionKeys.VIEW)) {
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			_log.error(
			"Unable to check _isDownloadActionAvailable for file : " + _fileEntry, e);
			return false;
		}
	}

	private boolean _isEditActionAvailable() throws PortalException {
		try {
			return _fileEntry != null && 
				!_fileEntry.isInTrash() && 
				_themeDisplay.getPermissionChecker().hasPermission(
					_fileEntry.getGroupId(),
					DLFileEntry.class.getName(),
					_fileEntry.getFileEntryId(),
					ActionKeys.UPDATE);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean _isFileEntryDeletable() throws PortalException {
		if (_fileEntry == null || _fileEntry.isInTrash()) {
			return false;
		}
		
		return _permissionChecker.hasPermission(
			_fileEntry.getGroupId(),
			DLFileEntry.class.getName(),
			_fileEntry.getFileEntryId(),
			ActionKeys.DELETE);
	}

	private boolean _hasViewPermission() throws PortalException {
		if (_fileEntry == null) {
			return false;
		}
		
		return _permissionChecker.hasPermission(
			_fileEntry.getGroupId(),
			DLFileEntry.class.getName(),
			_fileEntry.getFileEntryId(),
			ActionKeys.VIEW);
	}

	private boolean _hasUpdatePermission() throws PortalException {
		if (_fileEntry == null) {
			return false;
		}
		
		return _permissionChecker.hasPermission(
			_fileEntry.getGroupId(),
			DLFileEntry.class.getName(),
			_fileEntry.getFileEntryId(),
			ActionKeys.UPDATE);
	}

	// TODO check this
	private boolean _hasExportImportPermission() throws PortalException {
		// return _permissionChecker.hasPermission(
		// 	_fileEntry.getGroupId(),
		// 	"com.liferay.portlet.exportimport",
		// 	_fileEntry.getGroupId(),
		// 	"EXPORT_IMPORT_PORTLET_INFO");

		try {
			return GroupPermissionUtil.contains(
				_permissionChecker,
				_fileEntry.getGroupId(),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean _hasDeletePermission() throws PortalException {
		return _hasUpdatePermission(); // Même logique généralement
	}

	private boolean _isPermissionsButtonVisible() throws PortalException {
		return _permissionChecker.hasPermission(
			_fileEntry.getGroupId(),
			DLFileEntry.class.getName(),
			_fileEntry.getFileEntryId(),
			ActionKeys.PERMISSIONS);
	}

	private boolean _isMoveActionAvailable() throws PortalException {
		return _hasUpdatePermission() && !_fileEntry.isInTrash();
	}

	private boolean _isCheckoutDocumentActionAvailable() throws PortalException {
		if (_fileEntry == null || _fileEntry.isInTrash()) {
			return false;
		}
		
		// Vérifier que le fichier n'est pas déjà en checkout
		if (_fileEntry.isCheckedOut()) {
			return false;
		}
		
		// Vérifier les permissions UPDATE
		return _permissionChecker.hasPermission(
			_fileEntry.getGroupId(),
			DLFileEntry.class.getName(),
			_fileEntry.getFileEntryId(),
			ActionKeys.UPDATE);
	}

	private boolean _isCheckinActionAvailable() throws PortalException {
		if (_fileEntry == null || _fileEntry.isInTrash()) {
			return false;
		}
		
		// Le fichier doit être en checkout
		if (!_fileEntry.isCheckedOut()) {
			return false;
		}
		
		// L'utilisateur doit être celui qui a fait le checkout ou avoir les permissions
		return _fileEntry.hasLock() || 
			_permissionChecker.hasPermission(
				_fileEntry.getGroupId(),
				DLFileEntry.class.getName(),
				_fileEntry.getFileEntryId(),
				ActionKeys.UPDATE);
	}

	private boolean _isCancelCheckoutDocumentActionAvailable() throws PortalException {
		if (_fileEntry == null || _fileEntry.isInTrash()) {
			return false;
		}
		
		// Le fichier doit être en checkout
		if (!_fileEntry.isCheckedOut()) {
			return false;
		}
		
		// L'utilisateur doit être celui qui a fait le checkout ou être admin
		return _fileEntry.hasLock() || 
			_permissionChecker.isGroupAdmin(_fileEntry.getGroupId()) ||
			_permissionChecker.isCompanyAdmin();
	}

	// _fileShortcutDisplayContextHelper.isEditActionAvailable()
	// Remplacer par :
	private boolean _isFileShortcutEditActionAvailable() throws PortalException {
		if (_fileShortcut == null || _isFileShortcutInTrash()) {
			return false;
		}
		
		return _permissionChecker.hasPermission(
			_fileShortcut.getGroupId(),
			DLFileShortcutConstants.getClassName(),
			_fileShortcut.getFileShortcutId(),
			ActionKeys.UPDATE);
	}

	// _fileShortcutDisplayContextHelper.isMoveActionAvailable()
	// Remplacer par :
	private boolean _isFileShortcutMoveActionAvailable() throws PortalException {
		if (_fileShortcut == null || _isFileShortcutInTrash()) {
			return false;
		}
		
		return _permissionChecker.hasPermission(
			_fileShortcut.getGroupId(),
			DLFileShortcutConstants.getClassName(),
			_fileShortcut.getFileShortcutId(),
			ActionKeys.UPDATE);
	}

	// _fileShortcutDisplayContextHelper.isPermissionsButtonVisible()
	// Remplacer par :
	private boolean _isFileShortcutPermissionsButtonVisible() throws PortalException {
		if (_fileShortcut == null) {
			return false;
		}
		
		return _permissionChecker.hasPermission(
			_fileShortcut.getGroupId(),
			DLFileShortcutConstants.getClassName(),
			_fileShortcut.getFileShortcutId(),
			ActionKeys.PERMISSIONS);
	}

	// _fileShortcutDisplayContextHelper.hasExportImportPermission()
	// Remplacer par :
	private boolean _hasFileShortcutExportImportPermission() throws PortalException {
		// Vérification simplifiée pour la migration
		return _permissionChecker.isGroupAdmin(_fileShortcut.getGroupId()) ||
			_permissionChecker.isCompanyAdmin();
	}

	// _fileShortcutDisplayContextHelper.isFileShortcutDeletable()
	// Remplacer par :
	private boolean _isFileShortcutDeletable() throws PortalException {
		if (_fileShortcut == null || _isFileShortcutInTrash()) {
			return false;
		}
		
		return _permissionChecker.hasPermission(
			_fileShortcut.getGroupId(),
			DLFileShortcutConstants.getClassName(),
			_fileShortcut.getFileShortcutId(),
			ActionKeys.DELETE);
	}

	// _fileShortcutDisplayContextHelper.isDLFileShortcut()
	// Remplacer par :
	private boolean _isDLFileShortcut() {
		return _fileShortcut != null && 
			(_fileShortcut.getModel() instanceof DLFileShortcut);
	}

	private boolean _isFileShortcutInTrash() {
		try {
			if (_fileShortcut == null) {
				return false;
			}
			
			// Vérifier via le fichier cible
			FileEntry targetFileEntry = _fileShortcut.getFileVersion().getFileEntry();
			return targetFileEntry.isInTrash();
			
		} catch (Exception e) {
			// En cas d'erreur, considérer comme non supprimé
			return false;
		}
	}



	private Long _repositoryId;
	private Folder _folder;
	private final PermissionChecker _permissionChecker;

	private String _currentURL;
	// private final DLTrashUtil _dlTrashUtil;
	private final DLURLHelper _dlURLHelper;
	private final FileEntry _fileEntry;
	// private final FileEntryDisplayContextHelper _fileEntryDisplayContextHelper;
	private FileShortcut _fileShortcut;
	// private final FileShortcutDisplayContextHelper
	// 	_fileShortcutDisplayContextHelper;
	private final FileVersion _fileVersion;
	// private final FileVersionDisplayContextHelper
	// 	_fileVersionDisplayContextHelper;
	private final HttpServletRequest _httpServletRequest;
	private Boolean _ieOnWin32;
	private String _redirect;
	private final ResourceBundle _resourceBundle;
	private final ThemeDisplay _themeDisplay;
	private Boolean _trashEnabled;
	private final VersioningStrategy _versioningStrategy;

	private static final Log _log = LogFactoryUtil.getLog(UIItemsBuilder.class);

}