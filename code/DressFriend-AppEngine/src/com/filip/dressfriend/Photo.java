package com.filip.dressfriend;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
@com.google.appengine.repackaged.org.codehaus.jackson.annotate.JsonIgnoreProperties({"simplePost", "filename"})
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String description;
	private String path;
	@ManyToOne
	private SimplePost simplePost;
	@Transient
	private long likesCount;
	@Transient
	private long dislikesCount;
	@Transient
	private int commentsCount;
	
	
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
	public SimplePost getSimplePost() {
		return simplePost;
	}
	public void setSimplePost(SimplePost simplePost) {
		this.simplePost = simplePost;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Long getLikesCount() {
		return likesCount;
	}
	public void setLikesCount(Long likesCount) {
		this.likesCount = likesCount;
	}
	public Long getDislikesCount() {
		return dislikesCount;
	}
	public void setDislikesCount(Long dislikesCount) {
		this.dislikesCount = dislikesCount;
	}
	public int getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}
	
	@Transient
	public String getFilename() {
		int lastIndexOfSlash = path.lastIndexOf("/");
		int lastIndexOfURLArg = path.lastIndexOf("?");
		String filename = path.substring(lastIndexOfSlash + 1, lastIndexOfURLArg);
		return filename;
	}

	@Override
	public boolean equals(Object obj) {//equals method without likes/Dislikes count
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Photo other = (Photo) obj;
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
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Photo [id=" + id + ", description=" + description + ", path=" + path + ", likesCount="
				+ likesCount + ", dislikesCount=" + dislikesCount + "]";
	}
	
	
	
}
