package com.noetic.m2s.resource;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.noetic.common.v1.dto.ResponseDTO;
import com.noetic.dto.AccountDTO;
import com.noetic.dto.RunTimeConfigDTO;
import com.noetic.dto.SystemDTO;

/**
 * Created by hurman on 29/06/2017.
 */
@RequestMapping(value = "/internal",
        produces = "application/xml; charset=UTF-8",
        consumes = "application/xml")
public interface MetaSearchResource {


    
    /*CRUD Operations*/
    //add account
    @RequestMapping(produces = "application/json", consumes = "application/json",
            value = "/m2s/addAccount", method = RequestMethod.POST)
    public ResponseDTO<?> addAccount(@RequestBody AccountDTO accountDto, HttpServletResponse httpServletResponse);
    
    //edit account
    @RequestMapping(produces = "application/json", consumes = "application/json",
            value = "/m2s/editAccount", method = RequestMethod.PUT)
    public ResponseDTO<?> editAccount(@RequestBody AccountDTO accountDto, HttpServletResponse httpServletResponse);
    
    // delete account
    @RequestMapping(produces = "application/json", consumes = "application/json", 
            value = "/m2s/deleteAccount", method = RequestMethod.DELETE)
    public ResponseDTO<?> deleteAccount(@RequestParam(required = true) String accountId,
            HttpServletResponse httpServletResponse);
    
    // find account
    @RequestMapping(produces = "application/json", consumes = "application/json", 
            value = "/m2s/findAccount", method = RequestMethod.GET)
    public ResponseDTO<?> findAccount(@RequestParam(required = true) String accountId,
            HttpServletResponse httpServletResponse);
    
    //findAllAccounts
    @RequestMapping(produces = "application/json", consumes = "application/json",
            value = "/m2s/findAllAccounts", method = RequestMethod.GET)
    public ResponseDTO<?> findAllAccounts(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size, HttpServletResponse httpServletResponse);
    
    //SYSTEM CRUD
    //add system
    @RequestMapping(produces = "application/json", consumes = "application/json",
            value = "/m2s/addSystem", method = RequestMethod.POST)
    public ResponseDTO<?> addSystem(@RequestBody SystemDTO systemDto, HttpServletResponse httpServletResponse);
    
    //edit system
    @RequestMapping(produces = "application/json", consumes = "application/json",
            value = "/m2s/editSystem", method = RequestMethod.PUT)
    public ResponseDTO<?> editSystem(@RequestBody SystemDTO systemDto, HttpServletResponse httpServletResponse);
    
    // delete system
    @RequestMapping(produces = "application/json", consumes = "application/json", 
            value = "/m2s/deleteSystem", method = RequestMethod.DELETE)
    public ResponseDTO<?> deleteSystem(@RequestParam(required = true) String systemId,
            HttpServletResponse httpServletResponse);
    
    // find system
    @RequestMapping(produces = "application/json", consumes = "application/json", 
            value = "/m2s/findSystem", method = RequestMethod.GET)
    public ResponseDTO<?> findSystem(@RequestParam(required = true) String systemId,
            HttpServletResponse httpServletResponse);
    
    //findAllSystem
    @RequestMapping(produces = "application/json", consumes = "application/json",
            value = "/m2s/findAllSystems", method = RequestMethod.GET)
    public ResponseDTO<?> findAllSystems(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size, HttpServletResponse httpServletResponse);
    
    //RunTimeConfig CRUD
    //add RunConf
    @RequestMapping(produces = "application/json", consumes = "application/json", value = "/m2s/addRunConf",
            method = RequestMethod.POST)
    public ResponseDTO<?> addRunTimeConfig(@RequestBody RunTimeConfigDTO runTimeConfigDTO,
            HttpServletResponse httpServletResponse);

    // edit system
    @RequestMapping(produces = "application/json", consumes = "application/json", value = "/m2s/editRunConf",
            method = RequestMethod.PUT)
    public ResponseDTO<?> editRunConf(@RequestBody RunTimeConfigDTO runTimeConfigDTO,
            HttpServletResponse httpServletResponse);

    // delete system
    @RequestMapping(produces = "application/json", consumes = "application/json", value = "/m2s/deleteRunConf",
            method = RequestMethod.DELETE)
    public ResponseDTO<?> deleteRunConf(@RequestParam(required = true) String runConfId,
            HttpServletResponse httpServletResponse);

    // find system
    @RequestMapping(produces = "application/json", consumes = "application/json", value = "/m2s/findRunConf",
            method = RequestMethod.GET)
    public ResponseDTO<?> findRunConf(@RequestParam(required = true) String runConfId,
            HttpServletResponse httpServletResponse);

    // findAllSystem
    @RequestMapping(produces = "application/json", consumes = "application/json", value = "/m2s/findAllRunConfs",
            method = RequestMethod.GET)
    public ResponseDTO<?> findAllRunConfs(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size, HttpServletResponse httpServletResponse);
    
}
