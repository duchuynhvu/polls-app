package javaspring.training.polls.model.audit;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
		value = {"createdAt", "updatedAt"},
		allowGetters = true
)
public abstract class DateAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private Instant CreatedAt;
	
	@LastModifiedDate
	@Column(nullable = false)
	private Instant UpdatedAt;

	public Instant getCreatedAt() {
		return CreatedAt;
	}

	public void setCreatedAt(Instant createdAt) {
		CreatedAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return UpdatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		UpdatedAt = updatedAt;
	}
}
