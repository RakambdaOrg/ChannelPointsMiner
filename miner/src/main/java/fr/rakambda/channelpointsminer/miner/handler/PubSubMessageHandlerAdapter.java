package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.BroadcastSettingsUpdate;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.ClaimAvailable;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.CommunityMomentStart;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.CreateNotification;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.DropClaim;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.DropProgress;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.EventCreated;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.EventUpdated;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PointsEarned;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PointsSpent;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionMade;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionResult;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionUpdated;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.RaidUpdateV2;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.StreamDown;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.StreamUp;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.ViewCount;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.util.ClassWalker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unused")
public abstract class PubSubMessageHandlerAdapter implements IPubSubMessageHandler{
	private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
	private static final ConcurrentMap<Class<?>, MethodHandle> methods = new ConcurrentHashMap<>();
	private static final Set<Class<?>> unresolved;
	
	public void onClaimAvailable(@NotNull Topic topic, @NotNull ClaimAvailable message){}
	
	public void onEventCreated(@NotNull Topic topic, @NotNull EventCreated message){}
	
	public void onEventUpdated(@NotNull Topic topic, @NotNull EventUpdated message){}
	
	public void onPointsEarned(@NotNull Topic topic, @NotNull PointsEarned message){}
	
	public void onPointsSpent(@NotNull Topic topic, @NotNull PointsSpent message){}
	
	public void onRaidUpdateV2(@NotNull Topic topic, @NotNull RaidUpdateV2 message){}
	
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){}
	
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){}
	
	public void onPredictionMade(@NotNull Topic topic, @NotNull PredictionMade message){}
	
	public void onPredictionResult(@NotNull Topic topic, @NotNull PredictionResult message){}
	
	public void onPredictionUpdated(@NotNull Topic topic, @NotNull PredictionUpdated message){}
	
	public void onCreateNotification(@NotNull Topic topic, @NotNull CreateNotification message){}
	
	public void onCommunityMomentStart(@NotNull Topic topic, @NotNull CommunityMomentStart message){}
	
	public void onDropProgress(@NotNull Topic topic, @NotNull DropProgress message){}
	
	public void onDropClaim(@NotNull Topic topic, @NotNull DropClaim message){}
	
	public void onViewCount(@NotNull Topic topic, @NotNull ViewCount message){}
	
	public void onBroadcastSettingsUpdate(@NotNull Topic topic, @NotNull BroadcastSettingsUpdate message){}
	
	@Override
	public void handle(@NotNull Topic topic, @NotNull IPubSubMessage message){
		for(var clazz : ClassWalker.range(message.getClass(), IPubSubMessage.class)){
			if(unresolved.contains(clazz)){
				continue;
			}
			var methodHandle = methods.computeIfAbsent(clazz, PubSubMessageHandlerAdapter::findMethod);
			if(methodHandle == null){
				unresolved.add(clazz);
				continue;
			}
			
			try{
				methodHandle.invoke(this, topic, message);
			}
			catch(Throwable throwable){
				if(throwable instanceof RuntimeException){
					throw (RuntimeException) throwable;
				}
				if(throwable instanceof Error){
					throw (Error) throwable;
				}
				throw new IllegalStateException(throwable);
			}
		}
	}
	
	@Nullable
	private static MethodHandle findMethod(@NotNull Class<?> clazz){
		var name = clazz.getSimpleName();
		var type = MethodType.methodType(Void.TYPE, Topic.class, clazz);
		try{
			name = "on" + name;
			return lookup.findVirtual(PubSubMessageHandlerAdapter.class, name, type);
		}
		catch(NoSuchMethodException | IllegalAccessException ignored){
		} // this means this is probably a custom event!
		return null;
	}
	
	static{
		unresolved = ConcurrentHashMap.newKeySet();
		Collections.addAll(unresolved,
				Object.class // Objects aren't events
		);
	}
}
