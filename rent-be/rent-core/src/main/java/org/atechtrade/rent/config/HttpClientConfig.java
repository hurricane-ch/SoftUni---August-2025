package org.atechtrade.rent.config;

import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.util.InsecureTrustManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;

@Slf4j
@Configuration
public class HttpClientConfig {

    @Bean(name = "insecureHttpClient")
    public HttpClient insecureHttpClient() throws Exception {
        return HttpClient.newBuilder()
                .sslContext(getInsecureSSLContext())
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static SSLContext getInsecureSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new InsecureTrustManager()
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return sslContext;
    }

    public static SSLContext sslContext(final KeyManagerFactory keyManagerFactory,
                                        final TrustManagerFactory trustManagerFactory) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            return sslContext;
        } catch (Exception e) {
            log.error("An error has occurred: ", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static KeyManagerFactory getKeyStore(final String path, final String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(new FileInputStream(keystoreContent), keyStorePassword.toCharArray());
            keyStore.load(HttpClientConfig.class.getResourceAsStream(path), password.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, password.toCharArray());

            return kmf;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static TrustManagerFactory getTrustStore(final String path, final String password) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(new FileInputStream(truststoreContent), trustStorePassword.toCharArray());
            trustStore.load(HttpClientConfig.class.getResourceAsStream(path), password.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            return tmf;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

//    @Bean
//    WebClient webClient(
//            @Value("${rent.e-payment.keystore}") String keystore,
//            @Value("${rent.e-payment.keystore-password}") String keyStorePassword,
//            @Value("${rent.e-payment.truststore}") String truststore,
//            @Value("${rent.e-payment.truststore-password}") String trustStorePassword,
//            @Value("${rent.e-payment.base-url}") String baseUrl
//    ) {
//        ClientHttpConnector httpConnector = new ReactorClientHttpConnector(
//                HttpClient.create().secure(t -> t.sslContext(
//                        sslContext(getKeyStore(keystore, keyStorePassword), getTrustStore(truststore, trustStorePassword))
//                ))
//        );
//        return WebClient.builder()
//                .clientConnector(httpConnector)
//                .baseUrl(baseUrl)
//                .filter(logRequest())
//                .filter(logResponse())
//                .build();
//    }

//    private static ExchangeFilterFunction logRequest() {
//        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
//            if (log.isDebugEnabled()) {
//                StringBuilder sb = new StringBuilder(">>> Log Request: \n")
//                        .append(clientRequest.method())
//                        .append(" ")
//                        .append(clientRequest.url());
//                clientRequest
//                        .headers()
//                        .forEach((name, values) -> values.forEach(value -> sb
//                                .append("\n")
//                                .append(name)
//                                .append(":")
//                                .append(value)));
//                log.debug(sb.toString());
//            }
//            return Mono.just(clientRequest);
//        });
//    }

//    private static ExchangeFilterFunction logResponse() {
//        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
//            if (log.isDebugEnabled()) {
//                StringBuilder sb = new StringBuilder(">>> Log Response: \n")
//                        .append("Status: ")
//                        .append(clientResponse.rawStatusCode());
//                clientResponse
//                        .headers()
//                        .asHttpHeaders()
//                        .forEach((key, value1) -> value1.forEach(value -> sb
//                                .append("\n")
//                                .append(key)
//                                .append(":")
//                                .append(value)));
//                log.debug(sb.toString());
//            }
//            return Mono.just(clientResponse);
//        });
//    }
}
