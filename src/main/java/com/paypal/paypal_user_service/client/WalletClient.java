package com.paypal.paypal_user_service.client;

import com.paypal.paypal_user_service.dto.CreateWalletRequest;
import com.paypal.paypal_user_service.dto.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet-service", url = "${wallet.service.url}",path = "/paypal/api/wallet/v1" )
public interface WalletClient {
    @PostMapping("/create")
    WalletResponse createWallet(@RequestBody CreateWalletRequest request);
}
