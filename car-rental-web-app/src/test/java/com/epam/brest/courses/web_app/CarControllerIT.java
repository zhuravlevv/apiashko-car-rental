package com.epam.brest.courses.web_app;

import com.epam.brest.courses.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:app-context-test.xml"})
public class CarControllerIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldReturnListOfFreeCars() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/cars"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cars"))
                .andExpect(model().attribute("cars", hasItem(
                        allOf(
                                hasProperty("id", is(1)),
                                hasProperty("brand", is("BMW")),
                                hasProperty("registerNumber", is("3456 AB-1")),
                                hasProperty("price", is(new BigDecimal("240.00")))
                        )
                )))
                .andExpect(model().attribute("cars", hasItem(
                        allOf(
                                hasProperty("id", is(2)),
                                hasProperty("brand", is("AUDI")),
                                hasProperty("registerNumber", is("0056 AB-1")),
                                hasProperty("price", is(new BigDecimal("140.00")))
                        )
                )))
                .andExpect(model().attribute("cars", hasItem(
                        allOf(
                                hasProperty("id", is(3)),
                                hasProperty("brand", is("TYOYTA")),
                                hasProperty("registerNumber", is("3836 AB-1")),
                                hasProperty("price", is(new BigDecimal("200.00")))
                        )
                )));
    }

    @Test
    public void shouldOpenEditCarPage() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/car/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("car"))
                .andExpect(model().attribute("isNew", false))
                .andExpect(model().attribute("car", hasProperty("id", is(1))))
                .andExpect(model().attribute("car", hasProperty("brand", is("BMW"))))
                .andExpect(model().attribute("car", hasProperty("registerNumber", is("3456 AB-1"))))
                .andExpect(model().attribute("car", hasProperty("price", is(new BigDecimal("240.00")))))
        ;
    }

    @Test
    public void shouldReturnToCarsPageIfCarNotFoundById() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/car/99999")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/cars"));
    }

    @Test
    public void shouldUpdateCarAfterEdit() throws Exception {

        Car car = new Car();
        car.setId(1);
        car.setBrand("BMW");
        car.setRegisterNumber("3456 AB-1");
        car.setPrice(BigDecimal.valueOf(240));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/car/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("brand", "BMW")
                        .param("registerNumber", "3456 AB-1")
                        .param("price", "240")
                        .sessionAttr("car", car)
        ).andExpect(status().isFound())
                .andExpect(view().name("redirect:/cars"))
                .andExpect(redirectedUrl("/cars"));
    }

    @Test
    public void shouldOpenNewCarPage() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/car")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("car"))
                .andExpect(model().attribute("isNew", is(true)))
                .andExpect(model().attribute("car", isA(Car.class)));
    }

    @Test
    public void shouldAddNewCar() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/car")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("brand", "test")
                        .param("registerNumber", "1111 AB-1")
                        .param("price", "111")
        ).andExpect(status().isFound())
                .andExpect(view().name("redirect:/cars"))
                .andExpect(redirectedUrl("/cars"));
    }

    @Test
    public void shouldDeleteDepartment() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/car/1/delete")
        ).andExpect(status().isFound())
                .andExpect(view().name("redirect:/cars"))
                .andExpect(redirectedUrl("/cars"));
    }

    @Test
    public void shouldReturnListCarsWithNumberOfOrders() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/car-statistics")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dateFrom", "2020-01-01")
                        .param("dateTo", "2020-01-15"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("statistics"))
                .andExpect(model().attribute("cars", hasItem(
                        allOf(
                                hasProperty("id", is(2)),
                                hasProperty("brand", is("AUDI")),
                                hasProperty("registerNumber", is("0056 AB-1")),
                                hasProperty("numberOrders", is(1))
                        )
                )))
        ;
    }
}
