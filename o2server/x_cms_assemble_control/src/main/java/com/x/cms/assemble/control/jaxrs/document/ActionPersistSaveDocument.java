package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.element.Form;

public class ActionPersistSaveDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistSaveDocument.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<FileInfo> cloudPictures = null;
		String identity = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Form form = null;
		Boolean check = true;
		Wi wi = null;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			identity = wi.getIdentity();
			if( StringUtils.isEmpty( identity ) ) {
				identity = wi.getCreatorIdentity();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。");
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if (check) {
			try {
				identity = userManagerService.getPersonIdentity( effectivePerson.getDistinguishedName(), identity );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在查询用户身份信息时发生异常。Name:" + identity);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if ( StringUtils.isEmpty(wi.getTitle()) ) {
				check = false;
				Exception exception = new ExceptionDocumentTitleEmpty();
				result.error(exception);
			}
		}

		if (check) {
			if ( StringUtils.isEmpty( wi.getCategoryId() ) ) {
				check = false;
				Exception exception = new ExceptionDocumentCategoryIdEmpty();
				result.error(exception);
			}
		}

		if (check) {
			try {
				appInfo = appInfoServiceAdv.get( wi.getAppId() );
				if (appInfo == null) {
					check = false;
					Exception exception = new ExceptionAppInfoNotExists(wi.getAppId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据ID查询应用栏目信息时发生异常！ID：" + wi.getAppId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				categoryInfo = categoryInfoServiceAdv.get( wi.getCategoryId() );
				if (categoryInfo == null) {
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists(wi.getCategoryId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"系统在根据ID查询分类信息时发生异常！ID：" + wi.getCategoryId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 查询分类设置的编辑表单
		if (check) {
			if ( StringUtils.isEmpty(categoryInfo.getFormId() )) {
				check = false;
				Exception exception = new ExceptionCategoryFormIdEmpty();
				result.error(exception);
			}
		}

		if (check) {
			try {
				form = formServiceAdv.get(categoryInfo.getFormId());
				if (form == null) {
					check = false;
					Exception exception = new ExceptionFormForEditNotExists(categoryInfo.getFormId());
					result.error(exception);
				} else {
					wi.setForm(form.getId());
					wi.setFormName(form.getName());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"系统在根据ID查询编辑表单时发生异常！ID：" + categoryInfo.getFormId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (categoryInfo.getReadFormId() != null && !categoryInfo.getReadFormId().isEmpty()) {
				try {
					form = formServiceAdv.get(categoryInfo.getReadFormId());
					if (form == null) {
						check = false;
						Exception exception = new ExceptionFormForReadNotExists(categoryInfo.getReadFormId());
						result.error(exception);
					} else {
						wi.setReadFormId(form.getId());
						wi.setReadFormName(form.getName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e,
							"系统在根据ID查询阅读表单时发生异常！ID：" + categoryInfo.getReadFormId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			wi.setDocumentType( categoryInfo.getDocumentType() );
			wi.setAppId(categoryInfo.getAppId());
			wi.setAppAlias( appInfo.getAppAlias());
			wi.setAppName(appInfo.getAppName());
			wi.setCategoryName(categoryInfo.getCategoryName());
			wi.setCategoryId(categoryInfo.getId());
			wi.setCategoryAlias(categoryInfo.getCategoryAlias());
			if( StringUtils.isEmpty( wi.getDocumentType() ) ) {
				wi.setDocumentType( categoryInfo.getDocumentType() );
			}
			if( !"信息".equals(wi.getDocumentType()) && !"数据".equals( wi.getDocumentType() )) {
				wi.setDocumentType( "信息" );
			}
			if (wi.getPictureList() != null && !wi.getPictureList().isEmpty()) {
				wi.setHasIndexPic(true);
			}
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				if ( identity != null) {
					wi.setCreatorIdentity( identity );
					wi.setCreatorPerson( userManagerService.getPersonNameWithIdentity( identity ) );
					wi.setCreatorUnitName( userManagerService.getUnitNameByIdentity( identity ) );
					wi.setCreatorTopUnitName( userManagerService.getTopUnitNameByIdentity( identity ) );
				} else {
					if ("xadmin".equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
						wi.setCreatorIdentity("xadmin");
						wi.setCreatorPerson("xadmin");
						wi.setCreatorUnitName("xadmin");
						wi.setCreatorTopUnitName("xadmin");
					} else {
						//取第一个身份
						identity = userManagerService.getIdentityWithPerson(effectivePerson.getDistinguishedName());
						if(StringUtils.isNotEmpty(identity)) {
							wi.setCreatorIdentity( identity );
							wi.setCreatorPerson( effectivePerson.getDistinguishedName() );
							wi.setCreatorUnitName( userManagerService.getUnitNameByIdentity( identity ) );
							wi.setCreatorTopUnitName( userManagerService.getTopUnitNameByIdentity( identity ) );
						}else {
							Exception exception = new ExceptionPersonHasNoIdentity(effectivePerson.getDistinguishedName());
							result.error(exception);
						}						
					}
				}
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		if (check) {
			try {
				JsonElement dataJson = null;
				if( wi.getDocData() != null ) {
					dataJson = XGsonBuilder.instance().toJsonTree( wi.getDocData() );
				}
				document = documentPersistService.save( wi, dataJson );
				ApplicationCache.notify(Document.class);

				Wo wo = new Wo();
				wo.setId( document.getId() );
				result.setData( wo );

				//检查是否需要删除热点图片
				try {
					ThisApplication.queueDocumentUpdate.send( document );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在创建文档信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		// 处理文档的云文档图片信息
		if (check) {
			try {
				cloudPictures = fileInfoServiceAdv.getCloudPictureList(document.getId());
				if (cloudPictures == null) {
					cloudPictures = new ArrayList<>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在查询文档云图片信息时发生异常！ID:" + document.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			// 检查是否有需要删除的图片
			if (cloudPictures != null && !cloudPictures.isEmpty()) {
				boolean isExists = false;
				for (FileInfo picture : cloudPictures) {
					isExists = false;
					if (wi.getCloudPictures() != null && !wi.getCloudPictures().isEmpty()) {
						for (String cloudPictureId : wi.getCloudPictures()) {
							if (picture.getCloudId() != null && picture.getCloudId().equalsIgnoreCase(cloudPictureId)) {
								isExists = true;
							}
						}
					}
					if (!isExists) {
						try {
							fileInfoServiceAdv.deleteFileInfo(picture.getId());
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentInfoProcess(e, "系统在删除文档云图片信息时发生异常！ID:" + picture.getId());
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				}
			}
		}

		if (check) {
			// 检查是否有需要新添加的云图片信息
			if (wi.getCloudPictures() != null && !wi.getCloudPictures().isEmpty()) {
				boolean isExists = false;
				int index = 0;
				for (String cloudPictureId : wi.getCloudPictures()) {
					index++;
					isExists = false;
					for (FileInfo picture : cloudPictures) {
						if (picture.getCloudId() != null && picture.getCloudId().equalsIgnoreCase(cloudPictureId)) {
							isExists = true;
							fileInfoServiceAdv.updatePictureIndex(picture.getId(), index);
						}
					}
					if (!isExists) {
						try {
							// 说明原来的文件中不存在，需要添加一个新的云图片
							fileInfoServiceAdv.saveCloudPicture(cloudPictureId, document, index);
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentInfoProcess(e,
									"系统在新增文档云图片信息时发生异常！CLOUD_ID:" + cloudPictureId);
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				}
			}
		}
		
		if (check) {
			try {//将读者以及作者信息持久化到数据库中
				if( "published".equals( document.getDocStatus() )) {
					documentPersistService.refreshDocumentPermission( document.getId(), wi.getReaderList(), wi.getAuthorList() );
				}else {
					documentPersistService.refreshDocumentPermission( document.getId(), null, null );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在核对文档访问管理权限信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends Document {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, Document> copier = WrapCopierFactory.wi( Wi.class, Document.class, null, JpaObject.FieldsUnmodify);
		
		@FieldDescribe( "文档操作者身份." )
		private String identity = null;
		
		@FieldDescribe( "数据的路径列表." )
		private String[] dataPaths = null;
		
		@FieldDescribe( "启动流程的JobId." )
		private String wf_jobId = null;
		
		@FieldDescribe( "启动流程的WorkId." )
		private String wf_workId = null;
		
		@FieldDescribe( "启动流程的附件列表." )
		private String[] wf_attachmentIds = null;	
		
		@FieldDescribe( "文档数据." )
		private Map<?, ?> docData = null;
		
		@FieldDescribe( "文档读者." )
		private List<PermissionInfo> readerList = null;
		
		@FieldDescribe( "文档编辑者." )
		private List<PermissionInfo> authorList = null;
		
		@FieldDescribe( "图片列表." )
		private List<String> cloudPictures = null;
		
		@FieldDescribe( "不修改权限（跳过权限设置，保留原来的设置）." )
		private Boolean skipPermission  = false;
		
		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
		
		public List<PermissionInfo> getReaderList() {
			return readerList;
		}

		public List<PermissionInfo> getAuthorList() {
			return authorList;
		}

		public void setReaderList(List<PermissionInfo> readerList) {
			this.readerList = readerList;
		}

		public void setAuthorList(List<PermissionInfo> authorList) {
			this.authorList = authorList;
		}

		public String[] getDataPaths() {
			if( dataPaths != null && dataPaths.length == 1 && dataPaths[0].equals("null")){
				return null;
			}
			return dataPaths;
		}

		public void setDataPaths(String[] dataPaths) {
			this.dataPaths = dataPaths;
		}

		public Map<?, ?> getDocData() {
			return docData;
		}

		public void setDocData(Map<?, ?> docData) {
			this.docData = docData;
		}

		public String getWf_jobId() {
			return wf_jobId;
		}

		public String getWf_workId() {
			return wf_workId;
		}

		public String[] getWf_attachmentIds() {
			return wf_attachmentIds;
		}

		public void setWf_jobId(String wf_jobId) {
			this.wf_jobId = wf_jobId;
		}

		public void setWf_workId(String wf_workId) {
			this.wf_workId = wf_workId;
		}

		public void setWf_attachmentIds(String[] wf_attachmentIds) {
			this.wf_attachmentIds = wf_attachmentIds;
		}

		public List<String> getCloudPictures() {
			return cloudPictures;
		}

		public void setCloudPictures(List<String> cloudPictures) {
			this.cloudPictures = cloudPictures;
		}

		public Boolean getSkipPermission() {
			return skipPermission;
		}

		public void setSkipPermission(Boolean skipPermission) {
			this.skipPermission = skipPermission;
		}
	}

	public static class Wo extends WoId {

	}
}