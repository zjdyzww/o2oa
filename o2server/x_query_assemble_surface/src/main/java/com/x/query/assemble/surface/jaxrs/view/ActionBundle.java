package com.x.query.assemble.surface.jaxrs.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.list.TreeList;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.plan.Runtime;

class ActionBundle extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBundle.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		logger.debug("jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			View view = business.pick(id, View.class);
			if (null == view) {
				throw new ExceptionEntityNotExist(id, View.class);
			}
			Query query = business.pick(view.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(view.getQuery(), Query.class);
			}
			if (!business.readable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}
			if (!business.readable(effectivePerson, view)) {
				throw new ExceptionAccessDenied(effectivePerson, view);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (null == wi) {
				wi = new Wi();
			}
			Runtime runtime = this.runtime(effectivePerson, business, view, wi.getFilterList(), wi.getParameter(),
					wi.getCount());
			List<String> os = this.fetchBundle(business, view, runtime);
			Wo wo = new Wo();
			wo.setValueList(os);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("过滤")
		private List<FilterEntry> filterList = new TreeList<>();

		@FieldDescribe("参数")
		private Map<String, String> parameter = new HashMap<>();

		@FieldDescribe("数量")
		private Integer count = 0;

		public List<FilterEntry> getFilterList() {
			return filterList;
		}

		public void setFilterList(List<FilterEntry> filterList) {
			this.filterList = filterList;
		}

		public Map<String, String> getParameter() {
			return parameter;
		}

		public void setParameter(Map<String, String> parameter) {
			this.parameter = parameter;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

	}

	public static class Wo extends WrapStringList {

	}

}