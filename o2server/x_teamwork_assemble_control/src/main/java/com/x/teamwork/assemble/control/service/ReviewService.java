package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;

public class ReviewService {

	private UserManagerService userManagerService = new UserManagerService();
	private static  Logger logger = LoggerFactory.getLogger( ReviewService.class );
		
	/**
	 * 根据指定文档的权限信息重置或者刷新所有的Review信息
	 * @param emc
	 * @param docId
	 * @throws Exception
	 */
	public void refreshTaskReview( EntityManagerContainer emc, String taskId ) throws Exception {
		Task task = emc.find( taskId, Task.class );
		Project project = null;
		if( task != null ) {
			project = emc.find( task.getProject(), Project.class );
		}
		 if( task != null ) {//正常发布后的工作需要计算工作的可见范围
				logger.info( "refreshTaskReview -> refresh review for [published] task: " + task.getName() );
				List<String> persons = addTaskAllPermission( emc, new ArrayList<>(), taskId );
				logger.info( "refreshTaskReview -> there are "+ persons.size() +" permission in this task: " + task.getName() );
				refreshTaskReview( emc, project, task, persons );
		}
	}
	
	/**
	 * 根据指定文档删除所有的Review信息
	 * @param emc
	 * @param docId
	 * @throws Exception
	 */
	public void deleteTaskReview( EntityManagerContainer emc, String taskId ) throws Exception {
		Business business = new Business(emc);
		Integer maxQueryCount = 1000;
		Long count = business.reviewFactory().countByTask(taskId);
		Long maxTimes = count/maxQueryCount + 1 + 1; //多补偿一次		
		List<Review> reviewList = null;
		List<String> ids = null;
		for( int i = 0 ; i <= maxTimes; i++ ) {
			ids = business.reviewFactory().listReviewByTask(taskId, maxQueryCount );
			if( ListTools.isNotEmpty( ids )) {
				reviewList = emc.list( Review.class, ids);
			}
			if( ListTools.isNotEmpty( reviewList )) {
				emc.beginTransaction( Review.class );
				for( Review review : reviewList ) {
					emc.remove( review );
				}				
				emc.commit();
			}
		}
	}

	/**
	 * 将指定工作任务的Review刷新为指定的人员可见
	 * @param emc
	 * @param project
	 * @param task
	 * @param permissionPersons
	 * @throws Exception
	 */
	private void refreshTaskReview( EntityManagerContainer emc, Project project, Task task, List<String> permissionPersons) throws Exception {
		Business business = new Business(emc);
		Review review = null;
		List<Review> reviews = null;
		List<Review> reviews_tmp = null;
		//先检查该文档是否存在Review信息
		List<String> oldReviewIds = business.reviewFactory().listReviewByTask( task.getId(), 10000 );
		
		List<String> oldPermissionPersons = new ArrayList<>();
		if( permissionPersons == null ) {
			permissionPersons = new ArrayList<>();
		}
		
		emc.beginTransaction( Review.class );
		if( ListTools.isNotEmpty( oldReviewIds )) {
			reviews = emc.list( Review.class, oldReviewIds ); //查询该文档所有的Review列表，收集原来的Review人员名称oldPermissionPersons
			if( ListTools.isNotEmpty( reviews )) {
				emc.beginTransaction( Review.class );
				for( Review review_tmp : reviews ) {
					oldPermissionPersons.add( review_tmp.getPermissionObj() );
					//对比是否有需要删除的Review数据
					if( ListTools.isNotEmpty( permissionPersons) && permissionPersons.contains( review_tmp.getPermissionObj() ) ) {
						//说明存在的，保留，不需要处理，检查一下，是否有重复的需要删除
						reviews_tmp = business.reviewFactory().listByTaskAndPerson( task.getId(), review_tmp.getPermissionObj() );
						if( ListTools.isNotEmpty( reviews_tmp) && reviews_tmp.size() > 1 ) {
							//纠正，把重复的数据删除掉
							for( int i=0; i<reviews_tmp.size(); i++  ) {
								if( i > 0 ) {
									//删除多余的Review
									emc.remove( reviews_tmp.get(i), CheckRemoveType.all );
								}
							}
						}
					}else {
						//不存在的Review需要删除
						emc.remove( review_tmp, CheckRemoveType.all );
					}
				}
				emc.commit();
			}
		}		
		
		//再判断是否需要添加新的Review信息
		Person personObj = null;
		String personName = null;
		permissionPersons = ListTools.trim( permissionPersons, true, true, new String[0] ); //去重复
		for( String person : permissionPersons ) {
			if( !person.equalsIgnoreCase( "*" )) {
				//检查一下个人是否存在，防止姓名或者唯一标识变更过了导致权限不正确
				personObj = userManagerService.getPerson( person );
				if( personObj != null ) {
					personName = personObj.getDistinguishedName();
				}
			}
			if( StringUtils.isNotEmpty( personName )) {
				//查询一下，数据库里， 是否有相同的数据，如果有，就不再添加了
				 oldReviewIds = business.reviewFactory().listIdsByTaskAndPerson( task.getId(), personName );
				 if( ListTools.isEmpty( oldReviewIds )) {
					 review = createReviewWithTask( project, task, personName );
					 emc.beginTransaction( Review.class );
					 emc.persist( review, CheckPersistType.all );
					 emc.commit();
				 }
			}
		}
	}
	
