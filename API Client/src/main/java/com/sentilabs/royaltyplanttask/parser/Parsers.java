package com.sentilabs.royaltyplanttask.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentilabs.royaltyplanttask.response.AccountDetailResponse;
import com.sentilabs.royaltyplanttask.response.DocumentDetailResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by sentipy on 11/07/15.
 */
public class Parsers {

    public static AccountDetailResponse parseAccountDetailResponse(InputStream inputStream)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        AccountDetailResponse accountDetailResponse = objectMapper.readValue(inputStream
                , AccountDetailResponse.class);
        return accountDetailResponse;
    }

    public static List<AccountDetailResponse> parseAccountDetailResponseList(InputStream inputStream)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccountDetailResponse> accountDetailResponseList = objectMapper.readValue(inputStream
                , objectMapper.getTypeFactory().constructCollectionType(List.class, AccountDetailResponse.class));
        return accountDetailResponseList;
    }

    public static DocumentDetailResponse parseDocumentDetailResponse(InputStream inputStream)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DocumentDetailResponse documentDetailResponse = objectMapper.readValue(inputStream
                , DocumentDetailResponse.class);
        return documentDetailResponse;
    }

    public static List<DocumentDetailResponse> parseDocumentDetailResponseList(InputStream inputStream)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<DocumentDetailResponse> documentDetailResponseList = objectMapper.readValue(inputStream
                , objectMapper.getTypeFactory().constructCollectionType(List.class, DocumentDetailResponse.class));
        return documentDetailResponseList;
    }
}
