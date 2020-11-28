import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

public class Main {

    private static RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) throws HttpClientErrorException {
        final Scanner in = new Scanner(System.in);
        String[] selection = new String[4];
        String url = "http://localhost:8080/";
        ResponseEntity<List> responseEntity;

        responseEntity = restTemplate.getForEntity(URI.create(url + "getAvailableHandlebarTypes"), List.class);
        while (true) {
            printDescription("HandlebarType", responseEntity);
            selection[0] = in.nextLine();
            try {
                responseEntity = restTemplate.getForEntity(URI.create(url + "getAvailableMaterial?handleBarType=" + selection[0]), List.class);
                break;
            } catch (HttpClientErrorException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            printDescription("Material", responseEntity);
            selection[1] = in.nextLine();
            try {
                restTemplate.getForEntity(URI.create(url + "getHandleBarTypeMaterialValidation" + "?handleBarType=" + selection[0] + "&material=" + selection[1]), ResponseEntity.class);
                responseEntity = restTemplate.getForEntity(URI.create(url + "getAvailableGearshifts?material=" + selection[1]), List.class);
                break;
            } catch (HttpClientErrorException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            printDescription("GearShift", responseEntity);
            selection[2] = in.nextLine();
            try {
                restTemplate.getForEntity(URI.create(url + "getMaterialGearshiftValidation" + "?material=" + selection[1] + "&gearShift=" + selection[2]), ResponseEntity.class);
                responseEntity = restTemplate.getForEntity(URI.create(url + "getAvailableHandleMaterial?handleBarType=" + selection[0] + "&material=" + selection[1]), List.class);
                break;
            } catch (HttpClientErrorException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            printDescription("HandleMaterial", responseEntity);
            selection[3] = in.nextLine();
            try {
                restTemplate.getForEntity(URI.create(url + "getHandleMaterialValidation" + "?handleBarType=" + selection[0] + "&material=" + selection[1] + "&handleMaterial=" + selection[3]), ResponseEntity.class);
                break;
            } catch (HttpClientErrorException e) {
                System.out.println(e.getMessage());
            }
        }
        restTemplate.postForObject(url + "order", selection, String[].class);
    }

    static public void printDescription(String property, ResponseEntity<List> responseEntity) {
        System.out.println("Please select " + property);
        responseEntity.getBody().forEach(System.out::println);
        System.out.println("input> ");
    }
}
