package ru.netology;

import com.github.javafaker.Faker;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {
    private static Random random = new Random();

    DataGenerator() {
    }

    public static RequestInfo generateRequest(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return new RequestInfo(generateCity(),
                faker.name().lastName() + " " + faker.name().firstName(),
                faker.phoneNumber().phoneNumber());
    }

    public static String generateCity() {
        String[] cities = new String[]{"Москва", "Волгоград", "Санкт-Петербург", "Салехард", "Тюмень", "Кызыл"};
        return cities[random.nextInt(cities.length)];
    }

    public static String generateDate(int shift, int range) {
        return LocalDate.now().plusDays(3 + shift).plusDays(random.nextInt(range))
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String generateInvalidPhone() {
        return "+7926856325";
    }

    public static String generateOnlyName() {
        return "Ирина";
    }
}

