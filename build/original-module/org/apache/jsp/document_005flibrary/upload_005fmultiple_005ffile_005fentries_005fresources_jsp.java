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

public final class upload_005fmultiple_005ffile_005fentries_005fresources_jsp extends org.apache.jasper.runtime.HttpJspBase
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

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_actionURL_var_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_form_onSubmit_name_method_action;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_param_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_model$1context_model_bean_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_cssClass_collapsible;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ddm_html_synchronousFormSubmission_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_icon$1menu_showWhenSingleIcon_message_id_direction;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_button_value_type_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_icon_url_method_message_id_cssClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_fieldset;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_namespace_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_value_type_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_field$1wrapper_label_cssClass;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classPK_classNameId_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_script_require_position;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_id_extended;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_portlet_actionURL_var_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_form_onSubmit_name_method_action = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_param_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_model$1context_model_bean_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_cssClass_collapsible = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ddm_html_synchronousFormSubmission_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_icon$1menu_showWhenSingleIcon_message_id_direction = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_button_value_type_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_icon_url_method_message_id_cssClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_fieldset = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_namespace_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_value_type_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_field$1wrapper_label_cssClass = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classPK_classNameId_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_script_require_position = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_id_extended = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_portlet_actionURL_var_name.release();
    _jspx_tagPool_aui_form_onSubmit_name_method_action.release();
    _jspx_tagPool_portlet_param_value_name_nobody.release();
    _jspx_tagPool_aui_model$1context_model_bean_nobody.release();
    _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_cssClass_collapsible.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.release();
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.release();
    _jspx_tagPool_liferay$1ddm_html_synchronousFormSubmission_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody.release();
    _jspx_tagPool_liferay$1ui_icon$1menu_showWhenSingleIcon_message_id_direction.release();
    _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass.release();
    _jspx_tagPool_aui_button_value_type_nobody.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody.release();
    _jspx_tagPool_liferay$1ui_icon_url_method_message_id_cssClass_nobody.release();
    _jspx_tagPool_aui_fieldset.release();
    _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters.release();
    _jspx_tagPool_portlet_namespace_nobody.release();
    _jspx_tagPool_liferay$1ui_message_key_nobody.release();
    _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody.release();
    _jspx_tagPool_aui_input_value_type_name_nobody.release();
    _jspx_tagPool_aui_field$1wrapper_label_cssClass.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classPK_classNameId_nobody.release();
    _jspx_tagPool_aui_input_name_nobody.release();
    _jspx_tagPool_aui_script_require_position.release();
    _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_id_extended.release();
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

FileEntry fileEntry = (FileEntry)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY);

long groupId = BeanParamUtil.getLong(fileEntry, request, "groupId");

long repositoryId = BeanParamUtil.getLong(fileEntry, request, "repositoryId");

if (repositoryId <= 0) {

	// <liferay-ui:asset_add_button /> only passes in groupId

	repositoryId = BeanParamUtil.getLong(fileEntry, request, "groupId");
}

long folderId = BeanParamUtil.getLong(fileEntry, request, "folderId");

Folder folder = null;

if (folderId > 0) {
	folder = DLAppLocalServiceUtil.getFolder(folderId);
}

boolean inherited = true;

if ((folder != null) && (folder.getModel() instanceof DLFolder)) {
	DLFolder dlFolder = (DLFolder)folder.getModel();

	if (dlFolder.getRestrictionType() == DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW) {
		inherited = false;
	}
}

List<DLFileEntryType> fileEntryTypes = DLFileEntryTypeServiceUtil.getFolderFileEntryTypes(PortalUtil.getCurrentAndAncestorSiteGroupIds(scopeGroupId), folderId, inherited);

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

DLFileEntryType fileEntryType = null;

if ((fileEntryTypeId == -1) && !fileEntryTypes.isEmpty()) {
	fileEntryType = fileEntryTypes.get(0);

	fileEntryTypeId = fileEntryType.getFileEntryTypeId();
}

if (fileEntryTypeId > 0) {
	fileEntryType = DLFileEntryTypeLocalServiceUtil.getFileEntryType(fileEntryTypeId);
}

long assetClassPK = 0;

DLEditFileEntryDisplayContext dlEditFileEntryDisplayContext = null;

