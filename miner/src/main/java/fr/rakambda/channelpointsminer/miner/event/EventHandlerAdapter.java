package fr.rakambda.channelpointsminer.miner.event;

import fr.rakambda.channelpointsminer.miner.event.impl.ChatMessageEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimAvailableEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.EventCreatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.EventUpdatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.MinerStartedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsEarnedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsSpentEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionResultEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerRemovedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerUnknownEvent;
import fr.rakambda.channelpointsminer.miner.util.ClassWalker;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public abstract class EventHandlerAdapter implements IEventHandler{
	private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
	private static final ConcurrentMap<Class<?>, MethodHandle> methods = new ConcurrentHashMap<>();
	private static final Set<Class<?>> unresolved;
	
	public void onClaimAvailableEvent(@NonNull ClaimAvailableEvent event) throws Exception{}
	
	public void onDropClaimEvent(@NonNull DropClaimEvent event) throws Exception{}
	
	public void onEventCreatedEvent(@NonNull EventCreatedEvent event) throws Exception{}
	
	public void onEventUpdatedEvent(@NonNull EventUpdatedEvent event) throws Exception{}
	
	public void onMinerStartedEvent(@NonNull MinerStartedEvent event) throws Exception{}
	
	public void onPointsEarnedEvent(@NonNull PointsEarnedEvent event) throws Exception{}
	
	public void onPointsSpentEvent(@NonNull PointsSpentEvent event) throws Exception{}
	
	public void onPredictionMadeEvent(@NonNull PredictionMadeEvent event) throws Exception{}
	
	public void onPredictionResultEvent(@NonNull PredictionResultEvent event) throws Exception{}
	
	public void onStreamDownEvent(@NonNull StreamDownEvent event) throws Exception{}
	
	public void onStreamerAddedEvent(@NonNull StreamerAddedEvent event) throws Exception{}
	
	public void onStreamerRemovedEvent(@NonNull StreamerRemovedEvent event) throws Exception{}
	
	public void onStreamerUnknownEvent(@NonNull StreamerUnknownEvent event) throws Exception{}
	
	public void onStreamUpEvent(@NonNull StreamUpEvent event) throws Exception{}
	
	public void onChatMessageEvent(@NonNull ChatMessageEvent event) throws Exception{}
	
	public void onILoggableEvent(@NonNull ILoggableEvent event) throws Exception{}
	
	@Override
	public void onEvent(@NonNull IEvent event){
		for(var clazz : ClassWalker.range(event.getClass(), IEvent.class)){
			if(unresolved.contains(clazz)){
				continue;
			}
			var methodHandle = methods.computeIfAbsent(clazz, EventHandlerAdapter::findMethod);
			if(methodHandle == null){
				unresolved.add(clazz);
				continue;
			}
			
			try{
				methodHandle.invoke(this, event);
			}
			catch(Throwable throwable){
				log.error("EventHandler threw an exception", throwable);
			}
		}
	}
	
	@Nullable
	private static MethodHandle findMethod(@NonNull Class<?> clazz){
		var name = clazz.getSimpleName();
		var type = MethodType.methodType(Void.TYPE, clazz);
		try{
			name = "on" + name;
			return lookup.findVirtual(EventHandlerAdapter.class, name, type);
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
