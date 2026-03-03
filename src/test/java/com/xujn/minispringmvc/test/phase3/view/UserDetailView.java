package com.xujn.minispringmvc.test.phase3.view;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.view.View;

import java.util.Map;

@Component
public class UserDetailView implements View {

    @Override
    public void render(Map<String, Object> model, WebRequest request, WebResponse response) {
        Object name = model.getOrDefault("name", request.getParameter("name"));
        response.write("view:userDetail|name=" + name);
    }
}
