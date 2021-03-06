package com.epam.brest.courses.dao;

import com.epam.brest.courses.dao.config.TestConfig;
import com.epam.brest.courses.model.Car;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan("com.epam.brest.courses.*")
@ContextConfiguration(classes = {TestConfig.class})
class CarRepositoryIT {

    private final CarRepository carRepository;

    @Autowired
    CarRepositoryIT(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Test
    void findAll() {
        List<Car> cars = carRepository.findAll();
        assertNotNull(cars);
       // assertTrue(cars.size() > 0);
    }

    @Test
    void findAllByDate() {
        LocalDate date = LocalDate.of(2020,4,3);
        List<Car> cars = carRepository.findAllByDate(date);

        assertNotNull(cars);
    }

    @Test
    void findById() {
        //given
        Car car = new Car();
        car.setBrand("Honda");
        car.setRegisterNumber("5002 AB-1");
        car.setPrice(BigDecimal.valueOf(150));

        Car savedCar = carRepository.save(car);

        //when
        Optional<Car> optionalCar = carRepository.findById(savedCar.getId());

        //then
        assertTrue(optionalCar.isPresent());
        assertEquals(savedCar.getId(),optionalCar.get().getId());
        assertEquals(savedCar.getBrand(), optionalCar.get().getBrand());
        assertEquals(savedCar.getRegisterNumber(), optionalCar.get().getRegisterNumber());
        assertEquals(0 ,savedCar.getPrice().compareTo(optionalCar.get().getPrice()));
    }

    @Test
    void findByRegisterNumber() {
        //given
        Car car = new Car();
        car.setBrand("Honda");
        car.setRegisterNumber("5002 AB-1");
        car.setPrice(BigDecimal.valueOf(150));

        Car savedCar = carRepository.save(car);

        //when
        Optional<Car> optionalCar = carRepository.findByRegisterNumber(savedCar.getRegisterNumber());

        //then
        assertTrue(optionalCar.isPresent());
        assertEquals(savedCar.getId(),optionalCar.get().getId());
        assertEquals(savedCar.getBrand(), optionalCar.get().getBrand());
        assertEquals(savedCar.getRegisterNumber(), optionalCar.get().getRegisterNumber());
        assertEquals(0 ,savedCar.getPrice().compareTo(optionalCar.get().getPrice()));
    }

    @Test
    void create() {
        //given
        Car car = new Car();
        car.setBrand("Honda");
        car.setRegisterNumber("5302 AB-1");
        car.setPrice(BigDecimal.valueOf(150));

        //when
        Car savedCar = carRepository.save(car);

        //then
        assertNotNull(savedCar.getId());

        Optional<Car> optionalCar = carRepository.findById(savedCar.getId());
        assertTrue(optionalCar.isPresent());

        assertEquals(savedCar.getId(),optionalCar.get().getId());
        assertEquals(savedCar.getBrand(), optionalCar.get().getBrand());
        assertEquals(savedCar.getRegisterNumber(), optionalCar.get().getRegisterNumber());
    }

    @Test
    void update() {
        //given
        Car car = new Car();
        car.setBrand("Honda");
        car.setRegisterNumber("7302 AB-1");
        car.setPrice(BigDecimal.valueOf(150));

        Car savedCar = carRepository.save(car);
        Optional<Car> optionalCar = carRepository.findById(savedCar.getId());

        assertTrue(optionalCar.isPresent());
        optionalCar.get().setBrand("HONDA");
        optionalCar.get().setRegisterNumber("7350 AB-1");
        optionalCar.get().setPrice(BigDecimal.valueOf(200));

        //when
        int result = carRepository.update(optionalCar.get());

        //then
        assertTrue(1 == result);
        Optional<Car> updatedOptionalCar = carRepository.findById(savedCar.getId());
        assertTrue(updatedOptionalCar.isPresent());
        assertEquals(savedCar.getId(),updatedOptionalCar.get().getId());
        assertEquals("HONDA", updatedOptionalCar.get().getBrand());
        assertEquals("7350 AB-1", updatedOptionalCar.get().getRegisterNumber());
        assertEquals(0, BigDecimal.valueOf(200).compareTo(updatedOptionalCar.get().getPrice()));
    }

    @Test
    void delete() {
        //given
        Car car = new Car();
        car.setBrand("Honda");
        car.setRegisterNumber("5402 AB-1");
        car.setPrice(BigDecimal.valueOf(150));

        Car savedCar = carRepository.save(car);;

        //when
        carRepository.deleteById(savedCar.getId());

        //then
        Optional<Car> optionalCar = carRepository.findById(savedCar.getId());
        assertFalse(optionalCar.isPresent());
    }
}