package org.apache.jsp.document_005flibrary;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.util.AssetHelper;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLDisplayContextProvider;
import com.liferay.document.library.display.context.DLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLFilePicker;
import com.liferay.document.library.display.context.DLViewFileEntryHistoryDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryTypeException;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.DuplicateRepositoryNameException;
import com.liferay.document.library.kernel.exception.FileEntryLockException;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileShortcutPermissionException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.FolderNameException;
import com.liferay.document.library.kernel.exception.InvalidFileVersionException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.exception.NoSuchMetadataSetException;
import com.liferay.document.library.kernel.exception.RepositoryNameException;
import com.liferay.document.library.kernel.exception.RequiredFileEntryTypeException;
import com.liferay.document.library.kernel.exception.RequiredFileException;
import com.liferay.document.library.kernel.exception.SourceFileNameException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeServiceUtil;
import com.liferay.document.library.kernel.util.AudioProcessorUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.ImageProcessorUtil;
import com.liferay.document.library.kernel.util.PDFProcessorUtil;
import com.liferay.document.library.kernel.util.RawMetadataProcessor;
import com.liferay.document.library.kernel.util.VideoProcessorUtil;
import com.liferay.document.library.preview.exception.DLPreviewGenerationInProcessException;
import com.liferay.document.library.preview.exception.DLPreviewSizeException;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.document.library.web.internal.constants.DLWebKeys;
import com.liferay.document.library.web.internal.dao.search.DLResultRowSplitter;
import com.liferay.document.library.web.internal.dao.search.IGResultRowSplitter;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContextProvider;
import com.liferay.document.library.web.internal.display.context.DLAdminManagementToolbarDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLAdminNavigationDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLSelectRestrictedFileEntryTypesDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLViewFileEntryTypesDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLViewMoreMenuItemsDisplayContext;
import com.liferay.document.library.web.internal.display.context.IGDisplayContextProvider;
import com.liferay.document.library.web.internal.display.context.logic.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.logic.DLVisualizationHelper;
import com.liferay.document.library.web.internal.display.context.util.DLRequestHelper;
import com.liferay.document.library.web.internal.display.context.util.IGRequestHelper;
import com.liferay.document.library.web.internal.dynamic.data.mapping.util.DLDDMDisplay;
import com.liferay.document.library.web.internal.portlet.action.ActionUtil;
import com.liferay.document.library.web.internal.portlet.action.EditFileEntryMVCActionCommand;
import com.liferay.document.library.web.internal.search.EntriesChecker;
import com.liferay.document.library.web.internal.search.EntriesMover;
import com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryPermission;
import com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryTypePermission;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.document.library.web.internal.util.DLBreadcrumbUtil;
import com.liferay.document.library.web.internal.util.DLSubscriptionUtil;
import com.liferay.document.library.web.internal.util.DLTrashUtil;
import com.liferay.document.library.web.internal.util.DLWebComponentProvider;
import com.liferay.document.library.web.internal.util.IGUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMStructure;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureManager;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureManagerUtil;
import com.liferay.dynamic.data.mapping.kernel.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.kernel.StorageFieldRequiredException;
import com.liferay.dynamic.data.mapping.kernel.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.kernel.StructureDuplicateElementException;
import com.liferay.dynamic.data.mapping.kernel.StructureNameException;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMDisplay;
import com.liferay.dynamic.data.mapping.util.DDMNavigationHelper;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPDropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList;
import com.liferay.image.gallery.display.kernel.display.context.IGViewFileVersionDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.InvalidRepositoryException;
import com.liferay.portal.kernel.exception.NoSuchRepositoryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.language.UnicodeLanguageUtil;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.repository.AuthenticationRepositoryException;
import com.liferay.portal.kernel.repository.RepositoryConfiguration;
import com.liferay.portal.kernel.repository.RepositoryException;
import com.liferay.portal.kernel.repository.capabilities.CommentCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.ToolbarItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.UnicodeFormatter;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowEngineManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.repository.registry.RepositoryClassDefinition;
import com.liferay.portal.repository.registry.RepositoryClassDefinitionCatalogUtil;
import com.liferay.portal.upload.LiferayFileItem;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.DLGroupServiceSettings;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.portlet.usersadmin.search.GroupSearch;
import com.liferay.portlet.usersadmin.search.GroupSearchTerms;
import com.liferay.taglib.search.ResultRow;
import com.liferay.taglib.servlet.PipingServletResponse;
import com.liferay.taglib.util.PortalIncludeUtil;
import com.liferay.trash.model.TrashEntry;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.web.internal.bulk.selection.BulkSelectionRunnerUtil;
import com.liferay.document.library.web.internal.display.context.FolderActionDisplayContext;
import com.liferay.document.library.web.internal.util.DLAssetHelperUtil;
import com.liferay.document.library.web.internal.util.RepositoryClassDefinitionUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.util.RepositoryUtil;

public final class view_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList<String>(5);
    _jspx_dependants.add("/document_library/init.jsp");
    _jspx_dependants.add("/init.jsp");
    _jspx_dependants.add("/init-ext.jsp");
    _jspx_dependants.add("/document_library/init-ext.jsp");
    _jspx_dependants.add("/document_library/file_entries_template.jspf");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_actionURL_var_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_arguments_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_param_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_a_href;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_script;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_error_message_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1portlet_renderURL_varImpl;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_type_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_choose;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_buffer_var;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_script_use;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_component_module_context_containerId_componentId_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_include_servletContext_page_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_when_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_namespace_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1portlet_actionURL_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_sidebar$1panel_searchContainerId_resourceURL;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1trash_undo_portletURL_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_vertical$1card$1header;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_value_type_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_soy_component$1renderer_templateNamespace_module_context_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_actionURL_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_renderURL_var;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_otherwise;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_error_message_exception_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_form_name_method_action;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_renderURL_windowState;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_imageUrl_cssClass;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_resourceURL_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_renderURL;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_portlet_actionURL_var_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_param_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_a_href = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_script = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_error_message_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1portlet_renderURL_varImpl = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_type_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_choose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_buffer_var = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_script_use = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_component_module_context_containerId_componentId_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_when_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_namespace_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1portlet_actionURL_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_sidebar$1panel_searchContainerId_resourceURL = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1trash_undo_portletURL_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_vertical$1card$1header = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_value_type_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_soy_component$1renderer_templateNamespace_module_context_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_actionURL_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_renderURL_var = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_otherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_error_message_exception_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_form_name_method_action = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_renderURL_windowState = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_imageUrl_cssClass = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_resourceURL_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_renderURL = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_portlet_actionURL_var_name.release();
    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.release();
    _jspx_tagPool_portlet_param_value_name_nobody.release();
    _jspx_tagPool_aui_a_href.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_aui_script.release();
    _jspx_tagPool_liferay$1ui_error_message_key_nobody.release();
    _jspx_tagPool_liferay$1portlet_renderURL_varImpl.release();
    _jspx_tagPool_aui_input_type_name_nobody.release();
    _jspx_tagPool_c_choose.release();
    _jspx_tagPool_liferay$1util_buffer_var.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_aui_script_use.release();
    _jspx_tagPool_liferay$1frontend_component_module_context_containerId_componentId_nobody.release();
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.release();
    _jspx_tagPool_c_when_test.release();
    _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters.release();
    _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.release();
    _jspx_tagPool_portlet_namespace_nobody.release();
    _jspx_tagPool_liferay$1portlet_actionURL_name.release();
    _jspx_tagPool_liferay$1frontend_sidebar$1panel_searchContainerId_resourceURL.release();
    _jspx_tagPool_liferay$1trash_undo_portletURL_nobody.release();
    _jspx_tagPool_liferay$1frontend_vertical$1card$1header.release();
    _jspx_tagPool_aui_input_value_type_name_nobody.release();
    _jspx_tagPool_soy_component$1renderer_templateNamespace_module_context_nobody.release();
    _jspx_tagPool_portlet_actionURL_name_nobody.release();
    _jspx_tagPool_portlet_renderURL_var.release();
    _jspx_tagPool_c_otherwise.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1ui_error_message_exception_nobody.release();
    _jspx_tagPool_aui_form_name_method_action.release();
    _jspx_tagPool_portlet_renderURL_windowState.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_imageUrl_cssClass.release();
    _jspx_tagPool_portlet_resourceURL_id.release();
    _jspx_tagPool_portlet_renderURL.release();
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
      //  liferay-frontend:defineObjects
      com.liferay.frontend.taglib.servlet.taglib.DefineObjectsTag _jspx_th_liferay$1frontend_defineObjects_0 = (com.liferay.frontend.taglib.servlet.taglib.DefineObjectsTag) _jspx_tagPool_liferay$1frontend_defineObjects_nobody.get(com.liferay.frontend.taglib.servlet.taglib.DefineObjectsTag.class);
      _jspx_th_liferay$1frontend_defineObjects_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1frontend_defineObjects_0.setParent(null);
      int _jspx_eval_liferay$1frontend_defineObjects_0 = _jspx_th_liferay$1frontend_defineObjects_0.doStartTag();
      if (_jspx_th_liferay$1frontend_defineObjects_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1frontend_defineObjects_nobody.reuse(_jspx_th_liferay$1frontend_defineObjects_0);
        return;
      }
      _jspx_tagPool_liferay$1frontend_defineObjects_nobody.reuse(_jspx_th_liferay$1frontend_defineObjects_0);
      java.lang.String currentURL = null;
      javax.portlet.PortletURL currentURLObj = null;
      java.lang.String npmResolvedPackageName = null;
      java.util.ResourceBundle resourceBundle = null;
      javax.portlet.WindowState windowState = null;
      currentURL = (java.lang.String) _jspx_page_context.findAttribute("currentURL");
      currentURLObj = (javax.portlet.PortletURL) _jspx_page_context.findAttribute("currentURLObj");
      npmResolvedPackageName = (java.lang.String) _jspx_page_context.findAttribute("npmResolvedPackageName");
      resourceBundle = (java.util.ResourceBundle) _jspx_page_context.findAttribute("resourceBundle");
      windowState = (javax.portlet.WindowState) _jspx_page_context.findAttribute("windowState");
      out.write('\n');
      out.write('\n');
      //  liferay-theme:defineObjects
      com.liferay.taglib.theme.DefineObjectsTag _jspx_th_liferay$1theme_defineObjects_0 = (com.liferay.taglib.theme.DefineObjectsTag) _jspx_tagPool_liferay$1theme_defineObjects_nobody.get(com.liferay.taglib.theme.DefineObjectsTag.class);
      _jspx_th_liferay$1theme_defineObjects_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1theme_defineObjects_0.setParent(null);
      int _jspx_eval_liferay$1theme_defineObjects_0 = _jspx_th_liferay$1theme_defineObjects_0.doStartTag();
      if (_jspx_th_liferay$1theme_defineObjects_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1theme_defineObjects_nobody.reuse(_jspx_th_liferay$1theme_defineObjects_0);
        return;
      }
      _jspx_tagPool_liferay$1theme_defineObjects_nobody.reuse(_jspx_th_liferay$1theme_defineObjects_0);
      com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay = null;
      com.liferay.portal.kernel.model.Company company = null;
      com.liferay.portal.kernel.model.Account account = null;
      com.liferay.portal.kernel.model.User user = null;
      com.liferay.portal.kernel.model.User realUser = null;
      com.liferay.portal.kernel.model.Contact contact = null;
      com.liferay.portal.kernel.model.Layout layout = null;
      java.util.List layouts = null;
      java.lang.Long plid = null;
      com.liferay.portal.kernel.model.LayoutTypePortlet layoutTypePortlet = null;
      java.lang.Long scopeGroupId = null;
      com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker = null;
      java.util.Locale locale = null;
      java.util.TimeZone timeZone = null;
      com.liferay.portal.kernel.model.Theme theme = null;
      com.liferay.portal.kernel.model.ColorScheme colorScheme = null;
      com.liferay.portal.kernel.theme.PortletDisplay portletDisplay = null;
      java.lang.Long portletGroupId = null;
      themeDisplay = (com.liferay.portal.kernel.theme.ThemeDisplay) _jspx_page_context.findAttribute("themeDisplay");
      company = (com.liferay.portal.kernel.model.Company) _jspx_page_context.findAttribute("company");
      account = (com.liferay.portal.kernel.model.Account) _jspx_page_context.findAttribute("account");
      user = (com.liferay.portal.kernel.model.User) _jspx_page_context.findAttribute("user");
      realUser = (com.liferay.portal.kernel.model.User) _jspx_page_context.findAttribute("realUser");
      contact = (com.liferay.portal.kernel.model.Contact) _jspx_page_context.findAttribute("contact");
      layout = (com.liferay.portal.kernel.model.Layout) _jspx_page_context.findAttribute("layout");
      layouts = (java.util.List) _jspx_page_context.findAttribute("layouts");
      plid = (java.lang.Long) _jspx_page_context.findAttribute("plid");
      layoutTypePortlet = (com.liferay.portal.kernel.model.LayoutTypePortlet) _jspx_page_context.findAttribute("layoutTypePortlet");
      scopeGroupId = (java.lang.Long) _jspx_page_context.findAttribute("scopeGroupId");
      permissionChecker = (com.liferay.portal.kernel.security.permission.PermissionChecker) _jspx_page_context.findAttribute("permissionChecker");
      locale = (java.util.Locale) _jspx_page_context.findAttribute("locale");
      timeZone = (java.util.TimeZone) _jspx_page_context.findAttribute("timeZone");
      theme = (com.liferay.portal.kernel.model.Theme) _jspx_page_context.findAttribute("theme");
      colorScheme = (com.liferay.portal.kernel.model.ColorScheme) _jspx_page_context.findAttribute("colorScheme");
      portletDisplay = (com.liferay.portal.kernel.theme.PortletDisplay) _jspx_page_context.findAttribute("portletDisplay");
      portletGroupId = (java.lang.Long) _jspx_page_context.findAttribute("portletGroupId");
      out.write('\n');
      out.write('\n');
      //  portlet:defineObjects
      com.liferay.taglib.portlet.DefineObjectsTag _jspx_th_portlet_defineObjects_0 = (com.liferay.taglib.portlet.DefineObjectsTag) _jspx_tagPool_portlet_defineObjects_nobody.get(com.liferay.taglib.portlet.DefineObjectsTag.class);
      _jspx_th_portlet_defineObjects_0.setPageContext(_jspx_page_context);
      _jspx_th_portlet_defineObjects_0.setParent(null);
      int _jspx_eval_portlet_defineObjects_0 = _jspx_th_portlet_defineObjects_0.doStartTag();
      if (_jspx_th_portlet_defineObjects_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_portlet_defineObjects_nobody.reuse(_jspx_th_portlet_defineObjects_0);
        return;
      }
      _jspx_tagPool_portlet_defineObjects_nobody.reuse(_jspx_th_portlet_defineObjects_0);
      javax.portlet.ActionRequest actionRequest = null;
      javax.portlet.ActionResponse actionResponse = null;
      javax.portlet.EventRequest eventRequest = null;
      javax.portlet.EventResponse eventResponse = null;
      com.liferay.portal.kernel.portlet.LiferayPortletRequest liferayPortletRequest = null;
      com.liferay.portal.kernel.portlet.LiferayPortletResponse liferayPortletResponse = null;
      javax.portlet.PortletConfig portletConfig = null;
      java.lang.String portletName = null;
      javax.portlet.PortletPreferences portletPreferences = null;
      java.util.Map portletPreferencesValues = null;
      javax.portlet.PortletSession portletSession = null;
      java.util.Map portletSessionScope = null;
      javax.portlet.RenderRequest renderRequest = null;
      javax.portlet.RenderResponse renderResponse = null;
      javax.portlet.ResourceRequest resourceRequest = null;
      javax.portlet.ResourceResponse resourceResponse = null;
      actionRequest = (javax.portlet.ActionRequest) _jspx_page_context.findAttribute("actionRequest");
      actionResponse = (javax.portlet.ActionResponse) _jspx_page_context.findAttribute("actionResponse");
      eventRequest = (javax.portlet.EventRequest) _jspx_page_context.findAttribute("eventRequest");
      eventResponse = (javax.portlet.EventResponse) _jspx_page_context.findAttribute("eventResponse");
      liferayPortletRequest = (com.liferay.portal.kernel.portlet.LiferayPortletRequest) _jspx_page_context.findAttribute("liferayPortletRequest");
      liferayPortletResponse = (com.liferay.portal.kernel.portlet.LiferayPortletResponse) _jspx_page_context.findAttribute("liferayPortletResponse");
      portletConfig = (javax.portlet.PortletConfig) _jspx_page_context.findAttribute("portletConfig");
      portletName = (java.lang.String) _jspx_page_context.findAttribute("portletName");
      portletPreferences = (javax.portlet.PortletPreferences) _jspx_page_context.findAttribute("portletPreferences");
      portletPreferencesValues = (java.util.Map) _jspx_page_context.findAttribute("portletPreferencesValues");
      portletSession = (javax.portlet.PortletSession) _jspx_page_context.findAttribute("portletSession");
      portletSessionScope = (java.util.Map) _jspx_page_context.findAttribute("portletSessionScope");
      renderRequest = (javax.portlet.RenderRequest) _jspx_page_context.findAttribute("renderRequest");
      renderResponse = (javax.portlet.RenderResponse) _jspx_page_context.findAttribute("renderResponse");
      resourceRequest = (javax.portlet.ResourceRequest) _jspx_page_context.findAttribute("resourceRequest");
      resourceResponse = (javax.portlet.ResourceResponse) _jspx_page_context.findAttribute("resourceResponse");
      out.write('\n');
      out.write('\n');

