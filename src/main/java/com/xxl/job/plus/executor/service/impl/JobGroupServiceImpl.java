package com.xxl.job.plus.executor.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.plus.executor.model.XxlJobGroup;
import com.xxl.job.plus.executor.service.JobGroupService;
import com.xxl.job.plus.executor.service.JobLoginService;
import com.xxl.job.plus.executor.service.XxlJobProperties;
import groovy.util.logging.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : Hydra
 * @date: 2022/9/19 17:34
 * @version: 1.0
 */
@Service
public class JobGroupServiceImpl implements JobGroupService {



    @Autowired
    private XxlJobProperties xxlJobProperties;



    @Autowired
    private JobLoginService jobLoginService;

    @Override
    public List<XxlJobGroup> getJobGroup() {
        String url=xxlJobProperties.getAdminAddresses()+"/jobgroup/pageList";
        HttpResponse response = HttpRequest.post(url)
                .form("appname", xxlJobProperties.getAppname())
                .form("title", StringUtils.hasText(xxlJobProperties.getTitle())?
                        xxlJobProperties.getTitle():xxlJobProperties.getAppname())
                .cookie(jobLoginService.getCookie())
                .execute();

        String body = response.body();
        JSONArray array = JSONUtil.parse(body).getByPath("data", JSONArray.class);
        List<XxlJobGroup> list = array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobGroup.class))
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public boolean autoRegisterGroup() {
        String url=xxlJobProperties.getAdminAddresses()+"/jobgroup/save";
        HttpRequest httpRequest = HttpRequest.post(url)
                .form("appname",  xxlJobProperties.getAppname())
                .form("title", StringUtils.hasText(xxlJobProperties.getTitle())?
                        xxlJobProperties.getTitle():xxlJobProperties.getAppname());

        httpRequest.form("addressType",xxlJobProperties.getAddressType());
        if (xxlJobProperties.getAddressType().equals(1)){
            if (Strings.isBlank(xxlJobProperties.getAddressList())){
                throw new RuntimeException("手动录入模式下,执行器地址列表不能为空");
            }
            httpRequest.form("addressList",xxlJobProperties.getAddressList());
        }

        HttpResponse response = httpRequest.cookie(jobLoginService.getCookie())
                .execute();
        JSON respBody = JSONUtil.parse(response.body());
        Object code = respBody.getByPath("code");
        boolean result =  code.equals(200);
        if (!result){
            throw new RuntimeException("自动注册失败 result="+respBody.toString());
        }

        return result;
    }

    @Override
    public boolean preciselyCheck() {
        List<XxlJobGroup> jobGroup = getJobGroup();
        Optional<XxlJobGroup> has = jobGroup.stream()
                .filter(xxlJobGroup -> xxlJobGroup.getAppname().equals(xxlJobProperties.getAppname())
                        && xxlJobGroup.getTitle().equals(StringUtils.hasText(xxlJobProperties.getTitle())?
                        xxlJobProperties.getTitle():xxlJobProperties.getAppname()))
                .findAny();
        return has.isPresent();
    }

}
