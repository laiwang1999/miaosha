package com.yang.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    /**
     * @param data 成功后返回的参数
     */
    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    /**
     * @param codeMsg 状态码
     */
    private Result(CodeMsg codeMsg) {
        if (codeMsg == null) return;
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    /**
     * @param <T> 泛型类型
     * @return 返回成功的结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     * @param <T> 需要返回的泛型类型
     * @return 返回失败的结果
     */
    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }

}