if (fileEntry == null) {
	dlEditFileEntryDisplayContext = dlDisplayContextProvider.getDLEditFileEntryDisplayContext(request, response, fileEntryType);
}
else {
	dlEditFileEntryDisplayContext = dlDisplayContextProvider.getDLEditFileEntryDisplayContext(request, response, fileEntry);
}

      out.write('\n');
      out.write('\n');
      //  portlet:actionURL
      com.liferay.taglib.portlet.ActionURLTag _jspx_th_portlet_actionURL_0 = (com.liferay.taglib.portlet.ActionURLTag) _jspx_tagPool_portlet_actionURL_var_name.get(com.liferay.taglib.portlet.ActionURLTag.class);
      _jspx_th_portlet_actionURL_0.setPageContext(_jspx_page_context);
      _jspx_th_portlet_actionURL_0.setParent(null);
      _jspx_th_portlet_actionURL_0.setName("/document_library/upload_multiple_file_entries");
      _jspx_th_portlet_actionURL_0.setVar("uploadMultipleFileEntriesURL");
      int _jspx_eval_portlet_actionURL_0 = _jspx_th_portlet_actionURL_0.doStartTag();
      if (_jspx_eval_portlet_actionURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        //  portlet:param
        com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_0 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
        _jspx_th_portlet_param_0.setPageContext(_jspx_page_context);
        _jspx_th_portlet_param_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_actionURL_0);
        _jspx_th_portlet_param_0.setName("redirect");
        _jspx_th_portlet_param_0.setValue( redirect );
        int _jspx_eval_portlet_param_0 = _jspx_th_portlet_param_0.doStartTag();
        if (_jspx_th_portlet_param_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
          return;
        }
        _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
        out.write('\n');
      }
      if (_jspx_th_portlet_actionURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_portlet_actionURL_var_name.reuse(_jspx_th_portlet_actionURL_0);
        return;
      }
      _jspx_tagPool_portlet_actionURL_var_name.reuse(_jspx_th_portlet_actionURL_0);
      java.lang.String uploadMultipleFileEntriesURL = null;
      uploadMultipleFileEntriesURL = (java.lang.String) _jspx_page_context.findAttribute("uploadMultipleFileEntriesURL");
      out.write('\n');
      out.write('\n');
      //  aui:form
      com.liferay.taglib.aui.FormTag _jspx_th_aui_form_0 = (com.liferay.taglib.aui.FormTag) _jspx_tagPool_aui_form_onSubmit_name_method_action.get(com.liferay.taglib.aui.FormTag.class);
      _jspx_th_aui_form_0.setPageContext(_jspx_page_context);
      _jspx_th_aui_form_0.setParent(null);
      _jspx_th_aui_form_0.setAction( uploadMultipleFileEntriesURL );
      _jspx_th_aui_form_0.setMethod("post");
      _jspx_th_aui_form_0.setName("fm2");
      _jspx_th_aui_form_0.setOnSubmit( "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "updateMultipleFiles();" );
      int _jspx_eval_aui_form_0 = _jspx_th_aui_form_0.doStartTag();
      if (_jspx_eval_aui_form_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_0 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_0.setName( Constants.CMD );
        _jspx_th_aui_input_0.setType("hidden");
        _jspx_th_aui_input_0.setValue( Constants.ADD_MULTIPLE );
        int _jspx_eval_aui_input_0 = _jspx_th_aui_input_0.doStartTag();
        if (_jspx_th_aui_input_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_0);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_0);
        out.write('\n');
        out.write('	');
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_1 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_1.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_1.setName("repositoryId");
        _jspx_th_aui_input_1.setType("hidden");
        _jspx_th_aui_input_1.setValue( String.valueOf(repositoryId) );
        int _jspx_eval_aui_input_1 = _jspx_th_aui_input_1.doStartTag();
        if (_jspx_th_aui_input_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_1);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_1);
        out.write('\n');
        out.write('	');
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_2 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_2.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_2.setName("folderId");
        _jspx_th_aui_input_2.setType("hidden");
        _jspx_th_aui_input_2.setValue( String.valueOf(folderId) );
        int _jspx_eval_aui_input_2 = _jspx_th_aui_input_2.doStartTag();
        if (_jspx_th_aui_input_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_2);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_2);
        out.write("\n\n\t<div class=\"alert alert-info hide no-files-selected-info\" id=\"");
        if (_jspx_meth_portlet_namespace_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
          return;
        out.write("metadataExplanationContainer\">\n\t\t");
        if (_jspx_meth_liferay$1ui_message_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
          return;
        out.write("\n\t</div>\n\n\t");
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
        out.write("\n\n\t");
        //  liferay-ui:panel-container
        com.liferay.taglib.ui.PanelContainerTag _jspx_th_liferay$1ui_panel$1container_0 = (com.liferay.taglib.ui.PanelContainerTag) _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_id_extended.get(com.liferay.taglib.ui.PanelContainerTag.class);
        _jspx_th_liferay$1ui_panel$1container_0.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_panel$1container_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_liferay$1ui_panel$1container_0.setExtended( false );
        _jspx_th_liferay$1ui_panel$1container_0.setId("documentLibraryAssetPanelContainer");
        _jspx_th_liferay$1ui_panel$1container_0.setMarkupView("lexicon");
        _jspx_th_liferay$1ui_panel$1container_0.setPersistState( true );
        int _jspx_eval_liferay$1ui_panel$1container_0 = _jspx_th_liferay$1ui_panel$1container_0.doStartTag();
        if (_jspx_eval_liferay$1ui_panel$1container_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          if (_jspx_eval_liferay$1ui_panel$1container_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_panel$1container_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_panel$1container_0.doInitBody();
          }
          do {
            out.write("\n\t\t<div class=\"selected-files-count\">\n\t\t\t");
            if (_jspx_meth_liferay$1ui_message_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_panel$1container_0, _jspx_page_context))
              return;
            out.write("\n\t\t</div>\n\n\t\t");
            //  c:if
            org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
            _jspx_th_c_if_0.setPageContext(_jspx_page_context);
            _jspx_th_c_if_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel$1container_0);
            _jspx_th_c_if_0.setTest( (folder == null) || folder.isSupportsMetadata() );
            int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
            if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t");
                if (_jspx_meth_aui_input_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_0, _jspx_page_context))
                  return;
                out.write("\n\n\t\t\t");
                //  c:if
                org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                _jspx_th_c_if_1.setPageContext(_jspx_page_context);
                _jspx_th_c_if_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
                _jspx_th_c_if_1.setTest( !fileEntryTypes.isEmpty() );
                int _jspx_eval_c_if_1 = _jspx_th_c_if_1.doStartTag();
                if (_jspx_eval_c_if_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t\t\t");
                    //  liferay-ui:panel
                    com.liferay.taglib.ui.PanelTag _jspx_th_liferay$1ui_panel_0 = (com.liferay.taglib.ui.PanelTag) _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_cssClass_collapsible.get(com.liferay.taglib.ui.PanelTag.class);
                    _jspx_th_liferay$1ui_panel_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_panel_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                    _jspx_th_liferay$1ui_panel_0.setCollapsible( true );
                    _jspx_th_liferay$1ui_panel_0.setCssClass("document-type");
                    _jspx_th_liferay$1ui_panel_0.setMarkupView("lexicon");
                    _jspx_th_liferay$1ui_panel_0.setPersistState( true );
                    _jspx_th_liferay$1ui_panel_0.setTitle("document-type");
                    int _jspx_eval_liferay$1ui_panel_0 = _jspx_th_liferay$1ui_panel_0.doStartTag();
                    if (_jspx_eval_liferay$1ui_panel_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      out.write("\n\t\t\t\t\t");
                      //  aui:input
                      com.liferay.taglib.aui.InputTag _jspx_th_aui_input_4 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                      _jspx_th_aui_input_4.setPageContext(_jspx_page_context);
                      _jspx_th_aui_input_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_0);
                      _jspx_th_aui_input_4.setName("fileEntryTypeId");
                      _jspx_th_aui_input_4.setType("hidden");
                      _jspx_th_aui_input_4.setValue( (fileEntryTypeId > 0) ? fileEntryTypeId : 0 );
                      int _jspx_eval_aui_input_4 = _jspx_th_aui_input_4.doStartTag();
                      if (_jspx_th_aui_input_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_4);
                        return;
                      }
                      _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_4);
                      out.write("\n\t\t\t\t\t");
                      //  aui:input
                      com.liferay.taglib.aui.InputTag _jspx_th_aui_input_5 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                      _jspx_th_aui_input_5.setPageContext(_jspx_page_context);
                      _jspx_th_aui_input_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_0);
                      _jspx_th_aui_input_5.setName("defaultLanguageId");
                      _jspx_th_aui_input_5.setType("hidden");
                      _jspx_th_aui_input_5.setValue( themeDisplay.getLanguageId() );
                      int _jspx_eval_aui_input_5 = _jspx_th_aui_input_5.doStartTag();
                      if (_jspx_th_aui_input_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_5);
                        return;
                      }
                      _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_5);
                      out.write("\n\n\t\t\t\t\t<div class=\"document-type-selector\" id=\"");
                      if (_jspx_meth_portlet_namespace_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_panel_0, _jspx_page_context))
                        return;
                      out.write("documentTypeSelector\">\n\t\t\t\t\t\t");
                      //  liferay-ui:icon-menu
                      com.liferay.taglib.ui.IconMenuTag _jspx_th_liferay$1ui_icon$1menu_0 = (com.liferay.taglib.ui.IconMenuTag) _jspx_tagPool_liferay$1ui_icon$1menu_showWhenSingleIcon_message_id_direction.get(com.liferay.taglib.ui.IconMenuTag.class);
                      _jspx_th_liferay$1ui_icon$1menu_0.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1ui_icon$1menu_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_0);
                      _jspx_th_liferay$1ui_icon$1menu_0.setDirection("down");
                      _jspx_th_liferay$1ui_icon$1menu_0.setId("groupSelector");
                      _jspx_th_liferay$1ui_icon$1menu_0.setMessage( (fileEntryTypeId > 0) ? HtmlUtil.escape(fileEntryType.getName(locale)) : "basic-document" );
                      _jspx_th_liferay$1ui_icon$1menu_0.setShowWhenSingleIcon( true );
                      int _jspx_eval_liferay$1ui_icon$1menu_0 = _jspx_th_liferay$1ui_icon$1menu_0.doStartTag();
                      if (_jspx_eval_liferay$1ui_icon$1menu_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        if (_jspx_eval_liferay$1ui_icon$1menu_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                          out = _jspx_page_context.pushBody();
                          _jspx_th_liferay$1ui_icon$1menu_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                          _jspx_th_liferay$1ui_icon$1menu_0.doInitBody();
                        }
                        do {
                          out.write("\n\n\t\t\t\t\t\t\t");

							for (DLFileEntryType curFileEntryType : fileEntryTypes) {
							
                          out.write("\n\n\t\t\t\t\t\t\t\t");
                          //  liferay-portlet:resourceURL
                          com.liferay.taglib.portlet.ResourceURLTag _jspx_th_liferay$1portlet_resourceURL_0 = (com.liferay.taglib.portlet.ResourceURLTag) _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters.get(com.liferay.taglib.portlet.ResourceURLTag.class);
                          _jspx_th_liferay$1portlet_resourceURL_0.setPageContext(_jspx_page_context);
                          _jspx_th_liferay$1portlet_resourceURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_icon$1menu_0);
                          _jspx_th_liferay$1portlet_resourceURL_0.setCopyCurrentRenderParameters( false );
                          _jspx_th_liferay$1portlet_resourceURL_0.setId("/document_library/upload_multiple_file_entries");
                          _jspx_th_liferay$1portlet_resourceURL_0.setVar("viewFileEntryTypeURL");
                          int _jspx_eval_liferay$1portlet_resourceURL_0 = _jspx_th_liferay$1portlet_resourceURL_0.doStartTag();
                          if (_jspx_eval_liferay$1portlet_resourceURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                            out.write("\n\t\t\t\t\t\t\t\t\t");
                            //  portlet:param
                            com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_1 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                            _jspx_th_portlet_param_1.setPageContext(_jspx_page_context);
                            _jspx_th_portlet_param_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_resourceURL_0);
                            _jspx_th_portlet_param_1.setName("repositoryId");
                            _jspx_th_portlet_param_1.setValue( String.valueOf(repositoryId) );
                            int _jspx_eval_portlet_param_1 = _jspx_th_portlet_param_1.doStartTag();
                            if (_jspx_th_portlet_param_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
                              return;
                            }
                            _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
                            out.write("\n\t\t\t\t\t\t\t\t\t");
                            //  portlet:param
                            com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_2 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                            _jspx_th_portlet_param_2.setPageContext(_jspx_page_context);
                            _jspx_th_portlet_param_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_resourceURL_0);
                            _jspx_th_portlet_param_2.setName("folderId");
                            _jspx_th_portlet_param_2.setValue( String.valueOf(folderId) );
                            int _jspx_eval_portlet_param_2 = _jspx_th_portlet_param_2.doStartTag();
                            if (_jspx_th_portlet_param_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_2);
                              return;
                            }
                            _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_2);
                            out.write("\n\t\t\t\t\t\t\t\t\t");
                            //  portlet:param
                            com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_3 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                            _jspx_th_portlet_param_3.setPageContext(_jspx_page_context);
                            _jspx_th_portlet_param_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1portlet_resourceURL_0);
                            _jspx_th_portlet_param_3.setName("fileEntryTypeId");
                            _jspx_th_portlet_param_3.setValue( String.valueOf(curFileEntryType.getFileEntryTypeId()) );
                            int _jspx_eval_portlet_param_3 = _jspx_th_portlet_param_3.doStartTag();
                            if (_jspx_th_portlet_param_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_3);
                              return;
                            }
                            _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_3);
                            out.write("\n\t\t\t\t\t\t\t\t");
                          }
                          if (_jspx_th_liferay$1portlet_resourceURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                            _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters.reuse(_jspx_th_liferay$1portlet_resourceURL_0);
                            return;
                          }
                          _jspx_tagPool_liferay$1portlet_resourceURL_var_id_copyCurrentRenderParameters.reuse(_jspx_th_liferay$1portlet_resourceURL_0);
                          java.lang.String viewFileEntryTypeURL = null;
                          viewFileEntryTypeURL = (java.lang.String) _jspx_page_context.findAttribute("viewFileEntryTypeURL");
                          out.write("\n\n\t\t\t\t\t\t\t\t");
                          //  liferay-ui:icon
                          com.liferay.taglib.ui.IconTag _jspx_th_liferay$1ui_icon_0 = (com.liferay.taglib.ui.IconTag) _jspx_tagPool_liferay$1ui_icon_url_method_message_id_cssClass_nobody.get(com.liferay.taglib.ui.IconTag.class);
                          _jspx_th_liferay$1ui_icon_0.setPageContext(_jspx_page_context);
                          _jspx_th_liferay$1ui_icon_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_icon$1menu_0);
                          _jspx_th_liferay$1ui_icon_0.setCssClass("upload-multiple-document-types");
                          _jspx_th_liferay$1ui_icon_0.setId( "fileEntryType_" + String.valueOf(curFileEntryType.getFileEntryTypeId()) );
                          _jspx_th_liferay$1ui_icon_0.setMessage( HtmlUtil.escape(curFileEntryType.getName(locale)) );
                          _jspx_th_liferay$1ui_icon_0.setMethod("get");
                          _jspx_th_liferay$1ui_icon_0.setUrl( viewFileEntryTypeURL );
                          int _jspx_eval_liferay$1ui_icon_0 = _jspx_th_liferay$1ui_icon_0.doStartTag();
                          if (_jspx_th_liferay$1ui_icon_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                            _jspx_tagPool_liferay$1ui_icon_url_method_message_id_cssClass_nobody.reuse(_jspx_th_liferay$1ui_icon_0);
                            return;
                          }
                          _jspx_tagPool_liferay$1ui_icon_url_method_message_id_cssClass_nobody.reuse(_jspx_th_liferay$1ui_icon_0);
                          out.write("\n\n\t\t\t\t\t\t\t");

							}
							
                          out.write("\n\n\t\t\t\t\t\t");
                          int evalDoAfterBody = _jspx_th_liferay$1ui_icon$1menu_0.doAfterBody();
                          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                            break;
                        } while (true);
                        if (_jspx_eval_liferay$1ui_icon$1menu_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                          out = _jspx_page_context.popBody();
                      }
                      if (_jspx_th_liferay$1ui_icon$1menu_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_liferay$1ui_icon$1menu_showWhenSingleIcon_message_id_direction.reuse(_jspx_th_liferay$1ui_icon$1menu_0);
                        return;
                      }
                      _jspx_tagPool_liferay$1ui_icon$1menu_showWhenSingleIcon_message_id_direction.reuse(_jspx_th_liferay$1ui_icon$1menu_0);
                      out.write("\n\t\t\t\t\t</div>\n\n\t\t\t\t\t");

					if (fileEntryTypeId > 0) {
						try {
							List<DDMStructure> ddmStructures = fileEntryType.getDDMStructures();

							for (DDMStructure ddmStructure : ddmStructures) {
								DDMFormValues ddmFormValues = null;

								try {
									DLFileEntryMetadata fileEntryMetadata = DLFileEntryMetadataLocalServiceUtil.getFileEntryMetadata(ddmStructure.getStructureId(), fileVersionId);

									ddmFormValues = dlEditFileEntryDisplayContext.getDDMFormValues(fileEntryMetadata.getDDMStorageId());
								}
								catch (Exception e) {
								}

								if (groupId <= 0) {
									groupId = ddmStructure.getGroupId();
								}
					
                      out.write("\n\n\t\t\t\t\t\t\t\t");
                      //  aui:input
                      com.liferay.taglib.aui.InputTag _jspx_th_aui_input_6 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
                      _jspx_th_aui_input_6.setPageContext(_jspx_page_context);
                      _jspx_th_aui_input_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_0);
                      _jspx_th_aui_input_6.setName("ddmFormFieldNamespace");
                      _jspx_th_aui_input_6.setType("hidden");
                      _jspx_th_aui_input_6.setValue( String.valueOf(ddmStructure.getPrimaryKey()) );
                      int _jspx_eval_aui_input_6 = _jspx_th_aui_input_6.doStartTag();
                      if (_jspx_th_aui_input_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_6);
                        return;
                      }
                      _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_6);
                      out.write("\n\n\t\t\t\t\t\t\t\t<div class=\"document-type-fields\">\n\t\t\t\t\t\t\t\t\t");
                      //  liferay-ddm:html
                      com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag _jspx_th_liferay$1ddm_html_0 = (com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag) _jspx_tagPool_liferay$1ddm_html_synchronousFormSubmission_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody.get(com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag.class);
                      _jspx_th_liferay$1ddm_html_0.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1ddm_html_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_0);
                      _jspx_th_liferay$1ddm_html_0.setClassNameId( PortalUtil.getClassNameId(com.liferay.dynamic.data.mapping.model.DDMStructure.class) );
                      _jspx_th_liferay$1ddm_html_0.setClassPK( ddmStructure.getPrimaryKey() );
                      _jspx_th_liferay$1ddm_html_0.setDdmFormValues( ddmFormValues );
                      _jspx_th_liferay$1ddm_html_0.setDefaultEditLocale( LocaleUtil.fromLanguageId(themeDisplay.getLanguageId()) );
                      _jspx_th_liferay$1ddm_html_0.setFieldsNamespace( String.valueOf(ddmStructure.getPrimaryKey()) );
                      _jspx_th_liferay$1ddm_html_0.setGroupId( groupId );
                      _jspx_th_liferay$1ddm_html_0.setLocalizable( true );
                      _jspx_th_liferay$1ddm_html_0.setRequestedLocale( locale );
                      _jspx_th_liferay$1ddm_html_0.setSynchronousFormSubmission( false );
                      int _jspx_eval_liferay$1ddm_html_0 = _jspx_th_liferay$1ddm_html_0.doStartTag();
                      if (_jspx_th_liferay$1ddm_html_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_liferay$1ddm_html_synchronousFormSubmission_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1ddm_html_0);
                        return;
                      }
                      _jspx_tagPool_liferay$1ddm_html_synchronousFormSubmission_requestedLocale_localizable_groupId_fieldsNamespace_defaultEditLocale_ddmFormValues_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1ddm_html_0);
                      out.write("\n\t\t\t\t\t\t\t\t</div>\n\n\t\t\t\t\t");

							}
						}
						catch (Exception e) {
						}
					}
					
                      out.write("\n\n\t\t\t\t\t");
                      //  aui:script
                      com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_0 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script_require_position.get(com.liferay.taglib.aui.ScriptTag.class);
                      _jspx_th_aui_script_0.setPageContext(_jspx_page_context);
                      _jspx_th_aui_script_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_0);
                      _jspx_th_aui_script_0.setPosition("inline");
                      _jspx_th_aui_script_0.setRequire("metal-dom/src/all/dom as dom");
                      int _jspx_eval_aui_script_0 = _jspx_th_aui_script_0.doStartTag();
                      if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                          out = _jspx_page_context.pushBody();
                          _jspx_th_aui_script_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                          _jspx_th_aui_script_0.doInitBody();
                        }
                        do {
                          out.write("\n\t\t\t\t\t\tvar documentTypeMenuList = document.querySelector(\n\t\t\t\t\t\t\t'#");
                          if (_jspx_meth_portlet_namespace_2((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
                            return;
                          out.write("documentTypeSelector .lfr-menu-list'\n\t\t\t\t\t\t);\n\n\t\t\t\t\t\tif (documentTypeMenuList) {\n\t\t\t\t\t\t\tdom.delegate(documentTypeMenuList, 'click', 'li a', function(event) {\n\t\t\t\t\t\t\t\tevent.preventDefault();\n\n\t\t\t\t\t\t\t\tLiferay.Util.fetch(event.delegateTarget.getAttribute('href'))\n\t\t\t\t\t\t\t\t\t.then(function(response) {\n\t\t\t\t\t\t\t\t\t\treturn response.text();\n\t\t\t\t\t\t\t\t\t})\n\t\t\t\t\t\t\t\t\t.then(function(response) {\n\t\t\t\t\t\t\t\t\t\tvar commonFileMetadataContainer = document.getElementById(\n\t\t\t\t\t\t\t\t\t\t\t'");
                          if (_jspx_meth_portlet_namespace_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
                            return;
                          out.write("commonFileMetadataContainer'\n\t\t\t\t\t\t\t\t\t\t);\n\n\t\t\t\t\t\t\t\t\t\tif (commonFileMetadataContainer) {\n\t\t\t\t\t\t\t\t\t\t\tcommonFileMetadataContainer.innerHTML = response;\n\n\t\t\t\t\t\t\t\t\t\t\tdom.globalEval.runScriptsInElement(\n\t\t\t\t\t\t\t\t\t\t\t\tcommonFileMetadataContainer\n\t\t\t\t\t\t\t\t\t\t\t);\n\t\t\t\t\t\t\t\t\t\t}\n\n\t\t\t\t\t\t\t\t\t\tvar fileNodes = document.querySelectorAll(\n\t\t\t\t\t\t\t\t\t\t\t'input[name=");
                          if (_jspx_meth_portlet_namespace_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
                            return;
                          out.write("selectUploadedFile]'\n\t\t\t\t\t\t\t\t\t\t);\n\n\t\t\t\t\t\t\t\t\t\tvar selectedFileNodes = Array.prototype.filter.call(\n\t\t\t\t\t\t\t\t\t\t\tfileNodes,\n\t\t\t\t\t\t\t\t\t\t\tfunction(fileNode) {\n\t\t\t\t\t\t\t\t\t\t\t\treturn fileNode.checked;\n\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t);\n\n\t\t\t\t\t\t\t\t\t\tvar selectedFilesCount = selectedFileNodes.length;\n\n\t\t\t\t\t\t\t\t\t\tvar selectedFilesText = '';\n\n\t\t\t\t\t\t\t\t\t\tif (selectedFilesCount > 0) {\n\t\t\t\t\t\t\t\t\t\t\tselectedFilesText = selectedFileNodes[0].dataset.title;\n\t\t\t\t\t\t\t\t\t\t}\n\n\t\t\t\t\t\t\t\t\t\tif (selectedFilesCount > 1) {\n\t\t\t\t\t\t\t\t\t\t\tif (selectedFilesCount === fileNodes.length) {\n\t\t\t\t\t\t\t\t\t\t\t\tselectedFilesText =\n\t\t\t\t\t\t\t\t\t\t\t\t\t'");
                          out.print( UnicodeLanguageUtil.get(request, "all-files-selected") );
                          out.write("';\n\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t\telse {\n\t\t\t\t\t\t\t\t\t\t\t\tselectedFilesText = Liferay.Util.sub(\n\t\t\t\t\t\t\t\t\t\t\t\t\t'");
                          out.print( UnicodeLanguageUtil.get(request, "x-files-selected") );
                          out.write("',\n\t\t\t\t\t\t\t\t\t\t\t\t\tselectedFilesCount\n\t\t\t\t\t\t\t\t\t\t\t\t);\n\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t}\n\n\t\t\t\t\t\t\t\t\t\tvar selectedFilesCountElement = document.querySelector(\n\t\t\t\t\t\t\t\t\t\t\t'.selected-files-count'\n\t\t\t\t\t\t\t\t\t\t);\n\n\t\t\t\t\t\t\t\t\t\tif (selectedFilesCountElement) {\n\t\t\t\t\t\t\t\t\t\t\tselectedFilesCountElement.innerHTML = selectedFilesText;\n\n\t\t\t\t\t\t\t\t\t\t\tselectedFilesCountElement.setAttribute(\n\t\t\t\t\t\t\t\t\t\t\t\t'title',\n\t\t\t\t\t\t\t\t\t\t\t\tselectedFilesText\n\t\t\t\t\t\t\t\t\t\t\t);\n\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t});\n\t\t\t\t\t\t\t});\n\t\t\t\t\t\t}\n\t\t\t\t\t");
                          int evalDoAfterBody = _jspx_th_aui_script_0.doAfterBody();
                          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                            break;
                        } while (true);
                        if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                          out = _jspx_page_context.popBody();
                      }
                      if (_jspx_th_aui_script_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_aui_script_require_position.reuse(_jspx_th_aui_script_0);
                        return;
                      }
                      _jspx_tagPool_aui_script_require_position.reuse(_jspx_th_aui_script_0);
                      out.write("\n\t\t\t\t");
                    }
                    if (_jspx_th_liferay$1ui_panel_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_cssClass_collapsible.reuse(_jspx_th_liferay$1ui_panel_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_cssClass_collapsible.reuse(_jspx_th_liferay$1ui_panel_0);
                    out.write("\n\t\t\t");
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
                out.write("\n\n\t\t\t");
                //  liferay-expando:custom-attributes-available
                com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag _jspx_th_liferay$1expando_custom$1attributes$1available_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag) _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag.class);
                _jspx_th_liferay$1expando_custom$1attributes$1available_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1expando_custom$1attributes$1available_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
                _jspx_th_liferay$1expando_custom$1attributes$1available_0.setClassName( DLFileEntryConstants.getClassName() );
                int _jspx_eval_liferay$1expando_custom$1attributes$1available_0 = _jspx_th_liferay$1expando_custom$1attributes$1available_0.doStartTag();
                if (_jspx_eval_liferay$1expando_custom$1attributes$1available_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n\t\t\t\t");
                  //  liferay-expando:custom-attribute-list
                  com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag _jspx_th_liferay$1expando_custom$1attribute$1list_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag) _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag.class);
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1expando_custom$1attributes$1available_0);
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
                  out.write("\n\t\t\t");
                }
                if (_jspx_th_liferay$1expando_custom$1attributes$1available_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
                  return;
                }
                _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
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
            //  liferay-ui:panel
            com.liferay.taglib.ui.PanelTag _jspx_th_liferay$1ui_panel_1 = (com.liferay.taglib.ui.PanelTag) _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass.get(com.liferay.taglib.ui.PanelTag.class);
            _jspx_th_liferay$1ui_panel_1.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_panel_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel$1container_0);
            _jspx_th_liferay$1ui_panel_1.setCssClass("display-page-panel");
            _jspx_th_liferay$1ui_panel_1.setDefaultState("closed");
            _jspx_th_liferay$1ui_panel_1.setExtended( false );
            _jspx_th_liferay$1ui_panel_1.setId("dlFileEntryDisplayPagePanel");
            _jspx_th_liferay$1ui_panel_1.setMarkupView("lexicon");
            _jspx_th_liferay$1ui_panel_1.setPersistState( true );
            _jspx_th_liferay$1ui_panel_1.setTitle("display-page-template");
            int _jspx_eval_liferay$1ui_panel_1 = _jspx_th_liferay$1ui_panel_1.doStartTag();
            if (_jspx_eval_liferay$1ui_panel_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              out.write("\n\t\t\t");
              //  aui:fieldset
              com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_0 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset.get(com.liferay.taglib.aui.FieldsetTag.class);
              _jspx_th_aui_fieldset_0.setPageContext(_jspx_page_context);
              _jspx_th_aui_fieldset_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_1);
              int _jspx_eval_aui_fieldset_0 = _jspx_th_aui_fieldset_0.doStartTag();
              if (_jspx_eval_aui_fieldset_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t");
                //  liferay-asset:select-asset-display-page
                com.liferay.asset.taglib.servlet.taglib.SelectAssetDisplayPageTag _jspx_th_liferay$1asset_select$1asset$1display$1page_0 = (com.liferay.asset.taglib.servlet.taglib.SelectAssetDisplayPageTag) _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classPK_classNameId_nobody.get(com.liferay.asset.taglib.servlet.taglib.SelectAssetDisplayPageTag.class);
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_0);
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setClassNameId( PortalUtil.getClassNameId(DLFileEntry.class) );
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setClassPK( 0 );
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setGroupId( scopeGroupId );
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setShowPortletLayouts( true );
                _jspx_th_liferay$1asset_select$1asset$1display$1page_0.setShowViewInContextLink( true );
                int _jspx_eval_liferay$1asset_select$1asset$1display$1page_0 = _jspx_th_liferay$1asset_select$1asset$1display$1page_0.doStartTag();
                if (_jspx_th_liferay$1asset_select$1asset$1display$1page_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1asset_select$1asset$1display$1page_0);
                  return;
                }
                _jspx_tagPool_liferay$1asset_select$1asset$1display$1page_showViewInContextLink_showPortletLayouts_groupId_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1asset_select$1asset$1display$1page_0);
                out.write("\n\t\t\t");
              }
              if (_jspx_th_aui_fieldset_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_aui_fieldset.reuse(_jspx_th_aui_fieldset_0);
                return;
              }
              _jspx_tagPool_aui_fieldset.reuse(_jspx_th_aui_fieldset_0);
              out.write("\n\t\t");
            }
            if (_jspx_th_liferay$1ui_panel_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass.reuse(_jspx_th_liferay$1ui_panel_1);
              return;
            }
            _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass.reuse(_jspx_th_liferay$1ui_panel_1);
            out.write("\n\n\t\t");
            //  c:if
            org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_2 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
            _jspx_th_c_if_2.setPageContext(_jspx_page_context);
            _jspx_th_c_if_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel$1container_0);
            _jspx_th_c_if_2.setTest( (folder == null) || folder.isSupportsSocial() );
            int _jspx_eval_c_if_2 = _jspx_th_c_if_2.doStartTag();
            if (_jspx_eval_c_if_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t");
                //  liferay-ui:panel
                com.liferay.taglib.ui.PanelTag _jspx_th_liferay$1ui_panel_2 = (com.liferay.taglib.ui.PanelTag) _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass.get(com.liferay.taglib.ui.PanelTag.class);
                _jspx_th_liferay$1ui_panel_2.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_panel_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_2);
                _jspx_th_liferay$1ui_panel_2.setCssClass("categorization-panel");
                _jspx_th_liferay$1ui_panel_2.setDefaultState("closed");
                _jspx_th_liferay$1ui_panel_2.setExtended( false );
                _jspx_th_liferay$1ui_panel_2.setId("dlFileEntryCategorizationPanel");
                _jspx_th_liferay$1ui_panel_2.setMarkupView("lexicon");
                _jspx_th_liferay$1ui_panel_2.setPersistState( true );
                _jspx_th_liferay$1ui_panel_2.setTitle("categorization");
                int _jspx_eval_liferay$1ui_panel_2 = _jspx_th_liferay$1ui_panel_2.doStartTag();
                if (_jspx_eval_liferay$1ui_panel_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n\t\t\t\t");
                  //  aui:fieldset
                  com.liferay.taglib.aui.FieldsetTag _jspx_th_aui_fieldset_1 = (com.liferay.taglib.aui.FieldsetTag) _jspx_tagPool_aui_fieldset.get(com.liferay.taglib.aui.FieldsetTag.class);
                  _jspx_th_aui_fieldset_1.setPageContext(_jspx_page_context);
                  _jspx_th_aui_fieldset_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_2);
                  int _jspx_eval_aui_fieldset_1 = _jspx_th_aui_fieldset_1.doStartTag();
                  if (_jspx_eval_aui_fieldset_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    out.write("\n\t\t\t\t\t");
                    //  liferay-asset:asset-categories-selector
                    com.liferay.asset.taglib.servlet.taglib.soy.AssetCategoriesSelectorTag _jspx_th_liferay$1asset_asset$1categories$1selector_0 = (com.liferay.asset.taglib.servlet.taglib.soy.AssetCategoriesSelectorTag) _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody.get(com.liferay.asset.taglib.servlet.taglib.soy.AssetCategoriesSelectorTag.class);
                    _jspx_th_liferay$1asset_asset$1categories$1selector_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1asset_asset$1categories$1selector_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
                    _jspx_th_liferay$1asset_asset$1categories$1selector_0.setClassName( DLFileEntry.class.getName() );
                    _jspx_th_liferay$1asset_asset$1categories$1selector_0.setClassPK( assetClassPK );
                    _jspx_th_liferay$1asset_asset$1categories$1selector_0.setClassTypePK( fileEntryTypeId );
                    int _jspx_eval_liferay$1asset_asset$1categories$1selector_0 = _jspx_th_liferay$1asset_asset$1categories$1selector_0.doStartTag();
                    if (_jspx_th_liferay$1asset_asset$1categories$1selector_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1categories$1selector_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1asset_asset$1categories$1selector_classTypePK_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1categories$1selector_0);
                    out.write("\n\n\t\t\t\t\t");
                    //  liferay-asset:asset-tags-selector
                    com.liferay.asset.taglib.servlet.taglib.soy.AssetTagsSelectorTag _jspx_th_liferay$1asset_asset$1tags$1selector_0 = (com.liferay.asset.taglib.servlet.taglib.soy.AssetTagsSelectorTag) _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody.get(com.liferay.asset.taglib.servlet.taglib.soy.AssetTagsSelectorTag.class);
                    _jspx_th_liferay$1asset_asset$1tags$1selector_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1asset_asset$1tags$1selector_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_fieldset_1);
                    _jspx_th_liferay$1asset_asset$1tags$1selector_0.setClassName( DLFileEntry.class.getName() );
                    _jspx_th_liferay$1asset_asset$1tags$1selector_0.setClassPK( assetClassPK );
                    int _jspx_eval_liferay$1asset_asset$1tags$1selector_0 = _jspx_th_liferay$1asset_asset$1tags$1selector_0.doStartTag();
                    if (_jspx_th_liferay$1asset_asset$1tags$1selector_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1tags$1selector_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1asset_asset$1tags$1selector_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1tags$1selector_0);
                    out.write("\n\t\t\t\t");
                  }
                  if (_jspx_th_aui_fieldset_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_aui_fieldset.reuse(_jspx_th_aui_fieldset_1);
                    return;
                  }
                  _jspx_tagPool_aui_fieldset.reuse(_jspx_th_aui_fieldset_1);
                  out.write("\n\t\t\t");
                }
                if (_jspx_th_liferay$1ui_panel_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass.reuse(_jspx_th_liferay$1ui_panel_2);
                  return;
                }
                _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass.reuse(_jspx_th_liferay$1ui_panel_2);
                out.write("\n\t\t");
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
            out.write('\n');
            out.write('	');
            int evalDoAfterBody = _jspx_th_liferay$1ui_panel$1container_0.doAfterBody();
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_panel$1container_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_panel$1container_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_id_extended.reuse(_jspx_th_liferay$1ui_panel$1container_0);
          return;
        }
        _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_id_extended.reuse(_jspx_th_liferay$1ui_panel$1container_0);
        out.write("\n\n\t");
        //  aui:field-wrapper
        com.liferay.taglib.aui.FieldWrapperTag _jspx_th_aui_field$1wrapper_0 = (com.liferay.taglib.aui.FieldWrapperTag) _jspx_tagPool_aui_field$1wrapper_label_cssClass.get(com.liferay.taglib.aui.FieldWrapperTag.class);
        _jspx_th_aui_field$1wrapper_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_field$1wrapper_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_field$1wrapper_0.setCssClass("upload-multiple-file-permissions");
        _jspx_th_aui_field$1wrapper_0.setLabel("permissions");
        int _jspx_eval_aui_field$1wrapper_0 = _jspx_th_aui_field$1wrapper_0.doStartTag();
        if (_jspx_eval_aui_field$1wrapper_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n\t\t");
          //  liferay-ui:input-permissions
          com.liferay.taglib.ui.InputPermissionsTag _jspx_th_liferay$1ui_input$1permissions_0 = (com.liferay.taglib.ui.InputPermissionsTag) _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.get(com.liferay.taglib.ui.InputPermissionsTag.class);
          _jspx_th_liferay$1ui_input$1permissions_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1ui_input$1permissions_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_field$1wrapper_0);
          _jspx_th_liferay$1ui_input$1permissions_0.setModelName( DLFileEntryConstants.getClassName() );
          int _jspx_eval_liferay$1ui_input$1permissions_0 = _jspx_th_liferay$1ui_input$1permissions_0.doStartTag();
          if (_jspx_th_liferay$1ui_input$1permissions_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.reuse(_jspx_th_liferay$1ui_input$1permissions_0);
            return;
          }
          _jspx_tagPool_liferay$1ui_input$1permissions_modelName_nobody.reuse(_jspx_th_liferay$1ui_input$1permissions_0);
          out.write('\n');
          out.write('	');
        }
        if (_jspx_th_aui_field$1wrapper_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_field$1wrapper_label_cssClass.reuse(_jspx_th_aui_field$1wrapper_0);
          return;
        }
        _jspx_tagPool_aui_field$1wrapper_label_cssClass.reuse(_jspx_th_aui_field$1wrapper_0);
        out.write("\n\n\t<span id=\"");
        if (_jspx_meth_portlet_namespace_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
          return;
        out.write("selectedFileNameContainer\"></span>\n\n\t");
        //  aui:button
        com.liferay.taglib.aui.ButtonTag _jspx_th_aui_button_0 = (com.liferay.taglib.aui.ButtonTag) _jspx_tagPool_aui_button_value_type_nobody.get(com.liferay.taglib.aui.ButtonTag.class);
        _jspx_th_aui_button_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_button_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_button_0.setType("submit");
        _jspx_th_aui_button_0.setValue( dlEditFileEntryDisplayContext.getPublishButtonLabel() );
        int _jspx_eval_aui_button_0 = _jspx_th_aui_button_0.doStartTag();
        if (_jspx_th_aui_button_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_button_value_type_nobody.reuse(_jspx_th_aui_button_0);
          return;
        }
        _jspx_tagPool_aui_button_value_type_nobody.reuse(_jspx_th_aui_button_0);
        out.write('\n');
      }
      if (_jspx_th_aui_form_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_aui_form_onSubmit_name_method_action.reuse(_jspx_th_aui_form_0);
        return;
      }
      _jspx_tagPool_aui_form_onSubmit_name_method_action.reuse(_jspx_th_aui_form_0);
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

  private boolean _jspx_meth_portlet_namespace_0(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_0 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    int _jspx_eval_portlet_namespace_0 = _jspx_th_portlet_namespace_0.doStartTag();
    if (_jspx_th_portlet_namespace_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_0 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    _jspx_th_liferay$1ui_message_0.setKey("select-documents-from-the-left-to-add-them-to-the-documents-and-media");
    int _jspx_eval_liferay$1ui_message_0 = _jspx_th_liferay$1ui_message_0.doStartTag();
    if (_jspx_th_liferay$1ui_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_1(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_panel$1container_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_1 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_1.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel$1container_0);
    _jspx_th_liferay$1ui_message_1.setKey("no-files-selected");
    int _jspx_eval_liferay$1ui_message_1 = _jspx_th_liferay$1ui_message_1.doStartTag();
    if (_jspx_th_liferay$1ui_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
    return false;
  }

  private boolean _jspx_meth_aui_input_3(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_3 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_3.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
    _jspx_th_aui_input_3.setName("description");
    int _jspx_eval_aui_input_3 = _jspx_th_aui_input_3.doStartTag();
    if (_jspx_th_aui_input_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_name_nobody.reuse(_jspx_th_aui_input_3);
      return true;
    }
    _jspx_tagPool_aui_input_name_nobody.reuse(_jspx_th_aui_input_3);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_1(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_panel_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_1 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_1.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_0);
    int _jspx_eval_portlet_namespace_1 = _jspx_th_portlet_namespace_1.doStartTag();
    if (_jspx_th_portlet_namespace_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
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

  private boolean _jspx_meth_portlet_namespace_5(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_5 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_5.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    int _jspx_eval_portlet_namespace_5 = _jspx_th_portlet_namespace_5.doStartTag();
    if (_jspx_th_portlet_namespace_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_5);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_5);
    return false;
  }
}
