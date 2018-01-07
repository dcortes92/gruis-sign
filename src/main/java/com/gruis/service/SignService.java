package com.gruis.service;

import com.gruis.dto.Invoice;

public interface SignService {
    /**
     * Method to sign an invoice.
     * @param invoice the invoice to be signed.
     * @return a string, with the signing result.
     */
    String signInvoice(Invoice invoice);
}
