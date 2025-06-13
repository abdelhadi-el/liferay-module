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

public final class edit_005ffolder_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList<String>(4);
    _jspx_dependants.add("/document_library/init.jsp");
    _jspx_dependants.add("/init.jsp");
    _jspx_dependants.add("/init-ext.jsp");
    _jspx_dependants.add("/document_library/init-ext.jsp");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_arguments_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_param_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_error_exception;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_model$1context_model_bean_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_fieldset_label_helpMessage_collapsible_collapsed;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_option_value_selected_label_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_fieldset$1group_markupView;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_buffer_var;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_name_autoFocus_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_fieldset;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_select_name_label;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_when_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button_type_href_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_fieldset_label_collapsible_collapsed;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_renderURL_windowState;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_icon_message_markupView_label_icon_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_select_name_label_helpMessage_cssClass;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_actionURL_var_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_form_onSubmit_name_method_action;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_option_value_selected_label_id_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container_total_headerNames;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_option_value_label_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_script;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button_type_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_keyProperty_escapedModel_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_choose;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_script_use;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_namespace_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1iterator_paginate_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_value_type_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1results_results_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_otherwise;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_error_message_exception_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_icon_url_message_markupView_linkCssClass_label_icon_cssClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_select_title_name_label;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1text;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button$1row;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_param_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_error_exception = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_model$1context_model_bean_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_fieldset_label_helpMessage_collapsible_collapsed = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_option_value_selected_label_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_fieldset$1group_markupView = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_buffer_var = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_name_autoFocus_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_fieldset = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_select_name_label = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_when_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button_type_href_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_fieldset_label_collapsible_collapsed = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_renderURL_windowState = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_icon_message_markupView_label_icon_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_select_name_label_helpMessage_cssClass = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_actionURL_var_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_form_onSubmit_name_method_action = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_option_value_selected_label_id_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container_total_headerNames = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_option_value_label_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_script = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button_type_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_keyProperty_escapedModel_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_choose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_script_use = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_namespace_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1iterator_paginate_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_value_type_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1results_results_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_otherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_error_message_exception_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_icon_url_message_markupView_linkCssClass_label_icon_cssClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_select_title_name_label = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button$1row = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.release();
    _jspx_tagPool_portlet_param_value_name_nobody.release();
    _jspx_tagPool_liferay$1ui_error_exception.release();
    _jspx_tagPool_aui_model$1context_model_bean_nobody.release();
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.release();
    _jspx_tagPool_aui_fieldset_label_helpMessage_collapsible_collapsed.release();
    _jspx_tagPool_aui_option_value_selected_label_nobody.release();
    _jspx_tagPool_aui_fieldset$1group_markupView.release();
    _jspx_tagPool_liferay$1util_buffer_var.release();
    _jspx_tagPool_aui_input_name_autoFocus_nobody.release();
    _jspx_tagPool_aui_fieldset.release();
    _jspx_tagPool_aui_select_name_label.release();
    _jspx_tagPool_c_when_test.release();
    _jspx_tagPool_aui_button_type_href_nobody.release();
    _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.release();
    _jspx_tagPool_portlet_renderURL_windowState.release();
    _jspx_tagPool_liferay$1ui_icon_message_markupView_label_icon_nobody.release();
    _jspx_tagPool_aui_select_name_label_helpMessage_cssClass.release();
    _jspx_tagPool_aui_input_name_nobody.release();
    _jspx_tagPool_portlet_actionURL_var_name.release();
    _jspx_tagPool_aui_form_onSubmit_name_method_action.release();
    _jspx_tagPool_aui_option_value_selected_label_id_nobody.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_liferay$1ui_search$1container_total_headerNames.release();
    _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.release();
    _jspx_tagPool_aui_option_value_label_nobody.release();
    _jspx_tagPool_aui_script.release();
    _jspx_tagPool_aui_button_type_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_keyProperty_escapedModel_className.release();
    _jspx_tagPool_c_choose.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_aui_script_use.release();
    _jspx_tagPool_portlet_namespace_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1iterator_paginate_nobody.release();
    _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.release();
    _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name.release();
    _jspx_tagPool_aui_input_value_type_name_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1results_results_nobody.release();
    _jspx_tagPool_c_otherwise.release();
    _jspx_tagPool_liferay$1ui_error_message_exception_nobody.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1ui_icon_url_message_markupView_linkCssClass_label_icon_cssClass_nobody.release();
    _jspx_tagPool_aui_select_title_name_label.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text.release();
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

String redirect = ParamUtil.getString(request, "redirect");

Folder folder = (Folder)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FOLDER);

long folderId = BeanParamUtil.getLong(folder, request, "folderId");

long repositoryId = BeanParamUtil.getLong(folder, request, "repositoryId");

Folder parentFolder = null;

