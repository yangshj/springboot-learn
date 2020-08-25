// checks if groovy console works
import com.serge.springboot.pojo.City;
City city = new City();
city.setCityName("北京");
city.setDescription("北京是首都");
city.setProvinceId(1L);
cityService.insert(city);
cityService.findAll();
