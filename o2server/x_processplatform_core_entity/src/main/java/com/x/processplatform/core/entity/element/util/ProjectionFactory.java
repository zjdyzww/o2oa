package com.x.processplatform.core.entity.element.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Projection;

public class ProjectionFactory {

//	public static final String TYPE_STRING = "string";
//	public static final String TYPE_INTEGER = "integer";
//	public static final String TYPE_LONG = "long";
//	public static final String TYPE_DOUBLE = "double";
//	public static final String TYPE_BOOLEAN = "boolean";
//	public static final String TYPE_DATE = "date";
//	public static final String TYPE_TIME = "time";
//	public static final String TYPE_DATETIME = "dateTime";
//
//	public static final String TYPE_STRINGLIST = "stringList";
//	public static final String TYPE_INTEGERLIST = "integerList";
//	public static final String TYPE_LONGLIST = "longList";
//	public static final String TYPE_DOUBLELIST = "doubleList";
//	public static final String TYPE_BOOLEANLIST = "booleanList";
//
//	public static final String TYPE_STRINGLOB = "stringLob";
//	public static final String TYPE_STRINGMAP = "stringMap";

	public static void projectionWorkCompleted(Projection projection, Data data, WorkCompleted workCompleted)
			throws Exception {
		projection(projection, data, workCompleted);
	}

	public static void projectionTaskCompleted(Projection projection, Data data, TaskCompleted taskCompleted)
			throws Exception {
		projection(projection, data, taskCompleted);
	}

	public static void projectionRead(Projection projection, Data data, Read read) throws Exception {
		projection(projection, data, read);
	}

	public static void projectionReadCompleted(Projection projection, Data data, ReadCompleted readCompleted)
			throws Exception {
		projection(projection, data, readCompleted);
	}

	public static void projectionReview(Projection projection, Data data, Review review) throws Exception {
		projection(projection, data, review);
	}

	public static void projectionTable(Projection projection, Data data, JpaObject jpaObject) throws Exception {

		List<Projection.Item> items = XGsonBuilder.instance().fromJson(projection.getData(),
				new TypeToken<List<Projection.Item>>() {
				}.getType());

		for (Projection.Item item : items) {
			switch (item.getType()) {
			case JpaObject.TYPE_STRING:
				stringValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_INTEGER:
				integerValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_LONG:
				longValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_DOUBLE:
				doubleValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_BOOLEAN:
				booleanValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_DATE:
				dateValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_TIME:
				timeValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_DATETIME:
				dateTimeValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_STRINGLIST:
				stringListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_INTEGERLIST:
				integerListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_LONGLIST:
				longListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_DOUBLELIST:
				doubleListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_BOOLEANLIST:
				booleanListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_STRINGLOB:
				stringLobValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_STRINGMAP:
				stringMapValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			default:
				break;
			}
		}
	}

