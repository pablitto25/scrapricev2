package com.latamly.scrapy.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.latamly.scrapy.models.FindPriceModel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Component
public class FindML {

    public FindPriceModel findML(String nombre) {

        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\pabli\\OneDrive\\Escritorio\\Trabajo\\findmercadolibreprices\\src\\main\\resources\\chromedriver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();

        String nombreProducto = "";

        // Convertir el nombre del producto a minúsculas
        nombreProducto = nombre.toLowerCase();

        WebDriver driver = new ChromeDriver(options);
        driver.get("https://listado.mercadolibre.com.ar/" + nombreProducto);
        driver.manage().window().maximize();
        String[] keywords = nombreProducto.split(" ");
        List<WebElement> items = driver.findElements(By.className("ui-search-layout__item"));

        String tituloPrecioMasBajo = null;
        String precioMasBajo = null;
        String ratingMasBajo = null;
        String reviewMasBajo = null;

        try {
            for (WebElement li : items) {
                WebElement titleElement = li.findElement(By.className("ui-search-item__title"));
                WebElement priceElement = li.findElement(By.className("andes-money-amount__fraction"));

                String titulo = titleElement.getText();

                // Validar si el título contiene las palabras "Redragon", "Zeus" y "Wireless"
                boolean allKeywordsPresent = true;
                for (String keyword : keywords) {
                    if (!titulo.toLowerCase().contains(keyword.toLowerCase())) {
                        allKeywordsPresent = false;
                        break;
                    }
                }

                if (!allKeywordsPresent) {
                    continue;
                }

                String ratingValue = "No tiene Rating"; // Por defecto, asumimos que no tiene rating
                String reviewValue = "No tiene Reviews"; // Por defecto, asumimos que no tiene rating

                // Validacion si Rating y Reviews no encuentra el elemento agregar un texto por
                // defecto "No tiene Rating o No tiene Reviews"
                try {
                    WebElement ratingElement = li.findElement(By.className("ui-search-reviews__rating-number"));
                    ratingValue = ratingElement.getText(); // Si el elemento existe, obtenemos su texto
                    WebElement reviewsElement = li.findElement(By.className("ui-search-reviews__amount"));
                    reviewValue = reviewsElement.getText(); // Si el elemento existe, obtenemos su texto
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    // Si elemento no existe agregar por defecto el valor No tiene Rating
                }

                System.out.println(titleElement.getText());
                System.out.println(priceElement.getText());
                System.out.println(ratingValue);
                System.out.println(reviewValue);

                String precioActual = priceElement.getText();
                precioActual = precioActual.replaceAll("[^\\d.]", "");
                double precio = Double.parseDouble(precioActual);

                if (precioMasBajo == null || precio < Double.parseDouble(precioMasBajo)) {
                    // Si el precio actual es más bajo, actualiza el precio más bajo y guarda los
                    // detalles del producto
                    tituloPrecioMasBajo = titleElement.getText();
                    precioMasBajo = precioActual;
                    ratingMasBajo = ratingValue;
                    reviewMasBajo = reviewValue;
                }

            }
            System.out.println("Producto con el precio más bajo:");
            System.out.println("Título: " + tituloPrecioMasBajo);
            System.out.println("Precio: " + precioMasBajo);
            System.out.println("Rating: " + ratingMasBajo);
            System.out.println("Review: " + reviewMasBajo);

        } catch (org.openqa.selenium.NoSuchElementException | NumberFormatException e) {
            System.out.println(e);
        }

        driver.close();

        FindPriceModel producto = new FindPriceModel(tituloPrecioMasBajo, precioMasBajo);
        return producto;

    }
}