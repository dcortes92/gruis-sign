package com.gruis.service.impl;

import com.gruis.dto.Invoice;
import com.gruis.service.SignService;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl implements SignService {

    @Override
    public String signInvoice(Invoice invoice) {
        // TODO: implement logic here.
        return "OK";
    }
}
