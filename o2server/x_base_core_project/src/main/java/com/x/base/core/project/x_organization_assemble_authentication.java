package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "组织管理认证", packageName = "com.x.organization.assemble.authentication", containerEntities = {
		"com.x.organization.core.entity.Person", "com.x.organization.core.entity.Identity",
		"com.x.organization.core.entity.Role", "com.x.organization.core.entity.Bind",
		"com.x.organization.core.entity.OauthCode" }, storeJars = { "x_organization_core_entity",
				"x_organization_core_express" })
public class x_organization_assemble_authentication extends Deployable {

//	public x_organization_assemble_authentication() {
//		super();
//		dependency.containerEntities.add("com.x.organization.core.entity.Person");
//		dependency.containerEntities.add("com.x.organization.core.entity.Identity");
//		dependency.containerEntities.add("com.x.organization.core.entity.Role");
//		dependency.containerEntities.add("com.x.organization.core.entity.Bind");
//		dependency.containerEntities.add("com.x.organization.core.entity.OauthCode");
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//	}
}
