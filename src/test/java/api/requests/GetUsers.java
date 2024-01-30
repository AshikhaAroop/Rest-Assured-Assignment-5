package api.requests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utility.Utility;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetUsers extends Utility {

    Response response;

    @Test
    public void validateGetResponse(){

        //function call to validate content type, status code
        response= validateResponseGet(endPoint, 200);
    }

    @Test(dependsOnMethods = {"validateGetResponse"})
    public void validateGenderForAllUsers(){

        assertThat(response.path("data.gender"),everyItem(either(is("male")).or(is("female"))));
    }

    @Test(dependsOnMethods = {"validateGetResponse"})
    public void validateThreeUsersDomain (){
        JsonPath jsonPath = new JsonPath(response.asString());
        List<String> email = jsonPath.getList("data.email");
        int count=0;
        for (String s : email) {
            if(s.endsWith(".co") && count<3){
                count=count+1;
                if(count==3)
                    break;
            }
        }
        assertThat(count,equalTo(3));
    }

    @Test(dependsOnMethods = {"validateGetResponse"})
    public void validateIdValuesWithinRange () {
        assertThat(response.path("data.id"), everyItem(is(both(greaterThanOrEqualTo(20)).and(lessThanOrEqualTo(55)))));
    }
}
