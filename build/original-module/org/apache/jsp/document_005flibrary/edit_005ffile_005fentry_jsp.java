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

public final class edit_005ffile_005fentry_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


private static Log _log = LogFactoryUtil.getLog("com_liferay_document_library_web.document_library.edit_file_entry_jsp");

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList<String>(5);
    _jspx_dependants.add("/document_library/init.jsp");
    _jspx_dependants.add("/init.jsp");
    _jspx_dependants.add("/init-ext.jsp");
    _jspx_dependants.add("/document_library/init-ext.jsp");
    _jspx_dependants.add("/document_library/edit_file_entry_picker.jspf");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_arguments_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_error_exception;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_model$1context_model_bean_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_param_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button_value_type_name_disabled_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_validator_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_input$1asset$1links_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_script_require;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_script_sandbox;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_select_onChange_name_label_changesContext;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_option_value_selected_label_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_type_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_fieldset$1group_markupView;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_workflow$1status_version_status_showLabel_showIcon_showHelpMessage_model_markupView_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_fieldset;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_when_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_info$1bar;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1portlet_renderURL_windowState_var;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_name_maxLength_label_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button_type_href_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button_value_name_disabled_cssClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_fieldset_label_collapsible_collapsed;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1portlet_param_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_form_onSubmit_name_method_enctype_cssClass_action;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_type_onChange_name_autoFocus;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classTypeId_classPK_classNameId_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1categories$1error_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ddm_html_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_upload$1progress_message_id_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_value_type_name_label_checked_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_model$1context_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1portlet_actionURL_varImpl_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_choose;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button_value_onClick_disabled_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1tags$1error_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_include_servletContext_page_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_value_type_name_label_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_namespace_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_value_type_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button_value_onClick_name_disabled_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_field$1wrapper_label;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_otherwise;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_error_message_exception_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button$1row;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_error_exception = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_model$1context_model_bean_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_param_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button_value_type_name_disabled_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_validator_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_input$1asset$1links_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_script_require = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_script_sandbox = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_select_onChange_name_label_changesContext = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_option_value_selected_label_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_type_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_fieldset$1group_markupView = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_workflow$1status_version_status_showLabel_showIcon_showHelpMessage_model_markupView_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_fieldset = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_when_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_info$1bar = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1portlet_renderURL_windowState_var = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_name_maxLength_label_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button_type_href_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button_value_name_disabled_cssClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_fieldset_label_collapsible_collapsed = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1portlet_param_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_form_onSubmit_name_method_enctype_cssClass_action = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_type_onChange_name_autoFocus = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classTypeId_classPK_classNameId_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1categories$1error_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ddm_html_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_upload$1progress_message_id_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_value_type_name_label_checked_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_model$1context_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1portlet_actionURL_varImpl_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_choose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button_value_onClick_disabled_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1tags$1error_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_value_type_name_label_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_namespace_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_value_type_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button_value_onClick_name_disabled_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_field$1wrapper_label = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_otherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_error_message_exception_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button$1row = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.release();
    _jspx_tagPool_liferay$1ui_error_exception.release();
    _jspx_tagPool_aui_model$1context_model_bean_nobody.release();
    _jspx_tagPool_portlet_param_value_name_nobody.release();
    _jspx_tagPool_aui_button_value_type_name_disabled_nobody.release();
    _jspx_tagPool_aui_validator_name.release();
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.release();
    _jspx_tagPool_liferay$1asset_input$1asset$1links_classPK_className_nobody.release();
    _jspx_tagPool_aui_script_require.release();
    _jspx_tagPool_aui_script_sandbox.release();
    _jspx_tagPool_aui_select_onChange_name_label_changesContext.release();
    _jspx_tagPool_aui_option_value_selected_label_nobody.release();
    _jspx_tagPool_aui_input_type_name_nobody.release();
    _jspx_tagPool_aui_fieldset$1group_markupView.release();
    _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody.release();
    _jspx_tagPool_aui_workflow$1status_version_status_showLabel_showIcon_showHelpMessage_model_markupView_nobody.release();
    _jspx_tagPool_aui_fieldset.release();
    _jspx_tagPool_c_when_test.release();
    _jspx_tagPool_liferay$1frontend_info$1bar.release();
    _jspx_tagPool_liferay$1portlet_renderURL_windowState_var.release();
    _jspx_tagPool_aui_input_name_maxLength_label_nobody.release();
    _jspx_tagPool_aui_button_type_href_nobody.release();
    _jspx_tagPool_aui_button_value_name_disabled_cssClass_nobody.release();
    _jspx_tagPool_liferay$1ui_message_key_nobody.release();
    _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1portlet_param_value_name_nobody.release();
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.release();
    _jspx_tagPool_aui_form_onSubmit_name_method_enctype_cssClass_action.release();
    _jspx_tagPool_aui_input_name_nobody.release();
    _jspx_tagPool_aui_input_type_onChange_name_autoFocus.release();
    _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classTypeId_classPK_classNameId_nobody.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.release();
    _jspx_tagPool_liferay$1asset_asset$1categories$1error_nobody.release();
    _jspx_tagPool_aui_button_value_name_nobody.release();
    _jspx_tagPool_liferay$1ddm_html_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody.release();
    _jspx_tagPool_liferay$1ui_upload$1progress_message_id_nobody.release();
    _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.release();
    _jspx_tagPool_aui_model$1context_nobody.release();
    _jspx_tagPool_liferay$1portlet_actionURL_varImpl_name.release();
    _jspx_tagPool_c_choose.release();
    _jspx_tagPool_aui_button_value_onClick_disabled_nobody.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1asset_asset$1tags$1error_nobody.release();
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.release();
    _jspx_tagPool_aui_input_value_type_name_label_nobody.release();
    _jspx_tagPool_portlet_namespace_nobody.release();
    _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.release();
    _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody.release();
    _jspx_tagPool_aui_input_value_type_name_nobody.release();
    _jspx_tagPool_aui_button_value_onClick_name_disabled_nobody.release();
    _jspx_tagPool_aui_field$1wrapper_label.release();
    _jspx_tagPool_c_otherwise.release();
    _jspx_tagPool_liferay$1ui_error_message_exception_nobody.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.release();
    _jspx_tagPool_aui_button$1row.release();
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

String cmd = ParamUtil.getString(request, Constants.CMD, Constants.EDIT);

String redirect = ParamUtil.getString(request, "redirect");

String uploadProgressId = "dlFileEntryUploadProgress";

FileEntry fileEntry = (FileEntry)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY);

long fileEntryId = BeanParamUtil.getLong(fileEntry, request, "fileEntryId");

long repositoryId = BeanParamUtil.getLong(fileEntry, request, "repositoryId");

if (repositoryId <= 0) {

	// <liferay-ui:asset_add_button /> only passes in groupId

	repositoryId = BeanParamUtil.getLong(fileEntry, request, "groupId");
}

long folderId = BeanParamUtil.getLong(fileEntry, request, "folderId");

Folder folder = null;

if (fileEntry != null) {
	folder = fileEntry.getFolder();
}

