package de.legendlime.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Department domain object. Persistable interface must be used
 * to tell the database if we make an INSERT or an UPDATE
 * @author Thomas Kautenburger
 *
 */
@Table("department")
public class Department implements Persistable<Long> {
	
    @Id
	private Long deptId;
	private String name;
	private String description;
		
    @Transient
    private boolean newDepartment;

	@Override
	@Transient
	public Long getId() {
		return this.getDeptId();
	}
    @Override
    @Transient
    public boolean isNew() {
        return this.newDepartment || deptId == null;
    }

    public Department setAsNew() {
        this.newDepartment = true;
        return this;
    }

	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(long deptId) {
		this.deptId = deptId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
