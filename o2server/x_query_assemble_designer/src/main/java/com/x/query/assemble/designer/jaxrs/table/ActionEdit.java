package com.x.query.assemble.designer.jaxrs.table;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionDuplicateFlag;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionEdit extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			this.check(effectivePerson, business, table);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wi.copier.copy(wi, table);
			if (StringUtils.isEmpty(table.getName())) {
				throw new ExceptionEntityFieldEmpty(Table.class, Table.name_FIELDNAME);
			}
			if (StringUtils.isNotEmpty(emc.conflict(Table.class, table))) {
				throw new ExceptionDuplicateFlag(Table.class, emc.conflict(Table.class, table));
			}
			emc.beginTransaction(Table.class);
			XGsonBuilder.instance().fromJson(table.getData(), DynamicEntity.class);
			table.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			table.setLastUpdateTime(new Date());
			table.setStatus(Table.STATUS_draft);
			emc.check(table, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Table.class);
			ApplicationCache.notify(Statement.class);
			Wo wo = new Wo();
			wo.setId(table.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Table {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Table> copier = WrapCopierFactory.wi(Wi.class, Table.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Table.creatorPerson_FIELDNAME,
						Table.lastUpdatePerson_FIELDNAME, Table.lastUpdateTime_FIELDNAME, Table.data_FIELDNAME,
						Table.status_FIELDNAME));
	}
}