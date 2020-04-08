package com.drgou.service;

import com.drgou.bean.PackPage;
import com.drgou.exception.RestRequestException;
import com.drgou.utils.JsonResult;
import com.drgou.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * API请求服务类
 *
 * @author CHH
 * @create 2017-11-24
 **/
public class RestRequestService {

    //----------------------------------------------------

    /**
     * 通用Service断路器抛异常方法
     * <br/>其他服务挂了调不通时使用
     *
     * @param e
     * @return
     */
    public void commonFallbackException(Throwable e) {
        //此处可添加记录或处理Throwable异常的代码
        throw new RuntimeException("哎呀呀，当前服务发生异常，正在恢复中...");
    }

    //----------------------------------------------------

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate restApiTemplate;

    /**
     * REST GET 请求
     *
     * @param url
     * @return
     */
    public JsonResult restGetRequest(String url, Map<String, String> params)
            throws RestRequestException {
        if (params != null) {
            url = this.getFullUrl(url, params);
        }
        JsonResult result = null;
        try {
            result = restTemplate.getForEntity(url, JsonResult.class).getBody();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RestRequestException();
        }
        return result;
    }

    public JsonResult restApiGetRequest(String url, Map<String, String> params)
            throws RestRequestException {
        if (params != null) {
            url = this.getFullUrl(url, params);
        }
        JsonResult result = null;
        try {
            result = restApiTemplate.getForEntity(url, JsonResult.class).getBody();
        } catch (Exception ex) {
            throw new RestRequestException();
        }
        return result;
    }


    public <T> T restApiGetRequest(String url, Map<String, String> params, Class<T> classz)
            throws RestRequestException {
        if (params != null) {
            url = this.getFullUrl(url, params);
        }
        T result = null;
        try {
            result = restApiTemplate.getForEntity(url, classz).getBody();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RestRequestException();
        }
        return result;
    }


    /**
     * REST GET 请求
     *
     * @param url
     * @param params
     * @param isPackPage 是否将返回结果中的Spring的Page对象（首先要确保有此对象）封装成简洁的PackPage对象
     *                   解决Spring的Page对象不能转换成正确的Json字符串的问题
     * @return
     * @throws RestRequestException
     */
    public JsonResult restGetRequest(String url, Map<String, String> params, boolean isPackPage)
            throws RestRequestException {
        JsonResult result = this.restGetRequest(url, params);
        if (isPackPage) {
            PackPage newPage = this.packPage(result.getData());
            result.setData(newPage);
        }
        return result;
    }

    /**
     * REST GET 请求
     *
     * @param url
     * @param params
     * @param isPackPage 是否将返回结果中的Spring的Page对象（首先要确保有此对象）封装成简洁的PackPage对象
     *                   解决Spring的Page对象不能转换成正确的Json字符串的问题
     * @return
     * @throws RestRequestException
     */
    public JsonResult restApiGetRequest(String url, Map<String, String> params, boolean isPackPage)
            throws RestRequestException {
        JsonResult result = this.restApiGetRequest(url, params);
        if (isPackPage) {
            PackPage newPage = this.packPage(result.getData());
            result.setData(newPage);
        }
        return result;
    }

    /**
     * REST POST 请求
     *
     * @param url
     * @param params
     * @return
     */
    public JsonResult restPostRequest(String url, Map<String, String> params)
            throws RestRequestException {
        MultiValueMap<String, String> postData = (params != null ? this.getPostData(params) : null);
        JsonResult result = null;
        try {
            result = restTemplate.postForObject(url, postData, JsonResult.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RestRequestException();
        }
        return result;
    }

    public JsonResult restApiPostRequest(String url, Map<String, String> params)
            throws RestRequestException {
        MultiValueMap<String, String> postData = (params != null ? this.getPostData(params) : null);
        JsonResult result = null;
        try {
            result = restApiTemplate.postForObject(url, postData, JsonResult.class);
        } catch (Exception ex) {
            throw new RestRequestException();
        }
        return result;
    }

    public <T> T restApiPostJsonRequest(String url, String jsonParam, Class<T> classes) throws RestRequestException {
        HttpHeaders headers = new HttpHeaders();

//        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(jsonParam, headers);
        try {
            return restApiTemplate.postForObject(url, entity, classes);
        } catch (RestClientException e) {
//            e.printStackTrace();
            System.out.println("调用接口失败：" + e.getMessage());
            throw new RestRequestException();
        }
    }

    public <T> T restApiPostRequest(String url, Map<String, String> params, Class<T> classes)
            throws RestRequestException {
        MultiValueMap<String, String> postData = (params != null ? this.getPostData(params) : null);
        T result = null;
        try {
            result = restApiTemplate.postForObject(url, postData, classes);
        } catch (Exception ex) {
            System.out.println("调用接口失败：" + ex.getMessage());
            throw new RestRequestException();
        }
        return result;
    }

    //------------------------------------------------------------------

    /**
     * 将传入的Map参数集合解析为POST请求需要的MultiValueMap类型参数集合
     *
     * @param params
     * @return
     */
    private MultiValueMap<String, String> getPostData(Map<String, String> params) {
        MultiValueMap<String, String> reqEntity = new LinkedMultiValueMap<String, String>();
        Set<Map.Entry<String, String>> set = params.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            reqEntity.add(entry.getKey(), entry.getValue());
        }
        return reqEntity;
    }

    /**
     * 将传入的Map参数集合解析并追加到GET请求的URL后面
     *
     * @param url
     * @param params
     * @return
     */
    private String getFullUrl(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(url);
        Set<Map.Entry<String, String>> set = params.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        int i = 0;
        while (it.hasNext()) {
            sb.append(i > 0 ? "&" : "?");
            Map.Entry<String, String> entry = it.next();
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
            i++;
        }
        return sb.toString();
    }

    /**
     * 将返回结果中的原Spring的分页Page对象转换成新的分页PackPage对象
     *
     * @param originalPage
     * @return
     */
    private PackPage packPage(Object originalPage) {
        PackPage newPage = null;
        if (originalPage instanceof Map) {
            try {
                newPage = (PackPage) MapUtils.mapToObject((Map) originalPage, PackPage.class);
//                newPage.setPageIndex(newPage.getNumber());
//                newPage.setPageSize(newPage.getSize());
//                newPage.setTotalPage(newPage.getTotalPages());
//                newPage.setTotalCount(newPage.getTotalElements());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newPage;
    }
}
