package fr.rakambda.channelpointsminer.viewer.api;

import fr.rakambda.channelpointsminer.viewer.api.data.BalanceData;
import fr.rakambda.channelpointsminer.viewer.api.data.ChannelData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class ApiControllerTest{
    @MockitoBean
    private BalanceService balanceService;
    @MockitoBean
    private ChannelService channelService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testListChannels() throws Exception{
        when(channelService.listAll()).thenReturn(List.of(
                ChannelData.builder().id("ID1").username("USER1").build(),
                ChannelData.builder().id("ID2").username("USER2").build()
        ));
        
        mockMvc.perform(get("/api/channel"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [
                        {
                            "id": "ID1",
                            "username": "USER1"
                        },
                        {
                            "id": "ID2",
                            "username": "USER2"
                        }
                    ]"""));
    }
    
    @Test
    void testGetAllBalance() throws Exception{
        var channelId = "CID1";
        
        when(balanceService.getAllBalance(channelId)).thenReturn(List.of(
		        BalanceData.builder().date(Instant.parse("2022-04-15T19:14:20.123Z")).balance(100).reason("R1").build(),
		        BalanceData.builder().date(Instant.parse("2022-04-15T19:16:20.000Z")).balance(200).build()
        ));
        
        mockMvc.perform(get("/api/balance/{channelId}/all", channelId))
                .andExpect(status().isOk())
                .andExpect(content().json("""
		                [
		                    {
		                        "date": "2022-04-15T19:14:20.123Z",
		                        "balance": 100,
		                        "reason": "R1"
		                    },
		                    {
		                        "date": "2022-04-15T19:16:20Z",
		                        "balance": 200
		                    }
		                ]"""));
    }
}