package service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IpService{

    private final RestTemplate restTemplate;

    public IpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String traceIp(String ipAddress) {
        // Realiza la llamada a la API externa
        String url = "https://api.example.com/trace/" + ipAddress; // Cambia esto por tu API real
        // Obtiene la respuesta
        return restTemplate.getForObject(url, String.class);
    }
}
