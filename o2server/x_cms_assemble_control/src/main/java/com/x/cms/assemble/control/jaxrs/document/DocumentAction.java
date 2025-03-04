package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.queue.DataImportStatus;

@Path("document")
@JaxrsDescribe("信息发布信息文档管理")
public class DocumentAction extends StandardJaxrsAction{
	
	private static  Logger logger = LoggerFactory.getLogger( DocumentAction.class );

	@JaxrsMethodDescribe(value = "变更指定文档的分类信息.", action = ActionPersistChangeCategory.class)
	@PUT
	@Path("category/change")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_changeCategory( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistChangeCategory.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			try {
				result = new ActionPersistChangeCategory().execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "指修改指定文档的数据.", action = ActionPersistBatchModifyData.class)
	@PUT
	@Path("batch/data/modify")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_batchDataModify( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistBatchModifyData.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionPersistBatchModifyData().execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据导入批次号查询导入状态信息.", action = ActionQueryImportStatusWithName.class)
	@GET
	@Path("batch/{batchName}/status")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_checkImportStatus(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("导入批次号") @PathParam("batchName") String batchName) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<DataImportStatus> result = new ActionResult<>();
		try {
			result = new ActionQueryImportStatusWithName().execute( request, effectivePerson, batchName );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "查询所有的导入状态信息.", action = ActionQueryAllImportStatus.class)
	@GET
	@Path("batch/status")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_checkAllImportStatus(@Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<DataImportStatus>> result = new ActionResult<>();
		try {
			result = new ActionQueryAllImportStatus().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID获取信息发布文档信息对象详细信息，包括附件列表，数据信息.", action = ActionQueryGetDocument.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_get(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryGetDocument.Wo> result = new ActionResult<>();
		try {
			result = new ActionQueryGetDocument().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "列示文档对象可供排序和展示使用的列名.", action = ActionQueryListDocumentFields.class)
	@GET
	@Path("document/fields")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_listDocumentFields(@Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryListDocumentFields.Wo> result = new ActionResult<>();
		try {
			result = new ActionQueryListDocumentFields().execute( request );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	
	@JaxrsMethodDescribe(value = "根据ID访问信息发布文档信息对象详细信息，包括附件列表，数据信息.", action = ActionQueryViewDocument.class)
	@GET
	@Path("{id}/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_view(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryViewDocument.Wo> result = new ActionResult<>();
		try {
			result = new ActionQueryViewDocument().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID获取信息发布文档信息被访问次数.", action = ActionQueryCountViewTimes.class)
	@GET
	@Path("{id}/view/count")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_getViewCount(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryCountViewTimes.Wo> result = new ActionResult<>();
		try {
			result = new ActionQueryCountViewTimes().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "查询符合过滤条件的已发布的信息数量.", action = ActionQueryCountWithFilter.class)
	@PUT
	@Path("filter/count")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_countDocumentWithFilter( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryCountWithFilter.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryCountWithFilter().execute( request,  jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID删除信息发布文档信息.", action = ActionPersistDeleteDocument.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_delete(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistDeleteDocument.Wo> result = new ActionResult<>();
		try {
			result = new ActionPersistDeleteDocument().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据批次号删除信息发布文档信息.", action = ActionPersistDeleteWithBatch.class)
	@DELETE
	@Path("batch/{batchId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_deleteWithBatchName(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("batchId") String batchId) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistDeleteWithBatch.Wo> result = new ActionResult<>();
		try {
			result = new ActionPersistDeleteWithBatch().execute( request, batchId, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID归档信息发布文档信息.", action = ActionPersistArchive.class)
	@GET
	@Path("achive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_achive(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistArchive.Wo> result = new ActionResult<>();
		try {
			result = new ActionPersistArchive().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID修改信息发布文档状态为已发布.", action = ActionPersistPublishDocument.class)
	@PUT
	@Path("publish/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_publish(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id, JsonElement jsonElement ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistPublishDocument.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionPersistPublishDocument().execute( request, id, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "直接发布信息内容，创建新的信息发布文档并且直接发布.", action = ActionPersistPublishContent.class)
	@PUT
	@Path("publish/content")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_publishContent(@Context HttpServletRequest request, JsonElement jsonElement ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistPublishContent.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			System.out.println( "please wait, system try to publish content......" );
			try {
				result = new ActionPersistPublishContent().execute( request, jsonElement, effectivePerson );
				System.out.println( "system publish content successful!" );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID取消信息内容发布状态，修改为草稿.", action = ActionPersistPublishCancel.class)
	@PUT
	@Path("publish/{id}/cancel")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_publishCancel(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistPublishCancel.Wo> result = new ActionResult<>();
		try {
			result = new ActionPersistPublishCancel().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 下一页.", action = ActionQueryListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_listNextWithFilter( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryListNextWithFilter().execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "列示符合过滤条件的草稿信息内容, 下一页.", action = ActionQueryListDraftNextWithFilter.class)
	@PUT
	@Path("draft/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_listDraftNextWithFilter( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListDraftNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryListDraftNextWithFilter().execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "根据信息发布文档ID查询文档第一张图片信息列表.", action = ActionQueryGetFirstPicture.class)
	@GET
	@Path("pictures/{id}/first")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_listFirstPictures( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryGetFirstPicture.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionQueryGetFirstPicture().execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "根据信息发布文档ID查询文档所有的图片信息列表.", action = ActionQueryListAllPictures.class)
	@GET
	@Path("pictures/{id}/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query_listAllPictures( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListAllPictures.Wo>> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionQueryListAllPictures().execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "从Excel文件导入文档数据.", action = ActionPersistImportDataExcel.class)
	@POST
	@Path("import/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void persist_importDocumentFormExcel(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("分类ID") @PathParam("categoryId") String categoryId, 
			@JaxrsParameterDescribe("作为参数的JSON字符串") @FormDataParam("json_data") String json_data,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionPersistImportDataExcel.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPersistImportDataExcel().execute(request, effectivePerson, categoryId, bytes,  json_data, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "保存信息发布文档信息对象.", action = ActionPersistSaveDocument.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistSaveDocument.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			try {
				result = new ActionPersistSaveDocument().execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "文档点赞.", action = ActionPersistCommend.class)
	@GET
	@Path("{id}/commend")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_commend( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistCommend.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionPersistCommend().execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "取消文档点赞.", action = ActionPersistUnCommend.class)
	@GET
	@Path("{id}/uncommend")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_unCommend( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistUnCommend.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionPersistUnCommend().execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "文档置顶.", action = ActionPersistTopDocument.class)
	@GET
	@Path("{id}/top")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_top( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistTopDocument.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionPersistTopDocument().execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "取消文档点赞.", action = ActionPersistUnTopDocument.class)
	@GET
	@Path("{id}/unTop")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response persist_unTop( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistUnTopDocument.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionPersistUnTopDocument().execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}