	/**
	 * 将一个任务涉及到的所有权限转换为人员
	 * @param emc
	 * @param permissionObjs
	 * @param taskId
	 * @return
	 * @throws Exception 
	 */
	private List<String> addTaskAllPermission( EntityManagerContainer emc, List<String> permissionObjs, String taskId ) throws Exception {
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		Task task = emc.find( taskId, Task.class );
		Project project = null;
		if( task != null ) {			
			if( StringUtils.isNotBlank( task.getCreatorPerson() ) && !permissionObjs.contains( task.getCreatorPerson() )) {
				permissionObjs.add( task.getCreatorPerson() );
			}
			if( StringUtils.isNotBlank( task.getExecutor() ) && !permissionObjs.contains( task.getExecutor() )) {
				permissionObjs.add( task.getExecutor() );
			}
			if( ListTools.isNotEmpty( task.getParticipantList() )) {
				permissionObjs = addPermissionObj( permissionObjs, task.getParticipantList() );
			}
			//工作所在项目的创建者和负责人也应该可以看见这个任务
			project = emc.find( task.getProject(), Project.class );
			if( StringUtils.isNotBlank( project.getCreatorPerson() ) && !permissionObjs.contains( project.getCreatorPerson() )) {
				permissionObjs.add( project.getCreatorPerson() );
			}
			if( StringUtils.isNotBlank( project.getExecutor() ) && !permissionObjs.contains( project.getExecutor() )) {
				permissionObjs.add( project.getExecutor() );
			}
			
			//查查该工作是否有上级工作，如果有上级工作，那上级工作的可见人员也应该加入到该工作的可见人员中
			if( StringUtils.isNotEmpty( task.getParent() )) {
				permissionObjs = addTaskAllPermission( emc, permissionObjs, task.getParent() );
			}
		}
		return permissionObjs;
	}

	/**
	 * 将指定的权限名称拆解人员，添加到permissionObjs，并且返回
	 * @param permissionObjs
	 * @param objNames
	 * @return
	 * @throws Exception 
	 */
	private List<String> addPermissionObj(List<String> permissionObjs, List<String> objNames ) throws Exception {
		String result = null;
		List<String> persons  = null;
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		for( String objName : objNames ) {
			if( objName.endsWith( "@P" ) ) {
				if( !permissionObjs.contains( objName )) {
					permissionObjs.add( objName );
				}
			}else if( objName.endsWith( "@I" ) ) {//将Identity转换为人员
				result = userManagerService.getPersonNameWithIdentity( objName );
				permissionObjs = addStringToList( permissionObjs, result );
			}else if( objName.endsWith( "@U" ) ) {//将组织拆解为人员
				//判断一下，如果不是顶层组织，就或者顶层组织不唯一，才将组织解析为人员
				if( !userManagerService.isTopUnit( objName ) || userManagerService.countTopUnit() > 1 ) {
					persons  = userManagerService.listPersonWithUnit( objName );
					permissionObjs = addListToList( permissionObjs, persons );
				}
			}else if( objName.endsWith( "@G" ) ) {//将群组拆解为人员
				persons  = userManagerService.listPersonWithGroup( objName );
				permissionObjs = addListToList( permissionObjs, persons );
			}else if( objName.endsWith( "@R" ) ) {
				persons  = userManagerService.listPersonWithRole( objName );
				permissionObjs = addListToList( permissionObjs, persons );
			}else if( "*".equals( objName ) ) {
				permissionObjs = addStringToList( permissionObjs, objName );
			}
		}
		return permissionObjs;
	}

