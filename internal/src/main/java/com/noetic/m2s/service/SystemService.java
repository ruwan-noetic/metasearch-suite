package com.noetic.m2s.service;

import com.noetic.dto.GenericListDTO;
import com.noetic.dto.SystemDTO;
import com.noetic.exception.SystemNotFoundException;
import com.noetic.exception.TransformerException;
import com.noetic.m2s.domain.internal.System;


public interface SystemService {
    
    public void addSystem(System system);

    public void deleteSystem(int urn);

    public SystemDTO getSystem(int urn) throws TransformerException, SystemNotFoundException;

    public GenericListDTO getAllSystems(int page, int size) throws TransformerException;

}
