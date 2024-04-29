package com.latamly.scrapy.models;

public class FindPriceModel {

    private String title;
    private String url;
    private double price;
    private double reviews;
    private double ranking;

    public FindPriceModel(String title, String url, double price/* , double reviews, double ranking */) {
        this.title = title;
        this.url = url;
        this.price = price;
        /* this.reviews = reviews;
        this.ranking = ranking; */
    }

    

    public FindPriceModel(String title, double price) {
        this.title = title;
        this.price = price;
        this.reviews = 0.0; 
        this.ranking = 0.0;
    }



    public String getTitle() {
        return title;
    }
    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getReviews() {
        return reviews;
    }

    public void setReviews(double reviews) {
        this.reviews = reviews;
    }

    public double getRanking() {
        return ranking;
    }

    public void setRanking(double ranking) {
        this.ranking = ranking;
    }



    @Override
    public String toString() {
        return "FindPriceModel [title=" + title + ", url=" + url + ", price=" + price + "]";
    }

    

}
