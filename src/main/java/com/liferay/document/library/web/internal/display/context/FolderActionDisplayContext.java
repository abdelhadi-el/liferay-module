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

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
// import com.liferay.document.library.web.internal.display.context.logic.DLPortletInstanceSettingsHelper;
// import com.liferay.document.library.web.internal.display.context.util.DLRequestHelper;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.document.library.web.internal.security.permission.resource.DLPermission;
import com.liferay.document.library.web.internal.util.DLFolderUtil;
// import com.liferay.document.library.web.internal.util.DLTrashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
// import com.liferay.portal.kernel.workflow.WorkflowEngineManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import javax.portlet.ActionRequest;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.WindowStateException;

import javax.servlet.http.HttpServletRequest;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
// import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
// import com.liferay.trash.kernel.service.TrashEntryLocalServiceUtil;
// import com.liferay.trash.kernel.util.TrashUtil;
import com.liferay.trash.service.TrashEntryLocalServiceUtil;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
// import com.liferay.portal.kernel.workflow.WorkflowDefinitionManagerUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;

/**
 * @author Adolfo P??rez
 */
public class FolderActionDisplayContext {

	// private static final String DOCUMENT_LIBRARY_TRASH_UTIL = "DOCUMENT_LIBRARY_TRASH_UTIL";

	// Dans votre classe
	private ThemeDisplay _themeDisplay;
	private PermissionChecker _permissionChecker;
	private LiferayPortletRequest _liferayPortletRequest;
	private LiferayPortletResponse _liferayPortletResponse;
	private String _currentURL;
	private String _portletName;

	public FolderActionDisplayContext(
		HttpServletRequest httpServletRequest/* , DLTrashUtil dlTrashUtil*/) {

		_httpServletRequest = httpServletRequest;
		// _dlTrashUtil = dlTrashUtil;

		// _dlRequestHelper = new DLRequestHelper(httpServletRequest);²
		// Remplacement de DLRequestHelper
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		_permissionChecker = _themeDisplay.getPermissionChecker();
		
		PortletRequest portletRequest = (PortletRequest)httpServletRequest.getAttribute(JavaConstants.JAVAX_PORTLET_REQUEST);
		PortletResponse portletResponse = (PortletResponse)httpServletRequest.getAttribute(JavaConstants.JAVAX_PORTLET_RESPONSE);
		
		_liferayPortletRequest = PortalUtil.getLiferayPortletRequest(portletRequest);
		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(portletResponse);
		_currentURL = PortletURLUtil.getCurrent(_liferayPortletRequest, _liferayPortletResponse).toString();
		_portletName = _liferayPortletRequest.getPortletName();

	}

	public String getAddFileShortcutURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName", "/document_library/edit_file_shortcut");
		portletURL.setParameter("redirect", _currentURL);
		portletURL.setParameter(
			"repositoryId", String.valueOf(_getRepositoryId()));
		portletURL.setParameter("folderId", String.valueOf(_getFolderId()));

