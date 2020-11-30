package com.example.manufacturer;

import com.github.Pawedla.OrderReport;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

public class Main {

    private static RestTemplate restTemplate;

    public static void main(String[] args) throws HttpClientErrorException {


        //Setting timeout for restTemplate
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(10000);
        restTemplate = new RestTemplate(factory);

        final Scanner in = new Scanner(System.in);
        String[] selection = new String[5];
        String url = "http://localhost:8080/";
        ResponseEntity<List> responseEntity;


        //Loop for Ordering
        //Each step is in a loop itself, HttpClientErrorException gets thrown if selection is invalid
        while (true) {
            try {
                //Getting HandleBarTypes and selecting
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
                //Getting Material and selecting
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
                //Getting GearShift and selecting
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
                //Getting HandleMaterial and selecting
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
                //Setting name
                System.out.println();
                System.out.println("Please type in your name:");
                System.out.println("input> ");
                selection[4] = in.nextLine();

                //Posting order
                try {
                    OrderReport orderReport = restTemplate.postForObject(url + "order", selection, OrderReport.class);
                    System.out.println();
                    orderReport.printOrder();
                } catch (HttpClientErrorException e) {
                    System.out.println(e.getMessage());
                }
            } catch (ResourceAccessException e) {
                System.out.println("Request took longer than 10 seconds: " + e.getMessage());
            } catch (HttpServerErrorException e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }

            String continueOrdering = "";
            while (!continueOrdering.equals("y") && !continueOrdering.equals("n")) {
                System.out.println("Do you want to make another order ? (y/n)");
                continueOrdering = in.nextLine();
            }
            if (continueOrdering.equals("n")) break;
        }
    }

    static public void printDescription(String property, ResponseEntity<List> responseEntity) {
        System.out.println();
        System.out.println("Please select " + property);
        responseEntity.getBody().forEach(System.out::println);
        System.out.println("input> ");
    }
}
