package txl.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import txl.config.TxlConstants;
import txl.log.TxLogger;

/**
 * @ClassName:  HttpClientUtil.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-9 下午2:27:11
 */
public class HttpClientUtil
{
    private final static TxLogger  log = new TxLogger(HttpClientUtil.class, TxlConstants.MODULE_ID_BASE);
    /*缓存DefaultHttpClient */
    private static DefaultHttpClient client;
    
    /**
     * 
     * @param path
     * @param params
     * @param enc
     * @return
     * @throws ConnectTimeoutException  超时异常抛出，由调用者处理。
     * @throws HttpHostConnectException 
     */
    public static String httpPost(String path, Map<String, String> params, String enc) throws ConnectTimeoutException, HttpHostConnectException {
        if(path==null||path.length()==0)
            return "";
        String body = "";
        List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
        //HS_TODO: 测试http发送参数
        StringBuilder sb = new StringBuilder();
        if(params!=null && !params.isEmpty()){
            for(Map.Entry<String, String> entry : params.entrySet()){
                paramPairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                sb.append(entry.getKey()+":"+entry.getValue());
            }
        }
        log.info("request: "+path+"...params-> "+sb.toString());
        UrlEncodedFormEntity entitydata;
        try
        {
            entitydata = new UrlEncodedFormEntity(paramPairs, enc);
            HttpPost post = new HttpPost(path);  
            post.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, TxlConstants.HTTP_CONNECTION_TIMEOUT);
            post.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT,TxlConstants.HTTP_SO_TIMEOUT);
            post.setEntity(entitydata);
            if(client==null){
                client = new DefaultHttpClient() ;
            }
            HttpResponse response = client.execute(post);
            
            if(response.getStatusLine().getStatusCode()==200){
                HttpEntity entity = response.getEntity();
                body = EntityUtils.toString(entity, enc);    
            }
            
        } 
        catch(ConnectTimeoutException e){
            throw e;
        }
        catch(HttpHostConnectException e){
        	throw e;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }catch (ClientProtocolException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }finally{
            //client.getConnectionManager().shutdown();
        }
        log.info("body : "+body);
        return body;
    }
    
    public static String httpPostUTF8(String path, Map<String, String> params)throws ConnectTimeoutException, HttpHostConnectException{
        return httpPost(path, params, "utf-8");
    } 
}
