package com.course.kafka.broker.stream.feedback.rating;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import com.course.kafka.broker.message.FeedbackMessage;
import com.course.kafka.broker.message.FeedbackRatingOneMessage;

public class FeedbackRatingOneTransformer implements ValueTransformer<FeedbackMessage, FeedbackRatingOneMessage> {

	private ProcessorContext processorContext;
	private final String stateStoreName;
	private KeyValueStore<String, FeedbackRatingOneStoreValue> ratingStateStore;

	public FeedbackRatingOneTransformer(String stateStoreName) {
		if (StringUtils.isEmpty(stateStoreName)) {
			throw new IllegalArgumentException("State store name must not empty");
		}

		this.stateStoreName = stateStoreName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ProcessorContext context) {
		this.processorContext = context;
		this.ratingStateStore = (KeyValueStore<String, FeedbackRatingOneStoreValue>) this.processorContext
				.getStateStore(stateStoreName);
	}

	@Override
	public FeedbackRatingOneMessage transform(FeedbackMessage feedbackMessage) {
		var storeValue = Optional.ofNullable(ratingStateStore.get(feedbackMessage.getLocation()))
				.orElse(new FeedbackRatingOneStoreValue());

		// update new store
		var newSumRating = storeValue.getSumRating() + feedbackMessage.getRating();
		storeValue.setSumRating(newSumRating);
		var newCountRating = storeValue.getCountRating() + 1;
		storeValue.setCountRating(newCountRating);

		// put new store to state store
		ratingStateStore.put(feedbackMessage.getLocation(), storeValue);

		// build branch rating
		var branchRating = new FeedbackRatingOneMessage();
		branchRating.setLocation(feedbackMessage.getLocation());
		double averageRating = Math.round((double) newSumRating / newCountRating * 10d) / 10d;
		branchRating.setAverageRating(averageRating);

		return branchRating;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
