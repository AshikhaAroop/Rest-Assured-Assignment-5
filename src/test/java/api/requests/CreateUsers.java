package api.requests;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import utility.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class CreateUsers extends Utility {

    Response response;
    String[] validReqBodyFields = {"name", "gender", "email", "status"};
    String[] validReqBodyValues = {"Nikhita", "female", "hello@gmail.com", "active"};
    String fieldsEmptyMsg ="can't be blank";
    String genderEmptyMsg ="can't be blank, can be male of female";
    String invalidEmailMsg ="is invalid";
    String unAuthorisedMsg ="Authentication failed";
    String emailAlreadyTakenMsg= "has already been taken";

    @Test
    public void validatePostExcel() throws IOException {
        String jsonObject = null;
        List<String>list1 = null;
        int rownum = getRowCount(excelPath, "Sheet1");
        int colnum = getCellCount(excelPath, "Sheet1", 1);

        for (int i = 1; i <= rownum; i++) {
            list1=new ArrayList<>();
            for (int j = 0; j < colnum; j++) {
                list1.add(j, getCellDataExcel(excelPath, 0, i, j));

            }

            assert list1 != null;
            String randomstr= randomStringGenerator();
            list1.set(2, list1.get(2)+randomstr);
            jsonObject= customBodyRequestPostUser(list1.get(0), list1.get(1), list1.get(2), list1.get(3));
            response=validatePostCall(bearerToken,jsonObject,201);
        }
        validatePostWithExistingRecord(jsonObject);
    }

    public void validatePostWithExistingRecord(String jsonObject){
        response=validatePostCall(bearerToken,jsonObject,422);

        assertThat(response.path("data.field"),hasItem("email"));
        assertThat(response.path("data.message"),hasItem(emailAlreadyTakenMsg));

    }

    @Test
    public void validatePostWithoutToken(){
        String jsonObject=customBodyRequestPostUser(validReqBodyValues[0],validReqBodyValues[1],validReqBodyValues[2],validReqBodyValues[3]);
        response=validatePostCall(" ",jsonObject,401);
        assertThat(response.path("data.message"),is(equalTo(unAuthorisedMsg)));

    }

    @Test
    public void validateWithoutName(){
        String email= newEmailGenerator();

        String jsonObject=customBodyRequestPostUser("",validReqBodyValues[1],email,validReqBodyValues[3]);
        response=validatePostCall(bearerToken,jsonObject,422);
        assertThat(response.path("data[0].field"),is(equalTo(validReqBodyFields[0])));
        assertThat(response.path("data[0].message"),is(equalTo(fieldsEmptyMsg)));
    }

    @Test
    public void validateWithoutGender(){
        String email= newEmailGenerator();

        String jsonObject=customBodyRequestPostUser(validReqBodyValues[0],"",email,validReqBodyValues[3]);
        response=validatePostCall(bearerToken,jsonObject,422);
        assertThat(response.path("data[0].field"),is(equalTo(validReqBodyFields[1])));
        assertThat(response.path("data[0].message"),is(equalTo(genderEmptyMsg)));
    }

    @Test
    public void validateWithoutEmail(){
        String jsonObject=customBodyRequestPostUser(validReqBodyValues[0],validReqBodyValues[1],"",validReqBodyValues[3]);
        response=validatePostCall(bearerToken,jsonObject,422);
        assertThat(response.path("data[0].field"),is(equalTo(validReqBodyFields[2])));
        assertThat(response.path("data[0].message"),is(equalTo(fieldsEmptyMsg)));
    }

    @Test
    public void validateWithoutStatus(){
        String email= newEmailGenerator();

        String jsonObject=customBodyRequestPostUser(validReqBodyValues[0],validReqBodyValues[1],email,"");
        response=validatePostCall(bearerToken,jsonObject,422);
        assertThat(response.path("data[0].field"),is(equalTo(validReqBodyFields[3])));
        assertThat(response.path("data[0].message"),is(equalTo(fieldsEmptyMsg)));
    }

    @Test
    public void validateWithInvalidEmail(){
        String jsonObject=customBodyRequestPostUser(validReqBodyValues[0],validReqBodyValues[1],"ssd",validReqBodyValues[3]);
        response=validatePostCall(bearerToken,jsonObject,422);
        assertThat(response.path("data[0].field"),is(equalTo(validReqBodyFields[2])));
        assertThat(response.path("data[0].message"),is(equalTo(invalidEmailMsg)));
    }

    @Test
    public void validateEmailWithoutDot(){
        String email= newEmailGenerator();
        String newEmail= email.replace(".","a");
        String jsonObject=customBodyRequestPostUser(validReqBodyValues[0],validReqBodyValues[1],newEmail,validReqBodyValues[3]);
        response=validatePostCall(bearerToken,jsonObject,422);
        //expected to fail but passes with 200 code
    }

    @Test
    public void validateEmailWithoutAt(){
        String jsonObject=customBodyRequestPostUser(validReqBodyValues[0],validReqBodyValues[1],"ss.ss",validReqBodyValues[3]);
        response=validatePostCall(bearerToken,jsonObject,422);
        assertThat(response.path("data[0].field"),is(equalTo(validReqBodyFields[2])));
        assertThat(response.path("data[0].message"),is(equalTo(invalidEmailMsg)));
    }
}
