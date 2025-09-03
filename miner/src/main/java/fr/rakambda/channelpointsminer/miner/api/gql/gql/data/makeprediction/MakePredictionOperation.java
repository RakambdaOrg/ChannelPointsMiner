package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.makeprediction;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class MakePredictionOperation extends IGQLOperation<MakePredictionData>{
	public MakePredictionOperation(@NonNull String eventId, @NonNull String outcomeId, int amount, @NonNull String transactionId){
		super("MakePrediction");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "b44682ecc88358817009f20e69d75081b1e58825bb40aa53d5dbadcc17c881d8"));
		addVariable("input", InputData.builder()
				.eventId(eventId)
				.outcomeId(outcomeId)
				.points(amount)
				.transactionId(transactionId)
				.build());
	}
	
	@Override
	@NonNull
	public GenericType<GQLResponse<MakePredictionData>> getResponseType(){
		return new GenericType<>(){};
	}
}
