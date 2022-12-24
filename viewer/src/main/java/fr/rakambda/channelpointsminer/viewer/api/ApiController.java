package fr.rakambda.channelpointsminer.viewer.api;

import fr.rakambda.channelpointsminer.viewer.api.data.BalanceData;
import fr.rakambda.channelpointsminer.viewer.api.data.ChannelBalanceData;
import fr.rakambda.channelpointsminer.viewer.api.data.ChannelData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController{
	private final BalanceService balanceService;
	private final ChannelService channelService;
	
	@Autowired
	public ApiController(BalanceService balanceService, ChannelService channelService){
		this.balanceService = balanceService;
		this.channelService = channelService;
	}
	
	@GetMapping("/balance/all/range")
	public ResponseEntity<Map<String, ChannelBalanceData>> getBalance(@RequestParam("start") long start, @RequestParam("end") long end){
		var startInstant = Instant.ofEpochMilli(start);
		var endInstant = Instant.ofEpochMilli(end);
		
		var data = channelService.listAll().stream()
				.map(channel -> Pair.of(channel, balanceService.getRangeBalance(channel.getId(), startInstant, endInstant)))
				.filter(p -> !p.getSecond().isEmpty())
				.map(p -> ChannelBalanceData.builder()
						.channelId(p.getFirst().getId())
						.username(p.getFirst().getUsername())
						.balance(p.getSecond())
						.build())
				.collect(Collectors.toMap(ChannelBalanceData::getChannelId, d -> d));
		
		return ResponseEntity.ok(data);
	}
	
	@GetMapping("/balance/{channelId}/all")
	public ResponseEntity<Collection<BalanceData>> getBalance(@PathVariable("channelId") String channelId){
		var data = balanceService.getAllBalance(channelId);
		return ResponseEntity.ok(data);
	}
	
	@GetMapping("/channel")
	public ResponseEntity<Collection<ChannelData>> listChannels(){
		var data = channelService.listAll();
		return ResponseEntity.ok(data);
	}
}
