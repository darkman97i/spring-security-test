/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.securitytest.bean;

import com.openkm.securitytest.util.CustomToStringStyle;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "OKM_ROLE")
public class DbRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ROL_ID", length = 64)
	private String id = "";

	@Column(name = "ROL_ACTIVE")
	@Type(type = "true_false")
	private Boolean active = false;

	@ManyToMany(mappedBy = "dbRoles")
	private Set<DbUser> dbUsers = new HashSet<>();

	public DbRole() {
	}

	public DbRole(String id) {
		this.id = id;
	}

	public Boolean getActive() {
		if (active == null) {
			return false;
		}
		return active;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (this.getClass() == obj.getClass()) {
			DbRole other = (DbRole) obj;

			if (this.getId().equals(other.getId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, CustomToStringStyle.SHORT_PREFIX_STYLE)
				.append("id", id).append("active", active).toString();
	}
}
