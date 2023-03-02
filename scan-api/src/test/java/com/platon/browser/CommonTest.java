package com.platon.browser;

import com.alibaba.fastjson.JSON;
import com.platon.browser.dao.entity.InternalAddress;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class CommonTest {


    @Test
    public void test() throws IOException {

        File file = new File("src/test/resources/test.json");
        String str = FileUtils.readFileToString(file);
        List<InternalAddress> respPage = JSON.parseArray(str, InternalAddress.class);
        System.out.println(respPage.size());
        System.out.println("insert into scan_platon.internal_address (name, address, type, balance, restricting_balance, is_show, is_calculate, create_id, create_name, create_time, update_id, update_name, update_time)\n" +
                "values ");
        for (InternalAddress internalAddress : respPage) {
            int isCalculate = internalAddress.getIsCalculate() ? 1 : 0;
            System.out.println("('" + internalAddress.getName() +
                    "', '" + internalAddress.getAddress() +
                    "', 0, " + internalAddress.getBalance().multiply(new BigDecimal("1000000000000000000")) +
                    " , " + internalAddress.getRestrictingBalance().multiply(new BigDecimal("1000000000000000000")) +
                    " , 1, " + isCalculate +
                    ", 1, 'admin', '2021-09-17 14:56:23', 1, 'admin', '2021-10-22 18:36:00'),");
        }
    }

    @Test
    public void test1() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n  \"contract\": \"lat16zd7fy8erj7emrhgyv4rmtjuqe9heahpuasfpw\",\n  \"address\": \"\",\n  \"tokenId\": \"\",\n  \"pageNo\": 1,\n  \"pageSize\": 3000\n}");
        Request request = new Request.Builder()
                .url("https://scan.platon.network/browser-server//token/arc721-inventory/list")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().toString());
    }
}
