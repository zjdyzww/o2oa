package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "流程", packageName = "com.x.processplatform.assemble.surface", containerEntities = {
		"com.x.processplatform.core.entity.content.Attachment", "com.x.processplatform.core.entity.content.Read",
		"com.x.processplatform.core.entity.content.ReadCompleted", "com.x.processplatform.core.entity.content.Review",
		"com.x.processplatform.core.entity.content.Hint", "com.x.processplatform.core.entity.content.SerialNumber",
		"com.x.processplatform.core.entity.content.Task", "com.x.processplatform.core.entity.content.TaskCompleted",
		"com.x.processplatform.core.entity.content.Work", "com.x.processplatform.core.entity.content.WorkCompleted",
		"com.x.processplatform.core.entity.content.WorkLog", "com.x.processplatform.core.entity.element.Agent",
		"com.x.processplatform.core.entity.element.Application",
		"com.x.processplatform.core.entity.element.ApplicationDict",
		"com.x.processplatform.core.entity.element.ApplicationDictItem",
		"com.x.processplatform.core.entity.element.Begin", "com.x.processplatform.core.entity.element.Cancel",
		"com.x.processplatform.core.entity.element.Choice", "com.x.processplatform.core.entity.element.Delay",
		"com.x.processplatform.core.entity.element.Embed", "com.x.processplatform.core.entity.element.End",
		"com.x.processplatform.core.entity.element.File", "com.x.processplatform.core.entity.element.Form",
		"com.x.processplatform.core.entity.element.FormField", "com.x.processplatform.core.entity.element.Invoke",
		"com.x.processplatform.core.entity.element.Manual", "com.x.processplatform.core.entity.element.Merge",
		"com.x.processplatform.core.entity.element.Message", "com.x.processplatform.core.entity.element.Parallel",
		"com.x.processplatform.core.entity.element.Process", "com.x.processplatform.core.entity.element.Route",
		"com.x.processplatform.core.entity.element.Script", "com.x.processplatform.core.entity.element.Service",
		"com.x.processplatform.core.entity.element.Split", "com.x.processplatform.core.entity.element.QueryView",
		"com.x.processplatform.core.entity.element.QueryStat", "com.x.processplatform.core.entity.element.Projection",
		"com.x.query.core.entity.Item" }, storageTypes = { StorageType.processPlatform }, storeJars = {
				"x_organization_core_entity", "x_organization_core_express", "x_processplatform_core_entity",
				"x_query_core_entity" })
public class x_processplatform_assemble_surface extends Deployable {

//	public x_processplatform_assemble_surface() {
//		super();
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Read");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.ReadCompleted");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Review");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Hint");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.SerialNumber");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Task");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.TaskCompleted");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Work");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.WorkLog");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Agent");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Application");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDict");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDictItem");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Begin");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Cancel");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Choice");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Delay");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Embed");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.End");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.File");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Form");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.FormField");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Invoke");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Manual");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Merge");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Message");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Parallel");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Process");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Route");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Script");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Service");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Split");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.QueryView");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.element.QueryStat");
//		dependency.containerEntities.add("com.x.query.core.entity.Item");
//		dependency.storageTypes.add(StorageType.processPlatform.toString());
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
//	}
}
