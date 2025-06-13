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

public final class info_005fpanel_005ffile_005fentry_jsp extends org.apache.jasper.runtime.HttpJspBase
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

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_clay_link_title_label_href_elementClasses_data_buttonStyle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_section;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_ratings_inTrash_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_model$1context_model_bean_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1links_assetEntryId_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1sharing_button_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_icon$1help_message_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1tags$1available_classPK_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_clay_dropdown$1menu_triggerCssClasses_style_label_dropdownItems_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ddm_html_showEmptyFieldLabel_requestedLocale_readOnly_groupId_fieldsNamespace_ddmFormValues_classPK_classNameId_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_clay_link_title_label_href_elementClasses_buttonStyle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass_collapsible;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_extended_cssClass;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_model_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_user$1portrait_user_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_choose;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_include_servletContext_page_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_clay_label_style_label_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_when_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1tags$1summary_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1dynamic$1section_dynamic$1section_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_component_module_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_otherwise;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_clay_button_title_style_icon_elementClasses_data_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_defaultState_cssClass_collapsible;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attributes$1available_editable_classPK_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1categories$1summary_displayStyle_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_escapeAttribute_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_asset$1categories$1available_classPK_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_clay_link_title_label_href_elementClasses_data_buttonStyle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_section = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_ratings_inTrash_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_model$1context_model_bean_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1links_assetEntryId_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1sharing_button_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_icon$1help_message_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1tags$1available_classPK_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_clay_dropdown$1menu_triggerCssClasses_style_label_dropdownItems_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ddm_html_showEmptyFieldLabel_requestedLocale_readOnly_groupId_fieldsNamespace_ddmFormValues_classPK_classNameId_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_clay_link_title_label_href_elementClasses_buttonStyle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass_collapsible = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_extended_cssClass = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_model_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_user$1portrait_user_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_choose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_clay_label_style_label_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_when_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1tags$1summary_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1dynamic$1section_dynamic$1section_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_component_module_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_otherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_clay_button_title_style_icon_elementClasses_data_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_defaultState_cssClass_collapsible = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_editable_classPK_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1categories$1summary_displayStyle_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_escapeAttribute_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_asset$1categories$1available_classPK_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_clay_link_title_label_href_elementClasses_data_buttonStyle_nobody.release();
    _jspx_tagPool_liferay$1ui_section.release();
    _jspx_tagPool_liferay$1ui_ratings_inTrash_classPK_className_nobody.release();
    _jspx_tagPool_aui_model$1context_model_bean_nobody.release();
    _jspx_tagPool_liferay$1asset_asset$1links_assetEntryId_nobody.release();
    _jspx_tagPool_liferay$1sharing_button_classPK_className_nobody.release();
    _jspx_tagPool_liferay$1ui_icon$1help_message_nobody.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_liferay$1asset_asset$1tags$1available_classPK_className.release();
    _jspx_tagPool_clay_dropdown$1menu_triggerCssClasses_style_label_dropdownItems_nobody.release();
    _jspx_tagPool_liferay$1ddm_html_showEmptyFieldLabel_requestedLocale_readOnly_groupId_fieldsNamespace_ddmFormValues_classPK_classNameId_nobody.release();
    _jspx_tagPool_clay_link_title_label_href_elementClasses_buttonStyle_nobody.release();
    _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass_collapsible.release();
    _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_extended_cssClass.release();
    _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_model_nobody.release();
    _jspx_tagPool_liferay$1ui_user$1portrait_user_nobody.release();
    _jspx_tagPool_c_choose.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.release();
    _jspx_tagPool_clay_label_style_label_nobody.release();
    _jspx_tagPool_c_when_test.release();
    _jspx_tagPool_liferay$1asset_asset$1tags$1summary_classPK_className_nobody.release();
    _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.release();
    _jspx_tagPool_liferay$1ui_message_key_nobody.release();
    _jspx_tagPool_liferay$1dynamic$1section_dynamic$1section_name.release();
    _jspx_tagPool_liferay$1frontend_component_module_nobody.release();
    _jspx_tagPool_c_otherwise.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_clay_button_title_style_icon_elementClasses_data_nobody.release();
    _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_defaultState_cssClass_collapsible.release();
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.release();
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_editable_classPK_className.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1asset_asset$1categories$1summary_displayStyle_classPK_className_nobody.release();
    _jspx_tagPool_liferay$1ui_message_key_escapeAttribute_nobody.release();
    _jspx_tagPool_liferay$1asset_asset$1categories$1available_classPK_className.release();
    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.release();
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

FileEntry fileEntry = (FileEntry)request.getAttribute("info_panel.jsp-fileEntry");
FileVersion fileVersion = (FileVersion)request.getAttribute("info_panel.jsp-fileVersion");
boolean hideActions = GetterUtil.getBoolean(request.getAttribute("info_panel_file_entry.jsp-hideActions"));

DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext = dlDisplayContextProvider.getDLViewFileVersionDisplayContext(request, response, fileVersion);

