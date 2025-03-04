package com.x.processplatform.assemble.designer.jaxrs.projection;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Projection;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			Business business = new Business(emc);

			Process process = emc.flag(wi.getProcess(), Process.class);

			if (null == process) {
				throw new ExceptionEntityNotExist(wi.getProcess(), Process.class);
			}

			Application application = emc.flag(process.getApplication(), Application.class);

			if (null == application) {
				throw new ExceptionEntityNotExist(process.getApplication(), Application.class);
			}

			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			Projection projection = emc.flag(flag, Projection.class);

			if (null == projection) {
				throw new ExceptionEntityNotExist(flag, Projection.class);
			}

			Wi.copier.copy(wi, projection);

			projection.setProcess(process.getId());
			projection.setApplication(application.getId());

			if (this.duplicateWorkCompleted(business, projection)) {
				throw new ExceptionDuplicateWorkCompleted();
			}

			if (this.duplicateTaskCompleted(business, projection)) {
				throw new ExceptionDuplicateTaskCompleted();
			}

			if (this.duplicateRead(business, projection)) {
				throw new ExceptionDuplicateRead();
			}

			if (this.duplicateReadCompleted(business, projection)) {
				throw new ExceptionDuplicateReadCompleted();
			}

			if (this.duplicateReview(business, projection)) {
				throw new ExceptionDuplicateReview();
			}

			if (this.duplicateTable(business, projection)) {
				throw new ExceptionDuplicateTable();
			}

			if (StringUtils.equals(Projection.TYPE_TABLE, projection.getType())
					&& StringUtils.isEmpty(projection.getDynamicClassName())) {
				throw new ExceptionEntityFieldEmpty(Projection.class, Projection.dynamicClassName_FIELDNAME);
			}

			try {
				Class.forName(projection.getDynamicClassName());
			} catch (Exception e) {
				throw new ExceptionDynamicClassNotExist(projection.getDynamicClassName());
			}

			try {
				gson.fromJson(projection.getData(), new TypeToken<List<Projection.Item>>() {
				}.getType());
			} catch (Exception e) {
				throw new ExceptionDataError();
			}

			emc.beginTransaction(Projection.class);
			emc.check(projection, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Projection.class);
			Wo wo = new Wo();
			wo.setId(projection.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Projection {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Projection> copier = WrapCopierFactory.wi(Wi.class, Projection.class, null,
				Arrays.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
						JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME,
						Projection.application_FIELDNAME));

	}

}