	private static void projection(Projection projection, Data data, JpaObject jpaObject) throws Exception {

		List<Projection.Item> items = XGsonBuilder.instance().fromJson(projection.getData(),
				new TypeToken<List<Projection.Item>>() {
				}.getType());

		FieldIndex fieldIndex = new FieldIndex();

		for (Projection.Item item : items) {
			switch (item.getType()) {
			case JpaObject.TYPE_STRING:
				stringValue(data, item.getPath(), jpaObject, fieldIndex.nextStringFieldName());
				break;

			case JpaObject.TYPE_INTEGER:
				integerValue(data, item.getPath(), jpaObject, fieldIndex.nextIntegerFieldName());
				break;

			case JpaObject.TYPE_LONG:
				longValue(data, item.getPath(), jpaObject, fieldIndex.nextLongFieldName());
				break;

			case JpaObject.TYPE_DOUBLE:
				doubleValue(data, item.getPath(), jpaObject, fieldIndex.nextDoubleFieldName());
				break;

			case JpaObject.TYPE_BOOLEAN:
				booleanValue(data, item.getPath(), jpaObject, fieldIndex.nextBooleanFieldName());
				break;

			case JpaObject.TYPE_DATE:
				dateValue(data, item.getPath(), jpaObject, fieldIndex.nextDateFieldName());
				break;

			case JpaObject.TYPE_TIME:
				timeValue(data, item.getPath(), jpaObject, fieldIndex.nextTimeFieldName());
				break;

			case JpaObject.TYPE_DATETIME:
				dateTimeValue(data, item.getPath(), jpaObject, fieldIndex.nextDateTimeFieldName());
				break;

			case JpaObject.TYPE_STRINGLIST:
				stringListValue(data, item.getPath(), jpaObject, fieldIndex.nextStringListFieldName());
				break;

			case JpaObject.TYPE_INTEGERLIST:
				integerListValue(data, item.getPath(), jpaObject, fieldIndex.nextIntegerListFieldName());
				break;

			case JpaObject.TYPE_LONGLIST:
				longListValue(data, item.getPath(), jpaObject, fieldIndex.nextLongListFieldName());
				break;

			case JpaObject.TYPE_DOUBLELIST:
				doubleListValue(data, item.getPath(), jpaObject, fieldIndex.nextDoubleListFieldName());
				break;

			case JpaObject.TYPE_BOOLEANLIST:
				booleanListValue(data, item.getPath(), jpaObject, fieldIndex.nextBooleanListFieldName());
				break;

			case JpaObject.TYPE_STRINGLOB:
				stringLobValue(data, item.getPath(), jpaObject, fieldIndex.nextStringLobFieldName());
				break;

			case JpaObject.TYPE_STRINGMAP:
				stringMapValue(data, item.getPath(), jpaObject, fieldIndex.nextStringMapFieldName());
				break;

			default:
				break;
			}
		}
	}

