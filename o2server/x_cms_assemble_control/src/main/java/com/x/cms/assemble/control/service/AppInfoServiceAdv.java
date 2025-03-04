package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;

/**
 * 对栏目信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class AppInfoServiceAdv {

	private AppInfoService appInfoService = new AppInfoService();

	public AppInfo get( String id ) throws Exception {
		if ( StringUtils.isEmpty(id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public AppInfo getWithFlag( String flag ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.getWithFlag( emc, flag );
		} catch (Exception e) {
			throw e;
		}
	}

	public Long countCategoryByAppId(String id, String documentType) throws Exception {
		if ( StringUtils.isEmpty(id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.countCategoryByAppId(emc, id, documentType);
		} catch (Exception e) {
			throw e;
		}
	}

	public void delete( String id, EffectivePerson currentPerson ) throws Exception {
		if ( StringUtils.isEmpty(id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoService.delete(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
//	public void delete(String id, EffectivePerson currentPerson, String documentType, Integer maxCount ) throws Exception {
//		if (id == null || id.isEmpty()) {
//			throw new Exception("id is null.");
//		}
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			appInfoService.delete(emc, id, documentType, maxCount);
//		} catch (Exception e) {
//			throw e;
//		}
//	}

	public List<AppInfo> listAll(String documentType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAll(emc, documentType);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listAllIds(String documentType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAllIds(emc, documentType);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<AppInfo> list(List<String> app_ids) throws Exception {
		if (ListTools.isEmpty( app_ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( AppInfo.class,  app_ids );
//			return appInfoService.list(emc, app_ids);
		} catch (Exception e) {
			throw e;
		}
	}

	public AppInfo save( AppInfo appInfo, EffectivePerson currentPerson) throws Exception {
		if ( appInfo == null) {
			throw new Exception("appInfo is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 检查一下该应用栏目是否存在管理者, 如果不存在，则将当前登录者作为应用栏目的管理者
			if( ListTools.isEmpty( appInfo.getManageablePersonList())  && ListTools.isEmpty( appInfo.getManageableUnitList())  &&ListTools.isEmpty( appInfo.getManageableGroupList())) {
				if( "xadmin".equalsIgnoreCase( currentPerson.getName() )) {
					appInfo.addManageablePerson( "xadmin" );
				}else {
					appInfo.addManageablePerson( currentPerson.getDistinguishedName() );
				}
			}
			appInfo = appInfoService.save( emc, appInfo );
		} catch (Exception e) {
			throw e;
		}
		return appInfo;
	}

	public List<String> listByAppName(String appName) throws Exception {
		if ( StringUtils.isEmpty(appName )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listByAppName(emc, appName);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<AppInfo> listAppInfoWithAliases( List<String> appAliases) throws Exception {
		if ( ListTools.isEmpty( appAliases )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAppInfoByAppAliases(emc, appAliases);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> getWithAlias(String appAlias) throws Exception {
		if ( StringUtils.isEmpty(appAlias )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listByAppAlias(emc, appAlias);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void saveAppInfoIcon( String appId, String base64, String iconMainColor ) throws Exception {
		if ( StringUtils.isEmpty(appId )) {
			throw new Exception("appId is null");
		}
		if ( StringUtils.isEmpty(base64 )) {
			throw new Exception("base64 is null");
		}
		if ( StringUtils.isEmpty(iconMainColor)) {
			throw new Exception("iconMainColor is null");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo app = emc.find( appId, AppInfo.class );
			if( app == null ) {
				throw new Exception("appInfo not exists.id:" + appId);
			}else {
				emc.beginTransaction( AppInfo.class );
				app.setAppIcon(base64);
				app.setIconColor(iconMainColor);
				emc.check( app, CheckPersistType.all );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新栏目管理员权限信息
	 * @param appId
	 * @param personList
	 * @param unitList
	 * @param groupList
	 * @throws Exception 
	 */
	public void updateManagerPermission(String appId, List<String> personList, List<String> unitList, List<String> groupList) throws Exception {
		if ( StringUtils.isEmpty( appId )) {
			throw new Exception("appId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			emc.beginTransaction( AppInfo.class );
			appInfo.setManageablePersonList(personList);
			appInfo.setManageableUnitList(unitList);
			appInfo.setManageableGroupList(groupList);
			emc.check(appInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 更新栏目发布权限信息
	 * @param appId
	 * @param personList
	 * @param unitList
	 * @param groupList
	 * @throws Exception 
	 */
	public void updatePublisherPermission(String appId, List<String> personList, List<String> unitList, List<String> groupList) throws Exception {
		if ( StringUtils.isEmpty( appId )) {
			throw new Exception("appId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			emc.beginTransaction( AppInfo.class );
			appInfo.setPublishablePersonList(personList);
			appInfo.setPublishableUnitList(unitList);
			appInfo.setPublishableGroupList(groupList);
			emc.check(appInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 更新栏目可见权限信息
	 * @param appId
	 * @param personList
	 * @param unitList
	 * @param groupList
	 * @throws Exception 
	 */
	public void updateViewerPermission(String appId, List<String> personList, List<String> unitList, List<String> groupList) throws Exception {
		if ( StringUtils.isEmpty( appId )) {
			throw new Exception("appId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			emc.beginTransaction( AppInfo.class );
			appInfo.setViewablePersonList(personList);
			appInfo.setViewableUnitList(unitList);
			appInfo.setViewableGroupList(groupList);
			emc.check(appInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateAllPermission(AppInfo appInfo) throws Exception {
		if ( appInfo == null ) {
			throw new Exception("appInfo is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo_entity = emc.find( appInfo.getId(), AppInfo.class );
			if( appInfo_entity != null ) {
				emc.beginTransaction( AppInfo.class );
				appInfo_entity.setManageablePersonList(appInfo.getManageablePersonList() );
				appInfo_entity.setPublishablePersonList( appInfo.getPublishablePersonList() );
				appInfo_entity.setPublishableUnitList( appInfo.getPublishableUnitList() );
				appInfo_entity.setPublishableGroupList( appInfo.getPublishableGroupList() );
				appInfo_entity.setViewablePersonList( appInfo.getViewablePersonList() );
				appInfo_entity.setViewableUnitList( appInfo.getViewableUnitList() );
				appInfo_entity.setViewableGroupList( appInfo.getViewableGroupList() );
				if( StringUtils.isEmpty( appInfo_entity.getDocumentType() )) {
					appInfo_entity.setDocumentType( "信息" );
				}
				emc.check(appInfo_entity , CheckPersistType.all );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 判断用户是否为指定栏目的管理员
	 * @param appId
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public Boolean isAppInfoManager(String appId, String distinguishedName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			if( appInfo != null && ListTools.isNotEmpty( appInfo.getManageablePersonList() )){
				if( appInfo.getManageablePersonList().contains( distinguishedName )) {
					return true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	/**
	 * 判断用户是否拥有指定栏目的发布者权限
	 * @param appId
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public Boolean isAppInfoPublisher(String appId, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			if( appInfo != null ) {
				if( ListTools.isNotEmpty( appInfo.getPublishablePersonList() )){
					if( appInfo.getManageablePersonList().contains( personName )) {
						return true;
					}				
					if( appInfo.getPublishablePersonList().contains( personName )) {
						return true;
					}
					appInfo.getPublishableUnitList().retainAll( unitNames );
					if( ListTools.isNotEmpty( appInfo.getPublishableUnitList() )) {
						return true;
					}
					appInfo.getPublishableGroupList().retainAll( groupNames );
					if( ListTools.isNotEmpty( appInfo.getPublishableGroupList() )) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
	
	/**
	 * 判断用户是否拥有指定栏目的访问权限
	 * @param appId
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public Boolean isAppInfoViewer(String appId, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			if( ListTools.isNotEmpty( appInfo.getPublishablePersonList() )){
				if( appInfo.getManageablePersonList().contains( personName )) {
					return true;
				}				
				if( appInfo.getPublishablePersonList().contains( personName )) {
					return true;
				}
				appInfo.getPublishableUnitList().retainAll( unitNames );
				if( ListTools.isNotEmpty( appInfo.getPublishableUnitList() )) {
					return true;
				}
				appInfo.getPublishableGroupList().retainAll( groupNames );
				if( ListTools.isNotEmpty( appInfo.getPublishableGroupList() )) {
					return true;
				}
				if( appInfo.getViewablePersonList().contains( personName )) {
					return true;
				}
				appInfo.getViewableUnitList().retainAll( unitNames );
				if( ListTools.isNotEmpty( appInfo.getViewableUnitList() )) {
					return true;
				}
				appInfo.getViewableGroupList().retainAll( groupNames );
				if( ListTools.isNotEmpty( appInfo.getViewableGroupList() )) {
					return true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	public void reviewed(String appId) throws Exception {
		if ( StringUtils.isEmpty( appId )) {
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			emc.beginTransaction( AppInfo.class );
			emc.persist( appInfo, CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}	
}
