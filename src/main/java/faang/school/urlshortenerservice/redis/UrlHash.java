package faang.school.urlshortenerservice.redis;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash(value = "url", timeToLive = 86400)
@Builder
public class UrlHash implements Serializable {
    @Id
    @NotNull
    private String hash;
    @NotNull
    @URL
    private String url;
}