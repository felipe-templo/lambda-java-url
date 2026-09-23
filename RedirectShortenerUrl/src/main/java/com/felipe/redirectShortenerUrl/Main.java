package com.felipe.redirectShortenerUrl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Main implements RequestHandler<Map<String,Object>,Map<String,Object>> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final S3Client s3Client = S3Client.builder().build();
    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        String pathParameters = (String) input.get("rawPath");
        String shortUrlCode = pathParameters.replace("/","");

        if (shortUrlCode == null || shortUrlCode.isEmpty()){
            throw new IllegalArgumentException("Invalid input : shorteUrlCode is required");
        }
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket("url-shortener-storage-lima2208")
                .key(shortUrlCode + ".json")
                .build();

        InputStream s3InputStream;

        try {
          s3InputStream = s3Client.getObject(request);

        }
        catch (Exception e){
            throw new RuntimeException("Error fetching data S3 " + e.getMessage(),e);
        }
        UrlData urlData;
        try {
           urlData = objectMapper.readValue(s3InputStream,UrlData.class);
        }
        catch (Exception e){
            throw new RuntimeException("Error deserializin  UrlData " + e.getMessage(),e);
        }

        long currentTimeSecond = System.currentTimeMillis() / 1000;
        if(currentTimeSecond < urlData.getExpirationTime()){
            Map<String,Object> response = new HashMap<>();
            response.put("statusCode",302);
            Map<String,String> headers = new HashMap<>();
            headers.put("Location",urlData.getOriginUrl());
            response.put("headers",headers);

            return response;
        }
        Map<String,Object> response = new HashMap<>();
        response.put("statusCode",410);
        response.put("body","this URL expirated");

        return response;
    }
}