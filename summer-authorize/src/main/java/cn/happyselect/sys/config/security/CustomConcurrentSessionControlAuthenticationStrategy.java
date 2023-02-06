package cn.happyselect.sys.config.security;

import cn.happyselect.sys.bean.ResponseCode;
import cn.happyselect.sys.bean.dto.WsMessageDto;
import cn.happyselect.sys.constant.MsgTypeConstant;
import cn.happyselect.sys.service.MessageBizService;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import java.util.Comparator;
import java.util.List;

/**
 * 自定义session并发控制
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-20
 */
public class CustomConcurrentSessionControlAuthenticationStrategy extends ConcurrentSessionControlAuthenticationStrategy {

    /**
     * @param sessionRegistry the session registry which should be updated when the
     *                        authenticated session is changed.
     */
    public CustomConcurrentSessionControlAuthenticationStrategy(SessionRegistry sessionRegistry) {
        super(sessionRegistry);
    }

    @Override
    protected void allowableSessionsExceeded(List<SessionInformation> sessions, int allowableSessions, SessionRegistry registry) throws SessionAuthenticationException {
        super.allowableSessionsExceeded(sessions, allowableSessions, registry);

        // Determine least recently used sessions, and mark them for invalidation
        sessions.sort(Comparator.comparing(SessionInformation::getLastRequest));
        int maximumSessionsExceededBy = sessions.size() - allowableSessions + 1;
        List<SessionInformation> sessionsToBeExpired = sessions.subList(0, maximumSessionsExceededBy);
        for (SessionInformation session : sessionsToBeExpired) {
            WsMessageDto dto = new WsMessageDto();
            dto.setToHttpSessionId(session.getSessionId());
            dto.setTitle("账号退出");
            dto.setContent(ResponseCode.EXPIRED_LOGIN_CN);
            dto.setMsgType(MsgTypeConstant.EVENT);
            dto.setEvent(ResponseCode.EXPIRED_LOGIN);
            MessageBizService.sendToUser(dto);
        }
    }
}