	/**
	 * 根据工作任务信息以及可见权限来组织一个Review对象
	 * @param project
	 * @param task
	 * @param person
	 * @return
	 */
	private Review createReviewWithTask( Project project, Task task, String person ) {
		Review review = new Review();
		
		review.setId( Review.createId() );
		review.setTaskId( task.getId() );
		review.setParent( task.getParent() );
		review.setName( task.getName() );
		review.setProject(project.getId() );
		review.setProjectName( project.getTitle() );
		
		review.setCreatorPerson( task.getCreatorPerson() );
		review.setExecutor( task.getExecutor() );
		review.setExecutorIdentity( task.getExecutorIdentity() );
		review.setExecutorUnit( task.getExecutorUnit() );
		review.setPermissionObj( person );
		review.setPermissionObjType( "PERSON" );
		
		review.setStartTime( task.getStartTime() );
		review.setEndTime( task.getEndTime() );
		review.setCreateTime( task.getCreateTime() );		
		review.setUpdateTime( task.getUpdateTime() );
		
		review.setArchive( task.getArchive() );
		review.setCompleted( task.getCompleted() );
		review.setOvertime( task.getOvertime() );
		review.setDeleted( task.getDeleted() );
		review.setWorkStatus( task.getWorkStatus() );
		
		review.setPriority( task.getPriority() );
		review.setProgress( task.getProgress() );
		review.setOrder( task.getOrder() );
		
		review.setTaskSequence( task.getSequence() );
		
		review.setMemoDouble1( task.getMemoDouble1() );
		review.setMemoDouble2( task.getMemoDouble2() );
		review.setMemoInteger1( task.getMemoInteger1() );
		review.setMemoInteger2( task.getMemoInteger2() );
		review.setMemoInteger3( task.getMemoInteger3() );
		review.setMemoString255_1( task.getMemoString255_1() );
		review.setMemoString255_2( task.getMemoString255_2() );
		review.setMemoString64_1( task.getMemoString64_1() );
		review.setMemoString64_2( task.getMemoString64_2() );
		review.setMemoString64_3( task.getMemoString64_3() );
		
		review.setClaimed( task.getClaimed() );
		review.setTagContent( task.getTagContent() );
		review.setRemindRelevance( task.getRemindRelevance());
		
		review.setPermissionObj( person );
		if( "*".equals( person ) ) {
			review.setPermissionObjType( "*" );
		}else {
			review.setPermissionObjType( "PERSON" );
		}
		return review;
	}
	
	/**
	 * 将字符串添加到集合里，去重
	 * @param list
	 * @param string
	 * @return list
	 */
	private List<String> addStringToList( List<String> list, String string ) {
		if( list == null ) {
			list = new ArrayList<>();
		}
		if( !list.contains( string )) {
			list.add( string);
		}
		return list;
	}
	
	/**
	 * 将字符串列表添加到集合里，去重
	 * @param list
	 * @param list2
	 * @return list
	 */
	private List<String> addListToList( List<String> list, List<String> list2 ) {
		if( list == null ) {
			list = new ArrayList<>();
		}
		if( ListTools.isEmpty( list2 ) ) {
			return list;
		}else {
			for( String string : list2 ) {
				if( !list.contains( string )) {
					list.add( string);
				}
			}
		}
		return list;
	}

	/**
	 * 检查传入的taskIds中有多少是指定用户可见的ID
	 * @param taskIds
	 * @param personName
	 * @return
	 * @throws Exception 
	 */
	public List<String> checkTaskIdsWithPermission(EntityManagerContainer emc, List<String> taskIds, String personName) throws Exception {
		if( ListTools.isEmpty( taskIds )) {
			return null;
		}
		if( StringUtils.isEmpty( personName )) {
			return null;
		}
		Business business = new Business(emc);
		return business.reviewFactory().checkTaskIdsWithPermission( taskIds, personName );
	}

	/**
	 * 查询用户在指定项目下所有可见的工作任务ID列表
	 * @param emc
	 * @param person
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<String> listTaskIdsWithPerson(EntityManagerContainer emc, String person, String project ) throws Exception {
		if( StringUtils.isEmpty( person )) {
			return null;
		}
		if( StringUtils.isEmpty( project )) {
			return null;
		}
		Business business = new Business(emc);
		return business.reviewFactory().listTaskIdsWithPersonAndProject( person, project );
	}

	public List<Review> listTaskWithPersonAndParentId(EntityManagerContainer emc, String person, String taskId) throws Exception {
		if( StringUtils.isEmpty( person )) {
			return null;
		}
		if( StringUtils.isEmpty( taskId )) {
			return null;
		}
		Business business = new Business(emc);
		return business.reviewFactory().listTaskWithPersonAndParentId( person, taskId );
	}
}
