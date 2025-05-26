/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Category;
import com.socialapp.service.CategoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.socialapp.pojo.Category2; 
import com.socialapp.service.CategoryService2; 


/**
 *
 * @author DELL G15
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiCategoryController {
    @Autowired
    private CategoryService categoryService;
    
     @Autowired
    private CategoryService2 categoryService2; 

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> list() {
        return new ResponseEntity<>(categoryService.getCategories(), HttpStatus.OK);
    }
    
     
    @GetMapping("/categories2")
    public ResponseEntity<List<Category2>> listCategories2() {
        return new ResponseEntity<>(categoryService2.getCategories2(), HttpStatus.OK);
       
    }
}
