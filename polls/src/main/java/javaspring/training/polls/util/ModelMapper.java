package javaspring.training.polls.util;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javaspring.training.polls.model.Poll;
import javaspring.training.polls.model.User;
import javaspring.training.polls.payload.ChoiceResponse;
import javaspring.training.polls.payload.PollResponse;
import javaspring.training.polls.payload.UserSummary;

public class ModelMapper {

    private ModelMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static PollResponse mapPollToPollResponse(Poll poll,
            Map<Long, Long> choiceVotesMap, User creator, Long userVote) {
        PollResponse pollResponse = new PollResponse();
        pollResponse.setId(poll.getId());
        pollResponse.setQuestion(poll.getQuestion());
        pollResponse.setCreationDateTime(poll.getCreatedAt());
        pollResponse.setExpirationDateTime(poll.getExpirationDateTime());
        Instant now = Instant.now();
        pollResponse.setIsExpired(poll.getExpirationDateTime().isBefore(now));

        List<ChoiceResponse> choiceResponses = poll.getChoices().stream()
                .map(choice -> {
                    ChoiceResponse choiceResponse = new ChoiceResponse();
                    choiceResponse.setId(choice.getId());
                    choiceResponse.setText(choice.getText());

                    if (choiceVotesMap.containsKey(choice.getId())) {
                        choiceResponse.setVoteCount(
                                choiceVotesMap.get(choice.getId()));
                    } else {
                        choiceResponse.setVoteCount(0);
                    }
                    return choiceResponse;
                }).collect(Collectors.toList());

        pollResponse.setChoices(choiceResponses);
        UserSummary creatorSummary = new UserSummary(creator.getId(),
                creator.getUsername(), creator.getName());
        pollResponse.setCreatedBy(creatorSummary);

        if (userVote != null) {
            pollResponse.setSelectedChoice(userVote);
        }

        long totalVotes = pollResponse.getChoices().stream()
                .mapToLong(ChoiceResponse::getVoteCount).sum();
        pollResponse.setTotalVotes(totalVotes);

        return pollResponse;
    }
}
