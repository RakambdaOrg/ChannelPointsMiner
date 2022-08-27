package fr.raksrinana.channelpointsminer.miner.handler;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.ChannelLastViewedContentUpdated;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.ClaimClaimed;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.Commercial;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.CreateNotification;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.DeleteNotification;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.EventCreated;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.EventUpdated;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.GlobalLastViewedContentUpdated;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.IPubSubMessage;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.PointsEarned;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.PointsSpent;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.PredictionMade;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.PredictionResult;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.PredictionUpdated;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.RaidGoV2;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.RaidUpdateV2;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.StreamDown;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.StreamUp;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.UpdateSummary;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.ViewCount;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.util.ClassWalker;
import org.jetbrains.annotations.NotNull;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unused")
public abstract class TwitchWsEventHandlerAdapter implements IPubSubMessageHandler{
	private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
	private static final ConcurrentMap<Class<?>, MethodHandle> methods = new ConcurrentHashMap<>();
	private static final Set<Class<?>> unresolved;
	
	public void onChannelLastViewedContentUpdated(@NotNull Topic topic, @NotNull ChannelLastViewedContentUpdated message){}
	
	public void onClaimAvailable(@NotNull Topic topic, @NotNull ClaimAvailable message){}
	
	public void onClaimClaimed(@NotNull Topic topic, @NotNull ClaimClaimed message){}
	
	public void onCommercial(@NotNull Topic topic, @NotNull Commercial message){}
	
	public void onEventCreated(@NotNull Topic topic, @NotNull EventCreated message){}
	
	public void onEventUpdated(@NotNull Topic topic, @NotNull EventUpdated message){}
	
	public void onGlobalLastViewedContentUpdated(@NotNull Topic topic, @NotNull GlobalLastViewedContentUpdated message){}
	
	public void onPointsEarned(@NotNull Topic topic, @NotNull PointsEarned message){}
	
	public void onPointsSpent(@NotNull Topic topic, @NotNull PointsSpent message){}
	
	public void onRaidGoV2(@NotNull Topic topic, @NotNull RaidGoV2 message){}
	
	public void onRaidUpdateV2(@NotNull Topic topic, @NotNull RaidUpdateV2 message){}
	
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){}
	
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){}
	
	public void onViewCount(@NotNull Topic topic, @NotNull ViewCount message){}
	
	public void onPredictionMade(@NotNull Topic topic, @NotNull PredictionMade message){}
	
	public void onPredictionResult(@NotNull Topic topic, @NotNull PredictionResult message){}
	
	public void onPredictionUpdated(@NotNull Topic topic, @NotNull PredictionUpdated message){}
	
	public void onCreateNotification(@NotNull Topic topic, @NotNull CreateNotification message){}
	
	public void onDeleteNotification(@NotNull Topic topic, @NotNull DeleteNotification message){}
	
	public void onUpdateSummary(@NotNull Topic topic, @NotNull UpdateSummary message){}
	
	@Override
	public void handle(@NotNull Topic topic, @NotNull IPubSubMessage message){
		for(var clazz : ClassWalker.range(message.getClass(), IPubSubMessage.class)){
			if(unresolved.contains(clazz)){
				continue;
			}
			var methodHandle = methods.computeIfAbsent(clazz, TwitchWsEventHandlerAdapter::findMethod);
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
	
	private static MethodHandle findMethod(Class<?> clazz){
		var name = clazz.getSimpleName();
		var type = MethodType.methodType(Void.TYPE, Topic.class, clazz);
		try{
			name = "on" + name;
			return lookup.findVirtual(TwitchWsEventHandlerAdapter.class, name, type);
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
