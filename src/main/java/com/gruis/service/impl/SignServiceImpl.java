package com.gruis.service.impl;

import com.gruis.dto.Invoice;
import com.gruis.service.SignService;
import com.mifactura.xadessigner.signer.Signer;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl implements SignService {

    @Override
    public Invoice signInvoice(Invoice invoice) {
        // TODO: implement logic here.

        Signer signer = new Signer();

        signer.sign(invoice.getUserId(), invoice.getKey(), invoice.getCertificate(), invoice.getPin(), invoice.getXml(),
                    invoice.getUser(), invoice.getPassword(), invoice.getMode());

        return invoice;
    }
}
