package com.platon.browser;

import com.platon.bech32.Bech32;
import com.platon.crypto.Credentials;
import com.platon.parameters.NetworkParameters;
import com.platon.protocol.Web3j;
import com.platon.protocol.core.DefaultBlockParameterName;
import com.platon.protocol.core.Request;
import com.platon.protocol.core.methods.response.PlatonGetBalance;
import com.platon.protocol.core.methods.response.PlatonSign;
import com.platon.protocol.http.HttpService;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

public class CommonTest {

    @Test
    public void test() {
        NetworkParameters.init(20000L, "lat");
        String bech32Address = "lat164lck2djw8nd2e88svcx259c8a08eqryzh5mtd";
        String hex = Bech32.addressDecodeHex(bech32Address);
        System.out.println(hex);
    }

    public static void main(String[] args) throws IOException {
        NetworkParameters.selectPlatON();
        Credentials credentials = Credentials.create("0x9f63d041faacfad2a547a6db83f4466b3bd127e3537612769dbb3c63306c363b");
        Web3j web3j = Web3j.build(new HttpService("http://13.229.172.149:6789"));
        String address = "lat10qhhr8xws4f3xnal2a5pn834xsmmtjx3xstwlk";
        Request<?, PlatonGetBalance> request = web3j.platonGetBalance(address, DefaultBlockParameterName.LATEST);
        BigInteger req = request.send().getBalance();
        System.out.println(req);
        String sha3HashOfDataToSign = "";
        Request<?, PlatonSign> request1 = web3j.platonSign(address, sha3HashOfDataToSign);
        String req1 = request1.send().getSignature();
        System.out.println(req1);
    }
}