DLTrashUtil dlTrashUtil = (DLTrashUtil)request.getAttribute(DLWebKeys.DOCUMENT_LIBRARY_TRASH_UTIL);

DLWebComponentProvider dlWebComponentProvider = DLWebComponentProvider.getDLWebComponentProvider();

DLAdminDisplayContextProvider dlAdminDisplayContextProvider = dlWebComponentProvider.getDLAdminDisplayContextProvider();
DLDisplayContextProvider dlDisplayContextProvider = dlWebComponentProvider.getDLDisplayContextProvider();
IGDisplayContextProvider igDisplayContextProvider = dlWebComponentProvider.getIGDisplayContextProvider();

Format dateFormatDate = FastDateFormatFactoryUtil.getDate(locale, timeZone);
Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);

      out.write('\n');
      out.write('\n');
      out.write("\n\n\n\n\n\n");

DLRequestHelper dlRequestHelper = new DLRequestHelper(request);

String portletId = dlRequestHelper.getResourcePortletId();

portletName = dlRequestHelper.getResourcePortletName();

String portletResource = dlRequestHelper.getPortletResource();

DLAdminDisplayContext dlAdminDisplayContext = dlAdminDisplayContextProvider.getDLAdminDisplayContext(request, response);

DLAdminManagementToolbarDisplayContext dlAdminManagementToolbarDisplayContext = dlAdminDisplayContextProvider.getDLAdminManagementToolbarDisplayContext(request, response);

DLConfiguration dlConfiguration = ConfigurationProviderUtil.getSystemConfiguration(DLConfiguration.class);
DLGroupServiceSettings dlGroupServiceSettings = dlRequestHelper.getDLGroupServiceSettings();
DLPortletInstanceSettings dlPortletInstanceSettings = dlRequestHelper.getDLPortletInstanceSettings();

long rootFolderId = dlAdminDisplayContext.getRootFolderId();
String rootFolderName = dlAdminDisplayContext.getRootFolderName();

boolean showComments = ParamUtil.getBoolean(request, "showComments", true);
boolean showHeader = ParamUtil.getBoolean(request, "showHeader", true);

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');

