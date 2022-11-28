package com.example.cacheproxynew.service;


import com.example.cacheproxynew.entity.Element;
import com.example.cacheproxynew.exception.NotFoundElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElementServiceImp implements ElementService {
    @Value("${ports}")
    private List<Integer> ports = new ArrayList<>();
    @Value("${basicPath}")
    private String basic;
    @Value("${apiUrl}")
    private String api;

    private final RestTemplate restTemplate;


    @Override
    public Element saveElement(Element element) {
        List<Integer> ids = new ArrayList<>();
        ports.forEach(port -> {
            try {
                ids.add(restTemplate.exchange(basic + port + api, HttpMethod.POST, new HttpEntity<>(element), Integer.class).getBody());
            } catch (Exception e) {
                System.out.println("сервис с портом " + port + " не доступен . Причина: " + e.getMessage());
                return;
            }
        });
        return getElement(ids.get(0));
    }
    Map<Element, Integer> result = new HashMap<>();
    Element element;
    @Override
    public Element getElement(int id) {
//        Map<Element, Integer> result = new HashMap<>();
        //проходимся по портам и извлекаем элементы и соттветствующие порты
        ports.forEach(port -> {
            try {
                ResponseEntity<Element> response = restTemplate.exchange(basic + port + api + "/" + id, HttpMethod.GET, HttpEntity.EMPTY, Element.class);
                if (response.hasBody()) {
                    result.put(response.getBody(), port);
                }
            } catch (Exception e) {
                System.out.println("сервис с портом " + port + " не доступен . Причина: " + e.getMessage());
                return;
            }
        });
        //прохоим стримом по ключам результата и делим их по версиям
        Map<Integer, List<Element>> collectVersion = result.keySet().stream().collect(Collectors.groupingBy(Element::getVersion, Collectors.toList()));
        //проходим стримом и определяем максимальную версию
        Integer maxVersion = collectVersion.keySet().stream().max(Integer::compare).orElseThrow(() -> new NotFoundElementException("Cache not found"));
        //присваеваем макс версию
        Element element = collectVersion.get(maxVersion).get(0);
        //если несколько версий
        if (collectVersion.keySet().size() > 1) {
            //извлеваем этот элемент с макс версией
            Integer currentPort = result.get(element);
            //присваеваем всем макисамльную версию по всем портам
            Executors.newSingleThreadExecutor().submit(() -> synchronizeElement(currentPort, element));
        }
        return element;
    }
//
//    @Scheduled(fixedDelay = 1000)
//    private void synchronizeElementScheduled() {
//        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        scheduledExecutorService.execute(() -> synchronizeElement(result.get(element), element));
//        System.out.println("начало");
//        scheduledExecutorService.scheduleAtFixedRate(() -> synchronizeElement(result.get(element), element), 1, 1, TimeUnit.SECONDS);
//    }

    private void synchronizeElement(Integer currentPort, Element element) {
        //присваеваем всем макисамльную версию по всем портам
        ports.stream().filter(port -> !port.equals(currentPort))
                .forEach(port ->
                {
                    try {
                        restTemplate.exchange(basic + port + api, HttpMethod.POST, new HttpEntity<>(element), Element.class);
                    } catch (Exception e) {
                        System.out.println("данные на порту  " + port + " изменены . Причина: " + e.getMessage());
                        return;
                    }
                });
    }

}
