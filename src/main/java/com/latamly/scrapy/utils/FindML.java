package com.latamly.scrapy.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                "C:\\Users\\Pablo Cortes\\Desktop\\scrapricev2 final\\src\\main\\resources\\chromedriver\\chromedriver.exe");
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
        Double precioMasBajo = null;
        String ratingMasBajo = null;
        String reviewMasBajo = null;

        // Después de definir 'keywords' - Escribis palabras claves en minuscula
        Set<String> palabrasAIgnorar = new HashSet<>();
        palabrasAIgnorar.add("reparado");
        palabrasAIgnorar.add("refurbish");
        palabrasAIgnorar.add("reacondicionado");
        palabrasAIgnorar.add("almohadilla");
        palabrasAIgnorar.add("almohadillas");

        Double priceCompared = null;

        try {
            for (WebElement li : items) {
                WebElement titleElement = li.findElement(By.className("ui-search-item__title"));
                /* WebElement priceElement = li.findElement(By.className("andes-money-amount__fraction")); */
                List<WebElement> precios = li.findElements(By.className("andes-money-amount__fraction"));
                
                priceCompared = Double.MAX_VALUE;
                
                // Buscar el elemento 'usedElement'
                boolean isUsed = false;
                boolean isReacondicionado = false;
                try {
                    @SuppressWarnings("unused")
                    WebElement UsadoElement = li.findElement(By.xpath(".//*[contains(text(),'Usado')]"));
                    isUsed = true;
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    // Si el elemento 'usedElement' no está presente, el producto no es usado
                }
                // Si el producto es usado, continuar al siguiente producto
                if (isUsed) {
                    continue;
                }

                try {
                    @SuppressWarnings("unused")
                    WebElement ReacondicionadoElement = li
                            .findElement(By.xpath(".//*[contains(text(),'Reacondicionado')]"));
                    isReacondicionado = true;
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    // Si el elemento 'ReacondicionadoElement' no está presente, el producto no es
                    // usado
                }

                if (isReacondicionado) {
                    continue;
                }

                String titulo = titleElement.getText();

                // Verificar si alguna palabra a ignorar está presente en el título
                boolean ignoreProduct = false;
                for (String palabra : palabrasAIgnorar) {
                    if (titulo.toLowerCase().contains(palabra)) {
                        ignoreProduct = true;
                        break;
                    }
                }
                if (ignoreProduct) {
                    continue; // Ignorar este producto
                }

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

                for(WebElement pc : precios){
                    double priceFormated = Double.parseDouble(pc.getText());
                    System.out.println(pc.getText());
                    System.out.println(precios.size());
                    if (priceFormated < priceCompared) {
                        priceCompared = priceFormated;
                    }  
                }

                System.out.println(priceCompared);

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
                System.out.println(priceCompared);
                System.out.println(ratingValue);
                System.out.println(reviewValue);

                /* String precioActual = priceElement.getText();
                precioActual = precioActual.replaceAll("[^\\d.]", ""); */
                double precio = priceCompared;

                if (precioMasBajo == null || precio < precioMasBajo) {
                    // Si el precio actual es más bajo, actualiza el precio más bajo y guarda los
                    // detalles del producto
                    tituloPrecioMasBajo = titleElement.getText();
                    precioMasBajo = priceCompared;
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
        } finally {
            // Asegúrate de cerrar el WebDriver incluso si ocurre una excepción
            if (driver != null) {
                driver.quit();
            }
        }

        FindPriceModel producto = new FindPriceModel(tituloPrecioMasBajo, precioMasBajo);
        return producto;

    }
}