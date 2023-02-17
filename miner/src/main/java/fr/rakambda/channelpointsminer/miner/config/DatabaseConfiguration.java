package fr.rakambda.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonClassDescription("Database configuration.")
public class DatabaseConfiguration{
    @JsonProperty(value = "jdbcUrl", required = true)
    @JsonPropertyDescription(value = "JDBC connection URL. (supported: mariadb, mysql, sqlite)")
    @NotNull
    private String jdbcUrl;
    @JsonProperty("username")
    @JsonPropertyDescription(value = "Database username.")
    @Nullable
    private String username;
    @JsonProperty("password")
    @JsonPropertyDescription(value = "Database password.")
    @Nullable
    @ToString.Exclude
    private String password;
    @JsonProperty("maxPoolSize")
    @JsonPropertyDescription(value = "Maximum number of connections to the database. Default: 10")
    @Builder.Default
    private int maxPoolSize = 10;
    @JsonProperty("connectionTimeout")
    @JsonPropertyDescription(value = "Connection timeout in milliseconds, time to wait for a connection in the pool to be available. Default: 30s")
    @Builder.Default
    private long connectionTimeout = SECONDS.toMillis(30);
    @JsonProperty("idleTimeout")
    @JsonPropertyDescription(value = "Idle timeout in milliseconds, time allowed for a connection to be idle in the pool. Default: 10m")
    @Builder.Default
    private long idleTimeout = MINUTES.toMillis(10);
    @JsonProperty("lifetimeTimeout")
    @JsonPropertyDescription(value = "Lifetime timeout in milliseconds, maximum time for a connection to exist in the pool. Default: 30m")
    @Builder.Default
    private long lifetimeTimeout = MINUTES.toMillis(30);
}
