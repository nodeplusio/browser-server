package com.platon.browser.geo;

import com.platon.browser.util.GeoUtil;

import java.util.Arrays;

public class GeoTest {
   /* public static void main(String[] args) throws IOException, GeoIp2Exception {
        *//*try {
            String path = LookupService.class.getClassLoader().getResource("dev/GeoLiteCity.dat").getPath();
            LookupService cl = new LookupService(path, LookupService.GEOIP_MEMORY_CACHE);
            Location l2 = cl.getLocation("14.215.177.39");
            System.out.println(
                    "countryCode: " + l2.countryCode +"\n"+
                            "countryName: " + l2.countryName +"\n"+
                            "region: " + l2.region +"\n"+
                            "city: " + l2.city +"\n"+
                            "latitude: " + l2.latitude +"\n"+
                            "longitude: " + l2.longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }*//*


        CityResponse response = GeoUtil.getResponse("128.101.101.101");

        Country country = response.getCountry();
        System.out.println(country.getIsoCode());            // 'US'
        System.out.println(country.getName());               // 'United States'
        System.out.println(country.getNames().get("zh-CN")); // '美国'

        Subdivision subdivision = response.getMostSpecificSubdivision();
        System.out.println(subdivision.getName());    // 'Minnesota'
        System.out.println(subdivision.getIsoCode()); // 'MN'

        City city = response.getCity();
        System.out.println(city.getName()); // 'Minneapolis'

        Postal postal = response.getPostal();
        System.out.println(postal.getCode()); // '55455'

        Location location = response.getLocation();
        System.out.println(location.getLatitude());  // 44.9733
        System.out.println(location.getLongitude()); // -93.2323
    }*/




    public static void main(String[] args) {
        class IP{
            String name,ip;
            public IP(String name,String ip){
                this.name=name;
                this.ip=ip;
            }
        }
       IP[] ips = {
               new IP("新西兰奥克兰","156.62.183.193"),
               new IP("德国法兰克福","108.61.210.117"),
               new IP("美国西雅图","108.61.194.105"),
               new IP("美国纽约","170.3.239.102"),
               new IP("日本大阪","218.42.250.255"),
               new IP("日本东京","218.132.30.255"),
               new IP("法国巴黎","193.54.67.4"),
               new IP("莫斯科","89.208.161.81"),
               new IP("韩国首尔","112.144.9.177"),
               new IP("美国休斯顿","99.172.41.65")
       };
        Arrays.asList(ips).forEach(ip->{
            GeoUtil.IpLocation location = GeoUtil.getIpLocation(ip.ip);
            System.out.println("name:【"+ip.name+"】,ip:【"+ip.ip+"】,longitude:【"+location.getLongitude()+"】,latitude:【"+location.getLatitude()+"】");
        });
    }
}
