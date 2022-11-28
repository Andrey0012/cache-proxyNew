package com.example.cacheproxynew.controller;

import com.example.cacheproxynew.entity.Element;
import com.example.cacheproxynew.exception.BadRequestElementException;
import com.example.cacheproxynew.exception.MethodNotAllowedElementException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElementControllerTest {
    @Autowired
    private ElementController elementController;


    @Test(expected = BadRequestElementException.class)
    public void getControllerElementTest () {
        elementController.getElement("dfg");
    }


    @Test(expected = MethodNotAllowedElementException.class)
    public void addNewElementTest () {
        Element element = Element.builder().id(-1).value("value").version(1).build();
        elementController.addNewElement(element);
    }
}
