package fr.raksrinana.channelpointsminer.api.gql.data.makeprediction;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class MakePredictionOperation extends IGQLOperation<MakePredictionData>{
	public MakePredictionOperation(@NotNull String eventId, @NotNull String outcomeId, int amount, @NotNull String transactionId){
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
	@NotNull
	public GenericType<GQLResponse<MakePredictionData>> getResponseType(){
		return new GenericType<>(){};
	}
}
