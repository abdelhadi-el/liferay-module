package org.apache.jsp.image_005fgallery_005fdisplay;

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

public final class view_005fimages_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList<String>(5);
    _jspx_dependants.add("/image_gallery_display/init.jsp");
    _jspx_dependants.add("/init.jsp");
    _jspx_dependants.add("/init-ext.jsp");
    _jspx_dependants.add("/image_gallery_display/init-ext.jsp");
    _jspx_dependants.add("/document_library/cast_result.jspf");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_namespace_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_param_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_resultRow_actionJspServletContext_actionJsp;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container_searchContainer_emptyResultsMessage;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_renderURL_var;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_otherwise;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_vertical$1card_title_resultRow_imageUrl_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_horizontal$1card$1col;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_choose;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1text;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_script_use;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_title_resultRow_icon_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_when_test;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_portlet_namespace_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_param_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_resultRow_actionJspServletContext_actionJsp = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container_searchContainer_emptyResultsMessage = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_renderURL_var = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_otherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_vertical$1card_title_resultRow_imageUrl_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_horizontal$1card$1col = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_choose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_script_use = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_title_resultRow_icon_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_when_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_portlet_namespace_nobody.release();
    _jspx_tagPool_portlet_param_value_name_nobody.release();
    _jspx_tagPool_liferay$1ui_message_key_nobody.release();
    _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_resultRow_actionJspServletContext_actionJsp.release();
    _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_liferay$1ui_search$1container_searchContainer_emptyResultsMessage.release();
    _jspx_tagPool_portlet_renderURL_var.release();
    _jspx_tagPool_c_otherwise.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1frontend_vertical$1card_title_resultRow_imageUrl_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className.release();
    _jspx_tagPool_liferay$1frontend_horizontal$1card$1col.release();
    _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody.release();
    _jspx_tagPool_c_choose.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text.release();
    _jspx_tagPool_aui_script_use.release();
    _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_title_resultRow_icon_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody.release();
    _jspx_tagPool_c_when_test.release();
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
      out.write('\n');
      out.write('\n');

if (layout.isTypeControlPanel()) {
	portletPreferences = PortletPreferencesLocalServiceUtil.getPreferences(themeDisplay.getCompanyId(), scopeGroupId, PortletKeys.PREFS_OWNER_TYPE_GROUP, 0, DLPortletKeys.DOCUMENT_LIBRARY, null);
}

IGRequestHelper igRequestHelper = new IGRequestHelper(request);

String portletResource = igRequestHelper.getPortletResource();

DLPortletInstanceSettings dlPortletInstanceSettings = igRequestHelper.getDLPortletInstanceSettings();

long rootFolderId = dlPortletInstanceSettings.getRootFolderId();
String rootFolderName = StringPool.BLANK;

if (rootFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
	try {
		Folder rootFolder = DLAppLocalServiceUtil.getFolder(rootFolderId);

		rootFolderName = rootFolder.getName();

		if (rootFolder.getGroupId() != scopeGroupId) {
			rootFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			rootFolderName = StringPool.BLANK;
		}
	}
	catch (NoSuchFolderException nsfe) {
		rootFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}
}

String displayStyle = portletPreferences.getValue("displayStyle", StringPool.BLANK);
long displayStyleGroupId = GetterUtil.getLong(portletPreferences.getValue("displayStyleGroupId", null), themeDisplay.getScopeGroupId());

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');

SearchContainer igSearchContainer = (SearchContainer)request.getAttribute("view.jsp-igSearchContainer");

DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(igRequestHelper);

      out.write('\n');
      out.write('\n');
      //  liferay-ui:search-container
      com.liferay.taglib.ui.SearchContainerTag _jspx_th_liferay$1ui_search$1container_0 = (com.liferay.taglib.ui.SearchContainerTag) _jspx_tagPool_liferay$1ui_search$1container_searchContainer_emptyResultsMessage.get(com.liferay.taglib.ui.SearchContainerTag.class);
      _jspx_th_liferay$1ui_search$1container_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1ui_search$1container_0.setParent(null);
      _jspx_th_liferay$1ui_search$1container_0.setEmptyResultsMessage("there-are-no-media-files-in-this-folder");
      _jspx_th_liferay$1ui_search$1container_0.setSearchContainer( igSearchContainer );
      int _jspx_eval_liferay$1ui_search$1container_0 = _jspx_th_liferay$1ui_search$1container_0.doStartTag();
      if (_jspx_eval_liferay$1ui_search$1container_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        java.lang.Integer total = null;
        com.liferay.portal.kernel.dao.search.SearchContainer searchContainer = null;
        total = (java.lang.Integer) _jspx_page_context.findAttribute("total");
        searchContainer = (com.liferay.portal.kernel.dao.search.SearchContainer) _jspx_page_context.findAttribute("searchContainer");
        out.write('\n');
        out.write('	');
        //  liferay-ui:search-container-row
        com.liferay.taglib.ui.SearchContainerRowTag _jspx_th_liferay$1ui_search$1container$1row_0 = (com.liferay.taglib.ui.SearchContainerRowTag) _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className.get(com.liferay.taglib.ui.SearchContainerRowTag.class);
        _jspx_th_liferay$1ui_search$1container$1row_0.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_search$1container$1row_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container_0);
        _jspx_th_liferay$1ui_search$1container$1row_0.setClassName("Object");
        _jspx_th_liferay$1ui_search$1container$1row_0.setModelVar("result");
        int _jspx_eval_liferay$1ui_search$1container$1row_0 = _jspx_th_liferay$1ui_search$1container$1row_0.doStartTag();
        if (_jspx_eval_liferay$1ui_search$1container$1row_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Integer index = null;
          Object result = null;
          com.liferay.portal.kernel.dao.search.ResultRow row = null;
          if (_jspx_eval_liferay$1ui_search$1container$1row_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_search$1container$1row_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_search$1container$1row_0.doInitBody();
          }
          index = (java.lang.Integer) _jspx_page_context.findAttribute("index");
          result = (Object) _jspx_page_context.findAttribute("result");
          row = (com.liferay.portal.kernel.dao.search.ResultRow) _jspx_page_context.findAttribute("row");
          do {
            out.write("\n\t\t");
            out.write('\n');
            out.write('\n');

Folder curFolder = null;
FileEntry fileEntry = null;
FileShortcut fileShortcut = null;

if (result instanceof AssetEntry) {
	AssetEntry assetEntry = (AssetEntry)result;

	if (Objects.equals(assetEntry.getClassName(), DLFileEntryConstants.getClassName())) {
		fileEntry = DLAppLocalServiceUtil.getFileEntry(assetEntry.getClassPK());

		fileEntry = fileEntry.toEscapedModel();
	}
	else {
		fileShortcut = DLAppLocalServiceUtil.getFileShortcut(assetEntry.getClassPK());

		fileShortcut = fileShortcut.toEscapedModel();

		fileEntry = DLAppLocalServiceUtil.getFileEntry(fileShortcut.getToFileEntryId());

		fileEntry = fileEntry.toEscapedModel();
	}
}
else if (result instanceof FileEntry) {
	fileEntry = (FileEntry)result;

	fileEntry = fileEntry.toEscapedModel();
}
else if (result instanceof FileShortcut) {
	fileShortcut = (FileShortcut)result;

	fileShortcut = fileShortcut.toEscapedModel();

	fileEntry = DLAppLocalServiceUtil.getFileEntry(fileShortcut.getToFileEntryId());

	fileEntry = fileEntry.toEscapedModel();
}
else if (result instanceof Folder) {
	curFolder = (Folder)result;
}

            out.write("\n\n\t\t");
            //  c:choose
            org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_0 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
            _jspx_th_c_choose_0.setPageContext(_jspx_page_context);
            _jspx_th_c_choose_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1row_0);
            int _jspx_eval_c_choose_0 = _jspx_th_c_choose_0.doStartTag();
            if (_jspx_eval_c_choose_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t");
                //  c:when
                org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_0 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                _jspx_th_c_when_0.setPageContext(_jspx_page_context);
                _jspx_th_c_when_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
                _jspx_th_c_when_0.setTest( fileEntry != null );
                int _jspx_eval_c_when_0 = _jspx_th_c_when_0.doStartTag();
                if (_jspx_eval_c_when_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\n\t\t\t\t");

				String dataOptions = StringPool.BLANK;

				FileVersion fileVersion = fileEntry.getFileVersion();

				boolean hasAudio = AudioProcessorUtil.hasAudio(fileVersion);
				boolean hasImages = ImageProcessorUtil.hasImages(fileVersion);
				boolean hasPDFImages = PDFProcessorUtil.hasImages(fileVersion);
				boolean hasVideo = VideoProcessorUtil.hasVideo(fileVersion);

				String imagePreviewURL = null;
				String imageURL = themeDisplay.getPathThemeImages() + "/file_system/large/" + DLUtil.getGenericName(fileEntry.getExtension()) + ".png";
				int playerHeight = 500;

				String thumbnailId = null;

				if (fileShortcut != null) {
					thumbnailId = "shortcut_" + fileShortcut.getFileShortcutId();
				}
				else {
					thumbnailId = "entry_" + fileEntry.getFileEntryId();
				}

				if (PropsValues.DL_FILE_ENTRY_PREVIEW_ENABLED) {
					if (hasAudio) {
						for (String audioContainer : PropsValues.DL_FILE_ENTRY_PREVIEW_AUDIO_CONTAINERS) {
							dataOptions += "&" + audioContainer + "PreviewURL=" + HtmlUtil.escapeURL(DLURLHelperUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, "&supportedAudio=1&audioPreview=1&type=" + audioContainer));
						}

						imagePreviewURL = DLURLHelperUtil.getImagePreviewURL(fileEntry, fileVersion, themeDisplay);
						imageURL = DLURLHelperUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, HtmlUtil.escapeURL("&audioPreview=1") + "&supportedAudio=1&mediaGallery=1");
						playerHeight = 43;
					}
					else if (hasImages) {
						imagePreviewURL = DLURLHelperUtil.getThumbnailSrc(fileEntry, fileVersion, themeDisplay);
						imageURL = DLURLHelperUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, "&imagePreview=1");
					}
					else if (hasPDFImages) {
						imagePreviewURL = DLURLHelperUtil.getImagePreviewURL(fileEntry, fileVersion, themeDisplay);
						imageURL = DLURLHelperUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, "&previewFileIndex=1");
					}
					else if (hasVideo) {
						for (String videoContainer : PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_CONTAINERS) {
							dataOptions += "&" + videoContainer + "PreviewURL=" + HtmlUtil.escapeURL(DLURLHelperUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, "&supportedVideo=1&videoPreview=1&type=" + videoContainer));
						}

						imagePreviewURL = DLURLHelperUtil.getImagePreviewURL(fileEntry, fileVersion, themeDisplay);
						imageURL = DLURLHelperUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, "&supportedVideo=1&mediaGallery=1");
						playerHeight = PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_HEIGHT;
					}
				}

				String title = fileEntry.getTitle();

				if (Validator.isNotNull(fileEntry.getDescription())) {
					title += " - " + fileEntry.getDescription();
				}

				row.setCssClass("lfr-asset-item");
				
                    out.write("\n\n\t\t\t\t");
                    //  liferay-ui:search-container-column-text
                    com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_0 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                    _jspx_th_liferay$1ui_search$1container$1column$1text_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_search$1container$1column$1text_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
                    int _jspx_eval_liferay$1ui_search$1container$1column$1text_0 = _jspx_th_liferay$1ui_search$1container$1column$1text_0.doStartTag();
                    if (_jspx_eval_liferay$1ui_search$1container$1column$1text_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      if (_jspx_eval_liferay$1ui_search$1container$1column$1text_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                        out = _jspx_page_context.pushBody();
                        _jspx_th_liferay$1ui_search$1container$1column$1text_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                        _jspx_th_liferay$1ui_search$1container$1column$1text_0.doInitBody();
                      }
                      do {
                        out.write("\n\t\t\t\t\t<div class=\"image-link preview\" ");
                        out.print( (hasAudio || hasVideo) ? "data-options=\"height=" + playerHeight + "&thumbnailURL=" + HtmlUtil.escapeURL(DLURLHelperUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, "&videoThumbnail=1")) + "&width=640" + dataOptions + "\"" : StringPool.BLANK );
                        out.write(" href=\"");
                        out.print( imageURL );
                        out.write("\" thumbnailId=\"");
                        out.print( thumbnailId );
                        out.write("\" title=\"");
                        out.print( HtmlUtil.escapeAttribute(title) );
                        out.write("\">\n\t\t\t\t\t\t");
                        //  c:choose
                        org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_1 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                        _jspx_th_c_choose_1.setPageContext(_jspx_page_context);
                        _jspx_th_c_choose_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_0);
                        int _jspx_eval_c_choose_1 = _jspx_th_c_choose_1.doStartTag();
                        if (_jspx_eval_c_choose_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\t\t\t\t\t\t\t");
                            //  c:when
                            org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_1 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                            _jspx_th_c_when_1.setPageContext(_jspx_page_context);
                            _jspx_th_c_when_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                            _jspx_th_c_when_1.setTest( Validator.isNull(imagePreviewURL) );
                            int _jspx_eval_c_when_1 = _jspx_th_c_when_1.doStartTag();
                            if (_jspx_eval_c_when_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t");
                              //  liferay-frontend:icon-vertical-card
                              com.liferay.frontend.taglib.servlet.taglib.IconVerticalCardTag _jspx_th_liferay$1frontend_icon$1vertical$1card_0 = (com.liferay.frontend.taglib.servlet.taglib.IconVerticalCardTag) _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_title_resultRow_icon_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody.get(com.liferay.frontend.taglib.servlet.taglib.IconVerticalCardTag.class);
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setActionJsp( dlPortletInstanceSettingsHelper.isShowActions() ? "/image_gallery_display/image_action.jsp" : StringPool.BLANK );
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setActionJspServletContext( application );
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setCardCssClass("card-interactive card-interactive-secondary");
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setCssClass("entry-display-style");
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setIcon("documents-and-media");
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setResultRow( row );
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setTitle( dlPortletInstanceSettingsHelper.isShowActions() ? fileEntry.getTitle() : StringPool.BLANK );
                              int _jspx_eval_liferay$1frontend_icon$1vertical$1card_0 = _jspx_th_liferay$1frontend_icon$1vertical$1card_0.doStartTag();
                              if (_jspx_th_liferay$1frontend_icon$1vertical$1card_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_title_resultRow_icon_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody.reuse(_jspx_th_liferay$1frontend_icon$1vertical$1card_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_title_resultRow_icon_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody.reuse(_jspx_th_liferay$1frontend_icon$1vertical$1card_0);
                              out.write("\n\t\t\t\t\t\t\t");
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
                            out.write("\n\t\t\t\t\t\t\t");
                            //  c:otherwise
                            org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_0 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                            _jspx_th_c_otherwise_0.setPageContext(_jspx_page_context);
                            _jspx_th_c_otherwise_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                            int _jspx_eval_c_otherwise_0 = _jspx_th_c_otherwise_0.doStartTag();
                            if (_jspx_eval_c_otherwise_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t");
                              //  liferay-frontend:vertical-card
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardTag _jspx_th_liferay$1frontend_vertical$1card_0 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardTag) _jspx_tagPool_liferay$1frontend_vertical$1card_title_resultRow_imageUrl_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
                              _jspx_th_liferay$1frontend_vertical$1card_0.setActionJsp( dlPortletInstanceSettingsHelper.isShowActions() ? "/image_gallery_display/image_action.jsp" : StringPool.BLANK );
                              _jspx_th_liferay$1frontend_vertical$1card_0.setActionJspServletContext( application );
                              _jspx_th_liferay$1frontend_vertical$1card_0.setCardCssClass("card-interactive card-interactive-secondary");
                              _jspx_th_liferay$1frontend_vertical$1card_0.setCssClass("entry-display-style");
                              _jspx_th_liferay$1frontend_vertical$1card_0.setImageUrl( imagePreviewURL );
                              _jspx_th_liferay$1frontend_vertical$1card_0.setResultRow( row );
                              _jspx_th_liferay$1frontend_vertical$1card_0.setTitle( dlPortletInstanceSettingsHelper.isShowActions() ? fileEntry.getTitle() : StringPool.BLANK );
                              int _jspx_eval_liferay$1frontend_vertical$1card_0 = _jspx_th_liferay$1frontend_vertical$1card_0.doStartTag();
                              if (_jspx_th_liferay$1frontend_vertical$1card_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card_title_resultRow_imageUrl_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody.reuse(_jspx_th_liferay$1frontend_vertical$1card_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card_title_resultRow_imageUrl_cssClass_cardCssClass_actionJspServletContext_actionJsp_nobody.reuse(_jspx_th_liferay$1frontend_vertical$1card_0);
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
                        out.write("\n\t\t\t\t\t</div>\n\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1column$1text_0.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                      if (_jspx_eval_liferay$1ui_search$1container$1column$1text_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                        out = _jspx_page_context.popBody();
                    }
                    if (_jspx_th_liferay$1ui_search$1container$1column$1text_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_0);
                    out.write("\n\t\t\t");
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
                    out.write("\n\t\t\t\t");
                    //  portlet:renderURL
                    com.liferay.taglib.portlet.RenderURLTag _jspx_th_portlet_renderURL_0 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_portlet_renderURL_var.get(com.liferay.taglib.portlet.RenderURLTag.class);
                    _jspx_th_portlet_renderURL_0.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_renderURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_1);
                    _jspx_th_portlet_renderURL_0.setVar("viewFolderURL");
                    int _jspx_eval_portlet_renderURL_0 = _jspx_th_portlet_renderURL_0.doStartTag();
                    if (_jspx_eval_portlet_renderURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      out.write("\n\t\t\t\t\t");
                      if (_jspx_meth_portlet_param_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_renderURL_0, _jspx_page_context))
                        return;
                      out.write("\n\t\t\t\t\t");
                      //  portlet:param
                      com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_1 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                      _jspx_th_portlet_param_1.setPageContext(_jspx_page_context);
                      _jspx_th_portlet_param_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
                      _jspx_th_portlet_param_1.setName("redirect");
                      _jspx_th_portlet_param_1.setValue( currentURL );
                      int _jspx_eval_portlet_param_1 = _jspx_th_portlet_param_1.doStartTag();
                      if (_jspx_th_portlet_param_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
                        return;
                      }
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
                      out.write("\n\t\t\t\t\t");
                      //  portlet:param
                      com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_2 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                      _jspx_th_portlet_param_2.setPageContext(_jspx_page_context);
                      _jspx_th_portlet_param_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
                      _jspx_th_portlet_param_2.setName("folderId");
                      _jspx_th_portlet_param_2.setValue( String.valueOf(curFolder.getFolderId()) );
                      int _jspx_eval_portlet_param_2 = _jspx_th_portlet_param_2.doStartTag();
                      if (_jspx_th_portlet_param_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_2);
                        return;
                      }
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_2);
                      out.write("\n\t\t\t\t");
                    }
                    if (_jspx_th_portlet_renderURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_renderURL_var.reuse(_jspx_th_portlet_renderURL_0);
                      return;
                    }
                    _jspx_tagPool_portlet_renderURL_var.reuse(_jspx_th_portlet_renderURL_0);
                    java.lang.String viewFolderURL = null;
                    viewFolderURL = (java.lang.String) _jspx_page_context.findAttribute("viewFolderURL");
                    out.write("\n\n\t\t\t\t");

				row.setCssClass("lfr-asset-folder");
				
                    out.write("\n\n\t\t\t\t");
                    //  liferay-ui:search-container-column-text
                    com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_1 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                    _jspx_th_liferay$1ui_search$1container$1column$1text_1.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_search$1container$1column$1text_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_1);
                    int _jspx_eval_liferay$1ui_search$1container$1column$1text_1 = _jspx_th_liferay$1ui_search$1container$1column$1text_1.doStartTag();
                    if (_jspx_eval_liferay$1ui_search$1container$1column$1text_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      if (_jspx_eval_liferay$1ui_search$1container$1column$1text_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                        out = _jspx_page_context.pushBody();
                        _jspx_th_liferay$1ui_search$1container$1column$1text_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                        _jspx_th_liferay$1ui_search$1container$1column$1text_1.doInitBody();
                      }
                      do {
                        out.write("\n\t\t\t\t\t");
                        //  liferay-frontend:horizontal-card
                        com.liferay.frontend.taglib.servlet.taglib.HorizontalCardTag _jspx_th_liferay$1frontend_horizontal$1card_0 = (com.liferay.frontend.taglib.servlet.taglib.HorizontalCardTag) _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_resultRow_actionJspServletContext_actionJsp.get(com.liferay.frontend.taglib.servlet.taglib.HorizontalCardTag.class);
                        _jspx_th_liferay$1frontend_horizontal$1card_0.setPageContext(_jspx_page_context);
                        _jspx_th_liferay$1frontend_horizontal$1card_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_1);
                        _jspx_th_liferay$1frontend_horizontal$1card_0.setActionJsp( dlPortletInstanceSettingsHelper.isShowActions() ? "/document_library/folder_action.jsp" : StringPool.BLANK );
                        _jspx_th_liferay$1frontend_horizontal$1card_0.setActionJspServletContext( application );
                        _jspx_th_liferay$1frontend_horizontal$1card_0.setResultRow( row );
                        _jspx_th_liferay$1frontend_horizontal$1card_0.setText( curFolder.getName() );
                        _jspx_th_liferay$1frontend_horizontal$1card_0.setUrl( viewFolderURL );
                        int _jspx_eval_liferay$1frontend_horizontal$1card_0 = _jspx_th_liferay$1frontend_horizontal$1card_0.doStartTag();
                        if (_jspx_eval_liferay$1frontend_horizontal$1card_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          out.write("\n\t\t\t\t\t\t");
                          //  liferay-frontend:horizontal-card-col
                          com.liferay.frontend.taglib.servlet.taglib.HorizontalCardColTag _jspx_th_liferay$1frontend_horizontal$1card$1col_0 = (com.liferay.frontend.taglib.servlet.taglib.HorizontalCardColTag) _jspx_tagPool_liferay$1frontend_horizontal$1card$1col.get(com.liferay.frontend.taglib.servlet.taglib.HorizontalCardColTag.class);
                          _jspx_th_liferay$1frontend_horizontal$1card$1col_0.setPageContext(_jspx_page_context);
                          _jspx_th_liferay$1frontend_horizontal$1card$1col_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_horizontal$1card_0);
                          int _jspx_eval_liferay$1frontend_horizontal$1card$1col_0 = _jspx_th_liferay$1frontend_horizontal$1card$1col_0.doStartTag();
                          if (_jspx_eval_liferay$1frontend_horizontal$1card$1col_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                            if (_jspx_eval_liferay$1frontend_horizontal$1card$1col_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_horizontal$1card$1col_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_horizontal$1card$1col_0.doInitBody();
                            }
                            do {
                              out.write("\n\t\t\t\t\t\t\t");
                              //  liferay-frontend:horizontal-card-icon
                              com.liferay.frontend.taglib.servlet.taglib.HorizontalCardIconTag _jspx_th_liferay$1frontend_horizontal$1card$1icon_0 = (com.liferay.frontend.taglib.servlet.taglib.HorizontalCardIconTag) _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody.get(com.liferay.frontend.taglib.servlet.taglib.HorizontalCardIconTag.class);
                              _jspx_th_liferay$1frontend_horizontal$1card$1icon_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_horizontal$1card$1icon_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_horizontal$1card$1col_0);
                              _jspx_th_liferay$1frontend_horizontal$1card$1icon_0.setIcon( curFolder.isMountPoint() ? "repository" : "folder" );
                              int _jspx_eval_liferay$1frontend_horizontal$1card$1icon_0 = _jspx_th_liferay$1frontend_horizontal$1card$1icon_0.doStartTag();
                              if (_jspx_th_liferay$1frontend_horizontal$1card$1icon_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody.reuse(_jspx_th_liferay$1frontend_horizontal$1card$1icon_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody.reuse(_jspx_th_liferay$1frontend_horizontal$1card$1icon_0);
                              out.write("\n\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_horizontal$1card$1col_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                            } while (true);
                            if (_jspx_eval_liferay$1frontend_horizontal$1card$1col_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                          }
                          if (_jspx_th_liferay$1frontend_horizontal$1card$1col_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                            _jspx_tagPool_liferay$1frontend_horizontal$1card$1col.reuse(_jspx_th_liferay$1frontend_horizontal$1card$1col_0);
                            return;
                          }
                          _jspx_tagPool_liferay$1frontend_horizontal$1card$1col.reuse(_jspx_th_liferay$1frontend_horizontal$1card$1col_0);
                          out.write("\n\t\t\t\t\t");
                        }
                        if (_jspx_th_liferay$1frontend_horizontal$1card_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_resultRow_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_horizontal$1card_0);
                          return;
                        }
                        _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_resultRow_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_horizontal$1card_0);
                        out.write("\n\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1column$1text_1.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                      if (_jspx_eval_liferay$1ui_search$1container$1column$1text_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                        out = _jspx_page_context.popBody();
                    }
                    if (_jspx_th_liferay$1ui_search$1container$1column$1text_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_1);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_1);
                    out.write("\n\t\t\t");
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
            int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1row_0.doAfterBody();
            index = (java.lang.Integer) _jspx_page_context.findAttribute("index");
            result = (Object) _jspx_page_context.findAttribute("result");
            row = (com.liferay.portal.kernel.dao.search.ResultRow) _jspx_page_context.findAttribute("row");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_search$1container$1row_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_search$1container$1row_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className.reuse(_jspx_th_liferay$1ui_search$1container$1row_0);
          return;
        }
        _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className.reuse(_jspx_th_liferay$1ui_search$1container$1row_0);
        out.write("\n\n\t");
        //  liferay-ui:search-iterator
        com.liferay.taglib.ui.SearchIteratorTag _jspx_th_liferay$1ui_search$1iterator_0 = (com.liferay.taglib.ui.SearchIteratorTag) _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody.get(com.liferay.taglib.ui.SearchIteratorTag.class);
        _jspx_th_liferay$1ui_search$1iterator_0.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_search$1iterator_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container_0);
        _jspx_th_liferay$1ui_search$1iterator_0.setDisplayStyle("icon");
        _jspx_th_liferay$1ui_search$1iterator_0.setMarkupView("lexicon");
        _jspx_th_liferay$1ui_search$1iterator_0.setResultRowSplitter( new IGResultRowSplitter() );
        _jspx_th_liferay$1ui_search$1iterator_0.setSearchContainer( igSearchContainer );
        int _jspx_eval_liferay$1ui_search$1iterator_0 = _jspx_th_liferay$1ui_search$1iterator_0.doStartTag();
        if (_jspx_th_liferay$1ui_search$1iterator_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody.reuse(_jspx_th_liferay$1ui_search$1iterator_0);
          return;
        }
        _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody.reuse(_jspx_th_liferay$1ui_search$1iterator_0);
        out.write('\n');
      }
      if (_jspx_th_liferay$1ui_search$1container_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1ui_search$1container_searchContainer_emptyResultsMessage.reuse(_jspx_th_liferay$1ui_search$1container_0);
        return;
      }
      _jspx_tagPool_liferay$1ui_search$1container_searchContainer_emptyResultsMessage.reuse(_jspx_th_liferay$1ui_search$1container_0);
      out.write('\n');
      out.write('\n');

