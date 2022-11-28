package com.example.cacheproxynew.service;

import com.example.cacheproxynew.entity.Element;

public interface ElementService {
    Element saveElement(Element element);

    Element getElement(int id);
}
