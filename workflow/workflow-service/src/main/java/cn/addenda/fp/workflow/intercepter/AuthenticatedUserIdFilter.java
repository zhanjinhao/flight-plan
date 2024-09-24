package cn.addenda.fp.workflow.intercepter;

import cn.addenda.component.user.UserContext;
import org.flowable.engine.IdentityService;

import javax.servlet.*;
import java.io.IOException;

/**
 * 用户信息传输过滤器
 */
public class AuthenticatedUserIdFilter implements Filter {

  private final IdentityService identityService;

  public AuthenticatedUserIdFilter(IdentityService identityService) {
    this.identityService = identityService;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    identityService.setAuthenticatedUserId(UserContext.getUserId());
    try {
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      identityService.setAuthenticatedUserId(null);
    }
  }
}