PortletURL embeddedPlayerURL = renderResponse.createRenderURL();

embeddedPlayerURL.setParameter("mvcPath", "/image_gallery_display/embedded_player.jsp");
embeddedPlayerURL.setWindowState(LiferayWindowState.POP_UP);

      out.write('\n');
      out.write('\n');
      //  aui:script
      com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_0 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script_use.get(com.liferay.taglib.aui.ScriptTag.class);
      _jspx_th_aui_script_0.setPageContext(_jspx_page_context);
      _jspx_th_aui_script_0.setParent(null);
      _jspx_th_aui_script_0.setUse("aui-image-viewer,aui-image-viewer-media");
      int _jspx_eval_aui_script_0 = _jspx_th_aui_script_0.doStartTag();
      if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_aui_script_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_aui_script_0.doInitBody();
        }
        do {
          out.write("\n\tvar viewportRegion = A.getDoc().get('viewportRegion');\n\n\tvar maxHeight = viewportRegion.height;\n\tvar maxWidth = viewportRegion.width;\n\n\tvar playingMediaIndex = -1;\n\n\tvar imageViewer = new A.ImageViewer({\n\t\tafter: {\n\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_0.setPageContext(_jspx_page_context);
          _jspx_th_c_if_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
          _jspx_th_c_if_0.setTest( dlPortletInstanceSettingsHelper.isShowActions() );
          int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
          if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\tload: function(event) {\n\t\t\t\t\tvar instance = this;\n\n\t\t\t\t\tvar currentLink = instance.getCurrentLink();\n\n\t\t\t\t\tvar thumbnailId = currentLink.attr('thumbnailId');\n\n\t\t\t\t\tvar actions = instance._actions;\n\n\t\t\t\t\tif (actions) {\n\t\t\t\t\t\tvar defaultAction = A.one(\n\t\t\t\t\t\t\t'#");
              if (_jspx_meth_portlet_namespace_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_0, _jspx_page_context))
                return;
              out.write("buttonsContainer_' + thumbnailId\n\t\t\t\t\t\t);\n\n\t\t\t\t\t\tactions.empty();\n\n\t\t\t\t\t\tvar action = defaultAction.clone().show();\n\n\t\t\t\t\t\tactions.append(action);\n\t\t\t\t\t}\n\t\t\t\t}\n\t\t\t");
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
          out.write("\n\t\t},\n\t\tdelay: 5000,\n\t\tinfoTemplate:\n\t\t\t'");
          out.print( LanguageUtil.format(request, "image-x-of-x", new String[] {"{current}", "{total}"}, false) );
          out.write("',\n\t\tlinks: '#");
          if (_jspx_meth_portlet_namespace_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("imageGalleryAssetInfo .image-link.preview',\n\t\tmaxHeight: maxHeight,\n\t\tmaxWidth: maxWidth,\n\t\ton: {\n\t\t\tcurrentIndexChange: function() {\n\t\t\t\tif (playingMediaIndex != -1) {\n\t\t\t\t\tLiferay.fire(\n\t\t\t\t\t\t'");
          if (_jspx_meth_portlet_namespace_2((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("ImageViewer:currentIndexChange'\n\t\t\t\t\t);\n\n\t\t\t\t\tplayingMediaIndex = -1;\n\t\t\t\t}\n\t\t\t},\n\t\t\tvisibleChange: function(event) {\n\t\t\t\tif (!event.newVal && playingMediaIndex != -1) {\n\t\t\t\t\tLiferay.fire('");
          if (_jspx_meth_portlet_namespace_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("ImageViewer:close');\n\n\t\t\t\t\tplayingMediaIndex = -1;\n\t\t\t\t}\n\t\t\t}\n\t\t},\n\t\tplayingLabel: '(");
          if (_jspx_meth_liferay$1ui_message_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write(")',\n\t\tplugins: [\n\t\t\t{\n\t\t\t\tcfg: {\n\t\t\t\t\t'providers.liferay': {\n\t\t\t\t\t\tcontainer:\n\t\t\t\t\t\t\t'<iframe frameborder=\"0\" height=\"{height}\" scrolling=\"no\" src=\"");
          out.print( embeddedPlayerURL.toString() );
          out.write('&');
          if (_jspx_meth_portlet_namespace_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("thumbnailURL={thumbnailURL}&");
          if (_jspx_meth_portlet_namespace_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("mp3PreviewURL={mp3PreviewURL}&");
          if (_jspx_meth_portlet_namespace_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("mp4PreviewURL={mp4PreviewURL}&");
          if (_jspx_meth_portlet_namespace_7((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("oggPreviewURL={oggPreviewURL}&");
          if (_jspx_meth_portlet_namespace_8((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("ogvPreviewURL={ogvPreviewURL}\" width=\"{width}\"></iframe>',\n\t\t\t\t\t\tmatcher: /(.+)&mediaGallery=1/,\n\t\t\t\t\t\tmediaRegex: /(.+)&mediaGallery=1/,\n\t\t\t\t\t\toptions: A.merge(A.MediaViewerPlugin.DEFAULT_OPTIONS, {\n\t\t\t\t\t\t\tmp3PreviewURL: '',\n\t\t\t\t\t\t\tmp4PreviewURL: '',\n\t\t\t\t\t\t\toggPreviewURL: '',\n\t\t\t\t\t\t\togvPreviewURL: '',\n\t\t\t\t\t\t\tthumbnailURL: ''\n\t\t\t\t\t\t})\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\tfn: A.MediaViewerPlugin\n\t\t\t}\n\t\t],\n\t\tzIndex: ++Liferay.zIndex.WINDOW\n\t});\n\n\timageViewer.TPL_CLOSE = imageViewer.TPL_CLOSE.replace(\n\t\t/<\\s*span[^>]*>(.*?)<\\s*\\/\\s*span>/,\n\t\tLiferay.Util.getLexiconIconTpl('times', 'icon-monospaced')\n\t);\n\n\tvar TPL_PLAYER_PAUSE =\n\t\t'<span>' + Liferay.Util.getLexiconIconTpl('pause', 'glyphicon') + '</span>';\n\tvar TPL_PLAYER_PLAY =\n\t\t'<span>' + Liferay.Util.getLexiconIconTpl('play', 'glyphicon') + '</span>';\n\n\timageViewer.TPL_PLAYER = TPL_PLAYER_PLAY;\n\n\timageViewer._syncPlaying = function() {\n\t\tif (this.get('playing')) {\n\t\t\tthis._player.setHTML(TPL_PLAYER_PAUSE);\n\t\t}\n\t\telse {\n\t\t\tthis._player.setHTML(TPL_PLAYER_PLAY);\n\t\t}\n\t};\n\n\timageViewer.render();\n");
          out.write("\n\tLiferay.on('");
          if (_jspx_meth_portlet_namespace_9((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("Video:play', function() {\n\t\timageViewer.pause();\n\n\t\tplayingMediaIndex = this.get('currentIndex');\n\t});\n\n\tLiferay.on('");
          if (_jspx_meth_portlet_namespace_10((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("Audio:play', function() {\n\t\timageViewer.pause();\n\n\t\tplayingMediaIndex = this.get('currentIndex');\n\t});\n\n\tvar onClickLinksDefaultFn = imageViewer._onClickLinks;\n\n\timageViewer._onClickLinks = function(event) {\n\t\tif (!event.target.ancestor('.dropdown')) {\n\t\t\tonClickLinksDefaultFn.call(this, event);\n\t\t}\n\t};\n\n\timageViewer.set(\n\t\t'links',\n\t\t'#");
          if (_jspx_meth_portlet_namespace_11((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("imageGalleryAssetInfo .image-link.preview'\n\t);\n");
          int evalDoAfterBody = _jspx_th_aui_script_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_aui_script_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_aui_script_use.reuse(_jspx_th_aui_script_0);
        return;
      }
      _jspx_tagPool_aui_script_use.reuse(_jspx_th_aui_script_0);
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

  private boolean _jspx_meth_portlet_param_0(javax.servlet.jsp.tagext.JspTag _jspx_th_portlet_renderURL_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_0 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
    _jspx_th_portlet_param_0.setName("mvcRenderCommandName");
    _jspx_th_portlet_param_0.setValue("/image_gallery_display/view");
    int _jspx_eval_portlet_param_0 = _jspx_th_portlet_param_0.doStartTag();
    if (_jspx_th_portlet_param_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_0 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
    int _jspx_eval_portlet_namespace_0 = _jspx_th_portlet_namespace_0.doStartTag();
    if (_jspx_th_portlet_namespace_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_1(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_1 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_1.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
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

  private boolean _jspx_meth_liferay$1ui_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_0 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    _jspx_th_liferay$1ui_message_0.setKey("playing");
    int _jspx_eval_liferay$1ui_message_0 = _jspx_th_liferay$1ui_message_0.doStartTag();
    if (_jspx_th_liferay$1ui_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
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
}
