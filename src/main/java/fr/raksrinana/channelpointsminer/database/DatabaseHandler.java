package fr.raksrinana.channelpointsminer.database;

import fr.raksrinana.channelpointsminer.database.entity.ChannelEntity;
import fr.raksrinana.channelpointsminer.event.IEvent;
import fr.raksrinana.channelpointsminer.event.IEventListener;
import fr.raksrinana.channelpointsminer.event.IStreamerEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamDownEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamUpEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamerAddedEvent;
import fr.raksrinana.channelpointsminer.factory.TimeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;

@RequiredArgsConstructor
@Log4j2
public class DatabaseHandler implements IEventListener{
	@NotNull
	private IDatabase database;
	
	@Override
	public void onEvent(IEvent event){
		try{
			if(event instanceof StreamerAddedEvent e){
				addStreamer(e);
			}
			else if(event instanceof StreamUpEvent e){
				updateStreamer(e);
			}
			else if(event instanceof StreamDownEvent e){
				updateStreamer(e);
			}
		}
		catch(Exception e){
			log.error("Failed to process database event", e);
		}
	}
	
	private void addStreamer(@NotNull IStreamerEvent event) throws SQLException{
		var entity = ChannelEntity.builder()
				.id(event.getStreamerId())
				.username(event.getStreamerUsername().orElseThrow(() -> new IllegalStateException("No username present in streamer")))
				.lastStatusChange(TimeFactory.now())
				.build();
		
		database.createChannelOrUpdate(entity);
	}
	
	private void updateStreamer(@NotNull IStreamerEvent event) throws SQLException{
		database.updateChannelStatusTime(event.getStreamerId(), TimeFactory.now());
	}
	
	@Override
	public void close(){
		database.close();
	}
}
