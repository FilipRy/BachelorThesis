package com.filip.dressfriend;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Friendship {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private User user1;
	@ManyToOne
	private User user2;
	@Temporal(value = TemporalType.DATE)
	private Date friendsSince;
	
	public Friendship() {
		
	}
	
	public Friendship(User user1, User user2) {
		this.user1 = user1;
		this.user2 = user2;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser1() {
		return user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	public Date getFriendsSince() {
		return friendsSince;
	}

	public void setFriendsSince(Date friendsSince) {
		this.friendsSince = friendsSince;
	}
	
	


	@Override
	public String toString() {
		return "Friendship [id=" + id + ", user1=" + user1 + ", user2=" + user2
				+ ", friendsSince=" + friendsSince + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Friendship other = (Friendship) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (user1 == null) {
			if (other.user1 != null)
				return false;
		} else if (!user1.equals(other.user1))
			return false;
		if (user2 == null) {
			if (other.user2 != null)
				return false;
		} else if (!user2.equals(other.user2))
			return false;
		return true;
	}
	
	
	
}
