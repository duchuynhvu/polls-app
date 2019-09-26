package javaspring.training.polls.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javaspring.training.polls.exception.ResourceNotFoundException;
import javaspring.training.polls.model.User;
import javaspring.training.polls.payload.PagedResponse;
import javaspring.training.polls.payload.PollResponse;
import javaspring.training.polls.payload.UserIdentityAvailability;
import javaspring.training.polls.payload.UserProfile;
import javaspring.training.polls.payload.UserSummary;
import javaspring.training.polls.repository.PollRepository;
import javaspring.training.polls.repository.UserRepository;
import javaspring.training.polls.repository.VoteRepository;
import javaspring.training.polls.security.CurrentUser;
import javaspring.training.polls.security.UserPrincipal;
import javaspring.training.polls.service.PollService;
import javaspring.training.polls.util.AppConstants;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PollRepository pollRepository;
	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	private PollService pollService;
	
	@GetMapping("/user/me")
	@PreAuthorize("hasRole('USER')")
	public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
		UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
		return userSummary;
	}
	
	@GetMapping("/user/checkUsernameAvailability")
	public UserIdentityAvailability checkUsernameAvailability(
			@RequestParam(value = "username") String username) {
		Boolean isAvailable = !userRepository.existsByUsername(username);
		return new UserIdentityAvailability(isAvailable);
	}
	
	@GetMapping("/users/{username}")
	public UserProfile getUserProfile(
			@PathVariable(value = "username") String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(()->new ResourceNotFoundException("User", "username", username));
		
		long pollCount = pollRepository.countByCreatedBy(user.getId());
		long voteCount = voteRepository.countByUserId(user.getId());
		
		return new UserProfile(user.getId(), 
		        user.getUsername(), 
				user.getName(), 
				user.getCreatedAt(), 
				pollCount, 
				voteCount);
	}
	
	@GetMapping("/users/{username}/polls")
	public PagedResponse<PollResponse> getPollsCreatedBy(
			@PathVariable(value = "username") String username,
			@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
		return pollService.getPollsCreatedBy(username, currentUser, page, size);
	}
	
	@GetMapping("/users/{username}/votes")
	public PagedResponse<PollResponse> getPollsVotedBy(
			@PathVariable(value = "username") String username,
			@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size){
		return pollService.getPollsCreatedBy(username, currentUser, page, size);
	}
}
