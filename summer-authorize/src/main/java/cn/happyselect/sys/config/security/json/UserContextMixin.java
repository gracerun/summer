package cn.happyselect.sys.config.security.json;

import com.fasterxml.jackson.annotation.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class UserContextMixin {

    @JsonCreator
    public UserContextMixin(@JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
    }
}
