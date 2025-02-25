package com.x.organization.assemble.personal.jaxrs.trust;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.accredit.Trust;

import net.sf.ehcache.Element;

class ActionListTo extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					effectivePerson.getDistinguishedName());
			Element element = business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business, effectivePerson);
				business.cache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			return result;
		}
	}

	private List<Wo> list(Business business, EffectivePerson effectivePerson) throws Exception {
		List<Trust> os = business.entityManagerContainer().listEqual(Trust.class, Trust.toPerson_FIELDNAME,
				effectivePerson.getDistinguishedName());
		return Wo.copier.copy(os);
	}

	public static class Wo extends Trust {

		private static final long serialVersionUID = 4279205128463146835L;

		static WrapCopier<Trust, Wo> copier = WrapCopierFactory.wi(Trust.class, Wo.class, null,
				JpaObject.FieldsUnmodify);

	}

}
