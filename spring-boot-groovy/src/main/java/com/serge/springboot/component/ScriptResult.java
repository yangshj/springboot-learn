package com.serge.springboot.component;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.StringUtils;

/**
 * Description:
 *
 * @author : 杨帅军 (shuaijun.yang@ucarinc.com)
 * @since : 2020/8/24 18:31:52
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScriptResult {

    private String[] output;
    private Object result;

    private ScriptResult() {
    }

    public String[] getOutput() {
        return output;
    }

    public Object getResult() {
        return result;
    }

    public static ScriptResult create(Throwable throwable) {
        String message = throwable.getMessage() == null ? throwable.getClass().getName() : throwable.getMessage();
        return create(null, message);
    }

    public static ScriptResult create(Object result, String output) {
        ScriptResult scriptletResult = new ScriptResult();
        scriptletResult.result = result;
        if (StringUtils.hasLength(output)) {
            scriptletResult.output = output.split(System.lineSeparator());
        }
        return scriptletResult;
    }
}