		return portletURL.toString();
	}

	public String getAddFolderURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName", "/document_library/edit_folder");
		portletURL.setParameter("redirect", _currentURL);
		portletURL.setParameter(
			"repositoryId", String.valueOf(_getRepositoryId()));
		portletURL.setParameter(
			"parentFolderId", String.valueOf(_getFolderId()));
		portletURL.setParameter("ignoreRootFolder", Boolean.TRUE.toString());

		return portletURL.toString();
	}

	public String getAddMediaURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName", "/document_library/edit_file_entry");
		portletURL.setParameter("redirect", _currentURL);
		portletURL.setParameter(
			"repositoryId", String.valueOf(_getRepositoryId()));
		portletURL.setParameter("folderId", String.valueOf(_getFolderId()));

		return portletURL.toString();
	}

	public String getAddMultipleMediaURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName",
			"/document_library/upload_multiple_file_entries");
		portletURL.setParameter("redirect", _currentURL);
		portletURL.setParameter("backURL", _currentURL);
		portletURL.setParameter(
			"repositoryId", String.valueOf(_getRepositoryId()));
		portletURL.setParameter("folderId", String.valueOf(_getFolderId()));

		return portletURL.toString();
	}

	public String getAddRepositoryURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName", "/document_library/edit_repository");
		portletURL.setParameter("redirect", _currentURL);
		portletURL.setParameter("folderId", String.valueOf(_getFolderId()));

		return portletURL.toString();
	}

	public String getDeleteExpiredTemporaryFileEntriesURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createActionURL();

		portletURL.setParameter(
			ActionRequest.ACTION_NAME, "/document_library/edit_folder");
		portletURL.setParameter(
			Constants.CMD, "deleteExpiredTemporaryFileEntries");
		portletURL.setParameter("redirect", _currentURL);
		portletURL.setParameter(
			"repositoryId", String.valueOf(_getRepositoryId()));

		return portletURL.toString();
	}

	public String getDeleteFolderURL() throws PortalException {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createActionURL();

		Folder folder = _getFolder();

		if (!DLFolderUtil.isRepositoryRoot(folder)) {
			portletURL.setParameter(
				ActionRequest.ACTION_NAME, "/document_library/edit_folder");
		}
		else {
			portletURL.setParameter(
				ActionRequest.ACTION_NAME, "/document_library/edit_repository");
		}

		portletURL.setParameter(Constants.CMD, _getDeleteFolderCommand());
		portletURL.setParameter("redirect", _getParentFolderURL());

		if (!DLFolderUtil.isRepositoryRoot(folder)) {
			portletURL.setParameter("folderId", String.valueOf(_getFolderId()));
		}
		else {
			portletURL.setParameter(
				"repositoryId", String.valueOf(_getRepositoryId()));
		}

		return portletURL.toString();
	}

	public String getDownloadFolderURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		ResourceURL resourceURL = liferayPortletResponse.createResourceURL();

		resourceURL.setParameter("folderId", String.valueOf(_getFolderId()));
		resourceURL.setParameter(
			"repositoryId", String.valueOf(_getRepositoryId()));
		resourceURL.setResourceID("/document_library/download_folder");

		return resourceURL.toString();
	}

	public String getEditFolderURL() {
		Folder folder = _getFolder();

		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		if ((folder == null) || !DLFolderUtil.isRepositoryRoot(folder)) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/edit_folder");
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/edit_repository");
		}

		portletURL.setParameter("redirect", _currentURL);
		portletURL.setParameter("folderId", String.valueOf(_getFolderId()));
		portletURL.setParameter(
			"repositoryId", String.valueOf(_getRepositoryId()));

		if (folder == null) {
			portletURL.setParameter("rootFolder", Boolean.TRUE.toString());
		}

		return portletURL.toString();
	}

	public String getModelResource() {
		Folder folder = _getFolder();

		if (folder != null) {
			return DLFolderConstants.getClassName();
		}

		return "com.liferay.document.library";
	}

	public String getModelResourceDescription() throws PortalException {
		Folder folder = _getFolder();

		if (folder != null) {
			return folder.getName();
		}

		ThemeDisplay themeDisplay = _themeDisplay;

		return themeDisplay.getScopeGroupName();
	}

	public String getMoveFolderURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		return StringBundler.concat(
			"javascript:", liferayPortletResponse.getNamespace(),
			"move(1, 'rowIdsFolder', ", _getFolderId(), ");");
	}

	// Chintan - Copy Folder Functionality
	public String getCopyFolderURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		return StringBundler.concat(
			"javascript:", liferayPortletResponse.getNamespace(),
			"copy(1, 'rowIdsFolder', ", _getFolderId(), ");");
	}

	public String getPublishFolderURL() {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createActionURL();

		portletURL.setParameter(
			ActionRequest.ACTION_NAME, "/document_library/publish_folder");
		portletURL.setParameter("backURL", _currentURL);
		portletURL.setParameter("folderId", String.valueOf(_getFolderId()));

		return portletURL.toString();
	}

	public String getRandomNamespace() {
		if (_randomNamespace != null) {
			return _randomNamespace;
		}

		String portletName = _portletName;

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) ||
			portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {

			String randomKey = PortalUtil.generateRandomKey(
				_httpServletRequest, "portlet_document_library_folder_action");

			_randomNamespace = randomKey + StringPool.UNDERLINE;
		}
		else {
			String randomKey = PortalUtil.generateRandomKey(
				_httpServletRequest,
				"portlet_image_gallery_display_folder_action");

			_randomNamespace = randomKey + StringPool.UNDERLINE;
		}

		return _randomNamespace;
	}

	public long getResourcePrimKey() {
		Folder folder = _getFolder();

		if (folder != null) {
			return folder.getFolderId();
		}

		return _themeDisplay.getScopeGroupId();
	}

	public String getViewSlideShowURL() throws WindowStateException {
		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName", "/image_gallery_display/view_slide_show");
		portletURL.setParameter("folderId", String.valueOf(_getFolderId()));
		portletURL.setWindowState(LiferayWindowState.POP_UP);

		return portletURL.toString();
	}

	public boolean isAccessFromDesktopActionVisible() throws PortalException {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		if (!_hasViewPermission() || !portletDisplay.isWebDAVEnabled()) {
			return false;
		}

		Folder folder = _getFolder();

		if ((folder == null) ||
			(folder.getRepositoryId() == _themeDisplay.getScopeGroupId())) {

			return true;
		}

		return false;
	}

	public boolean isAddFileShortcutActionVisible() throws PortalException {
		String portletName = _portletName;

		if (!portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
			return false;
		}

		Folder folder = _getFolder();

		if ((folder != null) &&
			(folder.isMountPoint() || !folder.isSupportsShortcuts())) {

			return false;
		}

		if (DLFolderPermission.contains(
				_permissionChecker,
				_themeDisplay.getScopeGroupId(), _getFolderId(),
				ActionKeys.ADD_SHORTCUT)) {

			return true;
		}

		return false;
	}

	public boolean isAddFolderActionVisible() throws PortalException {
		if (DLFolderPermission.contains(
				_permissionChecker,
				_themeDisplay.getScopeGroupId(), _getFolderId(),
				ActionKeys.ADD_FOLDER)) {

			return true;
		}

		return false;
	}

	public boolean isAddMediaActionVisible() throws PortalException {
		String portletName = _portletName;

		if (!portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
			return false;
		}

		Folder folder = _getFolder();

		if ((folder != null) && DLFolderUtil.isRepositoryRoot(folder)) {
			return false;
		}

		if (DLFolderPermission.contains(
				_permissionChecker,
				_themeDisplay.getScopeGroupId(), _getFolderId(),
				ActionKeys.ADD_DOCUMENT)) {

			return true;
		}

		return false;
	}

	public boolean isAddRepositoryActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if (folder != null) {
			return false;
		}

		if (DLFolderPermission.contains(
				_permissionChecker,
				_themeDisplay.getScopeGroupId(), _getFolderId(),
				ActionKeys.ADD_REPOSITORY)) {

			return true;
		}

		return false;
	}

	public boolean isDeleteExpiredTemporaryFileEntriesActionVisible() {
		Folder folder = _getFolder();

		if (folder == null) {
			return false;
		}

		if (DLFolderUtil.isRepositoryRoot(folder) &&
			folder.isRepositoryCapabilityProvided(
				TemporaryFileEntriesCapability.class)) {

			return true;
		}

		return false;
	}

	public boolean isDeleteFolderActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if (folder == null) {
			return false;
		}

		if (DLFolderPermission.contains(
				_permissionChecker,
				_themeDisplay.getScopeGroupId(), _getFolderId(),
				ActionKeys.DELETE)) {

			return true;
		}

		return false;
	}

	public boolean isDownloadFolderActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if ((folder == null) ||
			RepositoryUtil.isExternalRepository(_getRepositoryId())) {

			return false;
		}

		if (_hasViewPermission()) {
			return true;
		}

		return false;
	}

	public boolean isEditFolderActionVisible() throws PortalException {
		if (!_isWorkflowEnabled()) {
			return false;
		}

		if (DLFolderPermission.contains(
				_permissionChecker,
				_themeDisplay.getScopeGroupId(), _getFolderId(),
				ActionKeys.UPDATE)) {

			return true;
		}

		return false;
	}

	public boolean isMoveFolderActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if ((folder == null) || DLFolderUtil.isRepositoryRoot(folder)) {
			return false;
		}

		if (DLFolderPermission.contains(
				_permissionChecker,
				_themeDisplay.getScopeGroupId(), _getFolderId(),
				ActionKeys.UPDATE)) {

			return true;
		}

		return false;
	}

	// Chintan - Copy Folder Functionality
	public boolean isCopyFolderActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if ((folder == null) || DLFolderUtil.isRepositoryRoot(folder)) {
			return false;
		}

		if (DLFolderPermission.contains(
				_permissionChecker,
				_themeDisplay.getScopeGroupId(), _getFolderId(),
				ActionKeys.UPDATE)) {

			return true;
		}

		return false;
	}

	public boolean isMultipleUploadSupported() {
		Folder folder = _getFolder();

		if ((folder == null) || folder.isSupportsMultipleUpload()) {
			return true;
		}

		return false;
	}

	public boolean isPermissionsActionVisible() throws PortalException {
		if (!_hasPermissionsPermission()) {
			return false;
		}

		Folder folder = _getFolder();

		if (folder == null) {
			return true;
		}

		if (!DLFolderUtil.isRepositoryRoot(folder)) {
			return true;
		}

		return false;
	}

	public boolean isPublishFolderActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if (folder == null) {
			return false;
		}

		String portletName = _portletName;

		if (!portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {
			return false;
		}

		if (!GroupPermissionUtil.contains(
				_permissionChecker,
				_themeDisplay.getScopeGroupId(),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO)) {

			return false;
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (!stagingGroupHelper.isStagingGroup(
				_themeDisplay.getScopeGroupId())) {

			return false;
		}

		if (!stagingGroupHelper.isStagedPortlet(
				_themeDisplay.getScopeGroupId(),
				DLPortletKeys.DOCUMENT_LIBRARY)) {

			return false;
		}

		return true;
	}

	public boolean isShowActions() throws PortalException {
		// DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper =
			// new DLPortletInstanceSettingsHelper(_dlRequestHelper);
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();
		DLPortletInstanceSettings dlPortletInstanceSettings = 
			DLPortletInstanceSettings.getInstance(_themeDisplay.getLayout(), portletDisplay.getId());

		// if (dlPortletInstanceSettingsHelper.isShowActions()) {
		if (dlPortletInstanceSettings.isShowActions()) {
			return true;
		}

		return false;
	}

	// public boolean isTrashEnabled() throws PortalException {
	// 	Folder folder = _getFolder();

	// 	if (((folder == null) ||
	// 		 folder.isRepositoryCapabilityProvided(TrashCapability.class)) &&
	// 		// _dlTrashUtil.isTrashEnabled(_themeDisplay.getScopeGroupId(), _getRepositoryId())
	// 		TrashUtil.isTrashEnabled(_themeDisplay.getScopeGroupId())
	// 		) {

	// 		return true;
	// 	}

	// 	return false;
	// }

	// TODO check this
	// public boolean isTrashEnabled() throws PortalException {
	// 	Folder folder = _getFolder();
		
	// 	if (((folder == null) ||
	// 		folder.isRepositoryCapabilityProvided(TrashCapability.class))) {
			
	// 		// Vérification si la corbeille est activée pour le groupe
	// 		try {
	// 			TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
	// 				"com.liferay.document.library.kernel.model.DLFolder");
	// 			return trashHandler != null;
	// 		} catch (Exception e) {
	// 			return false;
	// 		}
	// 	}
		
	// 	return false;
	// }

	public boolean isTrashEnabled() throws PortalException {
		Folder folder = _getFolder();

		if (((folder == null) ||
			folder.isRepositoryCapabilityProvided(TrashCapability.class)) &&
			_isTrashEnabled(_themeDisplay.getScopeGroupId(), _getRepositoryId())) {

			return true;
		}

		return false;
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


	public boolean isViewSlideShowActionVisible() throws PortalException {
		String portletName = _portletName;

		if (!portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
			return false;
		}

		if (!_hasViewPermission()) {
			return false;
		}

		int fileEntriesAndFileShortcutsCount =
			DLAppServiceUtil.getFileEntriesAndFileShortcutsCount(
				_getRepositoryId(), _getFolderId(), _getStatus());

		if (fileEntriesAndFileShortcutsCount == 0) {
			return false;
		}

		return true;
	}

	private String _getDeleteFolderCommand() throws PortalException {
		Folder folder = _getFolder();

		if (DLFolderUtil.isRepositoryRoot(folder)) {
			return Constants.DELETE;
		}

		if (isTrashEnabled()) {
			return Constants.MOVE_TO_TRASH;
		}

		return Constants.DELETE;
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

	private long _getFolderId() {
		Folder folder = _getFolder();

		if (folder == null) {
			return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return folder.getFolderId();
	}

	private String _getParentFolderURL() {
		if (!_isView()) {
			return _currentURL;
		}

		String portletName = _portletName;

		String mvcRenderCommandName = "/image_gallery_display/view";

		if (!portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
			mvcRenderCommandName = "/document_library/view";

			Folder folder = _getFolder();

			if ((folder != null) && !DLFolderUtil.isRepositoryRoot(folder) &&
				(folder.getParentFolderId() !=
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

				mvcRenderCommandName = "/document_library/view_folder";
			}
		}

		Folder folder = _getFolder();

		if (folder == null) {
			return StringPool.BLANK;
		}

		LiferayPortletResponse liferayPortletResponse =
			_liferayPortletResponse;

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		portletURL.setParameter("mvcRenderCommandName", mvcRenderCommandName);

		if (DLFolderUtil.isRepositoryRoot(folder)) {
			portletURL.setParameter(
				"folderId",
				String.valueOf(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
		}
		else {
			portletURL.setParameter(
				"folderId", String.valueOf(folder.getParentFolderId()));
		}

		return portletURL.toString();
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

	private int _getStatus() {
		if (_status != null) {
			return _status;
		}

		ThemeDisplay themeDisplay = _themeDisplay;

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isContentReviewer(
				_themeDisplay.getCompanyId(),
				_themeDisplay.getScopeGroupId())) {

			_status = WorkflowConstants.STATUS_ANY;
		}
		else {
			_status = WorkflowConstants.STATUS_APPROVED;
		}

		return _status;
	}

	private boolean _hasPermissionsPermission() throws PortalException {
		Folder folder = _getFolder();

		if (folder != null) {
			return DLFolderPermission.contains(
				_permissionChecker, folder,
				ActionKeys.PERMISSIONS);
		}

		return DLPermission.contains(
			_permissionChecker,
			_themeDisplay.getScopeGroupId(), ActionKeys.PERMISSIONS);
	}

	private boolean _hasViewPermission() throws PortalException {
		return DLFolderPermission.contains(
			_permissionChecker,
			_themeDisplay.getScopeGroupId(), _getFolderId(),
			ActionKeys.VIEW);
	}

	private boolean _isView() {
		if (_view != null) {
			return _view;
		}

		Folder folder = (Folder)_httpServletRequest.getAttribute(
			"info_panel.jsp-folder");

		if ((folder != null) && (folder.getFolderId() == _getFolderId())) {
			return true;
		}

		ResultRow row = (ResultRow)_httpServletRequest.getAttribute(
			WebKeys.SEARCH_CONTAINER_RESULT_ROW);

		String portletName = _portletName;

		if ((row == null) &&
			portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {

			_view = true;
		}
		else {
			_view = false;
		}

		return _view;
	}

	// private boolean _isWorkflowEnabled() {
	// 	if (!WorkflowEngineManagerUtil.isDeployed()) {
	// 		return false;
	// 	}

	// 	WorkflowHandler<Object> workflowHandler =
	// 		WorkflowHandlerRegistryUtil.getWorkflowHandler(
	// 			DLFileEntry.class.getName());

	// 	if (workflowHandler == null) {
	// 		return false;
	// 	}

	// 	return true;
	// }

	// Remplacer dans la méthode _isWorkflowEnabled()
    // private boolean _isWorkflowEnabled() {
    //     try {
    //         return WorkflowDefinitionManagerUtil.getActiveWorkflowDefinitionNames(
    //             _themeDisplay.getCompanyId()).size() > 0;
    //     } catch (Exception e) {
    //         return false;
    //     }

    //     WorkflowHandler<Object> workflowHandler =
    //         WorkflowHandlerRegistryUtil.getWorkflowHandler(
    //             DLFileEntry.class.getName());

    //     if (workflowHandler == null) {
    //         return false;
    //     }

    //     return true;
    // }
	private boolean _isWorkflowEnabled() {
		try {
			// Simple vérification de l'existence du WorkflowHandler
			WorkflowHandler<Object> workflowHandler =
				WorkflowHandlerRegistryUtil.getWorkflowHandler(
					DLFileEntry.class.getName());

			return workflowHandler != null;
			
		} catch (Exception e) {
			// En cas de problème, on considère que le workflow n'est pas activé
			return false;
		}
	}

	// private final DLRequestHelper _dlRequestHelper;
	// private final DLTrashUtil _dlTrashUtil;
	private Folder _folder;
	private final HttpServletRequest _httpServletRequest;
	private String _randomNamespace;
	private Long _repositoryId;
	private Integer _status;
	private Boolean _view;

}