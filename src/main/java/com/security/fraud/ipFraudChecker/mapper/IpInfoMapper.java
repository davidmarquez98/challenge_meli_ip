package com.security.fraud.ipFraudChecker.mapper;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import org.json.JSONObject;

public interface IpInfoMapper {
    void fromJsonToEntity(JSONObject jsonObject, IpInfoEntity ipInfoEntity);
}