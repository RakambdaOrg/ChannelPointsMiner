package fr.raksrinana.channelpointsminer.miner.handler.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PredictionState{
	CREATED(false),
	SCHEDULING(false),
	SCHEDULED(false),
	BET_ERROR(true),
	PLACED(false),
	FINISHED(true);
	
	private final boolean terminal;
}
