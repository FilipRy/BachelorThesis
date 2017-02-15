package com.filip.dressfriend;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class SimplePost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private User postedBy;
	private String description;
	// timestamp has to be saved in DB, because of centralized time for every
	// timezone
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date postTime;
	private String PHOTO_IDS;
	@Transient
	private List<Photo> photos;
	@Transient
	private List<User> postViewers;
	@Transient
	private boolean likedByMe;
	@Transient
	private boolean dislikedByMe;
	@Transient
	private Likes myLike;
	@Transient
	private Dislikes myDislike;

	public boolean isLikedByMe() {
		return likedByMe;
	}

	public void setLikedByMe(boolean likedByMe) {
		this.likedByMe = likedByMe;
	}

	public boolean isDislikedByMe() {
		return dislikedByMe;
	}

	public void setDislikedByMe(boolean dislikedByMe) {
		this.dislikedByMe = dislikedByMe;
	}

	public Likes getMyLike() {
		return myLike;
	}

	public void setMyLike(Likes myLike) {
		this.myLike = myLike;
	}

	public Dislikes getMyDislike() {
		return myDislike;
	}

	public void setMyDislike(Dislikes myDislike) {
		this.myDislike = myDislike;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public String getPHOTO_IDS() {
		return PHOTO_IDS;
	}

	public void setPHOTO_IDS(String pHOTO_IDS) {
		PHOTO_IDS = pHOTO_IDS;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePost other = (SimplePost) obj;
		if (PHOTO_IDS == null) {
			if (other.PHOTO_IDS != null)
				return false;
		} else if (!PHOTO_IDS.equals(other.PHOTO_IDS))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (photos == null) {
			if (other.photos != null)
				return false;
		} else if (!photos.equals(other.photos))
			return false;
		if (postedBy == null) {
			if (other.postedBy != null)
				return false;
		} else if (!postedBy.equals(other.postedBy))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimplePost [id=" + id + ", postedBy=" + postedBy + ", description=" + description
				+ ", postTime=" + postTime + ", PHOTO_IDS=" + PHOTO_IDS + ", photos=" + photos + "]";
	}

	public List<User> getPostViewers() {
		return postViewers;
	}

	public void setUserCanSeePost(List<User> userCanSeePost) {
		this.postViewers = userCanSeePost;
	}

}
