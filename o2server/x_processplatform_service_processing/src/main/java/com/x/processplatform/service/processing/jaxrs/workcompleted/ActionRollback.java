package com.x.processplatform.service.processing.jaxrs.workcompleted;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ProcessingAttributes;

class ActionRollback extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<Wo> result = new ActionResult<>();

			Business business = new Business(emc);

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			WorkCompleted workCompleted = emc.flag(flag, WorkCompleted.class);

			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(flag, WorkCompleted.class);
			}

			Application application = business.element().get(workCompleted.getApplication(), Application.class);

			if (null == application) {
				throw new ExceptionEntityNotExist(workCompleted.getApplication(), Application.class);
			}

			Process process = business.element().get(workCompleted.getProcess(), Process.class);

			if (null == process) {
				throw new ExceptionEntityNotExist(workCompleted.getProcess(), Process.class);
			}

			WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);

			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}

			if (BooleanUtils.isTrue(workLog.getSplitting())) {
				throw new ExceptionSplittingNotRollback(workCompleted.getId(), workLog.getId());
			}

			List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, workLog.getJob());

			WorkLogTree workLogTree = new WorkLogTree(workLogs);

			Node node = workLogTree.find(workLog);

			Nodes nodes = workLogTree.rootTo(node);

			emc.beginTransaction(Work.class);
			emc.beginTransaction(WorkCompleted.class);
			emc.beginTransaction(WorkLog.class);
			emc.beginTransaction(Attachment.class);
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			emc.beginTransaction(Review.class);

			Work work = this.createWork(workCompleted, workLog);
			emc.persist(work, CheckPersistType.all);

			this.disconnectWorkLog(work, workLog);

			this.rollbackTaskCompleted(business, work, nodes, workLog,
					emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob()));

			this.rollbackRead(business, work, nodes, workLog,
					emc.listEqual(Read.class, Read.job_FIELDNAME, work.getJob()));

			this.rollbackReadCompleted(business, work, nodes, workLog,
					emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, work.getJob()));

			this.rollbackReview(business, work, nodes,
					emc.listEqual(Review.class, Review.job_FIELDNAME, work.getJob()));

			this.rollbackWorkLog(business, work, nodes, workLogs);

			this.rollbackAttachment(business, work,
					emc.listEqual(Attachment.class, Attachment.job_FIELDNAME, work.getJob()));

			emc.remove(workCompleted);

			emc.commit();

			Processing processing = new Processing(wi);
			processing.processing(work.getId());
			Wo wo = new Wo();
			wo.setId(work.getId());
			result.setData(wo);
			return result;
		}
	}

	private Work createWork(WorkCompleted workCompleted, WorkLog workLog) throws Exception {
		Work work = new Work(workCompleted);
		work.setSplitting(false);
		work.setActivityName(workLog.getFromActivityName());
		work.setActivity(workLog.getFromActivity());
		work.setActivityAlias(workLog.getFromActivityAlias());
		work.setActivityArrivedTime(workLog.getFromTime());
		work.setActivityDescription("");
		work.setActivityToken(workLog.getFromActivityToken());
		work.setActivityType(workLog.getFromActivityType());
		work.setErrorRetry(0);
		work.setWorkStatus(WorkStatus.processing);
		return work;
	}

	private void disconnectWorkLog(Work work, WorkLog workLog) {
		workLog.setConnected(false);
		workLog.setArrivedActivity("");
		workLog.setArrivedActivityAlias("");
		workLog.setArrivedActivityName("");
		workLog.setArrivedActivityToken("");
		workLog.setArrivedActivityType(null);
		workLog.setArrivedTime(null);
		workLog.setDuration(0L);
		workLog.setWorkCompleted("");
		workLog.setWork(work.getId());
	}

	private void rollbackTaskCompleted(Business business, Work work, Nodes nodes, WorkLog workLog,
			List<TaskCompleted> list) throws Exception {
		for (TaskCompleted o : list) {
			if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())
					|| StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
				business.entityManagerContainer().remove(o);
			} else {
				o.setCompleted(false);
				o.setWorkCompleted("");
				o.setWork(work.getId());
			}
		}
	}

	private void rollbackRead(Business business, Work work, Nodes nodes, WorkLog workLog, List<Read> list)
			throws Exception {
		for (Read o : list) {
			if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())
					|| StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
				business.entityManagerContainer().remove(o);
			} else {
				o.setCompleted(false);
				o.setWorkCompleted("");
				o.setWork(work.getId());
			}
		}
	}

	private void rollbackReadCompleted(Business business, Work work, Nodes nodes, WorkLog workLog,
			List<ReadCompleted> list) throws Exception {
		for (ReadCompleted o : list) {
			if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())
					|| StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
				business.entityManagerContainer().remove(o);
			} else {
				o.setCompleted(false);
				o.setWorkCompleted("");
				o.setWork(work.getId());
			}
		}
	}

	private void rollbackReview(Business business, Work work, Nodes nodes, List<Review> list) throws Exception {
		Date date = nodes.latestArrivedTime();
		if (null != date) {
			for (Review o : list) {
				if (null != o.getStartTime() && o.getStartTime().after(date)) {
					business.entityManagerContainer().remove(o);
				} else {
					o.setCompleted(false);
					o.setWorkCompleted("");
					o.setWork(work.getId());
				}
			}
		}
	}

	private void rollbackAttachment(Business business, Work work, List<Attachment> list) throws Exception {
		for (Attachment o : list) {
			o.setCompleted(false);
			o.setWork(work.getId());
			o.setWorkCompleted("");
		}
	}

	private void rollbackWorkLog(Business business, Work work, Nodes nodes, List<WorkLog> list) throws Exception {
		for (WorkLog o : list) {
			if (!nodes.containsWorkLog(o)) {
				business.entityManagerContainer().remove(o);
			} else {
				o.setCompleted(false);
				o.setWorkCompleted("");
				o.setWork(work.getId());
			}
		}
	}

	public static class Wi extends ProcessingAttributes {

		@FieldDescribe("工作日志标识")
		private String workLog;

		public String getWorkLog() {
			return workLog;
		}

		public void setWorkLog(String workLog) {
			this.workLog = workLog;
		}

	}

	public static class Wo extends WoId {
	}

}