String navigation = ParamUtil.getString(request, "navigation");

      out.write('\n');
      out.write('\n');
      //  c:choose
      org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_0 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
      _jspx_th_c_choose_0.setPageContext(_jspx_page_context);
      _jspx_th_c_choose_0.setParent(null);
      int _jspx_eval_c_choose_0 = _jspx_th_c_choose_0.doStartTag();
      if (_jspx_eval_c_choose_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\n');
          out.write('	');
          //  c:when
          org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_0 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
          _jspx_th_c_when_0.setPageContext(_jspx_page_context);
          _jspx_th_c_when_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
          _jspx_th_c_when_0.setTest( navigation.equals("file_entry_types") );
          int _jspx_eval_c_when_0 = _jspx_th_c_when_0.doStartTag();
          if (_jspx_eval_c_when_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t");
              //  liferay-util:include
              com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_0 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
              _jspx_th_liferay$1util_include_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_include_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
              _jspx_th_liferay$1util_include_0.setPage("/document_library/view_file_entry_types.jsp");
              _jspx_th_liferay$1util_include_0.setServletContext( application );
              int _jspx_eval_liferay$1util_include_0 = _jspx_th_liferay$1util_include_0.doStartTag();
              if (_jspx_th_liferay$1util_include_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
                return;
              }
              _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
              out.write('\n');
              out.write('	');
              int evalDoAfterBody = _jspx_th_c_when_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_when_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_0);
            return;
          }
          _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_0);
          out.write('\n');
          out.write('	');
          //  c:otherwise
          org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_0 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
          _jspx_th_c_otherwise_0.setPageContext(_jspx_page_context);
          _jspx_th_c_otherwise_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
          int _jspx_eval_c_otherwise_0 = _jspx_th_c_otherwise_0.doStartTag();
          if (_jspx_eval_c_otherwise_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t");
              if (_jspx_meth_liferay$1util_dynamic$1include_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_otherwise_0, _jspx_page_context))
                return;
              out.write("\n\n\t\t");

		DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(dlRequestHelper);

		String mvcRenderCommandName = ParamUtil.getString(request, "mvcRenderCommandName");

		boolean defaultFolderView = dlAdminDisplayContext.isDefaultFolderView();

		String displayStyle = dlAdminDisplayContext.getDisplayStyle();

		Folder folder = dlAdminDisplayContext.getFolder();

		long folderId = dlAdminDisplayContext.getFolderId();

		long repositoryId = dlAdminDisplayContext.getRepositoryId();

		request.setAttribute("view.jsp-folder", folder);

		request.setAttribute("view.jsp-folderId", String.valueOf(folderId));

		request.setAttribute("view.jsp-repositoryId", String.valueOf(repositoryId));

		request.setAttribute("view.jsp-displayStyle", displayStyle);
		
              out.write("\n\n\t\t");
              //  liferay-util:buffer
              com.liferay.taglib.util.BufferTag _jspx_th_liferay$1util_buffer_0 = (com.liferay.taglib.util.BufferTag) _jspx_tagPool_liferay$1util_buffer_var.get(com.liferay.taglib.util.BufferTag.class);
              _jspx_th_liferay$1util_buffer_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_buffer_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_liferay$1util_buffer_0.setVar("uploadURL");
              int _jspx_eval_liferay$1util_buffer_0 = _jspx_th_liferay$1util_buffer_0.doStartTag();
              if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_liferay$1util_buffer_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_liferay$1util_buffer_0.doInitBody();
                }
                do {
                  //  liferay-portlet:actionURL
                  com.liferay.taglib.portlet.ActionURLTag _jspx_th_liferay$1portlet_actionURL_0 = (com.liferay.taglib.portlet.ActionURLTag) _jspx_tagPool_liferay$1portlet_actionURL_name.get(com.liferay.taglib.portlet.ActionURLTag.class);
                  _jspx_th_liferay$1portlet_actionURL_0.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1portlet_actionURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1util_buffer_0);
                  _jspx_th_liferay$1portlet_actionURL_0.setName("/document_library/edit_file_entry");
                  int _jspx_eval_liferay$1portlet_actionURL_0 = _jspx_th_liferay$1portlet_actionURL_0.doStartTag();
                  if (_jspx_eval_liferay$1portlet_actionURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_0 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_0.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_actionURL_0);
                    _jspx_th_portlet_param_0.setName( Constants.CMD );
                    _jspx_th_portlet_param_0.setValue( Constants.ADD_DYNAMIC );
                    int _jspx_eval_portlet_param_0 = _jspx_th_portlet_param_0.doStartTag();
                    if (_jspx_th_portlet_param_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
                    if (_jspx_meth_portlet_param_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1portlet_actionURL_0, _jspx_page_context))
                      return;
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_2 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_2.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_actionURL_0);
                    _jspx_th_portlet_param_2.setName("repositoryId");
                    _jspx_th_portlet_param_2.setValue( String.valueOf(repositoryId) );
                    int _jspx_eval_portlet_param_2 = _jspx_th_portlet_param_2.doStartTag();
                    if (_jspx_th_portlet_param_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_2);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_2);
                  }
                  if (_jspx_th_liferay$1portlet_actionURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1portlet_actionURL_name.reuse(_jspx_th_liferay$1portlet_actionURL_0);
                    return;
                  }
                  _jspx_tagPool_liferay$1portlet_actionURL_name.reuse(_jspx_th_liferay$1portlet_actionURL_0);
                  int evalDoAfterBody = _jspx_th_liferay$1util_buffer_0.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
                if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                  out = _jspx_page_context.popBody();
              }
              if (_jspx_th_liferay$1util_buffer_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_buffer_var.reuse(_jspx_th_liferay$1util_buffer_0);
                return;
              }
              _jspx_tagPool_liferay$1util_buffer_var.reuse(_jspx_th_liferay$1util_buffer_0);
              java.lang.String uploadURL = null;
              uploadURL = (java.lang.String) _jspx_page_context.findAttribute("uploadURL");
              out.write("\n\n\t\t");
              //  portlet:actionURL
              com.liferay.taglib.portlet.ActionURLTag _jspx_th_portlet_actionURL_0 = (com.liferay.taglib.portlet.ActionURLTag) _jspx_tagPool_portlet_actionURL_var_name.get(com.liferay.taglib.portlet.ActionURLTag.class);
              _jspx_th_portlet_actionURL_0.setPageContext(_jspx_page_context);
              _jspx_th_portlet_actionURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_portlet_actionURL_0.setName("/document_library/edit_entry");
              _jspx_th_portlet_actionURL_0.setVar("restoreTrashEntriesURL");
              int _jspx_eval_portlet_actionURL_0 = _jspx_th_portlet_actionURL_0.doStartTag();
              if (_jspx_eval_portlet_actionURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t");
                //  portlet:param
                com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_3 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                _jspx_th_portlet_param_3.setPageContext(_jspx_page_context);
                _jspx_th_portlet_param_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_actionURL_0);
                _jspx_th_portlet_param_3.setName( Constants.CMD );
                _jspx_th_portlet_param_3.setValue( Constants.RESTORE );
                int _jspx_eval_portlet_param_3 = _jspx_th_portlet_param_3.doStartTag();
                if (_jspx_th_portlet_param_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_3);
                  return;
                }
                _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_3);
                out.write("\n\t\t");
              }
              if (_jspx_th_portlet_actionURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_portlet_actionURL_var_name.reuse(_jspx_th_portlet_actionURL_0);
                return;
              }
              _jspx_tagPool_portlet_actionURL_var_name.reuse(_jspx_th_portlet_actionURL_0);
              java.lang.String restoreTrashEntriesURL = null;
              restoreTrashEntriesURL = (java.lang.String) _jspx_page_context.findAttribute("restoreTrashEntriesURL");
              out.write("\n\n\t\t");
              //  liferay-trash:undo
              com.liferay.trash.taglib.servlet.taglib.UndoTag _jspx_th_liferay$1trash_undo_0 = (com.liferay.trash.taglib.servlet.taglib.UndoTag) _jspx_tagPool_liferay$1trash_undo_portletURL_nobody.get(com.liferay.trash.taglib.servlet.taglib.UndoTag.class);
              _jspx_th_liferay$1trash_undo_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1trash_undo_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_liferay$1trash_undo_0.setPortletURL( restoreTrashEntriesURL );
              int _jspx_eval_liferay$1trash_undo_0 = _jspx_th_liferay$1trash_undo_0.doStartTag();
              if (_jspx_th_liferay$1trash_undo_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1trash_undo_portletURL_nobody.reuse(_jspx_th_liferay$1trash_undo_0);
                return;
              }
              _jspx_tagPool_liferay$1trash_undo_portletURL_nobody.reuse(_jspx_th_liferay$1trash_undo_0);
              out.write("\n\n\t\t");
              //  liferay-util:include
              com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_1 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
              _jspx_th_liferay$1util_include_1.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_include_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_liferay$1util_include_1.setPage("/document_library/navigation.jsp");
              _jspx_th_liferay$1util_include_1.setServletContext( application );
              int _jspx_eval_liferay$1util_include_1 = _jspx_th_liferay$1util_include_1.doStartTag();
              if (_jspx_th_liferay$1util_include_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
                return;
              }
              _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
              out.write("\n\n\t\t");
              //  liferay-util:include
              com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_2 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
              _jspx_th_liferay$1util_include_2.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_include_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_liferay$1util_include_2.setPage("/document_library/toolbar.jsp");
              _jspx_th_liferay$1util_include_2.setServletContext( application );
              int _jspx_eval_liferay$1util_include_2 = _jspx_th_liferay$1util_include_2.doStartTag();
              if (_jspx_th_liferay$1util_include_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
                return;
              }
              _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
              out.write("\n\n\t\t");

		BulkSelectionRunner bulkSelectionRunner = BulkSelectionRunnerUtil.getBulkSelectionRunner();

		Map<String, Object> context = new HashMap<>();

		context.put("bulkInProgress", bulkSelectionRunner.isBusy(user));
		context.put("pathModule", PortalUtil.getPathModule());
		context.put("portletNamespace", liferayPortletResponse.getNamespace());
		
              out.write("\n\n\t\t");
              //  soy:component-renderer
              com.liferay.frontend.taglib.soy.servlet.taglib.ComponentRendererTag _jspx_th_soy_component$1renderer_0 = (com.liferay.frontend.taglib.soy.servlet.taglib.ComponentRendererTag) _jspx_tagPool_soy_component$1renderer_templateNamespace_module_context_nobody.get(com.liferay.frontend.taglib.soy.servlet.taglib.ComponentRendererTag.class);
              _jspx_th_soy_component$1renderer_0.setPageContext(_jspx_page_context);
              _jspx_th_soy_component$1renderer_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_soy_component$1renderer_0.setContext( context );
              _jspx_th_soy_component$1renderer_0.setModule("document_library/js/bulk/BulkStatus.es");
              _jspx_th_soy_component$1renderer_0.setTemplateNamespace("com.liferay.document.library.web.BulkStatus.render");
              int _jspx_eval_soy_component$1renderer_0 = _jspx_th_soy_component$1renderer_0.doStartTag();
              if (_jspx_th_soy_component$1renderer_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_soy_component$1renderer_templateNamespace_module_context_nobody.reuse(_jspx_th_soy_component$1renderer_0);
                return;
              }
              _jspx_tagPool_soy_component$1renderer_templateNamespace_module_context_nobody.reuse(_jspx_th_soy_component$1renderer_0);
              out.write("\n\n\t\t<div id=\"");
              if (_jspx_meth_portlet_namespace_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_otherwise_0, _jspx_page_context))
                return;
              out.write("documentLibraryContainer\">\n\n\t\t\t");

			boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));
			
              out.write("\n\n\t\t\t<div class=\"closed ");
              out.print( portletTitleBasedNavigation ? "container-fluid-1280" : StringPool.BLANK );
              out.write(" sidenav-container sidenav-right\" id=\"");
              if (_jspx_meth_portlet_namespace_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_otherwise_0, _jspx_page_context))
                return;
              out.write("infoPanelId\">\n\t\t\t\t");
              //  liferay-portlet:resourceURL
              com.liferay.taglib.portlet.ResourceURLTag _jspx_th_liferay$1portlet_resourceURL_0 = (com.liferay.taglib.portlet.ResourceURLTag) _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters.get(com.liferay.taglib.portlet.ResourceURLTag.class);
              _jspx_th_liferay$1portlet_resourceURL_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1portlet_resourceURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_liferay$1portlet_resourceURL_0.setCopyCurrentRenderParameters( false );
              _jspx_th_liferay$1portlet_resourceURL_0.setId("/document_library/info_panel");
              _jspx_th_liferay$1portlet_resourceURL_0.setVar("sidebarPanelURL");
              int _jspx_eval_liferay$1portlet_resourceURL_0 = _jspx_th_liferay$1portlet_resourceURL_0.doStartTag();
              if (_jspx_eval_liferay$1portlet_resourceURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t");
                //  portlet:param
                com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_4 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                _jspx_th_portlet_param_4.setPageContext(_jspx_page_context);
                _jspx_th_portlet_param_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_resourceURL_0);
                _jspx_th_portlet_param_4.setName("folderId");
                _jspx_th_portlet_param_4.setValue( String.valueOf(folderId) );
                int _jspx_eval_portlet_param_4 = _jspx_th_portlet_param_4.doStartTag();
                if (_jspx_th_portlet_param_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_4);
                  return;
                }
                _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_4);
                out.write("\n\t\t\t\t\t");
                //  portlet:param
                com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_5 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                _jspx_th_portlet_param_5.setPageContext(_jspx_page_context);
                _jspx_th_portlet_param_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_resourceURL_0);
                _jspx_th_portlet_param_5.setName("repositoryId");
                _jspx_th_portlet_param_5.setValue( String.valueOf(repositoryId) );
                int _jspx_eval_portlet_param_5 = _jspx_th_portlet_param_5.doStartTag();
                if (_jspx_th_portlet_param_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_5);
                  return;
                }
                _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_5);
                out.write("\n\t\t\t\t");
              }
              if (_jspx_th_liferay$1portlet_resourceURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters.reuse(_jspx_th_liferay$1portlet_resourceURL_0);
                return;
              }
              _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters.reuse(_jspx_th_liferay$1portlet_resourceURL_0);
              java.lang.String sidebarPanelURL = null;
              sidebarPanelURL = (java.lang.String) _jspx_page_context.findAttribute("sidebarPanelURL");
              out.write("\n\n\t\t\t\t");
              //  liferay-frontend:sidebar-panel
              com.liferay.frontend.taglib.servlet.taglib.SidebarPanelTag _jspx_th_liferay$1frontend_sidebar$1panel_0 = (com.liferay.frontend.taglib.servlet.taglib.SidebarPanelTag) _jspx_tagPool_liferay$1frontend_sidebar$1panel_searchContainerId_resourceURL.get(com.liferay.frontend.taglib.servlet.taglib.SidebarPanelTag.class);
              _jspx_th_liferay$1frontend_sidebar$1panel_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1frontend_sidebar$1panel_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_liferay$1frontend_sidebar$1panel_0.setResourceURL( sidebarPanelURL );
              _jspx_th_liferay$1frontend_sidebar$1panel_0.setSearchContainerId("entries");
              int _jspx_eval_liferay$1frontend_sidebar$1panel_0 = _jspx_th_liferay$1frontend_sidebar$1panel_0.doStartTag();
              if (_jspx_eval_liferay$1frontend_sidebar$1panel_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t");
                //  liferay-util:include
                com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_3 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
                _jspx_th_liferay$1util_include_3.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1util_include_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_sidebar$1panel_0);
                _jspx_th_liferay$1util_include_3.setPage("/document_library/info_panel.jsp");
                _jspx_th_liferay$1util_include_3.setServletContext( application );
                int _jspx_eval_liferay$1util_include_3 = _jspx_th_liferay$1util_include_3.doStartTag();
                if (_jspx_th_liferay$1util_include_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_3);
                  return;
                }
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_3);
                out.write("\n\t\t\t\t");
              }
              if (_jspx_th_liferay$1frontend_sidebar$1panel_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1frontend_sidebar$1panel_searchContainerId_resourceURL.reuse(_jspx_th_liferay$1frontend_sidebar$1panel_0);
                return;
              }
              _jspx_tagPool_liferay$1frontend_sidebar$1panel_searchContainerId_resourceURL.reuse(_jspx_th_liferay$1frontend_sidebar$1panel_0);
              out.write("\n\n\t\t\t\t<div class=\"sidenav-content\">\n\t\t\t\t\t<div class=\"document-library-breadcrumb\" id=\"");
              if (_jspx_meth_portlet_namespace_2((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_otherwise_0, _jspx_page_context))
                return;
              out.write("breadcrumbContainer\">\n\t\t\t\t\t\t");
              //  c:if
              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
              _jspx_th_c_if_0.setPageContext(_jspx_page_context);
              _jspx_th_c_if_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_c_if_0.setTest( !mvcRenderCommandName.equals("/document_library/search") );
              int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
              if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t\t\t");
                  //  liferay-util:include
                  com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_4 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
                  _jspx_th_liferay$1util_include_4.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1util_include_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
                  _jspx_th_liferay$1util_include_4.setPage("/document_library/breadcrumb.jsp");
                  _jspx_th_liferay$1util_include_4.setServletContext( application );
                  int _jspx_eval_liferay$1util_include_4 = _jspx_th_liferay$1util_include_4.doStartTag();
                  if (_jspx_th_liferay$1util_include_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_4);
                    return;
                  }
                  _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_4);
                  out.write("\n\t\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_c_if_0.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_c_if_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_0);
                return;
              }
              _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_0);
              out.write("\n\t\t\t\t\t</div>\n\n\t\t\t\t\t");
              //  c:if
              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
              _jspx_th_c_if_1.setPageContext(_jspx_page_context);
              _jspx_th_c_if_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_c_if_1.setTest( portletDisplay.isWebDAVEnabled() && BrowserSnifferUtil.isIeOnWin32(request) );
              int _jspx_eval_c_if_1 = _jspx_th_c_if_1.doStartTag();
              if (_jspx_eval_c_if_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t\t<div class=\"alert alert-danger hide\" id=\"");
                  if (_jspx_meth_portlet_namespace_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_1, _jspx_page_context))
                    return;
                  out.write("openMSOfficeError\"></div>\n\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_c_if_1.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_c_if_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_1);
                return;
              }
              _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_1);
              out.write("\n\n\t\t\t\t\t");
              //  liferay-portlet:renderURL
              com.liferay.taglib.portlet.RenderURLTag _jspx_th_liferay$1portlet_renderURL_0 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_liferay$1portlet_renderURL_varImpl.get(com.liferay.taglib.portlet.RenderURLTag.class);
              _jspx_th_liferay$1portlet_renderURL_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1portlet_renderURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_liferay$1portlet_renderURL_0.setVarImpl("editFileEntryURL");
              int _jspx_eval_liferay$1portlet_renderURL_0 = _jspx_th_liferay$1portlet_renderURL_0.doStartTag();
              if (_jspx_eval_liferay$1portlet_renderURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t\t");
                if (_jspx_meth_portlet_param_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1portlet_renderURL_0, _jspx_page_context))
                  return;
                out.write("\n\t\t\t\t\t");
              }
              if (_jspx_th_liferay$1portlet_renderURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1portlet_renderURL_varImpl.reuse(_jspx_th_liferay$1portlet_renderURL_0);
                return;
              }
              _jspx_tagPool_liferay$1portlet_renderURL_varImpl.reuse(_jspx_th_liferay$1portlet_renderURL_0);
              com.liferay.portal.kernel.portlet.LiferayPortletURL editFileEntryURL = null;
              editFileEntryURL = (com.liferay.portal.kernel.portlet.LiferayPortletURL) _jspx_page_context.findAttribute("editFileEntryURL");
              out.write("\n\n\t\t\t\t\t");
              //  aui:form
              com.liferay.taglib.aui.FormTag _jspx_th_aui_form_0 = (com.liferay.taglib.aui.FormTag) _jspx_tagPool_aui_form_name_method_action.get(com.liferay.taglib.aui.FormTag.class);
              _jspx_th_aui_form_0.setPageContext(_jspx_page_context);
              _jspx_th_aui_form_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_aui_form_0.setAction( editFileEntryURL.toString() );
              _jspx_th_aui_form_0.setMethod("get");
              _jspx_th_aui_form_0.setName("fm2");
              int _jspx_eval_aui_form_0 = _jspx_th_aui_form_0.doStartTag();
              if (_jspx_eval_aui_form_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t\t");
                //  aui:input
                com.liferay.taglib.aui.InputTag _jspx_th_aui_input_0 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                _jspx_th_aui_input_0.setPageContext(_jspx_page_context);
                _jspx_th_aui_input_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_aui_input_0.setName( Constants.CMD );
                _jspx_th_aui_input_0.setType("hidden");
                int _jspx_eval_aui_input_0 = _jspx_th_aui_input_0.doStartTag();
                if (_jspx_th_aui_input_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_0);
                  return;
                }
                _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_0);
                out.write("\n\t\t\t\t\t\t");
                //  aui:input
                com.liferay.taglib.aui.InputTag _jspx_th_aui_input_1 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                _jspx_th_aui_input_1.setPageContext(_jspx_page_context);
                _jspx_th_aui_input_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_aui_input_1.setName("redirect");
                _jspx_th_aui_input_1.setType("hidden");
                _jspx_th_aui_input_1.setValue( currentURL );
                int _jspx_eval_aui_input_1 = _jspx_th_aui_input_1.doStartTag();
                if (_jspx_th_aui_input_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_1);
                  return;
                }
                _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_1);
                out.write("\n\t\t\t\t\t\t");
                //  aui:input
                com.liferay.taglib.aui.InputTag _jspx_th_aui_input_2 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                _jspx_th_aui_input_2.setPageContext(_jspx_page_context);
                _jspx_th_aui_input_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_aui_input_2.setName("repositoryId");
                _jspx_th_aui_input_2.setType("hidden");
                _jspx_th_aui_input_2.setValue( repositoryId );
                int _jspx_eval_aui_input_2 = _jspx_th_aui_input_2.doStartTag();
                if (_jspx_th_aui_input_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_2);
                  return;
                }
                _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_2);
                out.write("\n\t\t\t\t\t\t");
                if (_jspx_meth_aui_input_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
                  return;
                out.write("\n\t\t\t\t\t\t");
                //  aui:input
                com.liferay.taglib.aui.InputTag _jspx_th_aui_input_4 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                _jspx_th_aui_input_4.setPageContext(_jspx_page_context);
                _jspx_th_aui_input_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_aui_input_4.setName("folderId");
                _jspx_th_aui_input_4.setType("hidden");
                _jspx_th_aui_input_4.setValue( folderId );
                int _jspx_eval_aui_input_4 = _jspx_th_aui_input_4.doStartTag();
                if (_jspx_th_aui_input_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_4);
                  return;
                }
                _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_4);
                out.write("\n\t\t\t\t\t\t");
                if (_jspx_meth_aui_input_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
                  return;
                out.write("\n\t\t\t\t\t\t");
                if (_jspx_meth_aui_input_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
                  return;
                out.write("\n\t\t\t\t\t\t");
                //  aui:input
                com.liferay.taglib.aui.InputTag _jspx_th_aui_input_7 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                _jspx_th_aui_input_7.setPageContext(_jspx_page_context);
                _jspx_th_aui_input_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_aui_input_7.setName("selectAll");
                _jspx_th_aui_input_7.setType("hidden");
                _jspx_th_aui_input_7.setValue( false );
                int _jspx_eval_aui_input_7 = _jspx_th_aui_input_7.doStartTag();
                if (_jspx_th_aui_input_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_7);
                  return;
                }
                _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_7);
                out.write("\n\n\t\t\t\t\t\t");
                if (_jspx_meth_liferay$1util_dynamic$1include_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
                  return;
                out.write("\n\n\t\t\t\t\t\t");
                //  liferay-ui:error
                com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_0 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
                _jspx_th_liferay$1ui_error_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_error_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_liferay$1ui_error_0.setException( AuthenticationRepositoryException.class );
                _jspx_th_liferay$1ui_error_0.setMessage("you-cannot-access-the-repository-because-you-are-not-allowed-to-or-it-is-unavailable");
                int _jspx_eval_liferay$1ui_error_0 = _jspx_th_liferay$1ui_error_0.doStartTag();
                if (_jspx_th_liferay$1ui_error_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_0);
                  return;
                }
                _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_0);
                out.write("\n\t\t\t\t\t\t");
                //  liferay-ui:error
                com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_1 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
                _jspx_th_liferay$1ui_error_1.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_error_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_liferay$1ui_error_1.setException( DuplicateFileEntryException.class );
                _jspx_th_liferay$1ui_error_1.setMessage("the-folder-you-selected-already-has-an-entry-with-this-name.-please-select-a-different-folder");
                int _jspx_eval_liferay$1ui_error_1 = _jspx_th_liferay$1ui_error_1.doStartTag();
                if (_jspx_th_liferay$1ui_error_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_1);
                  return;
                }
                _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_1);
                out.write("\n\t\t\t\t\t\t");
                //  liferay-ui:error
                com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_2 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
                _jspx_th_liferay$1ui_error_2.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_error_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_liferay$1ui_error_2.setException( FileEntryLockException.MustBeUnlocked.class );
                _jspx_th_liferay$1ui_error_2.setMessage("you-cannot-perform-this-operation-on-checked-out-documents-.please-check-it-in-or-cancel-the-checkout-first");
                int _jspx_eval_liferay$1ui_error_2 = _jspx_th_liferay$1ui_error_2.doStartTag();
                if (_jspx_th_liferay$1ui_error_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_2);
                  return;
                }
                _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_2);
                out.write("\n\t\t\t\t\t\t");
                //  liferay-ui:error
                com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_3 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
                _jspx_th_liferay$1ui_error_3.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_error_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_liferay$1ui_error_3.setException( FileEntryLockException.MustOwnLock.class );
                _jspx_th_liferay$1ui_error_3.setMessage("you-can-only-checkin-documents-you-have-checked-out-yourself");
                int _jspx_eval_liferay$1ui_error_3 = _jspx_th_liferay$1ui_error_3.doStartTag();
                if (_jspx_th_liferay$1ui_error_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_3);
                  return;
                }
                _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_3);
                out.write("\n\t\t\t\t\t\t");
                //  liferay-ui:error
                com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_4 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_key_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
                _jspx_th_liferay$1ui_error_4.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_error_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_liferay$1ui_error_4.setKey("externalServiceFailed");
                _jspx_th_liferay$1ui_error_4.setMessage("you-cannot-access-external-service-because-you-are-not-allowed-to-or-it-is-unavailable");
                int _jspx_eval_liferay$1ui_error_4 = _jspx_th_liferay$1ui_error_4.doStartTag();
                if (_jspx_th_liferay$1ui_error_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_error_message_key_nobody.reuse(_jspx_th_liferay$1ui_error_4);
                  return;
                }
                _jspx_tagPool_liferay$1ui_error_message_key_nobody.reuse(_jspx_th_liferay$1ui_error_4);
                out.write("\n\n\t\t\t\t\t\t<div class=\"document-container\">\n\t\t\t\t\t\t\t");
                //  c:choose
                org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_1 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                _jspx_th_c_choose_1.setPageContext(_jspx_page_context);
                _jspx_th_c_choose_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                int _jspx_eval_c_choose_1 = _jspx_th_c_choose_1.doStartTag();
                if (_jspx_eval_c_choose_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t\t\t\t\t\t\t");
                    //  c:when
                    org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_1 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                    _jspx_th_c_when_1.setPageContext(_jspx_page_context);
                    _jspx_th_c_when_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                    _jspx_th_c_when_1.setTest( mvcRenderCommandName.equals("/document_library/search") );
                    int _jspx_eval_c_when_1 = _jspx_th_c_when_1.doStartTag();
                    if (_jspx_eval_c_when_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      do {
                        out.write("\n\t\t\t\t\t\t\t\t\t");
                        //  liferay-util:include
                        com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_5 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
                        _jspx_th_liferay$1util_include_5.setPageContext(_jspx_page_context);
                        _jspx_th_liferay$1util_include_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
                        _jspx_th_liferay$1util_include_5.setPage("/document_library/search_resources.jsp");
                        _jspx_th_liferay$1util_include_5.setServletContext( application );
                        int _jspx_eval_liferay$1util_include_5 = _jspx_th_liferay$1util_include_5.doStartTag();
                        if (_jspx_th_liferay$1util_include_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_5);
                          return;
                        }
                        _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_5);
                        out.write("\n\t\t\t\t\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_c_when_1.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                    }
                    if (_jspx_th_c_when_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_1);
                      return;
                    }
                    _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_1);
                    out.write("\n\t\t\t\t\t\t\t\t");
                    //  c:otherwise
                    org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_1 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                    _jspx_th_c_otherwise_1.setPageContext(_jspx_page_context);
                    _jspx_th_c_otherwise_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                    int _jspx_eval_c_otherwise_1 = _jspx_th_c_otherwise_1.doStartTag();
                    if (_jspx_eval_c_otherwise_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      do {
                        out.write("\n\t\t\t\t\t\t\t\t\t");
                        //  liferay-util:include
                        com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_6 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
                        _jspx_th_liferay$1util_include_6.setPageContext(_jspx_page_context);
                        _jspx_th_liferay$1util_include_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_1);
                        _jspx_th_liferay$1util_include_6.setPage("/document_library/view_entries.jsp");
                        _jspx_th_liferay$1util_include_6.setServletContext( application );
                        int _jspx_eval_liferay$1util_include_6 = _jspx_th_liferay$1util_include_6.doStartTag();
                        if (_jspx_th_liferay$1util_include_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_6);
                          return;
                        }
                        _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_6);
                        out.write("\n\t\t\t\t\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_c_otherwise_1.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                    }
                    if (_jspx_th_c_otherwise_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_1);
                      return;
                    }
                    _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_1);
                    out.write("\n\t\t\t\t\t\t\t");
                    int evalDoAfterBody = _jspx_th_c_choose_1.doAfterBody();
                    if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                      break;
                  } while (true);
                }
                if (_jspx_th_c_choose_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_1);
                  return;
                }
                _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_1);
                out.write("\n\n\t\t\t\t\t\t\t");
                out.write("\n\n<div class=\"lfr-template\" id=\"");
                if (_jspx_meth_portlet_namespace_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
                  return;
                out.write("appViewEntryTemplates\">\n\n\t");

	String thumbnailSrc = themeDisplay.getPathThemeImages() + "/file_system/large/default.png";
	
                out.write("\n\n\t");
                //  liferay-frontend:vertical-card
                com.liferay.frontend.taglib.servlet.taglib.VerticalCardTag _jspx_th_liferay$1frontend_vertical$1card_0 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardTag) _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_imageUrl_cssClass.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardTag.class);
                _jspx_th_liferay$1frontend_vertical$1card_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1frontend_vertical$1card_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_liferay$1frontend_vertical$1card_0.setCssClass("display-icon entry-display-style");
                _jspx_th_liferay$1frontend_vertical$1card_0.setImageUrl( thumbnailSrc );
                _jspx_th_liferay$1frontend_vertical$1card_0.setTitle("{title}");
                _jspx_th_liferay$1frontend_vertical$1card_0.setUrl( uploadURL );
                int _jspx_eval_liferay$1frontend_vertical$1card_0 = _jspx_th_liferay$1frontend_vertical$1card_0.doStartTag();
                if (_jspx_eval_liferay$1frontend_vertical$1card_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n\t\t");
                  //  liferay-frontend:vertical-card-header
                  com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag _jspx_th_liferay$1frontend_vertical$1card$1header_0 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1header.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag.class);
                  _jspx_th_liferay$1frontend_vertical$1card$1header_0.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1frontend_vertical$1card$1header_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card_0);
                  int _jspx_eval_liferay$1frontend_vertical$1card$1header_0 = _jspx_th_liferay$1frontend_vertical$1card$1header_0.doStartTag();
                  if (_jspx_eval_liferay$1frontend_vertical$1card$1header_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    if (_jspx_eval_liferay$1frontend_vertical$1card$1header_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                      out = _jspx_page_context.pushBody();
                      _jspx_th_liferay$1frontend_vertical$1card$1header_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                      _jspx_th_liferay$1frontend_vertical$1card$1header_0.doInitBody();
                    }
                    do {
                      out.write("\n\t\t\t");
                      //  liferay-ui:message
                      com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_0 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                      _jspx_th_liferay$1ui_message_0.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1ui_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1header_0);
                      _jspx_th_liferay$1ui_message_0.setArguments( HtmlUtil.escape(user.getFullName()) );
                      _jspx_th_liferay$1ui_message_0.setKey("right-now-by-x");
                      int _jspx_eval_liferay$1ui_message_0 = _jspx_th_liferay$1ui_message_0.doStartTag();
                      if (_jspx_th_liferay$1ui_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_0);
                        return;
                      }
                      _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_0);
                      out.write("\n\t\t");
                      int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1header_0.doAfterBody();
                      if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                        break;
                    } while (true);
                    if (_jspx_eval_liferay$1frontend_vertical$1card$1header_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                      out = _jspx_page_context.popBody();
                  }
                  if (_jspx_th_liferay$1frontend_vertical$1card$1header_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1frontend_vertical$1card$1header.reuse(_jspx_th_liferay$1frontend_vertical$1card$1header_0);
                    return;
                  }
                  _jspx_tagPool_liferay$1frontend_vertical$1card$1header.reuse(_jspx_th_liferay$1frontend_vertical$1card$1header_0);
                  out.write('\n');
                  out.write('	');
                }
                if (_jspx_th_liferay$1frontend_vertical$1card_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_imageUrl_cssClass.reuse(_jspx_th_liferay$1frontend_vertical$1card_0);
                  return;
                }
                _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_imageUrl_cssClass.reuse(_jspx_th_liferay$1frontend_vertical$1card_0);
                out.write("\n\n\t<li class=\"display-descriptive entry-display-style list-group-item\">\n\t\t<div class=\"list-group-item-field\"></div>\n\n\t\t<div class=\"list-group-item-field\">\n\t\t\t<div class=\"click-selector user-icon user-icon-square user-icon-xl\">\n\t\t\t\t<img alt=\"thumbnail\" class=\"img-responsive img-rounded\" src=\"");
                out.print( thumbnailSrc );
                out.write("\" />\n\t\t\t</div>\n\t\t</div>\n\n\t\t<div class=\"list-group-item-content\">\n\t\t\t<h5 class=\"text-default\">\n\t\t\t\t");
                //  liferay-ui:message
                com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_1 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                _jspx_th_liferay$1ui_message_1.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_liferay$1ui_message_1.setArguments( HtmlUtil.escape(user.getFullName()) );
                _jspx_th_liferay$1ui_message_1.setKey("right-now-by-x");
                int _jspx_eval_liferay$1ui_message_1 = _jspx_th_liferay$1ui_message_1.doStartTag();
                if (_jspx_th_liferay$1ui_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_1);
                  return;
                }
                _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_1);
                out.write("\n\t\t\t</h5>\n\n\t\t\t<h4>\n\t\t\t\t");
                //  aui:a
                com.liferay.taglib.aui.ATag _jspx_th_aui_a_0 = (com.liferay.taglib.aui.ATag) _jspx_tagPool_aui_a_href.get(com.liferay.taglib.aui.ATag.class);
                _jspx_th_aui_a_0.setPageContext(_jspx_page_context);
                _jspx_th_aui_a_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
                _jspx_th_aui_a_0.setHref( uploadURL.toString() );
                int _jspx_eval_aui_a_0 = _jspx_th_aui_a_0.doStartTag();
                if (_jspx_eval_aui_a_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n\t\t\t\t\t{title}\n\t\t\t\t");
                }
                if (_jspx_th_aui_a_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_a_href.reuse(_jspx_th_aui_a_0);
                  return;
                }
                _jspx_tagPool_aui_a_href.reuse(_jspx_th_aui_a_0);
                out.write("\n\t\t\t</h4>\n\t\t</div>\n\n\t\t<div class=\"list-group-item-field\"></div>\n\t</li>\n</div>");
                out.write("\n\t\t\t\t\t\t</div>\n\t\t\t\t\t");
              }
              if (_jspx_th_aui_form_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_form_name_method_action.reuse(_jspx_th_aui_form_0);
                return;
              }
              _jspx_tagPool_aui_form_name_method_action.reuse(_jspx_th_aui_form_0);
              out.write("\n\t\t\t\t</div>\n\t\t\t</div>\n\n\t\t\t<div id=\"");
              if (_jspx_meth_portlet_namespace_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_otherwise_0, _jspx_page_context))
                return;
              out.write("documentLibraryModal\"></div>\n\t\t</div>\n\n\t\t");

		if (!defaultFolderView && (folder != null) && (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) || portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN))) {
			PortalUtil.setPageDescription(folder.getDescription(), request);
		}

		boolean uploadable = true;

		if (!DLFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.ADD_DOCUMENT)) {
			uploadable = false;
		}
		else {
			List<AssetVocabulary> assetVocabularies = AssetVocabularyServiceUtil.getGroupVocabularies(PortalUtil.getCurrentAndAncestorSiteGroupIds(scopeGroupId));

			if (!assetVocabularies.isEmpty()) {
				long classNameId = ClassNameLocalServiceUtil.getClassNameId(DLFileEntryConstants.getClassName());

				for (AssetVocabulary assetVocabulary : assetVocabularies) {
					if (assetVocabulary.isRequired(classNameId, DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT)) {
						uploadable = false;

						break;
					}
				}
			}
		}

		PortletURL selectCategoriesURL = PortletProviderUtil.getPortletURL(request, AssetCategory.class.getName(), PortletProvider.Action.BROWSE);

		selectCategoriesURL.setParameter("eventName", liferayPortletResponse.getNamespace() + "selectCategories");
		selectCategoriesURL.setParameter("selectedCategories", "{selectedCategories}");
		selectCategoriesURL.setParameter("singleSelect", "{singleSelect}");
		selectCategoriesURL.setParameter("vocabularyIds", "{vocabularyIds}");
		selectCategoriesURL.setWindowState(LiferayWindowState.POP_UP);
		
              out.write("\n\n\t\t");
              if (_jspx_meth_aui_script_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_otherwise_0, _jspx_page_context))
                return;
              out.write("\n\n\t\t");
              //  aui:script
              com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_1 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script_use.get(com.liferay.taglib.aui.ScriptTag.class);
              _jspx_th_aui_script_1.setPageContext(_jspx_page_context);
              _jspx_th_aui_script_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_aui_script_1.setUse("liferay-document-library");
              int _jspx_eval_aui_script_1 = _jspx_th_aui_script_1.doStartTag();
              if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_aui_script_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_aui_script_1.doInitBody();
                }
                do {
                  out.write("\n\n\t\t\t");

			String[] entryColumns = dlPortletInstanceSettingsHelper.getEntryColumns();
			String[] escapedEntryColumns = new String[entryColumns.length];

			for (int i = 0; i < entryColumns.length; i++) {
				escapedEntryColumns[i] = HtmlUtil.escapeJS(entryColumns[i]);
			}

			long fileEntryTypeId = ParamUtil.getLong(request, "fileEntryTypeId", -1);

			PortletURL viewFileEntryTypeURL = PortletURLUtil.clone(currentURLObj, liferayPortletResponse);

			viewFileEntryTypeURL.setParameter("browseBy", "file-entry-type");
			viewFileEntryTypeURL.setParameter("fileEntryTypeId", (String)null);
			
                  out.write("\n\n\t\t\tLiferay.component(\n\t\t\t\t'");
                  if (_jspx_meth_portlet_namespace_8((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
                    return;
                  out.write("DocumentLibrary',\n\t\t\t\tnew Liferay.Portlet.DocumentLibrary({\n\t\t\t\t\tcolumnNames: ['");
                  out.print( StringUtil.merge(escapedEntryColumns, "','") );
                  out.write("'],\n\n\t\t\t\t\t");

					DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
					
                  out.write("\n\n\t\t\t\t\tdecimalSeparator: '");
                  out.print( decimalFormatSymbols.getDecimalSeparator() );
                  out.write("',\n\t\t\t\t\tdisplayStyle: '");
                  out.print( HtmlUtil.escapeJS(displayStyle) );
                  out.write("',\n\t\t\t\t\teditEntryUrl:\n\t\t\t\t\t\t'");
                  if (_jspx_meth_portlet_actionURL_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
                    return;
                  out.write("',\n\t\t\t\t\tdownloadEntryUrl:\n\t\t\t\t\t\t'");
                  //  portlet:resourceURL
                  com.liferay.taglib.portlet.ResourceURLTag _jspx_th_portlet_resourceURL_0 = (com.liferay.taglib.portlet.ResourceURLTag) _jspx_tagPool_portlet_resourceURL_id.get(com.liferay.taglib.portlet.ResourceURLTag.class);
                  _jspx_th_portlet_resourceURL_0.setPageContext(_jspx_page_context);
                  _jspx_th_portlet_resourceURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
                  _jspx_th_portlet_resourceURL_0.setId("/document_library/download_entry");
                  int _jspx_eval_portlet_resourceURL_0 = _jspx_th_portlet_resourceURL_0.doStartTag();
                  if (_jspx_eval_portlet_resourceURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_7 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_7.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_resourceURL_0);
                    _jspx_th_portlet_param_7.setName("folderId");
                    _jspx_th_portlet_param_7.setValue( String.valueOf(folderId) );
                    int _jspx_eval_portlet_param_7 = _jspx_th_portlet_param_7.doStartTag();
                    if (_jspx_th_portlet_param_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_7);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_7);
                  }
                  if (_jspx_th_portlet_resourceURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_portlet_resourceURL_id.reuse(_jspx_th_portlet_resourceURL_0);
                    return;
                  }
                  _jspx_tagPool_portlet_resourceURL_id.reuse(_jspx_th_portlet_resourceURL_0);
                  out.write("',\n\t\t\t\t\tfolders: {\n\t\t\t\t\t\tdefaultParentFolderId: '");
                  out.print( folderId );
                  out.write("',\n\t\t\t\t\t\tdimensions: {\n\t\t\t\t\t\t\theight:\n\t\t\t\t\t\t\t\t'");
                  out.print( PrefsPropsUtil.getLong(PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_HEIGHT) );
                  out.write("',\n\t\t\t\t\t\t\twidth:\n\t\t\t\t\t\t\t\t'");
                  out.print( PrefsPropsUtil.getLong(PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_WIDTH) );
                  out.write("'\n\t\t\t\t\t\t}\n\t\t\t\t\t},\n\t\t\t\t\tform: {\n\t\t\t\t\t\tmethod: 'POST',\n\t\t\t\t\t\tnode: A.one(document.");
                  if (_jspx_meth_portlet_namespace_9((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
                    return;
                  out.write("fm2)\n\t\t\t\t\t},\n\t\t\t\t\tmaxFileSize: ");
                  out.print( dlConfiguration.fileMaxSize() );
                  out.write(",\n\t\t\t\t\tnamespace: '");
                  if (_jspx_meth_portlet_namespace_10((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
                    return;
                  out.write("',\n\t\t\t\t\topenViewMoreFileEntryTypesURL:\n\t\t\t\t\t\t'");
                  //  portlet:renderURL
                  com.liferay.taglib.portlet.RenderURLTag _jspx_th_portlet_renderURL_0 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_portlet_renderURL_windowState.get(com.liferay.taglib.portlet.RenderURLTag.class);
                  _jspx_th_portlet_renderURL_0.setPageContext(_jspx_page_context);
                  _jspx_th_portlet_renderURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
                  _jspx_th_portlet_renderURL_0.setWindowState( LiferayWindowState.POP_UP.toString() );
                  int _jspx_eval_portlet_renderURL_0 = _jspx_th_portlet_renderURL_0.doStartTag();
                  if (_jspx_eval_portlet_renderURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    if (_jspx_meth_portlet_param_8((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_renderURL_0, _jspx_page_context))
                      return;
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_9 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_9.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
                    _jspx_th_portlet_param_9.setName("folderId");
                    _jspx_th_portlet_param_9.setValue( String.valueOf(folderId) );
                    int _jspx_eval_portlet_param_9 = _jspx_th_portlet_param_9.doStartTag();
                    if (_jspx_th_portlet_param_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_9);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_9);
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_10 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_10.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
                    _jspx_th_portlet_param_10.setName("eventName");
                    _jspx_th_portlet_param_10.setValue( liferayPortletResponse.getNamespace() + "selectAddMenuItem" );
                    int _jspx_eval_portlet_param_10 = _jspx_th_portlet_param_10.doStartTag();
                    if (_jspx_th_portlet_param_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_10);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_10);
                  }
                  if (_jspx_th_portlet_renderURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_portlet_renderURL_windowState.reuse(_jspx_th_portlet_renderURL_0);
                    return;
                  }
                  _jspx_tagPool_portlet_renderURL_windowState.reuse(_jspx_th_portlet_renderURL_0);
                  out.write("',\n\t\t\t\t\tportletId: '");
                  out.print( HtmlUtil.escapeJS(portletId) );
                  out.write("',\n\t\t\t\t\tredirect: encodeURIComponent('");
                  out.print( currentURL );
                  out.write("'),\n\t\t\t\t\tselectFileEntryTypeURL:\n\t\t\t\t\t\t'");
                  //  portlet:renderURL
                  com.liferay.taglib.portlet.RenderURLTag _jspx_th_portlet_renderURL_1 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_portlet_renderURL_windowState.get(com.liferay.taglib.portlet.RenderURLTag.class);
                  _jspx_th_portlet_renderURL_1.setPageContext(_jspx_page_context);
                  _jspx_th_portlet_renderURL_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
                  _jspx_th_portlet_renderURL_1.setWindowState( LiferayWindowState.POP_UP.toString() );
                  int _jspx_eval_portlet_renderURL_1 = _jspx_th_portlet_renderURL_1.doStartTag();
                  if (_jspx_eval_portlet_renderURL_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    if (_jspx_meth_portlet_param_11((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_renderURL_1, _jspx_page_context))
                      return;
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_12 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_12.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_1);
                    _jspx_th_portlet_param_12.setName("fileEntryTypeId");
                    _jspx_th_portlet_param_12.setValue( String.valueOf(fileEntryTypeId) );
                    int _jspx_eval_portlet_param_12 = _jspx_th_portlet_param_12.doStartTag();
                    if (_jspx_th_portlet_param_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_12);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_12);
                  }
                  if (_jspx_th_portlet_renderURL_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_portlet_renderURL_windowState.reuse(_jspx_th_portlet_renderURL_1);
                    return;
                  }
                  _jspx_tagPool_portlet_renderURL_windowState.reuse(_jspx_th_portlet_renderURL_1);
                  out.write("',\n\t\t\t\t\tselectFolderURL:\n\t\t\t\t\t\t'");
                  //  portlet:renderURL
                  com.liferay.taglib.portlet.RenderURLTag _jspx_th_portlet_renderURL_2 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_portlet_renderURL_windowState.get(com.liferay.taglib.portlet.RenderURLTag.class);
                  _jspx_th_portlet_renderURL_2.setPageContext(_jspx_page_context);
                  _jspx_th_portlet_renderURL_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
                  _jspx_th_portlet_renderURL_2.setWindowState( LiferayWindowState.POP_UP.toString() );
                  int _jspx_eval_portlet_renderURL_2 = _jspx_th_portlet_renderURL_2.doStartTag();
                  if (_jspx_eval_portlet_renderURL_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    if (_jspx_meth_portlet_param_13((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_renderURL_2, _jspx_page_context))
                      return;
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_14 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_14.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_14.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_2);
                    _jspx_th_portlet_param_14.setName("folderId");
                    _jspx_th_portlet_param_14.setValue( String.valueOf(folderId) );
                    int _jspx_eval_portlet_param_14 = _jspx_th_portlet_param_14.doStartTag();
                    if (_jspx_th_portlet_param_14.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_14);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_14);
                  }
                  if (_jspx_th_portlet_renderURL_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_portlet_renderURL_windowState.reuse(_jspx_th_portlet_renderURL_2);
                    return;
                  }
                  _jspx_tagPool_portlet_renderURL_windowState.reuse(_jspx_th_portlet_renderURL_2);
                  out.write("',\n\t\t\t\t\tscopeGroupId: ");
                  out.print( scopeGroupId );
                  out.write(",\n\t\t\t\t\tsearchContainerId: 'entries',\n\t\t\t\t\ttrashEnabled: ");
                  out.print( (scopeGroupId == repositoryId) && dlTrashUtil.isTrashEnabled(scopeGroupId, repositoryId) );
                  out.write(",\n\t\t\t\t\tuploadable: ");
                  out.print( uploadable );
                  out.write(",\n\t\t\t\t\tuploadURL: '");
                  out.print( uploadURL );
                  out.write("',\n\t\t\t\t\tviewFileEntryURL:\n\t\t\t\t\t\t'");
                  //  portlet:renderURL
                  com.liferay.taglib.portlet.RenderURLTag _jspx_th_portlet_renderURL_3 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_portlet_renderURL.get(com.liferay.taglib.portlet.RenderURLTag.class);
                  _jspx_th_portlet_renderURL_3.setPageContext(_jspx_page_context);
                  _jspx_th_portlet_renderURL_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
                  int _jspx_eval_portlet_renderURL_3 = _jspx_th_portlet_renderURL_3.doStartTag();
                  if (_jspx_eval_portlet_renderURL_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    if (_jspx_meth_portlet_param_15((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_renderURL_3, _jspx_page_context))
                      return;
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_16 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_16.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_16.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_3);
                    _jspx_th_portlet_param_16.setName("redirect");
                    _jspx_th_portlet_param_16.setValue( currentURL );
                    int _jspx_eval_portlet_param_16 = _jspx_th_portlet_param_16.doStartTag();
                    if (_jspx_th_portlet_param_16.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_16);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_16);
                  }
                  if (_jspx_th_portlet_renderURL_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_portlet_renderURL.reuse(_jspx_th_portlet_renderURL_3);
                    return;
                  }
                  _jspx_tagPool_portlet_renderURL.reuse(_jspx_th_portlet_renderURL_3);
                  out.write("',\n\t\t\t\t\tviewFileEntryTypeURL: '");
                  out.print( viewFileEntryTypeURL );
                  out.write("'\n\t\t\t\t}),\n\t\t\t\t{\n\t\t\t\t\tdestroyOnNavigate: true,\n\t\t\t\t\tportletId: '");
                  out.print( HtmlUtil.escapeJS(portletId) );
                  out.write("'\n\t\t\t\t}\n\t\t\t);\n\n\t\t\tvar changeScopeHandles = function(event) {\n\t\t\t\tdocumentLibrary.destroy();\n\n\t\t\t\tLiferay.detach('changeScope', changeScopeHandles);\n\t\t\t};\n\n\t\t\tLiferay.on('changeScope', changeScopeHandles);\n\n\t\t\t");
                  //  portlet:renderURL
                  com.liferay.taglib.portlet.RenderURLTag _jspx_th_portlet_renderURL_4 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_portlet_renderURL_var.get(com.liferay.taglib.portlet.RenderURLTag.class);
                  _jspx_th_portlet_renderURL_4.setPageContext(_jspx_page_context);
                  _jspx_th_portlet_renderURL_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
                  _jspx_th_portlet_renderURL_4.setVar("addFileEntryURL");
                  int _jspx_eval_portlet_renderURL_4 = _jspx_th_portlet_renderURL_4.doStartTag();
                  if (_jspx_eval_portlet_renderURL_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    out.write("\n\t\t\t\t");
                    if (_jspx_meth_portlet_param_17((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_renderURL_4, _jspx_page_context))
                      return;
                    out.write("\n\t\t\t\t");
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_18 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_18.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_18.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_4);
                    _jspx_th_portlet_param_18.setName( Constants.CMD );
                    _jspx_th_portlet_param_18.setValue( Constants.ADD );
                    int _jspx_eval_portlet_param_18 = _jspx_th_portlet_param_18.doStartTag();
                    if (_jspx_th_portlet_param_18.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_18);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_18);
                    out.write("\n\t\t\t\t");
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_19 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_19.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_19.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_4);
                    _jspx_th_portlet_param_19.setName("redirect");
                    _jspx_th_portlet_param_19.setValue( currentURL );
                    int _jspx_eval_portlet_param_19 = _jspx_th_portlet_param_19.doStartTag();
                    if (_jspx_th_portlet_param_19.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_19);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_19);
                    out.write("\n\t\t\t\t");
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_20 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_20.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_20.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_4);
                    _jspx_th_portlet_param_20.setName("groupId");
                    _jspx_th_portlet_param_20.setValue( String.valueOf(scopeGroupId) );
                    int _jspx_eval_portlet_param_20 = _jspx_th_portlet_param_20.doStartTag();
                    if (_jspx_th_portlet_param_20.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_20);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_20);
                    out.write("\n\t\t\t\t");
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_21 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_21.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_21.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_4);
                    _jspx_th_portlet_param_21.setName("repositoryId");
                    _jspx_th_portlet_param_21.setValue( String.valueOf(repositoryId) );
                    int _jspx_eval_portlet_param_21 = _jspx_th_portlet_param_21.doStartTag();
                    if (_jspx_th_portlet_param_21.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_21);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_21);
                    out.write("\n\t\t\t\t");
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_22 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_22.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_22.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_4);
                    _jspx_th_portlet_param_22.setName("folderId");
                    _jspx_th_portlet_param_22.setValue( String.valueOf(folderId) );
                    int _jspx_eval_portlet_param_22 = _jspx_th_portlet_param_22.doStartTag();
                    if (_jspx_th_portlet_param_22.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_22);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_22);
                    out.write("\n\t\t\t");
                  }
                  if (_jspx_th_portlet_renderURL_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_portlet_renderURL_var.reuse(_jspx_th_portlet_renderURL_4);
                    return;
                  }
                  _jspx_tagPool_portlet_renderURL_var.reuse(_jspx_th_portlet_renderURL_4);
                  java.lang.String addFileEntryURL = null;
                  addFileEntryURL = (java.lang.String) _jspx_page_context.findAttribute("addFileEntryURL");
                  out.write("\n\n\t\t\tvar editFileEntryHandler = function(event) {\n\t\t\t\tvar uri = '");
                  out.print( addFileEntryURL );
                  out.write("';\n\n\t\t\t\tlocation.href = Liferay.Util.addParams(\n\t\t\t\t\t'");
                  if (_jspx_meth_portlet_namespace_11((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
                    return;
                  out.write("fileEntryTypeId' + '=' + event.fileEntryTypeId,\n\t\t\t\t\turi\n\t\t\t\t);\n\t\t\t};\n\n\t\t\tLiferay.on('");
                  if (_jspx_meth_portlet_namespace_12((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
                    return;
                  out.write("selectAddMenuItem', editFileEntryHandler);\n\t\t");
                  int evalDoAfterBody = _jspx_th_aui_script_1.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
                if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                  out = _jspx_page_context.popBody();
              }
              if (_jspx_th_aui_script_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_script_use.reuse(_jspx_th_aui_script_1);
                return;
              }
              _jspx_tagPool_aui_script_use.reuse(_jspx_th_aui_script_1);
              out.write("\n\n\t\t");

		String pathModule = PortalUtil.getPathModule();

		Map<String, Object> tagsContext = new HashMap<>();

		long[] groupIds = PortalUtil.getCurrentAndAncestorSiteGroupIds(scopeGroupId);

		tagsContext.put("groupIds", groupIds);
		tagsContext.put("pathModule", pathModule);
		tagsContext.put("repositoryId", String.valueOf(repositoryId));

		Map<String, Object> categoriesContext = new HashMap<>();

		categoriesContext.put("groupIds", groupIds);
		categoriesContext.put("pathModule", pathModule);
		categoriesContext.put("repositoryId", String.valueOf(repositoryId));
		categoriesContext.put("selectCategoriesUrl", selectCategoriesURL.toString());
		
              out.write("\n\n\t\t");
              //  liferay-frontend:component
              com.liferay.frontend.taglib.servlet.taglib.ComponentTag _jspx_th_liferay$1frontend_component_0 = (com.liferay.frontend.taglib.servlet.taglib.ComponentTag) _jspx_tagPool_liferay$1frontend_component_module_context_containerId_componentId_nobody.get(com.liferay.frontend.taglib.servlet.taglib.ComponentTag.class);
              _jspx_th_liferay$1frontend_component_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1frontend_component_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_liferay$1frontend_component_0.setComponentId( liferayPortletResponse.getNamespace() + "EditTagsComponent" );
              _jspx_th_liferay$1frontend_component_0.setContainerId( "#" + liferayPortletResponse.getNamespace() + "documentLibraryModal" );
              _jspx_th_liferay$1frontend_component_0.setContext( tagsContext );
              _jspx_th_liferay$1frontend_component_0.setModule("document_library/js/categorization/EditTags.es");
              int _jspx_eval_liferay$1frontend_component_0 = _jspx_th_liferay$1frontend_component_0.doStartTag();
              if (_jspx_th_liferay$1frontend_component_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1frontend_component_module_context_containerId_componentId_nobody.reuse(_jspx_th_liferay$1frontend_component_0);
                return;
              }
              _jspx_tagPool_liferay$1frontend_component_module_context_containerId_componentId_nobody.reuse(_jspx_th_liferay$1frontend_component_0);
              out.write("\n\n\t\t");
              //  liferay-frontend:component
              com.liferay.frontend.taglib.servlet.taglib.ComponentTag _jspx_th_liferay$1frontend_component_1 = (com.liferay.frontend.taglib.servlet.taglib.ComponentTag) _jspx_tagPool_liferay$1frontend_component_module_context_containerId_componentId_nobody.get(com.liferay.frontend.taglib.servlet.taglib.ComponentTag.class);
              _jspx_th_liferay$1frontend_component_1.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1frontend_component_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
              _jspx_th_liferay$1frontend_component_1.setComponentId( liferayPortletResponse.getNamespace() + "EditCategoriesComponent" );
              _jspx_th_liferay$1frontend_component_1.setContainerId( "#" + liferayPortletResponse.getNamespace() + "documentLibraryModal" );
              _jspx_th_liferay$1frontend_component_1.setContext( categoriesContext );
              _jspx_th_liferay$1frontend_component_1.setModule("document_library/js/categorization/EditCategories.es");
              int _jspx_eval_liferay$1frontend_component_1 = _jspx_th_liferay$1frontend_component_1.doStartTag();
              if (_jspx_th_liferay$1frontend_component_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1frontend_component_module_context_containerId_componentId_nobody.reuse(_jspx_th_liferay$1frontend_component_1);
                return;
              }
              _jspx_tagPool_liferay$1frontend_component_module_context_containerId_componentId_nobody.reuse(_jspx_th_liferay$1frontend_component_1);
              out.write("\n\n\t\t");
              if (_jspx_meth_liferay$1util_dynamic$1include_2((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_otherwise_0, _jspx_page_context))
                return;
              out.write('\n');
              out.write('	');
              int evalDoAfterBody = _jspx_th_c_otherwise_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_otherwise_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_0);
            return;
          }
          _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_0);
          out.write('\n');
          int evalDoAfterBody = _jspx_th_c_choose_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_choose_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_0);
        return;
      }
      _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_0);
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }

  private boolean _jspx_meth_liferay$1util_dynamic$1include_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_otherwise_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-util:dynamic-include
    com.liferay.taglib.util.DynamicIncludeTag _jspx_th_liferay$1util_dynamic$1include_0 = (com.liferay.taglib.util.DynamicIncludeTag) _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.get(com.liferay.taglib.util.DynamicIncludeTag.class);
    _jspx_th_liferay$1util_dynamic$1include_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1util_dynamic$1include_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
    _jspx_th_liferay$1util_dynamic$1include_0.setKey("com.liferay.document.library.web#/document_library/view.jsp#pre");
    int _jspx_eval_liferay$1util_dynamic$1include_0 = _jspx_th_liferay$1util_dynamic$1include_0.doStartTag();
    if (_jspx_th_liferay$1util_dynamic$1include_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_0);
      return true;
    }
    _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_0);
    return false;
  }

  private boolean _jspx_meth_portlet_param_1(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1portlet_actionURL_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_1 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_1.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_actionURL_0);
    _jspx_th_portlet_param_1.setName("folderId");
    _jspx_th_portlet_param_1.setValue("{folderId}");
    int _jspx_eval_portlet_param_1 = _jspx_th_portlet_param_1.doStartTag();
    if (_jspx_th_portlet_param_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_otherwise_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_0 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
    int _jspx_eval_portlet_namespace_0 = _jspx_th_portlet_namespace_0.doStartTag();
    if (_jspx_th_portlet_namespace_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_otherwise_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_1 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_1.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
    int _jspx_eval_portlet_namespace_1 = _jspx_th_portlet_namespace_1.doStartTag();
    if (_jspx_th_portlet_namespace_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_2(javax.servlet.jsp.tagext.JspTag _jspx_th_c_otherwise_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_2 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_2.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
    int _jspx_eval_portlet_namespace_2 = _jspx_th_portlet_namespace_2.doStartTag();
    if (_jspx_th_portlet_namespace_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_2);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_2);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_3(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_3 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_3.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
    int _jspx_eval_portlet_namespace_3 = _jspx_th_portlet_namespace_3.doStartTag();
    if (_jspx_th_portlet_namespace_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_3);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_3);
    return false;
  }

  private boolean _jspx_meth_portlet_param_6(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1portlet_renderURL_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_6 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_6.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_renderURL_0);
    _jspx_th_portlet_param_6.setName("mvcRenderCommandName");
    _jspx_th_portlet_param_6.setValue("/document_library/edit_file_entry");
    int _jspx_eval_portlet_param_6 = _jspx_th_portlet_param_6.doStartTag();
    if (_jspx_th_portlet_param_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_6);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_6);
    return false;
  }

  private boolean _jspx_meth_aui_input_3(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_3 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_3.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    _jspx_th_aui_input_3.setName("newFolderId");
    _jspx_th_aui_input_3.setType("hidden");
    int _jspx_eval_aui_input_3 = _jspx_th_aui_input_3.doStartTag();
    if (_jspx_th_aui_input_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_3);
      return true;
    }
    _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_3);
    return false;
  }

  private boolean _jspx_meth_aui_input_5(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_5 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_5.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    _jspx_th_aui_input_5.setName("changeLog");
    _jspx_th_aui_input_5.setType("hidden");
    int _jspx_eval_aui_input_5 = _jspx_th_aui_input_5.doStartTag();
    if (_jspx_th_aui_input_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_5);
      return true;
    }
    _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_5);
    return false;
  }

  private boolean _jspx_meth_aui_input_6(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_6 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_6.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    _jspx_th_aui_input_6.setName("versionIncrease");
    _jspx_th_aui_input_6.setType("hidden");
    int _jspx_eval_aui_input_6 = _jspx_th_aui_input_6.doStartTag();
    if (_jspx_th_aui_input_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_6);
      return true;
    }
    _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_6);
    return false;
  }

  private boolean _jspx_meth_liferay$1util_dynamic$1include_1(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-util:dynamic-include
    com.liferay.taglib.util.DynamicIncludeTag _jspx_th_liferay$1util_dynamic$1include_1 = (com.liferay.taglib.util.DynamicIncludeTag) _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.get(com.liferay.taglib.util.DynamicIncludeTag.class);
    _jspx_th_liferay$1util_dynamic$1include_1.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1util_dynamic$1include_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    _jspx_th_liferay$1util_dynamic$1include_1.setKey("com.liferay.document.library.web#/document_library/view.jsp#errors");
    int _jspx_eval_liferay$1util_dynamic$1include_1 = _jspx_th_liferay$1util_dynamic$1include_1.doStartTag();
    if (_jspx_th_liferay$1util_dynamic$1include_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_1);
      return true;
    }
    _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_1);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_4(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_4 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_4.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    int _jspx_eval_portlet_namespace_4 = _jspx_th_portlet_namespace_4.doStartTag();
    if (_jspx_th_portlet_namespace_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_4);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_4);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_5(javax.servlet.jsp.tagext.JspTag _jspx_th_c_otherwise_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_5 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_5.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
    int _jspx_eval_portlet_namespace_5 = _jspx_th_portlet_namespace_5.doStartTag();
    if (_jspx_th_portlet_namespace_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_5);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_5);
    return false;
  }

  private boolean _jspx_meth_aui_script_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_otherwise_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:script
    com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_0 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script.get(com.liferay.taglib.aui.ScriptTag.class);
    _jspx_th_aui_script_0.setPageContext(_jspx_page_context);
    _jspx_th_aui_script_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
    int _jspx_eval_aui_script_0 = _jspx_th_aui_script_0.doStartTag();
    if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_aui_script_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_aui_script_0.doInitBody();
      }
      do {
        out.write("\n\t\t\tfunction ");
        if (_jspx_meth_portlet_namespace_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
          return true;
        out.write("move(\n\t\t\t\titemsSelected,\n\t\t\t\tparameterName,\n\t\t\t\tparameterValue\n\t\t\t) {\n\t\t\t\tvar dlComponent = Liferay.component('");
        if (_jspx_meth_portlet_namespace_7((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
          return true;
        out.write("DocumentLibrary');\n\n\t\t\t\tif (dlComponent) {\n\t\t\t\t\tdlComponent.showFolderDialog(\n\t\t\t\t\t\titemsSelected,\n\t\t\t\t\t\tparameterName,\n\t\t\t\t\t\tparameterValue\n\t\t\t\t\t);\n\t\t\t\t}\n\t\t\t}\n\t\t");
        int evalDoAfterBody = _jspx_th_aui_script_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
        out = _jspx_page_context.popBody();
    }
    if (_jspx_th_aui_script_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_script.reuse(_jspx_th_aui_script_0);
      return true;
    }
    _jspx_tagPool_aui_script.reuse(_jspx_th_aui_script_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_6(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_6 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_6.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_6 = _jspx_th_portlet_namespace_6.doStartTag();
    if (_jspx_th_portlet_namespace_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_6);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_6);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_7(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_7 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_7.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_7 = _jspx_th_portlet_namespace_7.doStartTag();
    if (_jspx_th_portlet_namespace_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_7);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_7);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_8(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_8 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_8.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_8 = _jspx_th_portlet_namespace_8.doStartTag();
    if (_jspx_th_portlet_namespace_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_8);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_8);
    return false;
  }

  private boolean _jspx_meth_portlet_actionURL_1(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:actionURL
    com.liferay.taglib.portlet.ActionURLTag _jspx_th_portlet_actionURL_1 = (com.liferay.taglib.portlet.ActionURLTag) _jspx_tagPool_portlet_actionURL_name_nobody.get(com.liferay.taglib.portlet.ActionURLTag.class);
    _jspx_th_portlet_actionURL_1.setPageContext(_jspx_page_context);
    _jspx_th_portlet_actionURL_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    _jspx_th_portlet_actionURL_1.setName("/document_library/edit_entry");
    int _jspx_eval_portlet_actionURL_1 = _jspx_th_portlet_actionURL_1.doStartTag();
    if (_jspx_th_portlet_actionURL_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_actionURL_name_nobody.reuse(_jspx_th_portlet_actionURL_1);
      return true;
    }
    _jspx_tagPool_portlet_actionURL_name_nobody.reuse(_jspx_th_portlet_actionURL_1);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_9(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_9 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_9.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_9 = _jspx_th_portlet_namespace_9.doStartTag();
    if (_jspx_th_portlet_namespace_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_9);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_9);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_10(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_10 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_10.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_10 = _jspx_th_portlet_namespace_10.doStartTag();
    if (_jspx_th_portlet_namespace_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_10);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_10);
    return false;
  }

  private boolean _jspx_meth_portlet_param_8(javax.servlet.jsp.tagext.JspTag _jspx_th_portlet_renderURL_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_8 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_8.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
    _jspx_th_portlet_param_8.setName("mvcPath");
    _jspx_th_portlet_param_8.setValue("/document_library/view_more_menu_items.jsp");
    int _jspx_eval_portlet_param_8 = _jspx_th_portlet_param_8.doStartTag();
    if (_jspx_th_portlet_param_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_8);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_8);
    return false;
  }

  private boolean _jspx_meth_portlet_param_11(javax.servlet.jsp.tagext.JspTag _jspx_th_portlet_renderURL_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_11 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_11.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_1);
    _jspx_th_portlet_param_11.setName("mvcPath");
    _jspx_th_portlet_param_11.setValue("/document_library/select_file_entry_type.jsp");
    int _jspx_eval_portlet_param_11 = _jspx_th_portlet_param_11.doStartTag();
    if (_jspx_th_portlet_param_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_11);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_11);
    return false;
  }

  private boolean _jspx_meth_portlet_param_13(javax.servlet.jsp.tagext.JspTag _jspx_th_portlet_renderURL_2, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_13 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_13.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_13.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_2);
    _jspx_th_portlet_param_13.setName("mvcRenderCommandName");
    _jspx_th_portlet_param_13.setValue("/document_library/select_folder");
    int _jspx_eval_portlet_param_13 = _jspx_th_portlet_param_13.doStartTag();
    if (_jspx_th_portlet_param_13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_13);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_13);
    return false;
  }

  private boolean _jspx_meth_portlet_param_15(javax.servlet.jsp.tagext.JspTag _jspx_th_portlet_renderURL_3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_15 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_15.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_15.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_3);
    _jspx_th_portlet_param_15.setName("mvcRenderCommandName");
    _jspx_th_portlet_param_15.setValue("/document_library/view_file_entry");
    int _jspx_eval_portlet_param_15 = _jspx_th_portlet_param_15.doStartTag();
    if (_jspx_th_portlet_param_15.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_15);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_15);
    return false;
  }

  private boolean _jspx_meth_portlet_param_17(javax.servlet.jsp.tagext.JspTag _jspx_th_portlet_renderURL_4, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_17 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_17.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_17.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_4);
    _jspx_th_portlet_param_17.setName("mvcRenderCommandName");
    _jspx_th_portlet_param_17.setValue("/document_library/edit_file_entry");
    int _jspx_eval_portlet_param_17 = _jspx_th_portlet_param_17.doStartTag();
    if (_jspx_th_portlet_param_17.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_17);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_17);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_11(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_11 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_11.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_11 = _jspx_th_portlet_namespace_11.doStartTag();
    if (_jspx_th_portlet_namespace_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_11);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_11);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_12(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_12 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_12.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_12 = _jspx_th_portlet_namespace_12.doStartTag();
    if (_jspx_th_portlet_namespace_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_12);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_12);
    return false;
  }

  private boolean _jspx_meth_liferay$1util_dynamic$1include_2(javax.servlet.jsp.tagext.JspTag _jspx_th_c_otherwise_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-util:dynamic-include
    com.liferay.taglib.util.DynamicIncludeTag _jspx_th_liferay$1util_dynamic$1include_2 = (com.liferay.taglib.util.DynamicIncludeTag) _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.get(com.liferay.taglib.util.DynamicIncludeTag.class);
    _jspx_th_liferay$1util_dynamic$1include_2.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1util_dynamic$1include_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
    _jspx_th_liferay$1util_dynamic$1include_2.setKey("com.liferay.document.library.web#/document_library/view.jsp#post");
    int _jspx_eval_liferay$1util_dynamic$1include_2 = _jspx_th_liferay$1util_dynamic$1include_2.doStartTag();
    if (_jspx_th_liferay$1util_dynamic$1include_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_2);
      return true;
    }
    _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_2);
    return false;
  }
}
