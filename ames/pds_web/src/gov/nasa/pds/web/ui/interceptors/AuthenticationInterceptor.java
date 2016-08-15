package gov.nasa.pds.web.ui.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class AuthenticationInterceptor implements Interceptor {

    private static final long serialVersionUID = 1L;

	@Override
    public void destroy() {
        // TODO Auto-generated method stub
        
    }

	@Override
    public void init() {
        // TODO Auto-generated method stub
        
    }

	@Override
    public String intercept(ActionInvocation invocation) throws Exception {
        // TODO Auto-generated method stub
        return invocation.invoke();
    }

}