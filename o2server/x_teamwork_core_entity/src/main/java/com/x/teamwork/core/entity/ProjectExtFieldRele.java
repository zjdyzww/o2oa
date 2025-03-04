package com.x.teamwork.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.ProjectExtFieldRele.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ProjectExtFieldRele.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ProjectExtFieldRele extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	
	private static final String TABLE = PersistenceProperties.ProjectExtFieldRele.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成（必填）.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
	}
	/*
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */	
	public static final String projectId_FIELDNAME = "projectId";
	@FieldDescribe("项目ID（必填）")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + projectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + projectId_FIELDNAME)
	private String projectId;

	public static final String extFieldName_FIELDNAME = "extFieldName";
	@FieldDescribe("备用列名称（必填）")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + extFieldName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extFieldName_FIELDNAME)
	private String extFieldName;
	
	public static final String displayName_FIELDNAME = "displayName";
	@FieldDescribe("显示属性名称（必填）")
	@Column( length = JpaObject.length_64B, name = ColumnNamePrefix + displayName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + displayName_FIELDNAME)
	private String displayName;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getExtFieldName() {
		return extFieldName;
	}

	public void setExtFieldName(String extFieldName) {
		this.extFieldName = extFieldName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}	
}