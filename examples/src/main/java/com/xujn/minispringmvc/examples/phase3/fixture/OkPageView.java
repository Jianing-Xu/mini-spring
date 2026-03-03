package com.xujn.minispringmvc.examples.phase3.fixture;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.view.View;

import java.util.Map;

@Component
public class OkPageView implements View {

    @Override
    public void render(Map<String, Object> model, WebRequest request, WebResponse response) {
        response.write("view:ok");
    }
}
