package com.esprit.services;

import com.google.gson.Gson;
import okhttp3.*;

public class LibreTranslateService {
    private static final String API_URL = "https://libretranslate.com/translate";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();

    private static class TranslateRequest {
        String q;
        String source;
        String target;
        String format = "text";

        TranslateRequest(String q, String source, String target) {
            this.q = q;
            this.source = source;
            this.target = target;
        }
    }

    private static class TranslateResponse {
        String translatedText;
    }

    public String translate(String text, String sourceLang, String targetLang) throws Exception {
        TranslateRequest requestBody = new TranslateRequest(text, sourceLang, targetLang);
        String json = gson.toJson(requestBody);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Erreur API: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();
            TranslateResponse result = gson.fromJson(responseBody, TranslateResponse.class);
            return result.translatedText;
        }
    }
}