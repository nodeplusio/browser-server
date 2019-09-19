package com.platon.browser.util;

import com.platon.browser.param.PlanParam;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * rlp处理数据类
 * User: dongqile
 * Date: 2019/8/13
 * Time: 16:48
 */
public  class Resolver {
    private Resolver(){}

    public static BigInteger bigIntegerResolver ( RlpString rlpString ) {
        RlpString integers = rlpString;
        RlpList integersList = RlpDecoder.decode(integers.getBytes());
        RlpString integersString = (RlpString) integersList.getValues().get(0);
        return new BigInteger(1, integersString.getBytes());
    }

    public static String StringResolver ( RlpString rlpString ) {
        RlpString Strings = rlpString;
        RlpList StringsList = RlpDecoder.decode(Strings.getBytes());
        RlpString StringsListString = (RlpString) StringsList.getValues().get(0);
        return Numeric.toHexString(StringsListString.getBytes());
    }

    public static List<PlanParam> ObjectResolver ( RlpString rlpString ) {
        List<PlanParam> list = new ArrayList <>();
        RlpList bean = RlpDecoder.decode(rlpString.getBytes());
        List <RlpType> beanList = ((RlpList) bean.getValues().get(0)).getValues();
        for (RlpType beanType : beanList) {
            RlpList beanTypeList = (RlpList) beanType;
            RlpString parama = (RlpString) beanTypeList.getValues().get(0);
            RlpString paramb = (RlpString) beanTypeList.getValues().get(1);
            PlanParam planParam = new PlanParam();
            planParam.setEpoch(parama.asPositiveBigInteger().intValue());
            planParam.setAmount(paramb.asPositiveBigInteger().toString());
            list.add(planParam);
        }
        return list;
    }
}
