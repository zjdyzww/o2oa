package com.x.processplatform.assemble.surface.jaxrs.worklog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.jaxrs.worklog.ActionListWithWorkOrWorkCompleted.Wo;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;

class ActionListRollbackWithWorkOrWorkCompleted extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListRollbackWithWorkOrWorkCompleted.class);

	private final static String taskCompletedList_FIELDNAME = "taskCompletedList";

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);

			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
					new ExceptionEntityNotExist(workOrWorkCompleted))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			final String job = business.job().findWithWorkOrWorkCompleted(workOrWorkCompleted);

			CompletableFuture<List<WoTaskCompleted>> future_taskCompleteds = CompletableFuture.supplyAsync(() -> {
				return this.taskCompleteds(business, job);
			});

			CompletableFuture<List<Wo>> future_workLogs = CompletableFuture.supplyAsync(() -> {
				return this.workLogs(business, job);
			});
			List<WoTaskCompleted> taskCompleteds = future_taskCompleteds.get();
			List<Wo> wos = future_workLogs.get();
			ListTools.groupStick(wos, taskCompleteds, WorkLog.fromActivityToken_FIELDNAME,
					TaskCompleted.activityToken_FIELDNAME, taskCompletedList_FIELDNAME);
			result.setData(wos);
			return result;
		}
	}

	private List<WoTaskCompleted> taskCompleteds(Business business, String job) {
		List<WoTaskCompleted> os = new ArrayList<>();
		try {
			os = business.entityManagerContainer()
					.fetchEqual(TaskCompleted.class, WoTaskCompleted.copier, TaskCompleted.job_FIELDNAME, job).stream()
					.sorted(Comparator.comparing(TaskCompleted::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e);
		}
		return os;
	}

	private List<Wo> workLogs(Business business, String job) {
		List<Wo> os = new ArrayList<>();
		try {
			os = business.entityManagerContainer().fetchEqual(WorkLog.class, Wo.copier, WorkLog.job_FIELDNAME, job)
					.stream()
					.filter(o -> (!BooleanUtils.isTrue(o.getSplitting()))
							&& (Objects.equals(o.getArrivedActivityType(), ActivityType.manual)))
					.sorted(Comparator.comparing(WorkLog::getCreateTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e);
		}
		return os;
	}

	public static class Wo extends WorkLog {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<WorkLog, Wo> copier = WrapCopierFactory.wo(WorkLog.class, Wo.class,
				ListTools.toList(WorkLog.id_FIELDNAME, WorkLog.fromActivity_FIELDNAME,
						WorkLog.fromActivityType_FIELDNAME, WorkLog.fromActivityName_FIELDNAME,
						WorkLog.fromActivityAlias_FIELDNAME, WorkLog.fromActivityToken_FIELDNAME,
						WorkLog.fromTime_FIELDNAME, WorkLog.arrivedActivity_FIELDNAME,
						WorkLog.arrivedActivityType_FIELDNAME, WorkLog.arrivedActivityName_FIELDNAME,
						WorkLog.arrivedActivityAlias_FIELDNAME, WorkLog.arrivedActivityToken_FIELDNAME,
						WorkLog.arrivedTime_FIELDNAME, WorkLog.routeName_FIELDNAME, WorkLog.connected_FIELDNAME,
						WorkLog.splitting_FIELDNAME, WorkLog.fromGroup_FIELDNAME, WorkLog.arrivedGroup_FIELDNAME,
						WorkLog.fromOpinionGroup_FIELDNAME, WorkLog.arrivedOpinionGroup_FIELDNAME),
				JpaObject.FieldsInvisible);

		private List<WoTask> taskList = new ArrayList<>();

		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		private List<WoRead> readList = new ArrayList<>();

		private List<WoReadCompleted> readCompletedList = new ArrayList<>();

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

		public List<WoReadCompleted> getReadCompletedList() {
			return readCompletedList;
		}

		public void setReadCompletedList(List<WoReadCompleted> readCompletedList) {
			this.readCompletedList = readCompletedList;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -4432508672641778924L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class,
				ListTools.toList(TaskCompleted.id_FIELDNAME, TaskCompleted.person_FIELDNAME,
						TaskCompleted.unit_FIELDNAME, TaskCompleted.routeName_FIELDNAME,
						TaskCompleted.opinion_FIELDNAME, TaskCompleted.startTime_FIELDNAME,
						TaskCompleted.activityName_FIELDNAME, TaskCompleted.completedTime_FIELDNAME,
						Task.activityToken_FIELDNAME),
				null);
	}

}