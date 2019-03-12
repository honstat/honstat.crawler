package com.honstat.house.controller;
import com.honstat.house.utils.ErrorEntity;
import com.honstat.house.utils.HttpResponseBuild;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.controller
 * @Description: TODO
 * @date 2018/10/15 11:30
 */
@RestController
public class BaseController {
    @ExceptionHandler
    @ResponseBody
    public Object exp(HttpServletRequest request, Exception ex) {
         ex.printStackTrace();
        return new HttpResponseBuild().setData(false).setCode(ErrorEntity.define("-1",ex.getMessage()));
    }
    volatile static boolean isInited=false;
}