if ((folder == null) && (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {
	try {
		folder = DLAppServiceUtil.getFolder(folderId);
	}
	catch (NoSuchFolderException nsfe) {
		folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}
}

FileVersion fileVersion = null;

long fileVersionId = 0;

long fileEntryTypeId = ParamUtil.getLong(request, "fileEntryTypeId", -1);

if (fileEntry != null) {
	fileVersion = fileEntry.getLatestFileVersion();

	fileVersionId = fileVersion.getFileVersionId();

	if ((fileEntryTypeId == -1) && (fileVersion.getModel() instanceof DLFileVersion)) {
		DLFileVersion dlFileVersion = (DLFileVersion)fileVersion.getModel();

		fileEntryTypeId = dlFileVersion.getFileEntryTypeId();
	}
}

DLFileEntryType dlFileEntryType = null;

if (fileEntryTypeId >= 0) {
	dlFileEntryType = DLFileEntryTypeLocalServiceUtil.getFileEntryType(fileEntryTypeId);
}

DLVersionNumberIncrease dlVersionNumberIncrease = DLVersionNumberIncrease.valueOf(request.getParameter("versionIncrease"), DLVersionNumberIncrease.AUTOMATIC);
boolean updateVersionDetails = ParamUtil.getBoolean(request, "updateVersionDetails");

long assetClassPK = DLAssetHelperUtil.getAssetClassPK(fileEntry, fileVersion);

boolean checkedOut = false;
boolean pending = false;

if (fileEntry != null) {
	checkedOut = fileEntry.isCheckedOut();
	pending = fileVersion.isPending();
}

boolean saveAsDraft = false;

if ((checkedOut || pending) && !dlPortletInstanceSettings.isEnableFileEntryDrafts()) {
	saveAsDraft = true;
}

DLEditFileEntryDisplayContext dlEditFileEntryDisplayContext = null;

if (fileEntry == null) {
	dlEditFileEntryDisplayContext = dlDisplayContextProvider.getDLEditFileEntryDisplayContext(request, response, dlFileEntryType);
}
else {
	dlEditFileEntryDisplayContext = dlDisplayContextProvider.getDLEditFileEntryDisplayContext(request, response, fileEntry);
}

String defaultLanguageId = LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault());

String headerTitle = LanguageUtil.get(request, "new-document");

if (fileVersion != null) {
	headerTitle = fileVersion.getTitle();
}
else if ((dlFileEntryType != null) && (fileEntryTypeId != 0)) {
	headerTitle = LanguageUtil.format(request, "new-x", dlFileEntryType.getName(locale), false);
}

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (portletTitleBasedNavigation) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);

	renderResponse.setTitle(headerTitle);
}

      out.write('\n');
      out.write('\n');
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_0.setPageContext(_jspx_page_context);
      _jspx_th_c_if_0.setParent(null);
      _jspx_th_c_if_0.setTest( portletTitleBasedNavigation && (fileVersion != null) );
      int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
      if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\n\t");

	String version = null;

	if (dlEditFileEntryDisplayContext.isVersionInfoVisible()) {
		version = fileVersion.getVersion();
	}
	
          out.write("\n\n\t");
          //  liferay-frontend:info-bar
          com.liferay.frontend.taglib.servlet.taglib.InfoBarTag _jspx_th_liferay$1frontend_info$1bar_0 = (com.liferay.frontend.taglib.servlet.taglib.InfoBarTag) _jspx_tagPool_liferay$1frontend_info$1bar.get(com.liferay.frontend.taglib.servlet.taglib.InfoBarTag.class);
          _jspx_th_liferay$1frontend_info$1bar_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1frontend_info$1bar_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
          int _jspx_eval_liferay$1frontend_info$1bar_0 = _jspx_th_liferay$1frontend_info$1bar_0.doStartTag();
          if (_jspx_eval_liferay$1frontend_info$1bar_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n\t\t");
            //  aui:workflow-status
            com.liferay.taglib.aui.WorkflowStatusTag _jspx_th_aui_workflow$1status_0 = (com.liferay.taglib.aui.WorkflowStatusTag) _jspx_tagPool_aui_workflow$1status_version_status_showLabel_showIcon_showHelpMessage_model_markupView_nobody.get(com.liferay.taglib.aui.WorkflowStatusTag.class);
            _jspx_th_aui_workflow$1status_0.setPageContext(_jspx_page_context);
            _jspx_th_aui_workflow$1status_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_info$1bar_0);
            _jspx_th_aui_workflow$1status_0.setMarkupView("lexicon");
            _jspx_th_aui_workflow$1status_0.setModel( DLFileEntry.class );
            _jspx_th_aui_workflow$1status_0.setShowHelpMessage( false );
            _jspx_th_aui_workflow$1status_0.setShowIcon( false );
            _jspx_th_aui_workflow$1status_0.setShowLabel( false );
            _jspx_th_aui_workflow$1status_0.setStatus( fileVersion.getStatus() );
            _jspx_th_aui_workflow$1status_0.setVersion( version );
            int _jspx_eval_aui_workflow$1status_0 = _jspx_th_aui_workflow$1status_0.doStartTag();
            if (_jspx_th_aui_workflow$1status_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_aui_workflow$1status_version_status_showLabel_showIcon_showHelpMessage_model_markupView_nobody.reuse(_jspx_th_aui_workflow$1status_0);
              return;
            }
            _jspx_tagPool_aui_workflow$1status_version_status_showLabel_showIcon_showHelpMessage_model_markupView_nobody.reuse(_jspx_th_aui_workflow$1status_0);
            out.write('\n');
            out.write('	');
          }
          if (_jspx_th_liferay$1frontend_info$1bar_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1frontend_info$1bar.reuse(_jspx_th_liferay$1frontend_info$1bar_0);
            return;
          }
          _jspx_tagPool_liferay$1frontend_info$1bar.reuse(_jspx_th_liferay$1frontend_info$1bar_0);
          out.write('\n');
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
      out.write("\n\n<div ");
      out.print( portletTitleBasedNavigation ? "class=\"container-fluid-1280\"" : StringPool.BLANK );
      out.write(">\n\t");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_1.setPageContext(_jspx_page_context);
      _jspx_th_c_if_1.setParent(null);
      _jspx_th_c_if_1.setTest( checkedOut );
      int _jspx_eval_c_if_1 = _jspx_th_c_if_1.doStartTag();
      if (_jspx_eval_c_if_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\n\t\t");

		boolean hasLock = fileEntry.hasLock();

		Lock lock = fileEntry.getLock();
		
          out.write("\n\n\t\t");
          //  c:choose
          org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_0 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
          _jspx_th_c_choose_0.setPageContext(_jspx_page_context);
          _jspx_th_c_choose_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
          int _jspx_eval_c_choose_0 = _jspx_th_c_choose_0.doStartTag();
          if (_jspx_eval_c_choose_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t");
              //  c:when
              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_0 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
              _jspx_th_c_when_0.setPageContext(_jspx_page_context);
              _jspx_th_c_when_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
              _jspx_th_c_when_0.setTest( hasLock );
              int _jspx_eval_c_when_0 = _jspx_th_c_when_0.doStartTag();
              if (_jspx_eval_c_when_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t<div class=\"alert alert-success\">\n\t\t\t\t\t");
                  //  c:choose
                  org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_1 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                  _jspx_th_c_choose_1.setPageContext(_jspx_page_context);
                  _jspx_th_c_choose_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
                  int _jspx_eval_c_choose_1 = _jspx_th_c_choose_1.doStartTag();
                  if (_jspx_eval_c_choose_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    do {
                      out.write("\n\t\t\t\t\t\t");
                      //  c:when
                      org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_1 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                      _jspx_th_c_when_1.setPageContext(_jspx_page_context);
                      _jspx_th_c_when_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                      _jspx_th_c_when_1.setTest( lock.isNeverExpires() );
                      int _jspx_eval_c_when_1 = _jspx_th_c_when_1.doStartTag();
                      if (_jspx_eval_c_when_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\n\t\t\t\t\t\t\t");
                          if (_jspx_meth_liferay$1ui_message_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_1, _jspx_page_context))
                            return;
                          out.write("\n\t\t\t\t\t\t");
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
                      out.write("\n\t\t\t\t\t\t");
                      //  c:otherwise
                      org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_0 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                      _jspx_th_c_otherwise_0.setPageContext(_jspx_page_context);
                      _jspx_th_c_otherwise_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                      int _jspx_eval_c_otherwise_0 = _jspx_th_c_otherwise_0.doStartTag();
                      if (_jspx_eval_c_otherwise_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\n\t\t\t\t\t\t\t");
                          //  liferay-ui:message
                          com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_1 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                          _jspx_th_liferay$1ui_message_1.setPageContext(_jspx_page_context);
                          _jspx_th_liferay$1ui_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
                          _jspx_th_liferay$1ui_message_1.setArguments( StringUtil.toLowerCase(LanguageUtil.getTimeDescription(request, DLFileEntryConstants.LOCK_EXPIRATION_TIME)) );
                          _jspx_th_liferay$1ui_message_1.setKey("you-now-have-a-lock-on-this-document");
                          _jspx_th_liferay$1ui_message_1.setTranslateArguments( false );
                          int _jspx_eval_liferay$1ui_message_1 = _jspx_th_liferay$1ui_message_1.doStartTag();
                          if (_jspx_th_liferay$1ui_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                            _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_1);
                            return;
                          }
                          _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_1);
                          out.write("\n\t\t\t\t\t\t");
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
                      out.write("\n\t\t\t\t\t");
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
                  out.write("\n\t\t\t\t</div>\n\t\t\t");
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
              out.write("\n\t\t\t");
              //  c:otherwise
              org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_1 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
              _jspx_th_c_otherwise_1.setPageContext(_jspx_page_context);
              _jspx_th_c_otherwise_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
              int _jspx_eval_c_otherwise_1 = _jspx_th_c_otherwise_1.doStartTag();
              if (_jspx_eval_c_otherwise_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t<div class=\"alert alert-danger\">\n\t\t\t\t\t");
                  //  liferay-ui:message
                  com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_2 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                  _jspx_th_liferay$1ui_message_2.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1ui_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_1);
                  _jspx_th_liferay$1ui_message_2.setArguments( new Object[] {HtmlUtil.escape(PortalUtil.getUserName(lock.getUserId(), String.valueOf(lock.getUserId()))), dateFormatDateTime.format(lock.getCreateDate())} );
                  _jspx_th_liferay$1ui_message_2.setKey("you-cannot-modify-this-document-because-it-was-checked-out-by-x-on-x");
                  _jspx_th_liferay$1ui_message_2.setTranslateArguments( false );
                  int _jspx_eval_liferay$1ui_message_2 = _jspx_th_liferay$1ui_message_2.doStartTag();
                  if (_jspx_th_liferay$1ui_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_2);
                    return;
                  }
                  _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_2);
                  out.write("\n\t\t\t\t</div>\n\t\t\t");
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
              out.write("\n\t\t");
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
          out.write('\n');
          out.write('	');
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
      out.write("\n\n\t");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_2 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_2.setPageContext(_jspx_page_context);
      _jspx_th_c_if_2.setParent(null);
      _jspx_th_c_if_2.setTest( !portletTitleBasedNavigation && showHeader );
      int _jspx_eval_c_if_2 = _jspx_th_c_if_2.doStartTag();
      if (_jspx_eval_c_if_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\t\t");
          //  liferay-ui:header
          com.liferay.taglib.ui.HeaderTag _jspx_th_liferay$1ui_header_0 = (com.liferay.taglib.ui.HeaderTag) _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.get(com.liferay.taglib.ui.HeaderTag.class);
          _jspx_th_liferay$1ui_header_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1ui_header_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_2);
          _jspx_th_liferay$1ui_header_0.setBackURL( redirect );
          _jspx_th_liferay$1ui_header_0.setLocalizeTitle( false );
          _jspx_th_liferay$1ui_header_0.setTitle( headerTitle );
          int _jspx_eval_liferay$1ui_header_0 = _jspx_th_liferay$1ui_header_0.doStartTag();
          if (_jspx_th_liferay$1ui_header_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.reuse(_jspx_th_liferay$1ui_header_0);
            return;
          }
          _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.reuse(_jspx_th_liferay$1ui_header_0);
          out.write('\n');
          out.write('	');
          int evalDoAfterBody = _jspx_th_c_if_2.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_2);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_2);
      out.write("\n\n\t");
      //  liferay-portlet:actionURL
      com.liferay.taglib.portlet.ActionURLTag _jspx_th_liferay$1portlet_actionURL_0 = (com.liferay.taglib.portlet.ActionURLTag) _jspx_tagPool_liferay$1portlet_actionURL_varImpl_name.get(com.liferay.taglib.portlet.ActionURLTag.class);
      _jspx_th_liferay$1portlet_actionURL_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1portlet_actionURL_0.setParent(null);
      _jspx_th_liferay$1portlet_actionURL_0.setName("/document_library/edit_file_entry");
      _jspx_th_liferay$1portlet_actionURL_0.setVarImpl("editFileEntryURL");
      int _jspx_eval_liferay$1portlet_actionURL_0 = _jspx_th_liferay$1portlet_actionURL_0.doStartTag();
      if (_jspx_eval_liferay$1portlet_actionURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n\t\t");
        if (_jspx_meth_liferay$1portlet_param_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1portlet_actionURL_0, _jspx_page_context))
          return;
        out.write('\n');
        out.write('	');
      }
      if (_jspx_th_liferay$1portlet_actionURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1portlet_actionURL_varImpl_name.reuse(_jspx_th_liferay$1portlet_actionURL_0);
        return;
      }
      _jspx_tagPool_liferay$1portlet_actionURL_varImpl_name.reuse(_jspx_th_liferay$1portlet_actionURL_0);
      com.liferay.portal.kernel.portlet.LiferayPortletURL editFileEntryURL = null;
      editFileEntryURL = (com.liferay.portal.kernel.portlet.LiferayPortletURL) _jspx_page_context.findAttribute("editFileEntryURL");
      out.write("\n\n\t");
      //  aui:form
      com.liferay.taglib.aui.FormTag _jspx_th_aui_form_0 = (com.liferay.taglib.aui.FormTag) _jspx_tagPool_aui_form_onSubmit_name_method_enctype_cssClass_action.get(com.liferay.taglib.aui.FormTag.class);
      _jspx_th_aui_form_0.setPageContext(_jspx_page_context);
      _jspx_th_aui_form_0.setParent(null);
      _jspx_th_aui_form_0.setAction( editFileEntryURL );
      _jspx_th_aui_form_0.setCssClass("lfr-dynamic-form");
      _jspx_th_aui_form_0.setDynamicAttribute(null, "enctype", new String("multipart/form-data"));
      _jspx_th_aui_form_0.setMethod("post");
      _jspx_th_aui_form_0.setName("fm");
      _jspx_th_aui_form_0.setOnSubmit( "event.preventDefault(); " + renderResponse.getNamespace() + "saveFileEntry(" + saveAsDraft + ");" );
      int _jspx_eval_aui_form_0 = _jspx_th_aui_form_0.doStartTag();
      if (_jspx_eval_aui_form_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n\t\t");
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
        out.write("\n\t\t");
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_1 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_1.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_1.setName("redirect");
        _jspx_th_aui_input_1.setType("hidden");
        _jspx_th_aui_input_1.setValue( redirect );
        int _jspx_eval_aui_input_1 = _jspx_th_aui_input_1.doStartTag();
        if (_jspx_th_aui_input_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_1);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_1);
        out.write("\n\t\t");
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_2 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_2.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_2.setName("uploadProgressId");
        _jspx_th_aui_input_2.setType("hidden");
        _jspx_th_aui_input_2.setValue( uploadProgressId );
        int _jspx_eval_aui_input_2 = _jspx_th_aui_input_2.doStartTag();
        if (_jspx_th_aui_input_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_2);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_2);
        out.write("\n\t\t");
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_3 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_3.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_3.setName("repositoryId");
        _jspx_th_aui_input_3.setType("hidden");
        _jspx_th_aui_input_3.setValue( repositoryId );
        int _jspx_eval_aui_input_3 = _jspx_th_aui_input_3.doStartTag();
        if (_jspx_th_aui_input_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_3);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_3);
        out.write("\n\t\t");
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
        out.write("\n\t\t");
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_5 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_5.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_5.setName("fileEntryId");
        _jspx_th_aui_input_5.setType("hidden");
        _jspx_th_aui_input_5.setValue( fileEntryId );
        int _jspx_eval_aui_input_5 = _jspx_th_aui_input_5.doStartTag();
        if (_jspx_th_aui_input_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_5);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_5);
        out.write("\n\n\t\t");
        //  c:if
        org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_3 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
        _jspx_th_c_if_3.setPageContext(_jspx_page_context);
        _jspx_th_c_if_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_c_if_3.setTest( (fileEntry != null) && checkedOut );
        int _jspx_eval_c_if_3 = _jspx_th_c_if_3.doStartTag();
        if (_jspx_eval_c_if_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          do {
            out.write("\n\t\t\t");
            if (_jspx_meth_aui_input_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_3, _jspx_page_context))
              return;
            out.write("\n\t\t\t");
            if (_jspx_meth_aui_input_7((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_3, _jspx_page_context))
              return;
            out.write("\n\t\t");
            int evalDoAfterBody = _jspx_th_c_if_3.doAfterBody();
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
        }
        if (_jspx_th_c_if_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_3);
          return;
        }
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_3);
        out.write("\n\n\t\t");
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_8 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_8.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_8.setName("workflowAction");
        _jspx_th_aui_input_8.setType("hidden");
        _jspx_th_aui_input_8.setValue( String.valueOf(WorkflowConstants.ACTION_PUBLISH) );
        int _jspx_eval_aui_input_8 = _jspx_th_aui_input_8.doStartTag();
        if (_jspx_th_aui_input_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_8);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_8);
        out.write("\n\n\t\t<div class=\"lfr-form-content\">\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_0 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_0.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_0.setException( RequiredFileException.class );
        _jspx_th_liferay$1ui_error_0.setMessage("please-select-the-file-again");
        int _jspx_eval_liferay$1ui_error_0 = _jspx_th_liferay$1ui_error_0.doStartTag();
        if (_jspx_th_liferay$1ui_error_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_0);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_0);
        out.write("\n\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_1 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_exception.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_1.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_1.setException( AntivirusScannerException.class );
        int _jspx_eval_liferay$1ui_error_1 = _jspx_th_liferay$1ui_error_1.doStartTag();
        if (_jspx_eval_liferay$1ui_error_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Object errorException = null;
          if (_jspx_eval_liferay$1ui_error_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_error_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_error_1.doInitBody();
          }
          errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
          do {
            out.write("\n\n\t\t\t\t");

				AntivirusScannerException ase = (AntivirusScannerException)errorException;
				
            out.write("\n\n\t\t\t\t");
            //  liferay-ui:message
            com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_3 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
            _jspx_th_liferay$1ui_message_3.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_1);
            _jspx_th_liferay$1ui_message_3.setKey( ase.getMessageKey() );
            int _jspx_eval_liferay$1ui_message_3 = _jspx_th_liferay$1ui_message_3.doStartTag();
            if (_jspx_th_liferay$1ui_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_3);
              return;
            }
            _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_3);
            out.write("\n\t\t\t");
            int evalDoAfterBody = _jspx_th_liferay$1ui_error_1.doAfterBody();
            errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_error_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_error_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_1);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_1);
        out.write("\n\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_2 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_2.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_2.setException( DuplicateFileEntryException.class );
        _jspx_th_liferay$1ui_error_2.setMessage("please-enter-a-unique-document-name");
        int _jspx_eval_liferay$1ui_error_2 = _jspx_th_liferay$1ui_error_2.doStartTag();
        if (_jspx_th_liferay$1ui_error_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_2);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_2);
        out.write("\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_3 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_3.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_3.setException( DuplicateFolderNameException.class );
        _jspx_th_liferay$1ui_error_3.setMessage("please-enter-a-unique-document-name");
        int _jspx_eval_liferay$1ui_error_3 = _jspx_th_liferay$1ui_error_3.doStartTag();
        if (_jspx_th_liferay$1ui_error_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_3);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_3);
        out.write("\n\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_4 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_exception.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_4.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_4.setException( LiferayFileItemException.class );
        int _jspx_eval_liferay$1ui_error_4 = _jspx_th_liferay$1ui_error_4.doStartTag();
        if (_jspx_eval_liferay$1ui_error_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Object errorException = null;
          if (_jspx_eval_liferay$1ui_error_4 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_error_4.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_error_4.doInitBody();
          }
          errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
          do {
            out.write("\n\t\t\t\t");
            //  liferay-ui:message
            com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_4 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
            _jspx_th_liferay$1ui_message_4.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_message_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_4);
            _jspx_th_liferay$1ui_message_4.setArguments( LanguageUtil.formatStorageSize(LiferayFileItem.THRESHOLD_SIZE, locale) );
            _jspx_th_liferay$1ui_message_4.setKey("please-enter-valid-content-with-valid-content-size-no-larger-than-x");
            _jspx_th_liferay$1ui_message_4.setTranslateArguments( false );
            int _jspx_eval_liferay$1ui_message_4 = _jspx_th_liferay$1ui_message_4.doStartTag();
            if (_jspx_th_liferay$1ui_message_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_4);
              return;
            }
            _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_4);
            out.write("\n\t\t\t");
            int evalDoAfterBody = _jspx_th_liferay$1ui_error_4.doAfterBody();
            errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_error_4 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_error_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_4);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_4);
        out.write("\n\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_5 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_exception.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_5.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_5.setException( FileExtensionException.class );
        int _jspx_eval_liferay$1ui_error_5 = _jspx_th_liferay$1ui_error_5.doStartTag();
        if (_jspx_eval_liferay$1ui_error_5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Object errorException = null;
          if (_jspx_eval_liferay$1ui_error_5 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_error_5.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_error_5.doInitBody();
          }
          errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
          do {
            out.write("\n\t\t\t\t");
            if (_jspx_meth_liferay$1ui_message_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_error_5, _jspx_page_context))
              return;
            out.write(' ');
            out.print( StringUtil.merge(dlConfiguration.fileExtensions(), StringPool.COMMA_AND_SPACE) );
            out.write(".\n\t\t\t");
            int evalDoAfterBody = _jspx_th_liferay$1ui_error_5.doAfterBody();
            errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_error_5 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_error_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_5);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_5);
        out.write("\n\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_6 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_exception.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_6.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_6.setException( FileMimeTypeException.class );
        int _jspx_eval_liferay$1ui_error_6 = _jspx_th_liferay$1ui_error_6.doStartTag();
        if (_jspx_eval_liferay$1ui_error_6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Object errorException = null;
          if (_jspx_eval_liferay$1ui_error_6 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_error_6.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_error_6.doInitBody();
          }
          errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
          do {
            out.write("\n\t\t\t\t");
            if (_jspx_meth_liferay$1ui_message_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_error_6, _jspx_page_context))
              return;
            out.write(' ');
            out.print( StringUtil.merge(dlPortletInstanceSettings.getMimeTypes(), StringPool.COMMA_AND_SPACE) );
            out.write(".\n\t\t\t");
            int evalDoAfterBody = _jspx_th_liferay$1ui_error_6.doAfterBody();
            errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_error_6 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_error_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_6);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_6);
        out.write("\n\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_7 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_7.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_7.setException( FileNameException.class );
        _jspx_th_liferay$1ui_error_7.setMessage("please-enter-a-file-with-a-valid-file-name");
        int _jspx_eval_liferay$1ui_error_7 = _jspx_th_liferay$1ui_error_7.doStartTag();
        if (_jspx_th_liferay$1ui_error_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_7);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_7);
        out.write("\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_8 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_8.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_8.setException( NoSuchFolderException.class );
        _jspx_th_liferay$1ui_error_8.setMessage("please-enter-a-valid-folder");
        int _jspx_eval_liferay$1ui_error_8 = _jspx_th_liferay$1ui_error_8.doStartTag();
        if (_jspx_th_liferay$1ui_error_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_8);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_8);
        out.write("\n\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_9 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_exception.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_9.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_9.setException( SourceFileNameException.class );
        int _jspx_eval_liferay$1ui_error_9 = _jspx_th_liferay$1ui_error_9.doStartTag();
        if (_jspx_eval_liferay$1ui_error_9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Object errorException = null;
          if (_jspx_eval_liferay$1ui_error_9 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_error_9.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_error_9.doInitBody();
          }
          errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
          do {
            out.write("\n\t\t\t\t");
            if (_jspx_meth_liferay$1ui_message_7((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_error_9, _jspx_page_context))
              return;
            out.write("\n\t\t\t");
            int evalDoAfterBody = _jspx_th_liferay$1ui_error_9.doAfterBody();
            errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_error_9 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_error_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_9);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_9);
        out.write("\n\n\t\t\t");

			long fileMaxSize = dlEditFileEntryDisplayContext.getMaximumUploadSize();
			
        out.write("\n\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_10 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_exception.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_10.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_10.setException( FileSizeException.class );
        int _jspx_eval_liferay$1ui_error_10 = _jspx_th_liferay$1ui_error_10.doStartTag();
        if (_jspx_eval_liferay$1ui_error_10 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Object errorException = null;
          if (_jspx_eval_liferay$1ui_error_10 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_error_10.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_error_10.doInitBody();
          }
          errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
          do {
            out.write("\n\t\t\t\t");
            //  liferay-ui:message
            com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_8 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
            _jspx_th_liferay$1ui_message_8.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_message_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_10);
            _jspx_th_liferay$1ui_message_8.setArguments( LanguageUtil.formatStorageSize(fileMaxSize, locale) );
            _jspx_th_liferay$1ui_message_8.setKey("please-enter-a-file-with-a-valid-file-size-no-larger-than-x");
            _jspx_th_liferay$1ui_message_8.setTranslateArguments( false );
            int _jspx_eval_liferay$1ui_message_8 = _jspx_th_liferay$1ui_message_8.doStartTag();
            if (_jspx_th_liferay$1ui_message_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_8);
              return;
            }
            _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_8);
            out.write("\n\t\t\t");
            int evalDoAfterBody = _jspx_th_liferay$1ui_error_10.doAfterBody();
            errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_error_10 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_error_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_10);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_10);
        out.write("\n\n\t\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_11 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_exception.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_11.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_11.setException( UploadRequestSizeException.class );
        int _jspx_eval_liferay$1ui_error_11 = _jspx_th_liferay$1ui_error_11.doStartTag();
        if (_jspx_eval_liferay$1ui_error_11 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Object errorException = null;
          if (_jspx_eval_liferay$1ui_error_11 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_error_11.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_error_11.doInitBody();
          }
          errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
          do {
            out.write("\n\t\t\t\t");
            //  liferay-ui:message
            com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_9 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
            _jspx_th_liferay$1ui_message_9.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_message_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_11);
            _jspx_th_liferay$1ui_message_9.setArguments( LanguageUtil.formatStorageSize(dlEditFileEntryDisplayContext.getMaximumUploadRequestSize(), locale) );
            _jspx_th_liferay$1ui_message_9.setKey("request-is-larger-than-x-and-could-not-be-processed");
            _jspx_th_liferay$1ui_message_9.setTranslateArguments( false );
            int _jspx_eval_liferay$1ui_message_9 = _jspx_th_liferay$1ui_message_9.doStartTag();
            if (_jspx_th_liferay$1ui_message_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_9);
              return;
            }
            _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_9);
            out.write("\n\t\t\t");
            int evalDoAfterBody = _jspx_th_liferay$1ui_error_11.doAfterBody();
            errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_error_11 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_error_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_11);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_11);
        out.write("\n\n\t\t\t");
        if (_jspx_meth_liferay$1asset_asset$1categories$1error_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
          return;
        out.write("\n\n\t\t\t");
        if (_jspx_meth_liferay$1asset_asset$1tags$1error_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
          return;
        out.write("\n\n\t\t\t");
        //  aui:model-context
        com.liferay.taglib.aui.ModelContextTag _jspx_th_aui_model$1context_0 = (com.liferay.taglib.aui.ModelContextTag) _jspx_tagPool_aui_model$1context_model_bean_nobody.get(com.liferay.taglib.aui.ModelContextTag.class);
        _jspx_th_aui_model$1context_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_model$1context_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_model$1context_0.setBean( fileVersion );
        _jspx_th_aui_model$1context_0.setModel( DLFileVersion.class );
        int _jspx_eval_aui_model$1context_0 = _jspx_th_aui_model$1context_0.doStartTag();
        if (_jspx_th_aui_model$1context_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_model$1context_model_bean_nobody.reuse(_jspx_th_aui_model$1context_0);
          return;
        }
        _jspx_tagPool_aui_model$1context_model_bean_nobody.reuse(_jspx_th_aui_model$1context_0);
        out.write("\n\n\t\t\t");
        //  aui:fieldset-group
        com.liferay.taglib.aui.FieldsetGroupTag _jspx_th_aui_fieldset$1group_0 = (com.liferay.taglib.aui.FieldsetGroupTag) _jspx_tagPool_aui_fieldset$1group_markupView.get(com.liferay.taglib.aui.FieldsetGroupTag.class);
        _jspx_th_aui_fieldset$1group_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_fieldset$1group_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_fieldset$1group_0.setMarkupView("lexicon");
        int _jspx_eval_aui_fieldset$1group_0 = _jspx_th_aui_fieldset$1group_0.doStartTag();
        if (_jspx_eval_aui_fieldset$1group_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n\t\t\t\t");
          //  aui:fieldset
          com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_0 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset.get(com.liferay.taglib.aui.FieldsetTag.class);
          _jspx_th_aui_fieldset_0.setPageContext(_jspx_page_context);
          _jspx_th_aui_fieldset_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          int _jspx_eval_aui_fieldset_0 = _jspx_th_aui_fieldset_0.doStartTag();
          if (_jspx_eval_aui_fieldset_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n\t\t\t\t\t");
            //  c:if
            org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_4 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
            _jspx_th_c_if_4.setPageContext(_jspx_page_context);
            _jspx_th_c_if_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_0);
            _jspx_th_c_if_4.setTest( fileMaxSize != 0 );
            int _jspx_eval_c_if_4 = _jspx_th_c_if_4.doStartTag();
            if (_jspx_eval_c_if_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t\t\t\t<div class=\"alert alert-info\">\n\t\t\t\t\t\t\t");
                //  liferay-ui:message
                com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_10 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                _jspx_th_liferay$1ui_message_10.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_message_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_4);
                _jspx_th_liferay$1ui_message_10.setArguments( LanguageUtil.formatStorageSize(fileMaxSize, locale) );
                _jspx_th_liferay$1ui_message_10.setKey("upload-documents-no-larger-than-x");
                _jspx_th_liferay$1ui_message_10.setTranslateArguments( false );
                int _jspx_eval_liferay$1ui_message_10 = _jspx_th_liferay$1ui_message_10.doStartTag();
                if (_jspx_th_liferay$1ui_message_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_10);
                  return;
                }
                _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_10);
                out.write("\n\t\t\t\t\t\t</div>\n\t\t\t\t\t");
                int evalDoAfterBody = _jspx_th_c_if_4.doAfterBody();
                if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                  break;
              } while (true);
            }
            if (_jspx_th_c_if_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_4);
              return;
            }
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_4);
            out.write("\n\n\t\t\t\t\t");

					String folderName = StringPool.BLANK;

					if (folderId > 0) {
						folder = DLAppLocalServiceUtil.getFolder(folderId);

						folder = folder.toEscapedModel();

						folderId = folder.getFolderId();
						folderName = folder.getName();
					}
					else {
						folderName = LanguageUtil.get(request, "home");
					}
					
            out.write("\n\n\t\t\t\t\t<div class=\"form-group\">\n\t\t\t\t\t\t");
            //  c:if
            org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_5 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
            _jspx_th_c_if_5.setPageContext(_jspx_page_context);
            _jspx_th_c_if_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_0);
            _jspx_th_c_if_5.setTest( dlEditFileEntryDisplayContext.isFolderSelectionVisible() );
            int _jspx_eval_c_if_5 = _jspx_th_c_if_5.doStartTag();
            if (_jspx_eval_c_if_5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t\t\t\t\t");
                //  aui:input
                com.liferay.taglib.aui.InputTag _jspx_th_aui_input_9 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_label_nobody.get(com.liferay.taglib.aui.InputTag.class);
                _jspx_th_aui_input_9.setPageContext(_jspx_page_context);
                _jspx_th_aui_input_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
                _jspx_th_aui_input_9.setLabel("folder");
                _jspx_th_aui_input_9.setName("folderName");
                _jspx_th_aui_input_9.setType("resource");
                _jspx_th_aui_input_9.setValue( folderName );
                int _jspx_eval_aui_input_9 = _jspx_th_aui_input_9.doStartTag();
                if (_jspx_th_aui_input_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_input_value_type_name_label_nobody.reuse(_jspx_th_aui_input_9);
                  return;
                }
                _jspx_tagPool_aui_input_value_type_name_label_nobody.reuse(_jspx_th_aui_input_9);
                out.write("\n\n\t\t\t\t\t\t\t");
                if (_jspx_meth_aui_button_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_5, _jspx_page_context))
                  return;
                out.write("\n\n\t\t\t\t\t\t\t");

							String taglibRemoveFolder = "Liferay.Util.removeEntitySelection('folderId', 'folderName', this, '" + renderResponse.getNamespace() + "');";
							
                out.write("\n\n\t\t\t\t\t\t\t");
                //  aui:button
                com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_1 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_value_onClick_name_disabled_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
                _jspx_th_aui_button_1.setPageContext(_jspx_page_context);
                _jspx_th_aui_button_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
                _jspx_th_aui_button_1.setDisabled( folderId <= 0 );
                _jspx_th_aui_button_1.setName("removeFolderButton");
                _jspx_th_aui_button_1.setOnClick( taglibRemoveFolder );
                _jspx_th_aui_button_1.setValue("remove");
                int _jspx_eval_aui_button_1 = _jspx_th_aui_button_1.doStartTag();
                if (_jspx_th_aui_button_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_button_value_onClick_name_disabled_nobody.reuse(_jspx_th_aui_button_1);
                  return;
                }
                _jspx_tagPool_aui_button_value_onClick_name_disabled_nobody.reuse(_jspx_th_aui_button_1);
                out.write("\n\n\t\t\t\t\t\t\t<script>\n\t\t\t\t\t\t\t\tvar selectFolderButton = document.getElementById(\n\t\t\t\t\t\t\t\t\t'");
                if (_jspx_meth_portlet_namespace_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_5, _jspx_page_context))
                  return;
                out.write("selectFolderButton'\n\t\t\t\t\t\t\t\t);\n\n\t\t\t\t\t\t\t\tif (selectFolderButton) {\n\t\t\t\t\t\t\t\t\tselectFolderButton.addEventListener('click', function(event) {\n\t\t\t\t\t\t\t\t\t\tLiferay.Util.selectEntity(\n\t\t\t\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\t\t\tdialog: {\n\t\t\t\t\t\t\t\t\t\t\t\t\tconstrain: true,\n\t\t\t\t\t\t\t\t\t\t\t\t\tdestroyOnHide: true,\n\t\t\t\t\t\t\t\t\t\t\t\t\tmodal: true,\n\t\t\t\t\t\t\t\t\t\t\t\t\twidth: 680\n\t\t\t\t\t\t\t\t\t\t\t\t},\n\t\t\t\t\t\t\t\t\t\t\t\tid: '");
                if (_jspx_meth_portlet_namespace_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_5, _jspx_page_context))
                  return;
                out.write("selectFolder',\n\t\t\t\t\t\t\t\t\t\t\t\ttitle:\n\t\t\t\t\t\t\t\t\t\t\t\t\t'");
                if (_jspx_meth_liferay$1ui_message_11((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_5, _jspx_page_context))
                  return;
                out.write("',\n\n\t\t\t\t\t\t\t\t\t\t\t\t");
                //  liferay-portlet:renderURL
                com.liferay.taglib.portlet.RenderURLTag _jspx_th_liferay$1portlet_renderURL_0 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_liferay$1portlet_renderURL_windowState_var.get(com.liferay.taglib.portlet.RenderURLTag.class);
                _jspx_th_liferay$1portlet_renderURL_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1portlet_renderURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
                _jspx_th_liferay$1portlet_renderURL_0.setVar("selectFolderURL");
                _jspx_th_liferay$1portlet_renderURL_0.setWindowState( LiferayWindowState.POP_UP.toString() );
                int _jspx_eval_liferay$1portlet_renderURL_0 = _jspx_th_liferay$1portlet_renderURL_0.doStartTag();
                if (_jspx_eval_liferay$1portlet_renderURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t");
                  if (_jspx_meth_portlet_param_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1portlet_renderURL_0, _jspx_page_context))
                    return;
                  out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                }
                if (_jspx_th_liferay$1portlet_renderURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1portlet_renderURL_windowState_var.reuse(_jspx_th_liferay$1portlet_renderURL_0);
                  return;
                }
                _jspx_tagPool_liferay$1portlet_renderURL_windowState_var.reuse(_jspx_th_liferay$1portlet_renderURL_0);
                java.lang.String selectFolderURL = null;
                selectFolderURL = (java.lang.String) _jspx_page_context.findAttribute("selectFolderURL");
                out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t\turi: '");
                out.print( selectFolderURL.toString() );
                out.write("'\n\t\t\t\t\t\t\t\t\t\t\t},\n\t\t\t\t\t\t\t\t\t\t\tfunction(event) {\n\t\t\t\t\t\t\t\t\t\t\t\tvar folderData = {\n\t\t\t\t\t\t\t\t\t\t\t\t\tidString: 'folderId',\n\t\t\t\t\t\t\t\t\t\t\t\t\tidValue: event.folderid,\n\t\t\t\t\t\t\t\t\t\t\t\t\tnameString: 'folderName',\n\t\t\t\t\t\t\t\t\t\t\t\t\tnameValue: event.foldername\n\t\t\t\t\t\t\t\t\t\t\t\t};\n\n\t\t\t\t\t\t\t\t\t\t\t\tLiferay.Util.selectFolder(folderData, '");
                if (_jspx_meth_portlet_namespace_2((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_5, _jspx_page_context))
                  return;
                out.write("');\n\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t);\n\t\t\t\t\t\t\t\t\t});\n\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t</script>\n\t\t\t\t\t\t");
                int evalDoAfterBody = _jspx_th_c_if_5.doAfterBody();
                if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                  break;
              } while (true);
            }
            if (_jspx_th_c_if_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_5);
              return;
            }
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_5);
            out.write("\n\t\t\t\t\t</div>\n\n\t\t\t\t\t");
            out.write('\n');
            out.write('\n');

