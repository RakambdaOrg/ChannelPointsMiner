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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
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
	
	public void onClaimAvailable(@NonNull Topic topic, @NonNull ClaimAvailable message){}
	
	public void onEventCreated(@NonNull Topic topic, @NonNull EventCreated message){}
	
	public void onEventUpdated(@NonNull Topic topic, @NonNull EventUpdated message){}
	
	public void onPointsEarned(@NonNull Topic topic, @NonNull PointsEarned message){}
	
	public void onPointsSpent(@NonNull Topic topic, @NonNull PointsSpent message){}
	
	public void onRaidUpdateV2(@NonNull Topic topic, @NonNull RaidUpdateV2 message){}
	
	public void onStreamDown(@NonNull Topic topic, @NonNull StreamDown message){}
	
	public void onStreamUp(@NonNull Topic topic, @NonNull StreamUp message){}
	
	public void onPredictionMade(@NonNull Topic topic, @NonNull PredictionMade message){}
	
	public void onPredictionResult(@NonNull Topic topic, @NonNull PredictionResult message){}
	
	public void onPredictionUpdated(@NonNull Topic topic, @NonNull PredictionUpdated message){}
	
	public void onCreateNotification(@NonNull Topic topic, @NonNull CreateNotification message){}
	
	public void onCommunityMomentStart(@NonNull Topic topic, @NonNull CommunityMomentStart message){}
	
	public void onDropProgress(@NonNull Topic topic, @NonNull DropProgress message){}
	
	public void onDropClaim(@NonNull Topic topic, @NonNull DropClaim message){}
	
	public void onViewCount(@NonNull Topic topic, @NonNull ViewCount message){}
	
	public void onBroadcastSettingsUpdate(@NonNull Topic topic, @NonNull BroadcastSettingsUpdate message){}
	
	@Override
	public void handle(@NonNull Topic topic, @NonNull IPubSubMessage message){
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
	private static MethodHandle findMethod(@NonNull Class<?> clazz){
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