long parentFolderId = BeanParamUtil.getLong(folder, request, "parentFolderId", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

String parentFolderName = LanguageUtil.get(request, "home");

try {
	if (parentFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
		parentFolder = DLAppLocalServiceUtil.getFolder(parentFolderId);

		parentFolderName = parentFolder.getName();
	}
}
catch (NoSuchFolderException nsfe) {
}

boolean rootFolder = ParamUtil.getBoolean(request, "rootFolder");

Group scopeGroup = GroupLocalServiceUtil.getGroup(scopeGroupId);

boolean workflowEnabled = WorkflowEngineManagerUtil.isDeployed() && (WorkflowHandlerRegistryUtil.getWorkflowHandler(DLFileEntry.class.getName()) != null) && DLFolderPermission.contains(permissionChecker, themeDisplay.getScopeGroupId(), folderId, ActionKeys.UPDATE) && !scopeGroup.isLayoutSetPrototype();

List<WorkflowDefinition> workflowDefinitions = null;

if (workflowEnabled) {
	workflowDefinitions = WorkflowDefinitionManagerUtil.getActiveWorkflowDefinitions(company.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
}

String languageId = LanguageUtil.getLanguageId(request);

String headerTitle = (folder == null) ? (rootFolder ? LanguageUtil.get(request, "home") : LanguageUtil.get(request, "new-folder")) : folder.getName();

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (portletTitleBasedNavigation) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);

	renderResponse.setTitle(headerTitle);
}

      out.write("\n\n<div ");
      out.print( portletTitleBasedNavigation ? "class=\"container-fluid-1280\"" : StringPool.BLANK );
      out.write(">\n\t");
      //  liferay-util:buffer
      com.liferay.taglib.util.BufferTag _jspx_th_liferay$1util_buffer_0 = (com.liferay.taglib.util.BufferTag) _jspx_tagPool_liferay$1util_buffer_var.get(com.liferay.taglib.util.BufferTag.class);
      _jspx_th_liferay$1util_buffer_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1util_buffer_0.setParent(null);
      _jspx_th_liferay$1util_buffer_0.setVar("removeFileEntryTypeIcon");
      int _jspx_eval_liferay$1util_buffer_0 = _jspx_th_liferay$1util_buffer_0.doStartTag();
      if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_liferay$1util_buffer_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_liferay$1util_buffer_0.doInitBody();
        }
        do {
          out.write("\n\t\t");
          //  liferay-ui:icon
          com.liferay.taglib.ui.IconTag _jspx_th_liferay$1ui_icon_0 = (com.liferay.taglib.ui.IconTag) _jspx_tagPool_liferay$1ui_icon_message_markupView_label_icon_nobody.get(com.liferay.taglib.ui.IconTag.class);
          _jspx_th_liferay$1ui_icon_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1ui_icon_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1util_buffer_0);
          _jspx_th_liferay$1ui_icon_0.setIcon("times");
          _jspx_th_liferay$1ui_icon_0.setLabel( true );
          _jspx_th_liferay$1ui_icon_0.setMarkupView("lexicon");
          _jspx_th_liferay$1ui_icon_0.setMessage("remove");
          int _jspx_eval_liferay$1ui_icon_0 = _jspx_th_liferay$1ui_icon_0.doStartTag();
          if (_jspx_th_liferay$1ui_icon_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1ui_icon_message_markupView_label_icon_nobody.reuse(_jspx_th_liferay$1ui_icon_0);
            return;
          }
          _jspx_tagPool_liferay$1ui_icon_message_markupView_label_icon_nobody.reuse(_jspx_th_liferay$1ui_icon_0);
          out.write('\n');
          out.write('	');
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
      java.lang.String removeFileEntryTypeIcon = null;
      removeFileEntryTypeIcon = (java.lang.String) _jspx_page_context.findAttribute("removeFileEntryTypeIcon");
      out.write("\n\n\t");
      //  portlet:actionURL
      com.liferay.taglib.portlet.ActionURLTag _jspx_th_portlet_actionURL_0 = (com.liferay.taglib.portlet.ActionURLTag) _jspx_tagPool_portlet_actionURL_var_name.get(com.liferay.taglib.portlet.ActionURLTag.class);
      _jspx_th_portlet_actionURL_0.setPageContext(_jspx_page_context);
      _jspx_th_portlet_actionURL_0.setParent(null);
      _jspx_th_portlet_actionURL_0.setName("/document_library/edit_folder");
      _jspx_th_portlet_actionURL_0.setVar("editFolderURL");
      int _jspx_eval_portlet_actionURL_0 = _jspx_th_portlet_actionURL_0.doStartTag();
      if (_jspx_eval_portlet_actionURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n\t\t");
        if (_jspx_meth_portlet_param_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_actionURL_0, _jspx_page_context))
          return;
        out.write('\n');
        out.write('	');
      }
      if (_jspx_th_portlet_actionURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_portlet_actionURL_var_name.reuse(_jspx_th_portlet_actionURL_0);
        return;
      }
      _jspx_tagPool_portlet_actionURL_var_name.reuse(_jspx_th_portlet_actionURL_0);
      java.lang.String editFolderURL = null;
      editFolderURL = (java.lang.String) _jspx_page_context.findAttribute("editFolderURL");
      out.write("\n\n\t");
      //  aui:form
      com.liferay.taglib.aui.FormTag _jspx_th_aui_form_0 = (com.liferay.taglib.aui.FormTag) _jspx_tagPool_aui_form_onSubmit_name_method_action.get(com.liferay.taglib.aui.FormTag.class);
      _jspx_th_aui_form_0.setPageContext(_jspx_page_context);
      _jspx_th_aui_form_0.setParent(null);
      _jspx_th_aui_form_0.setAction( editFolderURL );
      _jspx_th_aui_form_0.setMethod("post");
      _jspx_th_aui_form_0.setName("fm");
      _jspx_th_aui_form_0.setOnSubmit( "event.preventDefault(); " + renderResponse.getNamespace() + "savePage();" );
      int _jspx_eval_aui_form_0 = _jspx_th_aui_form_0.doStartTag();
      if (_jspx_eval_aui_form_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n\t\t");
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_0 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_0.setName( Constants.CMD );
        _jspx_th_aui_input_0.setType("hidden");
        _jspx_th_aui_input_0.setValue( rootFolder ? "updateWorkflowDefinitions" : ((folder == null) ? Constants.ADD : Constants.UPDATE) );
        int _jspx_eval_aui_input_0 = _jspx_th_aui_input_0.doStartTag();
        if (_jspx_th_aui_input_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_0);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_0);
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
        _jspx_th_aui_input_2.setName("folderId");
        _jspx_th_aui_input_2.setType("hidden");
        _jspx_th_aui_input_2.setValue( folderId );
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
        _jspx_th_aui_input_4.setName("parentFolderId");
        _jspx_th_aui_input_4.setType("hidden");
        _jspx_th_aui_input_4.setValue( parentFolderId );
        int _jspx_eval_aui_input_4 = _jspx_th_aui_input_4.doStartTag();
        if (_jspx_th_aui_input_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_4);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_4);
        out.write("\n\n\t\t");
        //  c:if
        org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
        _jspx_th_c_if_0.setPageContext(_jspx_page_context);
        _jspx_th_c_if_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_c_if_0.setTest( !portletTitleBasedNavigation );
        int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
        if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          do {
            out.write("\n\t\t\t");
            //  liferay-ui:header
            com.liferay.taglib.ui.HeaderTag _jspx_th_liferay$1ui_header_0 = (com.liferay.taglib.ui.HeaderTag) _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.get(com.liferay.taglib.ui.HeaderTag.class);
            _jspx_th_liferay$1ui_header_0.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_header_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
            _jspx_th_liferay$1ui_header_0.setBackURL( redirect );
            _jspx_th_liferay$1ui_header_0.setLocalizeTitle( false );
            _jspx_th_liferay$1ui_header_0.setTitle( headerTitle );
            int _jspx_eval_liferay$1ui_header_0 = _jspx_th_liferay$1ui_header_0.doStartTag();
            if (_jspx_th_liferay$1ui_header_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.reuse(_jspx_th_liferay$1ui_header_0);
              return;
            }
            _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.reuse(_jspx_th_liferay$1ui_header_0);
            out.write("\n\t\t");
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
        out.write("\n\n\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_0 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_0.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_0.setException( DuplicateFileEntryException.class );
        _jspx_th_liferay$1ui_error_0.setMessage("please-enter-a-unique-folder-name");
        int _jspx_eval_liferay$1ui_error_0 = _jspx_th_liferay$1ui_error_0.doStartTag();
        if (_jspx_th_liferay$1ui_error_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_0);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_0);
        out.write("\n\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_1 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_1.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_1.setException( DuplicateFolderNameException.class );
        _jspx_th_liferay$1ui_error_1.setMessage("please-enter-a-unique-folder-name");
        int _jspx_eval_liferay$1ui_error_1 = _jspx_th_liferay$1ui_error_1.doStartTag();
        if (_jspx_th_liferay$1ui_error_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_1);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_1);
        out.write("\n\n\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_2 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_exception.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_2.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_2.setException( FolderNameException.class );
        int _jspx_eval_liferay$1ui_error_2 = _jspx_th_liferay$1ui_error_2.doStartTag();
        if (_jspx_eval_liferay$1ui_error_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Object errorException = null;
          if (_jspx_eval_liferay$1ui_error_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_error_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_error_2.doInitBody();
          }
          errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
          do {
            out.write("\n\t\t\t<p>\n\t\t\t\t");
            //  liferay-ui:message
            com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_0 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
            _jspx_th_liferay$1ui_message_0.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_2);
            _jspx_th_liferay$1ui_message_0.setArguments( DLFolderConstants.getNameReservedWords(PropsValues.DL_NAME_BLACKLIST) );
            _jspx_th_liferay$1ui_message_0.setKey("the-folder-name-cannot-be-blank-or-a-reserved-word-such-as-x");
            int _jspx_eval_liferay$1ui_message_0 = _jspx_th_liferay$1ui_message_0.doStartTag();
            if (_jspx_th_liferay$1ui_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_0);
              return;
            }
            _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_0);
            out.write("\n\t\t\t</p>\n\n\t\t\t<p>\n\t\t\t\t");
            //  liferay-ui:message
            com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_1 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
            _jspx_th_liferay$1ui_message_1.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_2);
            _jspx_th_liferay$1ui_message_1.setArguments( DLFolderConstants.getNameInvalidEndCharacters(PropsValues.DL_CHAR_LAST_BLACKLIST) );
            _jspx_th_liferay$1ui_message_1.setKey("the-folder-name-cannot-end-with-the-following-characters-x");
            int _jspx_eval_liferay$1ui_message_1 = _jspx_th_liferay$1ui_message_1.doStartTag();
            if (_jspx_th_liferay$1ui_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_1);
              return;
            }
            _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_1);
            out.write("\n\t\t\t</p>\n\n\t\t\t<p>\n\t\t\t\t");
            //  liferay-ui:message
            com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_2 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
            _jspx_th_liferay$1ui_message_2.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_error_2);
            _jspx_th_liferay$1ui_message_2.setArguments( DLFolderConstants.getNameInvalidCharacters(PropsValues.DL_CHAR_BLACKLIST) );
            _jspx_th_liferay$1ui_message_2.setKey("the-folder-name-cannot-contain-the-following-invalid-characters-x");
            int _jspx_eval_liferay$1ui_message_2 = _jspx_th_liferay$1ui_message_2.doStartTag();
            if (_jspx_th_liferay$1ui_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_2);
              return;
            }
            _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_2);
            out.write("\n\t\t\t</p>\n\t\t");
            int evalDoAfterBody = _jspx_th_liferay$1ui_error_2.doAfterBody();
            errorException = (java.lang.Object) _jspx_page_context.findAttribute("errorException");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_error_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_error_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_2);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_exception.reuse(_jspx_th_liferay$1ui_error_2);
        out.write("\n\n\t\t");
        //  liferay-ui:error
        com.liferay.taglib.ui.ErrorTag _jspx_th_liferay$1ui_error_3 = (com.liferay.taglib.ui.ErrorTag) _jspx_tagPool_liferay$1ui_error_message_exception_nobody.get(com.liferay.taglib.ui.ErrorTag.class);
        _jspx_th_liferay$1ui_error_3.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_error_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_error_3.setException( RequiredFileEntryTypeException.class );
        _jspx_th_liferay$1ui_error_3.setMessage("please-select-a-document-type");
        int _jspx_eval_liferay$1ui_error_3 = _jspx_th_liferay$1ui_error_3.doStartTag();
        if (_jspx_th_liferay$1ui_error_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_3);
          return;
        }
        _jspx_tagPool_liferay$1ui_error_message_exception_nobody.reuse(_jspx_th_liferay$1ui_error_3);
        out.write("\n\n\t\t");
        //  aui:model-context
        com.liferay.taglib.aui.ModelContextTag _jspx_th_aui_model$1context_0 = (com.liferay.taglib.aui.ModelContextTag) _jspx_tagPool_aui_model$1context_model_bean_nobody.get(com.liferay.taglib.aui.ModelContextTag.class);
        _jspx_th_aui_model$1context_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_model$1context_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_model$1context_0.setBean( folder );
        _jspx_th_aui_model$1context_0.setModel( DLFolder.class );
        int _jspx_eval_aui_model$1context_0 = _jspx_th_aui_model$1context_0.doStartTag();
        if (_jspx_th_aui_model$1context_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_model$1context_model_bean_nobody.reuse(_jspx_th_aui_model$1context_0);
          return;
        }
        _jspx_tagPool_aui_model$1context_model_bean_nobody.reuse(_jspx_th_aui_model$1context_0);
        out.write("\n\n\t\t");
        //  aui:fieldset-group
        com.liferay.taglib.aui.FieldsetGroupTag _jspx_th_aui_fieldset$1group_0 = (com.liferay.taglib.aui.FieldsetGroupTag) _jspx_tagPool_aui_fieldset$1group_markupView.get(com.liferay.taglib.aui.FieldsetGroupTag.class);
        _jspx_th_aui_fieldset$1group_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_fieldset$1group_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_fieldset$1group_0.setMarkupView("lexicon");
        int _jspx_eval_aui_fieldset$1group_0 = _jspx_th_aui_fieldset$1group_0.doStartTag();
        if (_jspx_eval_aui_fieldset$1group_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n\t\t\t");
          //  aui:fieldset
          com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_0 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset.get(com.liferay.taglib.aui.FieldsetTag.class);
          _jspx_th_aui_fieldset_0.setPageContext(_jspx_page_context);
          _jspx_th_aui_fieldset_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          int _jspx_eval_aui_fieldset_0 = _jspx_th_aui_fieldset_0.doStartTag();
          if (_jspx_eval_aui_fieldset_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n\t\t\t\t");
            //  c:if
            org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
            _jspx_th_c_if_1.setPageContext(_jspx_page_context);
            _jspx_th_c_if_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_0);
            _jspx_th_c_if_1.setTest( !rootFolder );
            int _jspx_eval_c_if_1 = _jspx_th_c_if_1.doStartTag();
            if (_jspx_eval_c_if_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t\t\t");
                //  c:if
                org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_2 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                _jspx_th_c_if_2.setPageContext(_jspx_page_context);
                _jspx_th_c_if_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                _jspx_th_c_if_2.setTest( folder != null );
                int _jspx_eval_c_if_2 = _jspx_th_c_if_2.doStartTag();
                if (_jspx_eval_c_if_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t\t\t\t\t");
                    //  aui:input
                    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_5 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                    _jspx_th_aui_input_5.setPageContext(_jspx_page_context);
                    _jspx_th_aui_input_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_2);
                    _jspx_th_aui_input_5.setName("parentFolder");
                    _jspx_th_aui_input_5.setType("resource");
                    _jspx_th_aui_input_5.setValue( parentFolderName );
                    int _jspx_eval_aui_input_5 = _jspx_th_aui_input_5.doStartTag();
                    if (_jspx_th_aui_input_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_5);
                      return;
                    }
                    _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_5);
                    out.write("\n\t\t\t\t\t");
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
                out.write("\n\n\t\t\t\t\t");
                //  aui:input
                com.liferay.taglib.aui.InputTag _jspx_th_aui_input_6 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_name_autoFocus_nobody.get(com.liferay.taglib.aui.InputTag.class);
                _jspx_th_aui_input_6.setPageContext(_jspx_page_context);
                _jspx_th_aui_input_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                _jspx_th_aui_input_6.setAutoFocus( windowState.equals(WindowState.MAXIMIZED) );
                _jspx_th_aui_input_6.setName("name");
                int _jspx_eval_aui_input_6 = _jspx_th_aui_input_6.doStartTag();
                if (_jspx_th_aui_input_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_input_name_autoFocus_nobody.reuse(_jspx_th_aui_input_6);
                  return;
                }
                _jspx_tagPool_aui_input_name_autoFocus_nobody.reuse(_jspx_th_aui_input_6);
                out.write("\n\n\t\t\t\t\t");
                //  c:if
                org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_3 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                _jspx_th_c_if_3.setPageContext(_jspx_page_context);
                _jspx_th_c_if_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                _jspx_th_c_if_3.setTest( (parentFolder == null) || parentFolder.isSupportsMetadata() );
                int _jspx_eval_c_if_3 = _jspx_th_c_if_3.doStartTag();
                if (_jspx_eval_c_if_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t\t\t\t\t");
                    if (_jspx_meth_aui_input_7((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_3, _jspx_page_context))
                      return;
                    out.write("\n\t\t\t\t\t");
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
                out.write("\n\t\t\t\t");
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
            out.write("\n\t\t\t");
          }
          if (_jspx_th_aui_fieldset_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_aui_fieldset.reuse(_jspx_th_aui_fieldset_0);
            return;
          }
          _jspx_tagPool_aui_fieldset.reuse(_jspx_th_aui_fieldset_0);
          out.write("\n\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_4 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_4.setPageContext(_jspx_page_context);
          _jspx_th_c_if_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          _jspx_th_c_if_4.setTest( rootFolder || ((folder != null) && (folder.getModel() instanceof DLFolder)) );
          int _jspx_eval_c_if_4 = _jspx_th_c_if_4.doStartTag();
          if (_jspx_eval_c_if_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\n\t\t\t\t");

				DLFolder dlFolder = null;

				long defaultFileEntryTypeId = 0;

				if (!rootFolder) {
					dlFolder = (DLFolder)folder.getModel();

					defaultFileEntryTypeId = dlFolder.getDefaultFileEntryTypeId();
				}

				List<DLFileEntryType> fileEntryTypes = DLFileEntryTypeLocalServiceUtil.getFolderFileEntryTypes(PortalUtil.getCurrentAndAncestorSiteGroupIds(scopeGroupId), folderId, false);

				String headerNames = null;

				if (workflowEnabled) {
					headerNames = "name,workflow,null";
				}
				else {
					headerNames = "name,null";
				}
				
              out.write("\n\n\t\t\t\t");
              //  aui:fieldset
              com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_1 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset_label_helpMessage_collapsible_collapsed.get(com.liferay.taglib.aui.FieldsetTag.class);
              _jspx_th_aui_fieldset_1.setPageContext(_jspx_page_context);
              _jspx_th_aui_fieldset_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_4);
              _jspx_th_aui_fieldset_1.setCollapsed( true );
              _jspx_th_aui_fieldset_1.setCollapsible( true );
              _jspx_th_aui_fieldset_1.setHelpMessage( rootFolder ? "" : "document-type-restrictions-help" );
              _jspx_th_aui_fieldset_1.setLabel( rootFolder ? "" : (workflowEnabled ? "document-type-restrictions-and-workflow" : "document-type-restrictions") );
              int _jspx_eval_aui_fieldset_1 = _jspx_th_aui_fieldset_1.doStartTag();
              if (_jspx_eval_aui_fieldset_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t");
                //  c:if
                org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_5 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                _jspx_th_c_if_5.setPageContext(_jspx_page_context);
                _jspx_th_c_if_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
                _jspx_th_c_if_5.setTest( !rootFolder );
                int _jspx_eval_c_if_5 = _jspx_th_c_if_5.doStartTag();
                if (_jspx_eval_c_if_5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t\t\t\t\t");
                    //  aui:input
                    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_8 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.get(com.liferay.taglib.aui.InputTag.class);
                    _jspx_th_aui_input_8.setPageContext(_jspx_page_context);
                    _jspx_th_aui_input_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
                    _jspx_th_aui_input_8.setChecked( dlFolder.getRestrictionType() == DLFolderConstants.RESTRICTION_TYPE_INHERIT );
                    _jspx_th_aui_input_8.setId("restrictionTypeInherit");
                    _jspx_th_aui_input_8.setLabel( workflowEnabled ? LanguageUtil.format(request, "use-document-type-restrictions-and-workflow-of-the-parent-folder-x", parentFolderName, false) : LanguageUtil.format(request, "use-document-type-restrictions-of-the-parent-folder-x", parentFolderName, false) );
                    _jspx_th_aui_input_8.setName("restrictionType");
                    _jspx_th_aui_input_8.setType("radio");
                    _jspx_th_aui_input_8.setValue( DLFolderConstants.RESTRICTION_TYPE_INHERIT );
                    int _jspx_eval_aui_input_8 = _jspx_th_aui_input_8.doStartTag();
                    if (_jspx_th_aui_input_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.reuse(_jspx_th_aui_input_8);
                      return;
                    }
                    _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.reuse(_jspx_th_aui_input_8);
                    out.write("\n\n\t\t\t\t\t\t");
                    //  aui:input
                    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_9 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.get(com.liferay.taglib.aui.InputTag.class);
                    _jspx_th_aui_input_9.setPageContext(_jspx_page_context);
                    _jspx_th_aui_input_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
                    _jspx_th_aui_input_9.setChecked( dlFolder.getRestrictionType() == DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW );
                    _jspx_th_aui_input_9.setId("restrictionTypeDefined");
                    _jspx_th_aui_input_9.setLabel( workflowEnabled ? LanguageUtil.format(request, "define-specific-document-type-restrictions-and-workflow-for-this-folder-x", folder.getName(), false) : LanguageUtil.format(request, "define-specific-document-type-restrictions-for-this-folder-x", folder.getName(), false) );
                    _jspx_th_aui_input_9.setName("restrictionType");
                    _jspx_th_aui_input_9.setType("radio");
                    _jspx_th_aui_input_9.setValue( DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW );
                    int _jspx_eval_aui_input_9 = _jspx_th_aui_input_9.doStartTag();
                    if (_jspx_th_aui_input_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.reuse(_jspx_th_aui_input_9);
                      return;
                    }
                    _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.reuse(_jspx_th_aui_input_9);
                    out.write("\n\n\t\t\t\t\t\t<div class=\"");
                    out.print( (dlFolder.getRestrictionType() == DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW) ? StringPool.BLANK : "hide" );
                    out.write("\" id=\"");
                    if (_jspx_meth_portlet_namespace_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_5, _jspx_page_context))
                      return;
                    out.write("restrictionTypeDefinedDiv\">\n\t\t\t\t\t\t\t");
                    //  liferay-ui:search-container
                    com.liferay.taglib.ui.SearchContainerTag _jspx_th_liferay$1ui_search$1container_0 = (com.liferay.taglib.ui.SearchContainerTag) _jspx_tagPool_liferay$1ui_search$1container_total_headerNames.get(com.liferay.taglib.ui.SearchContainerTag.class);
                    _jspx_th_liferay$1ui_search$1container_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_search$1container_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
                    _jspx_th_liferay$1ui_search$1container_0.setHeaderNames( headerNames );
                    _jspx_th_liferay$1ui_search$1container_0.setTotal( fileEntryTypes.size() );
                    int _jspx_eval_liferay$1ui_search$1container_0 = _jspx_th_liferay$1ui_search$1container_0.doStartTag();
                    if (_jspx_eval_liferay$1ui_search$1container_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      java.lang.Integer total = null;
                      com.liferay.portal.kernel.dao.search.SearchContainer searchContainer = null;
                      total = (java.lang.Integer) _jspx_page_context.findAttribute("total");
                      searchContainer = (com.liferay.portal.kernel.dao.search.SearchContainer) _jspx_page_context.findAttribute("searchContainer");
                      out.write("\n\t\t\t\t\t\t\t\t");
                      //  liferay-ui:search-container-results
                      java.util.List results = null;
                      java.lang.Integer deprecatedTotal = null;
                      com.liferay.taglib.ui.SearchContainerResultsTag _jspx_th_liferay$1ui_search$1container$1results_0 = (com.liferay.taglib.ui.SearchContainerResultsTag) _jspx_tagPool_liferay$1ui_search$1container$1results_results_nobody.get(com.liferay.taglib.ui.SearchContainerResultsTag.class);
                      _jspx_th_liferay$1ui_search$1container$1results_0.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1ui_search$1container$1results_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container_0);
                      _jspx_th_liferay$1ui_search$1container$1results_0.setResults( fileEntryTypes );
                      int _jspx_eval_liferay$1ui_search$1container$1results_0 = _jspx_th_liferay$1ui_search$1container$1results_0.doStartTag();
                      results = (java.util.List) _jspx_page_context.findAttribute("results");
                      deprecatedTotal = (java.lang.Integer) _jspx_page_context.findAttribute("deprecatedTotal");
                      if (_jspx_th_liferay$1ui_search$1container$1results_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_liferay$1ui_search$1container$1results_results_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1results_0);
                        return;
                      }
                      results = (java.util.List) _jspx_page_context.findAttribute("results");
                      deprecatedTotal = (java.lang.Integer) _jspx_page_context.findAttribute("deprecatedTotal");
                      _jspx_tagPool_liferay$1ui_search$1container$1results_results_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1results_0);
                      out.write("\n\n\t\t\t\t\t\t\t\t");
                      //  liferay-ui:search-container-row
                      com.liferay.taglib.ui.SearchContainerRowTag _jspx_th_liferay$1ui_search$1container$1row_0 = (com.liferay.taglib.ui.SearchContainerRowTag) _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_keyProperty_escapedModel_className.get(com.liferay.taglib.ui.SearchContainerRowTag.class);
                      _jspx_th_liferay$1ui_search$1container$1row_0.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1ui_search$1container$1row_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container_0);
                      _jspx_th_liferay$1ui_search$1container$1row_0.setClassName("com.liferay.document.library.kernel.model.DLFileEntryType");
                      _jspx_th_liferay$1ui_search$1container$1row_0.setEscapedModel( true );
                      _jspx_th_liferay$1ui_search$1container$1row_0.setKeyProperty("fileEntryTypeId");
                      _jspx_th_liferay$1ui_search$1container$1row_0.setModelVar("dlFileEntryType");
                      int _jspx_eval_liferay$1ui_search$1container$1row_0 = _jspx_th_liferay$1ui_search$1container$1row_0.doStartTag();
                      if (_jspx_eval_liferay$1ui_search$1container$1row_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        java.lang.Integer index = null;
                        com.liferay.document.library.kernel.model.DLFileEntryType dlFileEntryType = null;
                        com.liferay.portal.kernel.dao.search.ResultRow row = null;
                        if (_jspx_eval_liferay$1ui_search$1container$1row_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                          out = _jspx_page_context.pushBody();
                          _jspx_th_liferay$1ui_search$1container$1row_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                          _jspx_th_liferay$1ui_search$1container$1row_0.doInitBody();
                        }
                        index = (java.lang.Integer) _jspx_page_context.findAttribute("index");
                        dlFileEntryType = (com.liferay.document.library.kernel.model.DLFileEntryType) _jspx_page_context.findAttribute("dlFileEntryType");
                        row = (com.liferay.portal.kernel.dao.search.ResultRow) _jspx_page_context.findAttribute("row");
                        do {
                          out.write("\n\t\t\t\t\t\t\t\t\t");
                          //  liferay-ui:search-container-column-text
                          com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_0 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                          _jspx_th_liferay$1ui_search$1container$1column$1text_0.setPageContext(_jspx_page_context);
                          _jspx_th_liferay$1ui_search$1container$1column$1text_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1row_0);
                          _jspx_th_liferay$1ui_search$1container$1column$1text_0.setName("name");
                          _jspx_th_liferay$1ui_search$1container$1column$1text_0.setValue( dlFileEntryType.getName(locale) );
                          int _jspx_eval_liferay$1ui_search$1container$1column$1text_0 = _jspx_th_liferay$1ui_search$1container$1column$1text_0.doStartTag();
                          if (_jspx_th_liferay$1ui_search$1container$1column$1text_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                            _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_0);
                            return;
                          }
                          _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_0);
                          out.write("\n\n\t\t\t\t\t\t\t\t\t");
                          //  c:if
                          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_6 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                          _jspx_th_c_if_6.setPageContext(_jspx_page_context);
                          _jspx_th_c_if_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1row_0);
                          _jspx_th_c_if_6.setTest( workflowEnabled );
                          int _jspx_eval_c_if_6 = _jspx_th_c_if_6.doStartTag();
                          if (_jspx_eval_c_if_6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                            do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-text
                              com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_1 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_1.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_6);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_1.setName("workflow");
                              int _jspx_eval_liferay$1ui_search$1container$1column$1text_1 = _jspx_th_liferay$1ui_search$1container$1column$1text_1.doStartTag();
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1ui_search$1container$1column$1text_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_1.doInitBody();
                              }
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              //  aui:select
                              com.liferay.taglib.aui.SelectTag _jspx_th_aui_select_0 = (com.liferay.taglib.aui.SelectTag) _jspx_tagPool_aui_select_title_name_label.get(com.liferay.taglib.aui.SelectTag.class);
                              _jspx_th_aui_select_0.setPageContext(_jspx_page_context);
                              _jspx_th_aui_select_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_1);
                              _jspx_th_aui_select_0.setLabel("");
                              _jspx_th_aui_select_0.setName( "workflowDefinition" + dlFileEntryType.getFileEntryTypeId() );
                              _jspx_th_aui_select_0.setTitle("workflow-definition");
                              int _jspx_eval_aui_select_0 = _jspx_th_aui_select_0.doStartTag();
                              if (_jspx_eval_aui_select_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_aui_select_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_aui_select_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_aui_select_0.doInitBody();
                              }
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_aui_option_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_select_0, _jspx_page_context))
                              return;
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t\t");

												WorkflowDefinitionLink workflowDefinitionLink = WorkflowDefinitionLinkLocalServiceUtil.fetchWorkflowDefinitionLink(company.getCompanyId(), repositoryId, DLFolderConstants.getClassName(), folderId, dlFileEntryType.getFileEntryTypeId(), true);

												for (WorkflowDefinition workflowDefinition : workflowDefinitions) {
													boolean selected = false;

													if ((workflowDefinitionLink != null) && Objects.equals(workflowDefinitionLink.getWorkflowDefinitionName(), workflowDefinition.getName()) && (workflowDefinitionLink.getWorkflowDefinitionVersion() == workflowDefinition.getVersion())) {
														selected = true;
													}
												
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t");
                              //  aui:option
                              com.liferay.taglib.aui.OptionTag _jspx_th_aui_option_1 = (com.liferay.taglib.aui.OptionTag) _jspx_tagPool_aui_option_value_selected_label_nobody.get(com.liferay.taglib.aui.OptionTag.class);
                              _jspx_th_aui_option_1.setPageContext(_jspx_page_context);
                              _jspx_th_aui_option_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_select_0);
                              _jspx_th_aui_option_1.setLabel( HtmlUtil.escapeAttribute(workflowDefinition.getTitle(languageId)) );
                              _jspx_th_aui_option_1.setSelected( selected );
                              _jspx_th_aui_option_1.setValue( HtmlUtil.escapeAttribute(workflowDefinition.getName()) + StringPool.AT + workflowDefinition.getVersion() );
                              int _jspx_eval_aui_option_1 = _jspx_th_aui_option_1.doStartTag();
                              if (_jspx_th_aui_option_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_option_value_selected_label_nobody.reuse(_jspx_th_aui_option_1);
                              return;
                              }
                              _jspx_tagPool_aui_option_value_selected_label_nobody.reuse(_jspx_th_aui_option_1);
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t\t");

												}
												
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_aui_select_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_aui_select_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_aui_select_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_select_title_name_label.reuse(_jspx_th_aui_select_0);
                              return;
                              }
                              _jspx_tagPool_aui_select_title_name_label.reuse(_jspx_th_aui_select_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1column$1text_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1ui_search$1container$1column$1text_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_1);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_1);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
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
                          out.write("\n\n\t\t\t\t\t\t\t\t\t");
                          //  liferay-ui:search-container-column-text
                          com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_2 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                          _jspx_th_liferay$1ui_search$1container$1column$1text_2.setPageContext(_jspx_page_context);
                          _jspx_th_liferay$1ui_search$1container$1column$1text_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1row_0);
                          int _jspx_eval_liferay$1ui_search$1container$1column$1text_2 = _jspx_th_liferay$1ui_search$1container$1column$1text_2.doStartTag();
                          if (_jspx_eval_liferay$1ui_search$1container$1column$1text_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                            if (_jspx_eval_liferay$1ui_search$1container$1column$1text_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1ui_search$1container$1column$1text_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_2.doInitBody();
                            }
                            do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t<a class=\"modify-link\" data-rowId=\"");
                              out.print( dlFileEntryType.getFileEntryTypeId() );
                              out.write("\" href=\"javascript:;\">");
                              out.print( removeFileEntryTypeIcon );
                              out.write("</a>\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1column$1text_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                            } while (true);
                            if (_jspx_eval_liferay$1ui_search$1container$1column$1text_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                          }
                          if (_jspx_th_liferay$1ui_search$1container$1column$1text_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                            _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_2);
                            return;
                          }
                          _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_2);
                          out.write("\n\t\t\t\t\t\t\t\t");
                          int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1row_0.doAfterBody();
                          index = (java.lang.Integer) _jspx_page_context.findAttribute("index");
                          dlFileEntryType = (com.liferay.document.library.kernel.model.DLFileEntryType) _jspx_page_context.findAttribute("dlFileEntryType");
                          row = (com.liferay.portal.kernel.dao.search.ResultRow) _jspx_page_context.findAttribute("row");
                          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                            break;
                        } while (true);
                        if (_jspx_eval_liferay$1ui_search$1container$1row_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                          out = _jspx_page_context.popBody();
                      }
                      if (_jspx_th_liferay$1ui_search$1container$1row_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_keyProperty_escapedModel_className.reuse(_jspx_th_liferay$1ui_search$1container$1row_0);
                        return;
                      }
                      _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_keyProperty_escapedModel_className.reuse(_jspx_th_liferay$1ui_search$1container$1row_0);
                      out.write("\n\n\t\t\t\t\t\t\t\t");
                      //  liferay-ui:search-iterator
                      com.liferay.taglib.ui.SearchIteratorTag _jspx_th_liferay$1ui_search$1iterator_0 = (com.liferay.taglib.ui.SearchIteratorTag) _jspx_tagPool_liferay$1ui_search$1iterator_paginate_nobody.get(com.liferay.taglib.ui.SearchIteratorTag.class);
                      _jspx_th_liferay$1ui_search$1iterator_0.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1ui_search$1iterator_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container_0);
                      _jspx_th_liferay$1ui_search$1iterator_0.setPaginate( false );
                      int _jspx_eval_liferay$1ui_search$1iterator_0 = _jspx_th_liferay$1ui_search$1iterator_0.doStartTag();
                      if (_jspx_th_liferay$1ui_search$1iterator_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_liferay$1ui_search$1iterator_paginate_nobody.reuse(_jspx_th_liferay$1ui_search$1iterator_0);
                        return;
                      }
                      _jspx_tagPool_liferay$1ui_search$1iterator_paginate_nobody.reuse(_jspx_th_liferay$1ui_search$1iterator_0);
                      out.write("\n\t\t\t\t\t\t\t");
                    }
                    if (_jspx_th_liferay$1ui_search$1container_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_search$1container_total_headerNames.reuse(_jspx_th_liferay$1ui_search$1container_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_search$1container_total_headerNames.reuse(_jspx_th_liferay$1ui_search$1container_0);
                    out.write("\n\n\t\t\t\t\t\t\t");
                    //  liferay-ui:icon
                    com.liferay.taglib.ui.IconTag _jspx_th_liferay$1ui_icon_1 = (com.liferay.taglib.ui.IconTag) _jspx_tagPool_liferay$1ui_icon_url_message_markupView_linkCssClass_label_icon_cssClass_nobody.get(com.liferay.taglib.ui.IconTag.class);
                    _jspx_th_liferay$1ui_icon_1.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_icon_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
                    _jspx_th_liferay$1ui_icon_1.setCssClass("modify-link select-file-entry-type");
                    _jspx_th_liferay$1ui_icon_1.setIcon("search");
                    _jspx_th_liferay$1ui_icon_1.setLabel( true );
                    _jspx_th_liferay$1ui_icon_1.setLinkCssClass("btn btn-default");
                    _jspx_th_liferay$1ui_icon_1.setMarkupView("lexicon");
                    _jspx_th_liferay$1ui_icon_1.setMessage("select-document-type");
                    _jspx_th_liferay$1ui_icon_1.setUrl( "javascript:" + renderResponse.getNamespace() + "openFileEntryTypeSelector();" );
                    int _jspx_eval_liferay$1ui_icon_1 = _jspx_th_liferay$1ui_icon_1.doStartTag();
                    if (_jspx_th_liferay$1ui_icon_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_icon_url_message_markupView_linkCssClass_label_icon_cssClass_nobody.reuse(_jspx_th_liferay$1ui_icon_1);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_icon_url_message_markupView_linkCssClass_label_icon_cssClass_nobody.reuse(_jspx_th_liferay$1ui_icon_1);
                    out.write("\n\n\t\t\t\t\t\t\t");
                    //  aui:select
                    com.liferay.taglib.aui.SelectTag _jspx_th_aui_select_1 = (com.liferay.taglib.aui.SelectTag) _jspx_tagPool_aui_select_name_label_helpMessage_cssClass.get(com.liferay.taglib.aui.SelectTag.class);
                    _jspx_th_aui_select_1.setPageContext(_jspx_page_context);
                    _jspx_th_aui_select_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
                    _jspx_th_aui_select_1.setCssClass( !fileEntryTypes.isEmpty() ? "default-document-type" : "default-document-type hide" );
                    _jspx_th_aui_select_1.setHelpMessage("default-document-type-help");
                    _jspx_th_aui_select_1.setLabel("default-document-type");
                    _jspx_th_aui_select_1.setName("defaultFileEntryTypeId");
                    int _jspx_eval_aui_select_1 = _jspx_th_aui_select_1.doStartTag();
                    if (_jspx_eval_aui_select_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      if (_jspx_eval_aui_select_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                        out = _jspx_page_context.pushBody();
                        _jspx_th_aui_select_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                        _jspx_th_aui_select_1.doInitBody();
                      }
                      do {
                        out.write("\n\n\t\t\t\t\t\t\t\t");

								for (DLFileEntryType fileEntryType : fileEntryTypes) {
								
                        out.write("\n\n\t\t\t\t\t\t\t\t\t");
                        //  aui:option
                        com.liferay.taglib.aui.OptionTag _jspx_th_aui_option_2 = (com.liferay.taglib.aui.OptionTag) _jspx_tagPool_aui_option_value_selected_label_id_nobody.get(com.liferay.taglib.aui.OptionTag.class);
                        _jspx_th_aui_option_2.setPageContext(_jspx_page_context);
                        _jspx_th_aui_option_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_select_1);
                        _jspx_th_aui_option_2.setDynamicAttribute(null, "id",  renderResponse.getNamespace() + "defaultFileEntryTypeId-" + fileEntryType.getFileEntryTypeId() );
                        _jspx_th_aui_option_2.setLabel( HtmlUtil.escape(fileEntryType.getName(locale)) );
                        _jspx_th_aui_option_2.setSelected( fileEntryType.getFileEntryTypeId() == defaultFileEntryTypeId );
                        _jspx_th_aui_option_2.setValue( fileEntryType.getFileEntryTypeId() );
                        int _jspx_eval_aui_option_2 = _jspx_th_aui_option_2.doStartTag();
                        if (_jspx_th_aui_option_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_aui_option_value_selected_label_id_nobody.reuse(_jspx_th_aui_option_2);
                          return;
                        }
                        _jspx_tagPool_aui_option_value_selected_label_id_nobody.reuse(_jspx_th_aui_option_2);
                        out.write("\n\n\t\t\t\t\t\t\t\t");

								}
								
                        out.write("\n\n\t\t\t\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_aui_select_1.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                      if (_jspx_eval_aui_select_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                        out = _jspx_page_context.popBody();
                    }
                    if (_jspx_th_aui_select_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_select_name_label_helpMessage_cssClass.reuse(_jspx_th_aui_select_1);
                      return;
                    }
                    _jspx_tagPool_aui_select_name_label_helpMessage_cssClass.reuse(_jspx_th_aui_select_1);
                    out.write("\n\t\t\t\t\t\t</div>\n\t\t\t\t\t");
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
                out.write("\n\n\t\t\t\t\t");
                //  c:if
                org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_7 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                _jspx_th_c_if_7.setPageContext(_jspx_page_context);
                _jspx_th_c_if_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
                _jspx_th_c_if_7.setTest( workflowEnabled );
                int _jspx_eval_c_if_7 = _jspx_th_c_if_7.doStartTag();
                if (_jspx_eval_c_if_7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t\t\t\t\t");
                    //  c:choose
                    org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_0 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                    _jspx_th_c_choose_0.setPageContext(_jspx_page_context);
                    _jspx_th_c_choose_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                    int _jspx_eval_c_choose_0 = _jspx_th_c_choose_0.doStartTag();
                    if (_jspx_eval_c_choose_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      do {
                        out.write("\n\t\t\t\t\t\t\t");
                        //  c:when
                        org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_0 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                        _jspx_th_c_when_0.setPageContext(_jspx_page_context);
                        _jspx_th_c_when_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
                        _jspx_th_c_when_0.setTest( !rootFolder );
                        int _jspx_eval_c_when_0 = _jspx_th_c_when_0.doStartTag();
                        if (_jspx_eval_c_when_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\t\t\t\t\t\t\t\t");
                            //  aui:input
                            com.liferay.taglib.aui.InputTag _jspx_th_aui_input_10 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.get(com.liferay.taglib.aui.InputTag.class);
                            _jspx_th_aui_input_10.setPageContext(_jspx_page_context);
                            _jspx_th_aui_input_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
                            _jspx_th_aui_input_10.setChecked( dlFolder.getRestrictionType() == DLFolderConstants.RESTRICTION_TYPE_WORKFLOW );
                            _jspx_th_aui_input_10.setId("restrictionTypeWorkflow");
                            _jspx_th_aui_input_10.setLabel( LanguageUtil.format(locale, "default-workflow-for-this-folder-x", folder.getName(), false) );
                            _jspx_th_aui_input_10.setName("restrictionType");
                            _jspx_th_aui_input_10.setType("radio");
                            _jspx_th_aui_input_10.setValue( DLFolderConstants.RESTRICTION_TYPE_WORKFLOW );
                            int _jspx_eval_aui_input_10 = _jspx_th_aui_input_10.doStartTag();
                            if (_jspx_th_aui_input_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.reuse(_jspx_th_aui_input_10);
                              return;
                            }
                            _jspx_tagPool_aui_input_value_type_name_label_id_checked_nobody.reuse(_jspx_th_aui_input_10);
                            out.write("\n\t\t\t\t\t\t\t");
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
                        out.write("\n\t\t\t\t\t\t\t");
                        //  c:otherwise
                        org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_0 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                        _jspx_th_c_otherwise_0.setPageContext(_jspx_page_context);
                        _jspx_th_c_otherwise_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
                        int _jspx_eval_c_otherwise_0 = _jspx_th_c_otherwise_0.doStartTag();
                        if (_jspx_eval_c_otherwise_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\t\t\t\t\t\t\t\t");
                            //  aui:input
                            com.liferay.taglib.aui.InputTag _jspx_th_aui_input_11 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                            _jspx_th_aui_input_11.setPageContext(_jspx_page_context);
                            _jspx_th_aui_input_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
                            _jspx_th_aui_input_11.setName("restrictionType");
                            _jspx_th_aui_input_11.setType("hidden");
                            _jspx_th_aui_input_11.setValue( DLFolderConstants.RESTRICTION_TYPE_WORKFLOW );
                            int _jspx_eval_aui_input_11 = _jspx_th_aui_input_11.doStartTag();
                            if (_jspx_th_aui_input_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_11);
                              return;
                            }
                            _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_11);
                            out.write("\n\t\t\t\t\t\t\t");
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
                        out.write("\n\t\t\t\t\t\t");
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
                    out.write("\n\n\t\t\t\t\t\t<div class=\"");
                    out.print( (rootFolder || (dlFolder.getRestrictionType() == DLFolderConstants.RESTRICTION_TYPE_WORKFLOW)) ? StringPool.BLANK : "hide" );
                    out.write("\" id=\"");
                    if (_jspx_meth_portlet_namespace_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_7, _jspx_page_context))
                      return;
                    out.write("restrictionTypeWorkflowDiv\">\n\t\t\t\t\t\t\t");
                    //  aui:select
                    com.liferay.taglib.aui.SelectTag _jspx_th_aui_select_2 = (com.liferay.taglib.aui.SelectTag) _jspx_tagPool_aui_select_name_label.get(com.liferay.taglib.aui.SelectTag.class);
                    _jspx_th_aui_select_2.setPageContext(_jspx_page_context);
                    _jspx_th_aui_select_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                    _jspx_th_aui_select_2.setLabel("default-workflow-for-all-document-types");
                    _jspx_th_aui_select_2.setName( "workflowDefinition" + DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL );
                    int _jspx_eval_aui_select_2 = _jspx_th_aui_select_2.doStartTag();
                    if (_jspx_eval_aui_select_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      if (_jspx_eval_aui_select_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                        out = _jspx_page_context.pushBody();
                        _jspx_th_aui_select_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                        _jspx_th_aui_select_2.doInitBody();
                      }
                      do {
                        out.write("\n\t\t\t\t\t\t\t\t");
                        if (_jspx_meth_aui_option_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_select_2, _jspx_page_context))
                          return;
                        out.write("\n\n\t\t\t\t\t\t\t\t");

								WorkflowDefinitionLink workflowDefinitionLink = WorkflowDefinitionLinkLocalServiceUtil.fetchWorkflowDefinitionLink(company.getCompanyId(), repositoryId, DLFolderConstants.getClassName(), folderId, DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL, true);

								for (WorkflowDefinition workflowDefinition : workflowDefinitions) {
									boolean selected = false;

									if ((workflowDefinitionLink != null) && Objects.equals(workflowDefinitionLink.getWorkflowDefinitionName(), workflowDefinition.getName()) && (workflowDefinitionLink.getWorkflowDefinitionVersion() == workflowDefinition.getVersion())) {
										selected = true;
									}
								
                        out.write("\n\n\t\t\t\t\t\t\t\t\t");
                        //  aui:option
                        com.liferay.taglib.aui.OptionTag _jspx_th_aui_option_4 = (com.liferay.taglib.aui.OptionTag) _jspx_tagPool_aui_option_value_selected_label_nobody.get(com.liferay.taglib.aui.OptionTag.class);
                        _jspx_th_aui_option_4.setPageContext(_jspx_page_context);
                        _jspx_th_aui_option_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_select_2);
                        _jspx_th_aui_option_4.setLabel( HtmlUtil.escapeAttribute(workflowDefinition.getTitle(languageId)) );
                        _jspx_th_aui_option_4.setSelected( selected );
                        _jspx_th_aui_option_4.setValue( HtmlUtil.escapeAttribute(workflowDefinition.getName()) + StringPool.AT + workflowDefinition.getVersion() );
                        int _jspx_eval_aui_option_4 = _jspx_th_aui_option_4.doStartTag();
                        if (_jspx_th_aui_option_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_aui_option_value_selected_label_nobody.reuse(_jspx_th_aui_option_4);
                          return;
                        }
                        _jspx_tagPool_aui_option_value_selected_label_nobody.reuse(_jspx_th_aui_option_4);
                        out.write("\n\n\t\t\t\t\t\t\t\t");

								}
								
                        out.write("\n\n\t\t\t\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_aui_select_2.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                      if (_jspx_eval_aui_select_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                        out = _jspx_page_context.popBody();
                    }
                    if (_jspx_th_aui_select_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_select_name_label.reuse(_jspx_th_aui_select_2);
                      return;
                    }
                    _jspx_tagPool_aui_select_name_label.reuse(_jspx_th_aui_select_2);
                    out.write("\n\t\t\t\t\t\t</div>\n\t\t\t\t\t");
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
                out.write("\n\t\t\t\t");
              }
              if (_jspx_th_aui_fieldset_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_fieldset_label_helpMessage_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_1);
                return;
              }
              _jspx_tagPool_aui_fieldset_label_helpMessage_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_1);
              out.write("\n\t\t\t");
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
          out.write("\n\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_8 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_8.setPageContext(_jspx_page_context);
          _jspx_th_c_if_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset$1group_0);
          _jspx_th_c_if_8.setTest( !rootFolder );
          int _jspx_eval_c_if_8 = _jspx_th_c_if_8.doStartTag();
          if (_jspx_eval_c_if_8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t");
              //  c:if
              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_9 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
              _jspx_th_c_if_9.setPageContext(_jspx_page_context);
              _jspx_th_c_if_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_8);
              _jspx_th_c_if_9.setTest( (parentFolder == null) || parentFolder.isSupportsMetadata() );
              int _jspx_eval_c_if_9 = _jspx_th_c_if_9.doStartTag();
              if (_jspx_eval_c_if_9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t");
                  //  liferay-expando:custom-attributes-available
                  com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag _jspx_th_liferay$1expando_custom$1attributes$1available_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag) _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag.class);
                  _jspx_th_liferay$1expando_custom$1attributes$1available_0.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1expando_custom$1attributes$1available_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_9);
                  _jspx_th_liferay$1expando_custom$1attributes$1available_0.setClassName( DLFolderConstants.getClassName() );
                  int _jspx_eval_liferay$1expando_custom$1attributes$1available_0 = _jspx_th_liferay$1expando_custom$1attributes$1available_0.doStartTag();
                  if (_jspx_eval_liferay$1expando_custom$1attributes$1available_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    out.write("\n\t\t\t\t\t\t");
                    //  aui:fieldset
                    com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_2 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.get(com.liferay.taglib.aui.FieldsetTag.class);
                    _jspx_th_aui_fieldset_2.setPageContext(_jspx_page_context);
                    _jspx_th_aui_fieldset_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1expando_custom$1attributes$1available_0);
                    _jspx_th_aui_fieldset_2.setCollapsed( true );
                    _jspx_th_aui_fieldset_2.setCollapsible( true );
                    _jspx_th_aui_fieldset_2.setLabel("custom-fields");
                    int _jspx_eval_aui_fieldset_2 = _jspx_th_aui_fieldset_2.doStartTag();
                    if (_jspx_eval_aui_fieldset_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      out.write("\n\t\t\t\t\t\t\t");
                      //  liferay-expando:custom-attribute-list
                      com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag _jspx_th_liferay$1expando_custom$1attribute$1list_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag) _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag.class);
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_2);
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setClassName( DLFolderConstants.getClassName() );
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setClassPK( (folder != null) ? folder.getFolderId() : 0 );
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
                    if (_jspx_th_aui_fieldset_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_2);
                      return;
                    }
                    _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_2);
                    out.write("\n\t\t\t\t\t");
                  }
                  if (_jspx_th_liferay$1expando_custom$1attributes$1available_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
                    return;
                  }
                  _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
                  out.write("\n\t\t\t\t");
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
              out.write("\n\n\t\t\t\t");
              //  c:if
              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_10 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
              _jspx_th_c_if_10.setPageContext(_jspx_page_context);
              _jspx_th_c_if_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_8);
              _jspx_th_c_if_10.setTest( (folder == null) && !RepositoryUtil.isExternalRepository(repositoryId) );
              int _jspx_eval_c_if_10 = _jspx_th_c_if_10.doStartTag();
              if (_jspx_eval_c_if_10 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t");
                  //  aui:fieldset
                  com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_3 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.get(com.liferay.taglib.aui.FieldsetTag.class);
                  _jspx_th_aui_fieldset_3.setPageContext(_jspx_page_context);
                  _jspx_th_aui_fieldset_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_10);
                  _jspx_th_aui_fieldset_3.setCollapsed( true );
                  _jspx_th_aui_fieldset_3.setCollapsible( true );
                  _jspx_th_aui_fieldset_3.setLabel("permissions");
                  int _jspx_eval_aui_fieldset_3 = _jspx_th_aui_fieldset_3.doStartTag();
                  if (_jspx_eval_aui_fieldset_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    out.write("\n\t\t\t\t\t\t");
                    //  liferay-ui:input-permissions
                    com.liferay.taglib.ui.InputPermissionsTag _jspx_th_liferay$1ui_input$1permissions_0 = (com.liferay.taglib.ui.InputPermissionsTag) _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.get(com.liferay.taglib.ui.InputPermissionsTag.class);
                    _jspx_th_liferay$1ui_input$1permissions_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_input$1permissions_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_3);
                    _jspx_th_liferay$1ui_input$1permissions_0.setModelName( DLFolderConstants.getClassName() );
                    int _jspx_eval_liferay$1ui_input$1permissions_0 = _jspx_th_liferay$1ui_input$1permissions_0.doStartTag();
                    if (_jspx_th_liferay$1ui_input$1permissions_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.reuse(_jspx_th_liferay$1ui_input$1permissions_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.reuse(_jspx_th_liferay$1ui_input$1permissions_0);
                    out.write("\n\t\t\t\t\t");
                  }
                  if (_jspx_th_aui_fieldset_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_3);
                    return;
                  }
                  _jspx_tagPool_aui_fieldset_label_collapsible_collapsed.reuse(_jspx_th_aui_fieldset_3);
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
              out.write("\n\t\t\t");
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
          out.write("\n\t\t");
        }
        if (_jspx_th_aui_fieldset$1group_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_fieldset$1group_markupView.reuse(_jspx_th_aui_fieldset$1group_0);
          return;
        }
        _jspx_tagPool_aui_fieldset$1group_markupView.reuse(_jspx_th_aui_fieldset$1group_0);
        out.write("\n\n\t\t");
        //  aui:button-row
        com.liferay.taglib.aui.ButtonRowTag _jspx_th_aui_button$1row_0 = (com.liferay.taglib.aui.ButtonRowTag) _jspx_tagPool_aui_button$1row.get(com.liferay.taglib.aui.ButtonRowTag.class);
        _jspx_th_aui_button$1row_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_button$1row_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        int _jspx_eval_aui_button$1row_0 = _jspx_th_aui_button$1row_0.doStartTag();
        if (_jspx_eval_aui_button$1row_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n\t\t\t");
          if (_jspx_meth_aui_button_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_button$1row_0, _jspx_page_context))
            return;
          out.write("\n\n\t\t\t");
          //  aui:button
          com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_1 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_type_href_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
          _jspx_th_aui_button_1.setPageContext(_jspx_page_context);
          _jspx_th_aui_button_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_button$1row_0);
          _jspx_th_aui_button_1.setHref( redirect );
          _jspx_th_aui_button_1.setType("cancel");
          int _jspx_eval_aui_button_1 = _jspx_th_aui_button_1.doStartTag();
          if (_jspx_th_aui_button_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_aui_button_type_href_nobody.reuse(_jspx_th_aui_button_1);
            return;
          }
          _jspx_tagPool_aui_button_type_href_nobody.reuse(_jspx_th_aui_button_1);
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
        _jspx_tagPool_aui_form_onSubmit_name_method_action.reuse(_jspx_th_aui_form_0);
        return;
      }
      _jspx_tagPool_aui_form_onSubmit_name_method_action.reuse(_jspx_th_aui_form_0);
      out.write("\n</div>\n\n");
      //  liferay-util:buffer
      com.liferay.taglib.util.BufferTag _jspx_th_liferay$1util_buffer_1 = (com.liferay.taglib.util.BufferTag) _jspx_tagPool_liferay$1util_buffer_var.get(com.liferay.taglib.util.BufferTag.class);
      _jspx_th_liferay$1util_buffer_1.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1util_buffer_1.setParent(null);
      _jspx_th_liferay$1util_buffer_1.setVar("workflowDefinitionsBuffer");
      int _jspx_eval_liferay$1util_buffer_1 = _jspx_th_liferay$1util_buffer_1.doStartTag();
      if (_jspx_eval_liferay$1util_buffer_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_liferay$1util_buffer_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_liferay$1util_buffer_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_liferay$1util_buffer_1.doInitBody();
        }
        do {
          out.write('\n');
          out.write('	');
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_11 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_11.setPageContext(_jspx_page_context);
          _jspx_th_c_if_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1util_buffer_1);
          _jspx_th_c_if_11.setTest( workflowEnabled );
          int _jspx_eval_c_if_11 = _jspx_th_c_if_11.doStartTag();
          if (_jspx_eval_c_if_11 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t");
              //  aui:select
              com.liferay.taglib.aui.SelectTag _jspx_th_aui_select_3 = (com.liferay.taglib.aui.SelectTag) _jspx_tagPool_aui_select_title_name_label.get(com.liferay.taglib.aui.SelectTag.class);
              _jspx_th_aui_select_3.setPageContext(_jspx_page_context);
              _jspx_th_aui_select_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_11);
              _jspx_th_aui_select_3.setLabel("");
              _jspx_th_aui_select_3.setName("LIFERAY_WORKFLOW_DEFINITION_FILE_ENTRY_TYPE");
              _jspx_th_aui_select_3.setTitle("workflow-definition");
              int _jspx_eval_aui_select_3 = _jspx_th_aui_select_3.doStartTag();
              if (_jspx_eval_aui_select_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                if (_jspx_eval_aui_select_3 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_aui_select_3.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_aui_select_3.doInitBody();
                }
                do {
                  out.write("\n\t\t\t");
                  if (_jspx_meth_aui_option_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_select_3, _jspx_page_context))
                    return;
                  out.write("\n\n\t\t\t");

			for (WorkflowDefinition workflowDefinition : workflowDefinitions) {
			
                  out.write("\n\n\t\t\t\t");
                  //  aui:option
                  com.liferay.taglib.aui.OptionTag _jspx_th_aui_option_6 = (com.liferay.taglib.aui.OptionTag) _jspx_tagPool_aui_option_value_label_nobody.get(com.liferay.taglib.aui.OptionTag.class);
                  _jspx_th_aui_option_6.setPageContext(_jspx_page_context);
                  _jspx_th_aui_option_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_select_3);
                  _jspx_th_aui_option_6.setLabel( HtmlUtil.escapeAttribute(workflowDefinition.getTitle(languageId)) );
                  _jspx_th_aui_option_6.setValue( HtmlUtil.escapeAttribute(workflowDefinition.getName()) + StringPool.AT + workflowDefinition.getVersion() );
                  int _jspx_eval_aui_option_6 = _jspx_th_aui_option_6.doStartTag();
                  if (_jspx_th_aui_option_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_aui_option_value_label_nobody.reuse(_jspx_th_aui_option_6);
                    return;
                  }
                  _jspx_tagPool_aui_option_value_label_nobody.reuse(_jspx_th_aui_option_6);
                  out.write("\n\n\t\t\t");

			}
			
                  out.write("\n\n\t\t");
                  int evalDoAfterBody = _jspx_th_aui_select_3.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
                if (_jspx_eval_aui_select_3 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                  out = _jspx_page_context.popBody();
              }
              if (_jspx_th_aui_select_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_select_title_name_label.reuse(_jspx_th_aui_select_3);
                return;
              }
              _jspx_tagPool_aui_select_title_name_label.reuse(_jspx_th_aui_select_3);
              out.write('\n');
              out.write('	');
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
          out.write('\n');
          int evalDoAfterBody = _jspx_th_liferay$1util_buffer_1.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_liferay$1util_buffer_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_liferay$1util_buffer_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1util_buffer_var.reuse(_jspx_th_liferay$1util_buffer_1);
        return;
      }
      _jspx_tagPool_liferay$1util_buffer_var.reuse(_jspx_th_liferay$1util_buffer_1);
      java.lang.String workflowDefinitionsBuffer = null;
      workflowDefinitionsBuffer = (java.lang.String) _jspx_page_context.findAttribute("workflowDefinitionsBuffer");
      out.write('\n');
      out.write('\n');
      //  aui:script
      com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_0 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script.get(com.liferay.taglib.aui.ScriptTag.class);
      _jspx_th_aui_script_0.setPageContext(_jspx_page_context);
      _jspx_th_aui_script_0.setParent(null);
      int _jspx_eval_aui_script_0 = _jspx_th_aui_script_0.doStartTag();
      if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_aui_script_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_aui_script_0.doInitBody();
        }
        do {
          out.write("\n\tvar ");
          if (_jspx_meth_portlet_namespace_2((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("documentTypesChanged = false;\n\n\tfunction ");
          if (_jspx_meth_portlet_namespace_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("openFileEntryTypeSelector() {\n\t\tvar searchContainer = Liferay.SearchContainer.get(\n\t\t\t'");
          if (_jspx_meth_portlet_namespace_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("dlFileEntryTypesSearchContainer'\n\t\t);\n\n\t\tvar searchContainerData = searchContainer.getData();\n\n\t\tif (!searchContainerData.length) {\n\t\t\tsearchContainerData = [];\n\t\t}\n\t\telse {\n\t\t\tsearchContainerData = searchContainerData.split(',');\n\t\t}\n\n\t\tLiferay.Util.selectEntity(\n\t\t\t{\n\t\t\t\tdialog: {\n\t\t\t\t\tconstrain: true,\n\t\t\t\t\tdestroyOnHide: true,\n\t\t\t\t\tmodal: true,\n\t\t\t\t\twidth: 1024\n\t\t\t\t},\n\t\t\t\teventName: '");
          if (_jspx_meth_portlet_namespace_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("selectFileEntryType',\n\t\t\t\tid: '");
          if (_jspx_meth_portlet_namespace_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("fileEntryTypeSelector',\n\t\t\t\tselectedData: searchContainerData,\n\t\t\t\ttitle: '");
          out.print( UnicodeLanguageUtil.get(request, "document-types") );
          out.write("',\n\t\t\t\turi:\n\t\t\t\t\t'");
          //  portlet:renderURL
          com.liferay.taglib.portlet.RenderURLTag _jspx_th_portlet_renderURL_0 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_portlet_renderURL_windowState.get(com.liferay.taglib.portlet.RenderURLTag.class);
          _jspx_th_portlet_renderURL_0.setPageContext(_jspx_page_context);
          _jspx_th_portlet_renderURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
          _jspx_th_portlet_renderURL_0.setWindowState( LiferayWindowState.POP_UP.toString() );
          int _jspx_eval_portlet_renderURL_0 = _jspx_th_portlet_renderURL_0.doStartTag();
          if (_jspx_eval_portlet_renderURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_meth_portlet_param_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_renderURL_0, _jspx_page_context))
              return;
            //  portlet:param
            com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_2 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
            _jspx_th_portlet_param_2.setPageContext(_jspx_page_context);
            _jspx_th_portlet_param_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
            _jspx_th_portlet_param_2.setName("includeBasicFileEntryType");
            _jspx_th_portlet_param_2.setValue( Boolean.TRUE.toString() );
            int _jspx_eval_portlet_param_2 = _jspx_th_portlet_param_2.doStartTag();
            if (_jspx_th_portlet_param_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_2);
              return;
            }
            _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_2);
          }
          if (_jspx_th_portlet_renderURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_portlet_renderURL_windowState.reuse(_jspx_th_portlet_renderURL_0);
            return;
          }
          _jspx_tagPool_portlet_renderURL_windowState.reuse(_jspx_th_portlet_renderURL_0);
          out.write("'\n\t\t\t},\n\t\t\tfunction(event) {\n\t\t\t\t");
          if (_jspx_meth_portlet_namespace_7((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("selectFileEntryType(\n\t\t\t\t\tevent.entityid,\n\t\t\t\t\tevent.entityname\n\t\t\t\t);\n\t\t\t}\n\t\t);\n\t}\n\n\tfunction ");
          if (_jspx_meth_portlet_namespace_8((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("savePage() {\n\t\tvar message =\n\t\t\t'");
          out.print( UnicodeLanguageUtil.get(request, workflowEnabled ? "change-document-types-and-workflow-message" : "change-document-types-message") );
          out.write("';\n\n\t\tvar submit = true;\n\n\t\tif (");
          if (_jspx_meth_portlet_namespace_9((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("documentTypesChanged) {\n\t\t\tif (!confirm(message)) {\n\t\t\t\tsubmit = false;\n\t\t\t}\n\t\t}\n\n\t\tif (submit) {\n\t\t\tsubmitForm(document.");
          if (_jspx_meth_portlet_namespace_10((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("fm);\n\t\t}\n\t}\n\n\tLiferay.provide(\n\t\twindow,\n\t\t'");
          if (_jspx_meth_portlet_namespace_11((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("selectFileEntryType',\n\t\tfunction(fileEntryTypeId, fileEntryTypeName) {\n\t\t\tvar A = AUI();\n\n\t\t\tvar searchContainer = Liferay.SearchContainer.get(\n\t\t\t\t'");
          if (_jspx_meth_portlet_namespace_12((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("dlFileEntryTypesSearchContainer'\n\t\t\t);\n\n\t\t\tvar fileEntryTypeLink =\n\t\t\t\t'<a class=\"modify-link\" data-rowId=\"' +\n\t\t\t\tfileEntryTypeId +\n\t\t\t\t'\" href=\"javascript:;\">");
          out.print( UnicodeFormatter.toString(removeFileEntryTypeIcon) );
          out.write("</a>';\n\n\t\t\t");
          //  c:choose
          org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_1 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
          _jspx_th_c_choose_1.setPageContext(_jspx_page_context);
          _jspx_th_c_choose_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
          int _jspx_eval_c_choose_1 = _jspx_th_c_choose_1.doStartTag();
          if (_jspx_eval_c_choose_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t");
              //  c:when
              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_1 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
              _jspx_th_c_when_1.setPageContext(_jspx_page_context);
              _jspx_th_c_when_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
              _jspx_th_c_when_1.setTest( workflowEnabled );
              int _jspx_eval_c_when_1 = _jspx_th_c_when_1.doStartTag();
              if (_jspx_eval_c_when_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\tvar restrictionTypeWorkflow = A.one(\n\t\t\t\t\t\t'#");
                  if (_jspx_meth_portlet_namespace_13((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_1, _jspx_page_context))
                    return;
                  out.write("restrictionTypeWorkflow'\n\t\t\t\t\t);\n\n\t\t\t\t\trestrictionTypeWorkflow.hide();\n\n\t\t\t\t\tvar workflowDefinitions =\n\t\t\t\t\t\t'");
                  out.print( UnicodeFormatter.toString(workflowDefinitionsBuffer) );
                  out.write("';\n\n\t\t\t\t\tworkflowDefinitions = workflowDefinitions.replace(\n\t\t\t\t\t\t/LIFERAY_WORKFLOW_DEFINITION_FILE_ENTRY_TYPE/g,\n\t\t\t\t\t\t'workflowDefinition' + fileEntryTypeId\n\t\t\t\t\t);\n\n\t\t\t\t\t");
                  if (_jspx_meth_portlet_namespace_14((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_1, _jspx_page_context))
                    return;
                  out.write("documentTypesChanged = true;\n\n\t\t\t\t\tsearchContainer.addRow(\n\t\t\t\t\t\t[fileEntryTypeName, workflowDefinitions, fileEntryTypeLink],\n\t\t\t\t\t\tfileEntryTypeId\n\t\t\t\t\t);\n\t\t\t\t");
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
              out.write("\n\t\t\t\t");
              if (_jspx_meth_c_otherwise_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_choose_1, _jspx_page_context))
                return;
              out.write("\n\t\t\t");
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
          out.write("\n\n\t\t\tsearchContainer.updateDataStore();\n\n\t\t\tvar select = A.one('#");
          if (_jspx_meth_portlet_namespace_15((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("defaultFileEntryTypeId');\n\n\t\t\tvar selectContainer = A.one(\n\t\t\t\t'#");
          if (_jspx_meth_portlet_namespace_16((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("restrictionTypeDefinedDiv .default-document-type'\n\t\t\t);\n\n\t\t\tselectContainer.show();\n\n\t\t\tvar option = A.Node.create(\n\t\t\t\t'<option id=\"");
          if (_jspx_meth_portlet_namespace_17((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("defaultFileEntryTypeId-' +\n\t\t\t\t\tfileEntryTypeId +\n\t\t\t\t\t'\" value=\"' +\n\t\t\t\t\tfileEntryTypeId +\n\t\t\t\t\t'\">' +\n\t\t\t\t\tfileEntryTypeName +\n\t\t\t\t\t'</option>'\n\t\t\t);\n\n\t\t\tselect.show();\n\n\t\t\tselect.append(option);\n\t\t},\n\t\t['liferay-search-container']\n\t);\n\n\tLiferay.Util.toggleRadio('");
          if (_jspx_meth_portlet_namespace_18((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("restrictionTypeInherit', '', [\n\t\t'");
          if (_jspx_meth_portlet_namespace_19((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("restrictionTypeDefinedDiv',\n\t\t'");
          if (_jspx_meth_portlet_namespace_20((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("restrictionTypeWorkflowDiv'\n\t]);\n\tLiferay.Util.toggleRadio(\n\t\t'");
          if (_jspx_meth_portlet_namespace_21((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("restrictionTypeDefined',\n\t\t'");
          if (_jspx_meth_portlet_namespace_22((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("restrictionTypeDefinedDiv',\n\t\t'");
          if (_jspx_meth_portlet_namespace_23((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("restrictionTypeWorkflowDiv'\n\t);\n\n\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_12 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_12.setPageContext(_jspx_page_context);
          _jspx_th_c_if_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
          _jspx_th_c_if_12.setTest( !rootFolder );
          int _jspx_eval_c_if_12 = _jspx_th_c_if_12.doStartTag();
          if (_jspx_eval_c_if_12 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\tLiferay.Util.toggleRadio(\n\t\t\t'");
              if (_jspx_meth_portlet_namespace_24((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_12, _jspx_page_context))
                return;
              out.write("restrictionTypeWorkflow',\n\t\t\t'");
              if (_jspx_meth_portlet_namespace_25((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_12, _jspx_page_context))
                return;
              out.write("restrictionTypeWorkflowDiv',\n\t\t\t'");
              if (_jspx_meth_portlet_namespace_26((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_12, _jspx_page_context))
                return;
              out.write("restrictionTypeDefinedDiv'\n\t\t);\n\t");
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
          out.write('\n');
          int evalDoAfterBody = _jspx_th_aui_script_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_aui_script_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_aui_script.reuse(_jspx_th_aui_script_0);
        return;
      }
      _jspx_tagPool_aui_script.reuse(_jspx_th_aui_script_0);
      out.write('\n');
      out.write('\n');
      //  aui:script
      com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_1 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script_use.get(com.liferay.taglib.aui.ScriptTag.class);
      _jspx_th_aui_script_1.setPageContext(_jspx_page_context);
      _jspx_th_aui_script_1.setParent(null);
      _jspx_th_aui_script_1.setUse("liferay-search-container");
      int _jspx_eval_aui_script_1 = _jspx_th_aui_script_1.doStartTag();
      if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_aui_script_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_aui_script_1.doInitBody();
        }
        do {
          out.write("\n\tvar searchContainer = Liferay.SearchContainer.get(\n\t\t'");
          if (_jspx_meth_portlet_namespace_27((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
            return;
          out.write("dlFileEntryTypesSearchContainer'\n\t);\n\n\tsearchContainer.get('contentBox').delegate(\n\t\t'click',\n\t\tfunction(event) {\n\t\t\tvar A = AUI();\n\n\t\t\tvar link = event.currentTarget;\n\n\t\t\tvar tr = link.ancestor('tr');\n\n\t\t\tsearchContainer.deleteRow(tr, link.getAttribute('data-rowId'));\n\n\t\t\tA.one(\n\t\t\t\t'#");
          if (_jspx_meth_portlet_namespace_28((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
            return;
          out.write("defaultFileEntryTypeId-' +\n\t\t\t\t\tlink.getAttribute('data-rowId')\n\t\t\t).remove();\n\n\t\t\t");
          if (_jspx_meth_portlet_namespace_29((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
            return;
          out.write("documentTypesChanged = true;\n\n\t\t\tvar select = A.one(\n\t\t\t\t'#");
          out.print( liferayPortletResponse.getNamespace() + "workflowDefinition" + DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL );
          out.write("'\n\t\t\t);\n\n\t\t\tvar selectContainer = A.one(\n\t\t\t\t'#");
          if (_jspx_meth_portlet_namespace_30((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
            return;
          out.write("restrictionTypeWorkflow'\n\t\t\t);\n\n\t\t\tvar fileEntryTypesCount = select.get('children').size();\n\n\t\t\tif (fileEntryTypesCount == 0) {\n\t\t\t\tselectContainer.hide();\n\n\t\t\t\tvar restrictionTypeWorkflow = A.one(\n\t\t\t\t\t'#");
          if (_jspx_meth_portlet_namespace_31((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
            return;
          out.write("restrictionTypeWorkflow'\n\t\t\t\t);\n\n\t\t\t\trestrictionTypeWorkflow.show();\n\t\t\t}\n\t\t\telse {\n\t\t\t\tselectContainer.show();\n\t\t\t}\n\t\t},\n\t\t'.modify-link'\n\t);\n");
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
      out.write('\n');
      out.write('\n');

if (!rootFolder && (folder == null)) {
	DLBreadcrumbUtil.addPortletBreadcrumbEntries(parentFolderId, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-folder"), currentURL);
}
else {
	DLBreadcrumbUtil.addPortletBreadcrumbEntries(folderId, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
}

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

  private boolean _jspx_meth_portlet_param_0(javax.servlet.jsp.tagext.JspTag _jspx_th_portlet_actionURL_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_0 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_actionURL_0);
    _jspx_th_portlet_param_0.setName("mvcRenderCommandName");
    _jspx_th_portlet_param_0.setValue("/document_library/edit_folder");
    int _jspx_eval_portlet_param_0 = _jspx_th_portlet_param_0.doStartTag();
    if (_jspx_th_portlet_param_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
    return false;
  }

  private boolean _jspx_meth_aui_input_7(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_7 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_7.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_3);
    _jspx_th_aui_input_7.setName("description");
    int _jspx_eval_aui_input_7 = _jspx_th_aui_input_7.doStartTag();
    if (_jspx_th_aui_input_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_name_nobody.reuse(_jspx_th_aui_input_7);
      return true;
    }
    _jspx_tagPool_aui_input_name_nobody.reuse(_jspx_th_aui_input_7);
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

  private boolean _jspx_meth_aui_option_0(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_select_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:option
    com.liferay.taglib.aui.OptionTag _jspx_th_aui_option_0 = (com.liferay.taglib.aui.OptionTag) _jspx_tagPool_aui_option_value_label_nobody.get(com.liferay.taglib.aui.OptionTag.class);
    _jspx_th_aui_option_0.setPageContext(_jspx_page_context);
    _jspx_th_aui_option_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_select_0);
    _jspx_th_aui_option_0.setLabel(new String("no-workflow"));
    _jspx_th_aui_option_0.setValue(new String(""));
    int _jspx_eval_aui_option_0 = _jspx_th_aui_option_0.doStartTag();
    if (_jspx_th_aui_option_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_option_value_label_nobody.reuse(_jspx_th_aui_option_0);
      return true;
    }
    _jspx_tagPool_aui_option_value_label_nobody.reuse(_jspx_th_aui_option_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_7, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_1 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_1.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
    int _jspx_eval_portlet_namespace_1 = _jspx_th_portlet_namespace_1.doStartTag();
    if (_jspx_th_portlet_namespace_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
    return false;
  }

  private boolean _jspx_meth_aui_option_3(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_select_2, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:option
    com.liferay.taglib.aui.OptionTag _jspx_th_aui_option_3 = (com.liferay.taglib.aui.OptionTag) _jspx_tagPool_aui_option_value_label_nobody.get(com.liferay.taglib.aui.OptionTag.class);
    _jspx_th_aui_option_3.setPageContext(_jspx_page_context);
    _jspx_th_aui_option_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_select_2);
    _jspx_th_aui_option_3.setLabel(new String("no-workflow"));
    _jspx_th_aui_option_3.setValue(new String(""));
    int _jspx_eval_aui_option_3 = _jspx_th_aui_option_3.doStartTag();
    if (_jspx_th_aui_option_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_option_value_label_nobody.reuse(_jspx_th_aui_option_3);
      return true;
    }
    _jspx_tagPool_aui_option_value_label_nobody.reuse(_jspx_th_aui_option_3);
    return false;
  }

  private boolean _jspx_meth_aui_button_0(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_button$1row_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:button
    com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_0 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_type_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
    _jspx_th_aui_button_0.setPageContext(_jspx_page_context);
    _jspx_th_aui_button_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_button$1row_0);
    _jspx_th_aui_button_0.setType("submit");
    int _jspx_eval_aui_button_0 = _jspx_th_aui_button_0.doStartTag();
    if (_jspx_th_aui_button_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_button_type_nobody.reuse(_jspx_th_aui_button_0);
      return true;
    }
    _jspx_tagPool_aui_button_type_nobody.reuse(_jspx_th_aui_button_0);
    return false;
  }

  private boolean _jspx_meth_aui_option_5(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_select_3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:option
    com.liferay.taglib.aui.OptionTag _jspx_th_aui_option_5 = (com.liferay.taglib.aui.OptionTag) _jspx_tagPool_aui_option_value_label_nobody.get(com.liferay.taglib.aui.OptionTag.class);
    _jspx_th_aui_option_5.setPageContext(_jspx_page_context);
    _jspx_th_aui_option_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_select_3);
    _jspx_th_aui_option_5.setLabel(new String("no-workflow"));
    _jspx_th_aui_option_5.setValue(new String(""));
    int _jspx_eval_aui_option_5 = _jspx_th_aui_option_5.doStartTag();
    if (_jspx_th_aui_option_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_option_value_label_nobody.reuse(_jspx_th_aui_option_5);
      return true;
    }
    _jspx_tagPool_aui_option_value_label_nobody.reuse(_jspx_th_aui_option_5);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_2(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_2 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_2.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_2 = _jspx_th_portlet_namespace_2.doStartTag();
    if (_jspx_th_portlet_namespace_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_2);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_2);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_3(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_3 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_3.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_3 = _jspx_th_portlet_namespace_3.doStartTag();
    if (_jspx_th_portlet_namespace_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_3);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_3);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_4(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_4 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_4.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
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

  private boolean _jspx_meth_portlet_param_1(javax.servlet.jsp.tagext.JspTag _jspx_th_portlet_renderURL_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_1 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_1.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
    _jspx_th_portlet_param_1.setName("mvcPath");
    _jspx_th_portlet_param_1.setValue("/document_library/select_restricted_file_entry_type.jsp");
    int _jspx_eval_portlet_param_1 = _jspx_th_portlet_param_1.doStartTag();
    if (_jspx_th_portlet_param_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
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

  private boolean _jspx_meth_portlet_namespace_11(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_11 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_11.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_11 = _jspx_th_portlet_namespace_11.doStartTag();
    if (_jspx_th_portlet_namespace_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_11);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_11);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_12(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_12 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_12.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_12 = _jspx_th_portlet_namespace_12.doStartTag();
    if (_jspx_th_portlet_namespace_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_12);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_12);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_13(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_13 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_13.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_13.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
    int _jspx_eval_portlet_namespace_13 = _jspx_th_portlet_namespace_13.doStartTag();
    if (_jspx_th_portlet_namespace_13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_13);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_13);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_14(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_14 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_14.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_14.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
    int _jspx_eval_portlet_namespace_14 = _jspx_th_portlet_namespace_14.doStartTag();
    if (_jspx_th_portlet_namespace_14.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_14);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_14);
    return false;
  }

  private boolean _jspx_meth_c_otherwise_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_choose_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:otherwise
    org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_1 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
    _jspx_th_c_otherwise_1.setPageContext(_jspx_page_context);
    _jspx_th_c_otherwise_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
    int _jspx_eval_c_otherwise_1 = _jspx_th_c_otherwise_1.doStartTag();
    if (_jspx_eval_c_otherwise_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\n\t\t\t\t\tsearchContainer.addRow(\n\t\t\t\t\t\t[fileEntryTypeName, fileEntryTypeLink],\n\t\t\t\t\t\tfileEntryTypeId\n\t\t\t\t\t);\n\t\t\t\t");
        int evalDoAfterBody = _jspx_th_c_otherwise_1.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_otherwise_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_1);
      return true;
    }
    _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_1);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_15(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_15 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_15.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_15.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_15 = _jspx_th_portlet_namespace_15.doStartTag();
    if (_jspx_th_portlet_namespace_15.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_15);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_15);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_16(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_16 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_16.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_16.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_16 = _jspx_th_portlet_namespace_16.doStartTag();
    if (_jspx_th_portlet_namespace_16.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_16);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_16);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_17(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_17 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_17.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_17.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_17 = _jspx_th_portlet_namespace_17.doStartTag();
    if (_jspx_th_portlet_namespace_17.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_17);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_17);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_18(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_18 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_18.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_18.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_18 = _jspx_th_portlet_namespace_18.doStartTag();
    if (_jspx_th_portlet_namespace_18.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_18);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_18);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_19(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_19 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_19.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_19.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_19 = _jspx_th_portlet_namespace_19.doStartTag();
    if (_jspx_th_portlet_namespace_19.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_19);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_19);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_20(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_20 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_20.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_20.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_20 = _jspx_th_portlet_namespace_20.doStartTag();
    if (_jspx_th_portlet_namespace_20.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_20);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_20);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_21(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_21 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_21.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_21.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_21 = _jspx_th_portlet_namespace_21.doStartTag();
    if (_jspx_th_portlet_namespace_21.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_21);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_21);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_22(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_22 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_22.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_22.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_22 = _jspx_th_portlet_namespace_22.doStartTag();
    if (_jspx_th_portlet_namespace_22.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_22);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_22);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_23(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_23 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_23.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_23.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_23 = _jspx_th_portlet_namespace_23.doStartTag();
    if (_jspx_th_portlet_namespace_23.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_23);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_23);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_24(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_12, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_24 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_24.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_24.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_12);
    int _jspx_eval_portlet_namespace_24 = _jspx_th_portlet_namespace_24.doStartTag();
    if (_jspx_th_portlet_namespace_24.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_24);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_24);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_25(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_12, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_25 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_25.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_25.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_12);
    int _jspx_eval_portlet_namespace_25 = _jspx_th_portlet_namespace_25.doStartTag();
    if (_jspx_th_portlet_namespace_25.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_25);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_25);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_26(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_12, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_26 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_26.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_26.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_12);
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

  private boolean _jspx_meth_portlet_namespace_28(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_28 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_28.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_28.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_28 = _jspx_th_portlet_namespace_28.doStartTag();
    if (_jspx_th_portlet_namespace_28.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_28);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_28);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_29(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_29 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_29.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_29.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_29 = _jspx_th_portlet_namespace_29.doStartTag();
    if (_jspx_th_portlet_namespace_29.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_29);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_29);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_30(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_30 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_30.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_30.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_30 = _jspx_th_portlet_namespace_30.doStartTag();
    if (_jspx_th_portlet_namespace_30.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_30);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_30);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_31(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_31 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_31.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_31.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_31 = _jspx_th_portlet_namespace_31.doStartTag();
    if (_jspx_th_portlet_namespace_31.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_31);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_31);
    return false;
  }
}
