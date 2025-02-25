package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.ProjectExtFieldRele;


/**
 * 对项目扩展属性信息查询的服务
 */
public class ProjectExtFieldReleQueryService {

	private ProjectExtFieldReleService projectExtFieldReleService = new ProjectExtFieldReleService();
	

	public List<ProjectExtFieldRele> list(List<String> ids ) throws Exception {
		if ( ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectExtFieldReleService.list( emc, ids );
		} catch (Exception e) {
			throw e;
		}
	}	
	
	/**
	 * 根据项目扩展属性关联信息的标识查询项目扩展属性关联信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProjectExtFieldRele get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectExtFieldReleService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据项目ID查询项目关联的所有扩展属性关联信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<ProjectExtFieldRele> listReleWithProject( String projectId ) throws Exception {
		if (StringUtils.isEmpty(projectId)) {
			return new ArrayList<>();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectExtFieldReleService.listReleWithProject(emc, projectId);
		} catch (Exception e) {
			throw e;
		}
	}
}