long assetClassPK = DLAssetHelperUtil.getAssetClassPK(fileEntry, fileVersion);

      out.write("\n\n<div class=\"sidebar-header\">\n\t");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_0.setPageContext(_jspx_page_context);
      _jspx_th_c_if_0.setParent(null);
      _jspx_th_c_if_0.setTest( !hideActions );
      int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
      if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\t\t<ul class=\"sidebar-header-actions\">\n\t\t\t<li>\n\t\t\t\t");
          //  liferay-util:include
          com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_0 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
          _jspx_th_liferay$1util_include_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1util_include_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
          _jspx_th_liferay$1util_include_0.setPage("/document_library/file_entry_action.jsp");
          _jspx_th_liferay$1util_include_0.setServletContext( application );
          int _jspx_eval_liferay$1util_include_0 = _jspx_th_liferay$1util_include_0.doStartTag();
          if (_jspx_th_liferay$1util_include_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
            return;
          }
          _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
          out.write("\n\t\t\t</li>\n\t\t</ul>\n\t");
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
      out.write("\n\n\t<h1 class=\"sidebar-title\">\n\t\t");
      out.print( HtmlUtil.escape(fileVersion.getTitle()) );
      out.write("\n\t</h1>\n\n\t");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_1.setPageContext(_jspx_page_context);
      _jspx_th_c_if_1.setParent(null);
      _jspx_th_c_if_1.setTest( dlViewFileVersionDisplayContext.isVersionInfoVisible() );
      int _jspx_eval_c_if_1 = _jspx_th_c_if_1.doStartTag();
      if (_jspx_eval_c_if_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\t\t");
          //  clay:label
          com.liferay.frontend.taglib.clay.servlet.taglib.soy.LabelTag _jspx_th_clay_label_0 = (com.liferay.frontend.taglib.clay.servlet.taglib.soy.LabelTag) _jspx_tagPool_clay_label_style_label_nobody.get(com.liferay.frontend.taglib.clay.servlet.taglib.soy.LabelTag.class);
          _jspx_th_clay_label_0.setPageContext(_jspx_page_context);
          _jspx_th_clay_label_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
          _jspx_th_clay_label_0.setLabel( LanguageUtil.get(request, "version") + StringPool.SPACE + fileVersion.getVersion() );
          _jspx_th_clay_label_0.setStyle("info");
          int _jspx_eval_clay_label_0 = _jspx_th_clay_label_0.doStartTag();
          if (_jspx_th_clay_label_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_clay_label_style_label_nobody.reuse(_jspx_th_clay_label_0);
            return;
          }
          _jspx_tagPool_clay_label_style_label_nobody.reuse(_jspx_th_clay_label_0);
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
      //  aui:model-context
      com.liferay.taglib.aui.ModelContextTag _jspx_th_aui_model$1context_0 = (com.liferay.taglib.aui.ModelContextTag) _jspx_tagPool_aui_model$1context_model_bean_nobody.get(com.liferay.taglib.aui.ModelContextTag.class);
      _jspx_th_aui_model$1context_0.setPageContext(_jspx_page_context);
      _jspx_th_aui_model$1context_0.setParent(null);
      _jspx_th_aui_model$1context_0.setBean( fileVersion );
      _jspx_th_aui_model$1context_0.setModel( DLFileVersion.class );
      int _jspx_eval_aui_model$1context_0 = _jspx_th_aui_model$1context_0.doStartTag();
      if (_jspx_th_aui_model$1context_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_aui_model$1context_model_bean_nobody.reuse(_jspx_th_aui_model$1context_0);
        return;
      }
      _jspx_tagPool_aui_model$1context_model_bean_nobody.reuse(_jspx_th_aui_model$1context_0);
      out.write("\n\n\t");
      //  aui:workflow-status
      com.liferay.taglib.aui.WorkflowStatusTag _jspx_th_aui_workflow$1status_0 = (com.liferay.taglib.aui.WorkflowStatusTag) _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_model_nobody.get(com.liferay.taglib.aui.WorkflowStatusTag.class);
      _jspx_th_aui_workflow$1status_0.setPageContext(_jspx_page_context);
      _jspx_th_aui_workflow$1status_0.setParent(null);
      _jspx_th_aui_workflow$1status_0.setModel( DLFileEntry.class );
      _jspx_th_aui_workflow$1status_0.setShowIcon( false );
      _jspx_th_aui_workflow$1status_0.setShowLabel( false );
      _jspx_th_aui_workflow$1status_0.setStatus( fileVersion.getStatus() );
      int _jspx_eval_aui_workflow$1status_0 = _jspx_th_aui_workflow$1status_0.doStartTag();
      if (_jspx_th_aui_workflow$1status_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_model_nobody.reuse(_jspx_th_aui_workflow$1status_0);
        return;
      }
      _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_model_nobody.reuse(_jspx_th_aui_workflow$1status_0);
      out.write("\n</div>\n\n<div class=\"sidebar-body\">\n\n\t");

	String tabsNames = "details";

	if (dlViewFileVersionDisplayContext.isVersionInfoVisible()) {
		tabsNames += ",versions";
	}
	
      out.write("\n\n\t");
      //  liferay-ui:tabs
      com.liferay.taglib.ui.TabsTag _jspx_th_liferay$1ui_tabs_0 = (com.liferay.taglib.ui.TabsTag) _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.get(com.liferay.taglib.ui.TabsTag.class);
      _jspx_th_liferay$1ui_tabs_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1ui_tabs_0.setParent(null);
      _jspx_th_liferay$1ui_tabs_0.setCssClass("navbar-no-collapse");
      _jspx_th_liferay$1ui_tabs_0.setNames( tabsNames );
      _jspx_th_liferay$1ui_tabs_0.setRefresh( false );
      int _jspx_eval_liferay$1ui_tabs_0 = _jspx_th_liferay$1ui_tabs_0.doStartTag();
      if (_jspx_eval_liferay$1ui_tabs_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n\t\t");
        //  liferay-ui:section
        com.liferay.taglib.ui.SectionTag _jspx_th_liferay$1ui_section_0 = (com.liferay.taglib.ui.SectionTag) _jspx_tagPool_liferay$1ui_section.get(com.liferay.taglib.ui.SectionTag.class);
        _jspx_th_liferay$1ui_section_0.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_section_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_tabs_0);
        int _jspx_eval_liferay$1ui_section_0 = _jspx_th_liferay$1ui_section_0.doStartTag();
        if (_jspx_eval_liferay$1ui_section_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.String sectionParam = null;
          java.lang.String sectionName = null;
          java.lang.Boolean sectionSelected = null;
          java.lang.String sectionScroll = null;
          java.lang.String sectionRedirectParams = null;
          sectionParam = (java.lang.String) _jspx_page_context.findAttribute("sectionParam");
          sectionName = (java.lang.String) _jspx_page_context.findAttribute("sectionName");
          sectionSelected = (java.lang.Boolean) _jspx_page_context.findAttribute("sectionSelected");
          sectionScroll = (java.lang.String) _jspx_page_context.findAttribute("sectionScroll");
          sectionRedirectParams = (java.lang.String) _jspx_page_context.findAttribute("sectionRedirectParams");
          out.write("\n\n\t\t\t");

			String thumbnailSrc = DLURLHelperUtil.getThumbnailSrc(fileEntry, fileVersion, themeDisplay);
			
          out.write("\n\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_2 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_2.setPageContext(_jspx_page_context);
          _jspx_th_c_if_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_c_if_2.setTest( Validator.isNotNull(thumbnailSrc) );
          int _jspx_eval_c_if_2 = _jspx_th_c_if_2.doStartTag();
          if (_jspx_eval_c_if_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t<div class=\"aspect-ratio aspect-ratio-16-to-9 sidebar-panel thumbnail\">\n\t\t\t\t\t<img alt=\"");
              //  liferay-ui:message
              com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_0 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_escapeAttribute_nobody.get(com.liferay.taglib.ui.MessageTag.class);
              _jspx_th_liferay$1ui_message_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1ui_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_2);
              _jspx_th_liferay$1ui_message_0.setEscapeAttribute( true );
              _jspx_th_liferay$1ui_message_0.setKey("thumbnail");
              int _jspx_eval_liferay$1ui_message_0 = _jspx_th_liferay$1ui_message_0.doStartTag();
              if (_jspx_th_liferay$1ui_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1ui_message_key_escapeAttribute_nobody.reuse(_jspx_th_liferay$1ui_message_0);
                return;
              }
              _jspx_tagPool_liferay$1ui_message_key_escapeAttribute_nobody.reuse(_jspx_th_liferay$1ui_message_0);
              out.write("\" class=\"aspect-ratio-item-center-middle aspect-ratio-item-fluid\" src=\"");
              out.print( DLURLHelperUtil.getThumbnailSrc(fileEntry, fileVersion, themeDisplay) );
              out.write("\" />\n\t\t\t\t</div>\n\t\t\t");
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
          out.write("\n\n\t\t\t");
          //  liferay-dynamic-section:dynamic-section
          com.liferay.frontend.taglib.dynamic.section.servlet.taglib.DynamicSectionTag _jspx_th_liferay$1dynamic$1section_dynamic$1section_0 = (com.liferay.frontend.taglib.dynamic.section.servlet.taglib.DynamicSectionTag) _jspx_tagPool_liferay$1dynamic$1section_dynamic$1section_name.get(com.liferay.frontend.taglib.dynamic.section.servlet.taglib.DynamicSectionTag.class);
          _jspx_th_liferay$1dynamic$1section_dynamic$1section_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1dynamic$1section_dynamic$1section_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_liferay$1dynamic$1section_dynamic$1section_0.setName("com.liferay.document.library.web#/document_library/info_panel_file_entry.jsp#fileEntryOwner");
          int _jspx_eval_liferay$1dynamic$1section_dynamic$1section_0 = _jspx_th_liferay$1dynamic$1section_dynamic$1section_0.doStartTag();
          if (_jspx_eval_liferay$1dynamic$1section_dynamic$1section_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_liferay$1dynamic$1section_dynamic$1section_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_liferay$1dynamic$1section_dynamic$1section_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_liferay$1dynamic$1section_dynamic$1section_0.doInitBody();
            }
            do {
              out.write("\n\t\t\t\t<div class=\"autofit-row sidebar-panel widget-metadata\">\n\t\t\t\t\t<div class=\"autofit-col inline-item-before\">\n\n\t\t\t\t\t\t");

						User owner = UserLocalServiceUtil.fetchUser(fileEntry.getUserId());
						
              out.write("\n\n\t\t\t\t\t\t");
              //  liferay-ui:user-portrait
              com.liferay.taglib.ui.UserPortraitTag _jspx_th_liferay$1ui_user$1portrait_0 = (com.liferay.taglib.ui.UserPortraitTag) _jspx_tagPool_liferay$1ui_user$1portrait_user_nobody.get(com.liferay.taglib.ui.UserPortraitTag.class);
              _jspx_th_liferay$1ui_user$1portrait_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1ui_user$1portrait_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1dynamic$1section_dynamic$1section_0);
              _jspx_th_liferay$1ui_user$1portrait_0.setUser( owner );
              int _jspx_eval_liferay$1ui_user$1portrait_0 = _jspx_th_liferay$1ui_user$1portrait_0.doStartTag();
              if (_jspx_th_liferay$1ui_user$1portrait_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1ui_user$1portrait_user_nobody.reuse(_jspx_th_liferay$1ui_user$1portrait_0);
                return;
              }
              _jspx_tagPool_liferay$1ui_user$1portrait_user_nobody.reuse(_jspx_th_liferay$1ui_user$1portrait_0);
              out.write("\n\t\t\t\t\t</div>\n\n\t\t\t\t\t<div class=\"autofit-col autofit-col-expand\">\n\t\t\t\t\t\t<div class=\"autofit-row\">\n\t\t\t\t\t\t\t<div class=\"autofit-col autofit-col-expand\">\n\t\t\t\t\t\t\t\t<div class=\"component-title h4 username\">\n\t\t\t\t\t\t\t\t\t");
              //  c:if
              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_3 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
              _jspx_th_c_if_3.setPageContext(_jspx_page_context);
              _jspx_th_c_if_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1dynamic$1section_dynamic$1section_0);
              _jspx_th_c_if_3.setTest( owner != null );
              int _jspx_eval_c_if_3 = _jspx_th_c_if_3.doStartTag();
              if (_jspx_eval_c_if_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t\t\t\t\t\t<a href=\"");
                  out.print( owner.isDefaultUser() ? StringPool.BLANK : owner.getDisplayURL(themeDisplay) );
                  out.write('"');
                  out.write('>');
                  out.print( HtmlUtil.escape(owner.getFullName()) );
                  out.write("</a>\n\t\t\t\t\t\t\t\t\t");
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
              out.write("\n\t\t\t\t\t\t\t\t</div>\n\n\t\t\t\t\t\t\t\t<small class=\"text-muted\">\n\t\t\t\t\t\t\t\t\t");
              if (_jspx_meth_liferay$1ui_message_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1dynamic$1section_dynamic$1section_0, _jspx_page_context))
                return;
              out.write("\n\t\t\t\t\t\t\t\t</small>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</div>\n\t\t\t\t</div>\n\t\t\t");
              int evalDoAfterBody = _jspx_th_liferay$1dynamic$1section_dynamic$1section_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_liferay$1dynamic$1section_dynamic$1section_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
              out = _jspx_page_context.popBody();
          }
          if (_jspx_th_liferay$1dynamic$1section_dynamic$1section_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1dynamic$1section_dynamic$1section_name.reuse(_jspx_th_liferay$1dynamic$1section_dynamic$1section_0);
            return;
          }
          _jspx_tagPool_liferay$1dynamic$1section_dynamic$1section_name.reuse(_jspx_th_liferay$1dynamic$1section_dynamic$1section_0);
          out.write("\n\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_4 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_4.setPageContext(_jspx_page_context);
          _jspx_th_c_if_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_c_if_4.setTest( dlViewFileVersionDisplayContext.isDownloadLinkVisible() || dlViewFileVersionDisplayContext.isSharingLinkVisible() );
          int _jspx_eval_c_if_4 = _jspx_th_c_if_4.doStartTag();
          if (_jspx_eval_c_if_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t<div class=\"sidebar-section\">\n\t\t\t\t\t<div class=\"btn-group sidebar-panel\">\n\t\t\t\t\t\t");
              //  c:if
              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_5 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
              _jspx_th_c_if_5.setPageContext(_jspx_page_context);
              _jspx_th_c_if_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_4);
              _jspx_th_c_if_5.setTest( dlViewFileVersionDisplayContext.isDownloadLinkVisible() );
              int _jspx_eval_c_if_5 = _jspx_th_c_if_5.doStartTag();
              if (_jspx_eval_c_if_5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t\t\t");
                  //  c:choose
                  org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_0 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                  _jspx_th_c_choose_0.setPageContext(_jspx_page_context);
                  _jspx_th_c_choose_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
                  int _jspx_eval_c_choose_0 = _jspx_th_c_choose_0.doStartTag();
                  if (_jspx_eval_c_choose_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    do {
                      out.write("\n\t\t\t\t\t\t\t\t");
                      //  c:when
                      org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_0 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                      _jspx_th_c_when_0.setPageContext(_jspx_page_context);
                      _jspx_th_c_when_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
                      _jspx_th_c_when_0.setTest( PropsValues.DL_FILE_ENTRY_CONVERSIONS_ENABLED && DocumentConversionUtil.isEnabled() );
                      int _jspx_eval_c_when_0 = _jspx_th_c_when_0.doStartTag();
                      if (_jspx_eval_c_when_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\n\n\t\t\t\t\t\t\t\t\t");

									String[] conversions = DocumentConversionUtil.getConversions(fileVersion.getExtension());
									
                          out.write("\n\n\t\t\t\t\t\t\t\t\t");
                          //  c:choose
                          org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_1 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                          _jspx_th_c_choose_1.setPageContext(_jspx_page_context);
                          _jspx_th_c_choose_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
                          int _jspx_eval_c_choose_1 = _jspx_th_c_choose_1.doStartTag();
                          if (_jspx_eval_c_choose_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                            do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_1 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_1.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                              _jspx_th_c_when_1.setTest( conversions.length > 0 );
                              int _jspx_eval_c_when_1 = _jspx_th_c_when_1.doStartTag();
                              if (_jspx_eval_c_when_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t<div class=\"btn-group-item\" data-analytics-file-entry-id=\"");
                              out.print( String.valueOf(fileEntry.getFileEntryId()) );
                              out.write("\">\n\t\t\t\t\t\t\t\t\t\t\t\t");
                              //  clay:dropdown-menu
                              com.liferay.frontend.taglib.clay.servlet.taglib.soy.DropdownMenuTag _jspx_th_clay_dropdown$1menu_0 = (com.liferay.frontend.taglib.clay.servlet.taglib.soy.DropdownMenuTag) _jspx_tagPool_clay_dropdown$1menu_triggerCssClasses_style_label_dropdownItems_nobody.get(com.liferay.frontend.taglib.clay.servlet.taglib.soy.DropdownMenuTag.class);
                              _jspx_th_clay_dropdown$1menu_0.setPageContext(_jspx_page_context);
                              _jspx_th_clay_dropdown$1menu_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
                              _jspx_th_clay_dropdown$1menu_0.setDropdownItems(
														new JSPDropdownItemList(pageContext) {
															{
																ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);

																Map<String, Object> data = new HashMap<>();

																data.put("analytics-file-entry-id", String.valueOf(fileEntry.getFileEntryId()));

																add(
																	dropdownItem -> {
																		dropdownItem.setData(data);
																		dropdownItem.setHref(DLURLHelperUtil.getDownloadURL(fileEntry, fileVersion, themeDisplay, StringPool.BLANK, false, true));
																		dropdownItem.setLabel(LanguageUtil.get(request, "this-version"));
																		dropdownItem.setSeparator(true);
																	});

																addGroup(
																	dropdownGroupItem -> {
																		dropdownGroupItem.setDropdownItems(
																			new DropdownItemList() {
																				{
																					for (String conversion : conversions) {
																						add(
																							dropdownItem -> {
																								dropdownItem.setData(data);
																								dropdownItem.setHref(DLURLHelperUtil.getDownloadURL(fileEntry, fileVersion, themeDisplay, "&targetExtension=" + conversion));
																								dropdownItem.setLabel(StringUtil.toUpperCase(conversion));
																							});
																					}
																				}
																			});
																		dropdownGroupItem.setLabel(LanguageUtil.get(request, "convert-to"));
																	});
															}
														}
													);
                              _jspx_th_clay_dropdown$1menu_0.setLabel( LanguageUtil.get(request, "download") );
                              _jspx_th_clay_dropdown$1menu_0.setStyle("primary");
                              _jspx_th_clay_dropdown$1menu_0.setTriggerCssClasses("btn-sm");
                              int _jspx_eval_clay_dropdown$1menu_0 = _jspx_th_clay_dropdown$1menu_0.doStartTag();
                              if (_jspx_th_clay_dropdown$1menu_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_clay_dropdown$1menu_triggerCssClasses_style_label_dropdownItems_nobody.reuse(_jspx_th_clay_dropdown$1menu_0);
                              return;
                              }
                              _jspx_tagPool_clay_dropdown$1menu_triggerCssClasses_style_label_dropdownItems_nobody.reuse(_jspx_th_clay_dropdown$1menu_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t");
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
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  c:otherwise
                              org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_0 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                              _jspx_th_c_otherwise_0.setPageContext(_jspx_page_context);
                              _jspx_th_c_otherwise_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                              int _jspx_eval_c_otherwise_0 = _jspx_th_c_otherwise_0.doStartTag();
                              if (_jspx_eval_c_otherwise_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t");

											Map<String, String> data = new HashMap<>();

											data.put("analytics-file-entry-id", String.valueOf(fileEntry.getFileEntryId()));
											
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t<div class=\"btn-group-item\">\n\t\t\t\t\t\t\t\t\t\t\t\t");
                              //  clay:link
                              com.liferay.frontend.taglib.clay.servlet.taglib.soy.LinkTag _jspx_th_clay_link_0 = (com.liferay.frontend.taglib.clay.servlet.taglib.soy.LinkTag) _jspx_tagPool_clay_link_title_label_href_elementClasses_data_buttonStyle_nobody.get(com.liferay.frontend.taglib.clay.servlet.taglib.soy.LinkTag.class);
                              _jspx_th_clay_link_0.setPageContext(_jspx_page_context);
                              _jspx_th_clay_link_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
                              _jspx_th_clay_link_0.setButtonStyle("primary");
                              _jspx_th_clay_link_0.setData( data );
                              _jspx_th_clay_link_0.setElementClasses("btn-sm");
                              _jspx_th_clay_link_0.setHref( DLURLHelperUtil.getDownloadURL(fileEntry, fileVersion, themeDisplay, StringPool.BLANK, false, true) );
                              _jspx_th_clay_link_0.setLabel( LanguageUtil.get(resourceBundle, "download") );
                              _jspx_th_clay_link_0.setTitle( LanguageUtil.format(resourceBundle, "file-size-x", LanguageUtil.formatStorageSize(fileVersion.getSize(), locale), false) );
                              int _jspx_eval_clay_link_0 = _jspx_th_clay_link_0.doStartTag();
                              if (_jspx_th_clay_link_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_clay_link_title_label_href_elementClasses_data_buttonStyle_nobody.reuse(_jspx_th_clay_link_0);
                              return;
                              }
                              _jspx_tagPool_clay_link_title_label_href_elementClasses_data_buttonStyle_nobody.reuse(_jspx_th_clay_link_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t");
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
                              out.write("\n\t\t\t\t\t\t\t\t\t");
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
                          out.write("\n\t\t\t\t\t\t\t\t");
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
                      out.write("\n\t\t\t\t\t\t\t\t");
                      //  c:otherwise
                      org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_1 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                      _jspx_th_c_otherwise_1.setPageContext(_jspx_page_context);
                      _jspx_th_c_otherwise_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
                      int _jspx_eval_c_otherwise_1 = _jspx_th_c_otherwise_1.doStartTag();
                      if (_jspx_eval_c_otherwise_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\n\t\t\t\t\t\t\t\t\t<div class=\"btn-group-item\" data-analytics-file-entry-id=\"");
                          out.print( String.valueOf(fileEntry.getFileEntryId()) );
                          out.write("\">\n\t\t\t\t\t\t\t\t\t\t");
                          //  clay:link
                          com.liferay.frontend.taglib.clay.servlet.taglib.soy.LinkTag _jspx_th_clay_link_1 = (com.liferay.frontend.taglib.clay.servlet.taglib.soy.LinkTag) _jspx_tagPool_clay_link_title_label_href_elementClasses_buttonStyle_nobody.get(com.liferay.frontend.taglib.clay.servlet.taglib.soy.LinkTag.class);
                          _jspx_th_clay_link_1.setPageContext(_jspx_page_context);
                          _jspx_th_clay_link_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_1);
                          _jspx_th_clay_link_1.setButtonStyle("primary");
                          _jspx_th_clay_link_1.setElementClasses("btn-sm");
                          _jspx_th_clay_link_1.setHref( DLURLHelperUtil.getDownloadURL(fileEntry, fileVersion, themeDisplay, StringPool.BLANK, false, true) );
                          _jspx_th_clay_link_1.setLabel( LanguageUtil.get(resourceBundle, "download") );
                          _jspx_th_clay_link_1.setTitle( LanguageUtil.format(resourceBundle, "file-size-x", LanguageUtil.formatStorageSize(fileVersion.getSize(), locale), false) );
                          int _jspx_eval_clay_link_1 = _jspx_th_clay_link_1.doStartTag();
                          if (_jspx_th_clay_link_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                            _jspx_tagPool_clay_link_title_label_href_elementClasses_buttonStyle_nobody.reuse(_jspx_th_clay_link_1);
                            return;
                          }
                          _jspx_tagPool_clay_link_title_label_href_elementClasses_buttonStyle_nobody.reuse(_jspx_th_clay_link_1);
                          out.write("\n\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t");
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
                  out.write("\n\t\t\t\t\t\t");
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
              out.write("\n\n\t\t\t\t\t\t");
              //  c:if
              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_6 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
              _jspx_th_c_if_6.setPageContext(_jspx_page_context);
              _jspx_th_c_if_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_4);
              _jspx_th_c_if_6.setTest( dlViewFileVersionDisplayContext.isSharingLinkVisible() );
              int _jspx_eval_c_if_6 = _jspx_th_c_if_6.doStartTag();
              if (_jspx_eval_c_if_6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t\t\t<div class=\"btn-group-item\">\n\t\t\t\t\t\t\t\t");
                  //  liferay-sharing:button
                  com.liferay.sharing.taglib.servlet.taglib.SharingButtonTag _jspx_th_liferay$1sharing_button_0 = (com.liferay.sharing.taglib.servlet.taglib.SharingButtonTag) _jspx_tagPool_liferay$1sharing_button_classPK_className_nobody.get(com.liferay.sharing.taglib.servlet.taglib.SharingButtonTag.class);
                  _jspx_th_liferay$1sharing_button_0.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1sharing_button_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_6);
                  _jspx_th_liferay$1sharing_button_0.setClassName( DLFileEntryConstants.getClassName() );
                  _jspx_th_liferay$1sharing_button_0.setClassPK( fileEntry.getFileEntryId() );
                  int _jspx_eval_liferay$1sharing_button_0 = _jspx_th_liferay$1sharing_button_0.doStartTag();
                  if (_jspx_th_liferay$1sharing_button_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1sharing_button_classPK_className_nobody.reuse(_jspx_th_liferay$1sharing_button_0);
                    return;
                  }
                  _jspx_tagPool_liferay$1sharing_button_classPK_className_nobody.reuse(_jspx_th_liferay$1sharing_button_0);
                  out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
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
              out.write("\n\t\t\t\t\t</div>\n\n\t\t\t\t\t<div class=\"sidebar-panel\">\n\n\t\t\t\t\t\t");

						boolean isLatestVersion = fileVersion.equals(fileEntry.getLatestFileVersion());

						String urlLabel = null;

						if (isLatestVersion) {
							urlLabel = LanguageUtil.get(resourceBundle, "latest-version-url");
						}
						else {
							urlLabel = LanguageUtil.format(request, "version-x-url", fileVersion.getVersion());
						}

						String urlInputId = liferayPortletResponse.getNamespace() + "urlInput";

						Map<String, String> urlButtonData = new HashMap<>();

						urlButtonData.put("clipboard-target", "#" + urlInputId);
						
              out.write("\n\n\t\t\t\t\t\t<div class=\"form-group\">\n\t\t\t\t\t\t\t<label for=\"");
              out.print( urlInputId );
              out.write('"');
              out.write('>');
              out.print( urlLabel );
              out.write("</label>\n\n\t\t\t\t\t\t\t<div class=\"input-group input-group-sm\">\n\t\t\t\t\t\t\t\t<div class=\"input-group-item input-group-prepend\">\n\t\t\t\t\t\t\t\t\t<input class=\"form-control\" id=\"");
              out.print( urlInputId );
              out.write("\" value=\"");
              out.print( DLURLHelperUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, StringPool.BLANK, !isLatestVersion, true) );
              out.write("\" />\n\t\t\t\t\t\t\t\t</div>\n\n\t\t\t\t\t\t\t\t<span class=\"input-group-append input-group-item input-group-item-shrink\">\n\t\t\t\t\t\t\t\t\t");
              //  clay:button
              com.liferay.frontend.taglib.clay.servlet.taglib.soy.ButtonTag _jspx_th_clay_button_0 = (com.liferay.frontend.taglib.clay.servlet.taglib.soy.ButtonTag) _jspx_tagPool_clay_button_title_style_icon_elementClasses_data_nobody.get(com.liferay.frontend.taglib.clay.servlet.taglib.soy.ButtonTag.class);
              _jspx_th_clay_button_0.setPageContext(_jspx_page_context);
              _jspx_th_clay_button_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_4);
              _jspx_th_clay_button_0.setData( urlButtonData );
              _jspx_th_clay_button_0.setElementClasses("btn-secondary dm-infopanel-copy-clipboard lfr-portal-tooltip");
              _jspx_th_clay_button_0.setIcon("paste");
              _jspx_th_clay_button_0.setStyle(new Boolean(false));
              _jspx_th_clay_button_0.setTitle( LanguageUtil.get(resourceBundle, "copy-link") );
              int _jspx_eval_clay_button_0 = _jspx_th_clay_button_0.doStartTag();
              if (_jspx_th_clay_button_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_clay_button_title_style_icon_elementClasses_data_nobody.reuse(_jspx_th_clay_button_0);
                return;
              }
              _jspx_tagPool_clay_button_title_style_icon_elementClasses_data_nobody.reuse(_jspx_th_clay_button_0);
              out.write("\n\t\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t</div>\n\n\t\t\t\t\t\t");
              //  c:if
              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_7 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
              _jspx_th_c_if_7.setPageContext(_jspx_page_context);
              _jspx_th_c_if_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_4);
              _jspx_th_c_if_7.setTest( portletDisplay.isWebDAVEnabled() && fileEntry.isSupportsSocial() && isLatestVersion );
              int _jspx_eval_c_if_7 = _jspx_th_c_if_7.doStartTag();
              if (_jspx_eval_c_if_7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\n\t\t\t\t\t\t\t");

							String webDavHelpMessage = null;

							if (BrowserSnifferUtil.isWindows(request)) {
								webDavHelpMessage = LanguageUtil.format(request, "webdav-windows-help", new Object[] {"https://support.microsoft.com/en-us/kb/892211", "https://help.liferay.com/hc/en-us/articles/360028720352-Desktop-Access-to-Documents-and-Media"}, false);
							}
							else {
								webDavHelpMessage = LanguageUtil.format(request, "webdav-help", "https://help.liferay.com/hc/en-us/articles/360028720352-Desktop-Access-to-Documents-and-Media", false);
							}

							String webDavURLInputId = liferayPortletResponse.getNamespace() + "webDavURLInput";

							Map<String, String> webDavButtonData = new HashMap<>();

							webDavButtonData.put("clipboard-target", "#" + webDavURLInputId);
							
                  out.write("\n\n\t\t\t\t\t\t\t<div class=\"form-group\">\n\t\t\t\t\t\t\t\t<label for=\"");
                  out.print( webDavURLInputId );
                  out.write("\">\n\t\t\t\t\t\t\t\t\t");
                  //  liferay-ui:message
                  com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_2 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                  _jspx_th_liferay$1ui_message_2.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1ui_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                  _jspx_th_liferay$1ui_message_2.setKey( TextFormatter.format("webDavURL", TextFormatter.K) );
                  int _jspx_eval_liferay$1ui_message_2 = _jspx_th_liferay$1ui_message_2.doStartTag();
                  if (_jspx_th_liferay$1ui_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_2);
                    return;
                  }
                  _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_2);
                  out.write("\n\n\t\t\t\t\t\t\t\t\t");
                  //  liferay-ui:icon-help
                  com.liferay.taglib.ui.IconHelpTag _jspx_th_liferay$1ui_icon$1help_0 = (com.liferay.taglib.ui.IconHelpTag) _jspx_tagPool_liferay$1ui_icon$1help_message_nobody.get(com.liferay.taglib.ui.IconHelpTag.class);
                  _jspx_th_liferay$1ui_icon$1help_0.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1ui_icon$1help_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                  _jspx_th_liferay$1ui_icon$1help_0.setMessage( webDavHelpMessage );
                  int _jspx_eval_liferay$1ui_icon$1help_0 = _jspx_th_liferay$1ui_icon$1help_0.doStartTag();
                  if (_jspx_th_liferay$1ui_icon$1help_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1ui_icon$1help_message_nobody.reuse(_jspx_th_liferay$1ui_icon$1help_0);
                    return;
                  }
                  _jspx_tagPool_liferay$1ui_icon$1help_message_nobody.reuse(_jspx_th_liferay$1ui_icon$1help_0);
                  out.write("\n\t\t\t\t\t\t\t\t</label>\n\n\t\t\t\t\t\t\t\t<div class=\"input-group input-group-sm\">\n\t\t\t\t\t\t\t\t\t<div class=\"input-group-item input-group-prepend\">\n\t\t\t\t\t\t\t\t\t\t<input class=\"form-control\" id=\"");
                  out.print( webDavURLInputId );
                  out.write("\" value=\"");
                  out.print( DLURLHelperUtil.getWebDavURL(themeDisplay, fileEntry.getFolder(), fileEntry) );
                  out.write("\" />\n\t\t\t\t\t\t\t\t\t</div>\n\n\t\t\t\t\t\t\t\t\t<span class=\"input-group-append input-group-item input-group-item-shrink\">\n\t\t\t\t\t\t\t\t\t\t");
                  //  clay:button
                  com.liferay.frontend.taglib.clay.servlet.taglib.soy.ButtonTag _jspx_th_clay_button_1 = (com.liferay.frontend.taglib.clay.servlet.taglib.soy.ButtonTag) _jspx_tagPool_clay_button_title_style_icon_elementClasses_data_nobody.get(com.liferay.frontend.taglib.clay.servlet.taglib.soy.ButtonTag.class);
                  _jspx_th_clay_button_1.setPageContext(_jspx_page_context);
                  _jspx_th_clay_button_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
                  _jspx_th_clay_button_1.setData( webDavButtonData );
                  _jspx_th_clay_button_1.setElementClasses("btn-secondary dm-infopanel-copy-clipboard lfr-portal-tooltip");
                  _jspx_th_clay_button_1.setIcon("paste");
                  _jspx_th_clay_button_1.setStyle(new Boolean(false));
                  _jspx_th_clay_button_1.setTitle( LanguageUtil.get(resourceBundle, "copy-link") );
                  int _jspx_eval_clay_button_1 = _jspx_th_clay_button_1.doStartTag();
                  if (_jspx_th_clay_button_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_clay_button_title_style_icon_elementClasses_data_nobody.reuse(_jspx_th_clay_button_1);
                    return;
                  }
                  _jspx_tagPool_clay_button_title_style_icon_elementClasses_data_nobody.reuse(_jspx_th_clay_button_1);
                  out.write("\n\t\t\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
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
              out.write("\n\t\t\t\t\t</div>\n\t\t\t\t</div>\n\t\t\t");
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
          out.write("\n\n\t\t\t<dl class=\"sidebar-dl sidebar-section\">\n\t\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_8 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_8.setPageContext(_jspx_page_context);
          _jspx_th_c_if_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_c_if_8.setTest( fileVersion.getModel() instanceof DLFileVersion );
          int _jspx_eval_c_if_8 = _jspx_th_c_if_8.doStartTag();
          if (_jspx_eval_c_if_8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\n\t\t\t\t\t");

					DLFileVersion dlFileVersion = (DLFileVersion)fileVersion.getModel();

					DLFileEntryType dlFileEntryType = dlFileVersion.getDLFileEntryType();
					
              out.write("\n\n\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t");
              if (_jspx_meth_liferay$1ui_message_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_8, _jspx_page_context))
                return;
              out.write("\n\t\t\t\t\t</dt>\n\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t");
              out.print( HtmlUtil.escape(dlFileEntryType.getName(locale)) );
              out.write("\n\t\t\t\t\t</dd>\n\t\t\t\t");
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
          out.write("\n\n\t\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_9 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_9.setPageContext(_jspx_page_context);
          _jspx_th_c_if_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_c_if_9.setTest( Validator.isNotNull(fileVersion.getExtension()) );
          int _jspx_eval_c_if_9 = _jspx_th_c_if_9.doStartTag();
          if (_jspx_eval_c_if_9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t");
              if (_jspx_meth_liferay$1ui_message_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_9, _jspx_page_context))
                return;
              out.write("\n\t\t\t\t\t</dt>\n\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t");
              out.print( HtmlUtil.escape(fileVersion.getExtension()) );
              out.write("\n\t\t\t\t\t</dd>\n\t\t\t\t");
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
          out.write("\n\n\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t");
          if (_jspx_meth_liferay$1ui_message_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_section_0, _jspx_page_context))
            return;
          out.write("\n\t\t\t\t</dt>\n\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t");
          out.print( HtmlUtil.escape(LanguageUtil.formatStorageSize(fileVersion.getSize(), locale)) );
          out.write("\n\t\t\t\t</dd>\n\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t");
          if (_jspx_meth_liferay$1ui_message_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_section_0, _jspx_page_context))
            return;
          out.write("\n\t\t\t\t</dt>\n\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t");
          //  liferay-ui:message
          com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_7 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
          _jspx_th_liferay$1ui_message_7.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1ui_message_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_liferay$1ui_message_7.setArguments( new Object[] {dateFormatDateTime.format(fileVersion.getModifiedDate()), HtmlUtil.escape(fileVersion.getStatusByUserName())} );
          _jspx_th_liferay$1ui_message_7.setKey("x-by-x");
          _jspx_th_liferay$1ui_message_7.setTranslateArguments( false );
          int _jspx_eval_liferay$1ui_message_7 = _jspx_th_liferay$1ui_message_7.doStartTag();
          if (_jspx_th_liferay$1ui_message_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_7);
            return;
          }
          _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_7);
          out.write("\n\t\t\t\t</dd>\n\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t");
          if (_jspx_meth_liferay$1ui_message_8((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_section_0, _jspx_page_context))
            return;
          out.write("\n\t\t\t\t</dt>\n\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t");
          //  liferay-ui:message
          com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_9 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
          _jspx_th_liferay$1ui_message_9.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1ui_message_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_liferay$1ui_message_9.setArguments( new Object[] {dateFormatDateTime.format(fileVersion.getCreateDate()), HtmlUtil.escape(fileVersion.getUserName())} );
          _jspx_th_liferay$1ui_message_9.setKey("x-by-x");
          _jspx_th_liferay$1ui_message_9.setTranslateArguments( false );
          int _jspx_eval_liferay$1ui_message_9 = _jspx_th_liferay$1ui_message_9.doStartTag();
          if (_jspx_th_liferay$1ui_message_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_9);
            return;
          }
          _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_9);
          out.write("\n\t\t\t\t</dd>\n\n\t\t\t\t");

				request.setAttribute("info_panel_location.jsp-parentFolder", fileEntry.getFolder());
				
          out.write("\n\n\t\t\t\t");
          //  liferay-util:include
          com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_1 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
          _jspx_th_liferay$1util_include_1.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1util_include_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_liferay$1util_include_1.setPage("/document_library/info_panel_location.jsp");
          _jspx_th_liferay$1util_include_1.setServletContext( application );
          int _jspx_eval_liferay$1util_include_1 = _jspx_th_liferay$1util_include_1.doStartTag();
          if (_jspx_th_liferay$1util_include_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
            return;
          }
          _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
          out.write("\n\n\t\t\t\t");
          //  liferay-asset:asset-tags-available
          com.liferay.asset.taglib.servlet.taglib.AssetTagsAvailableTag _jspx_th_liferay$1asset_asset$1tags$1available_0 = (com.liferay.asset.taglib.servlet.taglib.AssetTagsAvailableTag) _jspx_tagPool_liferay$1asset_asset$1tags$1available_classPK_className.get(com.liferay.asset.taglib.servlet.taglib.AssetTagsAvailableTag.class);
          _jspx_th_liferay$1asset_asset$1tags$1available_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1asset_asset$1tags$1available_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_liferay$1asset_asset$1tags$1available_0.setClassName( DLFileEntryConstants.getClassName() );
          _jspx_th_liferay$1asset_asset$1tags$1available_0.setClassPK( assetClassPK );
          int _jspx_eval_liferay$1asset_asset$1tags$1available_0 = _jspx_th_liferay$1asset_asset$1tags$1available_0.doStartTag();
          if (_jspx_eval_liferay$1asset_asset$1tags$1available_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t");
            if (_jspx_meth_liferay$1ui_message_10((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1asset_asset$1tags$1available_0, _jspx_page_context))
              return;
            out.write("\n\t\t\t\t\t</dt>\n\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t");
            //  liferay-asset:asset-tags-summary
            com.liferay.asset.taglib.servlet.taglib.AssetTagsSummaryTag _jspx_th_liferay$1asset_asset$1tags$1summary_0 = (com.liferay.asset.taglib.servlet.taglib.AssetTagsSummaryTag) _jspx_tagPool_liferay$1asset_asset$1tags$1summary_classPK_className_nobody.get(com.liferay.asset.taglib.servlet.taglib.AssetTagsSummaryTag.class);
            _jspx_th_liferay$1asset_asset$1tags$1summary_0.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1asset_asset$1tags$1summary_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1asset_asset$1tags$1available_0);
            _jspx_th_liferay$1asset_asset$1tags$1summary_0.setClassName( DLFileEntryConstants.getClassName() );
            _jspx_th_liferay$1asset_asset$1tags$1summary_0.setClassPK( assetClassPK );
            int _jspx_eval_liferay$1asset_asset$1tags$1summary_0 = _jspx_th_liferay$1asset_asset$1tags$1summary_0.doStartTag();
            if (_jspx_th_liferay$1asset_asset$1tags$1summary_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1asset_asset$1tags$1summary_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1tags$1summary_0);
              return;
            }
            _jspx_tagPool_liferay$1asset_asset$1tags$1summary_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1tags$1summary_0);
            out.write("\n\t\t\t\t\t</dd>\n\t\t\t\t");
          }
          if (_jspx_th_liferay$1asset_asset$1tags$1available_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1asset_asset$1tags$1available_classPK_className.reuse(_jspx_th_liferay$1asset_asset$1tags$1available_0);
            return;
          }
          _jspx_tagPool_liferay$1asset_asset$1tags$1available_classPK_className.reuse(_jspx_th_liferay$1asset_asset$1tags$1available_0);
          out.write("\n\n\t\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_10 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_10.setPageContext(_jspx_page_context);
          _jspx_th_c_if_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_c_if_10.setTest( dlPortletInstanceSettings.isEnableRatings() && fileEntry.isSupportsSocial() );
          int _jspx_eval_c_if_10 = _jspx_th_c_if_10.doStartTag();
          if (_jspx_eval_c_if_10 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t");
              if (_jspx_meth_liferay$1ui_message_11((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_10, _jspx_page_context))
                return;
              out.write("\n\t\t\t\t\t</dt>\n\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t");
              //  liferay-ui:ratings
              com.liferay.taglib.ui.RatingsTag _jspx_th_liferay$1ui_ratings_0 = (com.liferay.taglib.ui.RatingsTag) _jspx_tagPool_liferay$1ui_ratings_inTrash_classPK_className_nobody.get(com.liferay.taglib.ui.RatingsTag.class);
              _jspx_th_liferay$1ui_ratings_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1ui_ratings_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_10);
              _jspx_th_liferay$1ui_ratings_0.setClassName( DLFileEntryConstants.getClassName() );
              _jspx_th_liferay$1ui_ratings_0.setClassPK( fileEntry.getFileEntryId() );
              _jspx_th_liferay$1ui_ratings_0.setInTrash( fileEntry.isInTrash() );
              int _jspx_eval_liferay$1ui_ratings_0 = _jspx_th_liferay$1ui_ratings_0.doStartTag();
              if (_jspx_th_liferay$1ui_ratings_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1ui_ratings_inTrash_classPK_className_nobody.reuse(_jspx_th_liferay$1ui_ratings_0);
                return;
              }
              _jspx_tagPool_liferay$1ui_ratings_inTrash_classPK_className_nobody.reuse(_jspx_th_liferay$1ui_ratings_0);
              out.write("\n\t\t\t\t\t</dd>\n\t\t\t\t");
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
          _jspx_th_c_if_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_c_if_11.setTest( Validator.isNotNull(fileEntry.getDescription()) );
          int _jspx_eval_c_if_11 = _jspx_th_c_if_11.doStartTag();
          if (_jspx_eval_c_if_11 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t");
              if (_jspx_meth_liferay$1ui_message_12((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_11, _jspx_page_context))
                return;
              out.write("\n\t\t\t\t\t</dt>\n\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t");
              out.print( HtmlUtil.replaceNewLine(HtmlUtil.escape(fileVersion.getDescription())) );
              out.write("\n\t\t\t\t\t</dd>\n\t\t\t\t");
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
          //  liferay-asset:asset-categories-available
          com.liferay.asset.taglib.servlet.taglib.AssetCategoriesAvailableTag _jspx_th_liferay$1asset_asset$1categories$1available_0 = (com.liferay.asset.taglib.servlet.taglib.AssetCategoriesAvailableTag) _jspx_tagPool_liferay$1asset_asset$1categories$1available_classPK_className.get(com.liferay.asset.taglib.servlet.taglib.AssetCategoriesAvailableTag.class);
          _jspx_th_liferay$1asset_asset$1categories$1available_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1asset_asset$1categories$1available_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_liferay$1asset_asset$1categories$1available_0.setClassName( DLFileEntryConstants.getClassName() );
          _jspx_th_liferay$1asset_asset$1categories$1available_0.setClassPK( assetClassPK );
          int _jspx_eval_liferay$1asset_asset$1categories$1available_0 = _jspx_th_liferay$1asset_asset$1categories$1available_0.doStartTag();
          if (_jspx_eval_liferay$1asset_asset$1categories$1available_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t");
            if (_jspx_meth_liferay$1ui_message_13((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1asset_asset$1categories$1available_0, _jspx_page_context))
              return;
            out.write("\n\t\t\t\t\t</dt>\n\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t");
            //  liferay-asset:asset-categories-summary
            com.liferay.asset.taglib.servlet.taglib.AssetCategoriesSummaryTag _jspx_th_liferay$1asset_asset$1categories$1summary_0 = (com.liferay.asset.taglib.servlet.taglib.AssetCategoriesSummaryTag) _jspx_tagPool_liferay$1asset_asset$1categories$1summary_displayStyle_classPK_className_nobody.get(com.liferay.asset.taglib.servlet.taglib.AssetCategoriesSummaryTag.class);
            _jspx_th_liferay$1asset_asset$1categories$1summary_0.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1asset_asset$1categories$1summary_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1asset_asset$1categories$1available_0);
            _jspx_th_liferay$1asset_asset$1categories$1summary_0.setClassName( DLFileEntryConstants.getClassName() );
            _jspx_th_liferay$1asset_asset$1categories$1summary_0.setClassPK( assetClassPK );
            _jspx_th_liferay$1asset_asset$1categories$1summary_0.setDisplayStyle("simple-category");
            int _jspx_eval_liferay$1asset_asset$1categories$1summary_0 = _jspx_th_liferay$1asset_asset$1categories$1summary_0.doStartTag();
            if (_jspx_th_liferay$1asset_asset$1categories$1summary_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1asset_asset$1categories$1summary_displayStyle_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1categories$1summary_0);
              return;
            }
            _jspx_tagPool_liferay$1asset_asset$1categories$1summary_displayStyle_classPK_className_nobody.reuse(_jspx_th_liferay$1asset_asset$1categories$1summary_0);
            out.write("\n\t\t\t\t\t</dd>\n\t\t\t\t");
          }
          if (_jspx_th_liferay$1asset_asset$1categories$1available_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1asset_asset$1categories$1available_classPK_className.reuse(_jspx_th_liferay$1asset_asset$1categories$1available_0);
            return;
          }
          _jspx_tagPool_liferay$1asset_asset$1categories$1available_classPK_className.reuse(_jspx_th_liferay$1asset_asset$1categories$1available_0);
          out.write("\n\t\t\t</dl>\n\n\t\t\t");

			AssetEntry layoutAssetEntry = AssetEntryLocalServiceUtil.fetchEntry(DLFileEntryConstants.getClassName(), assetClassPK);
			
          out.write("\n\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_12 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_12.setPageContext(_jspx_page_context);
          _jspx_th_c_if_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_c_if_12.setTest( (layoutAssetEntry != null) && dlPortletInstanceSettings.isEnableRelatedAssets() && fileEntry.isSupportsSocial() );
          int _jspx_eval_c_if_12 = _jspx_th_c_if_12.doStartTag();
          if (_jspx_eval_c_if_12 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t");
              //  liferay-asset:asset-links
              com.liferay.asset.taglib.servlet.taglib.AssetLinksTag _jspx_th_liferay$1asset_asset$1links_0 = (com.liferay.asset.taglib.servlet.taglib.AssetLinksTag) _jspx_tagPool_liferay$1asset_asset$1links_assetEntryId_nobody.get(com.liferay.asset.taglib.servlet.taglib.AssetLinksTag.class);
              _jspx_th_liferay$1asset_asset$1links_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1asset_asset$1links_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_12);
              _jspx_th_liferay$1asset_asset$1links_0.setAssetEntryId( layoutAssetEntry.getEntryId() );
              int _jspx_eval_liferay$1asset_asset$1links_0 = _jspx_th_liferay$1asset_asset$1links_0.doStartTag();
              if (_jspx_th_liferay$1asset_asset$1links_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1asset_asset$1links_assetEntryId_nobody.reuse(_jspx_th_liferay$1asset_asset$1links_0);
                return;
              }
              _jspx_tagPool_liferay$1asset_asset$1links_assetEntryId_nobody.reuse(_jspx_th_liferay$1asset_asset$1links_0);
              out.write("\n\t\t\t");
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
          out.write("\n\n\t\t\t");
          //  liferay-ui:panel-container
          com.liferay.taglib.ui.PanelContainerTag _jspx_th_liferay$1ui_panel$1container_0 = (com.liferay.taglib.ui.PanelContainerTag) _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_extended_cssClass.get(com.liferay.taglib.ui.PanelContainerTag.class);
          _jspx_th_liferay$1ui_panel$1container_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1ui_panel$1container_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
          _jspx_th_liferay$1ui_panel$1container_0.setCssClass("metadata-panel-container");
          _jspx_th_liferay$1ui_panel$1container_0.setExtended( true );
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
              out.write("\n\t\t\t\t");
              //  c:if
              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_13 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
              _jspx_th_c_if_13.setPageContext(_jspx_page_context);
              _jspx_th_c_if_13.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel$1container_0);
              _jspx_th_c_if_13.setTest( dlViewFileVersionDisplayContext.getDDMStructuresCount() > 0 );
              int _jspx_eval_c_if_13 = _jspx_th_c_if_13.doStartTag();
              if (_jspx_eval_c_if_13 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\n\t\t\t\t\t");

					try {
						List<DDMStructure> ddmStructures = dlViewFileVersionDisplayContext.getDDMStructures();

						for (DDMStructure ddmStructure : ddmStructures) {
							DDMFormValues ddmFormValues = null;

							List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<DDMFormFieldValue>();

							try {
								ddmFormValues = dlViewFileVersionDisplayContext.getDDMFormValues(ddmStructure);

								ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
							}
							catch (Exception e) {
							}
					
                  out.write("\n\n\t\t\t\t\t\t\t");
                  //  c:if
                  org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_14 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                  _jspx_th_c_if_14.setPageContext(_jspx_page_context);
                  _jspx_th_c_if_14.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_13);
                  _jspx_th_c_if_14.setTest( !ddmFormFieldValues.isEmpty() );
                  int _jspx_eval_c_if_14 = _jspx_th_c_if_14.doStartTag();
                  if (_jspx_eval_c_if_14 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    do {
                      out.write("\n\t\t\t\t\t\t\t\t");
                      //  liferay-ui:panel
                      com.liferay.taglib.ui.PanelTag _jspx_th_liferay$1ui_panel_0 = (com.liferay.taglib.ui.PanelTag) _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass_collapsible.get(com.liferay.taglib.ui.PanelTag.class);
                      _jspx_th_liferay$1ui_panel_0.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1ui_panel_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_14);
                      _jspx_th_liferay$1ui_panel_0.setCollapsible( true );
                      _jspx_th_liferay$1ui_panel_0.setCssClass("metadata");
                      _jspx_th_liferay$1ui_panel_0.setDefaultState("closed");
                      _jspx_th_liferay$1ui_panel_0.setExtended( true );
                      _jspx_th_liferay$1ui_panel_0.setId( "documentLibraryMetadataPanel" + StringPool.UNDERLINE + ddmStructure.getStructureId() );
                      _jspx_th_liferay$1ui_panel_0.setMarkupView("lexicon");
                      _jspx_th_liferay$1ui_panel_0.setPersistState( true );
                      _jspx_th_liferay$1ui_panel_0.setTitle( HtmlUtil.escape(ddmStructure.getName(locale)) );
                      int _jspx_eval_liferay$1ui_panel_0 = _jspx_th_liferay$1ui_panel_0.doStartTag();
                      if (_jspx_eval_liferay$1ui_panel_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        out.write("\n\t\t\t\t\t\t\t\t\t");
                        //  liferay-ddm:html
                        com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag _jspx_th_liferay$1ddm_html_0 = (com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag) _jspx_tagPool_liferay$1ddm_html_showEmptyFieldLabel_requestedLocale_readOnly_groupId_fieldsNamespace_ddmFormValues_classPK_classNameId_nobody.get(com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag.class);
                        _jspx_th_liferay$1ddm_html_0.setPageContext(_jspx_page_context);
                        _jspx_th_liferay$1ddm_html_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_0);
                        _jspx_th_liferay$1ddm_html_0.setClassNameId( PortalUtil.getClassNameId(com.liferay.dynamic.data.mapping.model.DDMStructure.class) );
                        _jspx_th_liferay$1ddm_html_0.setClassPK( ddmStructure.getPrimaryKey() );
                        _jspx_th_liferay$1ddm_html_0.setDdmFormValues( ddmFormValues );
                        _jspx_th_liferay$1ddm_html_0.setFieldsNamespace( String.valueOf(ddmStructure.getPrimaryKey()) );
                        _jspx_th_liferay$1ddm_html_0.setGroupId( fileVersion.getGroupId() );
                        _jspx_th_liferay$1ddm_html_0.setReadOnly( true );
                        _jspx_th_liferay$1ddm_html_0.setRequestedLocale( locale );
                        _jspx_th_liferay$1ddm_html_0.setShowEmptyFieldLabel( false );
                        int _jspx_eval_liferay$1ddm_html_0 = _jspx_th_liferay$1ddm_html_0.doStartTag();
                        if (_jspx_th_liferay$1ddm_html_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_liferay$1ddm_html_showEmptyFieldLabel_requestedLocale_readOnly_groupId_fieldsNamespace_ddmFormValues_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1ddm_html_0);
                          return;
                        }
                        _jspx_tagPool_liferay$1ddm_html_showEmptyFieldLabel_requestedLocale_readOnly_groupId_fieldsNamespace_ddmFormValues_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1ddm_html_0);
                        out.write("\n\t\t\t\t\t\t\t\t");
                      }
                      if (_jspx_th_liferay$1ui_panel_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass_collapsible.reuse(_jspx_th_liferay$1ui_panel_0);
                        return;
                      }
                      _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_extended_defaultState_cssClass_collapsible.reuse(_jspx_th_liferay$1ui_panel_0);
                      out.write("\n\t\t\t\t\t\t\t");
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
                  out.write("\n\n\t\t\t\t\t");

						}
					}
					catch (Exception e) {
					}
					
                  out.write("\n\n\t\t\t\t");
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
              //  liferay-expando:custom-attributes-available
              com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag _jspx_th_liferay$1expando_custom$1attributes$1available_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag) _jspx_tagPool_liferay$1expando_custom$1attributes$1available_editable_classPK_className.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag.class);
              _jspx_th_liferay$1expando_custom$1attributes$1available_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1expando_custom$1attributes$1available_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel$1container_0);
              _jspx_th_liferay$1expando_custom$1attributes$1available_0.setClassName( DLFileEntryConstants.getClassName() );
              _jspx_th_liferay$1expando_custom$1attributes$1available_0.setClassPK( fileVersion.getFileVersionId() );
              _jspx_th_liferay$1expando_custom$1attributes$1available_0.setEditable( false );
              int _jspx_eval_liferay$1expando_custom$1attributes$1available_0 = _jspx_th_liferay$1expando_custom$1attributes$1available_0.doStartTag();
              if (_jspx_eval_liferay$1expando_custom$1attributes$1available_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t");
                //  liferay-ui:panel
                com.liferay.taglib.ui.PanelTag _jspx_th_liferay$1ui_panel_1 = (com.liferay.taglib.ui.PanelTag) _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_defaultState_cssClass_collapsible.get(com.liferay.taglib.ui.PanelTag.class);
                _jspx_th_liferay$1ui_panel_1.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_panel_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1expando_custom$1attributes$1available_0);
                _jspx_th_liferay$1ui_panel_1.setCollapsible( true );
                _jspx_th_liferay$1ui_panel_1.setCssClass("lfr-custom-fields");
                _jspx_th_liferay$1ui_panel_1.setDefaultState("closed");
                _jspx_th_liferay$1ui_panel_1.setId("documentLibraryCustomFieldsPanel");
                _jspx_th_liferay$1ui_panel_1.setMarkupView("lexicon");
                _jspx_th_liferay$1ui_panel_1.setPersistState( true );
                _jspx_th_liferay$1ui_panel_1.setTitle("custom-fields");
                int _jspx_eval_liferay$1ui_panel_1 = _jspx_th_liferay$1ui_panel_1.doStartTag();
                if (_jspx_eval_liferay$1ui_panel_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n\t\t\t\t\t\t");
                  //  liferay-expando:custom-attribute-list
                  com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag _jspx_th_liferay$1expando_custom$1attribute$1list_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag) _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag.class);
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_1);
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setClassName( DLFileEntryConstants.getClassName() );
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setClassPK( fileVersion.getFileVersionId() );
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setEditable( false );
                  _jspx_th_liferay$1expando_custom$1attribute$1list_0.setLabel( true );
                  int _jspx_eval_liferay$1expando_custom$1attribute$1list_0 = _jspx_th_liferay$1expando_custom$1attribute$1list_0.doStartTag();
                  if (_jspx_th_liferay$1expando_custom$1attribute$1list_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.reuse(_jspx_th_liferay$1expando_custom$1attribute$1list_0);
                    return;
                  }
                  _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.reuse(_jspx_th_liferay$1expando_custom$1attribute$1list_0);
                  out.write("\n\t\t\t\t\t");
                }
                if (_jspx_th_liferay$1ui_panel_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_defaultState_cssClass_collapsible.reuse(_jspx_th_liferay$1ui_panel_1);
                  return;
                }
                _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_defaultState_cssClass_collapsible.reuse(_jspx_th_liferay$1ui_panel_1);
                out.write("\n\t\t\t\t");
              }
              if (_jspx_th_liferay$1expando_custom$1attributes$1available_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1expando_custom$1attributes$1available_editable_classPK_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
                return;
              }
              _jspx_tagPool_liferay$1expando_custom$1attributes$1available_editable_classPK_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
              out.write("\n\n\t\t\t\t");

				try {
					List<DDMStructure> ddmStructures = DDMStructureManagerUtil.getClassStructures(company.getCompanyId(), PortalUtil.getClassNameId(RawMetadataProcessor.class), DDMStructureManager.STRUCTURE_COMPARATOR_STRUCTURE_KEY);

					for (DDMStructure ddmStructure : ddmStructures) {
						DDMFormValues ddmFormValues = null;

						DLFileEntryMetadata fileEntryMetadata = DLFileEntryMetadataLocalServiceUtil.fetchFileEntryMetadata(ddmStructure.getStructureId(), fileVersion.getFileVersionId());

						if (fileEntryMetadata != null) {
							ddmFormValues = dlViewFileVersionDisplayContext.getDDMFormValues(fileEntryMetadata.getDDMStorageId());
						}

						if (ddmFormValues != null) {
				
              out.write("\n\n\t\t\t\t\t\t\t");
              //  liferay-ui:panel
              com.liferay.taglib.ui.PanelTag _jspx_th_liferay$1ui_panel_2 = (com.liferay.taglib.ui.PanelTag) _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_defaultState_cssClass_collapsible.get(com.liferay.taglib.ui.PanelTag.class);
              _jspx_th_liferay$1ui_panel_2.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1ui_panel_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel$1container_0);
              _jspx_th_liferay$1ui_panel_2.setCollapsible( true );
              _jspx_th_liferay$1ui_panel_2.setCssClass("lfr-asset-metadata");
              _jspx_th_liferay$1ui_panel_2.setDefaultState("closed");
              _jspx_th_liferay$1ui_panel_2.setId( "documentLibraryMetadataPanel" + StringPool.UNDERLINE + ddmStructure.getStructureId() );
              _jspx_th_liferay$1ui_panel_2.setMarkupView("lexicon");
              _jspx_th_liferay$1ui_panel_2.setPersistState( true );
              _jspx_th_liferay$1ui_panel_2.setTitle( "metadata." + ddmStructure.getStructureKey() );
              int _jspx_eval_liferay$1ui_panel_2 = _jspx_th_liferay$1ui_panel_2.doStartTag();
              if (_jspx_eval_liferay$1ui_panel_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t\t\t\t\t");
                //  liferay-ddm:html
                com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag _jspx_th_liferay$1ddm_html_1 = (com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag) _jspx_tagPool_liferay$1ddm_html_showEmptyFieldLabel_requestedLocale_readOnly_groupId_fieldsNamespace_ddmFormValues_classPK_classNameId_nobody.get(com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag.class);
                _jspx_th_liferay$1ddm_html_1.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ddm_html_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_panel_2);
                _jspx_th_liferay$1ddm_html_1.setClassNameId( PortalUtil.getClassNameId(com.liferay.dynamic.data.mapping.model.DDMStructure.class) );
                _jspx_th_liferay$1ddm_html_1.setClassPK( ddmStructure.getPrimaryKey() );
                _jspx_th_liferay$1ddm_html_1.setDdmFormValues( ddmFormValues );
                _jspx_th_liferay$1ddm_html_1.setFieldsNamespace( String.valueOf(ddmStructure.getPrimaryKey()) );
                _jspx_th_liferay$1ddm_html_1.setGroupId( fileVersion.getGroupId() );
                _jspx_th_liferay$1ddm_html_1.setReadOnly( true );
                _jspx_th_liferay$1ddm_html_1.setRequestedLocale( ddmFormValues.getDefaultLocale() );
                _jspx_th_liferay$1ddm_html_1.setShowEmptyFieldLabel( false );
                int _jspx_eval_liferay$1ddm_html_1 = _jspx_th_liferay$1ddm_html_1.doStartTag();
                if (_jspx_th_liferay$1ddm_html_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ddm_html_showEmptyFieldLabel_requestedLocale_readOnly_groupId_fieldsNamespace_ddmFormValues_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1ddm_html_1);
                  return;
                }
                _jspx_tagPool_liferay$1ddm_html_showEmptyFieldLabel_requestedLocale_readOnly_groupId_fieldsNamespace_ddmFormValues_classPK_classNameId_nobody.reuse(_jspx_th_liferay$1ddm_html_1);
                out.write("\n\t\t\t\t\t\t\t");
              }
              if (_jspx_th_liferay$1ui_panel_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_defaultState_cssClass_collapsible.reuse(_jspx_th_liferay$1ui_panel_2);
                return;
              }
              _jspx_tagPool_liferay$1ui_panel_title_persistState_markupView_id_defaultState_cssClass_collapsible.reuse(_jspx_th_liferay$1ui_panel_2);
              out.write("\n\n\t\t\t\t");

						}
					}
				}
				catch (Exception e) {
				}
				
              out.write("\n\n\t\t\t");
              int evalDoAfterBody = _jspx_th_liferay$1ui_panel$1container_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_liferay$1ui_panel$1container_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
              out = _jspx_page_context.popBody();
          }
          if (_jspx_th_liferay$1ui_panel$1container_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_extended_cssClass.reuse(_jspx_th_liferay$1ui_panel$1container_0);
            return;
          }
          _jspx_tagPool_liferay$1ui_panel$1container_persistState_markupView_extended_cssClass.reuse(_jspx_th_liferay$1ui_panel$1container_0);
          out.write("\n\t\t");
        }
        if (_jspx_th_liferay$1ui_section_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_0);
          return;
        }
        _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_0);
        out.write("\n\n\t\t");
        //  c:if
        org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_15 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
        _jspx_th_c_if_15.setPageContext(_jspx_page_context);
        _jspx_th_c_if_15.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_tabs_0);
        _jspx_th_c_if_15.setTest( dlViewFileVersionDisplayContext.isVersionInfoVisible() );
        int _jspx_eval_c_if_15 = _jspx_th_c_if_15.doStartTag();
        if (_jspx_eval_c_if_15 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          do {
            out.write("\n\t\t\t");
            //  liferay-ui:section
            com.liferay.taglib.ui.SectionTag _jspx_th_liferay$1ui_section_1 = (com.liferay.taglib.ui.SectionTag) _jspx_tagPool_liferay$1ui_section.get(com.liferay.taglib.ui.SectionTag.class);
            _jspx_th_liferay$1ui_section_1.setPageContext(_jspx_page_context);
            _jspx_th_liferay$1ui_section_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_15);
            int _jspx_eval_liferay$1ui_section_1 = _jspx_th_liferay$1ui_section_1.doStartTag();
            if (_jspx_eval_liferay$1ui_section_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              java.lang.String sectionParam = null;
              java.lang.String sectionName = null;
              java.lang.Boolean sectionSelected = null;
              java.lang.String sectionScroll = null;
              java.lang.String sectionRedirectParams = null;
              sectionParam = (java.lang.String) _jspx_page_context.findAttribute("sectionParam");
              sectionName = (java.lang.String) _jspx_page_context.findAttribute("sectionName");
              sectionSelected = (java.lang.Boolean) _jspx_page_context.findAttribute("sectionSelected");
              sectionScroll = (java.lang.String) _jspx_page_context.findAttribute("sectionScroll");
              sectionRedirectParams = (java.lang.String) _jspx_page_context.findAttribute("sectionRedirectParams");
              out.write("\n\n\t\t\t\t");

				request.setAttribute("info_panel.jsp-fileEntry", fileEntry);
				
              out.write("\n\n\t\t\t\t");
              //  liferay-util:include
              com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_2 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
              _jspx_th_liferay$1util_include_2.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_include_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_1);
              _jspx_th_liferay$1util_include_2.setPage("/document_library/file_entry_history.jsp");
              _jspx_th_liferay$1util_include_2.setServletContext( application );
              int _jspx_eval_liferay$1util_include_2 = _jspx_th_liferay$1util_include_2.doStartTag();
              if (_jspx_th_liferay$1util_include_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
                return;
              }
              _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
              out.write("\n\t\t\t");
            }
            if (_jspx_th_liferay$1ui_section_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_1);
              return;
            }
            _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_1);
            out.write("\n\t\t");
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
        out.write('\n');
        out.write('	');
      }
      if (_jspx_th_liferay$1ui_tabs_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.reuse(_jspx_th_liferay$1ui_tabs_0);
        return;
      }
      _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.reuse(_jspx_th_liferay$1ui_tabs_0);
      out.write("\n</div>\n\n");
      if (_jspx_meth_liferay$1frontend_component_0(_jspx_page_context))
        return;
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

  private boolean _jspx_meth_liferay$1ui_message_1(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1dynamic$1section_dynamic$1section_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_1 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_1.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1dynamic$1section_dynamic$1section_0);
    _jspx_th_liferay$1ui_message_1.setKey("owner");
    int _jspx_eval_liferay$1ui_message_1 = _jspx_th_liferay$1ui_message_1.doStartTag();
    if (_jspx_th_liferay$1ui_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_3(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_8, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_3 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_3.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_8);
    _jspx_th_liferay$1ui_message_3.setKey("document-type");
    int _jspx_eval_liferay$1ui_message_3 = _jspx_th_liferay$1ui_message_3.doStartTag();
    if (_jspx_th_liferay$1ui_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_3);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_3);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_4(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_9, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_4 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_4.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_9);
    _jspx_th_liferay$1ui_message_4.setKey("extension");
    int _jspx_eval_liferay$1ui_message_4 = _jspx_th_liferay$1ui_message_4.doStartTag();
    if (_jspx_th_liferay$1ui_message_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_4);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_4);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_5(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_section_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_5 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_5.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
    _jspx_th_liferay$1ui_message_5.setKey("size");
    int _jspx_eval_liferay$1ui_message_5 = _jspx_th_liferay$1ui_message_5.doStartTag();
    if (_jspx_th_liferay$1ui_message_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_5);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_5);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_6(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_section_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_6 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_6.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
    _jspx_th_liferay$1ui_message_6.setKey("modified");
    int _jspx_eval_liferay$1ui_message_6 = _jspx_th_liferay$1ui_message_6.doStartTag();
    if (_jspx_th_liferay$1ui_message_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_6);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_6);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_8(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_section_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_8 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_8.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
    _jspx_th_liferay$1ui_message_8.setKey("created");
    int _jspx_eval_liferay$1ui_message_8 = _jspx_th_liferay$1ui_message_8.doStartTag();
    if (_jspx_th_liferay$1ui_message_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_8);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_8);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_10(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1asset_asset$1tags$1available_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_10 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_10.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1asset_asset$1tags$1available_0);
    _jspx_th_liferay$1ui_message_10.setKey("tags");
    int _jspx_eval_liferay$1ui_message_10 = _jspx_th_liferay$1ui_message_10.doStartTag();
    if (_jspx_th_liferay$1ui_message_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_10);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_10);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_11(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_10, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_11 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_11.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_10);
    _jspx_th_liferay$1ui_message_11.setKey("ratings");
    int _jspx_eval_liferay$1ui_message_11 = _jspx_th_liferay$1ui_message_11.doStartTag();
    if (_jspx_th_liferay$1ui_message_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_11);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_11);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_12(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_11, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_12 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_12.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_11);
    _jspx_th_liferay$1ui_message_12.setKey("description");
    int _jspx_eval_liferay$1ui_message_12 = _jspx_th_liferay$1ui_message_12.doStartTag();
    if (_jspx_th_liferay$1ui_message_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_12);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_12);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_13(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1asset_asset$1categories$1available_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_13 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_13.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_13.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1asset_asset$1categories$1available_0);
    _jspx_th_liferay$1ui_message_13.setKey("categories");
    int _jspx_eval_liferay$1ui_message_13 = _jspx_th_liferay$1ui_message_13.doStartTag();
    if (_jspx_th_liferay$1ui_message_13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_13);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_13);
    return false;
  }

  private boolean _jspx_meth_liferay$1frontend_component_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-frontend:component
    com.liferay.frontend.taglib.servlet.taglib.ComponentTag _jspx_th_liferay$1frontend_component_0 = (com.liferay.frontend.taglib.servlet.taglib.ComponentTag) _jspx_tagPool_liferay$1frontend_component_module_nobody.get(com.liferay.frontend.taglib.servlet.taglib.ComponentTag.class);
    _jspx_th_liferay$1frontend_component_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1frontend_component_0.setParent(null);
    _jspx_th_liferay$1frontend_component_0.setModule("document_library/js/InfoPanel.es");
    int _jspx_eval_liferay$1frontend_component_0 = _jspx_th_liferay$1frontend_component_0.doStartTag();
    if (_jspx_th_liferay$1frontend_component_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1frontend_component_module_nobody.reuse(_jspx_th_liferay$1frontend_component_0);
      return true;
    }
    _jspx_tagPool_liferay$1frontend_component_module_nobody.reuse(_jspx_th_liferay$1frontend_component_0);
    return false;
  }
}
