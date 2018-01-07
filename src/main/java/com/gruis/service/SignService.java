package com.gruis.service;

import com.gruis.dto.Invoice;

public interface SignService {
    /**
     * Method to sign an invoice.
     * @param invoice the invoice to be signed.
     * @return an invoice, with the signing result.
     */
    Invoice signInvoice(Invoice invoice);
}
