package com.gruis.controller;

import com.gruis.dto.Invoice;
import com.gruis.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sign")
public class SignController {
    private SignService signService;
    private HttpHeaders httpHeaders;

    @Autowired
    public SignController(SignService signService) {
        this.signService = signService;
        this.httpHeaders = new HttpHeaders();
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity signInvoice(@RequestBody Invoice invoice) {
        this.httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new ResponseEntity<>(
                signService.signInvoice(invoice),
                this.httpHeaders,
                HttpStatus.OK);
    }
}
