package com.x.processplatform.assemble.designer.jaxrs.projection;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Projection;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();

			Business business = new Business(emc);

			Projection projection = emc.flag(flag, Projection.class);

			if (null == projection) {
				throw new ExceptionEntityNotExist(flag, Projection.class);
			}

			Process process = emc.flag(projection.getProcess(), Process.class);

			if (null == process) {
				throw new ExceptionEntityNotExist(projection.getProcess(), Process.class);
			}

			Application application = emc.flag(process.getApplication(), Application.class);

			if (null == application) {
				throw new ExceptionEntityNotExist(process.getApplication(), Application.class);
			}

			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			emc.beginTransaction(Projection.class);
			emc.remove(projection);
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

}