package com.qrtx.areading.net.responses;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 17-3-9.
 */

public class Response {
    public static final int RESPONSE_OK = 0;

    public int responseCode;
    public String responseMsg;


    public static Response obtionBaseResponse(String jsonStr) {
        Response response = new Response();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            response.responseCode = jsonObject.getInt("responseCode");
            response.responseMsg = jsonObject.getString("responseMsg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public interface Obtionable<T extends Response> {
        T obtionResponse(String jsonStr);
    }
}
