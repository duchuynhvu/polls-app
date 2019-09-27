package javaspring.training.polls.payload;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class PollResponse {
	private Long id;
	private String question;
	private List<ChoiceResponse> choices = new ArrayList<>();
	private UserSummary createdBy;
	private Instant creationDateTime;
	private Instant expirationDateTime;
	private Boolean isExpired;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long selectedChoice;
	private Long totalVotes;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public List<ChoiceResponse> getChoices() {
		return new ArrayList<>(choices);
	}
	public void setChoices(List<ChoiceResponse> choices) {
		this.choices = new ArrayList<>(choices);
	}
	public UserSummary getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(UserSummary createdBy) {
		this.createdBy = createdBy;
	}
	public Instant getCreationDateTime() {
		return creationDateTime;
	}
	public void setCreationDateTime(Instant creationDateTime) {
		this.creationDateTime = creationDateTime;
	}
	public Instant getExpirationDateTime() {
		return expirationDateTime;
	}
	public void setExpirationDateTime(Instant expirationDateTime) {
		this.expirationDateTime = expirationDateTime;
	}
	public Boolean getIsExpired() {
		return isExpired;
	}
	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}
	public Long getSelectedChoice() {
		return selectedChoice;
	}
	public void setSelectedChoice(Long selectedChoice) {
		this.selectedChoice = selectedChoice;
	}
	public Long getTotalVotes() {
		return totalVotes;
	}
	public void setTotalVotes(Long totalVotes) {
		this.totalVotes = totalVotes;
	}
}
