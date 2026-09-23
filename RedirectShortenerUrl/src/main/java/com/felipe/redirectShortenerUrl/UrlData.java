package com.felipe.redirectShortenerUrl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UrlData {
    private String originUrl;
    private long expirationTime;
}
