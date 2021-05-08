package de.legendlime.demo.model;

/**
 * Department Data Transfer Object used for security reasons
 * @author Thomas Kautenburger
 *
 */

public class DepartmentDTO {
	
	private Long deptId;
	private String name;
	private String description;
		
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