DLFilePicker dlFilePicker = dlEditFileEntryDisplayContext.getDLFilePicker("onFilePick");

            out.write('\n');
            out.write('\n');
            //  c:choose
            org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_2 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
            _jspx_th_c_choose_2.setPageContext(_jspx_page_context);
            _jspx_th_c_choose_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_0);
            int _jspx_eval_c_choose_2 = _jspx_th_c_choose_2.doStartTag();
            if (_jspx_eval_c_choose_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write('\n');
                out.write('	');
                //  c:when
                org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_2 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                _jspx_th_c_when_2.setPageContext(_jspx_page_context);
                _jspx_th_c_when_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_2);
                _jspx_th_c_when_2.setTest( dlFilePicker == null );
                int _jspx_eval_c_when_2 = _jspx_th_c_when_2.doStartTag();
                if (_jspx_eval_c_when_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t");
                    //  aui:input
                    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_10 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_onChange_name_autoFocus.get(com.liferay.taglib.aui.InputTag.class);
                    _jspx_th_aui_input_10.setPageContext(_jspx_page_context);
                    _jspx_th_aui_input_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_2);
                    _jspx_th_aui_input_10.setAutoFocus( windowState.equals(WindowState.MAXIMIZED) || windowState.equals(LiferayWindowState.POP_UP) );
                    _jspx_th_aui_input_10.setName("file");
                    _jspx_th_aui_input_10.setOnChange( renderResponse.getNamespace() + "updateTitle();" );
                    _jspx_th_aui_input_10.setType("file");
                    int _jspx_eval_aui_input_10 = _jspx_th_aui_input_10.doStartTag();
                    if (_jspx_eval_aui_input_10 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      out.write("\n\t\t\t");
                      //  aui:validator
                      com.liferay.taglib.aui.ValidatorTagImpl _jspx_th_aui_validator_0 = (com.liferay.taglib.aui.ValidatorTagImpl) _jspx_tagPool_aui_validator_name.get(com.liferay.taglib.aui.ValidatorTagImpl.class);
                      _jspx_th_aui_validator_0.setPageContext(_jspx_page_context);
                      _jspx_th_aui_validator_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_input_10);
                      _jspx_th_aui_validator_0.setName("acceptFiles");
                      int _jspx_eval_aui_validator_0 = _jspx_th_aui_validator_0.doStartTag();
                      if (_jspx_eval_aui_validator_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        if (_jspx_eval_aui_validator_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                          out = _jspx_page_context.pushBody();
                          _jspx_th_aui_validator_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                          _jspx_th_aui_validator_0.doInitBody();
                        }
                        do {
                          out.write("\n\t\t\t\t'");
                          out.print( StringUtil.merge(dlConfiguration.fileExtensions()) );
                          out.write("'\n\t\t\t");
                          int evalDoAfterBody = _jspx_th_aui_validator_0.doAfterBody();
                          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                            break;
                        } while (true);
                        if (_jspx_eval_aui_validator_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                          out = _jspx_page_context.popBody();
                      }
                      if (_jspx_th_aui_validator_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_aui_validator_name.reuse(_jspx_th_aui_validator_0);
                        return;
                      }
                      _jspx_tagPool_aui_validator_name.reuse(_jspx_th_aui_validator_0);
                      out.write("\n\n\t\t\t");
                      //  aui:validator
                      com.liferay.taglib.aui.ValidatorTagImpl _jspx_th_aui_validator_1 = (com.liferay.taglib.aui.ValidatorTagImpl) _jspx_tagPool_aui_validator_name.get(com.liferay.taglib.aui.ValidatorTagImpl.class);
                      _jspx_th_aui_validator_1.setPageContext(_jspx_page_context);
                      _jspx_th_aui_validator_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_input_10);
                      _jspx_th_aui_validator_1.setName("maxFileSize");
                      int _jspx_eval_aui_validator_1 = _jspx_th_aui_validator_1.doStartTag();
                      if (_jspx_eval_aui_validator_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        if (_jspx_eval_aui_validator_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                          out = _jspx_page_context.pushBody();
                          _jspx_th_aui_validator_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                          _jspx_th_aui_validator_1.doInitBody();
                        }
                        do {
                          out.print( dlEditFileEntryDisplayContext.getMaximumUploadSize() );
                          int evalDoAfterBody = _jspx_th_aui_validator_1.doAfterBody();
                          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                            break;
                        } while (true);
                        if (_jspx_eval_aui_validator_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                          out = _jspx_page_context.popBody();
                      }
                      if (_jspx_th_aui_validator_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_aui_validator_name.reuse(_jspx_th_aui_validator_1);
                        return;
                      }
                      _jspx_tagPool_aui_validator_name.reuse(_jspx_th_aui_validator_1);
                      out.write("\n\t\t");
                    }
                    if (_jspx_th_aui_input_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_input_type_onChange_name_autoFocus.reuse(_jspx_th_aui_input_10);
                      return;
                    }
                    _jspx_tagPool_aui_input_type_onChange_name_autoFocus.reuse(_jspx_th_aui_input_10);
                    out.write('\n');
                    out.write('	');
                    int evalDoAfterBody = _jspx_th_c_when_2.doAfterBody();
                    if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                      break;
                  } while (true);
                }
                if (_jspx_th_c_when_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_2);
                  return;
                }
                _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_2);
                out.write('\n');
                out.write('	');
                //  c:otherwise
                org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_2 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                _jspx_th_c_otherwise_2.setPageContext(_jspx_page_context);
                _jspx_th_c_otherwise_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_2);
                int _jspx_eval_c_otherwise_2 = _jspx_th_c_otherwise_2.doStartTag();
                if (_jspx_eval_c_otherwise_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t");
                    //  aui:field-wrapper
                    com.liferay.taglib.aui.FieldWrapperTag _jspx_th_aui_field$1wrapper_0 = (com.liferay.taglib.aui.FieldWrapperTag) _jspx_tagPool_aui_field$1wrapper_label.get(com.liferay.taglib.aui.FieldWrapperTag.class);
                    _jspx_th_aui_field$1wrapper_0.setPageContext(_jspx_page_context);
                    _jspx_th_aui_field$1wrapper_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_2);
                    _jspx_th_aui_field$1wrapper_0.setLabel("file");
                    int _jspx_eval_aui_field$1wrapper_0 = _jspx_th_aui_field$1wrapper_0.doStartTag();
                    if (_jspx_eval_aui_field$1wrapper_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      out.write("\n\t\t\t");
                      //  aui:button
                      com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_2 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_value_name_disabled_cssClass_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
                      _jspx_th_aui_button_2.setPageContext(_jspx_page_context);
                      _jspx_th_aui_button_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_field$1wrapper_0);
                      _jspx_th_aui_button_2.setCssClass("customized-file-button disabled");
                      _jspx_th_aui_button_2.setDisabled( true );
                      _jspx_th_aui_button_2.setName("file");
                      _jspx_th_aui_button_2.setValue("select-file");
                      int _jspx_eval_aui_button_2 = _jspx_th_aui_button_2.doStartTag();
                      if (_jspx_th_aui_button_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_aui_button_value_name_disabled_cssClass_nobody.reuse(_jspx_th_aui_button_2);
                        return;
                      }
                      _jspx_tagPool_aui_button_value_name_disabled_cssClass_nobody.reuse(_jspx_th_aui_button_2);
                      out.write("\n\n\t\t\t<img id=\"");
                      if (_jspx_meth_portlet_namespace_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_field$1wrapper_0, _jspx_page_context))
                        return;
                      out.write("pickButtonIcon\" />\n\n\t\t\t<span id=\"");
                      if (_jspx_meth_portlet_namespace_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_field$1wrapper_0, _jspx_page_context))
                        return;
                      out.write("pickButtonTitle\"></span>\n\t\t");
                    }
                    if (_jspx_th_aui_field$1wrapper_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_field$1wrapper_label.reuse(_jspx_th_aui_field$1wrapper_0);
                      return;
                    }
                    _jspx_tagPool_aui_field$1wrapper_label.reuse(_jspx_th_aui_field$1wrapper_0);
                    out.write("\n\n\t\t");
                    //  aui:script
                    com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_0 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script_sandbox.get(com.liferay.taglib.aui.ScriptTag.class);
                    _jspx_th_aui_script_0.setPageContext(_jspx_page_context);
                    _jspx_th_aui_script_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_2);
                    _jspx_th_aui_script_0.setSandbox( true );
                    int _jspx_eval_aui_script_0 = _jspx_th_aui_script_0.doStartTag();
                    if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                        out = _jspx_page_context.pushBody();
                        _jspx_th_aui_script_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                        _jspx_th_aui_script_0.doInitBody();
                      }
                      do {
                        out.write("\n\t\t\tvar onFilePick = function(fields) {\n\t\t\t\tvar documentIconURL = fields['");
                        out.print( dlFilePicker.getIconFieldName() );
                        out.write("'];\n\n\t\t\t\tvar pickButtonIcon = document.getElementById(\n\t\t\t\t\t'");
                        if (_jspx_meth_portlet_namespace_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
                          return;
                        out.write("pickButtonIcon'\n\t\t\t\t);\n\n\t\t\t\tif (pickButtonIcon) {\n\t\t\t\t\tpickButtonIcon.setAttribute('src', documentIconURL);\n\t\t\t\t}\n\n\t\t\t\tvar documentTitle = fields['");
                        out.print( dlFilePicker.getTitleFieldName() );
                        out.write("'];\n\n\t\t\t\tvar pickButtonTitleElement = document.getElementById(\n\t\t\t\t\t'");
                        if (_jspx_meth_portlet_namespace_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
                          return;
                        out.write("pickButtonTitle'\n\t\t\t\t);\n\n\t\t\t\tif (pickButtonTitleElement) {\n\t\t\t\t\tpickButtonTitleElement.innerHTML = documentTitle;\n\t\t\t\t}\n\n\t\t\t\tvar titleElement = document.getElementById('");
                        if (_jspx_meth_portlet_namespace_7((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
                          return;
                        out.write("title');\n\n\t\t\t\tif (titleElement) {\n\t\t\t\t\ttitleElement.value = documentTitle;\n\t\t\t\t}\n\n\t\t\t\tvar documentDescription =\n\t\t\t\t\tfields['");
                        out.print( dlFilePicker.getDescriptionFieldName() );
                        out.write("'];\n\n\t\t\t\tvar documentDescriptionElement = document.getElementById(\n\t\t\t\t\t'");
                        if (_jspx_meth_portlet_namespace_8((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
                          return;
                        out.write("documentDescription'\n\t\t\t\t);\n\n\t\t\t\tif (documentDescriptionElement) {\n\t\t\t\t\tdocumentDescriptionElement.value = documentDescription;\n\t\t\t\t}\n\n\t\t\t\tvar ddmFieldWrappers = document.querySelectorAll(\n\t\t\t\t\t'#");
                        if (_jspx_meth_portlet_namespace_9((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
                          return;
                        out.write("fm .lfr-ddm-container .field-wrapper'\n\t\t\t\t);\n\n\t\t\t\tArray.prototype.forEach.call(ddmFieldWrappers, function(ddmFieldWrapper) {\n\t\t\t\t\tvar fieldName = ddmFieldWrapper.dataset.fieldname;\n\n\t\t\t\t\tvar input = ddmFieldWrapper.querySelector('input');\n\n\t\t\t\t\tif (input) {\n\t\t\t\t\t\tinput.value = fields[fieldName];\n\t\t\t\t\t}\n\t\t\t\t});\n\t\t\t};\n\n\t\t\t");
                        out.print( dlFilePicker.getJavaScript() );
                        out.write(";\n\n\t\t\tvar customizedFileButton = document.getElementById('");
                        if (_jspx_meth_portlet_namespace_10((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
                          return;
                        out.write("file');\n\n\t\t\tif (customizedFileButton) {\n\t\t\t\tvar filePicker = new ");
                        out.print( dlFilePicker.getJavaScriptModuleName() );
                        out.write("(\n\t\t\t\t\tfunction() {\n\t\t\t\t\t\tLiferay.Util.toggleDisabled(customizedFileButton, false);\n\t\t\t\t\t}\n\t\t\t\t);\n\n\t\t\t\tcustomizedFileButton.addEventListener(\n\t\t\t\t\t'click',\n\t\t\t\t\tfilePicker['");
                        out.print( dlFilePicker.getOnClickCallback() );
                        out.write("'].bind(filePicker)\n\t\t\t\t);\n\t\t\t}\n\t\t");
                        int evalDoAfterBody = _jspx_th_aui_script_0.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                      if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                        out = _jspx_page_context.popBody();
                    }
                    if (_jspx_th_aui_script_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_script_sandbox.reuse(_jspx_th_aui_script_0);
                      return;
                    }
                    _jspx_tagPool_aui_script_sandbox.reuse(_jspx_th_aui_script_0);
                    out.write('\n');
                    out.write('	');
                    int evalDoAfterBody = _jspx_th_c_otherwise_2.doAfterBody();
                    if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                      break;
                  } while (true);
                }
                if (_jspx_th_c_otherwise_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_2);
                  return;
                }
                _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_2);
                out.write('\n');
                int evalDoAfterBody = _jspx_th_c_choose_2.doAfterBody();
                if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                  break;
              } while (true);
            }
            if (_jspx_th_c_choose_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_2);
              return;
            }
            _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_2);
            out.write("\n\n\t\t\t\t\t");
            if (_jspx_meth_aui_input_11((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_fieldset_0, _jspx_page_context))
              return;
            out.write("\n\n\t\t\t\t\t");
            //  c:if
            org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_6 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
            _jspx_th_c_if_6.setPageContext(_jspx_page_context);
            _jspx_th_c_if_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_0);
            _jspx_th_c_if_6.setTest( (folder == null) || folder.isSupportsMetadata() );
            int _jspx_eval_c_if_6 = _jspx_th_c_if_6.doStartTag();
            if (_jspx_eval_c_if_6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t\t\t\t");
                if (_jspx_meth_aui_input_12((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_6, _jspx_page_context))
                  return;
                out.write("\n\n\t\t\t\t\t\t");
                //  c:if
                org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_7 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                _jspx_th_c_if_7.setPageContext(_jspx_page_context);
                _jspx_th_c_if_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_6);
                _jspx_th_c_if_7.setTest( (folder == null) || (folder.getModel() instanceof DLFolder) );
                int _jspx_eval_c_if_7 = _jspx_th_c_if_7.doStartTag();
                if (_jspx_eval_c_if_7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\n\t\t\t\t\t\t\t");

							boolean inherited = false;

							if (folder != null) {
								DLFolder dlFolder = (DLFolder)folder.getModel();

								if (dlFolder.getRestrictionType() == DLFolderConstants.RESTRICTION_TYPE_INHERIT) {
									inherited = true;
								}
							}

							List<DLFileEntryType> dlFileEntryTypes = DLFileEntryTypeLocalServiceUtil.getFolderFileEntryTypes(PortalUtil.getCurrentAndAncestorSiteGroupIds(scopeGroupId), folderId, inherited);
							
                    out.write("\n\n\t\t\t\t\t\t\t");
                    //  c:choose
                    org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_3 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                    _jspx_th_c_choose_3.setPageContext(_jspx_page_context);
                    _jspx_th_c_choose_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                    int _jspx_eval_c_choose_3 = _jspx_th_c_choose_3.doStartTag();
                    if (_jspx_eval_c_choose_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      do {
                        out.write("\n\t\t\t\t\t\t\t\t");
                        //  c:when
                        org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_3 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                        _jspx_th_c_when_3.setPageContext(_jspx_page_context);
                        _jspx_th_c_when_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_3);
                        _jspx_th_c_when_3.setTest( !cmd.equals(Constants.ADD) && (dlFileEntryTypes.size() > 1) );
                        int _jspx_eval_c_when_3 = _jspx_th_c_when_3.doStartTag();
                        if (_jspx_eval_c_when_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\t\t\t\t\t\t\t\t\t");
                            //  aui:select
                            com.liferay.taglib.aui.SelectTag _jspx_th_aui_select_0 = (com.liferay.taglib.aui.SelectTag) _jspx_tagPool_aui_select_onChange_name_label_changesContext.get(com.liferay.taglib.aui.SelectTag.class);
                            _jspx_th_aui_select_0.setPageContext(_jspx_page_context);
                            _jspx_th_aui_select_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_3);
                            _jspx_th_aui_select_0.setChangesContext( true );
                            _jspx_th_aui_select_0.setLabel("document-type");
                            _jspx_th_aui_select_0.setName("fileEntryTypeId");
                            _jspx_th_aui_select_0.setOnChange( renderResponse.getNamespace() + "changeFileEntryType();" );
                            int _jspx_eval_aui_select_0 = _jspx_th_aui_select_0.doStartTag();
                            if (_jspx_eval_aui_select_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_aui_select_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_aui_select_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_aui_select_0.doInitBody();
                              }
                              do {
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t");

										for (DLFileEntryType curDLFileEntryType : dlFileEntryTypes) {
											if ((curDLFileEntryType.getFileEntryTypeId() == DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) || (fileEntryTypeId == curDLFileEntryType.getFileEntryTypeId()) || DLFileEntryTypePermission.contains(permissionChecker, curDLFileEntryType, ActionKeys.VIEW)) {
										
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t");
                              //  aui:option
                              com.liferay.taglib.aui.OptionTag _jspx_th_aui_option_0 = (com.liferay.taglib.aui.OptionTag) _jspx_tagPool_aui_option_value_selected_label_nobody.get(com.liferay.taglib.aui.OptionTag.class);
                              _jspx_th_aui_option_0.setPageContext(_jspx_page_context);
                              _jspx_th_aui_option_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_select_0);
                              _jspx_th_aui_option_0.setLabel( HtmlUtil.escape(curDLFileEntryType.getName(locale)) );
                              _jspx_th_aui_option_0.setSelected( fileEntryTypeId == curDLFileEntryType.getPrimaryKey() );
                              _jspx_th_aui_option_0.setValue( curDLFileEntryType.getPrimaryKey() );
                              int _jspx_eval_aui_option_0 = _jspx_th_aui_option_0.doStartTag();
                              if (_jspx_th_aui_option_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_option_value_selected_label_nobody.reuse(_jspx_th_aui_option_0);
                              return;
                              }
                              _jspx_tagPool_aui_option_value_selected_label_nobody.reuse(_jspx_th_aui_option_0);
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t");

											}
										}
										
                              out.write("\n\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_aui_select_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_aui_select_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                            }
                            if (_jspx_th_aui_select_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_select_onChange_name_label_changesContext.reuse(_jspx_th_aui_select_0);
                              return;
                            }
                            _jspx_tagPool_aui_select_onChange_name_label_changesContext.reuse(_jspx_th_aui_select_0);
                            out.write("\n\t\t\t\t\t\t\t\t");
                            int evalDoAfterBody = _jspx_th_c_when_3.doAfterBody();
                            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                          } while (true);
                        }
                        if (_jspx_th_c_when_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_3);
                          return;
                        }
                        _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_3);
                        out.write("\n\t\t\t\t\t\t\t\t");
                        //  c:otherwise
                        org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_3 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                        _jspx_th_c_otherwise_3.setPageContext(_jspx_page_context);
                        _jspx_th_c_otherwise_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_3);
                        int _jspx_eval_c_otherwise_3 = _jspx_th_c_otherwise_3.doStartTag();
                        if (_jspx_eval_c_otherwise_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\t\t\t\t\t\t\t\t\t");
                            //  aui:input
                            com.liferay.taglib.aui.InputTag _jspx_th_aui_input_13 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                            _jspx_th_aui_input_13.setPageContext(_jspx_page_context);
                            _jspx_th_aui_input_13.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_3);
                            _jspx_th_aui_input_13.setName("fileEntryTypeId");
                            _jspx_th_aui_input_13.setType("hidden");
                            _jspx_th_aui_input_13.setValue( fileEntryTypeId );
                            int _jspx_eval_aui_input_13 = _jspx_th_aui_input_13.doStartTag();
                            if (_jspx_th_aui_input_13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_13);
                              return;
                            }
                            _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_13);
                            out.write("\n\t\t\t\t\t\t\t\t");
                            int evalDoAfterBody = _jspx_th_c_otherwise_3.doAfterBody();
                            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                          } while (true);
                        }
                        if (_jspx_th_c_otherwise_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_3);
                          return;
                        }
                        _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_3);
                        out.write("\n\t\t\t\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_c_choose_3.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                    }
                    if (_jspx_th_c_choose_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_3);
                      return;
                    }
                    _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_3);
                    out.write("\n\n\t\t\t\t\t\t\t");
                    //  aui:input
                    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_14 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                    _jspx_th_aui_input_14.setPageContext(_jspx_page_context);
                    _jspx_th_aui_input_14.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                    _jspx_th_aui_input_14.setName("defaultLanguageId");
                    _jspx_th_aui_input_14.setType("hidden");
                    _jspx_th_aui_input_14.setValue( defaultLanguageId );
                    int _jspx_eval_aui_input_14 = _jspx_th_aui_input_14.doStartTag();
                    if (_jspx_th_aui_input_14.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_14);
                      return;
                    }
                    _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_14);
                    out.write("\n\n\t\t\t\t\t\t\t");

							if (fileEntryTypeId > 0) {
								try {
									boolean localizable = true;

									List<DDMStructure> ddmStructures = dlFileEntryType.getDDMStructures();

									for (DDMStructure ddmStructure : ddmStructures) {
										com.liferay.dynamic.data.mapping.storage.DDMFormValues ddmFormValues = null;

										try {
											DLFileEntryMetadata fileEntryMetadata = DLFileEntryMetadataLocalServiceUtil.getFileEntryMetadata(ddmStructure.getStructureId(), fileVersionId);

											ddmFormValues = dlEditFileEntryDisplayContext.getDDMFormValues(fileEntryMetadata.getDDMStorageId());
										}
										catch (Exception e) {
										}
							
                    out.write("\n\n\t\t\t\t\t\t\t\t\t\t");
                    //  c:if
                    org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_8 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                    _jspx_th_c_if_8.setPageContext(_jspx_page_context);
                    _jspx_th_c_if_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                    _jspx_th_c_if_8.setTest( !dlEditFileEntryDisplayContext.isDDMStructureVisible(ddmStructure) );
                    int _jspx_eval_c_if_8 = _jspx_th_c_if_8.doStartTag();
                    if (_jspx_eval_c_if_8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      do {
                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t<div class=\"hide\">\n\t\t\t\t\t\t\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_c_if_8.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                    }
                    if (_jspx_th_c_if_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_8);
                      return;
                    }
                    _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_8);
                    out.write("\n\n\t\t\t\t\t\t\t\t\t\t");
                    //  liferay-ddm:html
                    com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag _jspx_th_liferay$1ddm_html_0 = (com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag) _jspx_tagPool_liferay$1ddm_html_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody.get(com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag.class);
                    _jspx_th_liferay$1ddm_html_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ddm_html_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                    _jspx_th_liferay$1ddm_html_0.setClassNameId( PortalUtil.getClassNameId(com.liferay.dynamic.data.mapping.model.DDMStructure.class) );
                    _jspx_th_liferay$1ddm_html_0.setClassPK( ddmStructure.getPrimaryKey() );
                    _jspx_th_liferay$1ddm_html_0.setDdmFormValues( ddmFormValues );
                    _jspx_th_liferay$1ddm_html_0.setDefaultEditLocale( LocaleUtil.fromLanguageId(defaultLanguageId) );
                    _jspx_th_liferay$1ddm_html_0.setFieldsNamespace( String.valueOf(ddmStructure.getPrimaryKey()) );
                    _jspx_th_liferay$1ddm_html_0.setGroupId( (fileEntry != null) ? fileEntry.getGroupId() : 0 );
                    _jspx_th_liferay$1ddm_html_0.setLocalizable( localizable );
                    _jspx_th_liferay$1ddm_html_0.setRequestedLocale( locale );
                    int _jspx_eval_liferay$1ddm_html_0 = _jspx_th_liferay$1ddm_html_0.doStartTag();
                    if (_jspx_th_liferay$1ddm_html_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ddm_html_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1ddm_html_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1ddm_html_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1ddm_html_0);
                    out.write("\n\n\t\t\t\t\t\t\t\t\t\t");
                    //  c:if
                    org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_9 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                    _jspx_th_c_if_9.setPageContext(_jspx_page_context);
                    _jspx_th_c_if_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                    _jspx_th_c_if_9.setTest( !dlEditFileEntryDisplayContext.isDDMStructureVisible(ddmStructure) );
                    int _jspx_eval_c_if_9 = _jspx_th_c_if_9.doStartTag();
                    if (_jspx_eval_c_if_9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      do {
                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_c_if_9.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                    }
                    if (_jspx_th_c_if_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_9);
                      return;
                    }
                    _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_9);
                    out.write("\n\n\t\t\t\t\t\t\t");

										localizable = false;
									}
								}
								catch (Exception e) {
									_log.error(e, e);
								}
							}
							
                    out.write("\n\n\t\t\t\t\t\t");
                    int evalDoAfterBody = _jspx_th_c_if_7.doAfterBody();
                    if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                      break;
                  } while (true);
                }
                if (_jspx_th_c_if_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_7);
                  return;
                }
                _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_7);
                out.write("\n\t\t\t\t\t");
                int evalDoAfterBody = _jspx_th_c_if_6.doAfterBody();
                if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                  break;
              } while (true);
            }
            if (_jspx_th_c_if_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_6);
              return;
            }
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_6);
            out.write("\n\t\t\t\t");
          }
          if (_jspx_th_aui_fieldset_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_aui_fieldset.reuse(_jspx_th_aui_fieldset_0);
            return;
          }
          _jspx_tagPool_aui_fieldset.reuse(_jspx_th_aui_fieldset_0);
          out.write("\n\n\t\t\t\t");
          //  c:choose
          org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_4 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
          _jspx_th_c_choose_4.setPageContext(_jspx_page_context);
          _jspx_th_c_choose_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          int _jspx_eval_c_choose_4 = _jspx_th_c_choose_4.doStartTag();
          if (_jspx_eval_c_choose_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t\t");
              //  c:when
              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_4 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
              _jspx_th_c_when_4.setPageContext(_jspx_page_context);
              _jspx_th_c_when_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_4);
              _jspx_th_c_when_4.setTest( (fileEntry != null) && !checkedOut && dlAdminDisplayContext.isVersioningStrategyOverridable() );
              int _jspx_eval_c_when_4 = _jspx_th_c_when_4.doStartTag();
              if (_jspx_eval_c_when_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t\t");
                  //  aui:fieldset
                  com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_1 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.get(com.liferay.taglib.aui.FieldsetTag.class);
                  _jspx_th_aui_fieldset_1.setPageContext(_jspx_page_context);
                  _jspx_th_aui_fieldset_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_4);
                  _jspx_th_aui_fieldset_1.setCollapsed( true );
                  _jspx_th_aui_fieldset_1.setCollapsible( true );
                  _jspx_th_aui_fieldset_1.setLabel("versioning");
                  int _jspx_eval_aui_fieldset_1 = _jspx_th_aui_fieldset_1.doStartTag();
                  if (_jspx_eval_aui_fieldset_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    out.write("\n\t\t\t\t\t\t\t");
                    //  aui:input
                    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_15 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_label_nobody.get(com.liferay.taglib.aui.InputTag.class);
                    _jspx_th_aui_input_15.setPageContext(_jspx_page_context);
                    _jspx_th_aui_input_15.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
                    _jspx_th_aui_input_15.setLabel("customize-the-version-number-increment-and-describe-my-changes");
                    _jspx_th_aui_input_15.setName("updateVersionDetails");
                    _jspx_th_aui_input_15.setType("toggle-switch");
                    _jspx_th_aui_input_15.setValue( updateVersionDetails );
                    int _jspx_eval_aui_input_15 = _jspx_th_aui_input_15.doStartTag();
                    if (_jspx_th_aui_input_15.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_input_value_type_name_label_nobody.reuse(_jspx_th_aui_input_15);
                      return;
                    }
                    _jspx_tagPool_aui_input_value_type_name_label_nobody.reuse(_jspx_th_aui_input_15);
                    out.write("\n\n\t\t\t\t\t\t\t<div class=\"");
                    out.print( updateVersionDetails ? StringPool.BLANK : "hide" );
                    out.write("\" id=\"");
                    if (_jspx_meth_portlet_namespace_11((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_fieldset_1, _jspx_page_context))
                      return;
                    out.write("versionDetails\">\n\t\t\t\t\t\t\t\t");
                    //  aui:input
                    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_16 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.get(com.liferay.taglib.aui.InputTag.class);
                    _jspx_th_aui_input_16.setPageContext(_jspx_page_context);
                    _jspx_th_aui_input_16.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
                    _jspx_th_aui_input_16.setChecked( dlVersionNumberIncrease == DLVersionNumberIncrease.MAJOR );
                    _jspx_th_aui_input_16.setLabel("major-version");
                    _jspx_th_aui_input_16.setName("versionIncrease");
                    _jspx_th_aui_input_16.setType("radio");
                    _jspx_th_aui_input_16.setValue( DLVersionNumberIncrease.MAJOR );
                    int _jspx_eval_aui_input_16 = _jspx_th_aui_input_16.doStartTag();
                    if (_jspx_th_aui_input_16.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.reuse(_jspx_th_aui_input_16);
                      return;
                    }
                    _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.reuse(_jspx_th_aui_input_16);
                    out.write("\n\n\t\t\t\t\t\t\t\t");
                    //  aui:input
                    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_17 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.get(com.liferay.taglib.aui.InputTag.class);
                    _jspx_th_aui_input_17.setPageContext(_jspx_page_context);
                    _jspx_th_aui_input_17.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
                    _jspx_th_aui_input_17.setChecked( dlVersionNumberIncrease == DLVersionNumberIncrease.MINOR );
                    _jspx_th_aui_input_17.setLabel("minor-version");
                    _jspx_th_aui_input_17.setName("versionIncrease");
                    _jspx_th_aui_input_17.setType("radio");
                    _jspx_th_aui_input_17.setValue( DLVersionNumberIncrease.MINOR );
                    int _jspx_eval_aui_input_17 = _jspx_th_aui_input_17.doStartTag();
                    if (_jspx_th_aui_input_17.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.reuse(_jspx_th_aui_input_17);
                      return;
                    }
                    _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.reuse(_jspx_th_aui_input_17);
                    out.write("\n\n\t\t\t\t\t\t\t\t");
                    //  aui:input
                    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_18 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.get(com.liferay.taglib.aui.InputTag.class);
                    _jspx_th_aui_input_18.setPageContext(_jspx_page_context);
                    _jspx_th_aui_input_18.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
                    _jspx_th_aui_input_18.setChecked( (dlVersionNumberIncrease == DLVersionNumberIncrease.AUTOMATIC) || (dlVersionNumberIncrease == DLVersionNumberIncrease.NONE) );
                    _jspx_th_aui_input_18.setLabel("keep-current-version-number");
                    _jspx_th_aui_input_18.setName("versionIncrease");
                    _jspx_th_aui_input_18.setType("radio");
                    _jspx_th_aui_input_18.setValue( DLVersionNumberIncrease.NONE );
                    int _jspx_eval_aui_input_18 = _jspx_th_aui_input_18.doStartTag();
                    if (_jspx_th_aui_input_18.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.reuse(_jspx_th_aui_input_18);
                      return;
                    }
                    _jspx_tagPool_aui_input_value_type_name_label_checked_nobody.reuse(_jspx_th_aui_input_18);
                    out.write("\n\n\t\t\t\t\t\t\t\t");
                    if (_jspx_meth_aui_model$1context_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_fieldset_1, _jspx_page_context))
                      return;
                    out.write("\n\n\t\t\t\t\t\t\t\t");
                    if (_jspx_meth_aui_input_19((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_fieldset_1, _jspx_page_context))
                      return;
                    out.write("\n\n\t\t\t\t\t\t\t\t");
                    //  aui:model-context
                    com.liferay.taglib.aui.ModelContextTag _jspx_th_aui_model$1context_2 = (com.liferay.taglib.aui.ModelContextTag) _jspx_tagPool_aui_model$1context_model_bean_nobody.get(com.liferay.taglib.aui.ModelContextTag.class);
                    _jspx_th_aui_model$1context_2.setPageContext(_jspx_page_context);
                    _jspx_th_aui_model$1context_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
                    _jspx_th_aui_model$1context_2.setBean( fileVersion );
                    _jspx_th_aui_model$1context_2.setModel( DLFileVersion.class );
                    int _jspx_eval_aui_model$1context_2 = _jspx_th_aui_model$1context_2.doStartTag();
                    if (_jspx_th_aui_model$1context_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_model$1context_model_bean_nobody.reuse(_jspx_th_aui_model$1context_2);
                      return;
                    }
                    _jspx_tagPool_aui_model$1context_model_bean_nobody.reuse(_jspx_th_aui_model$1context_2);
                    out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                  }
                  if (_jspx_th_aui_fieldset_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_1);
                    return;
                  }
                  _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_1);
                  out.write("\n\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_c_when_4.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_c_when_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_4);
                return;
              }
              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_4);
              out.write("\n\t\t\t\t\t");
              //  c:otherwise
              org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_4 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
              _jspx_th_c_otherwise_4.setPageContext(_jspx_page_context);
              _jspx_th_c_otherwise_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_4);
              int _jspx_eval_c_otherwise_4 = _jspx_th_c_otherwise_4.doStartTag();
              if (_jspx_eval_c_otherwise_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t\t");
                  //  aui:input
                  com.liferay.taglib.aui.InputTag _jspx_th_aui_input_20 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                  _jspx_th_aui_input_20.setPageContext(_jspx_page_context);
                  _jspx_th_aui_input_20.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_4);
                  _jspx_th_aui_input_20.setName("updateVersionDetails");
                  _jspx_th_aui_input_20.setType("hidden");
                  _jspx_th_aui_input_20.setValue( false );
                  int _jspx_eval_aui_input_20 = _jspx_th_aui_input_20.doStartTag();
                  if (_jspx_th_aui_input_20.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_20);
                    return;
                  }
                  _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_20);
                  out.write("\n\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_c_otherwise_4.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_c_otherwise_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_4);
                return;
              }
              _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_4);
              out.write("\n\t\t\t\t");
              int evalDoAfterBody = _jspx_th_c_choose_4.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_choose_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_4);
            return;
          }
          _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_4);
          out.write("\n\n\t\t\t\t");

				Group scopeGroup = themeDisplay.getScopeGroup();
				
          out.write("\n\n\t\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_10 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_10.setPageContext(_jspx_page_context);
          _jspx_th_c_if_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          _jspx_th_c_if_10.setTest( !scopeGroup.isCompany() );
          int _jspx_eval_c_if_10 = _jspx_th_c_if_10.doStartTag();
          if (_jspx_eval_c_if_10 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t\t");
              //  aui:fieldset
              com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_2 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.get(com.liferay.taglib.aui.FieldsetTag.class);
              _jspx_th_aui_fieldset_2.setPageContext(_jspx_page_context);
              _jspx_th_aui_fieldset_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_10);
              _jspx_th_aui_fieldset_2.setCollapsed( true );
              _jspx_th_aui_fieldset_2.setCollapsible( true );
              _jspx_th_aui_fieldset_2.setLabel("display-page-template");
              int _jspx_eval_aui_fieldset_2 = _jspx_th_aui_fieldset_2.doStartTag();
              if (_jspx_eval_aui_fieldset_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t\t");
                //  liferay-asset:select-asset-display-page
                com.liferay.asset.taglib.servlet.taglib.SelectAssetDisplayPageTag _jspx_th_liferay$1asset_select$1asset$1display$1page_0 = (com.liferay.asset.taglib.servlet.taglib.SelectAssetDisplayPageTag) _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classTypeId_classPK_classNameId_nobody.get(com.liferay.asset.taglib.servlet.taglib.SelectAssetDisplayPageTag.class);
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_2);
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setClassNameId( PortalUtil.getClassNameId(DLFileEntry.class) );
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setClassPK( (fileEntry != null) ? fileEntry.getFileEntryId() : 0 );
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setClassTypeId( (fileEntryTypeId < 0) ? DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT : fileEntryTypeId );
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setGroupId( scopeGroupId );
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setShowPortletLayouts( true );
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setShowViewInContextLink( true );
                int _jspx_eval_liferay$1asset_select$1asset$1display$1page_0 = _jspx_th_liferay$1asset_select$1asset$1display$1page_0.doStartTag();
                if (_jspx_th_liferay$1asset_select$1asset$1display$1page_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classTypeId_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1asset_select$1asset$1display$1page_0);
                  return;
                }
                _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classTypeId_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1asset_select$1asset$1display$1page_0);
                out.write("\n\t\t\t\t\t");
              }
              if (_jspx_th_aui_fieldset_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_2);
                return;
              }
              _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_2);
              out.write("\n\t\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_10.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_10);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_10);
          out.write("\n\n\t\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_11 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_11.setPageContext(_jspx_page_context);
          _jspx_th_c_if_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          _jspx_th_c_if_11.setTest( (folder == null) || folder.isSupportsMetadata() );
          int _jspx_eval_c_if_11 = _jspx_th_c_if_11.doStartTag();
          if (_jspx_eval_c_if_11 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t\t");
              //  liferay-expando:custom-attributes-available
              com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag _jspx_th_liferay$1expando_custom$1attributes$1available_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag) _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag.class);
              _jspx_th_liferay$1expando_custom$1attributes$1available_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1expando_custom$1attributes$1available_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_11);
              _jspx_th_liferay$1expando_custom$1attributes$1available_0.setClassName( DLFileEntryConstants.getClassName() );
              int _jspx_eval_liferay$1expando_custom$1attributes$1available_0 = _jspx_th_liferay$1expando_custom$1attributes$1available_0.doStartTag();
              if (_jspx_eval_liferay$1expando_custom$1attributes$1available_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t\t");
                //  aui:fieldset
                com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_3 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.get(com.liferay.taglib.aui.FieldsetTag.class);
                _jspx_th_aui_fieldset_3.setPageContext(_jspx_page_context);
                _jspx_th_aui_fieldset_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1expando_custom$1attributes$1available_0);
                _jspx_th_aui_fieldset_3.setCollapsed( true );
                _jspx_th_aui_fieldset_3.setCollapsible( true );
                _jspx_th_aui_fieldset_3.setLabel("custom-fields");
                int _jspx_eval_aui_fieldset_3 = _jspx_th_aui_fieldset_3.doStartTag();
                if (_jspx_eval_aui_fieldset_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n\t\t\t\t\t\t\t");
                  //  liferay-expando:custom-attribute-list
                  com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag _jspx_th_liferay$1expando_custom$1attribute$1list_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag) _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag.class);
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_3);
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setClassName( DLFileEntryConstants.getClassName() );
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setClassPK( fileVersionId );
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setEditable( true );
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setLabel( true );
                  int _jspx_eval_liferay$1expando_custom$1attribute$1list_0 = _jspx_th_liferay$1expando_custom$1attribute$1list_0.doStartTag();
                  if (_jspx_th_liferay$1expando_custom$1attribute$1list_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.reuse(_jspx_th_liferay$1expando_custom$1attribute$1list_0);
                    return;
                  }
                  _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.reuse(_jspx_th_liferay$1expando_custom$1attribute$1list_0);
                  out.write("\n\t\t\t\t\t\t");
                }
                if (_jspx_th_aui_fieldset_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_3);
                  return;
                }
                _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_3);
                out.write("\n\t\t\t\t\t");
              }
              if (_jspx_th_liferay$1expando_custom$1attributes$1available_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
                return;
              }
              _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
              out.write("\n\t\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_11.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_11);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_11);
          out.write("\n\n\t\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_12 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_12.setPageContext(_jspx_page_context);
          _jspx_th_c_if_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          _jspx_th_c_if_12.setTest( (folder == null) || folder.isSupportsSocial() );
          int _jspx_eval_c_if_12 = _jspx_th_c_if_12.doStartTag();
          if (_jspx_eval_c_if_12 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t\t");
              //  aui:fieldset
              com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_4 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.get(com.liferay.taglib.aui.FieldsetTag.class);
              _jspx_th_aui_fieldset_4.setPageContext(_jspx_page_context);
              _jspx_th_aui_fieldset_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_12);
              _jspx_th_aui_fieldset_4.setCollapsed( true );
              _jspx_th_aui_fieldset_4.setCollapsible( true );
              _jspx_th_aui_fieldset_4.setLabel("categorization");
              int _jspx_eval_aui_fieldset_4 = _jspx_th_aui_fieldset_4.doStartTag();
              if (_jspx_eval_aui_fieldset_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t\t");
                //  liferay-asset:asset-categories-selector
                com.liferay.asset.taglib.servlet.taglib.soy.AssetCategoriesSelectorTag _jspx_th_liferay$1asset_asset$1categories$1selector_0 = (com.liferay.asset.taglib.servlet.taglib.soy.AssetCategoriesSelectorTag) _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody.get(com.liferay.asset.taglib.servlet.taglib.soy.AssetCategoriesSelectorTag.class);
                _jspx_th_liferay$1asset_asset$1categories$1selector_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1asset_asset$1categories$1selector_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_4);
                _jspx_th_liferay$1asset_asset$1categories$1selector_0.setClassName( DLFileEntry.class.getName() );
                _jspx_th_liferay$1asset_asset$1categories$1selector_0.setClassPK( assetClassPK );
                _jspx_th_liferay$1asset_asset$1categories$1selector_0.setClassTypePK( (fileEntryTypeId < 0) ? DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT : fileEntryTypeId );
                int _jspx_eval_liferay$1asset_asset$1categories$1selector_0 = _jspx_th_liferay$1asset_asset$1categories$1selector_0.doStartTag();
                if (_jspx_th_liferay$1asset_asset$1categories$1selector_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1categories$1selector_0);
                  return;
                }
                _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1categories$1selector_0);
                out.write("\n\n\t\t\t\t\t\t");
                //  liferay-asset:asset-tags-selector
                com.liferay.asset.taglib.servlet.taglib.soy.AssetTagsSelectorTag _jspx_th_liferay$1asset_asset$1tags$1selector_0 = (com.liferay.asset.taglib.servlet.taglib.soy.AssetTagsSelectorTag) _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody.get(com.liferay.asset.taglib.servlet.taglib.soy.AssetTagsSelectorTag.class);
                _jspx_th_liferay$1asset_asset$1tags$1selector_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1asset_asset$1tags$1selector_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_4);
                _jspx_th_liferay$1asset_asset$1tags$1selector_0.setClassName( DLFileEntry.class.getName() );
                _jspx_th_liferay$1asset_asset$1tags$1selector_0.setClassPK( assetClassPK );
                int _jspx_eval_liferay$1asset_asset$1tags$1selector_0 = _jspx_th_liferay$1asset_asset$1tags$1selector_0.doStartTag();
                if (_jspx_th_liferay$1asset_asset$1tags$1selector_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1tags$1selector_0);
                  return;
                }
                _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1tags$1selector_0);
                out.write("\n\t\t\t\t\t");
              }
              if (_jspx_th_aui_fieldset_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_4);
                return;
              }
              _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_4);
              out.write("\n\n\t\t\t\t\t");
              //  aui:fieldset
              com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_5 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.get(com.liferay.taglib.aui.FieldsetTag.class);
              _jspx_th_aui_fieldset_5.setPageContext(_jspx_page_context);
              _jspx_th_aui_fieldset_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_12);
              _jspx_th_aui_fieldset_5.setCollapsed( true );
              _jspx_th_aui_fieldset_5.setCollapsible( true );
              _jspx_th_aui_fieldset_5.setLabel("related-assets");
              int _jspx_eval_aui_fieldset_5 = _jspx_th_aui_fieldset_5.doStartTag();
              if (_jspx_eval_aui_fieldset_5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t\t");
                //  liferay-asset:input-asset-links
                com.liferay.asset.taglib.servlet.taglib.InputAssetLinksTag _jspx_th_liferay$1asset_input$1asset$1links_0 = (com.liferay.asset.taglib.servlet.taglib.InputAssetLinksTag) _jspx_tagPool_liferay$1asset_input$1asset$1links_classPK_className_nobody.get(com.liferay.asset.taglib.servlet.taglib.InputAssetLinksTag.class);
                _jspx_th_liferay$1asset_input$1asset$1links_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1asset_input$1asset$1links_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_5);
                _jspx_th_liferay$1asset_input$1asset$1links_0.setClassName( DLFileEntry.class.getName() );
                _jspx_th_liferay$1asset_input$1asset$1links_0.setClassPK( assetClassPK );
                int _jspx_eval_liferay$1asset_input$1asset$1links_0 = _jspx_th_liferay$1asset_input$1asset$1links_0.doStartTag();
                if (_jspx_th_liferay$1asset_input$1asset$1links_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1asset_input$1asset$1links_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_input$1asset$1links_0);
                  return;
                }
                _jspx_tagPool_liferay$1asset_input$1asset$1links_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_input$1asset$1links_0);
                out.write("\n\t\t\t\t\t");
              }
              if (_jspx_th_aui_fieldset_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_5);
                return;
              }
              _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_5);
              out.write("\n\t\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_12.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_12);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_12);
          out.write("\n\n\t\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_13 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_13.setPageContext(_jspx_page_context);
          _jspx_th_c_if_13.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          _jspx_th_c_if_13.setTest( (fileEntry == null) && dlEditFileEntryDisplayContext.isPermissionsVisible() );
          int _jspx_eval_c_if_13 = _jspx_th_c_if_13.doStartTag();
          if (_jspx_eval_c_if_13 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t\t");
              //  aui:fieldset
              com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_6 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.get(com.liferay.taglib.aui.FieldsetTag.class);
              _jspx_th_aui_fieldset_6.setPageContext(_jspx_page_context);
              _jspx_th_aui_fieldset_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_13);
              _jspx_th_aui_fieldset_6.setCollapsed( true );
              _jspx_th_aui_fieldset_6.setCollapsible( true );
              _jspx_th_aui_fieldset_6.setLabel("permissions");
              int _jspx_eval_aui_fieldset_6 = _jspx_th_aui_fieldset_6.doStartTag();
              if (_jspx_eval_aui_fieldset_6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t\t");
                //  liferay-ui:input-permissions
                com.liferay.taglib.ui.InputPermissionsTag _jspx_th_liferay$1ui_input$1permissions_0 = (com.liferay.taglib.ui.InputPermissionsTag) _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.get(com.liferay.taglib.ui.InputPermissionsTag.class);
                _jspx_th_liferay$1ui_input$1permissions_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_input$1permissions_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_6);
                _jspx_th_liferay$1ui_input$1permissions_0.setModelName( DLFileEntryConstants.getClassName() );
                int _jspx_eval_liferay$1ui_input$1permissions_0 = _jspx_th_liferay$1ui_input$1permissions_0.doStartTag();
                if (_jspx_th_liferay$1ui_input$1permissions_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.reuse(_jspx_th_liferay$1ui_input$1permissions_0);
                  return;
                }
                _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.reuse(_jspx_th_liferay$1ui_input$1permissions_0);
                out.write("\n\t\t\t\t\t");
              }
              if (_jspx_th_aui_fieldset_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_6);
                return;
              }
              _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_6);
              out.write("\n\t\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_13.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_13);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_13);
          out.write("\n\n\t\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_14 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_14.setPageContext(_jspx_page_context);
          _jspx_th_c_if_14.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          _jspx_th_c_if_14.setTest( pending );
          int _jspx_eval_c_if_14 = _jspx_th_c_if_14.doStartTag();
          if (_jspx_eval_c_if_14 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t\t<div class=\"alert alert-info\">\n\t\t\t\t\t\t");
              if (_jspx_meth_liferay$1ui_message_12((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_14, _jspx_page_context))
                return;
              out.write("\n\t\t\t\t\t</div>\n\t\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_14.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_14.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_14);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_14);
          out.write("\n\t\t\t");
        }
        if (_jspx_th_aui_fieldset$1group_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_fieldset$1group_markupView.reuse(_jspx_th_aui_fieldset$1group_0);
          return;
        }
        _jspx_tagPool_aui_fieldset$1group_markupView.reuse(_jspx_th_aui_fieldset$1group_0);
        out.write("\n\t\t</div>\n\n\t\t");
        //  aui:button-row
        com.liferay.taglib.aui.ButtonRowTag _jspx_th_aui_button$1row_0 = (com.liferay.taglib.aui.ButtonRowTag) _jspx_tagPool_aui_button$1row.get(com.liferay.taglib.aui.ButtonRowTag.class);
        _jspx_th_aui_button$1row_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_button$1row_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        int _jspx_eval_aui_button$1row_0 = _jspx_th_aui_button$1row_0.doStartTag();
        if (_jspx_eval_aui_button$1row_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_15 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_15.setPageContext(_jspx_page_context);
          _jspx_th_c_if_15.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_button$1row_0);
          _jspx_th_c_if_15.setTest( dlEditFileEntryDisplayContext.isSaveButtonVisible() );
          int _jspx_eval_c_if_15 = _jspx_th_c_if_15.doStartTag();
          if (_jspx_eval_c_if_15 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t");
              //  aui:button
              com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_3 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_value_onClick_name_disabled_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
              _jspx_th_aui_button_3.setPageContext(_jspx_page_context);
              _jspx_th_aui_button_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_15);
              _jspx_th_aui_button_3.setDisabled( dlEditFileEntryDisplayContext.isSaveButtonDisabled() );
              _jspx_th_aui_button_3.setName("saveButton");
              _jspx_th_aui_button_3.setOnClick( renderResponse.getNamespace() + "saveFileEntry(true);" );
              _jspx_th_aui_button_3.setValue( dlEditFileEntryDisplayContext.getSaveButtonLabel() );
              int _jspx_eval_aui_button_3 = _jspx_th_aui_button_3.doStartTag();
              if (_jspx_th_aui_button_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_button_value_onClick_name_disabled_nobody.reuse(_jspx_th_aui_button_3);
                return;
              }
              _jspx_tagPool_aui_button_value_onClick_name_disabled_nobody.reuse(_jspx_th_aui_button_3);
              out.write("\n\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_15.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_15.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_15);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_15);
          out.write("\n\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_16 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_16.setPageContext(_jspx_page_context);
          _jspx_th_c_if_16.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_button$1row_0);
          _jspx_th_c_if_16.setTest( dlEditFileEntryDisplayContext.isPublishButtonVisible() );
          int _jspx_eval_c_if_16 = _jspx_th_c_if_16.doStartTag();
          if (_jspx_eval_c_if_16 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t");
              //  aui:button
              com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_4 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_value_type_name_disabled_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
              _jspx_th_aui_button_4.setPageContext(_jspx_page_context);
              _jspx_th_aui_button_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_16);
              _jspx_th_aui_button_4.setDisabled( dlEditFileEntryDisplayContext.isPublishButtonDisabled() );
              _jspx_th_aui_button_4.setName("publishButton");
              _jspx_th_aui_button_4.setType("submit");
              _jspx_th_aui_button_4.setValue( dlEditFileEntryDisplayContext.getPublishButtonLabel() );
              int _jspx_eval_aui_button_4 = _jspx_th_aui_button_4.doStartTag();
              if (_jspx_th_aui_button_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_button_value_type_name_disabled_nobody.reuse(_jspx_th_aui_button_4);
                return;
              }
              _jspx_tagPool_aui_button_value_type_name_disabled_nobody.reuse(_jspx_th_aui_button_4);
              out.write("\n\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_16.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_16.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_16);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_16);
          out.write("\n\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_17 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_17.setPageContext(_jspx_page_context);
          _jspx_th_c_if_17.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_button$1row_0);
          _jspx_th_c_if_17.setTest( dlEditFileEntryDisplayContext.isCheckoutDocumentButtonVisible() );
          int _jspx_eval_c_if_17 = _jspx_th_c_if_17.doStartTag();
          if (_jspx_eval_c_if_17 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t");
              //  aui:button
              com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_5 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_value_onClick_disabled_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
              _jspx_th_aui_button_5.setPageContext(_jspx_page_context);
              _jspx_th_aui_button_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_17);
              _jspx_th_aui_button_5.setDisabled( dlEditFileEntryDisplayContext.isCheckoutDocumentButtonDisabled() );
              _jspx_th_aui_button_5.setOnClick( renderResponse.getNamespace() + "checkOut();" );
              _jspx_th_aui_button_5.setValue("checkout[document]");
              int _jspx_eval_aui_button_5 = _jspx_th_aui_button_5.doStartTag();
              if (_jspx_th_aui_button_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_button_value_onClick_disabled_nobody.reuse(_jspx_th_aui_button_5);
                return;
              }
              _jspx_tagPool_aui_button_value_onClick_disabled_nobody.reuse(_jspx_th_aui_button_5);
              out.write("\n\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_17.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_17.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_17);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_17);
          out.write("\n\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_18 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_18.setPageContext(_jspx_page_context);
          _jspx_th_c_if_18.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_button$1row_0);
          _jspx_th_c_if_18.setTest( dlEditFileEntryDisplayContext.isCheckinButtonVisible() );
          int _jspx_eval_c_if_18 = _jspx_th_c_if_18.doStartTag();
          if (_jspx_eval_c_if_18 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t");
              //  aui:button
              com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_6 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_value_onClick_disabled_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
              _jspx_th_aui_button_6.setPageContext(_jspx_page_context);
              _jspx_th_aui_button_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_18);
              _jspx_th_aui_button_6.setDisabled( dlEditFileEntryDisplayContext.isCheckinButtonDisabled() );
              _jspx_th_aui_button_6.setOnClick( renderResponse.getNamespace() + "checkIn();" );
              _jspx_th_aui_button_6.setValue("save-and-checkin");
              int _jspx_eval_aui_button_6 = _jspx_th_aui_button_6.doStartTag();
              if (_jspx_th_aui_button_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_button_value_onClick_disabled_nobody.reuse(_jspx_th_aui_button_6);
                return;
              }
              _jspx_tagPool_aui_button_value_onClick_disabled_nobody.reuse(_jspx_th_aui_button_6);
              out.write("\n\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_18.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_18.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_18);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_18);
          out.write("\n\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_19 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_19.setPageContext(_jspx_page_context);
          _jspx_th_c_if_19.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_button$1row_0);
          _jspx_th_c_if_19.setTest( dlEditFileEntryDisplayContext.isCancelCheckoutDocumentButtonVisible() );
          int _jspx_eval_c_if_19 = _jspx_th_c_if_19.doStartTag();
          if (_jspx_eval_c_if_19 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t");
              //  aui:button
              com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_7 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_value_onClick_disabled_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
              _jspx_th_aui_button_7.setPageContext(_jspx_page_context);
              _jspx_th_aui_button_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_19);
              _jspx_th_aui_button_7.setDisabled( dlEditFileEntryDisplayContext.isCancelCheckoutDocumentButtonDisabled() );
              _jspx_th_aui_button_7.setOnClick( renderResponse.getNamespace() + "cancelCheckOut();" );
              _jspx_th_aui_button_7.setValue("cancel-checkout[document]");
              int _jspx_eval_aui_button_7 = _jspx_th_aui_button_7.doStartTag();
              if (_jspx_th_aui_button_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_button_value_onClick_disabled_nobody.reuse(_jspx_th_aui_button_7);
                return;
              }
              _jspx_tagPool_aui_button_value_onClick_disabled_nobody.reuse(_jspx_th_aui_button_7);
              out.write("\n\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_19.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_19.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_19);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_19);
          out.write("\n\n\t\t\t");
          //  aui:button
          com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_8 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_type_href_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
          _jspx_th_aui_button_8.setPageContext(_jspx_page_context);
          _jspx_th_aui_button_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_button$1row_0);
          _jspx_th_aui_button_8.setHref( redirect );
          _jspx_th_aui_button_8.setType("cancel");
          int _jspx_eval_aui_button_8 = _jspx_th_aui_button_8.doStartTag();
          if (_jspx_th_aui_button_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_aui_button_type_href_nobody.reuse(_jspx_th_aui_button_8);
            return;
          }
          _jspx_tagPool_aui_button_type_href_nobody.reuse(_jspx_th_aui_button_8);
          out.write("\n\t\t");
        }
        if (_jspx_th_aui_button$1row_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_button$1row.reuse(_jspx_th_aui_button$1row_0);
          return;
        }
        _jspx_tagPool_aui_button$1row.reuse(_jspx_th_aui_button$1row_0);
        out.write('\n');
        out.write('	');
      }
      if (_jspx_th_aui_form_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_aui_form_onSubmit_name_method_enctype_cssClass_action.reuse(_jspx_th_aui_form_0);
        return;
      }
      _jspx_tagPool_aui_form_onSubmit_name_method_enctype_cssClass_action.reuse(_jspx_th_aui_form_0);
      out.write("\n\n\t");
      //  liferay-ui:upload-progress
      com.liferay.taglib.ui.UploadProgressTag _jspx_th_liferay$1ui_upload$1progress_0 = (com.liferay.taglib.ui.UploadProgressTag) _jspx_tagPool_liferay$1ui_upload$1progress_message_id_nobody.get(com.liferay.taglib.ui.UploadProgressTag.class);
      _jspx_th_liferay$1ui_upload$1progress_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1ui_upload$1progress_0.setParent(null);
      _jspx_th_liferay$1ui_upload$1progress_0.setId( uploadProgressId );
      _jspx_th_liferay$1ui_upload$1progress_0.setMessage("uploading");
      int _jspx_eval_liferay$1ui_upload$1progress_0 = _jspx_th_liferay$1ui_upload$1progress_0.doStartTag();
      if (_jspx_th_liferay$1ui_upload$1progress_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1ui_upload$1progress_message_id_nobody.reuse(_jspx_th_liferay$1ui_upload$1progress_0);
        return;
      }
      _jspx_tagPool_liferay$1ui_upload$1progress_message_id_nobody.reuse(_jspx_th_liferay$1ui_upload$1progress_0);
      out.write("\n</div>\n\n");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_20 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_20.setPageContext(_jspx_page_context);
      _jspx_th_c_if_20.setParent(null);
      _jspx_th_c_if_20.setTest( (fileEntry != null) && checkedOut && dlAdminDisplayContext.isVersioningStrategyOverridable() );
      int _jspx_eval_c_if_20 = _jspx_th_c_if_20.doStartTag();
      if (_jspx_eval_c_if_20 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\n\t");

	request.setAttribute("edit_file_entry.jsp-checkedOut", checkedOut);
	
          out.write("\n\n\t");
          //  liferay-util:include
          com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_0 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
          _jspx_th_liferay$1util_include_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1util_include_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_20);
          _jspx_th_liferay$1util_include_0.setPage("/document_library/version_details.jsp");
          _jspx_th_liferay$1util_include_0.setServletContext( application );
          int _jspx_eval_liferay$1util_include_0 = _jspx_th_liferay$1util_include_0.doStartTag();
          if (_jspx_th_liferay$1util_include_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
            return;
          }
          _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
          out.write('\n');
          int evalDoAfterBody = _jspx_th_c_if_20.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_20.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_20);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_20);
      out.write("\n\n<script>\n\tvar form = document.");
      if (_jspx_meth_portlet_namespace_12(_jspx_page_context))
        return;
      out.write("fm;\n\n\tfunction ");
      if (_jspx_meth_portlet_namespace_13(_jspx_page_context))
        return;
      out.write("changeFileEntryType() {\n\t\tLiferay.Util.postForm(form, {\n\t\t\tdata: {\n\t\t\t\t");
      out.print( Constants.CMD );
      out.write(": '");
      out.print( Constants.PREVIEW );
      out.write("'\n\t\t\t}\n\t\t});\n\t}\n\n\tfunction ");
      if (_jspx_meth_portlet_namespace_14(_jspx_page_context))
        return;
      out.write("cancelCheckOut() {\n\t\tLiferay.Util.postForm(form, {\n\t\t\tdata: {\n\t\t\t\t");
      out.print( Constants.CMD );
      out.write(": '");
      out.print( Constants.CANCEL_CHECKOUT );
      out.write("'\n\t\t\t}\n\t\t});\n\t}\n\n\tfunction ");
      if (_jspx_meth_portlet_namespace_15(_jspx_page_context))
        return;
      out.write("checkIn() {\n\t\tLiferay.Util.setFormValues(form, {\n\t\t\t");
      out.print( Constants.CMD );
      out.write(": '");
      out.print( Constants.UPDATE_AND_CHECKIN );
      out.write("'\n\t\t});\n\n\t\tif (");
      out.print( dlAdminDisplayContext.isVersioningStrategyOverridable() );
      out.write(") {\n\t\t\t");
      if (_jspx_meth_portlet_namespace_16(_jspx_page_context))
        return;
      out.write("showVersionDetailsDialog(form);\n\t\t}\n\t\telse {\n\t\t\tsubmitForm(form);\n\t\t}\n\t}\n\n\tfunction ");
      if (_jspx_meth_portlet_namespace_17(_jspx_page_context))
        return;
      out.write("checkOut() {\n\t\tLiferay.Util.postForm(form, {\n\t\t\tdata: {\n\t\t\t\t");
      out.print( Constants.CMD );
      out.write(": '");
      out.print( Constants.CHECKOUT );
      out.write("'\n\t\t\t}\n\t\t});\n\t}\n\n\tfunction ");
      if (_jspx_meth_portlet_namespace_18(_jspx_page_context))
        return;
      out.write("saveFileEntry(draft) {\n\t\tvar fileElement = Liferay.Util.getFormElement(form, 'file');\n\n\t\tif (fileElement && fileElement.value) {\n\t\t\t");
      out.print( HtmlUtil.escape(uploadProgressId) );
      out.write(".startProgress();\n\t\t}\n\n\t\tvar data = {\n\t\t\t");
      out.print( Constants.CMD );
      out.write(":\n\t\t\t\t'");
      out.print( (fileEntry == null) ? Constants.ADD : Constants.UPDATE );
      out.write("'\n\t\t};\n\n\t\tif (draft) {\n\t\t\tdata.workflowAction = '");
      out.print( WorkflowConstants.ACTION_SAVE_DRAFT );
      out.write("';\n\t\t}\n\n\t\tLiferay.Util.postForm(form, {\n\t\t\tdata: data\n\t\t});\n\t}\n\n\tLiferay.provide(\n\t\twindow,\n\t\t'");
      if (_jspx_meth_portlet_namespace_19(_jspx_page_context))
        return;
      out.write("showVersionDetailsDialog',\n\t\tfunction(form) {\n\t\t\tLiferay.DocumentLibraryCheckin.showDialog(\n\t\t\t\t'");
      if (_jspx_meth_portlet_namespace_20(_jspx_page_context))
        return;
      out.write("',\n\t\t\t\tfunction(versionIncrease, changeLog) {\n\t\t\t\t\tLiferay.Util.postForm(form, {\n\t\t\t\t\t\tdata: {\n\t\t\t\t\t\t\tchangeLog: changeLog,\n\t\t\t\t\t\t\tupdateVersionDetails: true,\n\t\t\t\t\t\t\tversionIncrease: versionIncrease\n\t\t\t\t\t\t}\n\t\t\t\t\t});\n\t\t\t\t}\n\t\t\t);\n\t\t},\n\t\t['document-library-checkin']\n\t);\n\n\tfunction ");
      if (_jspx_meth_portlet_namespace_21(_jspx_page_context))
        return;
      out.write("updateTitle() {\n\t\tvar titleElement = document.getElementById('");
      if (_jspx_meth_portlet_namespace_22(_jspx_page_context))
        return;
      out.write("title');\n\n\t\tif (titleElement && !titleElement.value) {\n\t\t\tvar fileElement = document.getElementById('");
      if (_jspx_meth_portlet_namespace_23(_jspx_page_context))
        return;
      out.write("file');\n\n\t\t\tif (fileElement && fileElement.value) {\n\t\t\t\ttitleElement.value = fileElement.value.replace(/^.*[\\\\\\/]/, '');\n\t\t\t}\n\t\t}\n\n\t\tvar formComponent = Liferay.Form.get('");
      if (_jspx_meth_portlet_namespace_24(_jspx_page_context))
        return;
      out.write("fm');\n\n\t\tformComponent.formValidator.validateField('");
      if (_jspx_meth_portlet_namespace_25(_jspx_page_context))
        return;
      out.write("title');\n\t}\n</script>\n\n");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_21 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_21.setPageContext(_jspx_page_context);
      _jspx_th_c_if_21.setParent(null);
      _jspx_th_c_if_21.setTest( (fileEntry != null) && !checkedOut && dlAdminDisplayContext.isVersioningStrategyOverridable() );
      int _jspx_eval_c_if_21 = _jspx_th_c_if_21.doStartTag();
      if (_jspx_eval_c_if_21 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\n');
          out.write('	');
          if (_jspx_meth_aui_script_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_21, _jspx_page_context))
            return;
          out.write('\n');
          int evalDoAfterBody = _jspx_th_c_if_21.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_21.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_21);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_21);
      out.write('\n');
      out.write('\n');

if (fileEntry != null) {
	DLBreadcrumbUtil.addPortletBreadcrumbEntries(fileEntry, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
}
else {
	DLBreadcrumbUtil.addPortletBreadcrumbEntries(folderId, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-file-entry"), currentURL);
}

      out.write('\n');
      out.write('\n');
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

  private boolean _jspx_meth_liferay$1ui_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_0 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
    _jspx_th_liferay$1ui_message_0.setKey("you-now-have-an-indefinite-lock-on-this-document");
    int _jspx_eval_liferay$1ui_message_0 = _jspx_th_liferay$1ui_message_0.doStartTag();
    if (_jspx_th_liferay$1ui_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
    return false;
  }

  private boolean _jspx_meth_liferay$1portlet_param_0(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1portlet_actionURL_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_liferay$1portlet_param_0 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_liferay$1portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_liferay$1portlet_param_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1portlet_param_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_actionURL_0);
    _jspx_th_liferay$1portlet_param_0.setName("mvcRenderCommandName");
    _jspx_th_liferay$1portlet_param_0.setValue("/document_library/edit_file_entry");
    int _jspx_eval_liferay$1portlet_param_0 = _jspx_th_liferay$1portlet_param_0.doStartTag();
    if (_jspx_th_liferay$1portlet_param_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1portlet_param_value_name_nobody.reuse(_jspx_th_liferay$1portlet_param_0);
      return true;
    }
    _jspx_tagPool_liferay$1portlet_param_value_name_nobody.reuse(_jspx_th_liferay$1portlet_param_0);
    return false;
  }

  private boolean _jspx_meth_aui_input_6(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_6 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_6.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_3);
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

  private boolean _jspx_meth_aui_input_7(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_7 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_7.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_3);
    _jspx_th_aui_input_7.setName("changeLog");
    _jspx_th_aui_input_7.setType("hidden");
    int _jspx_eval_aui_input_7 = _jspx_th_aui_input_7.doStartTag();
    if (_jspx_th_aui_input_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_7);
      return true;
    }
    _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_7);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_5(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_error_5, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_5 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_5.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_5);
    _jspx_th_liferay$1ui_message_5.setKey("document-names-must-end-with-one-of-the-following-extensions");
    int _jspx_eval_liferay$1ui_message_5 = _jspx_th_liferay$1ui_message_5.doStartTag();
    if (_jspx_th_liferay$1ui_message_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_5);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_5);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_6(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_error_6, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_6 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_6.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_6);
    _jspx_th_liferay$1ui_message_6.setKey("media-files-must-be-one-of-the-following-formats");
    int _jspx_eval_liferay$1ui_message_6 = _jspx_th_liferay$1ui_message_6.doStartTag();
    if (_jspx_th_liferay$1ui_message_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_6);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_6);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_7(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_error_9, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_7 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_7.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_9);
    _jspx_th_liferay$1ui_message_7.setKey("the-source-file-does-not-have-the-same-extension-as-the-original-file");
    int _jspx_eval_liferay$1ui_message_7 = _jspx_th_liferay$1ui_message_7.doStartTag();
    if (_jspx_th_liferay$1ui_message_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_7);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_7);
    return false;
  }

  private boolean _jspx_meth_liferay$1asset_asset$1categories$1error_0(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-asset:asset-categories-error
    com.liferay.asset.taglib.servlet.taglib.AssetCategoriesErrorTag _jspx_th_liferay$1asset_asset$1categories$1error_0 = (com.liferay.asset.taglib.servlet.taglib.AssetCategoriesErrorTag) _jspx_tagPool_liferay$1asset_asset$1categories$1error_nobody.get(com.liferay.asset.taglib.servlet.taglib.AssetCategoriesErrorTag.class);
    _jspx_th_liferay$1asset_asset$1categories$1error_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1asset_asset$1categories$1error_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    int _jspx_eval_liferay$1asset_asset$1categories$1error_0 = _jspx_th_liferay$1asset_asset$1categories$1error_0.doStartTag();
    if (_jspx_th_liferay$1asset_asset$1categories$1error_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1asset_asset$1categories$1error_nobody.reuse(_jspx_th_liferay$1asset_asset$1categories$1error_0);
      return true;
    }
    _jspx_tagPool_liferay$1asset_asset$1categories$1error_nobody.reuse(_jspx_th_liferay$1asset_asset$1categories$1error_0);
    return false;
  }

  private boolean _jspx_meth_liferay$1asset_asset$1tags$1error_0(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-asset:asset-tags-error
    com.liferay.asset.taglib.servlet.taglib.AssetTagsErrorTag _jspx_th_liferay$1asset_asset$1tags$1error_0 = (com.liferay.asset.taglib.servlet.taglib.AssetTagsErrorTag) _jspx_tagPool_liferay$1asset_asset$1tags$1error_nobody.get(com.liferay.asset.taglib.servlet.taglib.AssetTagsErrorTag.class);
    _jspx_th_liferay$1asset_asset$1tags$1error_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1asset_asset$1tags$1error_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    int _jspx_eval_liferay$1asset_asset$1tags$1error_0 = _jspx_th_liferay$1asset_asset$1tags$1error_0.doStartTag();
    if (_jspx_th_liferay$1asset_asset$1tags$1error_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1asset_asset$1tags$1error_nobody.reuse(_jspx_th_liferay$1asset_asset$1tags$1error_0);
      return true;
    }
    _jspx_tagPool_liferay$1asset_asset$1tags$1error_nobody.reuse(_jspx_th_liferay$1asset_asset$1tags$1error_0);
    return false;
  }

  private boolean _jspx_meth_aui_button_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_5, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:button
    com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_0 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_value_name_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
    _jspx_th_aui_button_0.setPageContext(_jspx_page_context);
    _jspx_th_aui_button_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
    _jspx_th_aui_button_0.setName("selectFolderButton");
    _jspx_th_aui_button_0.setValue("select");
    int _jspx_eval_aui_button_0 = _jspx_th_aui_button_0.doStartTag();
    if (_jspx_th_aui_button_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_button_value_name_nobody.reuse(_jspx_th_aui_button_0);
      return true;
    }
    _jspx_tagPool_aui_button_value_name_nobody.reuse(_jspx_th_aui_button_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_5, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_0 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
    int _jspx_eval_portlet_namespace_0 = _jspx_th_portlet_namespace_0.doStartTag();
    if (_jspx_th_portlet_namespace_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_5, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_1 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_1.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
    int _jspx_eval_portlet_namespace_1 = _jspx_th_portlet_namespace_1.doStartTag();
    if (_jspx_th_portlet_namespace_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_11(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_5, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_11 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_11.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
    _jspx_th_liferay$1ui_message_11.setArguments(new String("folder"));
    _jspx_th_liferay$1ui_message_11.setKey("select-x");
    int _jspx_eval_liferay$1ui_message_11 = _jspx_th_liferay$1ui_message_11.doStartTag();
    if (_jspx_th_liferay$1ui_message_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_11);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_11);
    return false;
  }

  private boolean _jspx_meth_portlet_param_0(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1portlet_renderURL_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_0 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_renderURL_0);
    _jspx_th_portlet_param_0.setName("mvcRenderCommandName");
    _jspx_th_portlet_param_0.setValue("/document_library/select_folder");
    int _jspx_eval_portlet_param_0 = _jspx_th_portlet_param_0.doStartTag();
    if (_jspx_th_portlet_param_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_2(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_5, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_2 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_2.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
    int _jspx_eval_portlet_namespace_2 = _jspx_th_portlet_namespace_2.doStartTag();
    if (_jspx_th_portlet_namespace_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_2);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_2);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_3(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_field$1wrapper_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_3 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_3.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_field$1wrapper_0);
    int _jspx_eval_portlet_namespace_3 = _jspx_th_portlet_namespace_3.doStartTag();
    if (_jspx_th_portlet_namespace_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_3);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_3);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_4(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_field$1wrapper_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_4 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_4.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_field$1wrapper_0);
    int _jspx_eval_portlet_namespace_4 = _jspx_th_portlet_namespace_4.doStartTag();
    if (_jspx_th_portlet_namespace_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_4);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_4);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_5(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_5 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_5.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_5 = _jspx_th_portlet_namespace_5.doStartTag();
    if (_jspx_th_portlet_namespace_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_5);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_5);
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

  private boolean _jspx_meth_portlet_namespace_8(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_8 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_8.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_8 = _jspx_th_portlet_namespace_8.doStartTag();
    if (_jspx_th_portlet_namespace_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_8);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_8);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_9(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_9 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_9.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_9 = _jspx_th_portlet_namespace_9.doStartTag();
    if (_jspx_th_portlet_namespace_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_9);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_9);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_10(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_10 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_10.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_10 = _jspx_th_portlet_namespace_10.doStartTag();
    if (_jspx_th_portlet_namespace_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_10);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_10);
    return false;
  }

  private boolean _jspx_meth_aui_input_11(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_fieldset_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_11 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_11.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_0);
    _jspx_th_aui_input_11.setName("title");
    int _jspx_eval_aui_input_11 = _jspx_th_aui_input_11.doStartTag();
    if (_jspx_th_aui_input_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_name_nobody.reuse(_jspx_th_aui_input_11);
      return true;
    }
    _jspx_tagPool_aui_input_name_nobody.reuse(_jspx_th_aui_input_11);
    return false;
  }

  private boolean _jspx_meth_aui_input_12(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_6, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_12 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_12.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_6);
    _jspx_th_aui_input_12.setName("description");
    int _jspx_eval_aui_input_12 = _jspx_th_aui_input_12.doStartTag();
    if (_jspx_th_aui_input_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_name_nobody.reuse(_jspx_th_aui_input_12);
      return true;
    }
    _jspx_tagPool_aui_input_name_nobody.reuse(_jspx_th_aui_input_12);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_11(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_fieldset_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_11 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_11.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
    int _jspx_eval_portlet_namespace_11 = _jspx_th_portlet_namespace_11.doStartTag();
    if (_jspx_th_portlet_namespace_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_11);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_11);
    return false;
  }

  private boolean _jspx_meth_aui_model$1context_1(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_fieldset_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:model-context
    com.liferay.taglib.aui.ModelContextTag _jspx_th_aui_model$1context_1 = (com.liferay.taglib.aui.ModelContextTag) _jspx_tagPool_aui_model$1context_nobody.get(com.liferay.taglib.aui.ModelContextTag.class);
    _jspx_th_aui_model$1context_1.setPageContext(_jspx_page_context);
    _jspx_th_aui_model$1context_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
    int _jspx_eval_aui_model$1context_1 = _jspx_th_aui_model$1context_1.doStartTag();
    if (_jspx_th_aui_model$1context_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_model$1context_nobody.reuse(_jspx_th_aui_model$1context_1);
      return true;
    }
    _jspx_tagPool_aui_model$1context_nobody.reuse(_jspx_th_aui_model$1context_1);
    return false;
  }

  private boolean _jspx_meth_aui_input_19(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_fieldset_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_19 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_name_maxLength_label_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_19.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_19.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
    _jspx_th_aui_input_19.setLabel("version-notes");
    _jspx_th_aui_input_19.setDynamicAttribute(null, "maxLength", new String("75"));
    _jspx_th_aui_input_19.setName("changeLog");
    int _jspx_eval_aui_input_19 = _jspx_th_aui_input_19.doStartTag();
    if (_jspx_th_aui_input_19.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_name_maxLength_label_nobody.reuse(_jspx_th_aui_input_19);
      return true;
    }
    _jspx_tagPool_aui_input_name_maxLength_label_nobody.reuse(_jspx_th_aui_input_19);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_12(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_14, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_12 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_12.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_14);
    _jspx_th_liferay$1ui_message_12.setKey("there-is-a-publication-workflow-in-process");
    int _jspx_eval_liferay$1ui_message_12 = _jspx_th_liferay$1ui_message_12.doStartTag();
    if (_jspx_th_liferay$1ui_message_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_12);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_12);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_12(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_12 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_12.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_12.setParent(null);
    int _jspx_eval_portlet_namespace_12 = _jspx_th_portlet_namespace_12.doStartTag();
    if (_jspx_th_portlet_namespace_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_12);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_12);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_13(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_13 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_13.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_13.setParent(null);
    int _jspx_eval_portlet_namespace_13 = _jspx_th_portlet_namespace_13.doStartTag();
    if (_jspx_th_portlet_namespace_13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_13);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_13);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_14(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_14 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_14.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_14.setParent(null);
    int _jspx_eval_portlet_namespace_14 = _jspx_th_portlet_namespace_14.doStartTag();
    if (_jspx_th_portlet_namespace_14.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_14);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_14);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_15(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_15 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_15.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_15.setParent(null);
    int _jspx_eval_portlet_namespace_15 = _jspx_th_portlet_namespace_15.doStartTag();
    if (_jspx_th_portlet_namespace_15.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_15);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_15);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_16(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_16 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_16.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_16.setParent(null);
    int _jspx_eval_portlet_namespace_16 = _jspx_th_portlet_namespace_16.doStartTag();
    if (_jspx_th_portlet_namespace_16.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_16);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_16);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_17(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_17 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_17.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_17.setParent(null);
    int _jspx_eval_portlet_namespace_17 = _jspx_th_portlet_namespace_17.doStartTag();
    if (_jspx_th_portlet_namespace_17.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_17);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_17);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_18(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_18 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_18.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_18.setParent(null);
    int _jspx_eval_portlet_namespace_18 = _jspx_th_portlet_namespace_18.doStartTag();
    if (_jspx_th_portlet_namespace_18.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_18);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_18);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_19(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_19 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_19.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_19.setParent(null);
    int _jspx_eval_portlet_namespace_19 = _jspx_th_portlet_namespace_19.doStartTag();
    if (_jspx_th_portlet_namespace_19.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_19);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_19);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_20(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_20 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_20.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_20.setParent(null);
    int _jspx_eval_portlet_namespace_20 = _jspx_th_portlet_namespace_20.doStartTag();
    if (_jspx_th_portlet_namespace_20.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_20);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_20);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_21(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_21 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_21.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_21.setParent(null);
    int _jspx_eval_portlet_namespace_21 = _jspx_th_portlet_namespace_21.doStartTag();
    if (_jspx_th_portlet_namespace_21.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_21);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_21);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_22(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_22 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_22.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_22.setParent(null);
    int _jspx_eval_portlet_namespace_22 = _jspx_th_portlet_namespace_22.doStartTag();
    if (_jspx_th_portlet_namespace_22.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_22);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_22);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_23(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_23 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_23.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_23.setParent(null);
    int _jspx_eval_portlet_namespace_23 = _jspx_th_portlet_namespace_23.doStartTag();
    if (_jspx_th_portlet_namespace_23.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_23);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_23);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_24(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_24 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_24.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_24.setParent(null);
    int _jspx_eval_portlet_namespace_24 = _jspx_th_portlet_namespace_24.doStartTag();
    if (_jspx_th_portlet_namespace_24.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_24);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_24);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_25(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_25 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_25.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_25.setParent(null);
    int _jspx_eval_portlet_namespace_25 = _jspx_th_portlet_namespace_25.doStartTag();
    if (_jspx_th_portlet_namespace_25.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_25);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_25);
    return false;
  }

  private boolean _jspx_meth_aui_script_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_21, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:script
    com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_1 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script_require.get(com.liferay.taglib.aui.ScriptTag.class);
    _jspx_th_aui_script_1.setPageContext(_jspx_page_context);
    _jspx_th_aui_script_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_21);
    _jspx_th_aui_script_1.setRequire("metal-dom/src/dom as dom");
    int _jspx_eval_aui_script_1 = _jspx_th_aui_script_1.doStartTag();
    if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_aui_script_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_aui_script_1.doInitBody();
      }
      do {
        out.write("\n\t\tvar updateVersionDetailsElement = document.getElementById(\n\t\t\t'");
        if (_jspx_meth_portlet_namespace_26((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
          return true;
        out.write("updateVersionDetails'\n\t\t);\n\t\tvar versionDetailsElement = document.getElementById(\n\t\t\t'");
        if (_jspx_meth_portlet_namespace_27((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
          return true;
        out.write("versionDetails'\n\t\t);\n\n\t\tif (updateVersionDetailsElement && versionDetailsElement) {\n\t\t\tupdateVersionDetailsElement.addEventListener('click', function(event) {\n\t\t\t\tdom.toggleClasses(versionDetailsElement, 'hide');\n\t\t\t});\n\t\t}\n\t");
        int evalDoAfterBody = _jspx_th_aui_script_1.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
        out = _jspx_page_context.popBody();
    }
    if (_jspx_th_aui_script_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_script_require.reuse(_jspx_th_aui_script_1);
      return true;
    }
    _jspx_tagPool_aui_script_require.reuse(_jspx_th_aui_script_1);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_26(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_26 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_26.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_26.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_26 = _jspx_th_portlet_namespace_26.doStartTag();
    if (_jspx_th_portlet_namespace_26.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_26);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_26);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_27(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_27 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_27.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_27.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_27 = _jspx_th_portlet_namespace_27.doStartTag();
    if (_jspx_th_portlet_namespace_27.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_27);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_27);
    return false;
  }
}
