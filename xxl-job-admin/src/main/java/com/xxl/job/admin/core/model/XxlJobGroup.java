package com.xxl.job.admin.core.model;

import com.xxl.job.admin.core.util.SpringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xuxueli on 16/9/30.
 */
public class XxlJobGroup {

    private int id;
    private String appname;
    private String title;
    private int addressType;        // 执行器地址类型：0=自动注册、1=手动录入
    private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)
    private Date updateTime;

    // registry list
    private List<String> registryList;  // 执行器地址列表(系统注册)
    public List<String> getRegistryList() {
        if (addressList!=null && !addressList.trim().isEmpty()) {
            //address执行器管理填入的机器地址 http://ip:port这里需要兼容 lb://serviceName地址
            if (addressList.contains("lb://")) {
                addressList = Arrays.stream(addressList.split(","))
                        .filter(Objects::nonNull)
                        .filter(s -> !s.trim().isEmpty())
                        .map(this::obtainUrl)
                        .flatMap(Collection::stream)
                        .collect(Collectors.joining(","));
            }
            registryList = new ArrayList<>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

    public List<String> obtainUrl(String address){
        if (address.startsWith("lb://")) {
            String serviceName = address.replace("lb://", "");
            DiscoveryClient discoveryClient = SpringUtils.getBean(DiscoveryClient.class);
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            return instances.stream()
                    .map(i -> i.getUri().toString())
                    .collect(Collectors.toList());
        }
        return List.of(address);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

}
