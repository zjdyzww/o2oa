package com.x.cms.assemble.control.jaxrs.appinfo;

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

@Path("appinfo")
@JaxrsDescribe("信息发布内容栏目管理")
public class AppInfoAction extends StandardJaxrsAction {

	private static  Logger logger = LoggerFactory.getLogger(AppInfoAction.class);

	@JaxrsMethodDescribe(value = "创建或者更新信息栏目信息对象.", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if (check) {
			try {
				result = new ActionSave().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppInfoProcess(e, "应用栏目信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "根据ID删除信息栏目信息对象.", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "根据ID删除CMS应用信息对象发生未知异常，ID:" + id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据栏目ID删除所有的信息文档.", action = ActionEraseDocumentWithAppInfo.class)
	@DELETE
	@Path("erase/app/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response eraseWithAppId(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionEraseDocumentWithAppInfo.Wo> result = new ActionResult<>();
		try {
			result = new ActionEraseDocumentWithAppInfo().execute(request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "根据栏目ID删除所有的信息文档发生未知异常，ID:" + id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "根据标识获取信息栏目信息对象.", action = ActionGet.class)
	@GET
	@Path("{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @JaxrsParameterDescribe("栏目ID") @PathParam("flag") String flag) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<BaseAction.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute( request, effectivePerson, flag );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "根据指定ID查询应用栏目信息对象时发生异常。flag:" + flag );
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "根据别名获取信息栏目信息对象.", action = ActionGetByAlias.class)
	@GET
	@Path("alias/{alias}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getByAlias(@Context HttpServletRequest request, @JaxrsParameterDescribe("栏目别名") @PathParam("alias") String alias) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<BaseAction.Wo> result = new ActionResult<>();
		try {
			result = new ActionGetByAlias().execute(request, effectivePerson, alias);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "根据指定应用唯一标识查询应用栏目信息对象时发生异常。ALIAS:" + alias);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "获取用户有权限查看的所有信息栏目信息列表.", action = ActionListWhatICanView_Article.class)
	@GET
	@Path("list/user/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWhatICanView_Article(@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListWhatICanView_Article.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanView_Article().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"根据权限查询用户可以访问的信息栏目列表，person:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "获取用户有权限查看的所有信息栏目信息列表.", action = ActionListWhatICanView_Data.class)
	@GET
	@Path("list/user/view/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWhatICanView_Data(@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListWhatICanView_Data.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanView_Data().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"根据权限查询用户可以访问的数据栏目列表，person:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "获取用户有权限查看的所有栏目信息列表.", action = ActionListWhatICanView_AllType.class)
	@GET
	@Path("list/user/view/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWhatICanView_AllType(@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListWhatICanView_AllType.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanView_AllType().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"根据权限查询用户可以访问的数据栏目列表，person:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "获取用户有权限发布的所有信息栏目信息列表.", action = ActionListWhatICanPublish.class)
	@GET
	@Path("list/user/publish")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWhatICanPublish(@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanPublish().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"系统在根据用户权限查询所有可见的栏目信息时发生异常。Name:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "获取用户有权限发布的所有信息栏目信息列表.", action = ActionGetPublishableAppInfo.class)
	@GET
	@Path("get/user/publish/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPublishableAppInfo(@Context HttpServletRequest request,
			@JaxrsParameterDescribe("栏目ID") @PathParam("appId") String appId ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<BaseAction.Wo> result = new ActionResult<>();
		try {
			result = new ActionGetPublishableAppInfo().execute(request, effectivePerson, appId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"系统在根据用户权限查询可见的栏目信息时发生异常。Name:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "获取用户有权限管理的所有信息栏目信息列表.", action = ActionListWhatICanManage.class)
	@GET
	@Path("list/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWhatICanManage(@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanManage().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"系统在根据用户权限查询所有管理的栏目信息时发生异常。Name:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "获取用户有权限访问到的所有信息栏目信息列表.", action = ActionListAll.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAppInfo(@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListAll().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "查询所有应用栏目信息对象时发生异常");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件的信息栏目信息,下一页.", action = ActionListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListNextWithFilter().execute(request, effectivePerson, id, count, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "查询栏目信息对象时发生异常");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件的信息栏目信息,上一页.", action = ActionListPrevWithFilter.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListPrevWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListPrevWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "查询栏目信息对象时发生异常");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "上传或者替换栏目的图标内容，可以指定压缩大小	.", action = ActionAppIconUpload.class)
	@POST
	@Path("{appId}/icon/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void changeIcon(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("栏目ID") @PathParam("appId") String appId,
			@JaxrsParameterDescribe("最大宽度") @PathParam("size") Integer size,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionAppIconUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAppIconUpload().execute(request, effectivePerson, appId, size, bytes, disposition );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}