package javaspring.training.polls.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javaspring.training.polls.model.Poll;
import javaspring.training.polls.payload.ApiResponse;
import javaspring.training.polls.payload.PagedResponse;
import javaspring.training.polls.payload.PollRequest;
import javaspring.training.polls.payload.PollResponse;
import javaspring.training.polls.payload.VoteRequest;
import javaspring.training.polls.security.CurrentUser;
import javaspring.training.polls.security.UserPrincipal;
import javaspring.training.polls.service.PollService;
import javaspring.training.polls.util.AppConstants;

@RestController
@RequestMapping("/api/polls")
public class PollController {
	@Autowired
	private PollService pollService;

	@GetMapping
	public PagedResponse<PollResponse> getPolls(@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", 
			defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", 
			defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
		return pollService.getAllPolls(currentUser, page, size);
	}
	
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse> createPoll(@Valid @RequestBody PollRequest pollRequest) {
		Poll poll = pollService.createPoll(pollRequest);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{pollId}")
				.buildAndExpand(poll.getId()).toUri();
		
		return ResponseEntity.created(location)
				.body(new ApiResponse(true, "Poll Created Successfully"));
	}
	
	@PostMapping("/{pollId}/votes")
	@PreAuthorize("hasRole('USER')")
	public PollResponse getPollById(@CurrentUser UserPrincipal currentUser, 
			@PathVariable Long pollId, @Valid @RequestBody VoteRequest voteRequest) {
		return pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, currentUser);
	}
}
