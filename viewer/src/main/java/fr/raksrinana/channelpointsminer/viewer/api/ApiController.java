package fr.raksrinana.channelpointsminer.viewer.api;

import fr.raksrinana.channelpointsminer.viewer.api.data.BalanceData;
import fr.raksrinana.channelpointsminer.viewer.api.data.ChannelData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collection;

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
