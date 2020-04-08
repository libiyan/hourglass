package com.drgou.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: RequestOrderDTO
 * @Author: wendy
 * @Date: 2019/12/20
 */
@Data
public class RequestBaseDTO implements Serializable {

    private Integer pageIndex;
    private Integer pageSize;
    private String token;

}
