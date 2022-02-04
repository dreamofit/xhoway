package cn.xihoway.common;



import cn.ihoway.api.record.RecordAsm;
import cn.ihoway.api.security.TokenAsm;
import cn.xihoway.annotation.Processor;
import cn.xihoway.common.io.CommonInput;
import cn.xihoway.common.io.CommonOutput;
import cn.xihoway.container.HowayContainer;
import cn.xihoway.type.AuthorityLevel;
import cn.xihoway.type.StatusCode;
import cn.xihoway.util.HowayEncrypt;
import cn.xihoway.util.HowayLog;
import cn.xihoway.util.HowayResult;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * 程序处理器公共类，所有逻辑处理器必须继承该类
 * @param <I>
 * @param <O>
 */
public abstract class CommonProcessor<I extends CommonInput,O extends CommonOutput> {

    private final HowayLog logger = new HowayLog(CommonProcessor.class);

    /**
     * 数据检查
     * @param input input
     * @return HowayResult
     */
    protected StatusCode dataCheck(I input){
        return StatusCode.SUCCESS;
    }

    /**
     * 安全认证
     * @param input input
     * @return HowayResult
     */
    protected HowayResult certification(I input,O output, AuthorityLevel limitAuthority) {
        HashMap<String, Object> res = getIsTokenRule(input, limitAuthority);
        if((Integer)res.get("code") == 200){
            return HowayResult.createSuccessResult(output);
        }else {
            logger.error("token:" + input.token + "失败：" + res.get("msg"));
            return HowayResult.createFailResult(StatusCode.TOKENERROR,res.get("msg").toString(),output);
        }

    }

    protected HashMap<String, Object> getIsTokenRule(I input, AuthorityLevel limitAuthority) {
        TokenAsm tokenAsm = (TokenAsm) HowayContainer.getContext().getBean("TokenAsm");
        HashMap<String,Object> res = tokenAsm.isTokenRule(input.token, limitAuthority.getLevel());
        return res;
    }

    /**
     * 程序执行之前
     * @param input
     * @param output
     * @return
     */
    protected HowayResult beforeProcess(I input, O output){
        return HowayResult.createSuccessResult(output);
    }

    /**
     * 程序主要执行部分
     * @param input input
     * @param output output
     * @return HowayResult
     */
    protected abstract HowayResult process(I input,O output);

    /**
     * 程序执行之后
     * @param input input
     * @param output output
     * @return HowayResult
     */
    protected HowayResult afterProcess(I input, O output){
        return HowayResult.createSuccessResult(output);
    }

    public HowayResult doExecute(I input, O output){
        if(StringUtils.isNotBlank(input.traceId)){
            MDC.put("traceId",input.traceId);
        }

        if(StringUtils.isBlank(input.eventNo)){
            logger.info("事件编号不能为空!");
            return HowayResult.createFailResult(StatusCode.FIELDMISSING,"事件编号不能为空!",output);
        }
        HashMap<String,String> addInput = new HashMap<>();
        HowayResult response = insertRecord(input,output,addInput); //记录input日志
        if(response.getStatusCode() == StatusCode.DUPLICATREQUEST){
            return response;
        }
        response = getResponse(input,output);
        updateRecord(input,response,addInput); //记录output日志
        return response;
    }

    private HowayResult getResponse(I input,O output){
        try{
            Processor annotation = this.getClass().getAnnotation(Processor.class);
            logger.info("begin --> input: "+ JSON.toJSONString(input));
            StatusCode sc = dataCheck(input);
            if( sc.getCode() != StatusCode.SUCCESS.getCode() ){
                return HowayResult.createFailResult(sc,"数据检查失败",output);
            }
            if(annotation.certification()){
                HowayResult cerRes = certification(input,output,annotation.limitAuthority());
                if(!cerRes.isOk()){
                    return cerRes;
                }
            }
            HowayResult response = beforeProcess(input,output);
            if(!response.isOk()){
                return response;
            }
            response = process(input,output);
            if(!response.isOk()){
                return response;
            }
            response = afterProcess(input,output);
            if(!response.isOk()){
                return response;
            }
            logger.info("end --> response: "+ JSON.toJSONString(response));
            return response;
        }catch (Exception e){
            logger.error("[error] processor has an exception :"+Arrays.toString(e.getStackTrace()));
            return HowayResult.createFailResult(StatusCode.JAVAEXCEPTION,output);
        }
    }

    /**
     * 写日志 ———— input情况
     * @param input
     * @param output
     * @return
     */
    private HowayResult insertRecord(I input,O output,HashMap<String,String> addInput){
        try{
            RecordAsm recordAsm = (RecordAsm) HowayContainer.getContext().getBean("RecordAsm");
            HashMap<String,Object> res = recordAsm.findByEventNo(input.eventNo);
            if(res != null){
                logger.info("请求重复！eventNo:" + input.eventNo);
                String resOutput = (String) res.get("output");
                return HowayResult.createFailResult(StatusCode.DUPLICATREQUEST,"请求重复!",StringUtils.isBlank(resOutput)?output:JSON.parse(resOutput));
            }
            addInput.put("eventNo",input.eventNo);
            addInput.put("input",JSON.toJSONString(input));
            addInput.put("inputToken",input.token);
            long timeStamp = System.currentTimeMillis();
            Date date = new Date(timeStamp);
            addInput.put("inputTime",getCurrentTime(date));
            addInput.put("inputTimestamp",String.valueOf(timeStamp));
            addInput.put("sysName","forum");
            addInput.put("ip",input.ip);
            addInput.put("method",input.method);
            addInput.put("traceId", (String) MDC.get("traceId"));

        }catch (Exception e){
            logger.error("[warning] input日志写入失败，cause by :" + e.getCause());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        return HowayResult.createSuccessResult(output);
    }

    private void updateRecord(I input,HowayResult response,HashMap<String,String> addInput){
        try {
            RecordAsm recordAsm = (RecordAsm) HowayContainer.getContext().getBean("RecordAsm");
            addInput.put("eventNo",input.eventNo);
            addInput.put("output",JSON.toJSONString(response));
            long timeStamp = System.currentTimeMillis();
            Date date = new Date(timeStamp);
            addInput.put("outputTime",getCurrentTime(date));
            addInput.put("outputTimestamp",String.valueOf(timeStamp));
            addInput.put("responseCode",String.valueOf(response.getStatusCode().getCode()));
            recordAsm.addRecord(addInput);
        }catch (Exception e){
            logger.error("[warning] output日志写入失败，cause by :" + e.getCause());
            logger.error(Arrays.toString(e.getStackTrace()));
        }

    }

    protected String getCurrentTime(Date date){
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    protected HashMap<String, Object> getUserByToken(String token){
        TokenAsm tokenAsm = (TokenAsm) HowayContainer.getContext().getBean("TokenAsm");
        return tokenAsm.getUserByToken(token);
    }

    /**
     * 获取事件编号
     * @return eventNo
     */
    protected String getEventNo(){
        return HowayEncrypt.encrypt(UUID.randomUUID().toString(),"MD5",12);
    }
}

