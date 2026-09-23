package com.felipe.createUrlShortened;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
public class UrlData {
    private String originUrl;
    private long expirationTime;
}
