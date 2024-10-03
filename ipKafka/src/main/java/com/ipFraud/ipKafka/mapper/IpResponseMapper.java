package com.ipFraud.ipKafka.mapper;

import com.ipFraud.ipKafka.response.IpInfoResponse;
import org.json.JSONObject;

public interface IpResponseMapper {

    void fromJsonToResponse(JSONObject jsonObject, IpInfoResponse ipInfoResponse);

}
