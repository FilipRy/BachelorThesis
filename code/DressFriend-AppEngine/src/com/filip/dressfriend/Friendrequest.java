package com.filip.dressfriend;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Friendrequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private User userFrom;
	@ManyToOne
	private User userTo;

	public Friendrequest() {
		
	}
	
	public Friendrequest(User userFrom, User userTo){
		this.setUserFrom(userFrom);
		this.setUserTo(userTo);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUserFrom() {
		return userFrom;
	}

	public void setUserFrom(User userFrom) {
		this.userFrom = userFrom;
	}

	public User getUserTo() {
		return userTo;
	}

	public void setUserTo(User userTo) {
		this.userTo = userTo;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Friendrequest other = (Friendrequest) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (userFrom == null) {
			if (other.userFrom != null)
				return false;
		} else if (!userFrom.equals(other.userFrom))
			return false;
		if (userTo == null) {
			if (other.userTo != null)
				return false;
		} else if (!userTo.equals(other.userTo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Friendrequest [id=" + id + ", userFrom=" + userFrom
				+ ", userTo=" + userTo + "]";
	}
	
	
	
	
}
