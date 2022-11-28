package com.example.cacheproxynew.controller;


import com.example.cacheproxynew.entity.Element;
import com.example.cacheproxynew.exception.BadRequestElementException;
import com.example.cacheproxynew.exception.MethodNotAllowedElementException;
import com.example.cacheproxynew.service.ElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ElementController {

    private final ElementService elementService;

    @GetMapping("/{id}")
    public Element getElement(@PathVariable String id) {
        try {
            return elementService.getElement(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            throw new BadRequestElementException(" id не может быть значение " + id);
        }
    }

    @PostMapping("/")
    public Element addNewElement(@RequestBody Element element) {
        if ((element.getId()!=null && element.getId() < 1) || element.getVersion() < 1 || element.getValue() == null) {
            throw new MethodNotAllowedElementException("Invalid input");
        } else {
            return elementService.saveElement(element);
        }
    }
}