	private static void stringValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			String value = ListTools.toStringJoin(obj);
			value = StringTools.utf8SubString(value, JpaObject.length_255B);
			PropertyUtils.setProperty(jpaObject, property, value);
		}
	}

	private static void integerValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			if (Number.class.isAssignableFrom(obj.getClass())) {
				PropertyUtils.setProperty(jpaObject, property, ((Number) obj).intValue());
			} else {
				String str = Objects.toString(obj);
				if (NumberUtils.isCreatable(str)) {
					PropertyUtils.setProperty(jpaObject, property, NumberUtils.createInteger(str));
				}
			}
		}
	}

	private static void longValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			if (Number.class.isAssignableFrom(obj.getClass())) {
				PropertyUtils.setProperty(jpaObject, property, ((Number) obj).longValue());
			} else {
				String str = Objects.toString(obj);
				if (NumberUtils.isCreatable(str)) {
					PropertyUtils.setProperty(jpaObject, property, NumberUtils.createLong(str));
				}
			}
		}
	}

	private static void doubleValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			if (Number.class.isAssignableFrom(obj.getClass())) {
				PropertyUtils.setProperty(jpaObject, property, ((Number) obj).doubleValue());
			} else {
				String str = Objects.toString(obj);
				if (NumberUtils.isCreatable(str)) {
					PropertyUtils.setProperty(jpaObject, property, NumberUtils.createDouble(str));
				}
			}
		}
	}

	private static void booleanValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			PropertyUtils.setProperty(jpaObject, property, BooleanUtils.toBoolean(obj.toString()));
		}
	}

	private static void dateValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if ((null != obj) && DateTools.isDateTimeOrDate(obj.toString())) {
			PropertyUtils.setProperty(jpaObject, property, DateTools.parse(obj.toString()));
		} else {
			PropertyUtils.setProperty(jpaObject, property, null);
		}
	}

	private static void timeValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if ((null != obj) && DateTools.isDateTimeOrTime(obj.toString())) {
			PropertyUtils.setProperty(jpaObject, property, DateTools.parse(obj.toString()));
		} else {
			PropertyUtils.setProperty(jpaObject, property, null);
		}
	}

	private static void dateTimeValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if ((null != obj) && DateTools.isDateTime(obj.toString())) {
			PropertyUtils.setProperty(jpaObject, property, DateTools.parse(obj.toString()));
		} else {
			PropertyUtils.setProperty(jpaObject, property, null);
		}
	}

	private static void stringListValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		List<String> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					os.add(o.toString());
				}
			} else {
				os.add(obj.toString());
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void integerListValue(Data data, String path, JpaObject jpaObject, String property)
			throws Exception {
		List<Integer> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					if (NumberUtils.isCreatable(o.toString())) {
						os.add(NumberUtils.createInteger(o.toString()));
					}
				}
			} else {
				if (NumberUtils.isCreatable(obj.toString())) {
					os.add(NumberUtils.createInteger(obj.toString()));
				}
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void longListValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		List<Long> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					if (NumberUtils.isCreatable(o.toString())) {
						os.add(NumberUtils.createLong(o.toString()));
					}
				}
			} else {
				if (NumberUtils.isCreatable(obj.toString())) {
					os.add(NumberUtils.createLong(obj.toString()));
				}
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void doubleListValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		List<Double> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					if (NumberUtils.isCreatable(o.toString())) {
						os.add(NumberUtils.createDouble(o.toString()));
					}
				}
			} else {
				if (NumberUtils.isCreatable(obj.toString())) {
					os.add(NumberUtils.createDouble(obj.toString()));
				}
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void booleanListValue(Data data, String path, JpaObject jpaObject, String property)
			throws Exception {
		List<Boolean> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					os.add(BooleanUtils.toBoolean(Objects.toString(o)));
				}
			} else {
				os.add(BooleanUtils.toBoolean(Objects.toString(obj)));
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void stringLobValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			String value = ListTools.toStringJoin(obj);
			PropertyUtils.setProperty(jpaObject, property, value);
		}
	}

	private static void stringMapValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		Map<String, String> map = new TreeMap<String, String>();
		if ((null != obj) && (Map.class.isAssignableFrom(obj.getClass()))) {
			for (Entry<?, ?> en : ((Map<?, ?>) obj).entrySet()) {
				map.put(Objects.toString(en.getKey(), ""), Objects.toString(en.getValue(), ""));
			}
		}
		PropertyUtils.setProperty(jpaObject, property, map);
	}

	public static class FieldIndex extends GsonPropertyObject {

		private Integer stringIndex = 1;
		private Integer integerIndex = 1;
		private Integer longIndex = 1;
		private Integer boubleIndex = 1;
		private Integer booleanIndex = 1;
		private Integer dateIndex = 1;
		private Integer timeIndex = 1;
		private Integer dateTimeIndex = 1;
		private Integer stringListIndex = 1;
		private Integer integerListIndex = 1;
		private Integer longListIndex = 1;
		private Integer doubleListIndex = 1;
		private Integer booleanListIndex = 1;
		private Integer stringLobIndex = 1;
		private Integer stringMapIndex = 1;

		public String nextStringFieldName() {
			return String.format("%s%02d", "stringValue", stringIndex++);
		}

		public String nextIntegerFieldName() {
			return String.format("%s%02d", "integerValue", integerIndex++);
		}

		public String nextLongFieldName() {
			return String.format("%s%02d", "longValue", longIndex++);
		}

		public String nextDoubleFieldName() {
			return String.format("%s%02d", "doubleValue", boubleIndex++);
		}

		public String nextBooleanFieldName() {
			return String.format("%s%02d", "booleanValue", booleanIndex++);
		}

		public String nextDateFieldName() {
			return String.format("%s%02d", "dateValue", dateIndex++);
		}

		public String nextTimeFieldName() {
			return String.format("%s%02d", "timeValue", timeIndex++);
		}

		public String nextDateTimeFieldName() {
			return String.format("%s%02d", "dateTimeValue", dateTimeIndex++);
		}

		public String nextStringListFieldName() {
			return String.format("%s%02d", "stringListValue", stringListIndex++);
		}

		public String nextIntegerListFieldName() {
			return String.format("%s%02d", "integerListValue", integerListIndex++);
		}

		public String nextLongListFieldName() {
			return String.format("%s%02d", "longListValue", longListIndex++);
		}

		public String nextDoubleListFieldName() {
			return String.format("%s%02d", "doubleListValue", doubleListIndex++);
		}

		public String nextBooleanListFieldName() {
			return String.format("%s%02d", "booleanListValue", booleanListIndex++);
		}

		public String nextStringLobFieldName() {
			return String.format("%s%02d", "stringLobValue", stringLobIndex++);
		}

		public String nextStringMapFieldName() {
			return String.format("%s%02d", "stringMapValue", stringMapIndex++);
		}

	}

}
